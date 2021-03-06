package com.example.reviewstory.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {

    var fbFirestore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var stampList = ArrayList<REVIEW>()
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        context?.let { Places.initialize(it, "AIzaSyDTRY1lQAAW-WTWfbA_4KNcc30TFWWudDc") }
        val placesClient = activity?.let { Places.createClient(it) }
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
                childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                        as AutocompleteSupportFragment


        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                stampList.clear()
                Log.d("place", "${place.name}, ${place.id}")

                fbFirestore?.collectionGroup("review")
                    ?.whereEqualTo("s_name",place.name)
                    ?.get()
                    ?.addOnSuccessListener { result ->
                        for (document in result) {
                            var stamp = REVIEW()
                            var nick: String? = null
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
                            stamp.user_nick = document.data["user_nick"] as String?
                            //stamp.like_count = document.data["like_count"] as Int?
                            stamp.follow_count = Integer.parseInt(document.data["follow_count"].toString())

                            fbFirestore?.collection("user")
                                ?.whereEqualTo("user_num", stamp.user_num)
                                ?.get()
                                ?.addOnSuccessListener { reuslt ->
                                    for(document2 in reuslt){

                                        stamp.user_nick = document2.data["user_nick"] as String?
                                        //stamp.like_count = document2.data["like_count"] as Int?
                                        Log.d("?????????1", nick.toString())
                                        Log.d("????????? ?????????", document2.data["follow_count"].toString())


                                    }

                                }

                            Log.d("?????????2", stamp.user_nick.toString())
                            stampList.add(stamp)
                        }
                        Log.d("place", "?????? ?????? ${stampList.size}")
                        view.recycle_search.addItemDecoration(
                            DividerItemDecoration(
                                requireContext(),
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        /* ????????????????????? ????????? ??? ????????????????????? ?????? */
                        view.recycle_search.adapter = SearchAdapter(stampList, fbFirestore!!)
                        view.recycle_search.layoutManager = LinearLayoutManager(requireContext())
                    }
            }


            override fun onError(p0: Status) {
            }
        })

    }
}