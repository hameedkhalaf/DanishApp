package com.hameed.danish.domain

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product (@SerializedName("id") val id:Int = 0,
                    @SerializedName("productName") val productName: String,
                    @SerializedName("supplierName") val supplierName: String?,
                    @SerializedName("productDescription") val productDescription: String?,
                    @SerializedName("imgSrcUrl") val imgSrcUrl: String?,
                    @SerializedName("productPrice") val productPrice:Double?,
                    @SerializedName("productCost") val productCost:Double?,
                    @SerializedName("productWholesale") val productWholesale:Double=0.0,
                    @SerializedName("productCount1") val productCount1:String?,
                    @SerializedName("productCount2") val productCount2:Int=0,
)
fun Product.getFormattedWholesale():String =
String.format(" %s %n %.2f","جملة", productWholesale)
fun Product.getFormattedCost():String =
    String.format(" %s %n %.2f","شراء", productCost)
fun Product.getFormattedPrice():String =
    String.format(" %s %n %.2f","مفرق", productPrice)
fun Product.getFormattedQuantity():String =
    String.format(" %s %n %d","الكمية", productCount2)
fun Product.getFormattedREpoNum():String =
    String.format(" %s %n %s","مستودع", productCount1)