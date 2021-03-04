package org.fir3.cml.impl.java;

import org.fir3.cml.api.exception.TranslationException;
import org.fir3.cml.api.model.*;
import org.fir3.cml.api.util.ModelHelper;
import org.fir3.cml.api.util.Pair;
import org.fir3.cml.api.util.TypeHelper;
import org.fir3.cml.impl.java.config.JavaTypeInfo;
import org.fir3.cml.impl.java.config.TypeMapping;
import org.fir3.cml.impl.java.type.ClassType;
import org.fir3.cml.impl.java.type.JavaType;
import org.fir3.cml.impl.java.type.TypeVariableType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A translator for the {@link Environment} class.
 */
final class EnvironmentTranslator {
    private final Environment environment;
    private final File directory;
    private final Map<Type, JavaTypeInfo> knownTypes;

    /**
     * Creates a new instance of {@link EnvironmentTranslator}.
     *
     * @param environment   The environment that will be translated.
     * @param directory     The output directory that the translated Java
     *                      files will be stored in.
     *
     * @param typeMappings  The predefined type mappings.
     *
     * @throws NullPointerException If any of the specified parameters is
     *                              <code>null</code>.
     */
    public EnvironmentTranslator(
            Environment environment,
            File directory,
            Set<TypeMapping> typeMappings
    ) {
        Objects.requireNonNull(environment);
        Objects.requireNonNull(directory);
        Objects.requireNonNull(typeMappings);

        this.environment = environment;
        this.directory = directory;
        this.knownTypes = new HashMap<>();

        this.knownTypes.putAll(typeMappings.stream().collect(Collectors.toMap(
                TypeMapping::getCmlType,
                TypeMapping::getJavaType
        )));
    }

    /**
     * Returns the directory where the translated environment will be stored.
     *
     * @return  The directory where the translated environment will be stored.
     */
    public File getDirectory() {
        return this.directory;
    }

    /**
     * Translates the domain with the specified <code>name</code> of the
     * environment that was specified at the initialization of this instance
     * and stores the result at the directory that was specified at the
     * initialization as well.
     *
     * @param name  The name of the domain that will be translated.
     *
     * @throws TranslationException If there is no domain with the specified
     *                              <code>name</code> inside the environment
     *                              that was specified when initializing this
     *                              instance.
     *
     * @throws NullPointerException If <code>name</code> is <code>null</code>.
     */
    public void translateDomain(String name) throws TranslationException {
        Objects.requireNonNull(name);

        // Resolving the domain with the specified name

        Domain domain = this.environment.resolveDomain(name).orElseThrow(
                () -> new TranslationException(String.format(
                        "No domain with name '%s'",
                        name
                ))
        );

        // Translating the whole domain

        for (Model model : domain.getModels()) {
            if (model.getFlags().contains(Model.Flag.Builtin)) {
                continue;
            }

            this.translateModel(ModelHelper.toString(domain, model));
        }
    }

    /**
     * Translates the specified CML <code>type</code> to the corresponding Java
     * type.
     *
     * This method preserves the type parameter name of any
     * {@link ParameterType} that is translated to a {@link TypeVariableType}.
     *
     * @param type              The CML type that will be translated.
     * @param context           The context of the specified <code>type</code>.
     *
     * @return  The corresponding Java type for the specified CML
     *          <code>type</code>.
     *
     * @throws NullPointerException If <code>type</code> is <code>null</code>.
     * @throws TranslationException If translating the specified
     *                              <code>type</code> fails due to some reason.
     */
    public JavaTypeInfo translateType(
            Type type,
            Domain context
    ) throws TranslationException {
        Objects.requireNonNull(type);

        // Handling ParameterType instances first, because they do not need
        // much translation.
        //
        // NOTE:    It is important that the type of a ParameterType is not
        //          being normalized as otherwise its type parameter name has
        //          been changed, which makes it useless for its parent class
        //          (which provides type parameters with a specific name).

        if (type.getCategory() == Type.Category.Parameter) {
            ParameterType parameterType = (ParameterType) type;

            return new JavaTypeInfo(null, new TypeVariableType(
                    parameterType.getTypeParameterName()
            ));
        }

        // Normalizing the type to simplify its general handling

        Type normalizedType = TypeHelper.normalize(
                type,
                this.environment,
                context
        );

        // Handling the modelType requires translating its type parameters and
        // the model itself.

        ModelType modelType = (ModelType) type;
        ModelType normalizedModelType = (ModelType) normalizedType;

        ClassType translatedGenericModelType = (ClassType) this.translateModel(
                normalizedModelType.getModelName()
        ).getObjectType();

        // NOTE:    We perform the type parameter translation on the type
        //          parameter list of the not-normalized modelType, because
        //          otherwise we may loose the type parameter names.

        List<JavaType> translatedTypeParameters = new ArrayList<>();

        for (Type typeParameter : modelType.getTypeParameters()) {
            translatedTypeParameters.add(
                    this.translateType(typeParameter, context).getObjectType()
            );
        }

        return new JavaTypeInfo(null, new ClassType(
                translatedGenericModelType.getFullyQualifiedClassName(),
                translatedTypeParameters
        ));
    }

    /**
     * Translates the model that has the specified <code>name</code>.
     *
     * @param name  The name of the model that will be translated.
     *
     * @return  The type information of the Java class, which has been
     *          translated from the model with the specified <code>name</code>.
     *
     * @throws TranslationException If translating the model with the specified
     *                              <code>name</code> fails.
     *
     * @throws NullPointerException If <code>name</code> is <code>null</code>.
     */
    private JavaTypeInfo translateModel(String name)
            throws TranslationException {
        Objects.requireNonNull(name);

        // Resolving the model from the current environment

        Pair<Domain, Model> pair = this.environment.resolveModel(name, null)
                .orElseThrow(() -> new TranslationException(String.format(
                        "No model with name '%s'",
                        name
                )));

        Domain domain = pair.getFirstComponent();
        Model model = pair.getSecondComponent();

        // Creating the generic type information of the model

        Type type = TypeHelper.from(this.environment, domain, model);

        // Checking, if the model has been translated already

        JavaTypeInfo cls = this.knownTypes.get(type);

        if (cls != null) {
            return cls;
        }

        // If the model is built-in, we are throwing an exception here, because
        // the type must have been registered.

        if (model.getFlags().contains(Model.Flag.Builtin)) {
            throw new TranslationException("Model must be built-in");
        }

        // Translating the class and returning

        this.knownTypes.put(type, cls = new ModelTranslator(
                this,
                domain,
                model
        ).translate());

        return cls;
    }
}
