# Boring YURI

`Boring Yuri` (pronounced as `'bɔːrɪŋ júrij`) is a humble guy who knows how to do his boring job.
And his job is to deal with the [Uri][1] routine. This job has a little fun, but someone has
to do it. So `Boring Yuri` is at your service.

This is a source code generation library that provides a simple annotation-based API to convert
your objects into Android `Uri` or to restore the objects back from the `Uri`. The API itself was
inspired by popular annotation-based libraries [Retrofit][2] and [Gson][3], but, unlike them,
`Boring Yuri` does all the job on the compile time and the purpose of the library has a little
in common with them.

## Usage
### Basic example

Here is a simple HTTP URL to build:

```
https://example.com/user?name="John%20Doe"&phone_number="%2B15417543010"
```
Usually you have to create a `Uri.Builder` and provide URL scheme, authority, path,
query parameter names and the values.

```java
Uri uri = new Uri.Builder()
        .scheme("https")
        .authority("example.com")
        .encodedPath("/user")
        .appendQueryParameter("name", "John Doe")
        .appendQueryParameter("phone_number", "+15417543010")
        .build();
```

There is nothing complicated at first sight, but when there comes more URIs to build with many
more different paths and query parameters, when you need to ensure the parameters you pass are not
null and that they are correctly converted into `String`, it becomes much more complicated. Instead
of dealing with `Uri` API directly it would be more comfortable to work with application types and
to have some helper that can properly convert them for you and put them into an appropriate part
of the `Uri`.

All you need is to declare an interface, annotate it with `@UriFactory`, declare a `Uri` builder
method and map the method parameters to the query parameters:

```java
@UriFactory(scheme = "https", authority="example.com")
interface UserUriBuilder {

    @NonNull
    @UriBuilder("/user")
    Uri buildUserUri(
            @Param("name") String name,
            @Param("phone_number") String phoneNumber);

}
```
Which generates:

```java
class UserUriBuilderImpl implements UserUriBuilder {

    @NonNull
    public Uri buildUserUri(String name, String phoneNumber) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("example.com");

        builder.appendEncodedPath("user");
        if (name != null) {
            builder.appendQueryParameter("name", name);
        }
        if (phoneNumber != null) {
            builder.appendQueryParameter("phone_number", phoneNumber);
        }

        return builder.build();
    }

}
```
so calling the following:
```java
UserUriBuilder builder = new UserUriBuilderImpl();

builder.buildUserUri("John Doe", "+15417543010");
```
gives you `https://example.com/user?name="John%20Doe&phone_number=%2B15417543010"`

### Variable path segment

When a URI needs a variable path segment (or several variable segments) there is `@Path` comes
to your rescue. To build a URI that fetches a user details by the `id` set as the last path
segment:

```
https://example.com/user/42
```

You need to annotate the appropriate method parameter with `@Path`:

```java
@UriFactory(scheme = "https", authority="example.com")
interface UserUriBuilder {

    @NonNull
    @UriBuilder("/user/{id}")
    Uri buildUserUri(@Path("id") int userId);

}
```

Which generates:

```java
class UserUriBuilderImpl implements UserUriBuilder {
    @NonNull
    public Uri buildUserUri(String name, String phoneNumber) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("example.com");

        builder.appendEncodedPath("user");
        builder.appendPath(String.valueOf(userId));

        return builder.build();
    }
}
```
`@Path` can be used with as many method parameters as you need. You should just provide a path
segment name in the `@UriBuilder` base path. The segment name can be specified either in a
 `value()` parameter of `@Path` or a method parameter name can be used. The order of the path
method parameters doesn't matter so you're free do define them as you like:

```java
    @UriBuilder("/user/{group}/{id}")
    Uri buildUserUri(@Path("id") int userId, @Path String group);
```

In the example above `id` is explicitly named path segment, `group` is implicitly named
(i.e. method parameter name is used) and the order of the method parameters differs from the order
of the segments in the base path. So calling `builder.buildUserUri(42, "students")` will give you
`https://example.com/user/students/42`.

**IMPORTANT:** if you use code obfuscation it is highly recommended to specify the segment name in
`@Path` or `@Param` annotations explicitly or add the files that contain these annotation in
ProGuard (or R8) keep list. Otherwise, when the obfuscator change the method parameter names,
you'll have an unexpected result.

Path segment name is not obligatory, but you should preserve the order as it's the order of
the path method parameter as they appear in the `Uri` in exactly the same order as defined in
the method signature, i.e. for the builder method:

```java
    @UriBuilder("/user")
    Uri buildUserUri(@Path String group, @Path int userId);
```

Calling `builder.buildUserUri("students", 42)` will give you
`https://example.com/user/students/42`.
 
But calling the builder with the parameters switched:

```java
    @UriBuilder("/user")
    Uri buildUserUri(@Path int userId, @Path String group);
```

Will give you `https://example.com/user/42/students`.

**NOTE:** relying on the method parameters order is highly not recommended as it gives less
flexibility and there is a bigger chance to make a mistake. Use the named path segments instead.

**IMPORTANT:** `@Path` method parameters **can not be nullable**. They must be explicitly
annotated with `@NonNull` in Java or must have a non-nullable type in Kotlin. The only exception
when a `@Path` method parameter can be nullable is when it has a [@DefaultValue](#default-values).
This technically makes the path segment itself non-nullable, but allows to have a nullable
parameter in a method signature.

### Types

`Boring Yuri` knows how to convert primitives, primitive wrappers, `String` and `Uri` to a path
segment or a query parameter. But sometimes it's not enough and we have to deal with application
specific, platform or library types.

#### Arrays

It is allowed to have `@Param` method parameters of an array type. The `Uri` will contain query
parameters of the provided name for every `non-null` array element:

```java
@UriFactory(scheme = "https", authority="example.com")
interface UserUriBuilder {

    @UriBuilder("user")
    Uri buildFetchUserDetailsUri(@Param("id") int[] ids);

}
```

So calling `foo.buildFetchUserDetailsUri(new int[] { 42, 24 })` will build
`https://example.com/user?id=42&id=24`.

**NOTE:** the array rule is not applicable for query parameters of `List`, `Set` or any other
`Collection` and a [custom type conversion](#platform-or-library-specific-types) must be defined.

**NOTE:** unlike `@Param` method parameters `@Path` of an array type are supposed to have
a [custom type conversion](#platform-or-library-specific-types) defined.

#### Application specific types

If you want to use the same application specific type conversion for every `Uri`, you need to 
annotate the class with `@TypeAdapter` specifying a `BoringTypeAdapter` implementation as an
annotation value so `Boring Yuri` can understand how to convert the object of this type into a
`String` that is expected on the receiver's side.

```java
@TypeAdapter(UserTypeAdapter.class)
class User {

    private final int id;

    private final String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
```

Let's say we need to pass `User`'s `id` and `name` as a single query parameter joined with
a semicolon:
  
```java
class UserTypeAdapter implements BoringTypeAdapter<User> {

    @NonNull
    @Override
    public String serialize(@NonNull User user) {
        return user.getId() + ";" + user.getName();
    }

    @NonNull
    @Override
    public User deserialize(@NonNull String serialized) {
        String[] userData = serialized.split(";");

        return new User(Integer.parseInt(userData[0]), userData[1]);
    }

}
```

So so now we're safe to use `User` in a method parameter of a `Uri` builder method:

```java
@UriFactory(scheme = "https", authority="example.com")
interface UserUriBuilder {

    @NonNull
    @UriBuilder("/user")
    Uri buildUserUri(@Param("data") User user);

}
```

And calling `builder.buildUserUri(new User(42, "John Doe))` will give you
`https://example.com/user?data="42%3BJohn%20Doe"`, where `%3B` is a url encoded semicolon `;`.

#### Platform or Library specific types

When you're not able to annotate a custom class with `@TypeAdapter` because it does not belong
to your codebase, it is possible to specify the adapter at use:

```java
@UriFactory(scheme = "https", authority = "maps.example.com")
interface LocationUriBuilder {
    
    @UriBuilder("/maps/api/geocode")
    Uri buildGeocodeUri(
            @Param("latlng")
            @TypeAdapter(CoordinatesTypeAdapter.class) Pair<Double, Double> coordinates);

}
```
So if the type adapter is:
```java
class CoordinatesTypeAdapter implements BoringTypeAdapter<Pair<Double, Double>> {

    @NonNull
    @Override
    public String serialize(@NonNull Pair<Double, Double> coordinates) {
        return coordinates.first + "," + coordinates.second;
    }

    @NonNull
    @Override
    public Pair<Double, Double> deserialize(@NonNull String serialized) {
        String[] coordinates = serialized.split(",");

        return Pair.create(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
    }

}
```

Calling `builder.buildGeocodeUri(Pair.create(53.893009, 27.567444))` will give you
`https://maps.example.com/maps/api/geocode?latlng=53.893009,27.567444`

**NOTE:** specifying `@TypeAdapter` at use will override the standard type conversion (eg. if you
need a different decimal format for a number). 

If a **query parameter** is an array of any custom type (application, library or platform specific),
there must be provided a `@TypeAdapter` for the array component type, not to the array itself:

```java
@UriFactory(scheme = "https", authority = "maps.example.com")
interface LocationUriBuilder {
    
    @UriBuilder("/maps/api/geocode")
    Uri buildGeocodeUri(
            @Param("latlng")
            @TypeAdapter(CoordinatesTypeAdapter.class) Pair<Double, Double>[] coordinates);

}
```

In the example above `CoordinatesTypeAdapter` defines the type conversion rules for
`Pair<Double, Double>`, not to an array of `Pair`s.

For **path segments** though, a `@TypeAdapter` must be defined for the array itself, not just for
the array component type. 

### Constant query parameters

When the parameter value doesn't changes from one build to another, but it is expected to be set on
the receiver's side, `Boring Yuri` has a bunch of annotations for constant query parameters. For
example we need to tell the REST service if the client has a geolocation sensor and to provide a
zoom level of the requested static map:

```java
@UriFactory(scheme = "https", authority = "maps.example.com")
interface LocationUriBuilder {
    
    @UriBuilder("/maps/api/staticmap")
    @BooleanParam(name = "sensor", value = true)
    @DoubleParam(name = "zoom", value = 2.5)
    Uri buildStaticMapUri(@Param("lat") double latitude, @Param("lng") double longitude);

}
```

So calling: 

```java
builder.buildStaticMapUri(53.893009, 27.567444);
builder.buildStaticMapUri(37.773972, -122.431297);
```

will give two `Uri`'s accordingly:

```
https://maps.example.com/maps/api/staticmap?lat=53.893009&lng=27.567444&sensor=true&zoom=2.5
https://maps.example.com/maps/api/staticmap?lat=37.773972&lng=-122.431297&sensor=true&zoom=2.5
```

**NOTE:** one `Uri` builder method may have many constant query parameters of the same type.

It is allowed to define two or more constant query parameters that have the same name if they are
of the same type:

```java
@UriFactory(scheme = "https", authority = "example.com")
interface LocationUriBuilder {
    
    @UriBuilder("/search/api/media")
    @StringParam(name = "type", value = "photo")
    @StringParam(name = "type", value = "video")
    Uri buildMediaSearchUri(@Param("query") String searchQuery);

}
```

So calling: 

```java
builder.buildMediaSearchUri("Cute Cats");
builder.buildMediaSearchUri("Yawning Sloths");
```

will give `Uri`s for searching both `photo` and `video` by two different search queries:

```
https://example.com/search/api/media?query=Cute%20Cats&type=photo&type=video
https://example.com/search/api/media?query=Yawning%20Sloths&type=photo&type=video
```

**NOTE:** constant query parameters of a builder method **can not** have the same name if they are
of different types (eg. one is a `@StringParam` and the other is a `@LongParam`).

### Deserialize data from Uri

Sometimes, working with `Uri`, you may need to restore query parameters and path segments from
the given `Uri` and convert them into appropriate types. This often happens when your application
have its own  `ContentProvider`, so it becomes a receiver of the `Uri`.

```java
@UriFactory(scheme = ContentResolver.SCHEME_CONTENT, authority = "boringyuri.sample.provider")
interface ContactUriBuilder {

    @UriBuilder("/file/photo/{group}/{contactId}")
    @WithUriData
    Uri buildContactPhotoUri(
            @Path String group,
            @Path long contactId,
            @Param("desired_dimensions") @TypeAdapter(RectTypeAdapter.class) Rect desiredDimens);

}
```

Adding `@WithUriData` to the builder method will generate a data class:

```java
class ContactPhotoUriData {

    @NonNull
    private final Uri dataUri;

    private String group;

    private long contactId;

    private Rect desiredDimens;

    public ContactPhotoUriData(@NonNull Uri uri) {
        this.dataUri = uri;
    }

    public String getGroup() {
        // obtains 'group' from dataUri.getPathSegments().get(2)
        ... 

        return group;
    }
    
    public long getContactId() {
        // obtains 'contactId' from dataUri.getPathSegments().get(3) and converts String to long
        ...

        return contactId;
    }

    public Rect getDesiredDimens() {
        // obtains 'desiredDimens' from dataUri.getQueryParameter("desired_dimensions")
        // and uses RectTypeAdapter#deserialize() to create a Rect instance from String
        ...

        return desiredDimens;
    }

}
```

So in your `ContentProvider` you can just create an instance of `ContactPhotoUriData` and work with
the data it provides as if it is already parsed for you into an object of the appropriate type.

```java
class BoringContactProvider extends ContentProvider {

    ...
    
    @Nullable
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) {
        ContactPhotoUriData data = new ContactPhotoUriData(uri);

        String group = data.getGroup();
        long contactId = data.getContactId();
        Rect desiredDimensions = data.getDesiredDimens();
        
        // here you may use 'group', 'contactId' and 'desiredDimensions' do create
        // a ParcelFileDescriptor for the requested contact photo.
        ...
    }

}
```

Constant query parameters defined in the builder method are provided in the generated data class
as well. But because they can't be changed, there is no actually reading them from the given `Uri`.
The getter method instead just returns a constant value defined in the specific annotation:

```java
@UriBuilder("/maps/api/staticmap")
@WithUriData
@DoubleParam(name = "zoom", value = 2.5)
Uri buildStaticMapUri(@Param("lat") double latitude, @Param("lng") double longitude);
```

For the builder method above there is a data class:

```java
class StaticMapUriData {
    ...

    public double getZoom() {
        return 2.5;
    }

}
```  

If there are two or more constant query parameters that have the same name and type, they can be
obtained from the data class as an array of this type:

```java
@UriBuilder("/search/api/media")
@WithUriData
@StringParam(name = "type", value = "photo")
@StringParam(name = "type", value = "video")
Uri buildMediaSearchUri(@Param("query") String searchQuery);
```  

So for the builder above there'll be a data class:

```java
class MediaSearchUriData {
    ...

    @NonNull
    public String[] getType() {
        return new String[] { "photo", "video" };
    }

}
```

#### Independent Uri data class

There are some cases when you may need to have a `Uri` data class that is not attached to
a specific builder, but it is, let's say, "independent". For example:

 * You want to have a data class that knows how to parse `Uri` correctly, but you don't need
a builder.
 * You have two or more builders that produce `Uri`'s of a similar data (eg. they have the same
set of query parameters) so you would like to have a data class of the same type to work with
these `Uri`'s.

If you think that an independent `Uri` data class will work better for you, then define an interface
with getters for each of the path segments or query parameters that you need to obtain from the
`Uri`. Annotate these getters with `@Path` or `@Param` as if there was a builder method for them
 and add `@UriData` to the interface:

```java
@UriData("/file/photo/{group}/{id}")
interface ContactPhotoUriData {

    @NonNull
    @Path
    String getGroup();

    @Path("id")
    long getContactId();

    @Nullable
    @Param("desired_dimensions")
    @TypeAdapter(RectTypeAdapter.class)
    Rect getDesiredDimens();

}
```

For this interface you'll have a generated implementation similar to the one for `@WithUriData`
with the only difference that it also implements `ContactPhotoUriData`.

**NOTE:** `@UriData` may have a base `Uri` path with placeholders for segments, similar to the one
in `@UriBuilder`. If the same data class is used for two or more different `Uri`s, the wildcard
`*` can be used to replace the constant path segment that varies in these `Uri`s. Example:

```java
@UriData("/user/*/{id}")
public interface UserData {
    ...
}
```
Which means that the same `UserData` implementation can be used to parse data from
`/user/friend/42` and from `/user/colleague/24`.

If the base path is empty in `@UriData`, it means the first variable path segment will be expected
to be in the very first position after the authority of the `Uri`. Example:

```java
@UriData
public interface UserData {

    @Path
    long getId();
      
}
```
    
Here the path segment `id` can be parsed from `content://com.example.provider/42` using
the `UriData` class, but can not be parsed from `content://com.example.provider/user/42`.

**NOTE:** when the base path is not defined in `@UriData`, the order of the getter methods will
be used to parse the specific segments. Relying on the method order is **highly not recommended**
as it may give an unpredictable result and it's easier to make a mistake. So named path segments
are always more preferable.

**IMPORTANT:** the cost of the "independence" is that you have to manage manually the type
consistency of the builder method parameters and the getters of the data class (if you have the
builders of course). `@WithUriData` does it for you since there is one source of truth: the builder
method in the factory interface.

### Default values

When a builder method parameter is supposed to be nullable, but you need to provide some fallback
value when it is `null`, you may use `@DefaultValue`. In this case the receiver will always have
some non-null value in a query parameter or in a variable path segment.

Example:
```java
    @UriBuilder("/user")
    Uri buildUserUri(@Nullable @Param("id") @DefaultValue("d14bee5") String userId);
```
Calling `builder.buildUserUri(null)` gives you `/user?id=d14bee5`, when calling
`builder.buildUserUri("abc")` gives you `/user?id=abc`.

The `@DefaultValue` also works for reading the data via associated or independent data class. If
a query parameter wasn't provided in the `Uri` and it's supposed to be `@NonNull`, instead of
`NullPointerException` the getter will return the specified default value. When a query parameter
is supposed to be `@Nullable` in the builder, but provided with a `@DefaultValue`, the associated
getter method becomes `@NonNull`:

```java
    @UriBuilder("/user")
    @WithUriData
    Uri buildUserUri(@Param("id") @DefaultValue("d14bee5") String userId);
```

For this builder method `UserUriData` class will be generated. And calling
`new UserUriData(Uri.parse("https://example.com/user")).getUserId()` returns `d14bee5` even if the
`Uri` doesn't have any query parameters at all. It works exactly the same in independent `Uri`
data classes.

The `@DefautlValue` can also be used when a query parameter or a path segment have a value that
is incompatible with the associated getter of a primitive (or a primitive wrapper) type.

For example you have an independent `UserData` with `id` of type `long`:
```java
@UriData("/user")
interface UserData {
    
    @Param("id")
    @DefaultValue("42")
    long getId();

} 
```

But for some reason the `Uri` you're trying to process with this data class has `id` of `String`:
```java
Uri uri = Uri.parse("https://example.com/user?id=d14bee5");
UserData data = new UserData(uri);

long id = data.getId(); // id == 42 
```
Since `d14bee5` in the `Uri` can not be parsed to `long`, the specified default value will be
returned. Of course normally this should never happen if your app is both a producer of the `Uri`
and a receiver of the `Uri`, but when you're a client of some API, it's better to have some
predictable result when you parse the data.

**NOTE:** `@DefaultValue` allows you to have a `@Nullable` path segment method parameter:

```java
    @UriBuilder("/user/{id}")
    Uri buildUserUri(@Nullable @Path("id") @DefaultValue("d14bee5") String userId);
```

So calling `builder.buildUserUri(null)` gives you `/user/d14bee5`, when calling 
`builder.buildUserUri("abc")` gives you `/user/abc`.

**IMPORTANT:** The value specified in the `@DefaultValue` must be correctly serialized for
a specific type of a method parameter or a getter method, otherwise you may have an incorrect
or unexpected result. For the types that require a
[custom type conversion](#application-specific-types) the default value must be serialized
according to the rules of the specified `TypeAdapter`.

If you have a type adapter that serializes latitude and longitude joining them with a comma: 
```java
class LocationTypeAdapter implements BoringTypeAdapter<Location> {

    @NonNull
    @Override
    public String serialize(@NonNull Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    ...
}
```
So for a builder that uses `Location` as method parameter the default value should be like
`53.893009,27.567444`.

```java
    @UriBuilder("/maps/api/staticmap")
    Uri buildStaticMapUri(@Nullable @Param @DefaultValue("53.893009,27.567444") Location location);
```

## Installation

To add `Boring Yuri` to your project, include the following in your app module `build.gradle` file:

```groovy
android {
  ...
  // Boring YURI requires Java 8.
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
}
```

With Java only:

```groovy
dependencies {
  implementation "org.boringyuri:boringyuri-api:1.1.2"
  annotationProcessor "org.boringyuri:boringyuri-processor:1.1.2"
}
```

With Kotlin:

```groovy
apply plugin: 'kotlin-kapt'

dependencies {
  implementation "org.boringyuri:boringyuri-api:1.1.2"
  kapt "org.boringyuri:boringyuri-processor:1.1.2"
}
```
Snapshots of the development version are available in [JFrog's snapshots repository][4].
Add the repo below to download `SNAPSHOT` releases.

```groovy
repositories {
  jcenter()
  maven { url 'http://oss.jfrog.org/artifactory/oss-snapshot-local/' }
}
```

## Configuration

`Boring Yuri`'s annotation processors support the following configuration options:

 * `boringyuri.type_adapter_factory` – option to specify the `BoringTypeAdapter` factory class
  (it must be a fully qualified name) and to enable instance caching for the created adapters.
  Enabling this option allows to use the memory more efficiently and to create every instance of
  the specific type adapter only once. When the option is turned off, every instance of the adapter
  is created at use which gives to garbage collector more work.
 * `boringyuri.suppress_warning.ordered_segments` – every time you use ordered path segments
  instead of named path segments, you'll see the a compilation warning message `Template
  {path name} is not found in @UriBuilder("/base/path"). Fallback to ordered segments may
  cause an unpredictable result`. This message here is to warn that the ordered path approach
  is not recommended and you'd better switch to the named path segments. But if you're aware of
  what you're doing, you may turn the message off setting the option to `true`.

To enable any of the options above you need to include the following in your app module
`build.gradle` file:

With Java only:

```groovy
android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ["option_name": "option_value"]
            }
        }
    }
}
```

With Kotlin:

```groovy
kapt {
    arguments {
        arg("option_name", "option_value")
    }
}
```

## License

```
Copyright 2020 Anton Novikau

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[1]: https://developer.android.com/reference/android/net/Uri
[2]: https://github.com/square/retrofit
[3]: https://github.com/google/gson
[4]: https://oss.jfrog.org/oss-snapshot-local/