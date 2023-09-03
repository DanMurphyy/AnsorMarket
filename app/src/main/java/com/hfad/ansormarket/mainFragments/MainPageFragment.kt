package com.hfad.ansormarket.mainFragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.hfad.ansormarket.R
import com.hfad.ansormarket.SharedViewModel
import com.hfad.ansormarket.databinding.FragmentItemCreateBinding
import com.hfad.ansormarket.databinding.FragmentMainPageBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.logInScreens.LogMainActivity
import com.hfad.ansormarket.models.Constants
import com.hfad.ansormarket.models.Item
import java.io.IOException

class MainPageFragment : Fragment() {

    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var mSelectedImageFileUri: Uri? = null
    private var itemCreateBinding: FragmentItemCreateBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null


    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted)
                showImageChooserPermissionDeniedDialog()
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        mFirebaseViewModel.showProgress(requireContext())
        mFirebaseViewModel.loadUserData()

        mFirebaseViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            Glide
                .with(requireContext())
                .load(user!!.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.userImage)
            binding.name.text = user!!.name
            val loginUser = user.login
            val userLogin = loginUser.replace("@market.com", "").trim()
            binding.login.text = userLogin
        }

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LogMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
        }

        return (binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_page_add, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_add_item -> showBottomDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showBottomDialog() {
        bottomSheetDialog = BottomSheetDialog(
            requireContext(),
            R.style.Bottom_Sheet_Style
        ) // Set your own dialog theme
        itemCreateBinding = FragmentItemCreateBinding.inflate(LayoutInflater.from(requireContext()))

        itemCreateBinding!!.ivItemImage.setOnClickListener {
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

        itemCreateBinding!!.btnCreate.setOnClickListener {
            val mItemType = itemCreateBinding!!.etItemType.selectedItem.toString()
            val nameItem = itemCreateBinding!!.etItemName.text.toString()
            val weight = itemCreateBinding!!.etItemWeight.text.toString()
            val priceText = itemCreateBinding!!.etItemPrice.text.toString()
            val price = if (priceText.isNotEmpty()) {
                priceText.toInt()
            } else {
                // Handle the case where the price is empty (e.g., show an error message)
                0 // You can change this default value to another suitable value or handle it as needed
            }

            val category = mSharedViewModel.parseItemType(mItemType)
            val userLogin = mFirebaseViewModel.userLiveData.value!!.login.toString()

            if (TextUtils.isEmpty(nameItem) || TextUtils.isEmpty(weight) || price == 0 || category == null || TextUtils.isEmpty(
                    userLogin
                )
            ) {
                // Show a toast indicating that some fields are empty
                Toast.makeText(
                    requireContext(), getString(R.string.invalid_item_type), Toast.LENGTH_SHORT
                ).show()
            } else {
                if (mSelectedImageFileUri != null) {
                    // Fields are not empty, start uploading the image to Firestore storage
                    mFirebaseViewModel.uploadItemImage(requireContext(), mSelectedImageFileUri!!)
                    mFirebaseViewModel.imageUploadResult.observe(viewLifecycleOwner) { imageUrl ->
                        if (imageUrl != null) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.image_uploaded),
                                Toast.LENGTH_SHORT
                            ).show()
                            createItemData(itemCreateBinding!!) // Pass the binding here
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.image_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // No image selected, show a toast indicating that an image is required
                    Toast.makeText(
                        requireContext(), getString(R.string.image_not_selected), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        // Set the content view of the dialog to the root view of the binding
        bottomSheetDialog!!.setContentView(itemCreateBinding!!.root)

        val window: Window = bottomSheetDialog!!.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.attributes.windowAnimations = R.style.DialogAnimation
        bottomSheetDialog!!.show()
    }

    private fun hideBottomDialog() {
        bottomSheetDialog!!.dismiss()
    }

    private fun createItemData(binding: FragmentItemCreateBinding) {
        val mItemType = binding.etItemType.selectedItem.toString()
        val nameItem = binding.etItemName.text.toString()
        val weight = binding.etItemWeight.text.toString()
        val priceText = binding.etItemPrice.text.toString()
        val price = if (priceText.isNotEmpty()) {
            priceText.toInt()
        } else {
            // Handle the case where the price is empty (e.g., show an error message)
            0 // You can change this default value to another suitable value or handle it as needed
        }

        val category = mSharedViewModel.parseItemType(mItemType)
        val mItemImageUrl = mFirebaseViewModel.imageUploadResult.value.toString()
        val userLogin = mFirebaseViewModel.userLiveData.value!!.login.toString()

        if (TextUtils.isEmpty(nameItem) || TextUtils.isEmpty(weight) || price == 0 || category == null || TextUtils.isEmpty(
                userLogin
            )
        ) {
            // Show a toast indicating that some fields are empty
            Toast.makeText(
                requireContext(), getString(R.string.invalid_item_type), Toast.LENGTH_SHORT
            ).show()
        } else if (TextUtils.isEmpty(mItemImageUrl)) {
            // Show a toast indicating that the image is not selected
            Toast.makeText(
                requireContext(), getString(R.string.image_not_selected), Toast.LENGTH_SHORT
            ).show()
        } else {
            val item = Item(
                imageItem = mItemImageUrl.toString(),
                nameItem = nameItem,
                weight = weight,
                price = price,
                category = category,
                createdBy = userLogin,
            )

            // Call the ViewModel function to create the item
            mFirebaseViewModel.createItem(requireView(), item)
        }
        mFirebaseViewModel.createItemLiveData.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                // Show a toast indicating that the board is created
                Toast.makeText(
                    requireContext(), getString(R.string.board_created), Toast.LENGTH_SHORT
                ).show()

                // Hide the bottom dialog
                hideBottomDialog()
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.board_creating_error), Toast.LENGTH_SHORT
                ).show()
            }
        }
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
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(itemCreateBinding!!.ivItemImage) // Load the image into the ImageView
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showImageChooserPermissionDeniedDialog() {
        Log.d(ContentValues.TAG, "showImageChooserPermissionDeniedDialog called")
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Ilovaga kerakli ruxsatlar berilmagan, Iltimos, sozlamalarga kirib ruxsat bering.")
        builder.setPositiveButton("Sozlamalarga o'tish") { _, _ ->
            Log.d(ContentValues.TAG, "Positive button clicked")
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
            Log.d(ContentValues.TAG, "Negative button clicked")
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}