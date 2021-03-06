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
package com.github.projectsandstone.asmmeta.util;

import com.github.jonathanxd.iutils.description.DescriptionUtil;
import com.github.projectsandstone.asmmeta.data.IMetaData;
import com.github.projectsandstone.asmmeta.element.IAnnotationElement;
import com.github.projectsandstone.asmmeta.element.IMetaElement;

import java.lang.annotation.Annotation;

public class MetaElementUtils {

    public static IAnnotationElement[] getAnnotations(IMetaElement<?> element, Class<? extends Annotation> annotationType) {
        IMetaData metaData = element.getMetaData();

        return metaData.getAnnotationList().stream()
                .filter(annotationMetaElement -> {
                    if (annotationMetaElement instanceof IAnnotationElement) {
                        String name = annotationMetaElement.getName();
                        String s = DescriptionUtil.internalToName(name);

                        return s.equals(annotationType.getName());
                    }

                    return false;
                })
                .map(annotationMetaElement -> (IAnnotationElement) annotationMetaElement)
                .toArray(IAnnotationElement[]::new);

    }

}
