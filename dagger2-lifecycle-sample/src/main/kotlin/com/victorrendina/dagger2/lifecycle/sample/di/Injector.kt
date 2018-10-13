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

package com.victorrendina.dagger2.lifecycle.sample.di

import com.squareup.leakcanary.LeakCanary
import com.victorrendina.dagger2.lifecycle.DaggerLifecycleManager
import com.victorrendina.dagger2.lifecycle.sample.SampleApplication
import com.victorrendina.dagger2.lifecycle.sample.di.components.DaggerAppComponent
import com.victorrendina.dagger2.lifecycle.sample.di.lifecycle.AppLifecycleManager
import com.victorrendina.dagger2.lifecycle.sample.di.lifecycle.ScopeOneLifecycleManager
import com.victorrendina.dagger2.lifecycle.sample.di.lifecycle.ScopeTwoLifecycleManager
import com.victorrendina.dagger2.lifecycle.sample.di.scopes.ScopeOne
import com.victorrendina.dagger2.lifecycle.sample.di.scopes.ScopeTwo

object Injector {

    private lateinit var appManager: AppLifecycleManager
    private var scopeOneManager: ScopeOneLifecycleManager? = null
    private var scopeTwoManager: ScopeTwoLifecycleManager? = null

    /**
     * Initialize the [AppComponent] and create the [AppLifecycleManager]. Any dependencies that implement
     * [com.victorrendina.dagger2.lifecycle.AutoStart] will be started.
     */
    fun initializeAppComponent(application: SampleApplication) {
        if (!::appManager.isInitialized) {
            val component = DaggerAppComponent.builder()
                    .application(application)
                    .build()
            appManager = AppLifecycleManager(component).also { startDependencies(it) }
        }
    }

    /**
     * Initialize a sub-component scope and start any [com.victorrendina.dagger2.lifecycle.AutoStart]
     * dependencies.
     * @param scope class of scope annotation
     */
    fun initializeScope(scope: Class<*>) {
        destroyScope(scope)
        when (scope) {
            ScopeOne::class.java -> {
                scopeOneManager = ScopeOneLifecycleManager(appManager.oneBuilder.build())
                        .also { startDependencies(it) }
            }
            ScopeTwo::class.java -> {
                scopeTwoManager = ScopeTwoLifecycleManager(appManager.twoBuilder.build())
                        .also { startDependencies(it) }
            }
        }
    }

    /**
     * Destroy a sub-component scope and dispose of any [com.victorrendina.dagger2.lifecycle.AutoStart]
     * dependencies.
     * @param scope class of scope annotation
     */
    fun destroyScope(scope: Class<*>) {
        when (scope) {
            ScopeOne::class.java -> {
                scopeOneManager?.also { destroyDependencies(it) }
                scopeOneManager = null
            }
            ScopeTwo::class.java -> {
                scopeTwoManager?.also { destroyDependencies(it) }
                scopeTwoManager = null
            }
        }
    }

    /**
     * Wrapper around the [DaggerLifecycleManager.start] method.
     */
    private fun startDependencies(manager: DaggerLifecycleManager) {
        manager.start()
    }

    /**
     * Wrapper around the [DaggerLifecycleManager.dispose] method. A block is provided that executes
     * after each dependency is disposed and watches the dependency for memory leaks.
     */
    private fun destroyDependencies(manager: DaggerLifecycleManager) {
        manager.dispose {
            LeakCanary.installedRefWatcher().watch(it)
        }
        LeakCanary.installedRefWatcher().watch(manager)
    }
}