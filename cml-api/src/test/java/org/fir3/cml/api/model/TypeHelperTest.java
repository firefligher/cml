package org.fir3.cml.api.model;

import org.fir3.cml.api.util.TypeHelper;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TypeHelperTest {
    @Test
    public void testNormalize() {
        // Setting up the model, domain and environment

        Set<Model> models = new HashSet<>();

        Model simpleModel = new Model(
                "SimpleModel",
                EnumSet.noneOf(Model.Flag.class),
                Collections.emptyList(),
                Collections.emptySet()
        );

        models.add(simpleModel);

        Model genericModel = new Model(
                "GenericModel",
                EnumSet.noneOf(Model.Flag.class),
                Arrays.asList(
                        new TypeParameter("TFirst"),
                        new TypeParameter("TSecond")
                ), Collections.emptySet()
        );

        models.add(genericModel);

        Domain domain = new Domain(
                "__test_domain__",
                EnumSet.noneOf(Domain.Flag.class),
                models
        );

        Environment environment = new Environment(
                Collections.singleton(domain)
        );

        // Setting up the two types that reference the same resolved type, but
        // from within different contexts

        Type relativeType = new ModelType(
                "GenericModel",
                Arrays.asList(
                        new ModelType(
                                "SimpleModel",
                                Collections.emptyList()
                        ),
                        new ParameterType("SecondType")
                )
        );

        Type absoluteType = new ModelType(
                "__test_domain__.GenericModel",
                Arrays.asList(
                        new ModelType(
                                "__test_domain__.SimpleModel",
                                Collections.emptyList()
                        ),
                        new ParameterType("SecondType")
                )
        );

        // Normalizing both types

        Type normalizedRelativeType = TypeHelper.normalize(
                relativeType,
                environment,
                domain
        );

        Type normalizedAbsoluteType = TypeHelper.normalize(
                absoluteType,
                environment,
                null
        );

        // Asserting that the original types are not equal, but the normalized
        // ones are.

        assertNotEquals(relativeType, absoluteType);
        assertEquals(normalizedRelativeType, normalizedAbsoluteType);
    }

    @Test
    public void testReversibility() {
        // Setting up the model, domain and environment

        Set<Model> models = new HashSet<>();

        Model simpleModel = new Model(
                "SimpleModel",
                EnumSet.noneOf(Model.Flag.class),
                Collections.emptyList(),
                Collections.emptySet()
        );

        models.add(simpleModel);

        Model genericModel = new Model(
                "GenericModel",
                EnumSet.noneOf(Model.Flag.class),
                Arrays.asList(
                        new TypeParameter("TFirst"),
                        new TypeParameter("TSecond")
                ), Collections.emptySet()
        );

        models.add(genericModel);

        Domain domain = new Domain(
                "__test_domain__",
                EnumSet.noneOf(Domain.Flag.class),
                models
        );

        Environment environment = new Environment(
                Collections.singleton(domain)
        );

        // Setting up a type

        Type type = new ModelType(
                "GenericModel",
                Arrays.asList(
                        new ModelType(
                                "SimpleModel",
                                Collections.emptyList()
                        ),
                        new ParameterType("SecondType")
                )
        );

        // Converting the type to its unique string representation and back to
        // a type instance

        Type reversedType = TypeHelper.fromString(TypeHelper.toString(
                type,
                environment,
                domain
        ));

        // Asserting that the normalized instance of type is equal to the
        // reversedType

        assertEquals(
                TypeHelper.normalize(type, environment, domain),
                reversedType
        );
    }
}
