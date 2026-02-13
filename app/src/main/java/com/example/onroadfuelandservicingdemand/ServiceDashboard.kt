package com.example.onroadfuelandservicingdemand

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.gsm.SmsManager
import android.telephony.gsm.SmsManager.*
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agri_smartempoweringfarmerswithsoilanalysis.model.RetrofitClient
import com.example.onroadfuelandservicingdemand.User.History
import com.example.onroadfuelandservicingdemand.databinding.ActivityServiceDashboardBinding
import com.example.onroadfuelandservicingdemand.databinding.CardhistoryBinding
import com.example.onroadfuelandservicingdemand.databinding.RequestslistBinding
import com.example.onroadfuelandservicingdemand.model.request
import com.example.onroadfuelandservicingdemand.services.MapsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class ServiceDashboard : AppCompatActivity() {
    private val b by lazy {
        ActivityServiceDashboardBinding.inflate(layoutInflater)
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)

        b.profile.setOnClickListener {
            startActivity(Intent(this,profile::class.java))
        }

        b.textView2.text="WELCOME "+getSharedPreferences("user", MODE_PRIVATE).getString("name", "")!!


        val email=getSharedPreferences("user", MODE_PRIVATE).getString("email", "")!!

        val p= ProgressDialog(this)
        p.show()
        CoroutineScope(Dispatchers.IO).async {
            async {
                try {
                    RetrofitClient.instance.serviceshistory(email,"serviceshistory")
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ServiceDashboard, e.message, Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }.await().let {
                withContext(Dispatchers.Main){
                    it!!.body()?.user?.let{
                        Toast.makeText(this@ServiceDashboard, "$it", Toast.LENGTH_SHORT).show()
                        p.dismiss()
                        b.listviewservices.adapter=serviceAdapter(this@ServiceDashboard,it)
                        b.listviewservices.layoutManager=LinearLayoutManager(this@ServiceDashboard)

                    }


                }
            }
        }.start()

        b.logout.setOnClickListener {
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


    class serviceAdapter(var context: Context, var listdata: ArrayList<request>):
        RecyclerView.Adapter<serviceAdapter.DataViewHolder>(){

        inner class DataViewHolder(val view: RequestslistBinding) : RecyclerView.ViewHolder(view.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
            return DataViewHolder(
                RequestslistBinding.inflate(
                    LayoutInflater.from(context),parent,
                    false))
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: DataViewHolder, @SuppressLint("RecyclerView") position:Int) {
            with(holder.view){

                listdata[position].apply {

                    tvwcustname.text="Customer Name : ${uname}"
                    tvwcustmob.text="Customer Mobile : ${unum}"
                    tvwservccat.text=service
                    tvwaddinfo.text=addinfo
                    tvwstatus.text=status
                    btncompleted.visibility=View.GONE
                    linearfeed.visibility=View.GONE
                    tvfeedback.text=feedback

                    var floaft=0.0f
                    rating.forEach {
                        if(it!=' '&&it.isDigit()||it=='.'){
                            floaft=it.toFloat()
                        }
                    }
                    ratingbar.isIndeterminate=true
                    ratingbar.rating=floaft

                    imgphone.setOnClickListener {
                        val intent = Intent(Intent.ACTION_CALL).apply {
                            data = Uri.parse("tel:$wnum")
                            putExtra("videocall", true)
                        }
                        context.startActivity(intent)
                    }


                    imglocation.setOnClickListener {
                        context.startActivity(Intent(context,MapsActivity::class.java).apply {
                            putExtra("loc",loc)
                        })

                    }


                    if(feedback==""){
                        linearfeed.visibility=View.GONE
                    }else{
                        linearfeed.visibility=View.VISIBLE
                    }

                    if(status=="Accepted"){
                        btncompleted.visibility=View.VISIBLE
                    }else{
                        btncompleted.visibility=View.GONE
                    }

                    if(status=="Pending"){
                        routbtns.visibility= View.VISIBLE
                    }else{
                        routbtns.visibility= View.GONE
                    }
                    btncnfrmrequest.setOnClickListener {
                        updatestatus("Accepted",id,unum)
                    }
                    btnnotavil.setOnClickListener {
                        updatestatus("Not available now",id,unum)
                    }

                    btncompleted.setOnClickListener {
                        updatestatus("Completed",id,unum)
                    }
                }

            }

        }

        private fun updatestatus(status: String, id: Int,unum:String) {
            CoroutineScope(Dispatchers.IO).async {
                async {
                    try {
                        RetrofitClient.instance.updatestatus(status,id,"updatestatus")
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                        null
                    }
                }.await().let {
                    withContext(Dispatchers.Main){
                        it?.body()?.message?.let {
                            Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()


                            if (TextUtils.isDigitsOnly(unum)) {
                                val smsManager: SmsManager = getDefault()
                                smsManager.sendTextMessage(unum, null, "your Request is $status", null, null)

                            }
                        }
                    }
                }
            }.start()
        }


        override fun getItemCount() = listdata.size
    }
}