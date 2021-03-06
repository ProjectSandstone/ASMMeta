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

import com.github.projectsandstone.asmmeta.element.IMetaElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

public interface Resolvers {

    Function<IMetaElement<Annotation>, Annotation> getAnnotationResolver();

    Function<IMetaElement<Constructor<?>>, Constructor<?>> getConstructorResolver();

    Function<IMetaElement<Class<?>>, Class<?>> getClassResolver();

    Function<IMetaElement<Enum<?>>, Enum<?>> getEnumResolver();

    Function<IMetaElement<Field>, Field> getFieldResolver();

    Function<IMetaElement<Method>, Method> getMethodResolver();


    class Default implements Resolvers {

        @Override
        public Function<IMetaElement<Annotation>, Annotation> getAnnotationResolver() {
            return CommonResolvers.ANNOTATION_RESOLVER;
        }

        @Override
        public Function<IMetaElement<Constructor<?>>, Constructor<?>> getConstructorResolver() {
            return CommonResolvers.CONSTRUCTOR_RESOLVER;
        }

        @Override
        public Function<IMetaElement<Class<?>>, Class<?>> getClassResolver() {
            return CommonResolvers.CLASS_RESOLVER;
        }

        @Override
        public Function<IMetaElement<Enum<?>>, Enum<?>> getEnumResolver() {
            return CommonResolvers.ENUM_RESOLVER;
        }

        @Override
        public Function<IMetaElement<Field>, Field> getFieldResolver() {
            return CommonResolvers.FIELD_RESOLVER;
        }

        @Override
        public Function<IMetaElement<Method>, Method> getMethodResolver() {
            return CommonResolvers.METHOD_RESOLVER;
        }
    }
}
