package com.example.fooddiary.Activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.fooddiary.Helper.DatePickerDialog
import com.example.fooddiary.Helper.MyApplication
import com.example.fooddiary.R
import com.example.fooddiary.fragment.HomeFragment
import com.example.fooddiary.fragment.ReviewDetailFragment
import com.example.fooddiary.fragment.ReviewFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var homeFragment: HomeFragment
    lateinit var reviewFragment: ReviewFragment


    private var barYear: Int? = null
    private var barMonth: Int? = null

    var drawerToggle: ActionBarDrawerToggle? = null
    private var mToolBarNavigationListenerIsRegistered = false


    companion object {

        lateinit var instance : MainActivity

        fun getInstancem() : MainActivity {

            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        MainActivity.instance = this


        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false);



        drawerToggle =  ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerToggle!!.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle!!)
        drawerToggle!!.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.getMenu().getItem(0).setChecked(true);
        homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()


        //name, email
        val navHeaderView = nav_view.getHeaderView(0)
        val tvHeaderName =  navHeaderView.findViewById<TextView>(R.id.user_name_text)
        val tvHeaderEmail = navHeaderView.findViewById<TextView>(R.id.user_email_text)

        val userEmail = intent.getStringExtra("email")
        MyApplication.prefs.setString("email", userEmail)
        tvHeaderName.setText(intent.getStringExtra("name"))
        tvHeaderEmail.setText(userEmail)


        //플로팅 버튼
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            homeFragment.newPost()
        }


        setNowDateOnBar()
        //날짜 선택
        select_date_layout.setOnClickListener {

            val focusDialog = DatePickerDialog(this)
            focusDialog.setDialogListener(object : DatePickerDialog.CustomDialogListener {
                override fun onPositiveClicked(isOk: Boolean, year: Int, month: Int) {
                    if (isOk) {
                        println("저장")
                        barYear = year
                        barMonth = month

                        if(fab.visibility == View.VISIBLE){
                            homeFragment.setHomeDate(year.toString(), month.toString())
                        }
                        bar_month_text.text = "" + year + "년 " + month + "월"
                    }
                }
            })

            focusDialog.showDialog(barYear!!, barMonth!!)
        }

        app_bar_sort_btn.setOnClickListener {
            System.out.println("클릭ㅇㅇㅇ")


            val popup = PopupMenu(this@MainActivity, app_bar_sort_btn)
            popup.inflate(R.menu.sort_item)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->

                System.out.println("클릭")
                when (item.itemId) {
                    R.id.newest -> {
                        //handle menu1 click
                        supportFragmentManager.findFragmentById(R.id.frame_layout)?.let {
                            // the fragment exists
                            when(it){
                                is HomeFragment -> {
                                    homeFragment.setOrder("DESC")
                                }
                                is ReviewFragment -> {
                                    reviewFragment.setOrder("DESC")
                                }
                                is ReviewDetailFragment -> {

                                }
                            }

                        }
                        true
                    }
                    R.id.oldest -> {

                        supportFragmentManager.findFragmentById(R.id.frame_layout)?.let {
                            // the fragment exists
                            when(it){
                                is HomeFragment -> {
                                    (it as (HomeFragment)).setOrder("ASC")
                                }
                                is ReviewFragment -> {
                                    (it as (ReviewFragment)).setOrder("ASC")
                                }
                                is ReviewDetailFragment -> {
                                    (it as (ReviewDetailFragment)).setOrder("ASC")
                                }
                            }

                        }

                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        supportFragmentManager.addOnBackStackChangedListener {

            if (supportFragmentManager.backStackEntryCount == 0) {
                enableViews(false)
                setActionBarTitle("리뷰")
            } else {
                enableViews(true)
            }
        }
    }

    private fun enableViews(enable: Boolean) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (enable) {
            //You may not want to open the drawer on swipe from the left in this case
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            // Remove hamburger
            drawerToggle!!.setDrawerIndicatorEnabled(false)
            // Show back button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                drawerToggle!!.setToolbarNavigationClickListener(View.OnClickListener { // Doesn't have to be onBackPressed
                    onBackPressed()
                })
                mToolBarNavigationListenerIsRegistered = true
            }
        } else {
            //You must regain the power of swipe for the drawer.
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

            // Remove back button
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            // Show hamburger
            drawerToggle!!.setDrawerIndicatorEnabled(true)
            // Remove the/any drawer toggle listener
            drawerToggle!!.setToolbarNavigationClickListener(null)
            mToolBarNavigationListenerIsRegistered = false
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        setNowDateOnBar()
        when(item.itemId){
            R.id.nav_home -> {
                getSupportActionBar()!!.setDisplayShowTitleEnabled(false)
                fab.visibility = View.VISIBLE
                select_date_layout.visibility = View.VISIBLE
            }
            else -> {
                getSupportActionBar()!!.setDisplayShowTitleEnabled(true)
                fab.visibility = View.INVISIBLE
                select_date_layout.visibility = View.INVISIBLE
            }
        }

        when (item.itemId){
            R.id.nav_home -> {

                homeFragment = HomeFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, homeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            }
            R.id.nav_review -> {
                setActionBarTitle("리뷰")
                reviewFragment = ReviewFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, reviewFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        System.out.println("초기화 액티비티")
        refreshHome(requestCode, resultCode, data)

    }

    public fun refreshHome(requestCode: Int, resultCode: Int, data: Intent?){
        homeFragment.onActivityResult(requestCode, resultCode, data)
    }

    fun setNowDateOnBar(){
        val nowYear = SimpleDateFormat("yyyy", Locale.KOREA).format(Date())
        val nowMonth = SimpleDateFormat("M", Locale.KOREA).format(Date())
        barYear = Integer.parseInt(nowYear)
        barMonth = Integer.parseInt(nowMonth)
        bar_month_text.text = "" + nowYear + "년 " + nowMonth + "월"
    }

    override fun onBackPressed() {
        val manager: FragmentManager = supportFragmentManager
        if(manager.backStackEntryCount > 0){
            manager.popBackStack()
        }else{
            if(drawer_layout.isDrawerOpen(GravityCompat.START)){
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            super.onBackPressed()
        }


    }

    fun setActionBarTitle(title: String?) {
        getSupportActionBar()!!.setTitle(title)

    }
}