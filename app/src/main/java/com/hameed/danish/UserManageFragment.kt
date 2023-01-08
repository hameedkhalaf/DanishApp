package com.hameed.danish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hameed.danish.databinding.FragmentUserManageBinding
import com.hameed.danish.viewmodels.InventoryViewModelFactory
import com.hameed.danish.viewmodels.OverviewViewModel

class UserManageFragment : Fragment() {

    private val viewModel: OverviewViewModel by activityViewModels {
        InventoryViewModelFactory(
            requireActivity().application
        )
    }

    private val adapter = UserAdapter {
          val action = UserManageFragmentDirections.actionUserManageFragmentToAddUserFragment(it.id)
          this.findNavController().navigate(action)
    }
    private var _binding: FragmentUserManageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserManageBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = viewLifecycleOwner

        // Giving the binding access to the OverviewViewModel
        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.usersRecycleView.adapter = adapter
        viewModel.users.observe(this.viewLifecycleOwner) { users ->
            users?.apply {

                adapter.submitList(users)
            }
        }
    }

}