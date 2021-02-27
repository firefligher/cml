package org.fir3.cml.tool.parser;

import org.fir3.cml.api.model.Model;
import org.fir3.cml.api.model.TypeParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

final class ModelDeclaration {
    private final String name;
    private final EnumSet<Model.Flag> flags;
    private final List<TypeParameter> typeParameters;

    public ModelDeclaration(
            String name,
            EnumSet<Model.Flag> flags,
            List<TypeParameter> typeParameters
    ) {
        this.name = name;
        this.flags = EnumSet.copyOf(flags);
        this.typeParameters = Collections.unmodifiableList(new ArrayList<>(
                typeParameters
        ));
    }

    public String getName() {
        return this.name;
    }

    public EnumSet<Model.Flag> getFlags() {
        return EnumSet.copyOf(this.flags);
    }

    public List<TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }
}
