package com.example.reviewstory

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.example.reviewstory.main.MainFragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureBottomNavigation()

        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        if(true)
        {
            var userInfo = USER()
            var stampinfo = STAMP()
            var timelineinfo = TIMELINE()
            var followinfo = FOLLOWLIST()

            userInfo.user_num = fbAuth?.uid
            userInfo.user_id = fbAuth?.currentUser?.email

            fbFirestore?.collection("user")?.document(userInfo.user_id.toString())?.set(userInfo)
            fbFirestore?.collection("stamp")?.document(stampinfo.s_num.toString())?.set(stampinfo)
            fbFirestore?.collection("timeline")?.document(timelineinfo.tl_num.toString())?.set(timelineinfo)
            fbFirestore?.collection("followlist")?.document(followinfo.fol_num.toString())?.set(followinfo)



        }
    }

    private fun configureBottomNavigation() {
        vp_ac_main_frag_pager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 4)

        tl_ac_main_bottom_menu.setupWithViewPager(vp_ac_main_frag_pager)
        val bottomNaviLayout: View =
            this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        tl_ac_main_bottom_menu.getTabAt(0)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(1)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_search_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(2)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_add_tab) as RelativeLayout
        tl_ac_main_bottom_menu.getTabAt(3)!!.customView =
            bottomNaviLayout.findViewById(R.id.btn_bottom_navi_like_tab) as RelativeLayout
    }
}