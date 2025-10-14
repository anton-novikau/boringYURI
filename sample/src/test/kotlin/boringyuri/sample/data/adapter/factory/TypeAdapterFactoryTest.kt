package boringyuri.sample.data.adapter.factory

import org.junit.Assert
import org.junit.Test

class TypeAdapterFactoryTest {
    @Test
    fun testCreateAddressTypeAdapter() {
        val addressTypeAdapter = TypeAdapterFactory.createAddressTypeAdapter()
        val addressTypeAdapter2 = TypeAdapterFactory.createAddressTypeAdapter()
        Assert.assertSame(addressTypeAdapter, addressTypeAdapter2)
    }

    @Test
    fun testCreateAdminTypeAdapter() {
        val adminTypeAdapter = TypeAdapterFactory.createAdminTypeAdapter()
        val adminTypeAdapter2 = TypeAdapterFactory.createAdminTypeAdapter()
        Assert.assertSame(adminTypeAdapter, adminTypeAdapter2)
    }

    @Test
    fun testCreateCoordinatesTypeAdapter() {
        val coordinatesTypeAdapter = TypeAdapterFactory.createCoordinatesTypeAdapter()
        val coordinatesTypeAdapter2 = TypeAdapterFactory.createCoordinatesTypeAdapter()
        Assert.assertSame(coordinatesTypeAdapter, coordinatesTypeAdapter2)
    }

    @Test
    fun testCreateDoubleArrayTypeAdapter() {
        val doubleArrayTypeAdapter = TypeAdapterFactory.createDoubleArrayTypeAdapter()
        val doubleArrayTypeAdapter2 = TypeAdapterFactory.createDoubleArrayTypeAdapter()
        Assert.assertSame(doubleArrayTypeAdapter, doubleArrayTypeAdapter2)
    }

    @Test
    fun testCreateRectTypeAdapter() {
        val rectTypeAdapter = TypeAdapterFactory.createRectTypeAdapter()
        val rectTypeAdapter2 = TypeAdapterFactory.createRectTypeAdapter()
        Assert.assertSame(rectTypeAdapter, rectTypeAdapter2)
    }

    @Test
    fun testCreateUserTypeAdapter() {
        val userTypeAdapter = TypeAdapterFactory.createUserTypeAdapter()
        val userTypeAdapter2 = TypeAdapterFactory.createUserTypeAdapter()
        Assert.assertSame(userTypeAdapter, userTypeAdapter2)
    }
}