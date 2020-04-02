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

        builder.encodedPath("/user");
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
    @UriBuilder("/user")
    Uri buildUserUri(@Path int userId);

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

        builder.encodedPath("/user");
        builder.appendPath(String.valueOf(userId));

        return builder.build();
    }
}
```
`@Path` can be used with as many method parameters as you need, but you should preserve the
order as it's the order of the path segments in the `Uri`, i.e. for the builder method:

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

Will give you `https://example.com/user/42/students`

**IMPORTANT:** `@Path` method parameters **can not be nullable**.

### Types

`Boring Yuri` knows how to convert primitives, primitive wrappers, `String` and `Uri` to a path
segment or a query parameter. But sometimes it's not enough and we have to deal with application
specific, platform or library types.

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
`https://example.com/user?data="42;John%20Doe"`

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

**NOTE:** Specifying `@TypeAdapter` at use will override the standard type conversion (eg. if you
need a different decimal format for a number).  

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

```
builder.buildStaticMapUri(53.893009, 27.567444);
builder.buildStaticMapUri(37.773972, -122.431297);
```

will give two `Uri`'s accordingly:

```
https://maps.example.com/maps/api/staticmap?lat=53.893009&lng=27.567444&sensor=true&zoom=2.5
https://maps.example.com/maps/api/staticmap?lat=37.773972&lng=-122.431297&sensor=true&zoom=2.5
```

**NOTE:** One `Uri` builder method may have many constant query parameters of the same type.

### Deserialize data from Uri

Sometimes, working with `Uri`, you may need to restore query parameters and path segments from
the given `Uri` and convert them into appropriate types. This often happens when your application
have its own  `ContentProvider`, so it becomes a receiver of the `Uri`.

```java
@UriFactory(scheme = ContentResolver.SCHEME_CONTENT, authority = "boringyuri.sample.provider")
interface ContactUriBuilder {

    @UriBuilder("file/photo")
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
@UriData
interface ContactPhotoUriData {

    @NonNull
    @Path
    String getGroup();

    @Path
    long getContactId();

    @Nullable
    @Param("desired_dimensions")
    @TypeAdapter(RectTypeAdapter.class)
    Rect getDesiredDimens();

}
```

For this interface you'll have a generated implementation similar to the one for `@WithUriData`
with the only difference that it also implements `ContactPhotoUriData`.

**IMPORTANT:** the cost of the "independence" is that you have to manage manually the type
consistency of the builder method parameters and the getters of the data class (if you have the
builders of course). `@WithUriData` does it for you since there is one source of truth: the builder
method in the factory interface.

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
  implementation "org.boringyuri:boringyuri-api:1.0.0"
  annotationProcessor "org.boringyuri:boringyuri-processor:1.0.0"
}
```

With Kotlin:

```groovy
apply plugin: 'kotlin-kapt'

dependencies {
  implementation "org.boringyuri:boringyuri-api:1.0.0"
  kapt "org.boringyuri:boringyuri-processor:1.0.0"
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