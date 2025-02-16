package com.example.traintracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class PersonalInfoActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnValidatePersonalInfo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)

        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etEmail = findViewById(R.id.etEmail)
        btnValidatePersonalInfo = findViewById(R.id.btnValidatePersonalInfo)

        val departure = intent.getStringExtra("departureCity") ?: ""
        val arrival = intent.getStringExtra("arrivalCity") ?: ""
        val date = intent.getStringExtra("travelDate") ?: ""
        val selectedTime = intent.getStringExtra("selectedTime") ?: ""
        val trainName = intent.getStringExtra("trainName") ?: ""
        val price = intent.getStringExtra("price") ?: ""

        btnValidatePersonalInfo.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()

            val intent = Intent(this, ConfirmationActivity::class.java).apply {
                putExtra("departureCity", departure)
                putExtra("arrivalCity", arrival)
                putExtra("travelDate", date)
                putExtra("selectedTime", selectedTime)
                putExtra("trainName", trainName)
                putExtra("price", price)
                putExtra("firstName", firstName)
                putExtra("lastName", lastName)
                putExtra("email", email)
            }
            startActivity(intent)
        }
    }
}
