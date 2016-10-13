/**
 *      ASMMeta - Index information about classes and elements.
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2016 Sandstone <https://github.com/ProjectSandstone/ASMMeta/>
 *      Copyright (c) contributors
 *
 *
 *      Permission is hereby granted, free of charge, to any person obtaining a copy
 *      of this software and associated documentation files (the "Software"), to deal
 *      in the Software without restriction, including without limitation the rights
 *      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *      copies of the Software, and to permit persons to whom the Software is
 *      furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in
 *      all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *      THE SOFTWARE.
 */
package com.github.projectsandtone.asmmeta.asm;

import com.github.projectsandtone.asmmeta.data.MetaData;
import com.github.projectsandtone.asmmeta.element.AnnotationElement;
import com.github.projectsandtone.asmmeta.element.ClassElement;
import com.github.projectsandtone.asmmeta.element.ConstructorElement;
import com.github.projectsandtone.asmmeta.element.FieldElement;
import com.github.projectsandtone.asmmeta.element.IMetaElement;
import com.github.projectsandtone.asmmeta.element.MetaElement;
import com.github.projectsandtone.asmmeta.element.MethodElement;
import com.github.projectsandtone.asmmeta.resolver.Resolvers;
import com.github.projectsandtone.asmmeta.value.KeyedValue;
import com.github.projectsandtone.asmmeta.value.Value;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class ASMMetaClassVisitor extends ClassVisitor {
    private final Resolvers resolvers;
    private MetaElement<?> current;

    public ASMMetaClassVisitor(int api) {
        this(api, new Resolvers.Default());
    }

    public ASMMetaClassVisitor(int api, ClassVisitor cv) {
        this(api, cv, new Resolvers.Default());
    }

    public ASMMetaClassVisitor(int api, Resolvers resolvers) {
        super(api);
        this.resolvers = resolvers;
    }

    public ASMMetaClassVisitor(int api, ClassVisitor cv, Resolvers resolvers) {
        super(api, cv);
        this.resolvers = resolvers;
    }

    private AnnotationElement createAnnotationElement(MetaElement<?> metaElement, String desc) {
        return new AnnotationElement(metaElement, 0, desc, desc, new MetaData(), this.resolvers.getAnnotationResolver());
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        ClassElement classElement = new ClassElement(
                this.current,
                access,
                name,
                "",
                new MetaData(),
                this.resolvers.getClassResolver(),
                superName,
                interfaces);

        this.addToEnclosing(classElement);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationElement annotationElement = createAnnotationElement(this.current, desc);

        this.current.getMetaData().getAnnotationList().add(annotationElement);

        return new ASMMetaAnnotationVisitor(this.api,
                super.visitAnnotation(desc, visible),
                annotationElement,
                annotationElement.getValues()
        );
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {


        FieldElement fieldElement = new FieldElement(this.current, access, name, desc, new MetaData(), this.resolvers.getFieldResolver());

        this.addToEnclosing(fieldElement);

        return new FieldVisitor(Opcodes.ASM5, super.visitField(access, name, desc, signature, value)) {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                AnnotationElement annotationElement = createAnnotationElement(fieldElement, desc);

                fieldElement.getMetaData().getAnnotationList().add(annotationElement);

                return new ASMMetaAnnotationVisitor(this.api,
                        super.visitAnnotation(desc, visible),
                        annotationElement,
                        annotationElement.getValues()
                );
            }

            @Override
            public void visitEnd() {
                fieldElement.immutate();
                super.visitEnd();
            }
        };
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MetaElement<?> mElement;

        if (name.equals("<init>") || name.equals("<clinit>")) {
            mElement = new ConstructorElement(this.current, access, name, signature, new MetaData(), this.resolvers.getConstructorResolver());
        } else {
            mElement = new MethodElement(this.current, access, name, signature, new MetaData(), this.resolvers.getMethodResolver());
        }

        this.addToEnclosing(mElement);

        return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                AnnotationElement annotationElement = createAnnotationElement(mElement, desc);

                mElement.getMetaData().getAnnotationList().add(annotationElement);

                return new ASMMetaAnnotationVisitor(this.api,
                        super.visitAnnotation(desc, visible),
                        annotationElement,
                        annotationElement.getValues()
                );
            }

            @Override
            public void visitEnd() {
                mElement.immutate();
                super.visitEnd();
            }
        };
    }

    @Override
    public void visitEnd() {
        MetaElement<?> current = this.current;

        if (current != null) {
            current.immutate();
        }

        super.visitEnd();
    }

    public MetaElement<?> getCurrent() {
        return this.current;
    }

    public void addToEnclosing(MetaElement<?> iMetaElement) {
        IMetaElement<?> enclosing = this.current;

        if (enclosing == null) {
            current = iMetaElement;
        } else {
            enclosing.getChildElements().add(iMetaElement);
        }
    }


    private final class ASMMetaAnnotationVisitor extends AnnotationVisitor {
        private final MetaElement<?> element;
        private final List<Value<?>> values;

        public ASMMetaAnnotationVisitor(int api, AnnotationVisitor av, MetaElement<?> element, List<Value<?>> values) {
            super(api, av);
            this.element = element;
            this.values = values;
        }

        public Value<?> createValue(String name, Object value) {
            if (name == null)
                return new Value<>(value);
            else
                return new KeyedValue<>(name, value);
        }

        @Override
        public void visit(String name, Object value) {

            Value<?> createdValue;

            if(!(value instanceof Type)) {
                boolean array = value.getClass().isArray();

                if(!array) {
                    createdValue = this.createValue(name, value);
                } else {

                    AnnotationVisitor annotationVisitor = this.visitArray(name);

                    Object[] values = (Object[]) value;

                    for (Object o : values) {
                        annotationVisitor.visit(null, o);
                    }

                    annotationVisitor.visitEnd();

                    return;
                }

            } else {
                Type type = (Type) value;
                createdValue = this.createValue(name, new ClassElement(this.element, 0, type.getInternalName(), "", new MetaData(), resolvers.getClassResolver(), null, new String[0]));
            }

            this.values.add(createdValue);

            super.visit(name, value);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            AnnotationElement annotationElement = createAnnotationElement(this.element, desc);

            Value<?> createdValue = this.createValue(name, annotationElement);

            this.values.add(createdValue);

            return new ASMMetaAnnotationVisitor(
                    this.api,
                    super.visitAnnotation(name, desc),
                    annotationElement,
                    annotationElement.getValues());
        }

        @Override
        public AnnotationVisitor visitArray(String name) {

            List<Value<?>> list = new ArrayList<>();

            this.values.add(this.createValue(name, list));

            return new ASMMetaAnnotationVisitor(this.api, super.visitArray(name), this.element, list);
        }

        @Override
        public void visitEnum(String name, String desc, String value) {
            super.visitEnum(name, desc, value);
        }

        @Override
        public void visitEnd() {
            this.element.immutate();
            super.visitEnd();
        }
    }
}
