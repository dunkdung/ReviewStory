package com.example.reviewstory.search

import android.os.Bundle
import android.provider.VoicemailContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reviewstory.R
import com.example.reviewstory.STAMP
import com.example.reviewstory.timeline.ResultAdapter
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_stamps.view.*


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
        var stampList = ArrayList<STAMP>()
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
                Log.d("place", "${place.name}, ${place.id}")
                fbFirestore?.collection("stamp")?.whereEqualTo("s_name",place.name)?.get()
                    ?.addOnSuccessListener { result ->
                        for (document in result) {
                            var stamp = STAMP()
                            stamp.s_num = document.id
                            stamp.address = document.data["address"] as String?
                            stamp.s_name = document.data["s_name"] as String?
                            stamp.s_date = document.data["s_date"] as String?
                            stamp.user_num = document.data["user_num"] as String?
                            stampList.add(stamp)
                        }
                        Log.d("place", "검색 갯수 ${stampList.size}")
                        view.recycle_search.addItemDecoration(
                            DividerItemDecoration(
                                requireContext(),
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        /* 리사이클러뷰에 어댑터 및 레이아웃메니저 설정 */
                        view.recycle_search.adapter = SearchAdapter(stampList, fbFirestore!!)
                        view.recycle_search.layoutManager = LinearLayoutManager(requireContext())
                    }
            }

            override fun onError(p0: Status) {
            }
        })

    }
}