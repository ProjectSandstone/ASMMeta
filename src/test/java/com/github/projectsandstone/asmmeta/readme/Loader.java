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
package com.github.projectsandstone.asmmeta.readme;

import com.github.jonathanxd.iutils.reflection.Invokable;
import com.github.jonathanxd.iutils.reflection.Invokables;
import com.github.jonathanxd.iutils.reflection.Links;
import com.github.projectsandstone.asmmeta.ASMMeta;
import com.github.projectsandstone.asmmeta.element.MethodElement;
import com.github.projectsandstone.asmmeta.element.FieldElement;
import com.github.projectsandstone.asmmeta.element.IAnnotationElement;
import com.github.projectsandstone.asmmeta.element.IMetaElement;
import com.github.projectsandstone.asmmeta.util.MetaElementUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Loader {

    private final ASMMeta asmMeta = new ASMMeta();

    public void load(byte[] classBytes) {
        this.asmMeta.index(classBytes);
    }

    public InjectionPoint[] getInjectionPoints() {
        return this.asmMeta.getAllIndexexElements()
                .values().stream()
                .flatMap(iMetaElement -> Arrays.stream(this.getInjectionPoints(iMetaElement)))
                .toArray(InjectionPoint[]::new);
    }

    public InjectionPoint[] getInjectionPoints(String className) {
        Optional<IMetaElement<?>> iMetaElementOpt = this.asmMeta.get(className);

        return iMetaElementOpt.map(this::getInjectionPoints).orElse(new InjectionPoint[0]);
    }

    public InjectionPoint[] getInjectionPoints(IMetaElement<?> metaElement) {
        List<InjectionPoint> injectionPointList = new ArrayList<>();

        for (IMetaElement<?> iMetaElement : metaElement.getChildElements()) {

            if (iMetaElement instanceof MethodElement || iMetaElement instanceof FieldElement) {
                IAnnotationElement[] annotations = MetaElementUtils.getAnnotations(iMetaElement, Inject.class);

                if (annotations.length > 0) {
                    Invokable<?> invokable;
                    Class<?> type;

                    if (iMetaElement instanceof MethodElement) {
                        Method resolve = ((MethodElement) iMetaElement).getResolvedInstance();
                        resolve.setAccessible(true);

                        type = resolve.getParameterTypes()[0];

                        invokable = Invokables.fromMethod(resolve);
                    } else {
                        Field resolve = ((FieldElement) iMetaElement).getResolvedInstance();

                        resolve.setAccessible(true);

                        type = resolve.getType();

                        invokable = Invokables.fromFieldSetter(resolve);
                    }

                    if(!type.equals(String.class)) {
                        throw new IllegalArgumentException("Expected resolved element type: String, found: "+type+"!");
                    }

                    injectionPointList.add(new InjectionPoint(Links.ofInvokable(invokable), iMetaElement));
                }
            }
        }

        return injectionPointList.stream().toArray(InjectionPoint[]::new);
    }
}
