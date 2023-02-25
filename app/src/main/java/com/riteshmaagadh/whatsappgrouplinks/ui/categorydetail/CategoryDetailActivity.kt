package com.riteshmaagadh.whatsappgrouplinks.ui.categorydetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshmaagadh.whatsappgrouplinks.data.adapters.GroupsAdapter
import com.riteshmaagadh.whatsappgrouplinks.data.models.Group
import com.riteshmaagadh.whatsappgrouplinks.databinding.ActivityCategoryDetailBinding
import com.riteshmaagadh.whatsappgrouplinks.ui.Utils
import com.riteshmaagadh.whatsappgrouplinks.ui.error.ErrorActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Utils.isOnline(this)){
            binding.progressBar.visibility = View.VISIBLE

            val title = intent.extras?.getString("category_title")!!
            binding.categoryTitleTv.text = title

            binding.backArrow.setOnClickListener {
                finish()
            }

            binding.recyclerView.addItemDecoration(
                DividerItemDecoration(
                    this,
                    LinearLayoutManager.VERTICAL
                )
            )

            lifecycleScope.launch(Dispatchers.IO){
                FirebaseFirestore.getInstance()
                    .collection("whatsapp_groups")
                    .whereEqualTo("category",title)
//                    .whereEqualTo("active", true)
                    .get()
                    .addOnSuccessListener {
                        val list = it.toObjects(Group::class.java)
                        if (list.isNullOrEmpty()){
                            binding.emptyView.visibility = View.VISIBLE
                        } else {
                            binding.recyclerView.adapter = GroupsAdapter(list, this@CategoryDetailActivity)
                            binding.emptyView.visibility = View.GONE
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        binding.progressBar.visibility = View.GONE
                        val intent = Intent(this@CategoryDetailActivity, ErrorActivity::class.java)
                        intent.putExtra("has_network_gone",false)
                        startActivity(intent)
                        finish()
                    }
            }
        } else {
            val intent = Intent(this@CategoryDetailActivity, ErrorActivity::class.java)
            intent.putExtra("has_network_gone",true)
            startActivity(intent)
            finish()
        }



    }
}