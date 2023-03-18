package com.riteshmaagadh.whatsappgrouplinks.ui.categories

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshmaagadh.whatsappgrouplinks.R
import com.riteshmaagadh.whatsappgrouplinks.data.adapters.CategoriesAdapter
import com.riteshmaagadh.whatsappgrouplinks.data.adapters.GroupsAdapter
import com.riteshmaagadh.whatsappgrouplinks.data.models.Category
import com.riteshmaagadh.whatsappgrouplinks.data.models.Group
import com.riteshmaagadh.whatsappgrouplinks.databinding.FragmentCategoriesBinding
import com.riteshmaagadh.whatsappgrouplinks.ui.Utils
import com.riteshmaagadh.whatsappgrouplinks.ui.error.ErrorActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Utils.isOnline(requireContext())){
            binding.categoryRecyclerView.adapter =
                CategoriesAdapter(Utils.getCategories(), requireContext())
        } else {
            val intent = Intent(requireContext(), ErrorActivity::class.java)
            intent.putExtra("has_network_gone",true)
            startActivity(intent)
            requireActivity().finish()
        }




    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CategoriesFragment()
    }
}