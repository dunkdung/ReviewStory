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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var locationPermissionGranted = false
    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null
    private val workManager = WorkManager.getInstance(application)
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidThreeTen.init(this);
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLocationPermission()
        val controller = findNavController(R.id.navigation_host)

        NavigationUI.setupWithNavController(
            bottom_navigation, controller
        )
        val saveRequest =
            PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES)
                // Additional configuration
                .build()
        WorkManager.getInstance(this).enqueue(saveRequest)


        var user = USER()
        user.user_id = fbAuth?.currentUser?.email
        user.user_num = fbAuth?.currentUser?.uid


        fbFirestore?.collection("user")
            ?.add(user)

        fbFirestore?.collection("user")
            ?.whereEqualTo("user_id", fbAuth?.currentUser?.email)
            ?.get()
            ?.addOnSuccessListener { result ->
                for (document in result) {
                    var userInfo = USER()
                    var email : String? = null
                    Log.d("구글 로그인", "확인용")
                    if (document.data["user_nick"] as String? == null) {
                        userInfo.user_num = fbAuth?.uid
                        userInfo.user_id = fbAuth?.currentUser?.email
                        for(i in fbAuth?.currentUser?.email.toString()){
                            if(i.toString() == "@")
                            {
                                break
                            }
                            email = email + i.toString()


                        }
                        email = email?.substring(4)
                        userInfo.user_nick = email
                        fbFirestore?.collection("user")?.document(userInfo.user_num.toString())
                            ?.set(userInfo)
                        Log.d("구글 로그인2", userInfo.user_nick.toString())
                    }
                }
            }

    }



    private fun getLocationPermission(){
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("place", " get Permission Main")
            locationPermissionGranted = true
        } else {
            Log.d("place", "Permission not found Main")
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1);
        }
    }
}