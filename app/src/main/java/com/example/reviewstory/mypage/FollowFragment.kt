package com.example.reviewstory.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reviewstory.FOLLOWLIST
import com.example.reviewstory.R
import com.example.reviewstory.TIMELINE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_follow.view.*
import kotlinx.android.synthetic.main.fragment_time.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


class FollowFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        var follow_list = ArrayList<FOLLOWLIST>()

        fbFirestore?.collection("followlist")
                ?.whereEqualTo("user_num", fbAuth?.currentUser?.uid)
                ?.get()
                ?.addOnSuccessListener { result ->
                    for (document in result) {
                        var stamp = FOLLOWLIST()
                        stamp.fol_num = document.data["fol_num"] as String?
                        stamp.follow_id = document.data["follow_id"] as String?
                        stamp.user_id = document.data["user_id"] as String?
                        stamp.user_num = document.data["user_num"] as String?
                        follow_list.add(stamp)
                    }
                    view.follow_list.adapter = FollowAdapter(follow_list, fbFirestore!!)
                    view.follow_list.layoutManager = LinearLayoutManager(requireContext())
                }
        }
}