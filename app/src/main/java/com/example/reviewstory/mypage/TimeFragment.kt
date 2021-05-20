package com.example.reviewstory.mypage

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
import com.example.reviewstory.search.SearchAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_time.view.*
import kotlinx.android.synthetic.main.fragment_timeline.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class TimeFragment : Fragment() {

    var fbFirestore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        var timelist = ArrayList<REVIEW>()


        val safeArgs by navArgs<TimeFragmentArgs>()

        var date: String? = null

        date = safeArgs.date

        Log.d("check2", date.toString())

        var date2: String? = null

        if(date != null) {
            date2 = (LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1)).toString()



            fbFirestore?.collectionGroup("review")
                ?.whereEqualTo("user_num", fbAuth?.currentUser?.uid)
                ?.whereGreaterThan("s_date", date)
                ?.whereLessThan("s_date", date2)
                ?.get()
                ?.addOnSuccessListener { result ->
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
                        stamp.d_id = document.data["d_id"] as String?
                        timelist.add(stamp)
                        Log.d("check", date.toString())
                    }
                    view.recycle_result.addItemDecoration(
                        DividerItemDecoration(
                            requireContext(),
                            DividerItemDecoration.VERTICAL
                        )
                    )

                    view.recycle_result.adapter = MypageAdapter(timelist, fbFirestore!!)
                    view.recycle_result.layoutManager = LinearLayoutManager(requireContext())
                }
        }

    }
}