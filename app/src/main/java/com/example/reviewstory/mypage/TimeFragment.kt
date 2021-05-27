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
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        var timelist = ArrayList<TIMELINE>()


        val safeArgs by navArgs<TimeFragmentArgs>()

        var date: String? = null

        date = safeArgs.date

        Log.d("check2", date.toString())

        var date2: String? = null

        if(date != null) {
            date2 = (LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1)).toString()



            fbFirestore?.collection("timeline")
                ?.whereEqualTo("user_num", fbAuth?.currentUser?.uid)
                ?.whereEqualTo("start_date", date)
                ?.get()
                ?.addOnSuccessListener { result ->
                    for (document in result) {
                        var stamp = TIMELINE()
                        stamp.end_date = document.data["end_date"] as String?
                        stamp.start_date = document.data["start_date"] as String?
                        stamp.tl_date = document.data["tl_date"] as String?
                        stamp.tl_num = document.data["tl_num"] as String?
                        stamp.user_num = document.data[""] as String?
                        timelist.add(stamp)
                        Log.d("check", date.toString())
                    }
                    view.recycle_result.adapter = MypageAdapter(timelist, fbFirestore!!)
                    view.recycle_result.layoutManager = LinearLayoutManager(requireContext())
                }
        }

    }
}