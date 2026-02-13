package com.example.onroadfuelandservicingdemand

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agri_smartempoweringfarmerswithsoilanalysis.model.RetrofitClient
import com.example.onroadfuelandservicingdemand.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ymts0579.model.model.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private  val bind by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
        }

        val type=getSharedPreferences("user", MODE_PRIVATE).getString("type", "")!!

        when (type) {
            "User"->{
                startActivity(Intent(this@MainActivity,UserDashboard::class.java))
                finish()
            }
            "Mechanic"->{
                startActivity(Intent(this@MainActivity,ServiceDashboard::class.java))
                finish()
            }
            "Towing"->{
                startActivity(Intent(this@MainActivity,ServiceDashboard::class.java))
                finish()
            }
            "Fuel"->{
                startActivity(Intent(this@MainActivity,ServiceDashboard::class.java))
                finish()
            }
            else -> {
                bind.btnregister.setOnClickListener {
                    startActivity(Intent(this,Register::class.java))
                    finish()
                }
                bind.tvsignin.setOnClickListener {
                   val bb =BottomSheetDialog(this)
                    bb.setContentView(R.layout.cardlogin)
                   val etemail=bb.findViewById<EditText>(R.id.etemail)!!
                   val etpassword=bb.findViewById<EditText>(R.id.etpassword)!!
                   val btnlogin=bb.findViewById<Button>(R.id.btnlogin)!!
                    
                    
                    btnlogin.setOnClickListener { 
                        val email=etemail.text.toString().trim()
                        val pass=etpassword.text.toString().trim()
                        
                        
                        if(email.isEmpty()){
                            etemail.error="Enter your Email"
                        }else if(pass.isEmpty()){
                            etpassword.error="Enter your password"
                        }else {
                            readforlogin(email,pass)
                        }
                    }
                    bb.show()
                }
            }
        }

    }

    private fun readforlogin(email: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitClient.instance.login(email,pass,"login")
                .enqueue(object: Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>, response: Response<LoginResponse>
                    ) {
                        if(!response.body()?.error!!){
                            if (response.body()?.user!=null) {
                                response.body()?.user?.apply {
                                    getSharedPreferences("user", MODE_PRIVATE).edit().apply {
                                        putString("name", name)
                                        putString("email", email)
                                        putString("password", password)
                                        putString("mobile", mobile)
                                        putString("type", type)
                                        putString("address", address)
                                        putString("city", city)
                                        putInt("id",id)
                                        apply()
                                    }
                                    when (type) {
                                        "User"->{
                                            startActivity(Intent(this@MainActivity,UserDashboard::class.java))
                                            finish()
                                        }
                                        "Mechanic"->{
                                            startActivity(Intent(this@MainActivity,ServiceDashboard::class.java))
                                            finish()
                                        }
                                        "Towing"->{
                                            startActivity(Intent(this@MainActivity,ServiceDashboard::class.java))
                                            finish()
                                        }
                                        "Fuel"->{
                                            startActivity(Intent(this@MainActivity,ServiceDashboard::class.java))
                                            finish()
                                        }


                                    }
                                }


                            }
                        }else{
                            Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()


                    }

                })
        }

    }
}