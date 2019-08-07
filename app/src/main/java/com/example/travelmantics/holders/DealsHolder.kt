package com.example.travelmantics.holders

import android.content.Intent
import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.travelmantics.R
import com.example.travelmantics.activities.MainActivity
import com.example.travelmantics.model.TravelDeal
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class DealsHolder (itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
    lateinit var currentPayLoad:TravelDeal

    override fun onClick(v: View?) {
       val position: Int = adapterPosition
       val intent:Intent = Intent(v?.context,MainActivity::class.java)
        intent.putExtra("deal",currentPayLoad)
        v?.context?.startActivity(intent)
        Toast.makeText(v!!.context,"clicked $position",Toast.LENGTH_LONG).show()
    }
    init{
        itemView.setOnClickListener(this)
    }

    val tvTitle: TextView= itemView.findViewById<TextView>(R.id.tv_title)
    val tvDescription: TextView = itemView.findViewById<TextView>(R.id.tv_description)
    val tvPrice: TextView = itemView.findViewById<TextView>(R.id.tv_price)
    val imageDeal: ImageView = itemView.findViewById<ImageView>(R.id.image_deal)

    fun bind(deal: TravelDeal){
       tvTitle.text= deal.title
       tvDescription.text=deal.description
       tvPrice.text=deal.price
        showImage(deal.imageUrl)
        currentPayLoad=deal
    }

    fun showImage(url:String?) = Picasso.get().load(url).resize(160,160).centerCrop().into(imageDeal)

}