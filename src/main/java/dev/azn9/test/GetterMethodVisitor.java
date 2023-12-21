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

public class GetterMethodVisitor extends MethodVisitor {

    public static class GetterMethodVisitorWrapper implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {

        private final String fieldName;
        private final Class<?> finalFieldType;
        private final String originParameterType;
        private final String finalParameterType;

        public GetterMethodVisitorWrapper(String fieldName, Class<?> finalFieldType, String originParameterType, String finalParameterType) {
            this.fieldName = fieldName;
            this.finalFieldType = finalFieldType;
            this.originParameterType = originParameterType;
            this.finalParameterType = finalParameterType;
        }

        @Override
        public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
            // Start the method
            methodVisitor.visitCode();

            return new GetterMethodVisitor(methodVisitor, this.fieldName, this.finalFieldType, this.originParameterType, this.finalParameterType);
        }
    }

    /**
     * The method desriptor of the "get" method in {@link Bootstrap}
     */
    private final MethodType invokedType = MethodType.methodType(Object.class, Object.class, String.class);

    private final MethodVisitor methodVisitor;
    private final String fieldName;
    private final Class<?> fieldType;
    private final String originParameterType;
    private final String finalParameterType;

    // The GETFIELD instruction is preceded by an ALOAD 0 instruction that we need to remove when we replace the GETFIELD instruction
    private boolean potentialSkipALOAD0 = false;

    private GetterMethodVisitor(MethodVisitor methodVisitor, String fieldName, Class<?> fieldType, String originParameterType, String finalParameterType) {
        super(Opcodes.ASM7, methodVisitor);

        this.methodVisitor = methodVisitor;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.originParameterType = originParameterType;
        this.finalParameterType = finalParameterType;
    }

    @Override
    public void visitVarInsn(int opcode, int operand) {
        // Skip ALOAD 0
        if (opcode == Opcodes.ALOAD && operand == 0) {
            // We skip ALOAD 0
            this.potentialSkipALOAD0 = true;
        } else {
            // If we skipped ALOAD 0, re-emit it
            if (this.potentialSkipALOAD0) {
                super.visitVarInsn(Opcodes.ALOAD, 0);
                this.potentialSkipALOAD0 = false;
            }

            // Call the original instruction
            super.visitVarInsn(opcode, operand);
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (this.potentialSkipALOAD0 && opcode == Opcodes.GETFIELD && this.fieldName.equals(name)) {
            this.potentialSkipALOAD0 = false;

            // Push "this" on the stack
            this.methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

            // Push the field name on the stack
            this.methodVisitor.visitLdcInsn(name);

            // Invoke Bootstrap#get(String, String) with the field owner and name
            this.methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    Bootstrap.class.getName().replace('.', '/'), // JVM internal name
                    "get",
                    this.invokedType.descriptorString(), // JVM internal descriptor
                    false
            );

            // No need to cast the result if the field type is the same as the result type
            if (!this.originParameterType.equals(this.finalParameterType)) {
                // Call ASMUtils#castBackToX(Object) to cast the result to the field type
                this.methodVisitor.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        ASMUtils.getInternalName(),
                        ASMUtils.getCastBackMethodName(this.fieldType),
                        "(Ljava/lang/Object;)" + this.originParameterType, // JVM internal descriptor
                        false
                );
            }
        } else {
            // Re-emit the original ALOAD 0 if needed
            if (this.potentialSkipALOAD0) {
                super.visitVarInsn(Opcodes.ALOAD, 0);
                this.potentialSkipALOAD0 = false;
            }

            // Call the original instruction
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

}
