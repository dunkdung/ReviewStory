package com.example.reviewstory.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.reviewstory.FOLLOWLIST
import com.example.reviewstory.MyItem
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat


class DetailFragment : Fragment() {

    var fbFirestore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null
    var fbStorage: FirebaseStorage? = null

    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var stampList = ArrayList<REVIEW>()
        var items = ArrayList<MyItem>()


        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        val safeArgs by navArgs<DetailFragmentArgs>()
        var d_id: String? = safeArgs.dId
        var tl_num: String? = safeArgs.tlNum
        Log.d("place", "d-id         " + d_id.toString())
        Log.d("place", "tlnum         " + tl_num.toString())
        var i = 0
        btn_flw.setOnClickListener {
            fbFirestore?.collectionGroup("review")
                ?.whereEqualTo("tl_num", tl_num)
                ?.get()
                ?.addOnSuccessListener { result ->
                    var follow = FOLLOWLIST()
                    for (document in result) {
                        follow.fol_num = document.data["user_num"] as String?
                        follow.user_num = fbAuth?.currentUser?.uid
                    }
                    fbFirestore?.collection("followlist")
                        ?.document(follow.fol_num.toString() + follow.user_num.toString())
                        ?.set(follow)
                    Log.d("팔로우1", follow.fol_num.toString())
                    Log.d("팔로우2", follow.user_num.toString())
                }
        }
        fbFirestore?.collectionGroup("review")
            ?.whereEqualTo("tl_num", tl_num)
            ?.orderBy("s_date")
            ?.get()
            ?.addOnSuccessListener { result ->
                    var review: REVIEW? = null
                    //stampList.clear()
                    for (document in result) {
                        var stamp = REVIEW()
                        var item =  MyItem()
                        stamp.s_num = document.data["s_num"] as String?
                        stamp.address = document.data["address"] as String?
                        stamp.s_name = document.data["s_name"] as String?
                        stamp.s_date = document.data["s_date"] as String?
                        stamp.user_num = document.data["user_num"] as String?
                        stamp.rv_img = document.data["rv_img"] as String?
                        stamp.rv_txt = document.data["rv_txt"] as String?
                        stamp.tl_num = document.data["tl_num"] as String?
                        stamp.d_id = document.data["d_id"] as String?
                        stampList.add(stamp)
                        Log.d("place", stamp.rv_txt.toString())
                        Log.d("place", d_id.toString())

                        var a = IntRange(5, 9) // 0, 1, 2, 3 포함
                        var date1 = stamp.s_date?.slice(a)
                        Log.d("place", date1.toString())

                        if (d_id == document.data["d_id"]) {
                            review = stamp
                            index = i
                            Log.d("place", review.rv_txt.toString())
                            item.isActive = true
                            item.formattedDate = date1.toString()
                            item.title = document.data["s_name"] as String
                            item.tl_num = document.data["tl_num"] as String?
                            item.d_id = document.data["d_id"] as String?
                    }
                        else
                    {
                        item.isActive = false
                        item.formattedDate = date1.toString()
                        item.title = document.data["s_name"] as String
                        item.tl_num = document.data["tl_num"] as String?
                        item.d_id = document.data["d_id"] as String?
                    }
                        items.add(item)
                        textView10.text = stamp.user_num
                        textView11.text = stamp.address.toString()
                        textView12.text = stamp.s_name.toString()
                        textView13.text = stamp.rv_txt.toString()

                        if (review != null) {
                            context?.let {
                                Glide.with(it.applicationContext)
                                    .load(review.rv_img)
//                                    .override(600, 200)
                                    .into(imageView6)
                            }
                        }
                }
                view.sequneceLayout.setAdapter(MyAdapter(items))
            }



    }
}