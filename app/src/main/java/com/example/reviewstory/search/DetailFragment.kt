package com.example.reviewstory.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.reviewstory.R
import com.example.reviewstory.timeline.ReviewFragmentArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlin.String as String


class DetailFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        // val safeArgs by navArgs<DetailFragmentArgs>()
        // var snum: String? = safeArgs.snum

/*
        Log.d("snum1", snum.toString())

        fbFirestore?.collectionGroup("review")
            ?.whereEqualTo("s_num", snum)
            ?.get()
            ?.addOnSuccessListener { result ->
                for(document in result){
                    txt_review.text = document.data["rv_txt"] as String?
                    txt_user.text = document.data["user_num"] as String?

                    Log.d("snum2", txt_review.text.toString())



                }


            }


    }

}



 */
    }
}