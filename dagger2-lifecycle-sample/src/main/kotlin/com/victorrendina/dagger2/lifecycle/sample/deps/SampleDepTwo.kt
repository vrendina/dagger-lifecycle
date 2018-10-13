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

package com.victorrendina.dagger2.lifecycle.sample.deps

import android.util.Log
import com.victorrendina.dagger2.lifecycle.AutoDispose
import com.victorrendina.dagger2.lifecycle.AutoStart
import com.victorrendina.dagger2.lifecycle.DaggerLifecycle
import com.victorrendina.dagger2.lifecycle.sample.di.scopes.ScopeTwo
import javax.inject.Inject

/**
 * Sample dependency in the [ScopeTwo] scope that will be started when the
 * [com.victorrendina.dagger2.lifecycle.sample.di.components.ScopeTwoComponent] is initialized and
 * automatically disposed of when the component is disposed.
 */
@ScopeTwo
@DaggerLifecycle
class SampleDepTwo @Inject constructor() : AutoStart, AutoDispose {
    override fun start() {
        Log.d(tag, "Automatically started $tag")
    }

    override fun dispose() {
        Log.d(tag, "Automatically disposed $tag")
    }

    private val tag = this::class.java.simpleName
}