package com.hfad.ansormarket.logInScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hfad.ansormarket.databinding.FragmentIntroBinding

class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!

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

        binding.btnIntroIn.setOnClickListener {
            Toast.makeText(requireContext(), "Successful IN", Toast.LENGTH_SHORT).show()
        }

        binding.btnIntroUp.setOnClickListener {
            Toast.makeText(requireContext(), "Successful UP", Toast.LENGTH_SHORT).show()
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


    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        _binding = null
    }

}