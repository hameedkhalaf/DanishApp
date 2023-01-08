package com.hameed.danish

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hameed.danish.databinding.FragmentLoginBinding
import com.hameed.danish.domain.User
import com.hameed.danish.viewmodels.InventoryViewModelFactory
import com.hameed.danish.viewmodels.OverviewViewModel


class LoginFragment : Fragment() {
    lateinit var user: User
    private val viewModel: OverviewViewModel by viewModels {
        InventoryViewModelFactory(
            requireActivity().application )
    }
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isLoggedIn()
        binding.saveAction.setOnClickListener{
            login()
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.userName.text.toString(),
            binding.password.text.toString()
        )
    }

    private fun login() {
        if (isEntryValid()) {

            viewModel.login(
                binding.userName.text.toString(),
                binding.password.text.toString()
            ).observe(this.viewLifecycleOwner) { currentUser ->
                user = currentUser
                navigate(user)

            }


        }
        else {
            showErrorDialog(getString(R.string.empty_msg))
        }
    }

    private fun navigate(user: User) {
        if(user.id > 0 )
           {
               val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)?:return

               with (sharedPref.edit()) {
                       putString(getString(R.string.savedName), user.name)
                       putString(getString(R.string.savedPassword), user.password)
                       putString(getString(R.string.savedUsername), user.username)
                       putBoolean(getString(R.string.savedIsAdmin), user.isAdmin)
                       putInt(getString(R.string.user_id), user.id)
                   Log.e("I",user.id.toString())
                       apply()
                   }


               val action = LoginFragmentDirections.actionLoginFragmentToListProductsFragment()
        findNavController().navigate(action)
          }
            else if(user.id == -1)
        {
            showErrorDialog(getString(R.string.timeout_msg))
        }
        else
            {
                showErrorDialog(getString(R.string.err_msg))
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

    private fun isLoggedIn()
    {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        //val defaultValue = resources.getInteger(R.integer.saved_high_score_default_key)
        val name = sharedPref.getString(getString(R.string.savedName), null)
        val username = sharedPref.getString(getString(R.string.savedUsername), null)
        val password = sharedPref.getString(getString(R.string.savedPassword), null)

        if(username !=null && password != null && name !=null)
        {
            viewModel.login(
                binding.userName.text.toString(),
                binding.password.text.toString()
            ).observe(this.viewLifecycleOwner) { currentUser ->
                user = currentUser
                navigate(user)

            }
            val action = LoginFragmentDirections.actionLoginFragmentToListProductsFragment()
            findNavController().navigate(action)
        }
    }

}