package com.riteshmaagadh.telejoin.ui.categories

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.riteshmaagadh.telejoin.data.adapters.CategoriesAdapter
import com.riteshmaagadh.telejoin.databinding.FragmentCategoriesBinding
import com.riteshmaagadh.telejoin.ui.Utils
import com.riteshmaagadh.telejoin.ui.error.ErrorActivity

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