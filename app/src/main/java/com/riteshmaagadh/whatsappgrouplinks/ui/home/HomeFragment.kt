package com.riteshmaagadh.whatsappgrouplinks.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.riteshmaagadh.whatsappgrouplinks.data.adapters.GroupsAdapter
import com.riteshmaagadh.whatsappgrouplinks.data.models.Group
import com.riteshmaagadh.whatsappgrouplinks.databinding.FragmentHomeBinding
import com.riteshmaagadh.whatsappgrouplinks.ui.Utils
import com.riteshmaagadh.whatsappgrouplinks.ui.error.ErrorActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Utils.isOnline(requireContext())){
            binding.progressBar.visibility = View.VISIBLE

            binding.recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )

            lifecycleScope.launch(Dispatchers.IO){
                FirebaseFirestore.getInstance()
                    .collection("whatsapp_groups")
                    .orderBy("index", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {
                        binding.recyclerView.adapter = GroupsAdapter(it.toObjects(Group::class.java), requireContext())
                        binding.progressBar.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        binding.progressBar.visibility = View.GONE
                        val intent = Intent(requireContext(), ErrorActivity::class.java)
                        intent.putExtra("has_network_gone",false)
                        startActivity(intent)
                        requireActivity().finish()
                    }
            }

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
            HomeFragment()
    }
}