package com.example.onroadfuelandservicingdemand

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.onroadfuelandservicingdemand.User.History
import com.example.onroadfuelandservicingdemand.User.Userserviceproviders
import com.example.onroadfuelandservicingdemand.databinding.ActivityUserDashboardBinding

class UserDashboard : AppCompatActivity() {
    private val b by lazy {
        ActivityUserDashboardBinding.inflate(layoutInflater)
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)

        b.textView.text="WELCOME "+getSharedPreferences("user", MODE_PRIVATE).getString("name", "")!!

        b.linearhistory.setOnClickListener { startActivity(Intent(this,History::class.java)) }

        b.linearprofile.setOnClickListener { startActivity(Intent(this,profile::class.java)) }

        b.linearfuel.setOnClickListener {
            startActivity(Intent(this,Userserviceproviders::class.java).apply {
                putExtra("type","fuel")
            })

        }
        b.cardmechanic.setOnClickListener {
            startActivity(Intent(this,Userserviceproviders::class.java).apply {
                putExtra("type","mechanic")
            })
        }
        b.cardtowing.setOnClickListener {
            startActivity(Intent(this,Userserviceproviders::class.java).apply {
                putExtra("type","towing")
            })
        }

        b.btnlogout.setOnClickListener {
            val alertdialog= AlertDialog.Builder(this)
            alertdialog.setTitle("LOGOUT")
            alertdialog.setIcon(R.drawable.logo)
            alertdialog.setCancelable(false)
            alertdialog.setMessage("Do you Want to Logout?")
            alertdialog.setPositiveButton("Yes"){ alertdialog, which->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                val  shared=getSharedPreferences("user", MODE_PRIVATE)
                shared.edit().clear().apply()
                alertdialog.dismiss()
            }
            alertdialog.setNegativeButton("No"){alertdialog,which->
                Toast.makeText(this,"thank you", Toast.LENGTH_SHORT).show()
                alertdialog.dismiss()
            }
            alertdialog.show()

        }
    }
}