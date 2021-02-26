package org.fir3.cml.tool.tokenizer;

final class Helper {
    static Byte[] fromPrimitive(byte[] elements) {
        Byte[] result = new Byte[elements.length];

        for (int index = 0; index < elements.length; index++) {
            result[index] = elements[index];
        }

        return result;
    }
}
