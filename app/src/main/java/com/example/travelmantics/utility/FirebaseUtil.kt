package com.example.travelmantics.utility

import android.app.Activity

import android.util.Log
import com.example.travelmantics.activities.ListActivity
import com.example.travelmantics.model.TravelDeal
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirebaseUtil private constructor(){
    companion object{
        lateinit var mFirebaseDatabase:FirebaseDatabase
        lateinit var mDatabaseReference:DatabaseReference
        var firebaseUtil: FirebaseUtil?=null
        lateinit var mDeal : ArrayList<TravelDeal>
        lateinit var mFireBaseAuth:FirebaseAuth
        lateinit var mAuthListener: FirebaseAuth.AuthStateListener
        private const val RC_SIGN_IN = 123
        lateinit var mStoreage:FirebaseStorage
        lateinit var mStorageRef:StorageReference
        var isAdmin: Boolean= false;
        lateinit var caller: Activity

        fun openFbReference(ref:String, callerActivity:Activity){
          if(firebaseUtil == null){
              firebaseUtil = FirebaseUtil()
              mFirebaseDatabase = FirebaseDatabase.getInstance()
              mFireBaseAuth= FirebaseAuth.getInstance()
              caller= callerActivity
              mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                  val firebaseUser = firebaseAuth.currentUser
                  Log.i("clapp","i got here")
                  if (firebaseUser != null) {
                       val userId= firebaseUser.uid
                      checkIfAdmin(userId)

                  } else {

                      val providers = arrayListOf(
                          AuthUI.IdpConfig.EmailBuilder().build(),
                          AuthUI.IdpConfig.GoogleBuilder().build())

                          caller!!.startActivityForResult(
                          AuthUI.getInstance()
                              .createSignInIntentBuilder()
                             .setAvailableProviders(providers)
                              .build(),
                          RC_SIGN_IN)

                  }
              }
              connectStorage()

          }
            mDeal = ArrayList<TravelDeal>()
           mDatabaseReference = mFirebaseDatabase.reference.child(ref);
        }

        private fun checkIfAdmin(userId: String) {
          isAdmin= false
            val ref: DatabaseReference = mFirebaseDatabase.reference.child("administrators").child(userId)
            val listener:ChildEventListener = object:ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    isAdmin=true
                    val act:Activity = caller as ListActivity
                    if(act is ListActivity){
                      //  caller as ListActivity
                        act.showMenu()
                    }

                    //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    //To change body of created functions use File | Settings | File Templates.
                }

            }
            ref.addChildEventListener(listener)

        }

        fun attachListener(){
            //Log.i("runme","i ran")
            mFireBaseAuth.addAuthStateListener(mAuthListener)
          //  mFireBaseAuth.addAuthStateListener { mAuthListener }
        }

        fun detachListener(){
            mFireBaseAuth.removeAuthStateListener(mAuthListener)
            //mFireBaseAuth.removeAuthStateListener { mAuthListener  }
        }

        fun connectStorage(){
            mStoreage= FirebaseStorage.getInstance()
            mStorageRef =  mStoreage.reference.child("deals_picture")

        }


    }
}