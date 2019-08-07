package com.example.travelmantics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelmantics.holders.DealsHolder
import com.example.travelmantics.model.TravelDeal
import com.example.travelmantics.utility.FirebaseUtil
import com.example.travelmantics.R
import com.google.firebase.database.*

class DealAdapter():RecyclerView.Adapter<DealsHolder>() {

    private  var deals:ArrayList<TravelDeal>
    private  var mFirebaseDataBase: FirebaseDatabase
    private  var mDatabaseReference: DatabaseReference
    private  var mChildEventListener: ChildEventListener

    init {
        //FirebaseUtil.openFbReference("traveldeals",)
        mFirebaseDataBase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        deals=FirebaseUtil.mDeal
        mChildEventListener =object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val travelDeal:TravelDeal = dataSnapshot.getValue(TravelDeal::class.java ) as TravelDeal
                println("${travelDeal.title}")
                travelDeal.id=dataSnapshot.key
                deals.add(travelDeal)
                notifyItemInserted(deals.size-1)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                //To change body of created functions use File | Settings | File Templates.
            }
        }
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealsHolder {
        val context:Context = parent.context
        val itemViewHolder : View =LayoutInflater.from(context).inflate( R.layout.rv_row ,parent,false)
        return DealsHolder(itemViewHolder)
    }

    override fun getItemCount(): Int {
        return deals.size
    }

    override fun onBindViewHolder(holder: DealsHolder, position: Int) {
        val deal:TravelDeal = deals.get(position);
        holder.bind(deal)
    }
}