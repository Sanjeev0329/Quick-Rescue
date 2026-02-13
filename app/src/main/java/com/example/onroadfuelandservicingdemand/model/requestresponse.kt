package com.example.onroadfuelandservicingdemand.model



class requestresponse(val error: Boolean, val message:String, var user:ArrayList<request>) {
}


data class request(
    var id:Int,
   var wemail:String,
   var wname:String,
   var wnum:String,
   var wtype:String,
   var uname:String,
   var unum:String,
   var uemail:String,
   var service:String,
   var addinfo:String,
   var status:String,
   var cost:String,
   var rating:String,
   var feedback:String,
   var loc:String
)