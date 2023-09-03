package com.hfad.ansormarket.mainFragments

import androidx.fragment.app.Fragment

class ItemCreateFragment : Fragment() {

//    private var _binding: FragmentItemCreateBinding? = null
//    private val binding get() = _binding!!
//
//
//    private val mSharedViewModel: SharedViewModel by viewModels()
//    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
//    private var mSelectedImageFileUri: Uri? = null
//
//    private val permissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (!isGranted)
//                showImageChooserPermissionDeniedDialog()
//        }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentItemCreateBinding.inflate(inflater, container, false)
//
//        binding.ivItemImage.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(
//                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                // Permission is already granted, show image chooser
//                mSharedViewModel.showImageChooser(this)
//            } else {
//                // Request permission using the Activity Result API
//                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
//        }
//
//        binding.btnCreate.setOnClickListener {
//            if (mSelectedImageFileUri != null) {
//                mFirebaseViewModel.uploadItemImage(requireContext(), mSelectedImageFileUri!!)
//                mFirebaseViewModel.imageUploadResult.observe(viewLifecycleOwner) { imageUrl ->
//                    if (imageUrl != null) {
//                        Toast.makeText(
//                            requireContext(), getString(R.string.image_uploaded), Toast.LENGTH_SHORT
//                        ).show()
//                        createItemData()
//                    } else {
//                        Toast.makeText(
//                            requireContext(), getString(R.string.image_error), Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } else {
//                createItemData()
//            }
//        }
//
//        return binding.root
//    }
//
//    private fun createItemData() {
//
//        val mItemType = binding.etItemType.selectedItem.toString()
//        val nameItem = binding.etItemName.text.toString()
//        val weight = binding.etItemWeight.text.toString()
//        val price = binding.etItemPrice.text.toString().toInt()
//
//        val category = mSharedViewModel.parseItemType(mItemType)
//        val mItemImageUrl = mFirebaseViewModel.imageUploadResult
//        val userLogin = mFirebaseViewModel.userLiveData.value.toString()
//
//        if (category != null && !mItemImageUrl.value.isNullOrEmpty() && nameItem.isNotEmpty() && weight.isNotEmpty() && price.toString()
//                .isNotEmpty() && userLogin.isNotEmpty()
//        ) {
//            val item = Item(
//                imageItem = mItemImageUrl.toString(),
//                nameItem = nameItem,
//                weight = weight,
//                price = price,
//                category = category,
//                createdBy = userLogin,
//            )
//
//            // Call the ViewModel function to create the item
//            mFirebaseViewModel.createItem(requireView(), item)
//        } else {
//            // Handle the case where the selected item type cannot be parsed
//            Toast.makeText(
//                requireContext(), getString(R.string.invalid_item_type), Toast.LENGTH_SHORT
//            ).show()
//        }
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
//            mSelectedImageFileUri = data.data
//            try {
//                Glide
//                    .with(requireContext())
//                    .load(mSelectedImageFileUri)
//                    .centerCrop()
//                    .placeholder(R.drawable.ic_baseline_image_24)
//                    .into(binding.ivItemImage)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun showImageChooserPermissionDeniedDialog() {
//        Log.d(TAG, "showImageChooserPermissionDeniedDialog called")
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setMessage("Ilovaga kerakli ruxsatlar berilmagan, Iltimos, sozlamalarga kirib ruxsat bering.")
//        builder.setPositiveButton("Sozlamalarga o'tish") { _, _ ->
//            Log.d(TAG, "Positive button clicked")
//            try {
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                val uri = Uri.fromParts("package", requireContext().packageName, null)
//                intent.data = uri
//                startActivity(intent)
//            } catch (e: ActivityNotFoundException) {
//                e.printStackTrace()
//            }
//        }
//        builder.setNegativeButton("Bekor qilish") { dialog, _ ->
//            Log.d(TAG, "Negative button clicked")
//            dialog.dismiss()
//        }
//        builder.show()
//    }

}