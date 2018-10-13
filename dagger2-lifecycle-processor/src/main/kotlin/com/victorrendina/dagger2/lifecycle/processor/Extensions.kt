/*
 * Copyright (c) 2018 Victor Rendina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.victorrendina.dagger2.lifecycle.processor

import com.squareup.javapoet.ClassName
import com.victorrendina.dagger2.lifecycle.AutoDispose
import com.victorrendina.dagger2.lifecycle.AutoStart
import com.victorrendina.dagger2.lifecycle.DaggerLifecycle
import com.victorrendina.dagger2.lifecycle.DaggerLifecycleComponent
import dagger.Component
import dagger.Subcomponent
import javax.annotation.processing.RoundEnvironment
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Scope
import javax.lang.model.AnnotatedConstruct
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

val AUTO_START: ClassName = ClassName.get(AutoStart::class.java)
val AUTO_DISPOSE: ClassName = ClassName.get(AutoDispose::class.java)
val LIFECYCLE_COMPONENT: ClassName = ClassName.get(DaggerLifecycleComponent::class.java)
val JAVAX_PROVIDER: ClassName = ClassName.get(Provider::class.java)
val JAVAX_INJECT: ClassName = ClassName.get(Inject::class.java)

fun AnnotatedConstruct.getScope() = annotationMirrors
        .map { it.annotationType.asElement() as TypeElement }
        .filter { it.getAnnotation(Scope::class.java) != null }
        .map { ClassName.get(it) }
        .firstOrNull()

fun TypeElement.hasInterface(className: ClassName) = interfaces
        .map { (it as DeclaredType).asElement() as TypeElement }
        .map { ClassName.get(it) }
        .any { it == className }

fun RoundEnvironment.getComponents() =
        getElementsAnnotatedWith(Component::class.java, Subcomponent::class.java)
                .cast<TypeElement>()
                .filter { it.hasInterface(LIFECYCLE_COMPONENT) }

fun RoundEnvironment.getElementsAnnotatedWith(vararg annotations: Class<out Annotation>): Set<Element> {
    return annotations.fold(HashSet()) { elements, annotation ->
        elements.apply { addAll(getElementsAnnotatedWith(annotation)) }
    }
}

fun RoundEnvironment.getDependencies(): List<TypeElement> =
        getElementsAnnotatedWith(DaggerLifecycle::class.java).cast()

fun ClassName.lifecycleFileName(): ClassName = peerClass(simpleName() + "LifecycleDeps")

fun <K, T> HashMap<K, ArrayList<T>>.append(key: K, item: T) {
    var list = get(key)
    if (list == null) {
        list = ArrayList()
        put(key, list)
    }
    list.add(item)
}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> Iterable<*>.cast() = map { it as T }