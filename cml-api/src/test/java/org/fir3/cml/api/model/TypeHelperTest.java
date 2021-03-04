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

    @Test
    public void testNormalizeParameterTypes() {
        // Setting up a proper environment

        Domain domain = new Domain(
                "some.domain",
                EnumSet.noneOf(Domain.Flag.class),
                Collections.singleton(new Model(
                        "ComplexModel",
                        EnumSet.noneOf(Model.Flag.class),
                        Arrays.asList(
                                new TypeParameter("Param1"),
                                new TypeParameter("Param2")
                        ),
                        Collections.emptySet()
                ))
        );

        Environment env = new Environment(Collections.singleton(domain));

        // Creating the type that we will process in this test

        Type type = new ModelType("ComplexModel", Arrays.asList(
                new ModelType("ComplexModel", Arrays.asList(
                        new ParameterType("Parameter1"),
                        new ParameterType("OtherParameter")
                )),
                new ParameterType("OtherParameter")
        ));

        // Converting the model to its string representation

        String typeStr = TypeHelper.toString(type, env, domain);

        // Asserting that the typeStr is correct

        assertEquals(
                "GM:some.domain.ComplexModel<" +
                        "GM:some.domain.ComplexModel<P:P1,P:P2>,P:P2>",
                typeStr
        );
    }
}
