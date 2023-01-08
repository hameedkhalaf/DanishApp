package com.hameed.danish.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.hameed.danish.database.ProductsDatabase
import com.hameed.danish.database.asDomainModel
import com.hameed.danish.domain.Product
import com.hameed.danish.network.DanishApi
import com.hameed.danish.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductsRepository(private val database: ProductsDatabase) {
    /**
     * Refresh the products stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     */
    suspend fun refreshProducts() {
        withContext(Dispatchers.IO) {
            val productList = DanishApi.retrofitService.getProducts()
            database.productDao.deleteAll()
            return@withContext database.productDao.insertAll(productList.asDatabaseModel())
        }
    }


    val products: LiveData<List<Product>> = Transformations.map(database.productDao.getProducts()) {
        it.asDomainModel()
    }
}