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
import com.victorrendina.dagger2.lifecycle.AutoStart
import com.victorrendina.dagger2.lifecycle.DaggerLifecycle
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sample dependency in the [Singleton] scope that will be started when the application component
 * is initialized. The application component is never destroyed and therefore this dependency only
 * implements [AutoStart].
 */
@Singleton
@DaggerLifecycle
class SingletonDep @Inject constructor() : AutoStart {
    override fun start() {
        Log.d(tag, "Automatically started $tag")
    }

    private val tag = this::class.java.simpleName
}