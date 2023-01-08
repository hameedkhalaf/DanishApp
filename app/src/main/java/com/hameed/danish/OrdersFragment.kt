package com.hameed.danish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.hameed.danish.databinding.FragmentOrdersBinding
import com.hameed.danish.viewmodels.InventoryViewModelFactory
import com.hameed.danish.viewmodels.OverviewViewModel

class OrdersFragment : Fragment() {
    private val viewModel: OverviewViewModel by activityViewModels {
        InventoryViewModelFactory(
            requireActivity().application
        )
    }
    private val adapter = OrderAdapter()
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = viewLifecycleOwner

        // Giving the binding access to the OverviewViewModel
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.usersRecycleView.adapter = adapter
        viewModel.orders.observe(this.viewLifecycleOwner) { orders ->
            orders?.apply {

                adapter.submitList(orders)
            }
        }
    }

}