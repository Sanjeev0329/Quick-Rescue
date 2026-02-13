package com.example.onroadfuelandservicingdemand.User

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.agri_smartempoweringfarmerswithsoilanalysis.model.RetrofitClient
import com.example.onroadfuelandservicingdemand.MainActivity
import com.example.onroadfuelandservicingdemand.databinding.ActivityAddrequestBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class Addrequest : AppCompatActivity() {
    private val b by lazy{
        ActivityAddrequestBinding.inflate(layoutInflater)
    }
    var uname=""
    var unum=""
    var uemail=""
    @SuppressLint("SetTextI18n")
    private lateinit var fused: FusedLocationProviderClient
    var place=""
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)

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
                            runOnUiThread {
                                Toast.makeText(this, "$place", Toast.LENGTH_SHORT).show()
                            }

                        }
                    } else {
                        Toast.makeText(this, "View Point", Toast.LENGTH_SHORT).show()
                        place= Geocoder(this).getFromLocation(it.latitude,it.longitude,
                            1)!![0].getAddressLine(0)
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
        b.apply {
            getSharedPreferences("user", MODE_PRIVATE).apply {
                uname=( getString("name", "").toString())
                uemail=getString("email", "").toString()
                unum=( getString("mobile", "").toString())
            }
            val type=intent.getStringExtra("type")!!
            val name=intent.getStringExtra("name")!!
            val email=intent.getStringExtra("email")!!
            val mobile=intent.getStringExtra("mobile")!!
            val address=intent.getStringExtra("address")!!


            tvname.text= "Name : $name"
            tvmobile.text="mobile : "+mobile
            tvemail.text="Email : "+email
            tvaddre.text="Address : "+address
            tvservices.text=" Services on $type"

            val mechanic = ArrayList<String>()
            mechanic.add("Choose your service")
            mechanic.add("Radiator hoses – check for leaks and cracking")
            mechanic.add("Engine air filter – check that it is clean")
            mechanic.add("Battery – look for corrosion around terminals")
            mechanic.add("Fan belts – ensure none are loose or squeaking on start-up")
            mechanic.add("Unusual engine noises – hard to start, ticking, hissing or clunking")
            mechanic.add("Brake lights/Reverse lights/Indicators/hazard lights")
            mechanic.add("Treadwear and tread depth/Car jack and tools")
            mechanic.add("Radiator coolant/Power steering fluid")

            val Towing = ArrayList<String>()
            Towing.add("Choose your service")
            Towing.add("Accident vehicle tow request")
            Towing.add("Breakdown vehcile tow request")
            Towing.add("Engine Seized vehicle tow request")
            Towing.add("Vehicle seized by Authorities tow request")
            Towing.add("Garage drop tow request")

            val Fuelservices = ArrayList<String>()
            Fuelservices.add("Choose your service")
            Fuelservices.add("Vehicle stopped due to fuel less")
            Fuelservices.add("Fuel required for Generator Purpose")
            Fuelservices.add("Non moveable Heavy machinery Fuel Request")
            Fuelservices.add("Engine Oil Lubricants")
            Fuelservices.add("High Volume fuel request for Industrial works")

            if (type.equals("Mechanic", ignoreCase = true)) {
                val adpterbabycare = ArrayAdapter(
                    applicationContext, R.layout.simple_list_item_checked, mechanic
                )
                services.setAdapter(adpterbabycare)
            } else if (type.equals("Towing", ignoreCase = true)) {
                val adpterbabycare = ArrayAdapter(
                    applicationContext, R.layout.simple_list_item_checked, Towing
                )
                services.setAdapter(adpterbabycare)
            } else if (type.equals("Fuel", ignoreCase = true)) {
                val adpterbabycare = ArrayAdapter(
                    applicationContext, R.layout.simple_list_item_checked, Fuelservices
                )
                services.setAdapter(adpterbabycare)
            }


            btnsubmit.setOnClickListener {
                val desc=addinfo.text.toString().trim()
                val spin=services.selectedItem.toString()
                if(desc.isEmpty()){
                    addinfo.error="Enter your Description"
                }else if(spin=="Choose your service"){
                    Toast.makeText(this@Addrequest, "choose the proper Services", Toast.LENGTH_SHORT).show()
                }else{
                    CoroutineScope(Dispatchers.IO).async {
                        async {
                            try {
                                RetrofitClient.instance.addrequest(
                                    wemail = email,
                                    wname = name,
                                    wnum = mobile,
                                    wtype = type,
                                    uname = uname,
                                    unum = unum,
                                    uemail = uemail,
                                    service = spin,
                                    addinfo = desc,
                                    status = "Pending",
                                    cost = "",
                                    rating = "",
                                    feedback = "",
                                    loc=place,
                                    condition="addrequest"
                                )
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@Addrequest, e.message, Toast.LENGTH_SHORT).show()
                                }
                                null
                            }
                        }.await().let {
                            withContext(Dispatchers.Main){
                                it?.body()?.message?.let {
                                    Toast.makeText(this@Addrequest, "$it", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }.start()

                }
            }


        }
    }
}