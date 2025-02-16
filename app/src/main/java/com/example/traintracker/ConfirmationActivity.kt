package com.example.traintracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var tvBookingSummary: TextView
    private lateinit var tvTravelDetails: TextView
    private lateinit var tvTicketPrice: TextView
    private lateinit var tvPersonalSummary: TextView
    private lateinit var tvReservationCode: TextView
    private lateinit var ivQrCode: ImageView
    private lateinit var btnDownloadPdf: Button

    private var departure = ""
    private var arrival = ""
    private var date = ""
    private var time = ""
    private var price = ""
    private var firstName = ""
    private var lastName = ""
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        tvBookingSummary = findViewById(R.id.tvBookingSummary)
        tvTravelDetails = findViewById(R.id.tvTravelDetails)
        tvTicketPrice = findViewById(R.id.tvTicketPrice)
        tvPersonalSummary = findViewById(R.id.tvPersonalSummary)
        tvReservationCode = findViewById(R.id.tvReservationCode)
        ivQrCode = findViewById(R.id.ivQrCode)
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf)


        departure = intent.getStringExtra("departureCity") ?: "Non spécifié"
        arrival = intent.getStringExtra("arrivalCity") ?: "Non spécifié"
        date = intent.getStringExtra("travelDate") ?: "Non spécifié"
        time = intent.getStringExtra("selectedTime") ?: "Non spécifié"
        price = intent.getStringExtra("price") ?: "Non spécifié"
        firstName = intent.getStringExtra("firstName") ?: "Non spécifié"
        lastName = intent.getStringExtra("lastName") ?: "Non spécifié"
        email = intent.getStringExtra("email") ?: "Non spécifié"


        tvBookingSummary.text = "$departure → $arrival"
        tvTravelDetails.text = "📅 Date : $date | 🕒 Départ : $time"
        tvTicketPrice.text = "💰 Tarif : $price"
        tvPersonalSummary.text = "👤 Réservé au nom de :\n$firstName $lastName\n📩 $email"
        tvReservationCode.text = "🔑 Référence de réservation : XE865X698"

        btnDownloadPdf.setOnClickListener {
            val pdfFile = generatePdf()
            if (pdfFile != null) {
                sendEmailWithPdf(pdfFile)
            }
        }

    }


    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            generatePdf()
        } else {
            Toast.makeText(this, "Permission refusée. Impossible de télécharger le PDF.", Toast.LENGTH_LONG).show()
        }
    }

    // Génération du fichier PDF
    private fun generatePdf(): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(400, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = android.graphics.Paint()

        // 📌 Titre
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("🛤️ RÉSERVATION CONFIRMÉE", 50f, 50f, paint)

        // 📌 Infos du trajet
        paint.textSize = 16f
        paint.isFakeBoldText = false
        canvas.drawText("Trajet : $departure → $arrival", 50f, 100f, paint)
        canvas.drawText("📅 Date : $date", 50f, 130f, paint)
        canvas.drawText("🕒 Départ : $time", 50f, 160f, paint)
        canvas.drawText("💰 Tarif : $price", 50f, 190f, paint)

        // 📌 Infos passager
        paint.textSize = 14f
        canvas.drawText("👤 Réservé à :", 50f, 230f, paint)
        canvas.drawText("$firstName $lastName", 50f, 260f, paint)
        canvas.drawText("📩 Email : $email", 50f, 290f, paint)

        // 📌 Code de réservation
        paint.isFakeBoldText = true
        canvas.drawText("🔑 ${tvReservationCode.text}", 50f, 330f, paint)

        pdfDocument.finishPage(page)

        // 📂 Enregistre le fichier dans le dossier privé de l'application
        val fileName = "reservation_${departure}_$arrival.pdf"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            Toast.makeText(this, "📄 PDF enregistré", Toast.LENGTH_SHORT).show()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Erreur lors de la création du PDF", Toast.LENGTH_LONG).show()
            null
        }
    }
    private fun sendEmailWithPdf(pdfFile: File) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email)) // Envoi à l'email du passager
            putExtra(Intent.EXTRA_SUBJECT, "Votre réservation de train : $departure → $arrival")
            putExtra(
                Intent.EXTRA_TEXT,
                """
                Bonjour $firstName,
                
                Voici votre confirmation de réservation pour le trajet $departure → $arrival.
                
                📅 Date : $date
                🕒 Heure de départ : $time
                💰 Tarif : $price
                
                Votre code de réservation : XE865X698
                
                Vous trouverez votre billet en pièce jointe.
                
                Cordialement,
                L'équipe TrainTracker
            """.trimIndent()
            )

            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@ConfirmationActivity,
                "${applicationContext.packageName}.provider",
                pdfFile
            )
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }


        startActivity(Intent.createChooser(emailIntent, "Envoyer l'email..."))
    }


}
