package com.example.onroadfuelandservicingdemand

import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agri_smartempoweringfarmerswithsoilanalysis.model.RetrofitClient
import com.example.onroadfuelandservicingdemand.databinding.ActivityRegisterBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class Register : AppCompatActivity() {
    private val b by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private lateinit var fused: FusedLocationProviderClient
    var place=""
    var location=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)

        b.linearLogin.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        ArrayAdapter(
            this,
            R.layout.simple_dropdown_item_1line,
            arrayOf("Choose your option","User",
                "Mechanic",
                "Towing",
                "Fuel")
        ).apply {
            b.tvtype.adapter = this
        }

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
                              Toast.makeText(this, "$place", Toast.LENGTH_SHORT).show()
                              b.etuaddress.setText(place)
                              b.etucity.setText(location)
                          }

                      }
                    } else {
                        Toast.makeText(this@Register, "View Point", Toast.LENGTH_SHORT).show()
                        place= Geocoder(this).getFromLocation(it.latitude,it.longitude,
                            1)!![0].getAddressLine(0)
                        location= Geocoder(this).getFromLocation(it.latitude,it.longitude,
                            1)!![0].locality

                    }
                    b.etuaddress.setText(place)
                    b.etucity.setText(location)
                }

            }
            fused.lastLocation.addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
            fused.lastLocation.addOnCanceledListener {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        b.btnsubmit.setOnClickListener {
            val name=  b.etname.text.toString().trim()
            val email=  b.etemail.text.toString().trim()
            val address=  b.etuaddress.text.toString().trim()
            val city=  b.etucity.text.toString().trim()
            val pass=  b.etupass.text.toString().trim()
            val type=b.tvtype.selectedItem.toString()
            val num=b.etunum.text.toString().trim()

            if(name.isEmpty()){b.etname.error="Enter Your Name"}
            else if(email.isEmpty()){b.etemail.error="Enter Your Email"}
            else if(address.isEmpty()){b.etuaddress.error="Enter Your Address "}
            else if( city.isEmpty()){b.etucity.error="Enter Your city "}
            else if(pass.isEmpty()){b.etupass.error="Enter Your Password"}
            else if(num.isEmpty()){b.etunum.error="Enter Your Number"}
            else if(type=="Choose your option"){
                Toast.makeText(this, "Choose proper option", Toast.LENGTH_SHORT).show()
            }else{
                if(num.count()==10){
                    CoroutineScope(Dispatchers.IO).async {
                        async {
                            try {
                                RetrofitClient.instance.register(
                                    name =name,
                                    email = email,
                                    password =pass,
                                    mobile = num,
                                    type = type,
                                    address = address,
                                    loc = "",
                                    city = city,
                                    condition = "register"

                                )
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@Register, e.message, Toast.LENGTH_SHORT).show()
                                }
                                null
                            }
                        }.await().let {
                            withContext(Dispatchers.Main){
                                it?.body()?.message?.let {
                                    Toast.makeText(this@Register, "$it", Toast.LENGTH_SHORT).show()
                                    b.etname.text!!.clear()
                                    b.etemail.text!!.clear()
                                    b.etuaddress.text!!.clear()
                                    b.etucity.text!!.clear()
                                    b.etupass.text!!.clear()
                                    b.tvtype.setSelection(0)
                                    b.etunum.text!!.clear()
                                    finish()
                                    startActivity(Intent(this@Register,MainActivity::class.java))

                                }

                            }
                        }
                    }.start()
                }else{
                    b.etunum.error="Enter Your Number properly"
                }

            }
        }
    }
}