package com.example.reviewstory.timeline

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.reviewstory.R
import com.example.reviewstory.STAMP
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_timeline.*
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import java.time.LocalDateTime
import java.util.*


class TimelineFragment : Fragment() {

    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null

    // Use fields to define the data types to return.
    val placeFields: List<Place.Field> = listOf(Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG)

    // Use the builder to create a FindCurrentPlaceRequest.
    val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 위치정보 저장
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        activity?.let { Places.initialize(it.applicationContext, "AIzaSyDTRY1lQAAW-WTWfbA_4KNcc30TFWWudDc") }


        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        //updatePlace()

        // 장소 데이터 받아오기(시간 조건으로)
        var startDate: String? = null
        var endDate: String? = null
        startDate = "2021-04-30T09:14:12.668"
        endDate = "2021-05-05T19:20:07.610"

        fbFirestore?.collection("stamp")
                ?.whereGreaterThanOrEqualTo("s_date", startDate)
                ?.whereLessThan("s_date", endDate)
                ?.get()
                ?.addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("place", "${document.id} => ${document.data}")
                    }
                }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePlace(){
        if (ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("place", "Plac")
            val placesClient = Places.createClient(requireActivity().applicationContext)
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    var stampinfo = STAMP()

                    stampinfo.address = response.placeLikelihoods[0].place.address
                    stampinfo.s_date = LocalDateTime.now().toString()
                    stampinfo.user_num = fbAuth?.currentUser?.uid
                    stampinfo.s_name = response.placeLikelihoods[0].place.name

                    fbFirestore?.collection("stamp")?.add(stampinfo)

                    Log.d(
                            "place",
                            "Place '${response.placeLikelihoods[0].place.name}''${response.placeLikelihoods[0].place.address}''${response.placeLikelihoods[0].place.latLng}'"
                    )
                    text_timeline.text = "${response.placeLikelihoods[0].place.name}\n${response.placeLikelihoods[0].place.address}\n${response.placeLikelihoods[0].place.latLng}"
                }
            }
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            Log.d("place", "Place not found")
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 1);
        }
    }
}