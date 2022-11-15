package com.riteshmaagadh.whatsappgrouplinks.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.riteshmaagadh.whatsappgrouplinks.R
import com.riteshmaagadh.whatsappgrouplinks.databinding.ActivityMainBinding
import com.riteshmaagadh.whatsappgrouplinks.ui.addlink.AddLinkFragment
import com.riteshmaagadh.whatsappgrouplinks.ui.categories.CategoriesFragment
import com.riteshmaagadh.whatsappgrouplinks.ui.home.HomeFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        switchScreen(HomeFragment.newInstance())

        binding.bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> switchScreen(HomeFragment.newInstance())
                R.id.nav_categories -> switchScreen(CategoriesFragment.newInstance())
                R.id.nav_add_link -> switchScreen(AddLinkFragment.newInstance())
                else -> { false }
            }
        })

        binding.bottomNavigationView.setOnItemReselectedListener {

        }

    }

    private fun switchScreen(fragment: Fragment?): Boolean {
        return if (fragment == null) {
            false
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss()
            true
        }
    }

}