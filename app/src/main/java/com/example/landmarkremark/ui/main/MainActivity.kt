package com.example.landmarkremark.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.landmarkremark.R
import com.example.landmarkremark.models.LocationData
import com.example.landmarkremark.ui.collections.CollectionsFragment
import com.example.landmarkremark.ui.profile.ProfileFragment
import com.example.landmarkremark.ui.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {


    /**
     * MainActivity's fragments
     */
    private val searchFragment: SearchFragment by lazy {
        SearchFragment()
    }
    private val collectionsFragment: CollectionsFragment by lazy {
        CollectionsFragment()
    }
    private val profileFragment: ProfileFragment by lazy {
        ProfileFragment()
    }

    private lateinit var mainViewModel: MainViewModel

    lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setActionBar()
        setViewModels()
        setNavController()
//        setMenu()
    }

    override fun onStart() {
        super.onStart()
    }

    private fun setViewModels() {
        val locations = intent.getParcelableArrayListExtra<LocationData>(LocationData::class.java.simpleName)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mainViewModel.setLocations(locations)
        mainViewModel.getLocations().observe(this, Observer {
            Timber.d("getLocations - main: ${it?.size}")

        })

//        mainViewModel.writeNote()
//        mainViewModel.getLocations()
    }

    /**
     * Set Navigation Controllers
     */
    private fun setNavController() {
        // Set selected item to be live view
        main_bottom_nav_view.selectedItemId = R.id.navigation_bottom_search

        // Set drawer's item onSelectedListener
        main_bottom_nav_view.setOnNavigationItemSelectedListener(this)
        main_nav_view.setNavigationItemSelectedListener(this)

        // Set active fragment to live fragment
        activeFragment = searchFragment
        supportFragmentManager.beginTransaction().add(R.id.main_fragment_container, activeFragment).commit()

        setActionBarButtons(R.id.navigation_bottom_search)
    }

    /**
     * Set action bar buttons onClick and drawable
     * Set drawer's menu to select the current bottom navigation tab
     */
    private fun setActionBarButtons(itemId: Int) {
        when (itemId) {
            R.id.navigation_bottom_search -> {
                main_nav_view.menu.findItem(R.id.navigation_search).isChecked = true
//                replaceActiveFragment(searchFragment, itemId)
            }

            R.id.navigation_bottom_collections -> {
                main_nav_view.menu.findItem(R.id.navigation_collections).isChecked = true
//                replaceActiveFragment(collectionsFragment, itemId)
            }

            R.id.navigation_bottom_profile -> {
                main_nav_view.menu.findItem(R.id.navigation_profile).isChecked = true
            }

        }
    }

    /**
     * Set all navigation item selected
     * Drawer's navigation item will just call their bottom navigation's counterpart
     * Bottom navigation items will replace the current active fragment
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Timber.d("checkFragment - onNavigationItemSelected: $item")

        main_drawer_layout.closeDrawers()
        when (item.itemId) {
            R.id.navigation_bottom_search -> {
                Timber.d("checkFragment - search")
                replaceActiveFragment(searchFragment, item.itemId)
            }
            R.id.navigation_bottom_collections -> {
                Timber.d("checkFragment - collection")
                replaceActiveFragment(collectionsFragment, item.itemId)
            }
            R.id.navigation_bottom_edit -> {

            }
            R.id.navigation_bottom_profile -> {

            }

        }
        return true
    }

    /**
     * Replace active fragment with the fragment selected
     */
    private fun replaceActiveFragment(fragment: Fragment, itemId: Int) {
        if (activeFragment != fragment) {
            if (supportFragmentManager.fragments.contains(fragment)) {
                // Show fragment if supportFragmentManager contains it
                supportFragmentManager.beginTransaction().show(fragment).hide(activeFragment).commit()
            } else {
                // Add fragment if supportFragmentManager doesn't contain it
                supportFragmentManager.beginTransaction().add(R.id.main_fragment_container, fragment).hide(activeFragment).commit()
            }

            activeFragment = fragment
        }
        setActionBarButtons(itemId)
    }
}
