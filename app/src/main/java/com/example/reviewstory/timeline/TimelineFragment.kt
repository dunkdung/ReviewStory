package com.example.reviewstory.timeline

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.reviewstory.R
import com.example.reviewstory.REVIEW
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_timeline.*
import kotlinx.android.synthetic.main.fragment_timeline.view.*
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*


class TimelineFragment : Fragment() {

    var fbFirestore : FirebaseFirestore? = null
    var fbAuth : FirebaseAuth? = null
    var start_date: String? = null
    var last_date: String? = null

    // Use fields to define the data types to return.
    val placeFields: List<Place.Field> = listOf(Place.Field.NAME,Place.Field.ADDRESS)

    // Use the builder to create a FindCurrentPlaceRequest.
    val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 위치정보 저장
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        activity?.let { Places.initialize(it.applicationContext, "AIzaSyDTRY1lQAAW-WTWfbA_4KNcc30TFWWudDc") }

        start_btn.setOnClickListener {
            /*현재날짜를 가져옵니다.*/
            val currentCaldenar =
                Calendar.getInstance().apply { time = Date(System.currentTimeMillis()) }

            /* 날짜를 선택하는 다이얼로그를 만듭니다.*/
            DatePickerDialog(
                requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    currentCaldenar.apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }.run {
                        /* 선택한 데이터를 2020-01-01와 같은 형식으로 가져옵니다.*/
                        start_date = SimpleDateFormat("yyyy-MM-dd").format(currentCaldenar.time)
                        /* 날짜 선택 여부를 체크하여 처리하기 위한 함수 호출
                           - 선택한 날짜를 선택창에 표시 */
                        start_date?.let { start_txt.text = it }
                    }
                },
                /* DatePickerDialog의 Date를 오늘 날짜로 초기화)*/
                currentCaldenar.get(Calendar.YEAR),
                currentCaldenar.get(Calendar.MONTH),
                currentCaldenar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        last_btn.setOnClickListener {
            /*현재날짜를 가져옵니다.*/
            val currentCaldenar =
                Calendar.getInstance().apply { time = Date(System.currentTimeMillis()) }

            /* 날짜를 선택하는 다이얼로그를 만듭니다.*/
            DatePickerDialog(
                requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    currentCaldenar.apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth+1)
                    }.run {
                        /* 선택한 데이터를 2020-01-01와 같은 형식으로 가져옵니다.*/
                        last_date = SimpleDateFormat("yyyy-MM-dd").format(currentCaldenar.time)
                        /* 날짜 선택 여부를 체크하여 처리하기 위한 함수 호출
                           - 선택한 날짜를 선택창에 표시 */
                        last_txt.text = (LocalDate.parse(last_date, DateTimeFormatter.ISO_DATE).minusDays(1)).toString()

                        last_date
                    }
                },
                /* DatePickerDialog의 Date를 오늘 날짜로 초기화)*/
                currentCaldenar.get(Calendar.YEAR),
                currentCaldenar.get(Calendar.MONTH),
                currentCaldenar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        //updatePlace()
        view.btn_search?.setOnClickListener {

            if(start_date != null || last_date != null) {
                val direction: NavDirections =
                    TimelineFragmentDirections.actionTimelineFragmentToStampsFragment(
                        start_date.toString(),
                        last_date.toString()
                    )
                findNavController().navigate(direction)
            }
            else{
                Toast.makeText(context, "날짜를 선택해 주세요", Toast.LENGTH_LONG).show()
            }
        }




    }

}




