package com.example.reviewstory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var locationPermissionGranted = false
    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null
    private val workManager = WorkManager.getInstance(application)
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        val controller = findNavController(R.id.navigation_host)

        NavigationUI.setupWithNavController(
            bottom_navigation, controller
        )
        val saveRequest =
            PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES)
                // Additional configuration
                .build()
        WorkManager.getInstance(this).enqueue(saveRequest)

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