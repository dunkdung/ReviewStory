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
import com.example.reviewstory.R
import com.example.reviewstory.STAMP
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

        btn.setOnClickListener {
            var review = STAMP()

            review.rv_txt = txt.text.toString()

            fbFirestore?.collection("stamp")?.document(review.s_num.toString())
                ?.set(review)

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