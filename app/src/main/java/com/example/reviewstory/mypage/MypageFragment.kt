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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage, container, false)
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
        btn_flwlist.setOnClickListener {
            val direction: NavDirections =
                MypageFragmentDirections.actionMypageFragment2ToFollowFragment()
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
                /* ????????? ???????????? 2020-01-01??? ?????? ???????????? ???????????????.*/
                date = SimpleDateFormat("yyyy-MM-dd").format(currentCaldenar.time)
                /* ?????? ?????? ????????? ???????????? ???????????? ?????? ?????? ??????
                   - ????????? ????????? ???????????? ?????? */

            }
            Log.d("date", date.toString())
        }


        btn_search.setOnClickListener {
            if (date != null) {
                val direction: NavDirections =
                    MypageFragmentDirections.actionMypageFragment2ToTimeFragment(date.toString(), fbAuth?.uid.toString())
                findNavController().navigate(direction)
                Log.d("date2", date.toString())
            } else {
                Toast.makeText(context, "????????? ????????? ?????????", Toast.LENGTH_LONG).show()
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
            ?.document(this.auth?.uid.toString())
            ?.get()
            ?.addOnSuccessListener {
                var user = USER()
                user.user_nick = it.data?.get("user_nick") as String?

                et_name.text = user.user_nick.toString()
                Log.d("?????????", user.user_nick.toString())
                Log.d("?????????2", this.auth?.uid.toString())


            }


    }
}


