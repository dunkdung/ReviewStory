package com.example.reviewstory.timeline

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.example.reviewstory.STAMP
import com.example.reviewstory.TIMELINE
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_review.*
import kotlinx.android.synthetic.main.fragment_timeline.*


@Suppress("DEPRECATION")
class ReviewFragment : Fragment() {

    var fbFirestore: FirebaseFirestore? = null
    val OPNE_GALLERY = 1
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

        val safeArgs by navArgs<ReviewFragmentArgs>()

        val snum = safeArgs.snum
        val tlnum = safeArgs.tlnum
        val std = safeArgs.startdate
        val edd = safeArgs.enddate


        Log.d("place", snum)
        txt_btn.setOnClickListener {
            var review = REVIEW()
            review.rv_txt = rv_edit.text.toString()

            fbFirestore?.collection("stamp")
                ?.whereEqualTo("s_num", snum)
                ?.get()
                ?.addOnSuccessListener { result ->
                    for (document in result) {
                        var stamp = STAMP()
                        stamp.s_num = snum
                        stamp.address = document.data["address"] as String?
                        stamp.s_name = document.data["s_name"] as String?
                        stamp.s_date = document.data["s_date"] as String?
                        stamp.user_num = document.data["user_num"] as String?

                        review.address = stamp.address
                        review.s_num = stamp.s_num
                        review.s_name = stamp.s_name
                        review.s_date = stamp.s_date
                        review.user_num = stamp.user_num

                        fbFirestore?.collection("timeline")
                            ?.document(tlnum)
                            ?.collection("review")
                            ?.add(review)
                    }
                    Log.d("place", "리뷰추가")
                }
            val direction: NavDirections = ReviewFragmentDirections.actionReviewFragmentToStampsFragment(std,edd)
            findNavController().navigate(direction)
        }

        img_btn.setOnClickListener {
            openGallery()
        }

    }


    private fun openGallery() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(Intent.createChooser(intent,"load"),OPNE_GALLERY)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (resultCode == OPNE_GALLERY) {
                var dataUri = data?.data
                val source =
                    ImageDecoder.createSource(this.requireActivity().contentResolver, dataUri!!)
                Log.d("imgopen", dataUri.toString())

                try{
                    //val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, dataUri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(bitmap)
                    Log.d("image", dataUri.toString())
                }catch (e:Exception){

                }
            }
        }
    }
}


