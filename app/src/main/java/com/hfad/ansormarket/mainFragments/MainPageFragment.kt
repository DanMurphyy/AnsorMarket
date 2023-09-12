package com.hfad.ansormarket.mainFragments

import android.Manifest
import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hfad.ansormarket.R
import com.hfad.ansormarket.SharedViewModel
import com.hfad.ansormarket.adapters.ItemListAdapter
import com.hfad.ansormarket.databinding.FragmentItemCreateBinding
import com.hfad.ansormarket.databinding.FragmentMainPageBinding
import com.hfad.ansormarket.databinding.ItemInfoLayoutBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.models.Constants
import com.hfad.ansormarket.models.Item
import com.hfad.ansormarket.models.MyCart
import jp.wasabeef.recyclerview.animators.LandingAnimator
import java.io.IOException

class MainPageFragment : Fragment() {

    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var mSelectedImageFileUri: Uri? = null
    private var itemCreateBinding: FragmentItemCreateBinding? = null
    private var itemInfoBinding: ItemInfoLayoutBinding? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var mDialog: Dialog? = null
    private val adapter: ItemListAdapter by lazy { ItemListAdapter() }
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
        mFirebaseViewModel.loadUserData(requireContext())
        showRecyclerView()

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveUpdates()
    }

    private fun liveUpdates() {
        mFirebaseViewModel.itemList.observe(viewLifecycleOwner) { itemList ->
            // Update the adapter only if it's not null
            adapter.setItems(itemList)
        }

        mFirebaseViewModel.myCartsLiveData.observe(viewLifecycleOwner) { myCartList ->
            // Update the adapter with cart data
            adapter.setMyCartData(myCartList)
            mSharedViewModel.cartItemCount(requireView(), myCartList)
        }

        // Fetch data only if it's not already fetched
        if (mFirebaseViewModel.itemList.value == null) {
            mFirebaseViewModel.fetchAllItems()
        }

        if (mFirebaseViewModel.myCartsLiveData.value == null) {
            mFirebaseViewModel.fetchMyCart()
        }
    }

    private fun showRecyclerView() {
        val recyclerView = binding.recyclerViewList
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = LandingAnimator().apply {
            addDuration = 200
        }

        adapter.setOnClickListener(object : ItemListAdapter.OnClickListener {
            override fun onClick(position: Int, currentItem: Item, quantity: Int) {
                showInfoDialog(currentItem, quantity)
            }

            override fun onAddToCartClick(currentItem: Item, quantity: Int) {
                toCart(currentItem, quantity)
                liveUpdates()
            }
        })


    }

    private fun showInfoDialog(currentItem: Item, quantity: Int) {
        var newQuantity = quantity
        val myCartList = mFirebaseViewModel.myCartsLiveData.value!!
        var isItemInCart = false
        var myCartCurrentItem = ""

        for (i in myCartList) {
            if (currentItem.documentId == i.itemProd.documentId) {
                isItemInCart = true
                myCartCurrentItem = i.documentId
                break
            }
        }

        mDialog = Dialog(requireContext(), R.style.Bottom_Sheet_Style)
        itemInfoBinding = ItemInfoLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        val window: Window = mDialog!!.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.attributes.windowAnimations = R.style.DialogAnimation
        Glide
            .with(requireContext())
            .load(currentItem.imageItem)
            .fitCenter()
            .placeholder(R.drawable.ic_images)
            .into(itemInfoBinding!!.infoListImage)
        itemInfoBinding!!.infoListPrice.text = currentItem.price.toString()
        itemInfoBinding!!.infoListName.text = currentItem.nameItem
        itemInfoBinding!!.infoListWeight.text = currentItem.weight
        itemInfoBinding!!.quantityInfo.text = newQuantity.toString()

        itemInfoBinding!!.quantityPlus.setOnClickListener {
            newQuantity++
            val newAmount = currentItem.price * newQuantity // Recalculate newAmount
            itemInfoBinding!!.quantityInfo.text = newQuantity.toString()
            mFirebaseViewModel.updateCartItemQuantity(myCartCurrentItem, newQuantity, newAmount)
        }

        itemInfoBinding!!.quantityMinus.setOnClickListener {
            if (newQuantity > 1) {
                newQuantity--
                itemInfoBinding!!.quantityInfo.text = newQuantity.toString()
                val newAmount = currentItem.price * newQuantity // Recalculate newAmount
                mFirebaseViewModel.updateCartItemQuantity(myCartCurrentItem, newQuantity, newAmount)
            } else if (newQuantity == 1) {
                mFirebaseViewModel.deleteMyCart(myCartCurrentItem)
                Toast.makeText(
                    requireContext(), getString(R.string.cart_removed), Toast.LENGTH_SHORT
                ).show()
                liveUpdates()
                hideInfoDialog()
            }

        }

        itemInfoBinding!!.iconDelete.setOnClickListener {
            mFirebaseViewModel.deleteMyCart(myCartCurrentItem)
            Toast.makeText(
                requireContext(), getString(R.string.cart_removed), Toast.LENGTH_SHORT
            ).show()
            liveUpdates()
            hideInfoDialog()
        }
        if (isItemInCart) {
            itemInfoBinding!!.btnToCart.visibility = View.GONE
            itemInfoBinding!!.iconDelete.visibility = View.VISIBLE

        } else {
            itemInfoBinding!!.btnToCart.visibility = View.VISIBLE
            itemInfoBinding!!.iconDelete.visibility = View.GONE
        }

        itemInfoBinding!!.btnToCart.setOnClickListener {
            toCart(currentItem, newQuantity)
            liveUpdates()
            hideInfoDialog()
        }

        mDialog!!.setContentView(itemInfoBinding!!.root)
        mDialog!!.setCancelable(true)
        mDialog!!.setCanceledOnTouchOutside(true)
        mDialog!!.show()
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

    }

    private fun hideInfoDialog() {
        mDialog!!.dismiss()
    }

    private fun toCart(currentItem: Item, quantity: Int) {
        val mQuantity = quantity
        val newAmount = currentItem.price * mQuantity

        val userId = mFirebaseViewModel.getCurrentUserId() // Assuming userId is not empty here

        val myCart = MyCart(
            currentItem,
            mQuantity,
            userId,
            amount = newAmount
        )

        mFirebaseViewModel.toCart(requireView(), myCart)

        mFirebaseViewModel.toCartResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
//                Toast.makeText(
//                    requireContext(), getString(R.string.board_created), Toast.LENGTH_SHORT
//                ).show()
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.board_creating_error), Toast.LENGTH_SHORT
                ).show()
            }
        }
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
            Log.d("MyApp", "Create button clicked") // Log the button click
            val nameItem = itemCreateBinding!!.etItemName.text.toString()
            val weight = itemCreateBinding!!.etItemWeight.text.toString()
            val priceText = itemCreateBinding!!.etItemPrice.text.toString()
            val price = if (priceText.isNotEmpty()) {
                priceText.toInt()
            } else {
                // Handle the case where the price is empty (e.g., show an error message)
                0 // You can change this default value to another suitable value or handle it as needed
            }

            val userLogin = mFirebaseViewModel.userLiveData.value!!.login.toString()

            if (TextUtils.isEmpty(nameItem) || TextUtils.isEmpty(weight) || price == 0 || TextUtils.isEmpty(
                    userLogin
                )
            ) {
                // Show a toast indicating that some fields are empty
                Toast.makeText(
                    requireContext(), getString(R.string.invalid_item_type), Toast.LENGTH_SHORT
                ).show()
                Log.d("MyApp", "Some fields are empty")

            } else {
                if (mSelectedImageFileUri != null) {
                    Log.d("MyApp", "getActiveOrders: $mSelectedImageFileUri")

                    // Fields are not empty, start uploading the image to Firestore storage
                    mFirebaseViewModel.uploadItemImage(requireContext(), mSelectedImageFileUri!!)
                    mFirebaseViewModel.imageUploadResult.observe(viewLifecycleOwner) { isSuccess ->
                        if (isSuccess != null) {
                            Log.d("MyApp", "Image selected: $mSelectedImageFileUri")
                            if (isSuccess) {
                                Log.d("MyApp", "Image uploaded successfully: $isSuccess")
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.image_uploaded),
                                    Toast.LENGTH_SHORT
                                ).show()
                                createItemData(itemCreateBinding!!) // Pass the binding here
                            } else {
                                Log.d("MyApp", "Image upload error")

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.image_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                } else {
                    // No image selected, show a toast indicating that an image is required
                    Toast.makeText(
                        requireContext(), getString(R.string.image_not_selected), Toast.LENGTH_SHORT
                    ).show()
                    Log.d("MyApp", "No image selected")
                }
                mSelectedImageFileUri = null
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
        liveUpdates()
        bottomSheetDialog!!.dismiss()
    }

    private fun createItemData(binding: FragmentItemCreateBinding) {
        Log.d("MyApp", "Creating item data")
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

        val mItemImageUrl = mFirebaseViewModel.imageUploadLive.value.toString()
        val userLogin = mFirebaseViewModel.userLiveData.value!!.login

        if (TextUtils.isEmpty(nameItem) || TextUtils.isEmpty(weight) || price == 0 || TextUtils.isEmpty(
                mItemType
            ) || TextUtils.isEmpty(
                userLogin
            )
        ) {
            // Show a toast indicating that some fields are empty
            Toast.makeText(
                requireContext(), getString(R.string.invalid_item_type), Toast.LENGTH_SHORT
            ).show()
            Log.d("MyApp", "Some fields are empty during item data creation")

        } else if (TextUtils.isEmpty(mItemImageUrl)) {
            // Show a toast indicating that the image is not selected
            Toast.makeText(
                requireContext(), getString(R.string.image_not_selected), Toast.LENGTH_SHORT
            ).show()
            Log.d("MyApp", "Image is not selected during item data creation")

        } else {
            val item = Item(
                imageItem = mItemImageUrl.toString(),
                nameItem = nameItem,
                weight = weight,
                price = price,
                category = mItemType,
                createdBy = userLogin,
            )

            // Call the ViewModel function to create the item
            mFirebaseViewModel.createItem(requireView(), item)
            mSelectedImageFileUri = null
            mFirebaseViewModel.resetItemResult()
        }
        mFirebaseViewModel.createItemResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess != null) {
                if (isSuccess) {
                    // Show a toast indicating that the board is created
                    Toast.makeText(
                        requireContext(), getString(R.string.board_created), Toast.LENGTH_SHORT
                    ).show()

                    // Hide the bottom dialog
                    hideBottomDialog()
                    mSelectedImageFileUri = null
//                    mFirebaseViewModel.resetItemResult()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.board_creating_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("MyApp", "Error during item creation")
                }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}