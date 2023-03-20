package com.riteshmaagadh.telejoin.ui.categorydetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.riteshmaagadh.telejoin.R
import com.riteshmaagadh.telejoin.data.adapters.GroupsAdapter
import com.riteshmaagadh.telejoin.data.models.Config
import com.riteshmaagadh.telejoin.data.models.Group
import com.riteshmaagadh.telejoin.databinding.ActivityCategoryDetailBinding
import com.riteshmaagadh.telejoin.ui.Utils
import com.riteshmaagadh.telejoin.ui.error.ErrorActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryDetailBinding
    private var isScrolling = false
    private var currentItems = 0
    private var totalItems = 0
    private var scrolledOutItems = 0
    private val collectionRef = FirebaseFirestore.getInstance()
        .collection("telegram_groups")
    private lateinit var lastDoc: DocumentSnapshot
    private lateinit var groupsAdapter: GroupsAdapter
    private val groupList: ArrayList<Group> = arrayListOf()
    private var rewardedAd: RewardedAd? = null
    private val adRequest = AdRequest.Builder().build()
    private var isAdRequired = true

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

            val layoutManager = LinearLayoutManager(this)

            binding.recyclerView.layoutManager = layoutManager

            try {
                FirebaseFirestore.getInstance()
                    .collection("config")
                    .document(                            "E1iB6yUEDBkf6dcBnlEl")
                    .get()
                    .addOnSuccessListener {
                        val config = it.toObject(Config::class.java)
                        isAdRequired = config?.ad_required!!
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            groupsAdapter = GroupsAdapter(groupList, this, object : GroupsAdapter.AdapterCallbacks {
                override fun onJoinButtonClicked(groupLink: String) {
                    if (isAdRequired) {
                        if (rewardedAd != null) {
                            rewardedAd?.show(this@CategoryDetailActivity
                            ) { reward ->
                                loadAd()
                            }
                            rewardedAd?.fullScreenContentCallback = object :
                                FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent()
                                    Utils.openWhatsapp(this@CategoryDetailActivity, groupLink)
                                }
                            }
                        } else {
                            Utils.openWhatsapp(this@CategoryDetailActivity, groupLink)
                        }
                    } else {
                        Utils.openWhatsapp(this@CategoryDetailActivity, groupLink)
                    }
                }
            })
            binding.recyclerView.adapter = groupsAdapter



            binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    currentItems = layoutManager.childCount
                    totalItems = layoutManager.itemCount
                    scrolledOutItems = layoutManager.findFirstVisibleItemPosition()

                    if (isScrolling && (currentItems + scrolledOutItems == totalItems)) {
                        isScrolling = false
                        fetchData()
                    }
                }
            })

            lifecycleScope.launch(Dispatchers.IO){
                collectionRef
                    .orderBy("index", Query.Direction.DESCENDING)
                    .whereEqualTo("active",true)
                    .whereEqualTo("category",title)
                    .limit(10)
                    .get()
                    .addOnSuccessListener {
                        if (it.documents.isNotEmpty()) {
                            lastDoc = it.documents[it.size() - 1]
                            val groups = it.toObjects(Group::class.java)
                            groupList.addAll(groups)
                            groupsAdapter.notifyDataSetChanged()
                            binding.progressBar.visibility = View.GONE
                        } else {
                            binding.emptyView.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                    .addOnFailureListener {
                        binding.progressBar.visibility = View.GONE
                        val intent = Intent(this@CategoryDetailActivity, ErrorActivity::class.java)
                        intent.putExtra("has_network_gone",false)
                        startActivity(intent)
                        finish()
                    }
            }

            loadAd()

        } else {
            val intent = Intent(this@CategoryDetailActivity, ErrorActivity::class.java)
            intent.putExtra("has_network_gone",true)
            startActivity(intent)
            finish()
        }



    }

    private fun fetchData() {
        lifecycleScope.launch(Dispatchers.IO) {
            collectionRef
                .orderBy("index", Query.Direction.DESCENDING)
                .whereEqualTo("active",true)
                .startAfter(lastDoc)
                .limit(10)
                .get()
                .addOnSuccessListener {
                    if (it.documents.isNotEmpty()) {
                        lastDoc = it.documents[it.size() - 1]
                        val groups = it.toObjects(Group::class.java)
                        groupList.addAll(groups)
                        groupsAdapter.notifyDataSetChanged()
                    }
                }
        }
    }


    private fun loadAd() {
        RewardedAd.load(this,getString(R.string.rewarded_ad_unit_id), adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
            }
        })
    }

}