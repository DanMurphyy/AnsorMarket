package com.hfad.ansormarket.logInScreens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.FragmentOTPBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import java.util.concurrent.TimeUnit

class OTPFragment : Fragment() {
    private var _binding: FragmentOTPBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var resendTV: TextView
    private lateinit var inputOTP: Array<EditText>
    private lateinit var progressBar: ProgressBar
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private lateinit var otp: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var name: String

    private val args by navArgs<OTPFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOTPBinding.inflate(inflater, container, false)
        val view = binding.root

        otp = args.otp
        resendToken = args.resendToken
        phoneNumber = args.phoneNumber
        name = args.userName

        init()
        progressBar.visibility = View.INVISIBLE
        addTextChangeListener()
        resendOTPTvVisibility()
        setupClickListeners()

        return view
    }

    private fun setupClickListeners() {
        binding.resendTextView.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }

        binding.verifyOTPBtn.setOnClickListener {
            val typedOTP = inputOTP.joinToString("") { it.text.toString() }

            if (typedOTP.length == 6) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(otp, typedOTP)
                progressBar.visibility = View.VISIBLE
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid OTP", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun resendOTPTvVisibility() {
        inputOTP.forEach { it.text.clear() }
        resendTV.visibility = View.INVISIBLE
        resendTV.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed({
            resendTV.visibility = View.VISIBLE
            resendTV.isEnabled = true
        }, 60000)
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressBar.visibility = View.VISIBLE

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.registration_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigate()
                } else {
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.register_error_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    progressBar.visibility = View.VISIBLE
                }
            }
    }

    private fun navigate() {
        mFirebaseViewModel.registerUser(requireView(), name, phoneNumber)
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
            progressBar.visibility = View.VISIBLE
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
            otp = verificationId
            resendToken = token
        }
    }

    private fun addTextChangeListener() {
        inputOTP.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < inputOTP.lastIndex) {
                        inputOTP[index + 1].requestFocus()
                    } else if (s?.isEmpty()!! && index > 0) {
                        inputOTP[index - 1].requestFocus()
                    }
                }
            })
        }
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        progressBar = binding.otpProgressBar
        resendTV = binding.resendTextView
        inputOTP = arrayOf(
            binding.otpEditText1,
            binding.otpEditText2,
            binding.otpEditText3,
            binding.otpEditText4,
            binding.otpEditText5,
            binding.otpEditText6
        )
    }

    fun success() {
        if (activity is LogMainActivity) {
            (activity as LogMainActivity).intentLog()
            Log.d("TAG", "being called2")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
