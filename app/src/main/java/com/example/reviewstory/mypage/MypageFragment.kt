package com.example.reviewstory.mypage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.reviewstory.MainActivity
import com.example.reviewstory.R
import com.example.reviewstory.USER
import com.example.reviewstory.timeline.TimelineFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.fragment_mypage.btn_search
import kotlinx.android.synthetic.main.fragment_timeline.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*


class MypageFragment : Fragment() {

    private var locationPermissionGranted = false
    var date: String? = null
    private lateinit var auth: FirebaseAuth
    var fbFirestore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getLocationPermission()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }


    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("place", " get Permission")
            locationPermissionGranted = true
        } else {
            Log.d("place", "Permission not found")
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 1
                )
            };
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //var date = Calendar.getInstance()
        val auth = FirebaseAuth.getInstance()
        fbFirestore = FirebaseFirestore.getInstance()
        logout_button.setOnClickListener {
            auth.signOut()
//            val intent = Intent(context, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
            val direction: NavDirections =
                MypageFragmentDirections.actionMypageFragment2ToLoginActivity()
            findNavController().navigate(direction)
        }

        var currentCaldenar =
            Calendar.getInstance().apply { time = Date(System.currentTimeMillis()) }
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            //date= SimpleDateFormat("yyyy-MM-dd").format(currentCaldenar.time)
            //date = String.format("%d %d %d", year, month+1, dayOfMonth)
            currentCaldenar.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.run {
                /* 선택한 데이터를 2020-01-01와 같은 형식으로 가져옵니다.*/
                date = SimpleDateFormat("yyyy-MM-dd").format(currentCaldenar.time)
                /* 날짜 선택 여부를 체크하여 처리하기 위한 함수 호출
                   - 선택한 날짜를 선택창에 표시 */

            }
            Log.d("date", date.toString())
        }


        btn_search.setOnClickListener {
            if (date != null) {
                val direction: NavDirections =
                    MypageFragmentDirections.actionMypageFragment2ToTimeFragment(date.toString())
                findNavController().navigate(direction)
                Log.d("date2", date.toString())
            } else {
                Toast.makeText(context, "날짜를 선택해 주세요", Toast.LENGTH_LONG).show()
            }

        }

        this.auth = FirebaseAuth.getInstance()
        logout_button.setOnClickListener {
            auth.signOut()
//            val intent = Intent(context, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
            val direction: NavDirections =
                MypageFragmentDirections.actionMypageFragment2ToLoginActivity()
            findNavController().navigate(direction)
        }

        fbFirestore?.collection("user")
            ?.document(fbAuth?.uid.toString())
            ?.get()
            ?.addOnSuccessListener {
                var user = USER()
                user.user_nick = it.data?.get("user_nick") as String?

                et_name.text = user.user_nick

            }


    }
}


