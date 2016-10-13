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
package com.github.projectsandstone.asmmeta.resolver;

import com.github.jonathanxd.iutils.description.DescriptionUtil;
import com.github.projectsandstone.asmmeta.element.IMetaElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

public class CommonResolvers {

    public static final RethrowFunction<IMetaElement<Class<?>>, Class<?>> CLASS_RESOLVER =
            classMetaElement -> DescriptionUtil.resolve(classMetaElement.getName(), CommonResolvers.class.getClassLoader());

    public static final RethrowFunction<IMetaElement<Field>, Field> FIELD_RESOLVER =
            fieldMetaElement -> {

                Class<?> resolvedClass = CommonResolvers.resolveDeclaringClass(fieldMetaElement);

                String desc = fieldMetaElement.getDesc();


                Class<?> type = DescriptionUtil.resolveUnsafe(desc, resolvedClass.getClassLoader());

                Field declaredField = resolvedClass.getDeclaredField(fieldMetaElement.getName());

                Class<?> foundType = declaredField.getType();

                if (!foundType.equals(type)) {
                    throw new IllegalStateException("Cannot resolve element, found field type: '" + foundType + "', expected: '" + type + "'. Field: '" + declaredField + "'.");
                }

                return declaredField;

            };

    public static final RethrowFunction<IMetaElement<Method>, Method> METHOD_RESOLVER =
            methodMetaElement -> {
                Class<?> declaring = CommonResolvers.resolveDeclaringClass(methodMetaElement);
                String name = methodMetaElement.getName(); // Method name
                String desc = methodMetaElement.getDesc(); // Method Types description

                String[] parameterTypesNames = DescriptionUtil.getParameterTypes(desc);
                String returnTypeName = DescriptionUtil.getReturnType(desc);

                Class<?>[] parameterTypes = DescriptionUtil.resolveUnsafe(parameterTypesNames, declaring.getClassLoader());
                Class<?> returnType = DescriptionUtil.resolveUnsafe(returnTypeName, declaring.getClassLoader());

                Method method;

                try {
                    method = declaring.getMethod(name, parameterTypes);
                } catch (Exception e) {
                    method = declaring.getDeclaredMethod(name, parameterTypes);
                }

                Class<?> foundType = method.getReturnType();

                if (!foundType.equals(returnType)) {
                    throw new IllegalStateException("Cannot resolve element, found method return type: '" + foundType + "', expected: '" + returnType + "'. Method: '" + method + "'.");
                }

                return method;
            };

    public static final RethrowFunction<IMetaElement<Constructor<?>>, Constructor<?>> CONSTRUCTOR_RESOLVER =
            methodMetaElement -> {
                Class<?> declaring = CommonResolvers.resolveDeclaringClass(methodMetaElement);
                // Constructor name is always <init> (or <clinit>)
                String desc = methodMetaElement.getDesc(); // Method Types description

                String[] parameterTypesNames = DescriptionUtil.getParameterTypes(desc);
                // Constructor return type is always 'void'

                Class<?>[] parameterTypes = DescriptionUtil.resolveUnsafe(parameterTypesNames, declaring.getClassLoader());

                Constructor<?> constructor;

                try {
                    constructor = declaring.getConstructor(parameterTypes);
                } catch (Exception e) {
                    constructor = declaring.getDeclaredConstructor(parameterTypes);
                }

                return constructor;
            };

    @SuppressWarnings("unchecked")
    public static final RethrowFunction<IMetaElement<Annotation>, Annotation> ANNOTATION_RESOLVER =
            annotationIMetaElement -> {
                AnnotatedElement annotatedElement = CommonResolvers.resolveDeclaringElement(annotationIMetaElement);
                Class<?> declaring = CommonResolvers.resolveDeclaringClass(annotationIMetaElement.getEnclosingElement());

                String clName = annotationIMetaElement.getName();

                Class<?> annotationType = DescriptionUtil.resolveUnsafe(clName, declaring.getClassLoader());

                return annotatedElement.getAnnotation((Class<? extends Annotation>) annotationType);
            };

    @SuppressWarnings("unchecked")
    public static final RethrowFunction<IMetaElement<Enum<?>>, Enum<?>> ENUM_RESOLVER =
            enumIMetaElement -> {
                String desc = enumIMetaElement.getDesc();

                Class<?> type = DescriptionUtil.resolveUnsafe(desc, CommonResolvers.class.getClassLoader());

                if (!type.isEnum()) {
                    throw new IllegalStateException("Resolved class is not an enum!");
                }

                Field declaredField = type.getDeclaredField(enumIMetaElement.getName());

                if (!declaredField.getType().isEnum()) {
                    throw new IllegalStateException("Resolved field is not an enum!");
                }

                return (Enum<?>) declaredField.get(null);
            };

    public static AnnotatedElement resolveDeclaringElement(IMetaElement<?> metaElement) {
        IMetaElement<?> enclosingElement = metaElement.getEnclosingElement();

        Object resolve = enclosingElement == null ? null : enclosingElement.getResolvedInstance();

        if (enclosingElement == null) {
            throw new IllegalStateException("Cannot determine enclosing element!");
        }

        if (!(resolve instanceof AnnotatedElement)) {
            throw new IllegalStateException("Enclosing type is not an AnnotatedElement.");
        }

        return (AnnotatedElement) resolve;
    }

    public static Class<?> resolveDeclaringClass(IMetaElement<?> metaElement) {
        IMetaElement<?> enclosingElement = metaElement.getEnclosingElement();

        Object resolve = enclosingElement == null ? null : enclosingElement.getResolvedInstance();

        if (enclosingElement == null) {
            throw new IllegalStateException("Cannot determine enclosing element!");
        }

        if (!(resolve instanceof Class<?>)) {
            throw new IllegalStateException("Enclosing type is not a class.");
        }

        return (Class<?>) resolve;
    }

    private interface RethrowFunction<T, R> extends Function<T, R> {

        R applyRethrow(T t) throws Throwable;

        @Override
        default R apply(T t) {
            try {
                return this.applyRethrow(t);
            } catch (Throwable th) {
                throw new RuntimeException(th);
            }
        }
    }
}
