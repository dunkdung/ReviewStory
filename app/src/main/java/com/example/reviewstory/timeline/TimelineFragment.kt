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
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.add
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 위치정보 저장
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        activity?.let { Places.initialize(it.applicationContext, "AIzaSyDTRY1lQAAW-WTWfbA_4KNcc30TFWWudDc") }


        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        updatePlace()
        var startDate = "2021-04-30T09:14:12.668"
        var endDate = "2021-05-05T19:20:07.610"
        btn_search?.setOnClickListener {
            Log.d("place", "바튼 클릭")
            //if (startDate == null || endDate == null) {
            if (false) {
                /* 선택이 안된 경우 에러메세지를 띄웁니다.*/
                Toast.makeText(requireContext(), "분류와 날짜를 입력해주세요.", Toast.LENGTH_LONG).show()
            } else {
                /* 정상적으로 선택했다면 데이터를 Bundle 담아 ResultFragemtn 넘깁니다.*/
                Log.i("START_DATE", startDate)
                Log.i("END_DATE", endDate)
               childFragmentManager.beginTransaction().add(R.id.child_fragment, StampsFragment()).commit()
            }
        }
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

                    fbAuth?.currentUser?.email?.let { fbFirestore?.collection("user")?.document(it)?.collection("stamp")?.add(stampinfo) }

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