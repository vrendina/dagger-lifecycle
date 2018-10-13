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
import javax.lang.model.element.TypeElement

data class LifecycleDependency(
    val scope: ClassName,
    val element: TypeElement,
    val autoStart: Boolean,
    val autoDispose: Boolean
) {

    override fun toString() = "${element.simpleName} in ${scope.simpleName()} scope (AutoStart: $autoStart) (AutoDispose: $autoDispose)"
}
