package com.example.reviewstory.timeline

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.example.reviewstory.TIMELINE
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_stamps.view.*
import kotlin.collections.ArrayList


class StampsFragment : Fragment() {
    var fbFirestore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null

    // Use fields to define the data types to return.
    val placeFields: List<Place.Field> =
        listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

    // Use the builder to create a FindCurrentPlaceRequest.
    val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stamps, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var endDate: String? = null
        var startDate: String? = null

        val safeArgs by navArgs<StampsFragmentArgs>()

        startDate = safeArgs.sdate
        endDate = safeArgs.ldate

        var stampList = ArrayList<REVIEW>()
        var snumList = ArrayList<String>()
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        if (endDate != null && startDate != null) {
            var tline = TIMELINE()
            tline.tl_num = fbAuth!!.uid + startDate + endDate
            tline.end_date = endDate
            tline.start_date = startDate
            tline.user_num = fbAuth!!.uid
            fbFirestore?.collection("timeline")?.document(tline.tl_num.toString())?.set(tline)
            fbFirestore?.collection("stamp")
               ?.whereGreaterThan("s_date", startDate)
               ?.whereEqualTo("user_num", fbAuth!!.uid)
               ?.whereLessThan("s_date", endDate)
               ?.get()?.addOnSuccessListener { result ->
                    for (document in result) {
                            var stamp = REVIEW()
                            stamp.s_num = document.data["s_num"] as String?
                            stamp.address = document.data["address"] as String?
                            stamp.s_name = document.data["s_name"] as String?
                            stamp.s_date = document.data["s_date"] as String?
                            stamp.user_num = document.data["user_num"] as String?
                            stampList.add(stamp)
                        }
                        /*  리사이클러뷰에 구분선 설정 */
                        view.recycle_result.addItemDecoration(
                            DividerItemDecoration(
                                requireContext(),
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        /* 리사이클러뷰에 어댑터 및 레이아웃메니저 설정 */
                        view.recycle_result.adapter = ResultAdapter(stampList, tline)
                        view.recycle_result.layoutManager = LinearLayoutManager(requireContext())
                    }
            }
        }
    }






