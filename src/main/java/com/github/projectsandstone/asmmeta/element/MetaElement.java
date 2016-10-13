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

import com.github.projectsandstone.asmmeta.data.MetaData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class MetaElement<T> implements IMetaElement<T> {

    private final IMetaElement<?> enclosing;
    private final int modifiers;
    private final String name;
    private final String desc;
    private final MetaData metaData;
    private final Function<IMetaElement<T>, T> resolver;
    private List<IMetaElement<?>> child = new ArrayList<>();
    private T cached = null;

    public MetaElement(IMetaElement<?> enclosing,
                       int modifiers,
                       String name,
                       String desc,
                       MetaData metaData,
                       Function<IMetaElement<T>, T> resolver) {

        this.enclosing = enclosing;
        this.modifiers = modifiers;
        this.name = name;
        this.desc = desc;
        this.metaData = metaData;
        this.resolver = resolver;
    }

    @Override
    public IMetaElement<?> getEnclosingElement() {
        return this.enclosing;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MetaData getMetaData() {
        return this.metaData;
    }

    @Override
    public T resolve() {
        return this.resolver.apply(this);
    }

    @Override
    public T getResolvedInstance() {
        if(this.cached == null)
            this.cached = this.resolve();

        return this.cached;
    }

    @Override
    public List<IMetaElement<?>> getChildElements() {
        return this.child;
    }

    public void immutate() {
        this.getMetaData().immutate();
        this.child = Collections.unmodifiableList(this.getChildElements());
    }

    @Override
    public String toString() {

        return String.format("%s[%s]", this.getClass().getSimpleName(), ""
                + "enclosing=" + (this.getEnclosingElement() == null
                ? "null"
                : this.getEnclosingElement().getClass().getSimpleName() + "[name=" + this.getEnclosingElement().getName() + "]") + ", "
                + "modifiers=" + this.getModifiers() + ", "
                + "name=" + this.getName() + ", "
                + "desc=" + this.getDesc() + ", "
                + "metadata=" + this.getMetaData() + ", "
                + "childs=" + this.getChildElements());
    }

}
