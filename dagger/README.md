# Dagger extension for Boring YURI

An extension for `Boring Yuri` to provide generated `Uri` factories as injectable [Dagger][1]
dependencies.

To be able to inject the generated `Uri` factories you need to add a generated module in the
application [Component][2] like in the example:

```java
@Singleton
@Component(modules = {
        ...
        BoringYuriModule.class
})
interface AppComponent extends AndroidInjector<MyApplication> {

    @Component.Factory
    abstract class Factory implements AndroidInjector.Factory<MyApplication> {
    }

}
```

When you have the generated `dagger` module included in your application component, you may enjoy
your injectable `Uri` factories:

```java
public class MyActivity extends AppCompatActivity {

    @Inject
    ContactUriFactory uriFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        ...

        Uri contactUri = uriFactory.buildContactUri(42);
        ...
    }
}
```

## Sample

The detailed example on how to use `dagger` extension for `Boring Yuri` can be found in
[sample-dagger](../dagger-sample).

## Installation

To add `dagger` extension for `Boring Yuri`, include the following in your app module
`build.gradle.kts` (or `build.gradle`) file:

With Kotlin KSP:

```kotlin
dependencies {
        ...
  ksp("com.github.anton-novikau:boringyuri-dagger-ksp:2.0.2")
}
```

With Kotlin KAPT:

```kotlin
dependencies {
        ...
  kapt("com.github.anton-novikau:boringyuri-dagger:2.0.2")
}
```

With Java only:

```groovy
dependencies {
         ...
  annotationProcessor "com.github.anton-novikau:boringyuri-dagger:2.0.2"
}
```

## Configuration
Dagger extension annotation processor for `Boring Yuri` supports the following configuration
option:

 * `boringyuri.dagger.module` – if the option is not set, the default name of the `dagger`
   generated module is `boringyuri.dagger.BoringYuriModule`. But if you prefer to have the module
   in your own package or would like to give it a different name, you may use this option and
   provide a new fully qualified name that works for you better.

To enable the option above you need to include the following in your app module
`build.gradle.kts` (or `build.gradle`) file:

With Kotlin KAPT:

```kotlin
kapt {
    arguments {
        arg("boringyuri.dagger.module", "your.company.domain.BoringYuriModule")
    }
}
```

With Java only:

```groovy
android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ["boringyuri.dagger.module": "your.company.domain.BoringYuriModule"]
            }
        }
    }
}
```

[1]: https://github.com/google/dagger/
[2]: https://github.com/google/dagger/blob/master/java/dagger/Component.java