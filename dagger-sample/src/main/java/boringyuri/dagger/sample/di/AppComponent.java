/*
 * Copyright 2020 Anton Novikau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boringyuri.dagger.sample.di;

import javax.inject.Singleton;

import boringyuri.dagger.sample.App;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

// Dagger component that includes a generated BoringYuriModule can not be
// a kotlin interface because kapt generates java stubs for dagger annotation
// processor and the generated module that is added to @Component annotation
// has not created yet.
// So to get rid of the java stubs generation step we have to make the Component
// a java interface. In this case Boring Yuri's dagger processor will be able
// to produce the module file before the Component is compiled.
@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        BoringYuriModule.class
})
public interface AppComponent extends AndroidInjector<App> {

    @Component.Factory
    abstract class Factory implements AndroidInjector.Factory<App> {
    }

}
