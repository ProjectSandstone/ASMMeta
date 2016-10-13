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
package com.github.projectsandtone.asmmeta.element;

import com.github.projectsandtone.asmmeta.data.MetaData;
import com.github.projectsandtone.asmmeta.util.StringUtils;
import com.github.projectsandtone.asmmeta.value.Value;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

// TODO Annotation
public class AnnotationElement extends MetaElement<Annotation> implements IAnnotationElement {

    private List<Value<?>> values = new ArrayList<>();

    public AnnotationElement(IMetaElement<?> enclosing, int modifiers, String name, String desc, MetaData metaData, Function<IMetaElement<Annotation>, Annotation> resolver) {
        super(enclosing, modifiers, name, desc, metaData, resolver);
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public void immutate() {
        this.getMetaData().immutate();
        this.values = Collections.unmodifiableList(this.values);
    }

    @Override
    public String toString() {
        String str = super.toString();

        return StringUtils.insertAt(str, str.length()-1, ", values="+values);
    }
}
