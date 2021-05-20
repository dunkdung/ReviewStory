package com.example.reviewstory.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.reviewstory.MainActivity
import com.example.reviewstory.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_join.*

class JoinActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var fbFirestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        fbFirestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        btn_register.setOnClickListener() {


            createEmail()
        }

    }

    private fun createEmail() {
        auth?.createUserWithEmailAndPassword(email_edit.text.toString(), pass_edit.text.toString())
            ?.addOnCompleteListener(this) {
                val data = hashMapOf(
                    "user_email" to email_edit.text.toString(),
                    "user_bd" to et_age.text.toString(),
                    "nickname" to et_name.text.toString(),
                    "user_pw" to pass_edit.text.toString(),
                    "user_pn" to et_hak.text.toString()
                )

                if (it.isSuccessful) {
                    Toast.makeText(this, "가입 성공", Toast.LENGTH_SHORT).show()
                    fbFirestore?.collection("user")
                        ?.add(data)
                        ?.addOnSuccessListener { documentReference ->
                        }
                        ?.addOnFailureListener { e ->
                        }
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "기입 정보 이상, 중복 계정, 비밀번호 확인", Toast.LENGTH_SHORT).show()
                }
            }
    }
}