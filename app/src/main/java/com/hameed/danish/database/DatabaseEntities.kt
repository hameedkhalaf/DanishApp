package com.hameed.danish.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hameed.danish.domain.Product

@Entity
data class DatabaseProduct constructor(
    @PrimaryKey
        val id : Int,
        val productName: String,
        val supplierName: String?,
        val productDescription: String?,
        val imgSrcUrl: String?,
        val productPrice:Double?,
        val productCost:Double?,
        val productWholesale:Double=0.0,
        val productCount1:String?,
        val productCount2:Int=0,
)
/**
 * Map DatabaseProduct to domain entities
 */
fun List<DatabaseProduct>.asDomainModel(): List<Product> {
    return map {
        Product(
            id = it.id,
            productName = it.productName,
            supplierName = it.supplierName,
            productDescription = it.productDescription,
            imgSrcUrl = it.imgSrcUrl,
            productPrice = it.productPrice,
            productCost = it.productCost,
            productWholesale = it.productWholesale,
            productCount1 = it.productCount1,
            productCount2 = it.productCount2
        )
    }
}
