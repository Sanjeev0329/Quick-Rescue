package com.example.agri_smartempoweringfarmerswithsoilanalysis.model



import com.example.onroadfuelandservicingdemand.model.requestresponse
import com.ymts0579.fooddonationapp.model.Userresponse
import com.ymts0579.model.model.DefaultResponse
import com.ymts0579.model.model.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {
    @FormUrlEncoded
    @POST("users.php")
    suspend fun register(
       @Field("name")name:String,
       @Field("email")email:String,
       @Field("password")password:String,
       @Field("mobile")mobile:String,
       @Field("type")type:String,
       @Field("address")address:String,
       @Field("loc")loc:String,
       @Field("city")city:String,
       @Field("condition") condition: String,
    ): Response<DefaultResponse>





    @FormUrlEncoded
    @POST("users.php")
    fun login(
        @Field("email") email: String, @Field("password") password: String,
        @Field("condition") condition: String
    ): Call<LoginResponse>



    @FormUrlEncoded
    @POST("users.php")
    suspend fun updateusers(
        @Field("name") name: String,
        @Field("mobile") mobile:String,
        @Field("password") password: String,
        @Field("city") city: String,
        @Field("address")address:String,
        @Field("id") id:Int,
        @Field("condition") condition: String
    ): Response<DefaultResponse>


    @FormUrlEncoded
    @POST("users.php")
    suspend fun servicestype(
        @Field("city")city:String,
        @Field("type")type:String,
        @Field("condition") condition: String
    ):Response<Userresponse>


    @FormUrlEncoded
     @POST( "request.php")
     suspend fun addrequest(
       @Field("wemail") wemail:String,
       @Field("wname") wname:String,
       @Field("wnum") wnum:String,
       @Field("wtype") wtype:String,
       @Field("uname") uname:String,
       @Field("unum") unum:String,
       @Field("uemail") uemail:String,
       @Field("service") service:String,
       @Field("addinfo") addinfo:String,
       @Field("status") status:String,
       @Field("cost") cost:String,
       @Field("rating") rating:String,
       @Field("feedback") feedback:String,
       @Field("loc")loc:String,
       @Field("condition") condition: String
     ):Response<DefaultResponse>

    @FormUrlEncoded
    @POST( "request.php")
     suspend fun userhistory(
         @Field("uemail") uemail:String,
         @Field("condition") condition: String
     ):Response<requestresponse>



    @FormUrlEncoded
    @POST( "request.php")
     suspend fun serviceshistory(
         @Field("wemail") wemail:String,
         @Field("condition") condition: String
     ):Response<requestresponse>


    @FormUrlEncoded
    @POST( "request.php")
     suspend fun updatestatus(
        @Field("status") status:String,
        @Field("id")id: Int,
        @Field("condition") condition: String
     ):Response<DefaultResponse>


     @FormUrlEncoded
     @POST( "request.php")
     suspend fun updaterating(
         @Field("rating") rating:String,
         @Field("feedback") feedback:String,
         @Field("id")id: Int,
         @Field("condition") condition: String
     ):Response<DefaultResponse>



}