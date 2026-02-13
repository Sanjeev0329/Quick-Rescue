package com.example.onroadfuelandservicingdemand.User

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.gsm.SmsManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agri_smartempoweringfarmerswithsoilanalysis.model.RetrofitClient
import com.example.connectinglocalworkers.model.User
import com.example.onroadfuelandservicingdemand.R
import com.example.onroadfuelandservicingdemand.databinding.ActivityHistoryBinding
import com.example.onroadfuelandservicingdemand.databinding.CardhistoryBinding
import com.example.onroadfuelandservicingdemand.databinding.CarduserserviceBinding
import com.example.onroadfuelandservicingdemand.model.request
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ymts0579.model.model.DefaultResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class History : AppCompatActivity() {
    private val b by lazy {
        ActivityHistoryBinding.inflate(layoutInflater)
    }
    var uemail=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)

        getSharedPreferences("user", MODE_PRIVATE).apply {
            uemail=getString("email", "").toString()

        }

        val p= ProgressDialog(this)
        p.show()
        CoroutineScope(Dispatchers.IO).async {
            async {
                try {
                    RetrofitClient.instance.userhistory("$uemail","userhistory")
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@History, e.message, Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }.await().let {
                withContext(Dispatchers.Main){
                    it!!.body()?.user?.let{
                        Toast.makeText(this@History, "$it", Toast.LENGTH_SHORT).show()
                        p.dismiss()
                        b.listhistory.adapter=userhistoyrAdapter(this@History,it)
                        b.listhistory.layoutManager=LinearLayoutManager(this@History)

                    }


                }
            }
        }.start()




    }


    class userhistoyrAdapter(var context: Context, var listdata: ArrayList<request>):
        RecyclerView.Adapter<userhistoyrAdapter.DataViewHolder>(){

        inner class DataViewHolder(val view: CardhistoryBinding) : RecyclerView.ViewHolder(view.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
            return DataViewHolder(
                CardhistoryBinding.inflate(
                    LayoutInflater.from(context),parent,
                    false))
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: DataViewHolder, @SuppressLint("RecyclerView") position:Int) {
            with(holder.view){

                listdata[position].apply {

                    tvservcname.text=service
                    tvservccat.text=addinfo
                    tvhistworkname.text=wname
                    tvworkmobile.text=wnum
                    tvhiststaus.text=status

                    imgphone.setOnClickListener {
                        val intent = Intent(Intent.ACTION_CALL).apply {
                            data = Uri.parse("tel:$wnum")
                            putExtra("videocall", true)
                        }
                        context.startActivity(intent)

                    }

                    if(feedback==""){
                        btnfeedback.visibility= View.VISIBLE
                    }else{
                        btnfeedback.visibility= View.GONE
                    }

                    btnfeedback.setOnClickListener {


                        val dd= BottomSheetDialog(context)
                        dd.setContentView(R.layout.cardfeedback)
                        val etfeedback=dd.findViewById<EditText>(R.id.etfeedback)!!
                        val btnsubmit=dd.findViewById<Button>(R.id.btnsubmit)!!
                        val rating=dd.findViewById<RatingBar>(R.id.rating)!!
                        btnsubmit.setOnClickListener {
                            val rate=rating.rating.toString()
                            val feed=etfeedback.text.toString().trim()
                            if(rate=="0.0"){
                                Toast.makeText(context, "give your rating", Toast.LENGTH_SHORT).show()
                            }else if(feed.isEmpty()){
                                Toast.makeText(context, "Enter your Feedback", Toast.LENGTH_SHORT).show()
                            }else {
                                CoroutineScope(Dispatchers.IO).async {
                                    async {
                                        try {
                                            RetrofitClient.instance.updaterating(rate,feed,id,"updaterating")
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

                                            }
                                        }
                                    }
                                }.start()
                    }
                        }

                        dd.show()

                    }

                }

            }

        }


        override fun getItemCount() = listdata.size
    }
}