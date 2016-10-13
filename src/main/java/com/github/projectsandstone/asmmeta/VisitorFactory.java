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
package com.github.projectsandstone.asmmeta;

import com.github.projectsandstone.asmmeta.resolver.Resolvers;
import com.github.projectsandstone.asmmeta.asm.ASMMetaClassVisitor;

import org.objectweb.asm.ClassVisitor;

public final class VisitorFactory {
    public static ASMMetaClassVisitor createVisitor(int api) {
        return new ASMMetaClassVisitor(api);
    }

    public static ASMMetaClassVisitor createVisitor(int api, ClassVisitor parent) {
        return new ASMMetaClassVisitor(api, parent);
    }

    public static ASMMetaClassVisitor createVisitor(int api, ClassVisitor parent, Resolvers resolvers) {
        return new ASMMetaClassVisitor(api, parent, resolvers);
    }
}
