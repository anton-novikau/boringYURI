## Boring YURI 1.0.0 (2020-04-20)

* Build [Uri][1] from a builder method parameters.
* Generate data classes for a specific `Uri` builder methods to deserialize `Uri` path segments
  and query parameters into object representation.
* Generate independent data classes that are not associated with a specific `Uri` builder method
  by an interface declaration.
* Add `TypeAdapter` to serialize/deserialize non-standard types.
* Add annotations for constant query parameters.

[1]: https://developer.android.com/reference/android/net/Uri
