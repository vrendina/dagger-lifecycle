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

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.victorrendina.dagger2.lifecycle.DaggerLifecycle
import dagger.Component
import dagger.Subcomponent
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class DaggerLifecycleProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var filer: Filer

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes() = setOf(
            DaggerLifecycle::class.java.canonicalName,
            Component::class.java.canonicalName,
            Subcomponent::class.java.canonicalName
    )

    override fun init(env: ProcessingEnvironment) {
        super.init(env)
        messager = env.messager
        filer = env.filer
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val lifecycleComponents = roundEnv.getComponents()
        val lifecycleDependencies = roundEnv.getDependencies()

        val scopeToComponent = processComponents(lifecycleComponents)
        val componentToDependencies = HashMap<ClassName, ArrayList<LifecycleDependency>>()

        // Create an empty list of dependencies for each component so the files are always generated
        scopeToComponent.forEach { (_, component) ->
            componentToDependencies[component] = ArrayList()
        }

        lifecycleDependencies.forEach { element ->
            val scope = element.getScope()
            if (scope == null) {
                error("Missing scope annotation on ${element.simpleName}", element)
                return false
            }

            val component = scopeToComponent[scope]
            if (component == null) {
                error("Missing component for ${scope.simpleName()}. Does your ${scope.simpleName()} component implement DaggerLifecycleComponent?", element)
                return false
            } else {
                val isAutoStart = element.hasInterface(AUTO_START)
                val isAutoDispose = element.hasInterface(AUTO_DISPOSE)

                // Must contain at least one interface for AutoStart or AutoDispose
                if (!isAutoStart && !isAutoDispose) {
                    error("Missing interface(s) on ${element.simpleName}. Must use AutoStart and/or AutoDispose.", element)
                    return false
                }
                componentToDependencies.append(component, LifecycleDependency(scope, element, isAutoStart, isAutoDispose))
            }
        }

        componentToDependencies
                .map { LifecycleComponent(it.key, it.value) }
                .forEach { it.brewJava().writeTo(filer) }

        return false
    }

    private fun processComponents(components: List<TypeElement>): Map<ClassName, ClassName> {
        val scopeToComponent = HashMap<ClassName, ClassName>()
        components.forEach { component ->
            val scope = component.getScope()
            if (scope == null) {
                error("Missing scope annotation on ${component.simpleName}", component)
                return emptyMap()
            } else {
                if (!scopeToComponent.containsKey(scope)) {
                    scopeToComponent[scope] = ClassName.get(component)
                } else {
                    error("Multiple components annotated with scope ${scope.simpleName()}", component)
                    return emptyMap()
                }
            }
        }
        return scopeToComponent
    }

    private fun error(message: String, element: Element? = null) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element)
    }
}
