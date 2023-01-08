package com.hameed.danish.network


//import kotlinx.coroutines.flow.Flow

import com.google.gson.GsonBuilder
import com.hameed.danish.database.DatabaseProduct
import com.hameed.danish.domain.Order
import com.hameed.danish.domain.Product
import com.hameed.danish.domain.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


private const val BASE_URL =
    "baseUrl"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

var gson = GsonBuilder()
    .setDateFormat("yyyy-MM-dd hh:mm:ss")
    .create()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(BASE_URL)
    .build()


interface DanishApiService {
//    @GET("photos")
//     fun getPhotos(): Flow<List<MarsPhoto>>
    @GET("api/products")
    suspend fun getProducts(): List<Product>
    @GET("api/products/{id}")
     suspend fun getProduct(@Path("id")id: Int): Product
    @Multipart
    @POST("api/products")
    suspend fun addProduct(@Part files: MultipartBody.Part?, @Part ("product") product: Product): Product
    @PUT("api/products/{id}")
    suspend fun putProduct(@Path("id") int: Int,@Body product: Product) : Product
    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") int: Int)

    @GET("api/users")
    suspend fun getUsers(): List<User>
    @GET("api/users/{id}")
    suspend fun getUser(@Path("id")id: Int): User
    @POST("api/users/login")
    suspend fun getUser(@Body user: User):User
    @POST("api/users")
    suspend fun addUser(@Body user: User):User
    @PUT("api/users/changepassword/{id}")
    suspend fun changePassword(@Path("id") int: Int,@Body user: User) : User
    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") int: Int,@Body user: User) : User
    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") int: Int)

    @GET("api/orders")
    suspend fun getOrders(): List<Order>
    @POST("api/orders")
    suspend fun addOrder(@Body order: Order):Order
    @Multipart
    @POST("api/products")
    suspend fun sendPhoto(@Part files: MultipartBody.Part?, @Part ("product") product: Product): Product


}

object DanishApi {
    val retrofitService : DanishApiService by lazy {
        retrofit.create(DanishApiService::class.java) }
}

/**
 * Convert Network results to database objects
 */
fun List<Product>.asDatabaseModel(): List<DatabaseProduct> {
    return map {
        DatabaseProduct(
            id = it.id,
            productName = it.productName,
            supplierName = it.supplierName,
            productDescription = it.productDescription,
            imgSrcUrl = it.imgSrcUrl,
            productPrice = it.productPrice,
            productCost = it.productCost,
            productWholesale = it.productWholesale,
            productCount1 = it.productCount1,
            productCount2 = it.productCount2)
    }
}
