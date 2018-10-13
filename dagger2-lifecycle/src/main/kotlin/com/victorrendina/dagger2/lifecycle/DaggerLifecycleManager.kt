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

package com.victorrendina.dagger2.lifecycle

/**
 * Contains convenience methods to call the [AutoStart.start] and [AutoDispose.dispose] methods on all the dependencies
 * in a [DaggerLifecycleDependencyProvider]. It is not required to use the [DaggerLifecycleManager].
 */
abstract class DaggerLifecycleManager {

    /**
     * Return an instance of the generated [DaggerLifecycleDependencyProvider] that contains
     * the dependencies for the component scope under management by this lifecycle manager. This
     * will typically be injected into your implementation of the [DaggerLifecycleManager].
     */
    abstract fun getProvider(): DaggerLifecycleDependencyProvider

    /**
     * Start any [AutoStart] dependencies managed by this class.
     */
    fun start(postAction: ((AutoStart) -> Unit)? = null) {
        getProvider().getAutoStart().forEach {
            it.get().apply {
                start()
                postAction?.invoke(this)
            }
        }
    }

    /**
     * Dispose of any [AutoDispose] dependencies managed by this class.
     */
    fun dispose(postAction: ((AutoDispose) -> Unit)? = null) {
        getProvider().getAutoDispose().forEach {
            it.get().apply {
                dispose()
                postAction?.invoke(this)
            }
        }
    }
}