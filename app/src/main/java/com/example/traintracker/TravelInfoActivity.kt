package com.example.traintracker


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import java.util.Calendar

class TravelInfoActivity : AppCompatActivity() {

    private lateinit var etDepartureCity: EditText
    private lateinit var etArrivalCity: EditText
    private lateinit var etTravelDate: EditText
    private lateinit var btnSearchTrains: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_info)

        etDepartureCity = findViewById(R.id.etDepartureCity)
        etArrivalCity = findViewById(R.id.etArrivalCity)
        etTravelDate = findViewById(R.id.etTravelDate)
        btnSearchTrains = findViewById(R.id.btnSearchTrains)

        // Ouvrir un DatePickerDialog au clic sur etTravelDate
        etTravelDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format de date : jj/mm/aaaa
                    etTravelDate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )
            datePicker.show()
        }

        btnSearchTrains.setOnClickListener {
            // Récupère les valeurs
            val departure = etDepartureCity.text.toString().trim()
            val arrival = etArrivalCity.text.toString().trim()
            val date = etTravelDate.text.toString().trim()

            // Lance l'activité de disponibilité
            val intent = Intent(this, AvailabilityActivity::class.java).apply {
                putExtra("departureCity", departure)
                putExtra("arrivalCity", arrival)
                putExtra("travelDate", date)
            }
            startActivity(intent)
        }
    }
}
