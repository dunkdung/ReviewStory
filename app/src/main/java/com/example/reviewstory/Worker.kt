package com.example.reviewstory

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import kotlin.math.ceil

class LocationWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null
    // Use fields to define the data types to return.
    val placeFields: List<Place.Field> = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
    val fusedLocationClient: FusedLocationProviderClient
            = LocationServices.getFusedLocationProviderClient(appContext)
    // Use the builder to create a FindCurrentPlaceRequest.
    val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val sharedPref = applicationContext.getSharedPreferences("test", MODE_PRIVATE)
        val editor =sharedPref.edit()
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        Places.initialize(applicationContext, "AIzaSyDTRY1lQAAW-WTWfbA_4KNcc30TFWWudDc")
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                applicationContext as Activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1);
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if (location != null) {
                var latitude = sharedPref.getString("lat","0")?.toDouble()
                var longitude = sharedPref.getString("lon","0")?.toDouble()

                if ((latitude != ceil(location.latitude*1000000) /1000000) or (longitude != ceil(location.longitude*1000000) /1000000)){
                    Log.d("place", "Location: $latitude")
                    Log.d("place", "Location is Updated $latitude -> ${location.latitude},,, $longitude ->${location.longitude}")
                    latitude = ceil(location.latitude*1000000) /1000000
                    longitude = ceil(location.longitude*1000000) /1000000
                    editor.putString("lat", "$latitude")
                    editor.putString("lon", "$longitude")
                    editor.apply()
                    Log.d("place", "latitude -> ${sharedPref.getString("lat","0")}")
                    updatePlace()
                }
                else{
                    Log.d("place", "Location is Not change ${sharedPref.getString("lat","0")}, ${sharedPref.getString("lon","0")}")
                    updatePlace()
                }

            }
        }
        return Result.success()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePlace(){
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            Log.d("place", "백그라운드 실행")
            val sharedPref = applicationContext.getSharedPreferences("test", MODE_PRIVATE)
            val editor =sharedPref.edit()
            val placesClient = Places.createClient(applicationContext.applicationContext)
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    var num = sharedPref.getInt("s_num",0)
                    var stampinfo = STAMP()
                    stampinfo.address = response.placeLikelihoods[0].place.address
                    stampinfo.s_date = LocalDateTime.now().toString()
                    stampinfo.user_num = fbAuth?.currentUser?.uid
                    stampinfo.s_name = response.placeLikelihoods[0].place.name
                    stampinfo.places = response.placeLikelihoods
                    stampinfo.s_num = fbAuth?.currentUser?.uid + "$num"
                    editor.putInt("s_num", num + 1)
                    editor.apply()
                    fbAuth?.currentUser?.email?.let { fbFirestore?.collection("user")?.document(it)?.collection("stamp")?.add(stampinfo) }
                    fbFirestore?.collection("stamp")?.add(stampinfo)
                    Log.d(
                        "place",
                        "Place '${response.placeLikelihoods[0].place.name}''${num}''${response.placeLikelihoods[0].place.latLng}'"
                    )
                }
            }
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            Log.d("place", "Place not found")
            ActivityCompat.requestPermissions(
                applicationContext as Activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1);
        }
    }
}
