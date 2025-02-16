package com.example.traintracker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AvailabilityActivity : AppCompatActivity() {

    private lateinit var selectedDate: String
    private lateinit var departureCity: String
    private lateinit var arrivalCity: String
    private lateinit var trainRecyclerView: RecyclerView
    private lateinit var dateRecyclerView: RecyclerView
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())


    private val trainSchedules = mutableListOf<TrainSchedule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_availability)


        trainRecyclerView = findViewById(R.id.rvTrainSchedules)
        dateRecyclerView = findViewById(R.id.rvDates)


        departureCity = intent.getStringExtra("departureCity") ?: "Ville départ"
        arrivalCity = intent.getStringExtra("arrivalCity") ?: "Ville arrivée"
        val rawDate = intent.getStringExtra("travelDate") ?: ""


        selectedDate = convertDateFormat(rawDate)


        val datesList = getNext7Days(selectedDate)
        generateTrainSchedules(departureCity, arrivalCity, datesList)


        setupHeader()
        setupRecyclerViews()
    }

    /**
     * Convertit une date de format "1/3/2025" en "1 Mar"
     */
    private fun convertDateFormat(inputDate: String): String {
        return try {
            val sdfInput = SimpleDateFormat("d/M/yyyy", Locale.FRANCE)
            val sdfOutput = SimpleDateFormat("d MMM", Locale.ENGLISH)
            val date = sdfInput.parse(inputDate)
            sdfOutput.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Remplit l'en-tête avec les informations de la recherche
     */
    private fun setupHeader() {
        findViewById<TextView>(R.id.tvDepartureCity).text = departureCity
        findViewById<TextView>(R.id.tvArrivalCity).text = arrivalCity
        findViewById<TextView>(R.id.tvDepartureCode).text = departureCity.take(3).uppercase()
        findViewById<TextView>(R.id.tvArrivalCode).text = arrivalCity.take(3).uppercase()
    }

    /**
     * Initialise les RecyclerViews (dates et trains)
     */
    private fun setupRecyclerViews() {
        dateRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val dates = getNext7Days(selectedDate)
        val dateAdapter = DateAdapter(dates) { selectedDateItem ->
            selectedDate = selectedDateItem.fullDate
            updateTrainList(selectedDate)
        }
        dateRecyclerView.adapter = dateAdapter

        trainRecyclerView.layoutManager = LinearLayoutManager(this)
        updateTrainList(selectedDate)
    }

    /**
     * Met à jour la liste des trains affichés en fonction de la date sélectionnée
     */
    private fun updateTrainList(date: String) {
        coroutineScope.launch {
            val filteredSchedules = withContext(Dispatchers.Default) {
                trainSchedules.filter { it.date == date }
            }
            trainRecyclerView.adapter = TrainAdapter(filteredSchedules) { selectedTrain ->
                handleTrainSelection(selectedTrain, date)
            }
        }
    }

    /**
     * Lance l'activité PersonalInfoActivity avec les détails sélectionnés
     */
    private fun handleTrainSelection(selectedTrain: TrainSchedule, date: String) {
        val intent = Intent(this, PersonalInfoActivity::class.java).apply {
            putExtra("departureCity", departureCity)
            putExtra("arrivalCity", arrivalCity)
            putExtra("travelDate", date)
            putExtra("selectedTime", selectedTrain.time)
            putExtra("trainName", selectedTrain.trainName)
            putExtra("price", selectedTrain.price)
        }
        startActivity(intent)
    }

    /**
     * Génère une liste de trajets répartis sur 7 jours
     */
    private fun generateTrainSchedules(departure: String, arrival: String, dates: List<DateAdapter.DateItem>) {
        trainSchedules.clear()

        // Liste des horaires et types de train possibles
        val availableTrains = listOf(
            Pair("08:00", "TGV InOui"),
            Pair("12:30", "Ouigo"),
            Pair("14:00", "TGV InOui"),
            Pair("09:15", "Ouigo"),
            Pair("11:00", "TGV InOui"),
            Pair("16:20", "Ouigo"),
            Pair("18:45", "TGV InOui")
        )

        val prices = listOf("89€", "49€", "79€", "45€", "95€", "39€", "99€")


        dates.forEachIndexed { index, dateItem ->
            val date = dateItem.fullDate
            val numTrains = (2..4).random()

            val shuffledTrains = availableTrains.shuffled().take(numTrains)
            val shuffledPrices = prices.shuffled().take(numTrains)

            shuffledTrains.forEachIndexed { i, train ->
                trainSchedules.add(
                    TrainSchedule(
                        departure, arrival, date, train.first, train.second, shuffledPrices[i]
                    )
                )
            }
        }
    }

    /**
     * Génère la liste des 7 prochains jours à partir de la date choisie
     */
    private fun getNext7Days(startDate: String): List<DateAdapter.DateItem> {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("d MMM", Locale.ENGLISH)
        val parsedDate = sdf.parse(startDate) ?: Date()
        calendar.time = parsedDate

        return (0..6).map { i ->
            if (i > 0) calendar.add(Calendar.DAY_OF_YEAR, 1)
            DateAdapter.DateItem(
                dayName = SimpleDateFormat("EEE", Locale.FRANCE).format(calendar.time),
                dayNumber = SimpleDateFormat("dd", Locale.FRANCE).format(calendar.time),
                fullDate = sdf.format(calendar.time),
                isSelected = i == 0
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
