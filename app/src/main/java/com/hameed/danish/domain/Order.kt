package com.hameed.danish.domain

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Order(
    @SerializedName("id") val id:Int=0,
    @SerializedName("product_name")val product_name: String="",
    @SerializedName("repo_1")val repo_1: Int,
    @SerializedName("repo_2")val repo_2:Int,
    @SerializedName("emp_name")val emp_name: String,
    @SerializedName("date")val date: String?
)

fun Order.getFormattedEmpName():String =
    String.format(" %s : %s","البائع", emp_name)
fun Order.getFormattedQuantity():String =
    String.format(" %s : %s","الكمية ", repo_1)
fun Order.getFormattedDate():String {
    val stringArray: List<String> = date?.split("T") ?: listOf()
   return String.format(" %s             %s",stringArray[0],stringArray[stringArray.size - 1].substring(0,8))
}
