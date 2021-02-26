package org.fir3.cml.tool.impl;

import org.fir3.cml.api.model.Attribute;
import org.fir3.cml.api.model.Model;
import org.fir3.cml.api.model.TypeParameter;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModelImplTest {
    private static final TypeParameter TEST_TYPE_PARAMETER_1 =
            new TypeParameterImpl("TestTypeParameter1");

    private static final TypeParameter TEST_TYPE_PARAMETER_2 =
            new TypeParameterImpl("TestTypeParameter1");

    private static final Attribute TEST_ATTRIBUTE_1 = new AttributeImpl(
            "testAttribute1",
            new ModelTypeImpl("UnknownModel")
    );

    private static final Attribute TEST_ATTRIBUTE_2 = new AttributeImpl(
            "testAttribute2",
            new ModelTypeImpl("UnknownModel")
    );

    @Test
    public void testCopyInConstructor() {
        // Setting up the required parameters

        EnumSet<Model.Flag> flags = EnumSet.of(Model.Flag.Builtin);

        List<TypeParameter> typeParameters = new ArrayList<>();
        typeParameters.add(ModelImplTest.TEST_TYPE_PARAMETER_1);

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(ModelImplTest.TEST_ATTRIBUTE_1);

        // Creating the model

        Model model = new ModelImpl("Test", flags, typeParameters, attributes);

        // Attempting to modify the model by modifying the passed parameters

        flags.remove(Model.Flag.Builtin);

        typeParameters.remove(ModelImplTest.TEST_TYPE_PARAMETER_1);
        typeParameters.add(ModelImplTest.TEST_TYPE_PARAMETER_2);

        attributes.remove(ModelImplTest.TEST_ATTRIBUTE_1);
        attributes.add(ModelImplTest.TEST_ATTRIBUTE_2);

        // Assertions

        EnumSet<Model.Flag> actualFlags = model.getFlags();
        assertEquals(1, actualFlags.size());
        assertTrue(actualFlags.contains(Model.Flag.Builtin));

        List<TypeParameter> actualTypeParameters = model.getTypeParameters();
        assertEquals(1, actualTypeParameters.size());
        assertTrue(actualTypeParameters.contains(
                ModelImplTest.TEST_TYPE_PARAMETER_1
        ));

        Set<Attribute> actualAttributes = model.getAttributes();
        assertEquals(1, actualAttributes.size());
        assertTrue(actualAttributes.contains(ModelImplTest.TEST_ATTRIBUTE_1));
    }

    @Test
    public void testUnmodifiability() {
        Model model = new ModelImpl(
                "Test",
                EnumSet.of(Model.Flag.Builtin),
                Collections.singletonList(ModelImplTest.TEST_TYPE_PARAMETER_1),
                Collections.singleton(ModelImplTest.TEST_ATTRIBUTE_1)
        );

        // We must not be able to modify the collections that are stored in
        // model (although we may be able to modify the collections that we
        // obtain by the model's getters).

        EnumSet<Model.Flag> flags = model.getFlags();

        try {
            flags.remove(Model.Flag.Builtin);
        } catch (Throwable ignored) {
            // Not mandatory here.
        }

        EnumSet<Model.Flag> actualFlags = model.getFlags();
        assertEquals(1, actualFlags.size());
        assertTrue(actualFlags.contains(Model.Flag.Builtin));

        List<TypeParameter> typeParameters = model.getTypeParameters();

        try {
            typeParameters.remove(ModelImplTest.TEST_TYPE_PARAMETER_1);
            typeParameters.add(ModelImplTest.TEST_TYPE_PARAMETER_2);
        } catch (Throwable ignored) {
            // Not mandatory here.
        }

        List<TypeParameter> actualTypeParameters = model.getTypeParameters();
        assertEquals(1, actualTypeParameters.size());
        assertTrue(actualTypeParameters.contains(
                ModelImplTest.TEST_TYPE_PARAMETER_1
        ));

        Set<Attribute> attributes = model.getAttributes();

        try {
            attributes.remove(ModelImplTest.TEST_ATTRIBUTE_1);
            attributes.add(ModelImplTest.TEST_ATTRIBUTE_2);
        } catch (Throwable ignored) {
            // Not mandatory here.
        }

        Set<Attribute> actualAttributes = model.getAttributes();
        assertEquals(1, actualAttributes.size());
        assertTrue(actualAttributes.contains(ModelImplTest.TEST_ATTRIBUTE_1));
    }
}
