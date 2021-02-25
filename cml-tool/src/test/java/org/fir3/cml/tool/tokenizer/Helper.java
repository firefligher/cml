package org.fir3.cml.tool.tokenizer;

final class Helper {
    static Integer[] fromByteArray(byte[] elements) {
        Integer[] result = new Integer[elements.length];

        for (int index = 0; index < elements.length; index++) {
            result[index] = (((int) elements[index]) & 0xFF);
        }

        return result;
    }
}
