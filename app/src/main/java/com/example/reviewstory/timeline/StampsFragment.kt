package com.example.reviewstory.timeline

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reviewstory.R
import com.example.reviewstory.STAMP
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_stamps.view.*
import org.json.JSONObject


class StampsFragment : Fragment() {

    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null

    // Use fields to define the data types to return.
    val placeFields: List<Place.Field> = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

    // Use the builder to create a FindCurrentPlaceRequest.
    val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stamps, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("key"){ key, bundle ->
            val result = bundle.getString("bundleKey")
            Log.d("place", "데이터 전달 $result")
        }

        var stampList = ArrayList<STAMP>()
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        Log.d("place", "search")
//        var endDate = arguments?.getString("End_DATE")
//        var startDate = arguments?.getString("START_DATE")
        val startDate = "2021-04-30T09:14:12.668"
        val endDate = "2021-05-05T19:20:07.610"
        if (endDate != null && startDate != null) {
            Log.d("place", "search2")

            fbFirestore?.collection("stamp")
                ?.whereGreaterThanOrEqualTo("s_date", startDate)
                ?.whereLessThan("s_date", endDate)
                ?.get()
                ?.addOnSuccessListener { result ->
                        for (document in result) {
                            var stamp = STAMP()
                            stamp.s_num = document.id
                            stamp.address = document.data["address"] as String?
                            stamp.s_name = document.data["s_name"] as String?
                            stamp.s_date = document.data["s_date"] as String?
                            stamp.user_num = document.data["user_num"] as String?
                            stampList.add(stamp)
                            Log.d("place", "${document.id} => ${document.data["address"]}")
                            Log.d("place", "${stamp.address} => ${stamp.s_name}")
                        }

                    Log.d("place", "stamps 생성 ${stampList.size}")
                    /*  리사이클러뷰에 구분선 설정 */
                    view.recycle_result.addItemDecoration(
                        DividerItemDecoration(
                            requireContext(),
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    /* 리사이클러뷰에 어댑터 및 레이아웃메니저 설정 */
                    view.recycle_result.adapter = ResultAdapter(stampList, fbFirestore!!)
                    view.recycle_result.layoutManager = LinearLayoutManager(requireContext())
                }
        }//end of if
    }//end of onViewCreated
}