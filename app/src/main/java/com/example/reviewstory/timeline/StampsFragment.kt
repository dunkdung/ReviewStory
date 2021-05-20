package com.example.reviewstory.timeline

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
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
import kotlinx.android.synthetic.main.fragment_stamps.*
import kotlinx.android.synthetic.main.fragment_stamps.view.*
import java.time.LocalDate
import java.time.LocalDateTime
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
        onResume()
        val safeArgs by navArgs<StampsFragmentArgs>()

        startDate = safeArgs.sdate
        endDate = safeArgs.ldate

        var stampList = ArrayList<REVIEW>()
        var snumList = ArrayList<String>()
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        if (endDate != null && startDate != null) {
            stampList.clear()
            var tline = TIMELINE()
            tline.tl_num = fbAuth!!.uid + startDate + endDate
            tline.end_date = endDate
            tline.start_date = startDate
            tline.user_num = fbAuth!!.uid
            tline.tl_date = LocalDateTime.now().toString()
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
                        stamp.rv_img = document.data["rv_img"] as String?
                        stamp.rv_txt = document.data["rv_txt"] as String?
                        stamp.tl_num = document.data["tl_num"] as String?
                        stamp.d_id = document.id
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
                        view.recycle_result.adapter = ResultAdapter(stampList, tline, fbFirestore!!)
                        view.recycle_result.layoutManager = LinearLayoutManager(requireContext())



                    }

        }

        save_btn.setOnClickListener {
            /*
            fbFirestore?.collection("timeline")
                    ?.document(fbAuth!!.uid + startDate + endDate)
                    ?.collection("review")
                    ?.get()
                    ?.addOnSuccessListener { result ->
                        for (document in result) {
                            var review = REVIEW()
                            review.s_num = document.data["s_num"] as String?
                            review.address = document.data["address"] as String?
                            review.s_name = document.data["s_name"] as String?
                            review.s_date = document.data["s_date"] as String?
                            review.user_num = document.data["user_num"] as String?
                            reviewList.add(review)
                        }

                        Log.d("place", "stamps 생성 ${reviewList.size}")
                        /*  리사이클러뷰에 구분선 설정 */
                        view.recycle_result.addItemDecoration(
                                DividerItemDecoration(
                                        requireContext(),
                                        DividerItemDecoration.VERTICAL
                                )
                        )
                        /* 리사이클러뷰에 어댑터 및 레이아웃메니저 설정 */
                        view.recycle_result.adapter = ResultAdapter(reviewList, tline)
                        view.recycle_result.layoutManager = LinearLayoutManager(requireContext())
                    }
        }*/

            val direction: NavDirections = StampsFragmentDirections.actionStampsFragmentToTimelineFragment("test")
            findNavController().navigate(direction)



        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }
}






