package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Domain;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DomainDeclarationTest {
    @Test
    public void testCopyInConstructor() {
        EnumSet<Domain.Flag> flags = EnumSet.of(Domain.Flag.Ubiquitous);
        DomainDeclaration declaration = new DomainDeclaration(
                "domain.test",
                flags
        );

        // Modifying the flags EnumSet

        flags.clear();

        // Validating that modifying the flags EnumSet did not modify the
        // declaration instance

        assertTrue(declaration.getFlags().contains(Domain.Flag.Ubiquitous));
    }

    @Test
    public void testImmutability() {
        DomainDeclaration declaration = new DomainDeclaration(
                "domain.test",
                EnumSet.of(Domain.Flag.Ubiquitous)
        );

        // Trying to modify the flags of the declaration

        try {
            declaration.getFlags().clear();
        } catch (Throwable ignored) { }

        // Asserting that the flags of declaration have not been modified

        EnumSet<Domain.Flag> actualFlags = declaration.getFlags();

        assertTrue(actualFlags.contains(Domain.Flag.Ubiquitous));
    }
}
