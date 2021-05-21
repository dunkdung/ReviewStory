package com.example.reviewstory.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.list_item_review.view.*


class DetailFragment : Fragment() {

    var fbFirestore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null
    var fbStorage: FirebaseStorage? = null
    var review = REVIEW()
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
        var stamp = REVIEW()

        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        stampList.clear()
        var storageRef = fbStorage?.reference?.child("images")
        val safeArgs by navArgs<DetailFragmentArgs>()
        var d_id: String? = safeArgs.dId
        var tl_num: String? = safeArgs.tlNum
        Log.d("place", "d-id         " + d_id.toString())
        Log.d("place", "tlnum         " + tl_num.toString())
        var i = 0
        fbFirestore?.collection("timeline")
                ?.document(tl_num.toString())
                ?.collection("review")
                ?.get()
                ?.addOnSuccessListener { result ->
                    for (document in result) {
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
                        if (d_id == document.data["d_id"]) {
                            review = stamp
                            index = i
                            Log.d("place", review.rv_txt.toString())

                            txt_user.text = stamp.user_num
                            textView10.text = stamp.rv_txt.toString()
                            textView9.text = stamp.s_name.toString()
                            textView11.text = stamp.score
                            D_review.text = stamp.address.toString()


                            Glide.with(this)
                                .load(stamp.rv_img)
                                .override(600, 200)
                                .into(imageView3)
                            view.txt_user.text = review.rv_txt
                            Log.d("place", "이미지 주소    " + review.rv_img.toString())
                            context?.let {
                                Glide.with(it.applicationContext)
                                    .load(review.rv_img)
                                    .override(600, 200)
                                    .into(imageView3)
                            }
                        }
                        i += 1
                    }

                    view.recycler_detail.adapter = DetailAdapter(stampList, fbFirestore!!)
                    view.recycler_detail.layoutManager = LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.HORIZONTAL }
                }

    }
}