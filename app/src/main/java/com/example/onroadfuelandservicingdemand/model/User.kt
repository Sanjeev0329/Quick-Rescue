package com.example.connectinglocalworkers.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    var id:Int,
    var name:String,
    var email:String,
    var password:String,
    var mobile:String,
    var type:String,
    var address:String,
    var loc:String,
    var city:String,

    )
