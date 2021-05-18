package com.example.reviewstory

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.reviewstory.main.MainFragmentStatePagerAdapter
import com.example.reviewstory.timeline.StampsFragment
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_timeline.*
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private var locationPermissionGranted = false
    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        val controller = findNavController(R.id.navigation_host)
        /*Navigation을 세팅합니다. */
        NavigationUI.setupActionBarWithNavController(
            this,
            controller,
            /* Login 화면과 검색 화면에서 뒤로가기를 없앱니다.*/
            AppBarConfiguration.Builder(R.id.mypageFragment2, R.id.settingsFragment, R.id.searchFragment, R.id.timelineFragment).build()
        )

        NavigationUI.setupWithNavController(
            bottom_navigation, controller
        )

        getLocationPermission()
        if(true)
        {
            var userInfo = USER()

            userInfo.user_num = fbAuth?.uid
            userInfo.user_id = fbAuth?.currentUser?.email

            fbFirestore?.collection("user")?.document(userInfo.user_id.toString())?.set(userInfo)
        }
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getLocationPermission(){
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("place", " get Permission")
            locationPermissionGranted = true
        } else {
            Log.d("place", "Permission not found")
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1);
        }
    }
}