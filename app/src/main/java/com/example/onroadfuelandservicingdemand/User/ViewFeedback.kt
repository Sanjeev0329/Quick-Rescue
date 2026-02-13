package com.example.onroadfuelandservicingdemand.User

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agri_smartempoweringfarmerswithsoilanalysis.model.RetrofitClient
import com.example.onroadfuelandservicingdemand.R
import com.example.onroadfuelandservicingdemand.ServiceDashboard
import com.example.onroadfuelandservicingdemand.databinding.ActivityViewFeedbackBinding
import com.example.onroadfuelandservicingdemand.model.request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class ViewFeedback : AppCompatActivity() {
    private val b by lazy {
        ActivityViewFeedbackBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(b.root)


        val email=intent.getStringExtra("email").toString()

        val p= ProgressDialog(this)
        p.show()
        CoroutineScope(Dispatchers.IO).async {
            async {
                try {
                    RetrofitClient.instance.serviceshistory(email,"serviceshistory")
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ViewFeedback, e.message, Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }.await().let {
                withContext(Dispatchers.Main){
                    it!!.body()?.user?.let{
                        Toast.makeText(this@ViewFeedback, "$it", Toast.LENGTH_SHORT).show()
                        p.dismiss()
                        b.listfeed.layoutManager=LinearLayoutManager(this@ViewFeedback)
                        b.listfeed.adapter=feedbackadapter(this@ViewFeedback,it)

                    }


                }
            }
        }.start()
    }


    class feedbackadapter(var context: Context, var listdata: ArrayList<request>):
        RecyclerView.Adapter<feedbackadapter.DataViewHolder>(){
        var id=0
        class DataViewHolder(view: View) : RecyclerView.ViewHolder(view) {


            val  ratingbar: RatingBar =view.findViewById(R.id.rating)
            val  tvfeedback: TextView =view.findViewById(R.id.tvfeedback)
            val linearfeed: LinearLayout =view.findViewById(R.id.linearfeed)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.cardfeed, parent, false)
            return DataViewHolder(view)
        }

        override fun onBindViewHolder(holder: DataViewHolder, @SuppressLint("RecyclerView") position:Int) {
            holder.apply {
                listdata.get(position).apply {

                    tvfeedback.text=feedback

                    var floaft=0.0f
                    rating.forEach {
                        if(it!=' '&&it.isDigit()||it=='.'){
                            floaft=it.toFloat()
                        }
                    }
                    ratingbar.isIndeterminate=true
                    ratingbar.rating=floaft

                    if(feedback.isEmpty()){
                        linearfeed.visibility= View.GONE
                    }else{
                        linearfeed.visibility= View.VISIBLE
                    }





                }

            }

        }




        override fun getItemCount() = listdata.size
    }
}