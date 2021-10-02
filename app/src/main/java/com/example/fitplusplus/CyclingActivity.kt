package com.example.fitplusplus


import `in`.unicodelabs.kdgaugeview.KdGaugeView
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.view.View.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class CyclingActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    var speedoMeterView: KdGaugeView? = null
    var txtLat: TextView? = null
    private lateinit var startCycling : Button
    private lateinit var stopCycling : Button
    private lateinit var cyclingChronometer : Chronometer
    private lateinit var setDistance : TextView
    private lateinit var setLat : EditText
    private lateinit var setLong : EditText

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var PERMISSION_ID = 44
    var p1 = 0f
    var p2:kotlin.Float = 0f
    var p3:kotlin.Float = 0f
    var p4:kotlin.Float = 0f
    private val INTERVAL = (1000 * 2).toLong()
    private val FASTEST_INTERVAL = (1000 * 1).toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cycling)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        speedoMeterView = findViewById(R.id.speedMeter);
        txtLat = findViewById(R.id.textView);
        setDistance = findViewById(R.id.setDistance)

        startCycling = findViewById(R.id.startCycling)
        stopCycling = findViewById(R.id.stopCycling)
        stopCycling.visibility = GONE
        cyclingChronometer = findViewById(R.id.cyclingChronometer)

        setLat = findViewById(R.id.setLat)
        setLong = findViewById(R.id.setLong)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let { it: Location ->
                        // Logic to handle location object
                        p1 = location.getLongitude().toFloat()
                        p2 = location.getLatitude().toFloat()
                        val dSpeed: Float = location.getSpeed()
                        val a = 3.6 * dSpeed
                        val kmhSpeed = Math.round(a).toInt()
                        txtLat!!.text = "Longitude:" + location.getLongitude()
                            .toString() + " Latitude:" + location.getLatitude()
                            .toString() + "  SPEED=" + kmhSpeed
                        speedoMeterView!!.setSpeed(kmhSpeed.toFloat())

                        requestNewLocationData()
                    } ?: kotlin.run {
                        // Handle Null case or Request periodic location update https://developer.android.com/training/location/receive-location-updates
                        requestNewLocationData()
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(INTERVAL)
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            p3 = mLastLocation.getLongitude().toFloat()
            p4 = mLastLocation.getLatitude().toFloat()

            val dSpeed: Float = mLastLocation.getSpeed()
            val a = 3.6 * dSpeed
            val kmhSpeed = Math.round(a).toInt()
            txtLat!!.text = "Longitude:" + mLastLocation.getLongitude()
                .toString() + " Latitude:" + mLastLocation.getLatitude()
                .toString() + "  SPEED=" + kmhSpeed

            startCycling.setOnClickListener {
                stopCycling.visibility = VISIBLE
                startCycling.visibility = GONE
                //val distance =   distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double)
                //setDistance.text = "Distance: " + distance
                cyclingChronometer.start()
                setLat.setText(mLastLocation.getLatitude()
                    .toString())
                setLong.setText(mLastLocation.getLongitude()
                    .toString())

                val lat1 = setLat.text.toString().toDouble()
                val lon1 = setLong.text.toString().toDouble()

                val distance = distance(lat1, lon1, mLastLocation.getLatitude(), mLastLocation.getLongitude())
                val decimalDistanceRounded:Double = String.format("%.2f", distance).toDouble()
                setDistance.text = decimalDistanceRounded.toString()
            }
            stopCycling.setOnClickListener {
                stopCycling.visibility = GONE
                startCycling.visibility = VISIBLE
                cyclingChronometer.stop()
            }
            speedoMeterView!!.setSpeed(kmhSpeed.toFloat())
            getLastLocation()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLastLocation()
        }
    }

    //get distance
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}

