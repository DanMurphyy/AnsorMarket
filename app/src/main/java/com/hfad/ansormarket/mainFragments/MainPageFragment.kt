package com.hfad.ansormarket.mainFragments

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.hfad.ansormarket.R
import com.hfad.ansormarket.SharedViewModel
import com.hfad.ansormarket.adapters.ItemListAdapter
import com.hfad.ansormarket.databinding.FragmentMainPageBinding
import com.hfad.ansormarket.databinding.ItemInfoLayoutBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.models.Item
import com.hfad.ansormarket.models.MyCart
import jp.wasabeef.recyclerview.animators.LandingAnimator

@Suppress("DEPRECATION")
class MainPageFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var itemInfoBinding: ItemInfoLayoutBinding? = null
    private var mDialog: Dialog? = null
    private val adapter: ItemListAdapter by lazy { ItemListAdapter() }

    private var currentSearchQuery: String? = null
    private var currentCategory: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        @Suppress("DEPRECATION", "DEPRECATION", "DEPRECATION")
        setHasOptionsMenu(true)
        mFirebaseViewModel.loadUserData(requireContext())
        mFirebaseViewModel.fetchAllItems()
        showRecyclerView()

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveUpdates()
        filterAndCategory()
    }

    private fun filterAndCategory() {
        val chipGroup = binding.chipGroup
        val chipIds = listOf(
            R.id.showAll,
            R.id.Ichimliklar,
            R.id.qandolat_mahsulotlari,
            R.id.Sut_Mahsulotlari,
            R.id.Tortlar_va_Pishiriqlar,
            R.id.Konservalar,
            R.id.Snaklar,
            R.id.Mevalar,
            R.id.Bolalar_Ovqatlari,
            R.id.Un_Mahsulotlari,
            R.id.Souslar,
            R.id.Gosht,
            R.id.Gigiena_vositalari,
            R.id.Oshxona,
            R.id.Yoglar
        )


        // Set click listeners for each chip to handle the selection manually
        // Set click listeners for each chip to handle the selection manually
        for (chipId in chipIds) {
            val chip = chipGroup.findViewById<Chip>(chipId)
            chip.setOnClickListener { view ->
                // Iterate through the chips and uncheck all except the clicked one
                for (id in chipIds) {
                    val currentChip = chipGroup.findViewById<Chip>(id)
                    Log.d("chips", "selectedChip: $currentChip")
                    currentChip.isChecked = currentChip == view
                }

                if (chipId == R.id.showAll) {
                    currentCategory = null
                    allItem()
                } else {
                    val categoryText = (view as Chip).text.toString()
                    currentCategory = categoryText
                    category(categoryText)
                    applyCategoryFilter(categoryText)
                }
            }
        }
    }

    private fun allItem() {
        mFirebaseViewModel.itemList.observe(viewLifecycleOwner) { itemList ->
            adapter.setItems(itemList)
        }
    }

    private fun category(categoryText: String) {
        val categoryText = categoryText.trim()
        val filteredItems = mFirebaseViewModel.itemList.value?.filter { item ->
            val itemCategory = item.category.trim()
            val isCategoryMatch = itemCategory.equals(categoryText, ignoreCase = true)
            // Log the item's name, category, and match status
            Log.d(
                "chips",
                "Item Name: ${item.nameItem}, Category: $itemCategory, Match: $isCategoryMatch"
            )
            isCategoryMatch
        }

        filteredItems?.let {
            adapter.setItems(it)
            Log.d("chips", "Selected category items: $it")
        }
    }

    private fun liveUpdates() {
        mFirebaseViewModel.itemList.observe(viewLifecycleOwner) { itemList ->
            filteredItems = itemList.filter { item ->
                currentCategory?.let {
                    val itemCategory = item.category.trim()
                    itemCategory.equals(it, ignoreCase = true)
                } ?: true
            }
            adapter.setItems(filteredItems)
        }
        mFirebaseViewModel.myCartsLiveData.observe(viewLifecycleOwner) { myCartList ->
            adapter.setMyCartData(myCartList)
            mSharedViewModel.cartItemCount(requireView(), myCartList)
        }
        mFirebaseViewModel.fetchMyCart()
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
        liveUpdates()
        mDialog!!.dismiss()
    }

    private var filteredItems: List<Item> = emptyList()


    private fun applyCategoryFilter(categoryText: String) {
        val filteredItems = mFirebaseViewModel.itemList.value?.filter { item ->
            val itemCategory = item.category.trim()
            val isCategoryMatch = itemCategory.equals(categoryText, ignoreCase = true)
            Log.d(
                "chips",
                "Item Name: ${item.nameItem}, Category: $itemCategory, Match: $isCategoryMatch"
            )
            isCategoryMatch
        }

        filteredItems?.let {
            this.filteredItems = it // Update the filteredItems property
            adapter.setItems(it)
            Log.d("chips", "Selected category items: $it")
        }
    }

    private fun toCart(currentItem: Item, quantity: Int) {
        val newAmount = currentItem.price * quantity

        val userId = mFirebaseViewModel.getCurrentUserId() // Assuming userId is not empty here

        val myCart = MyCart(
            currentItem,
            quantity,
            userId,
            amount = newAmount
        )

        mFirebaseViewModel.toCart(requireView(), myCart)

        mFirebaseViewModel.toCartResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
//                Toast.makeText(
//                    requireContext(), getString(R.string.board_created), Toast.LENGTH_SHORT
//                ).show()
                currentCategory?.let { applyCategoryFilter(it) }
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.board_creating_error), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_page_add, menu)
        val search: MenuItem = menu.findItem(R.id.menu_search)
        val searchView: SearchView? = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_add_item -> Toast.makeText(
                requireContext(),
                "nimadur boladi",
                Toast.LENGTH_SHORT
            ).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrBlank()) {
            searchThroughDatabase(query)
        }

        return true
    }


    override fun onQueryTextChange(query: String?): Boolean {
        if (!query.isNullOrBlank()) {
            searchThroughDatabase(query)
        } else if (!currentSearchQuery.isNullOrBlank()) {
            searchThroughDatabase(currentSearchQuery!!)
        }

        return true
    }

    private fun searchThroughDatabase(query: String) {
        currentSearchQuery = query.trim()
        val filteredItems = mFirebaseViewModel.itemList.value?.filter { item ->
            item.nameItem.contains(currentSearchQuery ?: "", ignoreCase = true)
        }

        filteredItems?.let {
            adapter.setItems(it)
        }
    }


}