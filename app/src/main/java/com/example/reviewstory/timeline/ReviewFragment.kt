package com.example.reviewstory.timeline

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_review.*
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class ReviewFragment : Fragment() {
    var fbFirestore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null
    var fbStorage: FirebaseStorage? = null

    var rating_num: String? = null
    var pickImageFromAlbum = 0
    var uriPhoto: Uri? = null
    var imageuri: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        fbStorage = FirebaseStorage.getInstance()
        img_btn.setOnClickListener {
            var photoPicerIntent = Intent(Intent.ACTION_PICK)
            photoPicerIntent.type = "image/*"
            startActivityForResult(photoPicerIntent, pickImageFromAlbum)
        }
        rating_area.setOnRatingBarChangeListener{ratingBar, fl, b ->
            rating_num = fl.toString()
        }

        val safeArgs by navArgs<ReviewFragmentArgs>()
        val snum = safeArgs.snum
        val tlnum = safeArgs.tlnum
        val std = safeArgs.startdate
        val edd = safeArgs.enddate
        txt_btn.setOnClickListener {
            var review = REVIEW()
            review.rv_txt = rv_edit.text.toString()
            var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            var imgFileName = "IMAGE_" + timeStamp + "_.png"
            var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)
            if (uriPhoto != null) {
                storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        imageuri = uri.toString()
                        fbFirestore?.collection("user")
                            ?.whereEqualTo("user_num", fbAuth!!.uid)
                            ?.get()
                            ?.addOnSuccessListener { reuslt ->
                                for(document in reuslt){
                                    review.user_nick = document.data["user_nick"] as String?
                                    //review.g_nick = fbAuth!!.currentUser.email

                                }
                            }
                        fbFirestore?.collection("stamp")
                                ?.whereEqualTo("s_num", snum)
                                ?.get()
                                ?.addOnSuccessListener { result ->
                                    for (document in result) {
                                        var stamp = REVIEW()
                                        stamp.s_num = snum
                                        stamp.s_num = document.data["s_num"] as String?
                                        stamp.address = document.data["address"] as String?
                                        stamp.s_name = document.data["s_name"] as String?
                                        stamp.s_date = document.data["s_date"] as String?
                                        stamp.user_num = document.data["user_num"] as String?
                                        stamp.rv_img = document.data["rv_img"] as String?
                                        stamp.rv_txt = review.rv_txt
                                        stamp.tl_num = document.data["tl_num"] as String?
                                        stamp.d_id = document.id
                                        review.address = stamp.address
                                        review.s_num = stamp.s_num
                                        review.s_name = stamp.s_name
                                        review.s_date = stamp.s_date
                                        review.user_num = stamp.user_num
                                        review.rv_img = imageuri
                                        review.tl_num = tlnum
                                        review.d_id = document.id
                                        review.score = rating_num
                                        fbFirestore?.collection("timeline")
                                                ?.document(tlnum)
                                                ?.collection("review")
                                                ?.document(document.id)
                                                ?.set(review)
                                    }
                                    Log.d("place", "리뷰추가 ")
                                    Log.d("nick", review.user_nick.toString())

                                }
                    }
                }
            }
            else{
                fbFirestore?.collection("user")
                    ?.whereEqualTo("user_num", fbAuth!!.uid)
                    ?.get()
                    ?.addOnSuccessListener { reuslt ->
                        for(document in reuslt){
                            review.user_nick = document.data["user_nick"] as String?
                            //review.g_nick = fbAuth!!.currentUser.email
                            Log.d("마지막1", review.user_nick.toString())
                        }
                    }
                Log.d("마지막2", review.user_nick.toString())
                fbFirestore?.collection("stamp")
                    ?.whereEqualTo("s_num", snum)
                    ?.get()
                    ?.addOnSuccessListener { result ->
                        for (document in result) {
                            var stamp = REVIEW()
                            stamp.s_num = snum
                            stamp.s_num = document.data["s_num"] as String?
                            stamp.address = document.data["address"] as String?
                            stamp.s_name = document.data["s_name"] as String?
                            stamp.s_date = document.data["s_date"] as String?
                            stamp.user_num = document.data["user_num"] as String?
                            stamp.rv_img = document.data["rv_img"] as String?
                            stamp.rv_txt = review.rv_txt
                            stamp.tl_num = document.data["tl_num"] as String?
                            stamp.d_id = document.id
                            review.s_num = stamp.s_num
                            review.s_name = stamp.s_name
                            review.s_date = stamp.s_date
                            review.user_num = stamp.user_num
                            review.rv_img = null
                            review.tl_num = tlnum
                            review.d_id = document.id
                            review.score = rating_num
                            review.address = stamp.address
                            fbFirestore?.collection("stamp")?.document(stamp.d_id.toString())?.set(stamp)
                            fbFirestore?.collection("timeline")
                                ?.document(tlnum)
                                ?.collection("review")
                                ?.document(document.id)
                                ?.set(review)
                            Log.d("change1", review.rv_txt.toString())
                            Log.d("change2", stamp.s_num.toString())
                            Log.d("마지막3", review.user_nick.toString())
                        }

                    }
            }
            val direction: NavDirections = ReviewFragmentDirections.actionReviewFragmentToStampsFragment(std, edd)
            Toast.makeText(context,"리뷰 작성 성공", Toast.LENGTH_LONG).show()
            findNavController().navigate(direction)
        }
    }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            uriPhoto = data?.data
            imageView.setImageURI(uriPhoto)
        }
    }