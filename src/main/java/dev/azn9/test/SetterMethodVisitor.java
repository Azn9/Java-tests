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

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.pool.TypePool;

import java.lang.invoke.MethodType;

public class SetterMethodVisitor extends MethodVisitor {

    public static class SetterMethodVisitorWrapper implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {

        private final String fieldName;

        public SetterMethodVisitorWrapper(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
            // Start the method
            methodVisitor.visitCode();

            return new SetterMethodVisitor(methodVisitor, this.fieldName);
        }
    }

    /**
     * The method desriptor of the "get" method in {@link Bootstrap}
     */
    private final MethodType invokedType = MethodType.methodType(Void.class, Object.class, Object.class, String.class);

    private final MethodVisitor methodVisitor;
    private final String fieldName;

    private String skippedOwner;
    private String skippedName;
    private String skippedDescriptor;
    private boolean skippedIsInterface;

    // The PUTFIELD instruction is preceded by an INVOKEVIRTUAL instruction if the field is a primitive type
    private boolean potentialSkipINVOKEVIRTUAL = false;

    private SetterMethodVisitor(MethodVisitor methodVisitor, String fieldName) {
        super(Opcodes.ASM7, methodVisitor);

        this.methodVisitor = methodVisitor;
        this.fieldName = fieldName;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        // Skip INVOKEVIRTUAL
        if (opcode == Opcodes.INVOKEVIRTUAL) {
            // We skip INVOKEVIRTUAL
            this.potentialSkipINVOKEVIRTUAL = true;

            this.skippedOwner = owner;
            this.skippedName = name;
            this.skippedDescriptor = descriptor;
            this.skippedIsInterface = isInterface;
        } else {
            // If we skipped INVOKEVIRTUAL, re-emit it
            if (this.potentialSkipINVOKEVIRTUAL) {
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, this.skippedOwner, this.skippedName, this.skippedDescriptor, this.skippedIsInterface);
                this.potentialSkipINVOKEVIRTUAL = false;
            }

            // Call the original instruction
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (this.potentialSkipINVOKEVIRTUAL && opcode == Opcodes.PUTFIELD && this.fieldName.equals(name)) {
            this.potentialSkipINVOKEVIRTUAL = false;

            // Push "this" on the stack
            this.methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

            // Push the field name on the stack
            this.methodVisitor.visitLdcInsn(name);

            // Invoke Bootstrap#get(String, String) with the field owner and name
            this.methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    Bootstrap.class.getName().replace('.', '/'), // JVM internal name
                    "set",
                    this.invokedType.descriptorString(), // JVM internal descriptor
                    false
            );
        } else {
            // Re-emit the original INVOKEVIRTUAL if needed
            if (this.potentialSkipINVOKEVIRTUAL) {
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, this.skippedOwner, this.skippedName, this.skippedDescriptor, this.skippedIsInterface);
                this.potentialSkipINVOKEVIRTUAL = false;
            }

            // Call the original instruction
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

}
