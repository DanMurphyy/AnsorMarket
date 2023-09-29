package com.hfad.ansormarket.logInScreens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.FragmentIntroBinding
import com.hfad.ansormarket.firebase.FirebaseRepository
import com.hfad.ansormarket.firebase.FirebaseViewModel
import java.util.concurrent.TimeUnit

class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private lateinit var number: String
    private lateinit var name: String
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentIntroBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        mAuth = FirebaseRepository().auth

        val adRequest = AdRequest.Builder().build()
        binding.adView1.loadAd(adRequest)
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
            }

        }

        binding.sendOTPBtn.setOnClickListener {
            number = binding.verifyMobileNumber.text!!.trim().toString()
            name = binding.etNameUp.text.toString()
            if (name.isNotEmpty()) {
                if (number.isNotEmpty()) {
                    if (number.length == 9) {
                        number = "+998$number"
                        binding.phoneProgressBar.visibility = View.VISIBLE
                        val options = PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(number)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(requireActivity())                 // Activity (for callback binding)
                            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                            .build()
                        Log.d("TAG", "phone number: $number")

                        PhoneAuthProvider.verifyPhoneNumber(options)

                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.please_enter_correct_number),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_enter_number),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                showErrorSnackBar(requireView(), getString(R.string.please_enter_name))
            }
        }

        return (binding.root)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.registration_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigate()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    }
                    // Update UI
                }
                binding.phoneProgressBar.visibility = View.INVISIBLE
            }
    }

    private fun navigate() {
        mFirebaseViewModel.registerUser(requireView(), name, number)
        Log.d("TAG", "being called1")
        success()

    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: $e")
                Toast.makeText(
                    requireContext(),
                    getString(R.string.register_error_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: $e")
                Toast.makeText(
                    requireContext(),
                    getString(R.string.register_error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.phoneProgressBar.visibility = View.VISIBLE
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            Log.d("TAG", "phone number sent")
            val action = IntroFragmentDirections.actionIntroFragmentToOTPFragment(
                verificationId,
                token, number, name
            )
            findNavController().navigate(action)
            binding.phoneProgressBar.visibility = View.INVISIBLE
        }
    }


    fun success() {
        if (activity is LogMainActivity) {
            (activity as LogMainActivity).intentLog()
            Log.d("TAG", "being called2")
        }
    }


    private fun showErrorSnackBar(view: View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAnchorView(R.id.sub_title)
        snackBar.view.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.snackbar_error_color
            )
        )
        //        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snackBar.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}