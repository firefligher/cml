package org.fir3.cml.impl.java;

import org.fir3.cml.api.exception.TranslationException;
import org.fir3.cml.api.model.Attribute;
import org.fir3.cml.api.model.Domain;
import org.fir3.cml.api.model.Model;
import org.fir3.cml.api.model.TypeParameter;
import org.fir3.cml.impl.java.config.JavaTypeInfo;
import org.fir3.cml.impl.java.type.ClassType;
import org.fir3.cml.impl.java.type.TypeVariableType;
import org.fir3.cml.impl.java.util.NameConverter;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A translator for the {@link Model} class.
 */
final class ModelTranslator {
    private static File getPackageDirectory(
            File baseDirectory,
            String packageName
    ) {
        File pkgDir = baseDirectory;

        for (String pkg : packageName.split("\\.")) {
            pkgDir = new File(pkgDir, pkg);
        }

        return pkgDir;
    }

    private final EnvironmentTranslator environmentTranslator;
    private final Domain domain;
    private final Model model;

    /**
     * Creates a new instance of {@link ModelTranslator}, which may be used for
     * translating the specified <code>model</code>.
     *
     * @param environmentTranslator The translator of the environment that the
     *                              specified <code>domain</code> and
     *                              <code>model</code> originate from.
     *
     * @param domain                The domain of the <code>model</code> that
     *                              will be translated.
     *
     * @param model                 The model that will be translated.
     *
     * @throws NullPointerException     If any of the specified parameters is
     *                                  <code>null</code>.
     *
     * @throws IllegalArgumentException If the specified <code>model</code> is
     *                                  built-in.
     */
    public ModelTranslator(
            EnvironmentTranslator environmentTranslator,
            Domain domain,
            Model model
    ) {
        Objects.requireNonNull(environmentTranslator);
        Objects.requireNonNull(domain);
        Objects.requireNonNull(model);

        if (model.getFlags().contains(Model.Flag.Builtin)) {
            throw new IllegalArgumentException("model is built-in");
        }

        this.environmentTranslator = environmentTranslator;
        this.domain = domain;
        this.model = model;
    }

    /**
     * Translates the model, that has been specified at initialization of this
     * instance, to a corresponding Java class.
     *
     * @return  The type information of the Java class that has been translated
     *          from the model of this translator.
     *
     * @throws TranslationException If the model cannot be translated to a Java
     *                              class
     */
    public JavaTypeInfo translate() throws TranslationException {
        // Determining the package name and the target directory of the
        // translated class

        String packageName = NameConverter.convertDomainToPackage(
                this.domain.getName()
        );

        File packageDirectory = ModelTranslator.getPackageDirectory(
                this.environmentTranslator.getDirectory(),
                packageName
        );

        // Ensuring that the package directory exists

        if (!packageDirectory.isDirectory() && !packageDirectory.mkdirs()) {
            throw new TranslationException(String.format(
                    "Cannot create package directory: '%s'",
                    packageDirectory.getAbsolutePath()
            ));
        }

        // Determining the name of the translated class and the target file

        String className = NameConverter.convertModelToClass(
                this.model.getName()
        );

        File classFile = new File(packageDirectory, String.format(
                "%s.java",
                className
        ));

        // Translating the class

        JavaClassSource cls = Roaster.create(JavaClassSource.class);
        cls.setPackage(packageName);
        cls.setName(className);

        for (TypeParameter typeParameter : this.model.getTypeParameters()) {
            cls.addTypeVariable(
                    NameConverter.convertTypeParameterToTypeVariable(
                            typeParameter.getName()
                    )
            );
        }

        for (Attribute attribute : this.model.getAttributes()) {
            JavaTypeInfo javaType = this.environmentTranslator.translateType(
                    attribute.getType(),
                    domain
            );

            cls.addProperty(
                    javaType.hasPrimitiveType()
                            ? javaType.getPrimitiveType().toJavaType()
                            : javaType.getObjectType().toJavaType(),
                    attribute.getName()
            );
        }

        // Writing the class to its corresponding file

        try (Writer writer = new FileWriter(classFile)) {
            writer.write(cls.toString());
        } catch (IOException ex) {
            throw new TranslationException("Cannot write class to file", ex);
        }

        return new JavaTypeInfo(null, new ClassType(
                String.format("%s.%s", packageName, className),
                this.model.getTypeParameters()
                        .stream()
                        .map(p -> new TypeVariableType(p.getName()))
                        .collect(Collectors.toList())
        ));
    }
}
