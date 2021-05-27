package com.example.reviewstory.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import com.example.reviewstory.MainActivity
import com.example.reviewstory.R
import com.example.reviewstory.USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_join.*
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var fbFirestore: FirebaseFirestore? = null
    val emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        fbFirestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        btn_register.setOnClickListener() {


            createEmail()
        }
        et_passck.addTextChangedListener(object : TextWatcher {
            // EditText에 문자 입력 전
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            // EditText에 변화가 있을 경우
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            // EditText 입력이 끝난 후
            override fun afterTextChanged(p0: Editable?) {
                if(pass_edit.getText().toString().equals(et_passck.getText().toString())){
                    txt_check.setText("일치합니다.")
                }
                else
                    txt_check.setText("일치하지 않습니다.")
            }
        })

        email_edit.addTextChangedListener(object  : TextWatcher{
            // EditText에 문자 입력 전
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            // EditText에 변화가 있을 경우
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            // EditText 입력이 끝난 후
            override fun afterTextChanged(p0: Editable?) {
                var email = email_edit.text.toString().trim()
                val p = Pattern.matches(emailValidation, email)

                if (p){ // 정상
                    id_check.setText("올바른 형식입니다.")
                    id_check.setTextColor(R.color.color_main.toInt())
                }else{// 비정상
                    id_check.setText("잘못된 형식입니다.")
                    id_check.setTextColor(R.color.quantum_googred300.toInt())
                }


            }
        })
    }

    private fun createEmail() {
        auth?.createUserWithEmailAndPassword(email_edit.text.toString(), pass_edit.text.toString())
            ?.addOnCompleteListener(this) {

                val user = USER()
                user.user_id = email_edit.text.toString()
                user.user_bd = et_age.text.toString()
                user.user_nick = et_name.text.toString()
                user.user_fw = pass_edit.text.toString()
                user.user_pn = et_hak.text.toString()
                user.user_num = auth?.uid

                if (it.isSuccessful) {
                    Toast.makeText(this, "가입 성공", Toast.LENGTH_SHORT).show()
                    fbFirestore?.collection("user")
                        ?.document(user.user_num.toString())
                        ?.set(user)
                        ?.addOnSuccessListener { documentReference ->
                        }
                        ?.addOnFailureListener { e ->
                        }
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()

                } else {
                    id_check.setText("이메일 중복")
                    Toast.makeText(this, "기입 정보를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }


}