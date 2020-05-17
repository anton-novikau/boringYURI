## Boring YURI 1.1.1 (2020-05-18)

* Add `@DefaultValue` support for query parameters and path segments. 

## Boring YURI 1.1.0 (2020-05-03)

* Add support of the named path segments in `Uri` factories and `Uri` data classes.
* Improve validation and compilation error/warning messages.
* Use `TypeAdapterFactory` with instance cache for `BoringTypeAdapter`s.
* Add [Dagger][2] support to `Uri` factory classes.
* Add annotation processor options to configure `Boring Yuri`'s processors. 

## Boring YURI 1.0.0 (2020-04-20)

* Build [Uri][1] from a builder method parameters.
* Generate data classes for a specific `Uri` builder methods to deserialize `Uri` path segments
  and query parameters into object representation.
* Generate independent data classes that are not associated with a specific `Uri` builder method
  by an interface declaration.
* Add `TypeAdapter` to serialize/deserialize non-standard types.
* Add annotations for constant query parameters.

[1]: https://developer.android.com/reference/android/net/Uri
[2]: https://github.com/google/dagger/
