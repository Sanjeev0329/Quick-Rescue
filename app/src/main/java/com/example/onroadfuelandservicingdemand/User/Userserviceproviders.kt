package com.example.onroadfuelandservicingdemand.User

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agri_smartempoweringfarmerswithsoilanalysis.model.RetrofitClient
import com.example.connectinglocalworkers.model.User
import com.example.onroadfuelandservicingdemand.MainActivity
import com.example.onroadfuelandservicingdemand.R
import com.example.onroadfuelandservicingdemand.databinding.ActivityUserserviceprovidersBinding
import com.example.onroadfuelandservicingdemand.databinding.CarduserserviceBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ymts0579.model.model.DefaultResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Userserviceproviders : AppCompatActivity() {
    private val b by lazy {
        ActivityUserserviceprovidersBinding.inflate(layoutInflater)
    }
    private lateinit var fused: FusedLocationProviderClient
    var place=""
    var location=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        val type=intent.getStringExtra("type")

        fused= LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),10)
        }else{
            fused.lastLocation.addOnSuccessListener{
                it?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Geocoder(this).getFromLocation(it.latitude,it.longitude,1
                        ) { p0 ->
                            place = p0[0].getAddressLine(0)
                            location = p0[0].locality
                            runOnUiThread {
                              //  Toast.makeText(this, "$place", Toast.LENGTH_SHORT).show()
                                type?.let { it1 -> readcity(place,location, it1) }
                            }

                        }
                    } else {
                        
                        place= Geocoder(this).getFromLocation(it.latitude,it.longitude,
                            1)!![0].getAddressLine(0)
                        location= Geocoder(this).getFromLocation(it.latitude,it.longitude,
                            1)!![0].locality
                        type?.let { it1 -> readcity(place,location, it1) }
                    }
                   
                }

            }
            fused.lastLocation.addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
            fused.lastLocation.addOnCanceledListener {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readcity(place: String, location: String, type: String) {
        val p= ProgressDialog(this)
        p.show()
        CoroutineScope(Dispatchers.IO).async {
            async {
                try {
                    RetrofitClient.instance.servicestype(location, type,"servicestype")
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Userserviceproviders, e.message, Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }.await().let {
                withContext(Dispatchers.Main){
                    it!!.body()?.user?.let{
                        Toast.makeText(this@Userserviceproviders, "$it", Toast.LENGTH_SHORT).show()
                        p.dismiss()
                        b.listservices.adapter=userserviceAdapter(this@Userserviceproviders,it,place,type)
                        b.listservices.layoutManager=LinearLayoutManager(this@Userserviceproviders)
                    }


                }
            }
        }.start()

    }

    class userserviceAdapter(var context: Context, var listdata: ArrayList<User>, var place:String, var types:String):
        RecyclerView.Adapter<userserviceAdapter.DataViewHolder>(){

        inner class DataViewHolder(val view: CarduserserviceBinding) : RecyclerView.ViewHolder(view.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
            return DataViewHolder(
                CarduserserviceBinding.inflate(
                    LayoutInflater.from(context),parent,
                    false))
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: DataViewHolder, @SuppressLint("RecyclerView") position:Int) {
            with(holder.view){

                listdata[position].apply {
                    tvfname .text=name
                    tvfemail.text=email
                    tvfnum.text=mobile
                    tvfcity.text=city
                    tvfdis.text=address
                    btnaddrequest.setOnClickListener {
                        context.startActivity(Intent(context,Addrequest::class.java).apply {
                            putExtra("type",types)
                            putExtra("name",name)
                            putExtra("email",email)
                            putExtra("mobile",mobile)
                            putExtra("city",city)
                            putExtra("address",address)


                        })
                    }


                    btnfeedback.setOnClickListener {
                        context.startActivity(Intent(context,ViewFeedback::class.java).apply {
                            putExtra("email",email)
                        })
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val locationA = Location("point A")
                        val locationB = Location("point B")
                        Geocoder(context).getFromLocationName(place,1){
                            p0->
                            locationA.latitude = p0[0].latitude
                            locationA.longitude = p0[0].longitude
                        }

                        Geocoder(context).getFromLocationName(address,1){l->
                            locationB.latitude = l[0].latitude
                            locationB.longitude = l[0].longitude
                        }
                        val  distance = locationA.distanceTo(locationB)/1000
                        tvdistance.setText("$distance km")
                    } else {
                        val geo = Geocoder(context).getFromLocationName(place,1)!!
                        val ge= Geocoder(context).getFromLocationName(address,1)!!
                        val locationA = Location("point A")
                        locationA.latitude = geo[0].latitude
                        locationA.longitude = geo[0].longitude
                        val locationB = Location("point B")
                        locationB.latitude = ge[0].latitude
                        locationB.longitude = ge[0].longitude
                        val  distance = locationA.distanceTo(locationB)/1000
                        tvdistance.setText("$distance km")
                    }






                }

            }

        }


        override fun getItemCount() = listdata.size
    }
}