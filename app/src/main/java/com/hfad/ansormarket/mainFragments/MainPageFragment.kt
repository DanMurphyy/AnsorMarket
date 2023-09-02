package com.hfad.ansormarket.mainFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.FragmentMainPageBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.logInScreens.LogMainActivity

class MainPageFragment : Fragment() {

    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()


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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}