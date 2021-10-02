package com.example.fitplusplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ProfileGetStarted : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_get_started)

        var database = FirebaseDatabase.getInstance("https://fitplusplus-63271-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        //get started
        val btnstart = findViewById<Button>(R.id.btngetStarted)
        val gender = findViewById<TextView>(R.id.setGender)
        val m = findViewById<EditText>(R.id.month)
        val d = findViewById<EditText>(R.id.day)
        val y = findViewById<EditText>(R.id.year)
        val height = findViewById<EditText>(R.id.height)
        val weight = findViewById<EditText>(R.id.weight)

        // access the items of the list
        val languages = resources.getStringArray(R.array.gender)

        // access the spinner
        val spinner = findViewById<Spinner>(R.id.gender)
        val setGender = findViewById<TextView>(R.id.setGender)

        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, languages)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    setGender.text = languages[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        btnstart.setOnClickListener {
            val month = m.text.toString().toInt()
            val day = d.text.toString().toInt()
            val year = y.text.toString().toInt()

            val current_age = getAge(year,month,day).toString()
            val height_current = height.text.toString().toInt()
            val curret_weight = weight.text.toString().toInt()

            val profile_info = ProfileInfo(gender.text.toString(),current_age,height_current,curret_weight)
            database.child("Users").child(currentUser?.uid.toString()).setValue(profile_info)
            val getStartIntent = Intent(applicationContext,Dashboard::class.java)
            startActivity(getStartIntent)
        }
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