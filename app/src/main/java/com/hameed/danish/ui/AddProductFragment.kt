package com.hameed.danish.ui


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

import com.hameed.danish.R
import com.hameed.danish.databinding.FragmentAddProductBinding
import com.hameed.danish.domain.Product
import com.hameed.danish.domain.UploadRequestBody
import com.hameed.danish.viewmodels.InventoryViewModelFactory
import com.hameed.danish.viewmodels.OverviewViewModel
import okhttp3.MultipartBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class AddProductFragment : Fragment(), UploadRequestBody.UploadCallback {

    //private val navigationArgs: ListProductsFragmentArgs by navArgs()
    lateinit var product: Product
    private val navigationArgs : AddProductFragmentArgs by navArgs()
    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.
    private val viewModel: OverviewViewModel by activityViewModels {
        InventoryViewModelFactory(
           requireActivity().application )
    }
    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        binding.productImage.setOnClickListener {
            openImage()
        }
        return binding.root
    }

    private fun openImage() {
//        Intent(Intent.ACTION_PICK).also {
//            it.type = "image/*"
//            val mimeType = arrayOf("image/jpeg","image/png")
//            it.putExtra(Intent.EXTRA_MIME_TYPES,mimeType)
//        }
        Log.e("Pick","!")
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED)
        {
            Log.e("Pick","ok")
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
        else
        {
            Log.e("Pick","No")
            activity?.let { it1 -> ActivityCompat.requestPermissions(it1,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ,1) }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.productImage.setImageURI(imageUri)
           binding.choose.isVisible =false
        }
    }
    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString()
        )
    }
    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewItem(it:View) {

        if (isEntryValid()) {

            viewModel.addNewItem(
                binding.itemName.text.toString(),
                binding.productImage.toString(),
                binding.descName.text.toString(),
                binding.supplierName.text.toString(),
                this.binding.itemCost.text.toString().ifBlank { "0" },
                this.binding.itemPrice.text.toString().ifBlank { "0" },
                this.binding.itemWholesale.text.toString().ifBlank { "0" },
                this.binding.itemCount1.text.toString() ,
                this.binding.itemCount2.text.toString().ifBlank { "0" },
                uploadImage())
            val snackbar = Snackbar
                .make(it, getString(R.string.product_added), Snackbar.LENGTH_SHORT)
            snackbar.show()
             val action = AddProductFragmentDirections.actionAddProductFragmentToListProductsFragment()
              findNavController().navigate(action)
        }

    }

    @SuppressLint("Recycle")
    private fun uploadImage(): MultipartBody.Part? {
        if(imageUri != null) {


            val parcelFileDescriptor = activity?.contentResolver!!.openFileDescriptor(
                imageUri!!, "r", null
            ) ?: return null
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(activity?.cacheDir, activity?.contentResolver!!.getFileName(imageUri!!))

            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            val body = UploadRequestBody(file, "image", this)

            return MultipartBody.Part.createFormData(
                "image",
                file.name,
                body
            )

        }
        return null

    }


    private fun bind(product: Product) {
        binding.apply {
            itemName.setText(product.productName, TextView.BufferType.SPANNABLE)
            supplierName.setText(product.supplierName, TextView.BufferType.SPANNABLE)
            descName.setText(product.productDescription, TextView.BufferType.SPANNABLE)
            itemPrice.setText(product.productPrice.toString(), TextView.BufferType.SPANNABLE)
            itemCost.setText(product.productCost.toString(), TextView.BufferType.SPANNABLE)
            itemWholesale.setText(product.productWholesale.toString(), TextView.BufferType.SPANNABLE)
            itemCount1.setText(product.productCount1, TextView.BufferType.SPANNABLE)
            itemCount2.setText(String.format("%d", product.productCount2), TextView.BufferType.SPANNABLE)

            saveAction.setOnClickListener{
                updateItem()
                val snackbar = Snackbar
                    .make(it, getString(R.string.product_updated), Snackbar.LENGTH_SHORT)
                snackbar.show()
            }

        }
    }
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateProduct(
                this.navigationArgs.id,
                this.binding.itemName.text.toString(),
                this.binding.productImage.toString(),
                this.binding.descName.text.toString(),
                this.binding.supplierName.text.toString(),
                this.binding.itemCost.text.toString().ifBlank { "0" },
                this.binding.itemPrice.text.toString().ifBlank { "0" },
                this.binding.itemWholesale.text.toString().ifBlank { "0" },
                this.binding.itemCount1.text.toString() ,
                this.binding.itemCount2.text.toString().ifBlank { "0" },
            )
            val action = AddProductFragmentDirections.actionAddProductFragmentToListProductsFragment()
            findNavController().navigate(action)
        }
        else{
            showErrorDialog("يجب ملئ الحقول المطلوبة")
        }
    }
    private fun showErrorDialog(msg:String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.err))
            .setMessage(msg)
            .setCancelable(false)
            .setNegativeButton(getString(R.string.accept)) { _, _ -> }

            .show()
    }

    /**
     * Called when the view is created.
     * The itemId Navigation argument determines the edit item  or add new item.
     * If the itemId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.id
        if(id>0)

        {
            viewModel.retrieveProduct(id).observe(this.viewLifecycleOwner){
                selectedProduct->
                try {
                product = selectedProduct
                bind(product)
                    binding.saveAction.setText(R.string.edit)
                    activity?.setTitle(R.string.edit_product_fragment)

            }
            catch (e:Exception)
            {
                Log.e("Products",e.message.toString())
            }
            }

        }
        else {
            binding.saveAction.setText(R.string.save_action)
            binding.saveAction.setOnClickListener {
                addNewItem(it)

            }
            activity?.setTitle(R.string.edit_product_fragment)
        }
    }


    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    override fun onProgressUpdate(persentage: Int) {

    }


}

private fun ContentResolver.getFileName(selectedImageUri: Uri): String {
    var name=""
    val returnCursor = this.query(selectedImageUri,null,null,null,null)
    if(returnCursor != null)
    {
        val indexName = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(indexName)
        returnCursor.close()
    }
    return name
}

