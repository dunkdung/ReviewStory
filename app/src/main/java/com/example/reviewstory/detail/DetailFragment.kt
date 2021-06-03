package com.example.reviewstory.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.reviewstory.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*


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
                        fbFirestore?.collection("user")
                            ?.document(follow.fol_num.toString())
                            ?.get()
                            ?.addOnSuccessListener {
                                follow.follow_id = it.data?.get("user_nick") as String?

                                fbFirestore?.collection("followlist")
                                    ?.document(follow.fol_num.toString() + follow.user_num.toString())
                                    ?.set(follow)

                                Log.d("팔로우4", follow.follow_id.toString())
                            }
                        fbFirestore?.collection("user")
                            ?.document(follow.user_num.toString())
                            ?.get()
                            ?.addOnSuccessListener {
                                follow.user_id = it.data?.get("user_nick") as String?
                                fbFirestore?.collection("followlist")
                                    ?.document(follow.fol_num.toString() + follow.user_num.toString())
                                    ?.set(follow)
                                Log.d("팔로우3", follow.user_id.toString())
                            }
                    }
                    fbFirestore?.collection("followlist")
                        ?.document(follow.fol_num.toString() + follow.user_num.toString())
                        ?.set(follow)
                    Log.d("팔로우1", follow.fol_num.toString())
                    Log.d("팔로우2", follow.user_num.toString())

                    var tsDoc = fbFirestore?.collection("user")
                        ?.document(follow.fol_num.toString())

                    fbFirestore?.runTransaction { transaction ->
                        var uid = follow.user_num
                        var contentDTO = transaction.get(tsDoc!!).toObject<USER>()

                        if (contentDTO!!.follow_list.containsKey(uid)) {
                            contentDTO?.follow_count = contentDTO?.follow_count?.minus(1)
                            contentDTO?.follow_list.remove(uid)
                        } else {
                            contentDTO?.follow_count = contentDTO?.follow_count?.plus(1)
                            contentDTO.follow_list[uid!!] = true
                        }
                        transaction.set(tsDoc, contentDTO)
//                                if(contentDTO!!.like.containsKey(FirebaseAuth.getInstance().currentUser?.uid)){
//                                    view.img_favorite.setImageResource(R.drawable.ic_gps)
//                                } else{
//                                    view.img_favorite.setImageResource(R.drawable.ic_favorite_border)
//                                }
                    }



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
                    var item = MyItem()
                    stamp.s_num = document.data["s_num"] as String?
                    stamp.address = document.data["address"] as String?
                    stamp.s_name = document.data["s_name"] as String?
                    stamp.s_date = document.data["s_date"] as String?
                    stamp.user_num = document.data["user_num"] as String?
                    stamp.rv_img = document.data["rv_img"] as String?
                    stamp.rv_txt = document.data["rv_txt"] as String?
                    stamp.tl_num = document.data["tl_num"] as String?
                    stamp.d_id = document.data["d_id"] as String?
                    stamp.score = document.data["score"] as String?
                    stampList.add(stamp)
                    Log.d("place", "별점은?????" + stamp.score)
                    var a = IntRange(5, 9) // 0, 1, 2, 3 포함
                    var date1 = stamp.s_date?.slice(a)

                    if (d_id == document.data["d_id"]) {
                        review = stamp
                        index = i
                        item.isActive = true
                        item.formattedDate = date1.toString()
                        item.title = document.data["s_name"] as String
                        item.tl_num = document.data["tl_num"] as String?
                        item.d_id = document.data["d_id"] as String?
                    } else {
                        item.isActive = false
                        item.formattedDate = date1.toString()
                        item.title = document.data["s_name"] as String
                        item.tl_num = document.data["tl_num"] as String?
                        item.d_id = document.data["d_id"] as String?
                    }
                    items.add(item)
                    var user_nick = "작성자"
                    fbFirestore?.collection("user")
                        ?.document(review?.user_num.toString())
                        ?.get()
                        ?.addOnSuccessListener {
                            user_nick = it.data?.get("user_nick").toString()
                            tv_mainwriter.text = user_nick + "님의 타임라인"
                        }

                    tv_address.text = review?.address.toString()
                    tv_place.text = review?.s_name.toString()
                    tv_review.text = review?.rv_txt.toString()
                    Log.d("place", review?.score.toString())
                    if (review?.score != null) {
                        appCompatRatingBar.visibility = View.VISIBLE
                        rating_point.visibility = View.VISIBLE
                        appCompatRatingBar.rating = review?.score!!.toFloat()
                        rating_point.text = review.score
                    } else {
                        appCompatRatingBar.visibility = View.INVISIBLE
                        rating_point.visibility = View.INVISIBLE
                    }

                    if (review != null) {
                        context?.let {
                            Glide.with(it.applicationContext)
                                .load(review.rv_img)
//                                    .override(600, 200)
                                .into(imageView6)
                        }
                    }
//                    img_favorite.setOnClickListener {
//                        var tsDoc = fbFirestore?.collection("timeline")
//                            ?.document(review?.tl_num as String)
//                            ?.collection("review")
//                            ?.document(review?.d_id as String)
//                        fbFirestore?.runTransaction { transaction ->
//                            var uid = FirebaseAuth.getInstance().currentUser?.uid
//                            var contentDTO = transaction.get(tsDoc!!).toObject<REVIEW>()
//
//                            if (contentDTO!!.like.containsKey(uid)) {
//                                contentDTO?.like_count = contentDTO?.like_count?.minus(1)
//                                contentDTO?.like.remove(uid)
//                            } else {
//                                contentDTO?.like_count = contentDTO?.like_count?.plus(1)
//                                contentDTO.like[uid!!] = true
//                            }
//                            transaction.set(tsDoc, contentDTO)
////                                if(contentDTO!!.like.containsKey(FirebaseAuth.getInstance().currentUser?.uid)){
////                                    view.img_favorite.setImageResource(R.drawable.ic_gps)
////                                } else{
////                                    view.img_favorite.setImageResource(R.drawable.ic_favorite_border)
////                                }
//                        }
//                    }
//
//
//                }
//                if (review!!.like.containsKey(FirebaseAuth.getInstance().currentUser?.uid)) {
//                    img_favorite.setImageResource(R.drawable.ic_gps)
//                } else {
//                    img_favorite.setImageResource(R.drawable.ic_favorite_border)
//                }
                    // }
                    view.sequneceLayout.setAdapter(MyAdapter(items))
                }


            }
    }
}





