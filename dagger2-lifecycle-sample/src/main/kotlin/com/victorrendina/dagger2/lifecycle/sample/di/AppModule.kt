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

import android.content.Context
import android.content.res.Resources
import com.victorrendina.dagger2.lifecycle.sample.SampleApplication
import com.victorrendina.dagger2.lifecycle.sample.di.components.ScopeOneComponent
import com.victorrendina.dagger2.lifecycle.sample.di.components.ScopeTwoComponent
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(subcomponents = [
    ScopeOneComponent::class,
    ScopeTwoComponent::class
])
class AppModule {

    @Provides
    fun provideContext(application: SampleApplication): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideResources(application: SampleApplication): Resources = application.resources
}