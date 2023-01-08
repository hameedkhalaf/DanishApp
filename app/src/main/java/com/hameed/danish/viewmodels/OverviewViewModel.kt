package com.hameed.danish.viewmodels


import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hameed.danish.database.getDatabase
import com.hameed.danish.domain.Order
import com.hameed.danish.domain.Product
import com.hameed.danish.domain.User
import com.hameed.danish.network.DanishApi
import com.hameed.danish.repository.ProductsRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException

/**
 *
 */
enum class DanishApiStatus { LOADING, ERROR, DONE }


class OverviewViewModel(application: Application) : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    // The external immutable LiveData for the request status
    val users: LiveData<List<User>> = _users
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders
    private val productsRepository = ProductsRepository(getDatabase(application))
    val productList = productsRepository.products

    private val _retrieveProduct = MutableLiveData<Product>()
    private val retrieveProduct: LiveData<Product> = _retrieveProduct
    private val _currentUser = MutableLiveData<User>()
    private val currentUser: LiveData<User> = _currentUser
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<DanishApiStatus>()
    // The external immutable LiveData for the request status
    val status: LiveData<DanishApiStatus> = _status
    private val _retrieveUser = MutableLiveData<User>()
    private val retrieveUser: LiveData<User> = _retrieveUser
    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            _status.value = DanishApiStatus.LOADING
            try {
                productsRepository.refreshProducts()

                _status.value = DanishApiStatus.DONE
            } catch (e: Exception) {
                _status.value = DanishApiStatus.ERROR
              }
        }

    }

    /**
     * Call getDanishUsers() and  refreshDataFromRepository()  on init so we can display status immediately.
     */
    init {
        getDanishUsers()
        refreshDataFromRepository()
        getDanishOrders()

    }

    /**
     * Gets Mars Users information from the Danish API Retrofit service and updates the
     * [User] [List] [LiveData].
     */


    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(itemName: String): Boolean {
        return itemName.isNotBlank()
    }

    /**
     * Returns an instance of the [Product] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewItemEntry(itemName: String,imgSrcUrl:String , productDescription:String , supplierName: String,productCost: String, itemPrice: String
                                , itemWholesale: String,  itemCount1: String,  itemCount2: String): Product {
        return Product(
            productName = itemName,
            productDescription = productDescription,
            imgSrcUrl = imgSrcUrl,
            supplierName = supplierName,
            productCost =  productCost.toDouble(),
            productPrice = itemPrice.toDouble(),
            productWholesale = itemWholesale.toDouble(),
            productCount1 = itemCount1,
            productCount2 = itemCount2.toInt(),
        )
    }
    /**
     * Launching a new coroutine to add a product in a non-blocking way
     */
    private fun insertItem(product: Product,files: MultipartBody.Part?) {
        viewModelScope.launch {

            try {
                  DanishApi.retrofitService.addProduct(files,product)
                productsRepository.refreshProducts()
            }
            catch (e: HttpException) {
                Log.e("api", e.message.toString())
            }
            catch (e:Exception)
            {
                Log.e("Add",e.toString())
            }
        }
    }
    fun addNewItem(itemName: String,imgSrcUrl:String = "" , productDescription:String = "",supplierName: String,itemCost: String, itemPrice: String
                   , itemWholesale: String,  itemCount1: String,  itemCount2: String,files: MultipartBody.Part?) {
        val newItem = getNewItemEntry(itemName, imgSrcUrl, productDescription, supplierName, itemCost, itemPrice
                                      ,itemWholesale, itemCount1, itemCount2)

        insertItem(newItem,files)

    }


    fun retrieveProduct(id: Int): LiveData<Product> {
         viewModelScope.launch {

             try {

                 _retrieveProduct.value = DanishApi.retrofitService.getProduct(id)


             } catch (e: HttpException) {
                 Log.e("api", e.message.toString())
             }
             catch (e:Exception)
             {
                 Log.e("api1", e.message.toString())
             }

             }
        return retrieveProduct
    }

    private fun updateProduct(product: Product)
    {
        viewModelScope.launch {
            try {
                DanishApi.retrofitService.putProduct(product.id,product)
                //_productList.value = DanishApi.retrofitService.getProducts()
                productsRepository.refreshProducts()
                //_orders.value = DanishApi.retrofitService.getOrders()
            }
            catch (e:Exception)
            {
                Log.e("API",e.message.toString())
            }
        }
    }
    fun sellProduct(empName:String, quantity:Int,product: Product)
    {
        if(product.productCount2>=quantity )
        {
            val newProduct = product.copy(productCount2 = product.productCount2 - quantity)
            updateProduct(newProduct)
            val order = Order(product_name = product.productName, repo_1 = quantity, repo_2 = quantity, emp_name = empName, date = null)
            insertOrder(order)


        }
    }
    fun isStockAvailable(quantity1:Int,product: Product):Boolean
    {
        return if(quantity1 ==0 ) {
            false
        } else {
            (product.productCount2 >= quantity1 )
        }
    }
    fun deleteProduct(id: Int)
    {
        viewModelScope.launch {
            try {
                DanishApi.retrofitService.deleteProduct(id)
                //_productList.value = DanishApi.retrofitService.getProducts()
                productsRepository.refreshProducts()

            }
            catch (e:Exception)
            {
                Log.e("Delete",e.message.toString())
            }
        }
    }
    private fun getUpdatedProductEntry(id: Int,itemName: String,imgSrcUrl:String , productDescription:String , supplierName: String,productCost: String, itemPrice: String
                                       , itemWholesale: String,  itemCount1: String,  itemCount2: String): Product {
        return Product(
            id = id,
            productName = itemName,
            productDescription = productDescription,
            imgSrcUrl = imgSrcUrl,
            supplierName = supplierName,
            productCost = productCost.toDouble(),
            productPrice = itemPrice.toDouble(),
            productWholesale = itemWholesale.toDouble(),
            productCount1 = itemCount1,
            productCount2 = itemCount2.toInt(),
        )
    }

     fun updateProduct(id: Int,itemName: String,imgSrcUrl:String , productDescription:String , supplierName: String,productCost: String, itemPrice: String
                                       , itemWholesale: String,  itemCount1: String,  itemCount2: String)
     {
         val updatedProduct = getUpdatedProductEntry(id,itemName,imgSrcUrl, productDescription, supplierName,
             productCost, itemPrice, itemWholesale,  itemCount1,  itemCount2)
         updateProduct(updatedProduct)
     }

     fun getFilteredProduct(name: String): List<Product>? {
         return productList.value?.filter {it -> it.productName.contains(name) }

    }

    fun refresh()
    {
        refreshDataFromRepository()
    }

    /* Users Section */
    private fun getDanishUsers() {
        viewModelScope.launch {
            _status.value = DanishApiStatus.LOADING
            try {
                _users.value = DanishApi.retrofitService.getUsers()
                Log.e("Users","Ok")
                _status.value = DanishApiStatus.DONE
            } catch (e: Exception) {
                _status.value = DanishApiStatus.ERROR


            }

        }
    }
    fun isEntryValid(username: String,password: String,name:String="name"): Boolean {
        return !(username.isBlank() || password.isBlank() || name.isBlank())

    }
    fun login(username: String,password: String): LiveData<User>
    {
        val newUser = User(username = username, password = password)
        viewModelScope.launch {
            _status.value = DanishApiStatus.LOADING
            try {
                _currentUser.value = DanishApi.retrofitService.getUser(newUser)
                _status.value = DanishApiStatus.DONE

            }
            catch (e:HttpException)
            {
                _currentUser.value = User(name = "no name", username = "", password = "", isAdmin = false)
                _status.value = DanishApiStatus.DONE
            }
            catch (e:Exception)
            {
                Log.e("Network",e.message.toString())
                _currentUser.value = User(id=-1,name = "no name", username = "", password = "", isAdmin = false)

            }
        }

        return currentUser
    }
    fun addNewUser(name: String, username: String, password: String,isAdmin:Boolean) {
        val newUser = User(name = name, username = username, password = password, isAdmin = isAdmin)
        insertUser(newUser)

    }
    private fun insertUser(user: User) {
        viewModelScope.launch {
            _status.value = DanishApiStatus.LOADING
            try {
                DanishApi.retrofitService.addUser(user)
                _users.value=DanishApi.retrofitService.getUsers()
                _status.value = DanishApiStatus.DONE
            }
            catch (e:Exception)
            {
                Log.e("Add",e.message.toString())
                _status.value = DanishApiStatus.ERROR
            }
        }
    }

    fun deleteUser(id: Int)
    {
        viewModelScope.launch {
            try {
                DanishApi.retrofitService.deleteUser(id)
                _users.value = DanishApi.retrofitService.getUsers()

            }
            catch (e:Exception)
            {
                Log.e("Delete",e.message.toString())
            }
        }
    }

    fun retrieveUser(id: Int): LiveData<User> {
        viewModelScope.launch {

            try {

                _retrieveUser.value = DanishApi.retrofitService.getUser(id)


            } catch (e: HttpException) {
                Log.e("api", e.message.toString())
            }
            catch (e:Exception)
            {
                Log.e("api1", e.message.toString())
            }

        }
        return retrieveUser
    }

    fun updateUser(user: User) {
        val newUser = user.copy(name = user.name, isAdmin = user.isAdmin)
        viewModelScope.launch {

            try {
                DanishApi.retrofitService.updateUser(user.id,newUser)
                _users.value=DanishApi.retrofitService.getUsers()
            }
            catch (e:Exception)
            {
                Log.e("UpdateUser",e.message.toString())
            }
        }

    }
    fun changePassword(user: User) {
        val newUser = user.copy(name = user.name, password = user.password)
        viewModelScope.launch {

            try {
                DanishApi.retrofitService.changePassword(user.id,newUser)
                _users.value=DanishApi.retrofitService.getUsers()
            }
            catch (e:Exception)
            {
                Log.e("ChangePassword",e.message.toString())
            }
        }

    }
    /* End Users Section */

    /* End Orders Section */
    private fun getDanishOrders() {
        viewModelScope.launch {
            _status.value = DanishApiStatus.LOADING

            try {
                _orders.value = DanishApi.retrofitService.getOrders()
                _status.value = DanishApiStatus.DONE
            } catch (e: Exception) {
                _status.value = DanishApiStatus.ERROR
                }

        }
    }
    private fun insertOrder(order: Order) {
        viewModelScope.launch {

            try {
                DanishApi.retrofitService.addOrder(order)
                _orders.value=DanishApi.retrofitService.getOrders()
            }
            catch (e:Exception)
            {
                Log.e("AddOrder",e.message.toString())
            }
        }
    }

    /* End Orders Section */
}
