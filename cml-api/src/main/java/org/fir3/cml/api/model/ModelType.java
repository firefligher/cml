package org.fir3.cml.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A model type is directly derived from a defined model and must always
 * fill all type parameters of this model.
 *
 * <p>
 *     If two instances <code>a</code> and <code>b</code> of
 *     <code>ModelType</code> have the same model name and type parameters,
 *     the two instances are considered to be equal and both,
 *     <code>a.equals(b)</code> and <code>b.equals(a)</code>, must return
 *     <code>true</code>.
 * </p>
 */
public final class ModelType implements Type {
    private final String modelName;
    private final List<Type> typeParameters;

    /**
     * Initializes a new instance of <code>ModelType</code>.
     *
     * @param modelName         The name of the new model instance
     * @param typeParameters    The type parameters of the new model instance
     */
    public ModelType(String modelName, List<Type> typeParameters) {
        Objects.requireNonNull(modelName);
        Objects.requireNonNull(typeParameters);

        this.modelName = modelName;
        this.typeParameters = Collections.unmodifiableList(new ArrayList<>(
                typeParameters
        ));
    }

    /**
     * Returns the name of the model that this type was derived from.
     *
     * @return  The name of the model that this type was derived from.
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * Returns the list of types that fill the type parameters of the model
     * that is referenced by this instance.
     *
     * @return  The list of types that fill the type parameters of the
     *          referenced model
     */
    public List<Type> getTypeParameters() {
        return this.typeParameters;
    }

    @Override
    public Category getCategory() {
        return Category.Model;
    }

    @Override
    public int hashCode() {
        return this.modelName.hashCode() ^ this.typeParameters.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModelType) {
            ModelType modelType = (ModelType) obj;

            return Objects.equals(this.modelName, modelType.getModelName()) &&
                    Objects.equals(
                            this.typeParameters,
                            modelType.getTypeParameters()
                    );
        }

        return false;
    }
}
