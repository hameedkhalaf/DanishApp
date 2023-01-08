package com.hameed.danish

import android.app.Application
import com.hameed.danish.database.ProductsDatabase
import com.hameed.danish.database.getDatabase

class DanishApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database: ProductsDatabase by lazy { getDatabase(this) }
    val appcontext = this
}
