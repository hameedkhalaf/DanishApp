package com.hameed.danish


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import com.hameed.danish.databinding.FragmentProductDetailBinding
import com.hameed.danish.viewmodels.InventoryViewModelFactory
import com.hameed.danish.viewmodels.OverviewViewModel
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hameed.danish.domain.*

class ProductDetailFragment : Fragment() {
    private val navigationArgs: ProductDetailFragmentArgs by navArgs()
  lateinit var product: Product
     var name:String = ""
    private val viewModel: OverviewViewModel by activityViewModels {
        InventoryViewModelFactory(
            requireActivity().application
        )
    }
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = viewLifecycleOwner

        // Giving the binding access to the OverviewViewModel
        binding.viewmodel = viewModel

        // Sets the adapter of the photosGrid RecyclerView
        // binding.productsRecycleView.adapter = ProductAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id =  navigationArgs.id

        viewModel.retrieveProduct(id).observe(this.viewLifecycleOwner)
        {
                selectedProduct ->

               product = selectedProduct
               bind(product)


        }
    }
    private  fun isLoggedIn():Boolean
    {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return false
        //val defaultValue = resources.getInteger(R.integer.saved_high_score_default_key)
         name = sharedPref.getString(getString(R.string.savedName), "").toString()
        val username = sharedPref.getString(getString(R.string.savedUsername), null)
        val password = sharedPref.getString(getString(R.string.savedPassword), null)
        val isAdmin = sharedPref.getBoolean(getString(R.string.savedIsAdmin),false )

        return if(username !=null && password != null && name !="") {
            isAdmin
        } else false
    }
    private fun bind(product: Product) {
        showAdminInfo()
        disableSellButton()
        binding.apply {

            itemName.text = product.productName
            supplierName.text = product.supplierName
            itemDescription.text = product.productDescription
            itemPrice.text = product.getFormattedPrice()
            itemWholeprice.text = product.getFormattedWholesale()
            itemQuantity.text = product.getFormattedQuantity()
            itemCost.text = product.getFormattedCost()
            sellItem.setOnClickListener{
                sellItem()
                val data:String = String.format("%s\n  %s : %s   %s : %s" ,
                    "تمت العملية بنجاح ", "المنتج",itemName.text,"الكمية",itemQuantity.text)
                val snackbar = Snackbar
                    .make(it, data, Snackbar.LENGTH_SHORT)
                snackbar.show()
            }

            deleteItem.setOnClickListener{
                showConfirmationDialog()

            }
            editItem.setOnClickListener {
                editProduct()
            }
            soldItem1.doOnTextChanged { _, _, _, _ ->
               disableSellButton()
            }


        }

       // binding.itemName.text = item.itemName
        //binding.itemPrice.text = item.getFormattedPrice()
        //binding.itemCount.text = item.quantityInStock.toString()

    }

    private fun showAdminInfo() {
       binding.apply {
           supplierName.isVisible = isLoggedIn()
           itemCost.isVisible = isLoggedIn()
           deleteItem.isVisible = isLoggedIn()
           editItem.isVisible = isLoggedIn()
       }
    }

    private fun disableSellButton()
    {
        val repo1 = if(binding.soldItem1.text!!.isBlank()) 0 else binding.soldItem1.text.toString().toInt()

        binding.sellItem.isEnabled = viewModel.isStockAvailable(
           repo1,product)
    }
    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {
        viewModel.deleteProduct(product.id)
        findNavController().navigateUp()
    }
    private fun sellItem() {
        val repo1 = if(binding.soldItem1.text!!.isBlank()) 0 else binding.soldItem1.text.toString().toInt()

        viewModel.sellProduct(name,repo1,product)
        findNavController().navigateUp()
    }
    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()

            }
            .show()
    }
    private fun editProduct() {
       try{
           val action = ProductDetailFragmentDirections.actionProductDetailFragmentToAddProductFragment(
               getString(R.string.edit_product_fragment),
               product.id
           )
           this.findNavController().navigate(action)
       }
       catch(e:Exception)
       {
           Log.e("Edit",e.message.toString())
       }
    }

}