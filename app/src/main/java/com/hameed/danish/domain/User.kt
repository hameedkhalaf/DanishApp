package com.hameed.danish.domain


data class User(val id:Int=0,
                var name: String="",
                val username: String,
                val password:String,
                var isAdmin:Boolean=false)
fun User.getFormattedID():String =
    String.format(" %s : %d","الرقم", id)
fun User.getFormattedName():String =
    String.format(" %s : %s","الاسم", name)
fun User.getFormattedUserName():String =
    String.format(" %s : %s","اسم المستخدم", username)
fun User.getFormattedType():String {
    if (isAdmin)
    {
       return String.format(" %s : %s", "نوع الحساب", "مدير")
    }
    else
       return String.format(" %s : %s", "نوع الحساب", "مستخدم")
}