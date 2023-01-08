package com.hameed.danish

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hameed.danish.databinding.FragmentAddUserBinding
import com.hameed.danish.domain.User
import com.hameed.danish.viewmodels.InventoryViewModelFactory
import com.hameed.danish.viewmodels.OverviewViewModel


class AddUserFragment : Fragment() {
    lateinit var user: User
    private val navigationArgs : AddUserFragmentArgs by navArgs()

    private val viewModel: OverviewViewModel by activityViewModels {
        InventoryViewModelFactory(
            requireActivity().application )
    }
    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
        disableAll()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.uid
        val newTitle = navigationArgs.title

        if(id>0 && newTitle == getString(R.string.change_password))

        {
            viewModel.retrieveUser(id).observe(this.viewLifecycleOwner){
                    selectedUser->
                try {
                    user = selectedUser
                    bind(user)
                    binding.saveAction.setText(R.string.change_password)
                    binding.password.setText("")
                    binding.password.isVisible=true
                    binding.deleteUser.isVisible = false
                    binding.userName.isEnabled=false
                    binding.isAdminSwitch.isVisible = false
                    binding.isAdmin.isVisible = false
                    binding.saveAction.isEnabled = true
                    binding.saveAction.setText(R.string.change_password)
                    binding.saveAction.setOnClickListener{
                        changePassword()
                        val snackbar = Snackbar
                            .make(it, getString(R.string.pwd_changred), Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }

                }
                catch (e:Exception)
                {
                    Log.e("Users",e.message.toString())
                }
            }
            activity?.actionBar?.setTitle(R.string.change_password)
        }
        else if (id>0)
        {
            viewModel.retrieveUser(id).observe(this.viewLifecycleOwner){
                    selectedUser->
                try {
                    user = selectedUser
                    bind(user)
                    binding.userName.isEnabled = false
                    binding.password.isVisible = false
                    binding.isAdminSwitch.isVisible = true
                    binding.deleteUser.isVisible = true
                    binding.isAdmin.isVisible = true
                    binding.saveAction.isEnabled = true
                    binding.saveAction.setText(R.string.edit)
                    binding.saveAction.setOnClickListener{
                        update()
                        val snackbar = Snackbar
                            .make(it, getString(R.string.user_updated), Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }

                }
                catch (e:Exception)
                {
                    Log.e("Users",e.message.toString())
                }
            }
            activity?.actionBar?.setTitle(R.string.edit_user)
        }
        else if(id == -1)  {
            binding.saveAction.setText(R.string.save_action)
            binding.saveAction.setOnClickListener {
                addUser()
                val snackbar = Snackbar
                    .make(it, getString(R.string.user_added), Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
            binding.userName.isEnabled = true
            binding.password.isVisible = true
            binding.deleteUser.isVisible = false
            binding.isAdminSwitch.isVisible = true
            binding.isAdmin.isVisible = true
            binding.saveAction.isEnabled = true

    }
    }
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.userName.text.toString(),
            binding.password.text.toString(),
            binding.name.text.toString()
        )
    }
    private fun addUser() {
        if(isEntryValid())
        {
            viewModel.addNewUser(
                binding.name.text.toString(),
                binding.userName.text.toString(),
                binding.password.text.toString(),
                binding.isAdminSwitch.isChecked
            )

            val action = AddUserFragmentDirections.actionAddUserFragmentToListProductsFragment()
            findNavController().navigate(action)
        }
        else
        {
            showErrorDialog("يجب ملئ الحقول المطلوبة")
        }
    }

    private fun bind(user: User) {
        //val price = "%.2f".format(product.productPrice)
        binding.apply {
            userName.setText(user.username, TextView.BufferType.SPANNABLE)
            name.setText(user.name, TextView.BufferType.SPANNABLE)
            password.setText(user.password, TextView.BufferType.SPANNABLE)
            isAdminSwitch.isChecked = user.isAdmin
            deleteUser.setOnClickListener {
                viewModel.deleteUser(user.id)
                val action = AddUserFragmentDirections.actionAddUserFragmentToListProductsFragment()
                findNavController().navigate(action)
            }

        }
    }
    private fun disableAll()
    {
        binding.apply {
            userName.isEnabled = false
            password.isVisible = false
            deleteUser.isVisible = false
            isAdminSwitch.isVisible = false
            isAdmin.isVisible = false
            saveAction.isEnabled = false
        }
    }
    private fun update() {
        if(isEntryValid())
        {
            user.name =  binding.name.text.toString()

            user.isAdmin =  binding.isAdminSwitch.isChecked
            viewModel.updateUser(
                user
            )

            val action = AddUserFragmentDirections.actionAddUserFragmentToUserManageFragment()
            findNavController().navigate(action)
        }
        else
        {
            showErrorDialog("يجب ملئ الحقول المطلوبة")
        }
    }
    private fun changePassword() {
        if(isEntryValid())
        {
            viewModel.changePassword(
                user
            )

            val action = AddUserFragmentDirections.actionAddUserFragmentToUserManageFragment()
            findNavController().navigate(action)
        }
        else
        {
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
    override fun onDestroy() {
        super.onDestroy()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

}