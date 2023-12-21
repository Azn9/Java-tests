/*
 * Copyright 2023-Now Axel "Azn9" JOLY - contact@azn9.dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.azn9.test;

import java.lang.reflect.Field;

@SuppressWarnings("unused") // ASM
public class ASMUtils {

    private static final String INTERNAL_CLASS_NAME = ASMUtils.class.getName().replace('.', '/');

    private ASMUtils() {
        // Utility class
    }

    /**
     * Get the class that correspond to the given field.
     * This method always return the object type, even if the field is a primitive type.
     *
     * @param declaredField The field to get the type from
     * @return The class that correspond to the given field
     */
    public static Class<?> getObjectFieldType(Field declaredField) {
        Class<?> fieldType = declaredField.getType();

        if (fieldType.isPrimitive()) {
            if (fieldType == int.class) {
                fieldType = Integer.class;
            } else if (fieldType == long.class) {
                fieldType = Long.class;
            } else if (fieldType == float.class) {
                fieldType = Float.class;
            } else if (fieldType == double.class) {
                fieldType = Double.class;
            } else if (fieldType == boolean.class) {
                fieldType = Boolean.class;
            } else if (fieldType == byte.class) {
                fieldType = Byte.class;
            } else if (fieldType == short.class) {
                fieldType = Short.class;
            } else if (fieldType == char.class) {
                fieldType = Character.class;
            } else {
                throw new IllegalStateException("Unknown primitive type: " + fieldType); // Ptdr ?
            }
        }

        return fieldType;
    }

    /**
     * Get the JVM internal descriptor of the given class.
     *
     * @param clazz The class to get the descriptor from
     * @return The JVM internal descriptor of the given class
     */
    public static String getParameterName(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == int.class) {
                return "I";
            } else if (clazz == long.class) {
                return "J";
            } else if (clazz == float.class) {
                return "F";
            } else if (clazz == double.class) {
                return "D";
            } else if (clazz == boolean.class) {
                return "Z";
            } else if (clazz == byte.class) {
                return "B";
            } else if (clazz == short.class) {
                return "S";
            } else if (clazz == char.class) {
                return "C";
            } else {
                throw new IllegalStateException("Unknown primitive type: " + clazz); // Ptdr ?
            }
        } else {
            return "L" + clazz.getName().replace('.', '/') + ";";
        }
    }

    /**
     * Get the method name to cast back the given class.
     *
     * @param clazz The class to get the method name from
     * @return The method name to cast back the given class
     */
    public static String getCastBackMethodName(Class<?> clazz) {
        if (clazz == Integer.class) {
            return "castBackToInt";
        } else if (clazz == Long.class) {
            return "castBackToLong";
        } else if (clazz == Float.class) {
            return "castBackToFloat";
        } else if (clazz == Double.class) {
            return "castBackToDouble";
        } else if (clazz == Boolean.class) {
            return "castBackToBoolean";
        } else if (clazz == Byte.class) {
            return "castBackToByte";
        } else if (clazz == Short.class) {
            return "castBackToShort";
        } else if (clazz == Character.class) {
            return "castBackToChar";
        } else {
            return "";
        }
    }

    /**
     * Cast an Integer to int.
     *
     * @param value The Integer to cast
     * @return The int value of the given Integer
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static int castBackToInt(Object value) {
        return ((Integer) value).intValue();
    }

    /**
     * Cast a Long to long.
     *
     * @param value The Long to cast
     * @return The long value of the given Long
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static long castBackToLong(Object value) {
        return ((Long) value).longValue();
    }

    /**
     * Cast a Float to float.
     *
     * @param value The Float to cast
     * @return The float value of the given Float
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static float castBackToFloat(Object value) {
        return ((Float) value).floatValue();
    }

    /**
     * Cast a Double to double.
     *
     * @param value The Double to cast
     * @return The double value of the given Double
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static double castBackToDouble(Object value) {
        return ((Double) value).doubleValue();
    }

    /**
     * Cast a Boolean to boolean.
     *
     * @param value The Boolean to cast
     * @return The boolean value of the given Boolean
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static boolean castBackToBoolean(Object value) {
        return ((Boolean) value).booleanValue();
    }

    /**
     * Cast a Byte to byte.
     *
     * @param value The Byte to cast
     * @return The byte value of the given Byte
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static byte castBackToByte(Object value) {
        return ((Byte) value).byteValue();
    }

    /**
     * Cast a Short to short.
     *
     * @param value The Short to cast
     * @return The short value of the given Short
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static short castBackToShort(Object value) {
        return ((Short) value).shortValue();
    }

    /**
     * Cast a Character to char.
     *
     * @param value The Character to cast
     * @return The char value of the given Character
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public static char castBackToChar(Object value) {
        return ((Character) value).charValue();
    }

    /**
     * Get the JVM internal name of the {@link ASMUtils} class.
     *
     * @return the JVM internal name of the {@link ASMUtils} class
     */
    public static String getInternalName() {
        return ASMUtils.INTERNAL_CLASS_NAME;
    }
}
