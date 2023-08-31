package com.hfad.ansormarket.logInScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.FragmentIntroBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel

class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()

    companion object {
        private const val SIGN_IN_VIEW = "SIGN_IN_VIEW"
        private const val SIGN_UP_VIEW = "SIGN_UP_VIEW"
    }

    private var currentVisibleView: String = SIGN_IN_VIEW

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        makeUnitsVisible()


        mFirebaseViewModel.registrationResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                    requireContext(), getString(R.string.registration_successful),
                    Toast.LENGTH_SHORT
                ).show()
                // Perform navigation or any other action on success
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.registration_failed),
                    Toast.LENGTH_SHORT
                ).show()
                // Handle registration failure
            }
        }

        mFirebaseViewModel.signInResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                success()
            } else {
                // Show error message
                mFirebaseViewModel.showErrorSnackBar(
                    requireView(),
                    getString(R.string.authentication_failed)
                )
            }
        }


        binding.btnIntroUp.setOnClickListener {
            val name = binding.etNameUp.text.toString()
            val login = binding.etLoginUp.text.toString() + "@market.com"
            val password = binding.etPasswordUp.text.toString()
            if (name.isNotEmpty() && password.length < 6) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_minimum_password),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mFirebaseViewModel.registerUser(requireView(), name, login, password)
            }
        }

        binding.btnIntroIn.setOnClickListener {
            val email: String = binding.etLoginIn.text.toString().trim { it <= ' ' } + "@market.com"
            val password: String = binding.etPasswordIn.text.toString().trim { it <= ' ' }

            mFirebaseViewModel.signInUser(requireView(), email, password)
        }

        return (binding.root)
    }


    private fun makeUnitsVisible() {
        binding.rgSignings.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbSignIn.id -> {
                    makeVisibleSignInView()
                }
                binding.rbSignUp.id -> {
                    makeVisibleSignUpView()
                }
            }
        }
    }


    private fun makeVisibleSignInView(): Boolean {
        currentVisibleView = SIGN_IN_VIEW
        binding.signInLo.visibility = View.VISIBLE
        binding.signUpLo.visibility = View.GONE

        binding.etLoginUp.text?.clear()
        binding.etNameUp.text?.clear()
        binding.etPasswordUp.text?.clear()

        return true
    }

    private fun makeVisibleSignUpView(): Boolean {
        currentVisibleView = SIGN_UP_VIEW
        binding.signUpLo.visibility = View.VISIBLE
        binding.signInLo.visibility = View.GONE

        binding.etLoginIn.text?.clear()
        binding.etPasswordIn.text?.clear()

        return true
    }

     fun success() {
        if (activity is LogMainActivity) {
            (activity as LogMainActivity).intentLog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}