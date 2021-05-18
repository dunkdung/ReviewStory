package com.example.reviewstory

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime

class LocationWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null
    // Use fields to define the data types to return.
    val placeFields: List<Place.Field> = listOf(Place.Field.NAME, Place.Field.ADDRESS)

    // Use the builder to create a FindCurrentPlaceRequest.
    val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        Places.initialize(applicationContext, "AIzaSyDTRY1lQAAW-WTWfbA_4KNcc30TFWWudDc")
        updatePlace()

        return Result.success()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePlace(){
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            Log.d("place", "백그라운드 실행")
            val placesClient = Places.createClient(applicationContext.applicationContext)
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    var stampinfo = STAMP()

                    stampinfo.address = response.placeLikelihoods[0].place.address
                    stampinfo.s_date = LocalDateTime.now().toString()
                    stampinfo.user_num = fbAuth?.currentUser?.uid
                    stampinfo.s_name = response.placeLikelihoods[0].place.name
                    stampinfo.places = response.placeLikelihoods

                    fbAuth?.currentUser?.email?.let { fbFirestore?.collection("user")?.document(it)?.collection("stamp")?.add(stampinfo) }
                    fbFirestore?.collection("stamp")?.add(stampinfo)

                    Log.d(
                        "place",
                        "Place '${response.placeLikelihoods[0].place.name}''${response.placeLikelihoods[0].place.address}''${response.placeLikelihoods[0].place.latLng}'"
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