package com.example.onroadfuelandservicingdemand

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agri_smartempoweringfarmerswithsoilanalysis.model.RetrofitClient
import com.example.onroadfuelandservicingdemand.databinding.ActivityProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class profile : AppCompatActivity() {
    private val b by lazy {
        ActivityProfileBinding.inflate(layoutInflater)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)


       var  email=""
       var  type=""
       var  id=0
        getSharedPreferences("user", MODE_PRIVATE).apply {
           b.etname.setText( getString("name", "").toString())
            email=getString("email", "").toString()
           b.etupass.setText( getString("password", "").toString())
           b.etunum.setText( getString("mobile", "").toString())
            type=getString("type", "").toString()
           b.etuaddress.setText( getString("address", "").toString())
           b.etucity.setText( getString("city", "").toString())
           id= getInt("id",0)

        }

        b.btnsubmit.setOnClickListener {
          val name = b.etname.text.toString().trim()
          val num=  b.etunum.text.toString().trim()
          val address=  b.etuaddress.text.toString().trim()
          val city=  b.etucity.text.toString().trim()
          val pass = b.etupass.text.toString().trim()


            if(name.isEmpty()){b.etname.error="Enter  your Name" }
            else if(num.isEmpty()){b.etunum.error="Enter your Number"}
            else if(address.isEmpty()){b.etuaddress.error="Enter your Address"}
            else if(city.isEmpty()){b.etucity.error="Enter your city"}
            else if(pass.isEmpty()){b.etupass.error="Enter your password"}
            else {
                if(num.count()==10){
                    CoroutineScope(Dispatchers.IO).async {
                        async {
                            try {
                                RetrofitClient.instance.updateusers(
                                    name = name,
                                    mobile =num,
                                    password =pass,
                                    city = city,
                                    address =address,
                                    id = id,
                                    condition = "update"
                                )


                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@profile, e.message, Toast.LENGTH_SHORT).show()
                                }
                                null
                            }
                        }.await().let {
                            withContext(Dispatchers.Main){
                                it?.body()?.message?.let {
                                    Toast.makeText(this@profile, "$it", Toast.LENGTH_SHORT).show()
                                    getSharedPreferences("user", MODE_PRIVATE).edit().apply {
                                        putString("name", name)
                                        putString("email", email)
                                        putString("password", pass)
                                        putString("mobile", num)
                                        putString("type", type)
                                        putString("address", address)
                                        putString("city", city)
                                        putInt("id",id)
                                        apply()
                                    }


                                }

                            }
                        }
                    }.start()

                }else{
                    b.etunum.error="Enter your Number "
                }
            }
        }



    }
}