package org.fir3.cml.api.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {
    private static final TypeParameter TEST_TYPE_PARAMETER_1 =
            new TypeParameter("TestTypeParameter1");

    private static final TypeParameter TEST_TYPE_PARAMETER_2 =
            new TypeParameter("TestTypeParameter1");

    private static final Attribute TEST_ATTRIBUTE_1 = new Attribute(
            "testAttribute1",
            new ModelType("UnknownModel", Collections.emptyList())
    );

    private static final Attribute TEST_ATTRIBUTE_2 = new Attribute(
            "testAttribute2",
            new ModelType("UnknownModel", Collections.emptyList())
    );

    @Test
    public void testCopyInConstructor() {
        // Setting up the required parameters

        EnumSet<Model.Flag> flags = EnumSet.of(Model.Flag.Builtin);

        List<TypeParameter> typeParameters = new ArrayList<>();
        typeParameters.add(ModelTest.TEST_TYPE_PARAMETER_1);

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(ModelTest.TEST_ATTRIBUTE_1);

        // Creating the model

        Model model = new Model("Test", flags, typeParameters, attributes);

        // Attempting to modify the model by modifying the passed parameters

        flags.remove(Model.Flag.Builtin);

        typeParameters.remove(ModelTest.TEST_TYPE_PARAMETER_1);
        typeParameters.add(ModelTest.TEST_TYPE_PARAMETER_2);

        attributes.remove(ModelTest.TEST_ATTRIBUTE_1);
        attributes.add(ModelTest.TEST_ATTRIBUTE_2);

        // Assertions

        EnumSet<Model.Flag> actualFlags = model.getFlags();
        assertEquals(1, actualFlags.size());
        assertTrue(actualFlags.contains(Model.Flag.Builtin));

        List<TypeParameter> actualTypeParameters = model.getTypeParameters();
        assertEquals(1, actualTypeParameters.size());
        assertTrue(actualTypeParameters.contains(
                ModelTest.TEST_TYPE_PARAMETER_1
        ));

        Set<Attribute> actualAttributes = model.getAttributes();
        assertEquals(1, actualAttributes.size());
        assertTrue(actualAttributes.contains(ModelTest.TEST_ATTRIBUTE_1));
    }

    @Test
    public void testUnmodifiability() {
        Model model = new Model(
                "Test",
                EnumSet.of(Model.Flag.Builtin),
                Collections.singletonList(ModelTest.TEST_TYPE_PARAMETER_1),
                Collections.singleton(ModelTest.TEST_ATTRIBUTE_1)
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
            typeParameters.remove(ModelTest.TEST_TYPE_PARAMETER_1);
            typeParameters.add(ModelTest.TEST_TYPE_PARAMETER_2);
        } catch (Throwable ignored) {
            // Not mandatory here.
        }

        List<TypeParameter> actualTypeParameters = model.getTypeParameters();
        assertEquals(1, actualTypeParameters.size());
        assertTrue(actualTypeParameters.contains(
                ModelTest.TEST_TYPE_PARAMETER_1
        ));

        Set<Attribute> attributes = model.getAttributes();

        try {
            attributes.remove(ModelTest.TEST_ATTRIBUTE_1);
            attributes.add(ModelTest.TEST_ATTRIBUTE_2);
        } catch (Throwable ignored) {
            // Not mandatory here.
        }

        Set<Attribute> actualAttributes = model.getAttributes();
        assertEquals(1, actualAttributes.size());
        assertTrue(actualAttributes.contains(ModelTest.TEST_ATTRIBUTE_1));
    }

    @Test
    public void testEqualsAndHashCode() {
        Model model1 = new Model(
                "TestModel",
                EnumSet.of(Model.Flag.Builtin),
                Collections.singletonList(new TypeParameter("Param")),
                Collections.singleton(new Attribute(
                        "_TestAttribute",
                        new ParameterType("Param")
                ))
        );

        Model model2 = new Model(
                "TestModel",
                EnumSet.of(Model.Flag.Builtin),
                Collections.singletonList(new TypeParameter("Param")),
                Collections.singleton(new Attribute(
                        "_TestAttribute",
                        new ParameterType("Param")
                ))
        );

        assertEquals(model1, model2);
        assertEquals(model2, model1);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    public void testConstructorCheckForAttributeCollision() {
        Set<Attribute> attributes = new HashSet<>();

        attributes.add(new Attribute(
                "_CollidingAttribute",
                new ModelType("Model1", Collections.emptyList())
        ));

        attributes.add(new Attribute(
                "_CollidingAttribute",
                new ModelType("Model2", Collections.emptyList())
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Model(
                        "BadModel",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        attributes
                )
        );
    }

    @Test
    public void testConstructorCheckForParamTypeSanity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Model(
                        "BadModel",
                        EnumSet.noneOf(Model.Flag.class),
                        Collections.emptyList(),
                        Collections.singleton(new Attribute(
                                "_InvalidAttribute",
                                new ParameterType("UnknownTypeParam")
                        ))
                )
        );
    }

    @Test
    public void testConstructorCheckForTypeParamCollision() {
        List<TypeParameter> typeParameters = Arrays.asList(
                new TypeParameter("CollidingParam"),
                new TypeParameter("CollidingParam")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Model(
                        "BadModel",
                        EnumSet.noneOf(Model.Flag.class),
                        typeParameters,
                        Collections.emptySet()
                )
        );
    }
}
