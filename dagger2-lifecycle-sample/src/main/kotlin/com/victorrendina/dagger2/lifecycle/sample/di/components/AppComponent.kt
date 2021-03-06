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

package com.victorrendina.dagger2.lifecycle.sample.di.components

import com.victorrendina.dagger2.lifecycle.DaggerLifecycleComponent
import com.victorrendina.dagger2.lifecycle.sample.SampleApplication
import com.victorrendina.dagger2.lifecycle.sample.di.AppModule
import com.victorrendina.dagger2.lifecycle.sample.di.lifecycle.AppLifecycleManager
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class
])
interface AppComponent : DaggerLifecycleComponent<AppLifecycleManager> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: SampleApplication): Builder

        fun build(): AppComponent
    }
}