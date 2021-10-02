package com.example.fitplusplus

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.database.DatabaseError

import androidx.annotation.NonNull
class Profile : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var getAge: TextView
    private lateinit var getGender : TextView
    private lateinit var getHeight : TextView
    private lateinit var getWeight : TextView

    // Database Reference for Firebase.
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        //val id_txt = findViewById<TextView>(R.id.id_txt)
        val name_txt = findViewById<TextView>(R.id.name_txt)
        val email_txt = findViewById<TextView>(R.id.getEmail)

        //id_txt.text = currentUser?.uid
        name_txt.text = currentUser?.displayName
        email_txt.text = currentUser?.email

        val profile_image = findViewById<CircleImageView>(R.id.profile_image)
        Glide.with(this).load(currentUser?.photoUrl).into(profile_image)

        val sign_out_btn = findViewById<LinearLayout>(R.id.sign_out_btn)
        val back_btn = findViewById<ImageButton>(R.id.back_btn)

        back_btn.setOnClickListener{
            val back_intent = Intent(this, Dashboard::class.java)
            startActivity(back_intent)
        }

        sign_out_btn.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        var userExist = FirebaseDatabase.getInstance("https://fitplusplus-63271-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users/" + currentUser?.uid.toString())

        /* val profile_info = ProfileInfo("Ford","Mjstang",2010)
         database.child(currentUser?.uid.toString()).setValue(profile_info)*/
        userExist.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //exist
                }
                else{
                    val getStartIntent = Intent(applicationContext,ProfileGetStarted::class.java)
                    startActivity(getStartIntent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        getAge = findViewById(R.id.getAge)
        getGender = findViewById(R.id.getGender)
        getHeight = findViewById(R.id.getHeight)
        getWeight = findViewById(R.id.getWeight)
        readData()
    }

    private fun readData(){
        databaseReference = FirebaseDatabase.getInstance("https://fitplusplus-63271-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")
        databaseReference.child( mAuth.currentUser?.uid.toString()).get().addOnSuccessListener {
            val age = it.child("age").value
            val gender = it.child("gender").value
            val height = it.child("height").value
            val weight = it.child("weight").value

            //Toast.makeText(this,"Successfuly Read",Toast.LENGTH_SHORT).show()
            //Log.i("Value Database: ",age.toString())
            getAge.text = age.toString()
            getGender.text = gender.toString()
            getHeight.text = height.toString() + "cm"
            getWeight.text = weight.toString() + "kg"
        }.addOnFailureListener{
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }
    }
}
