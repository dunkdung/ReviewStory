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

class TimelineFragment : Fragment() {

    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null

    private lateinit var FusedLocationProviderClient: FusedLocationProviderClient

    //장치가 현재 위치한 지리적 위치입니다. 즉, 마지막으로 알려진
    //Fused Location Provider에서 검색한 위치입니다.
    private var mLastKnownLocation: Location? = null
    private var mCameraPosition: CameraPosition? = null
    // Use fields to define the data types to return.
    val placeFields: List<Place.Field> = listOf(Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG)

    // Use the builder to create a FindCurrentPlaceRequest.
    val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()



        activity?.let { Places.initialize(it.applicationContext, "AIzaSyDTRY1lQAAW-WTWfbA_4KNcc30TFWWudDc") }

        // FusedLocationProviderClient 구성
        FusedLocationProviderClient = activity?.let {
            LocationServices.getFusedLocationProviderClient(
                it
            )
        }!!

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            Log.d("place", "Plac")
            val placesClient = Places.createClient(requireActivity().applicationContext)
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    if (true) {
                        var stampinfo = STAMP()

                        stampinfo.address = response.placeLikelihoods[0].place.address
                        stampinfo.s_date = LocalDateTime.now().toString()
                        stampinfo.user_num = fbAuth?.currentUser?.uid
                        stampinfo.s_name = response.placeLikelihoods[0].place.name

                        fbFirestore?.collection("stamp")?.document(stampinfo.s_num.toString())
                            ?.set(stampinfo)
                        fbFirestore?.collection("stamp")?.get()
                            ?.addOnSuccessListener { result ->
                                for (document in result) {
                                    Log.d("place", "${document.id} => ${document.data}")
                                }
                            }
                    }else Log.d("place","저장 실패")
                    Log.d(
                          "place",
                          "Place '${response.placeLikelihoods[0].place.name}''${response.placeLikelihoods[0].place.address}''${response.placeLikelihoods[0].place.latLng}'"
                    )
                    text_timeline.text = "${response.placeLikelihoods[0].place.name}\n${response.placeLikelihoods[0].place.address}\n${response.placeLikelihoods[0].place.latLng}\n${LocalDateTime.now()}"
//                    for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
//                        Log.d(
//                            "place",
//                            "Place '${placeLikelihood.place.name}''${placeLikelihood.place}' has likelihood: ${placeLikelihood.likelihood}"
//                        )
//                    }
                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.d("place", "Place not found: ${exception.statusCode}")
                    }
                }
            }
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            Log.d("place", "Place not found")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }
}