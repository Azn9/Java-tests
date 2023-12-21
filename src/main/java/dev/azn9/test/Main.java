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

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.VisibilityBridgeStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.matcher.ElementMatchers;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Main {

    public static void main(String[] args) throws Exception {
        ByteBuddyAgent.install();

        Test test = new Test();
        test.setI(1);
        test.readI();

        DynamicType.Builder<Test> tempBuilder = new ByteBuddy()
                .with(VisibilityBridgeStrategy.Default.NEVER)
                .redefine(Test.class);

        for (Field declaredField : Test.class.getDeclaredFields()) {
            int modifiers = declaredField.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }

            Class<?> finalFieldType = ASMUtils.getObjectFieldType(declaredField);
            String originParameterType = ASMUtils.getParameterName(declaredField.getType());
            String finalParameterType = ASMUtils.getParameterName(finalFieldType);

            tempBuilder = tempBuilder.visit(
                    new AsmVisitorWrapper.ForDeclaredMethods().method(
                                    ElementMatchers.definedMethod(ElementMatchers.any()),
                                    new GetterMethodVisitor.GetterMethodVisitorWrapper(
                                            declaredField.getName(),
                                            finalFieldType,
                                            originParameterType,
                                            finalParameterType
                                    ),
                                    new SetterMethodVisitor.SetterMethodVisitorWrapper(
                                            declaredField.getName()
                                    )
                            )
                            .writerFlags(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
                            .readerFlags(ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG)
            );
        }

        DynamicType.Unloaded<Test> unloaded = tempBuilder.make();

        org.objectweb.asm.ClassReader reader = new org.objectweb.asm.ClassReader(unloaded.getBytes());
        ClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.out));
        reader.accept(tcv, 0);

        Class<? extends Test> tempClass;
        try {
            ByteBuddyAgent.getInstrumentation().retransformClasses();

            tempClass = unloaded
                    .load(Test.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
                    .getLoaded();
        } catch (Throwable t) {
            System.err.println(t.getCause());
            t.printStackTrace();
            return;
        }

        Test test2 = tempClass.getDeclaredConstructor().newInstance();
        test2.setI(2);
        test2.readI();

        unloaded.close();
    }

}
