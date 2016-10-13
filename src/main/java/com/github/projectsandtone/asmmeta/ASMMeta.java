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
package com.github.projectsandtone.asmmeta;

import com.github.jonathanxd.iutils.description.DescriptionUtil;
import com.github.projectsandtone.asmmeta.asm.ASMMetaClassVisitor;
import com.github.projectsandtone.asmmeta.element.IClassElement;
import com.github.projectsandtone.asmmeta.element.IMetaElement;
import com.github.projectsandtone.asmmeta.element.MetaElement;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ASMMeta {

    private final Map<String, IMetaElement<?>> index = new HashMap<>();
    private final Map<String, IMetaElement<?>> unmodIndex = Collections.unmodifiableMap(this.index);

    public void index(byte[] classBytes) {
        IClassElement read = this.read(classBytes);

        String name = read.getName();

        String s = DescriptionUtil.internalToName(name);

        this.index.put(s, read);
    }

    public IClassElement read(byte[] bytes) {
        ASMMetaClassVisitor asmMetaClassVisitor = VisitorFactory.createVisitor(Opcodes.ASM5);

        ClassReader cr = new ClassReader(bytes);

        cr.accept(asmMetaClassVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        MetaElement<?> current = asmMetaClassVisitor.getCurrent();

        if (!(current instanceof IClassElement)) {
            throw new IllegalStateException("Byte array is not a class!");
        }

        return (IClassElement) current;
    }


    public Optional<IMetaElement<?>> get(String name) {
        return Optional.ofNullable(this.index.get(name));
    }

    public Map<String, IMetaElement<?>> getAllIndexexElements() {
        return this.unmodIndex;
    }
}
