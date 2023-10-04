package com.hfad.ansormarket.mainFragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.hfad.ansormarket.R
import com.hfad.ansormarket.SharedViewModel
import com.hfad.ansormarket.databinding.FragmentMyProfileBinding
import com.hfad.ansormarket.firebase.FirebaseRepository
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.lanChange.MyPreference
import com.hfad.ansormarket.logInScreens.LogMainActivity
import com.hfad.ansormarket.models.Constants
import com.hfad.ansormarket.models.User
import java.io.IOException
import java.util.concurrent.TimeUnit


class MyProfileFragment : Fragment() {
    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var mSelectedImageFileUri: Uri? = null
    private val TAG = "MyProfileLog"
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) showImageChooserPermissionDeniedDialog()
        }
    private lateinit var myPreference: MyPreference
    private lateinit var number: String
    private lateinit var address: String
    private lateinit var name: String
    private lateinit var mAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        val adRequest = AdRequest.Builder().build()
        binding.adView4.loadAd(adRequest)
        myPreference = MyPreference(requireContext())
        mAuth = FirebaseRepository().auth
        regOrNot()
        mFirebaseViewModel.loadUserData(requireContext())

        binding.imageUser.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mSharedViewModel.showImageChooser(this)
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        binding.btnUpdateProfile.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                if (mSelectedImageFileUri.toString().isNotEmpty()) {
                    mFirebaseViewModel.uploadImage(requireContext(), mSelectedImageFileUri!!)
                }
                mFirebaseViewModel.imageUploadResult.observe(viewLifecycleOwner) { imageUrl ->
                    if (imageUrl != null) {
                        if (imageUrl) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.image_uploaded),
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUserProfileData()
                            mFirebaseViewModel.resetImageUploadResult()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.image_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                updateUserProfileData()
            }
        }
        binding.lifecycleOwner = viewLifecycleOwner // Important for LiveData binding
        binding.viewModel = mFirebaseViewModel // Set the ViewModel

        var lang: String? = myPreference.getLoginCount()
        when (lang) {
            "en" -> binding.rbLotincha.isChecked = true
            "uz" -> binding.rbKirilcha.isChecked = true
            else -> binding.rbLotincha.isChecked = true
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            lang = when (checkedId) {
                R.id.rb_lotincha -> "en"
                R.id.rb_kirilcha -> "uz"
                else -> "en"
            }
            // Save the selected language in SharedPreferences
            myPreference.setLoginCount(lang!!)
            activity?.recreate()
        }

        binding.regBtnProfile.setOnClickListener {
            number = binding.verifyMobileNumber.text!!.trim().toString()
            name = binding.regEtUserName.text.toString()
            address = binding.regEtAddressName.text.toString()
            val validation = mFirebaseViewModel.validateForm(requireView(), name, address, number)
            if (validation) {
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
                }
            }
        }

        return (binding.root)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        @Suppress("DEPRECATION")
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data
            try {
                Glide
                    .with(requireContext())
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.imageUser)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUserProfileData() {
        val mProfileImageUrl = mFirebaseViewModel.imageUploadLive.value
        // Check if an image was uploaded successfully
        val updatedUser = if (!mProfileImageUrl.isNullOrEmpty()) {
            User(
                name = binding.etUserName.text.toString(),
                address = binding.etAddressName.text.toString(),
                mobile = binding.etMobileNumber.text.toString(),
                image = mProfileImageUrl
            )
        } else {
            // If no image was uploaded, keep the existing image URL
            User(
                name = binding.etUserName.text.toString(),
                address = binding.etAddressName.text.toString(),
                mobile = binding.etMobileNumber.text.toString(),
//                image = mFirebaseViewModel.userLiveData.value?.image ?: ""
            )
        }
        // Call the ViewModel function to update the user profile data
        mFirebaseViewModel.updateUserProfileData(requireView(), updatedUser)
        Log.d(TAG, "User data updated")
    }

    private fun showImageChooserPermissionDeniedDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.denied_permissions))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        builder.setNegativeButton(getString(R.string.close_dialog_perm)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.quit_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_item -> quitDialog()
        }
        @Suppress("DEPRECATION")
        return super.onOptionsItemSelected(item)
    }

    private fun quitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.log_out_dialog))
        builder.setPositiveButton(getString(R.string.proceed_off_work_time)) { _, _ ->
            logOut()
        }
        builder.setNegativeButton(getString(R.string.close_dialog_perm)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LogMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finish()
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
                    regAndRef()
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

    private fun regAndRef() {
        mFirebaseViewModel.registerUser(requireView(), name, number, address)
        Log.d("TAG", "being called1")
        activity?.recreate()
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
            val action = MyProfileFragmentDirections.actionMyProfileFragmentToOTPFragment(
                verificationId,
                token, number, name, address
            )
            findNavController().navigate(action)
            binding.phoneProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun regOrNot() {
        val userId = mFirebaseViewModel.getCurrentUserId()
        if (userId.isNotEmpty()) {
            binding.loReg.visibility = View.GONE
            binding.loUpdate.visibility = View.VISIBLE
        } else {
            binding.loReg.visibility = View.VISIBLE
            binding.loUpdate.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}