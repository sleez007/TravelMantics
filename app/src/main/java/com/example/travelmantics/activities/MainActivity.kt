package com.example.travelmantics.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.travelmantics.R
import com.example.travelmantics.model.TravelDeal
import com.example.travelmantics.utility.FirebaseUtil
import com.firebase.ui.auth.data.model.Resource
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        val  PICTURE_RESULT:Int = 42
    }

    private lateinit var mFirebaseDataBase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private var deal: TravelDeal? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseUtil.openFbReference("traveldeals",this)
        mFirebaseDataBase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        val intentOb:Intent = intent
        val deal:TravelDeal? = intentOb.getSerializableExtra("deal") as? TravelDeal ?: TravelDeal()
        this.deal =deal
        txt_title.setText(deal?.title);
        txt_description.setText(deal?.description)
        txt_price.setText(deal?.price)
        showImage(deal?.imageUrl)
        btn_image.setOnClickListener{
            it->
            val intent = Intent()
            intent.type = "image/jpeg"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_RESULT)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK){
           val imageUri: Uri = data!!.data
            val ref:StorageReference = FirebaseUtil.mStorageRef.child(imageUri.lastPathSegment)
            ref.putFile(imageUri).continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.i("activa",downloadUri.toString())
                    deal?.imageUrl=downloadUri.toString();
                    showImage(downloadUri.toString())

                } else {
                    // Handle failures
                    // ...
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.save_menu,menu)
        if(FirebaseUtil.isAdmin){
            enableEditText(true)
            menu!!.findItem(R.id.delete_menu).setVisible(true)
            menu!!.findItem(R.id.save_menu).setVisible(true)
            btn_image.isEnabled= true

        }else{
            menu!!.findItem(R.id.save_menu).setVisible(false)
            menu!!.findItem(R.id.delete_menu).setVisible(false)
            enableEditText(false)
            btn_image.isEnabled= false
        }



        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.save_menu ->{
                saveDeal()
                Toast.makeText(this,"Deal saved", Toast.LENGTH_LONG).show()
                clean()
                backToList()
                return true
            }
            R.id.delete_menu ->{
                deleteDeal()
                Toast.makeText(this,"Deal deleted ", Toast.LENGTH_LONG).show()
                backToList()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clean() {
         txt_title.setText("");
         txt_description.setText("")
         txt_price.setText("")
    }

    private fun saveDeal() {
        val title: String = txt_title.text.toString()
        val description: String  =txt_description.text.toString()
        val price: String = txt_price.text.toString()
        deal?.price= price
        deal?.description=description
        deal?.title = title
        when(deal?.id){
            null->{
                mDatabaseReference.push().setValue(deal)
            }else->{
               mDatabaseReference.child(deal!!.id as String).setValue(deal)
            }
        }

    }

    private fun deleteDeal(){
        if( deal == null){
             Toast.makeText(this,"Please save a deal before deleting it",Toast.LENGTH_LONG).show()
              return
        }
        mDatabaseReference.child(deal?.id as String).removeValue()
    }

    private fun backToList(){
       val   intent:Intent =Intent(this,ListActivity::class.java );
        startActivity(intent)
    }

    public fun enableEditText(isEnabled: Boolean){
      txt_title.isEnabled=isEnabled
      txt_description.isEnabled=isEnabled
      txt_price.isEnabled=isEnabled

    }

    fun showImage(url:String?){
        val width:Int = Resources.getSystem().displayMetrics.widthPixels;
        Picasso.get().load(url).resize(width,width*2/3).centerCrop().into(image)
    }
}
