package com.example.fitplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import androidx.annotation.NonNull

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.cardview.widget.CardView


class Dashboard : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        //val id_txt = findViewById<TextView>(R.id.id_txt)
        val name_txt = findViewById<TextView>(R.id.name_txt)
        //val email_txt = findViewById<TextView>(R.id.email_txt)

        //id_txt.text = currentUser?.uid
        name_txt.text = currentUser?.displayName
        //email_txt.text = currentUser?.email

        val profile_image = findViewById<CircleImageView>(R.id.profile_image)
        Glide.with(this).load(currentUser?.photoUrl).into(profile_image)

        /*val sign_out_btn = findViewById<Button>(R.id.sign_out_btn)
        sign_out_btn.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }*/
        profile_image.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        //menu Activity
        val cyclingBtn = findViewById<CardView>(R.id.cyclingBtn)
        val runningBtn = findViewById<CardView>(R.id.runningBtn)
        cyclingBtn.setOnClickListener {
            val cyclingActivityIntent = Intent(this,CyclingActivity::class.java)
            startActivity(cyclingActivityIntent)
        }
        runningBtn.setOnClickListener {
            val runningActivityIntent = Intent(this,RunActivity::class.java)
            startActivity(runningActivityIntent)
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
    }

    private fun getAge(year: Int, month: Int, day: Int): String? {
        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        dob.set(year, month, day)
        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        val ageInt = age
        return ageInt.toString()
    }
}