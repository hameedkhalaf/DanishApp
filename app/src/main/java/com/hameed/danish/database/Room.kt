package com.hameed.danish.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {
    @Query("select * from databaseproduct")
    fun getProducts(): LiveData<List<DatabaseProduct>>
    @Query("SELECT * FROM databaseproduct WHERE productName LIKE  :name")
    fun getFilterdProducts(name:String): LiveData<List<DatabaseProduct>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( products: List<DatabaseProduct>)
    @Query("DELETE  FROM databaseproduct")
    fun deleteAll( )

}

@Database(entities = [DatabaseProduct::class], version = 4)
abstract class ProductsDatabase: RoomDatabase() {
    abstract val productDao: ProductDao

}
private lateinit var INSTANCE: ProductsDatabase

fun getDatabase(context: Context): ProductsDatabase {
    synchronized(ProductsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                ProductsDatabase::class.java,
                "products")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}

