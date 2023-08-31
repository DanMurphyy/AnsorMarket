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
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hfad.ansormarket.R
import com.hfad.ansormarket.SharedViewModel
import com.hfad.ansormarket.databinding.FragmentMyProfileBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.logInScreens.LogMainActivity
import com.hfad.ansormarket.models.Constants
import com.hfad.ansormarket.models.User
import java.io.IOException


class MyProfileFragment : Fragment() {
    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var mSelectedImageFileUri: Uri? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted)
                showImageChooserPermissionDeniedDialog()
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        mFirebaseViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            setUserDataInUI(user)
        }

        mFirebaseViewModel.imageUploadResult.observe(viewLifecycleOwner) { imageUrl ->
            if (imageUrl != null) {
                Toast.makeText(requireContext(), "Good!!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Bad!!", Toast.LENGTH_SHORT).show()
            }
        }

        mFirebaseViewModel.showProgress(requireContext())
        mFirebaseViewModel.loadUserData()

        binding.imageUser.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted, show image chooser
                mSharedViewModel.showImageChooser(this)
            } else {
                // Request permission using the Activity Result API
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        binding.btnUpdateProfile.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                mFirebaseViewModel.uploadImage(requireContext(), mSelectedImageFileUri!!)
            }
        }


        return (binding.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

    private fun setUserDataInUI(user: User) {
        Glide
            .with(requireContext())
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.imageUser)

        binding.etUserName.setText(user.name)
        val loginUser = user.login
        val userLogin = loginUser.replace("@market.com", "").trim()
        binding.etUserLogin.setText(userLogin)
        binding.etAddressName.setText(user.address)

        val mobileUser = user.mobile.toString()
        if (mobileUser.length == 1 && mobileUser.contains('0')) {
            binding.etMobileName.setText("")
        } else {
            binding.etMobileName.setText(user.mobile.toString())
        }
    }

    private fun showImageChooserPermissionDeniedDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Ilovaga kerakli ruxsatlar berilmagan, Iltimos, sozlamalarga kirib ruxsat bering.")
        builder.setPositiveButton("Sozlamalarga o'tish") { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        builder.setNegativeButton("Bekor qilish") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.quit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_item -> logOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LogMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finish()
    }

}