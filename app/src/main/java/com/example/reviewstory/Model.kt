package com.example.reviewstory

import java.sql.Date


data class USER(
    var user_num : String? = null,
    var user_id : String? = null,
    var user_fw : String? = null,
    var user_bd : String? = null,
    var user_pn : String? = null,
    var user_pic : String? = null,
    var user_email : String? = null){

}

data class STAMP(
    var s_num : Int? = null,
    var user_num : String? = null,
    var tl_num : Int? = null,
    var address : String? = null,
    var s_name : String? = null,
    var s_date : String? = null,
    var rv_txt : String? = null,
    var r_pic : String? = null,
    var score : String? = null

){

}

data class FOLLOWLIST(
    var fol_num : Int? = null,
    var user_id : String? = null,
    var user_num : String? = null,
    var follow_id : String? = null
){

}

data class TIMELINE(
    var tl_num : Int? = null,
    var user_num : String? = null,
    var tl_date : Date? = null
){

}