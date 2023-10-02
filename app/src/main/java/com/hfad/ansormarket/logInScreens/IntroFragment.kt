package com.hfad.ansormarket.logInScreens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.AdRequest
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.FragmentIntroBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel

class IntroFragment : Fragment() {

    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentIntroBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

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

        binding.regInfoBtn.setOnClickListener {
            success()
        }

        return (binding.root)
    }

    fun success() {
        if (activity is LogMainActivity) {
            (activity as LogMainActivity).intentRegLog()
            Log.d("TAG", "being called2")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}