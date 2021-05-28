package com.example.reviewstory.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.reviewstory.R
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.reviewstory.USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_join.*

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    var fbFirestore: FirebaseFirestore? = null
    var fbAuth: FirebaseAuth? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        fbFirestore = FirebaseFirestore.getInstance()
        fbAuth = FirebaseAuth.getInstance()

        addPreferencesFromResource(R.xml.preferences)



        val sps = PreferenceManager.getDefaultSharedPreferences(context)
        val editName = sps.getString("edit_text_preference_1", "")
        var user = USER()
        Log.d("제발요1", editName.toString())


        sps.registerOnSharedPreferenceChangeListener { sharedPreferences, s ->
            var editName2 = sps.getString("edit_text_preference_1", "")

            var show = sps.getBoolean("private",false)
            if (editName2 != null) {
                fbFirestore?.collection("user")
                    ?.document(fbAuth?.uid.toString())
                    ?.get()
                    ?.addOnSuccessListener {
                        user.user_id = it.data?.get("user_id") as String?
                        user.user_bd = it.data?.get("user_bd") as String?
                        user.user_fw = it.data?.get("user_fw") as String?
                        user.user_pn = it.data?.get("user_pn") as String?
                        user.user_num = it.data?.get("user_num") as String?
                        user.user_nick = editName2
                        fbFirestore?.collection("user")
                            ?.document(fbAuth?.uid.toString())?.set(user)
                        Log.d("제발요2", editName2)
                        Log.d("비공개", show.toString())
                    }
            }

            if(show == true){
                fbFirestore?.collection("user")
                    ?.document(fbAuth?.uid.toString())
                    ?.get()
                    ?.addOnSuccessListener {
                        user.user_id = it.data?.get("user_id") as String?
                        user.user_bd = it.data?.get("user_bd") as String?
                        user.user_fw = it.data?.get("user_fw") as String?
                        user.user_pn = it.data?.get("user_pn") as String?
                        user.user_num = it.data?.get("user_num") as String?
                        user.user_nick = it.data?.get("user_nick") as String?
                        user.user_private = true
                        fbFirestore?.collection("user")
                            ?.document(fbAuth?.uid.toString())?.set(user)
                        Log.d("공개", user.user_private.toString())
                    }
            }
        }
    }








    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}


