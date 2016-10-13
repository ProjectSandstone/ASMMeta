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
package com.github.projectsandstone.asmmeta.element;

import com.github.projectsandstone.asmmeta.value.Value;

import java.lang.annotation.Annotation;
import java.util.List;

public interface IAnnotationElement extends IMetaElement<Annotation> {


    /**
     * Returns a list of values, whose each element type must be:
     *
     * - KeyedValue - Represents all values that have a Key (all KeyedValues in returned list
     * contains a String key).
     *
     * - Value - Represents all value that have no keys, only the value (Only arrays has no 'key').
     *
     * And the {@link Value#getValue()} type must be:
     *
     * - IMetaElement - (Another {@link IAnnotationElement annotation}, an {@link IEnumElement
     * enum} or {@link IClassElement class}).
     *
     * - Primitive types or String - A boxed version of primitive type or a String.
     *
     * - Another List of {@link Value} - another list of values, denoting an array.
     */
    List<Value<?>> getValues();
}
