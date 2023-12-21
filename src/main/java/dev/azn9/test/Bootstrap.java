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

@SuppressWarnings("unused")
public class Bootstrap {

    private Bootstrap() {
        // Empty
    }

    @SuppressWarnings({
            "java:S3011", // Reflection
            "java:S112", // throws Exception
    })
    public static Object get(Object owner, String name) throws Exception {
        Field field = owner.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(owner); // Default value
    }

    @SuppressWarnings({
            "java:S3011", // Reflection
            "java:S112", // throws Exception
    })
    public static Void set(Object owner, Object value, String name) throws Exception {
        Field field = owner.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(owner, value); // Default value
        return null;
    }

}