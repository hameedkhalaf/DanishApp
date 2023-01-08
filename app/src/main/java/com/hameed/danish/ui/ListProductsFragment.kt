package com.hameed.danish.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hameed.danish.R
import com.hameed.danish.databinding.FragmentListProductsBinding
import com.hameed.danish.viewmodels.InventoryViewModelFactory
import com.hameed.danish.viewmodels.OverviewViewModel
import java.util.*

@Suppress("DEPRECATION")
class ListProductsFragment : Fragment() {

    private val viewModel: OverviewViewModel by activityViewModels {
        InventoryViewModelFactory(
            requireActivity().application
        )
    }

    private val adapter = ProductAdapter {
        val action = ListProductsFragmentDirections.actionListProductsFragmentToProductDetailFragment(it.id)
        this.findNavController().navigate(action)
    }
    private var _binding: FragmentListProductsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mainHandler.removeCallbacks(updateTextTask)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListProductsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
         binding.lifecycleOwner = viewLifecycleOwner

        // Giving the binding access to the OverviewViewModel
        binding.viewmodel = viewModel
        requireActivity().actionBar?.title = "Hi"
//        fixedRateTimer("timer", false, 0L, 30 * 1000) {
//            requireActivity().runOnUiThread {
//                viewModel.refresh()
//                Log.e("Timer","3")
//            }
//        }

        // Sets the adapter of the photosGrid RecyclerView
       // binding.productsRecycleView.adapter = ProductAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productsRecycleView.adapter = adapter
        viewModel.productList.observe(this.viewLifecycleOwner) { products ->
            products?.apply {
             adapter.submitList(products)
            }
        }
        binding.floatingActionButton.setOnClickListener{
            val action = ListProductsFragmentDirections.actionListProductsFragmentToAddProductFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }
        binding.floatingActionButton.isVisible=isLoggedIn()
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing=true
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing=false
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //menu.clear()
        inflater.inflate(R.menu.menu,menu)
                val menuItem = menu.findItem(R.id.search)
        val searchView : SearchView = MenuItemCompat.getActionView(menuItem) as SearchView
        searchView.isIconified
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mySearch(query)
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                mySearch(newText)
                return true
            }
        })

    }

    private fun mySearch(newText: String) {
        adapter.submitList(viewModel.getFilteredProduct(newText))
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {

                true
            }
            R.id.add_user -> {
                Log.e("Pass",id.toString())
                val action = ListProductsFragmentDirections.actionListProductsFragmentToAddUserFragment()
                findNavController().navigate(action)
                true
            }
            R.id.order ->{
                val action = ListProductsFragmentDirections.actionListProductsFragmentToOrdersFragment()
                findNavController().navigate(action)
                true
            }
            R.id.manage_user ->{
                val action = ListProductsFragmentDirections.actionListProductsFragmentToUserManageFragment()
                findNavController().navigate(action)
                true
            }
            R.id.change_password ->{

                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return false
                val id = sharedPref.getInt(getString(R.string.user_id), -1)

                val action = ListProductsFragmentDirections.actionListProductsFragmentToAddUserFragment(uid = id, title = getString(R.string.change_password))
                findNavController().navigate(action)
                true
            }
            R.id.logout ->{
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

                if (sharedPref != null) {
                    with (sharedPref.edit()) {
                        putString(getString(R.string.savedName), null)
                        putString(getString(R.string.savedPassword), null)
                        putString(getString(R.string.savedUsername), null)
                        putBoolean(getString(R.string.savedIsAdmin), false)
                        apply()
                    }
                }
                val action = ListProductsFragmentDirections.actionListProductsFragmentToLoginFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.add_user).isVisible=isLoggedIn()
        menu.findItem(R.id.order).isVisible = isLoggedIn()
        menu.findItem(R.id.manage_user).isVisible = isLoggedIn()
    }
    private  fun isLoggedIn():Boolean
    {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return false
        //val defaultValue = resources.getInteger(R.integer.saved_high_score_default_key)
        val name = sharedPref.getString(getString(R.string.savedName), null)
        val username = sharedPref.getString(getString(R.string.savedUsername), null)
        val password = sharedPref.getString(getString(R.string.savedPassword), null)
        val isAdmin = sharedPref.getBoolean(getString(R.string.savedIsAdmin),false )

        return if(username !=null && password != null && name !=null) {
            isAdmin
        } else false
    }



}