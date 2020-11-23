package com.example.landmarkremark.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.landmarkremark.R
import com.example.landmarkremark.models.LocationData
import com.example.landmarkremark.ui.collections.CollectionsFragment
import com.example.landmarkremark.ui.profile.ProfileFragment
import com.example.landmarkremark.ui.explore.ExploreFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val ADD_LOCATION_NOTE_REQUEST_CODE = 100
    }

    /**
     * MainActivity's fragments
     */
    private val exploreFragment: ExploreFragment by lazy {
        ExploreFragment()
    }
    private val collectionsFragment: CollectionsFragment by lazy {
        CollectionsFragment()
    }
    private val profileFragment: ProfileFragment by lazy {
        ProfileFragment()
    }

    private lateinit var mainViewModel: MainViewModel

    private lateinit var activeFragment: Fragment

    /**
     * Action bar logo and title
     */
    private lateinit var actionBarLogo: ImageView
    private lateinit var actionBarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar()
        setViewModels()
        setNavController()
    }

    private fun setViewModels() {
        val locations =
            intent.getParcelableArrayListExtra<LocationData>(LocationData::class.java.simpleName)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.init()
        mainViewModel.setLocations(locations)
    }

    private fun setActionBar() {
        val actionBar = main_toolbar as MaterialToolbar
        actionBarLogo = actionBar.findViewById<ImageView>(R.id.action_bar_img_title)
        actionBarTitle = actionBar.findViewById<TextView>(R.id.action_bar_text_title)
        setSupportActionBar(actionBar)
    }

    /**
     * Set Navigation Controllers
     */
    private fun setNavController() {
        // Set selected item to be explore fragment
        main_bottom_nav_view.selectedItemId = R.id.navigation_bottom_explore

        // Set bottom navigation item onSelectedListener
        main_bottom_nav_view.setOnNavigationItemSelectedListener(this)

        // Set active fragment to exploreFragment
        activeFragment = exploreFragment
        supportFragmentManager.beginTransaction().add(R.id.main_fragment_container, activeFragment)
            .commit()

        setActionBarButtons(R.id.navigation_bottom_explore)
    }

    /**
     * Set action bar buttons onClick and drawable
     * Set drawer's menu to select the current bottom navigation tab
     */
    private fun setActionBarButtons(itemId: Int) {
        when (itemId) {
            R.id.navigation_bottom_explore -> {
                actionBarLogo.setImageResource(R.drawable.ic_explore)
                actionBarTitle.text = getString(R.string.title_menu_explore)
            }
            R.id.navigation_bottom_collections -> {
                actionBarLogo.setImageResource(R.drawable.ic_collections)
                actionBarTitle.text = getString(R.string.title_menu_collections)
            }
            R.id.navigation_bottom_profile -> {
                actionBarLogo.setImageResource(R.drawable.ic_profile)
                actionBarTitle.text = getString(R.string.title_menu_profile)
            }
        }
    }

    /**
     * Set all navigation item selected
     * Drawer's navigation item will just call their bottom navigation's counterpart
     * Bottom navigation items will replace the current active fragment
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        main_drawer_layout.closeDrawers()
        when (item.itemId) {
            R.id.navigation_bottom_explore -> {
                replaceActiveFragment(exploreFragment, item.itemId)
            }
            R.id.navigation_bottom_collections -> {
                replaceActiveFragment(collectionsFragment, item.itemId)
            }
            R.id.navigation_bottom_profile -> {
                replaceActiveFragment(profileFragment, item.itemId)
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
                supportFragmentManager.beginTransaction().show(fragment).hide(activeFragment)
                    .commit()
            } else {
                // Add fragment if supportFragmentManager doesn't contain it
                supportFragmentManager.beginTransaction()
                    .add(R.id.main_fragment_container, fragment).hide(activeFragment).commit()
            }
            activeFragment = fragment
        }
        setActionBarButtons(itemId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ADD_LOCATION_NOTE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    mainViewModel.getLocations()
                }
            }
        }
    }

    fun goToExploreFragment(locationData: LocationData? = null) {
        if (activeFragment != exploreFragment) {
            exploreFragment.showLocationInfo(locationData)
            main_bottom_nav_view.findViewById<View>(R.id.navigation_bottom_explore).callOnClick()
        }
    }
}
