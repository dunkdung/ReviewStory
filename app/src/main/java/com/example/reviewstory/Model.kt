package com.example.reviewstory

import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PlaceLikelihood
import java.sql.Date


data class USER(
    var user_num : String? = null,
    var user_id : String? = null,
    var user_fw : String? = null,
    var user_bd : String? = null,
    var user_pn : String? = null,
    var user_pic : String? = null,
    var user_email : String? = null,
    var user_nick : String? = null){

}


data class FOLLOWLIST(
    var fol_num : Int? = null,
    var user_id : String? = null,
    var user_num : String? = null,
    var follow_id : String? = null
){

}

data class TIMELINE(
    var tl_num : String? = null,
    var user_num : String? = null,
    var tl_date : String? = null,
    var start_date : String? =null,
    var end_date : String? = null
){

}

data class REVIEW(
    var s_num : String? = null,
    var d_id : String? = null,
    var write : Boolean = false,
    var user_num : String? = null,
    var tl_num : String? = null,
    var address : String? = null,
    var s_name : String? = null,
    var s_date : String? = null,
    var score : String? = null,
    var places: List<PlaceLikelihood>? =null,
    var rv_txt : String? = null,
    var rv_img : String? = null
){

}