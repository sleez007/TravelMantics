package com.example.travelmantics.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelmantics.R
import com.example.travelmantics.adapter.DealAdapter
import com.example.travelmantics.model.TravelDeal
import com.example.travelmantics.utility.FirebaseUtil
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {


    private lateinit var deals:ArrayList<TravelDeal>
    private lateinit var mFirebaseDataBase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mChildEventListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)


    }

    fun showMenu(){
        invalidateOptionsMenu()
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }

    override fun onResume() {
        super.onResume()
        FirebaseUtil.openFbReference("traveldeals",this)
        val dealsAdapter:DealAdapter = DealAdapter()
        rv_deals.adapter=dealsAdapter
        val dealsLayoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        rv_deals.layoutManager=dealsLayoutManager
        FirebaseUtil.attachListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.list_activity_menu,menu)

        val insertMenu: MenuItem = menu!!.findItem(R.id.insert_menu)
        if(FirebaseUtil.isAdmin){
            insertMenu.setVisible(true)
        }else{
            insertMenu.setVisible(false)
        }

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.insert_menu ->{
                 val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.logout_menu->{
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        Log.d("Logout","user has been logged out")
                        FirebaseUtil.attachListener()
                }
                FirebaseUtil.detachListener()
                return true

            }
        }
        return super.onOptionsItemSelected(item)
    }


}
