package com.example.calculator.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.calculator.DbHelper
import com.example.calculator.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ReportDataActivity : AppCompatActivity() {

    private var idDb: Long = -1L
    private val db by lazy { DbHelper(this, null) }


    private lateinit var createReportDate: TextView
    private lateinit var clientSelectedNameStatic: TextView
    private lateinit var deleteClientButton: ImageButton
    private lateinit var takePhotoButton: ImageButton

    private lateinit var photosLayout: ConstraintLayout
    private lateinit var overlayView: View
    private lateinit var miniImage: ImageView



    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (::photosLayout.isInitialized && ::overlayView.isInitialized) {
            photosLayout.visibility = View.GONE
            overlayView.visibility = View.GONE
        }
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap

            if (imageBitmap != null) {
                val savedFile = saveImageToInternalStorage(imageBitmap)

                if (savedFile != null) {
                    Toast.makeText(this, "Фото сохранено: ${savedFile.absolutePath}", Toast.LENGTH_SHORT).show()
                    miniImage.setImageBitmap(imageBitmap)
                    db.updateReport(idDb, "photoPath", savedFile.absolutePath)
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Нужен доступ к камере", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_data)

        createReportDate = findViewById(R.id.create_report_date)
        clientSelectedNameStatic = findViewById(R.id.selected_client_name)

        val backReportsButton: ImageButton = findViewById(R.id.back_to_reports_button)
        val deleteReportButton: ImageButton = findViewById(R.id.delete_report_button)
        val mainClientLayout: ConstraintLayout = findViewById(R.id.main_client)
        val mainPhotoLayout: ConstraintLayout = findViewById(R.id.main_photo)
        photosLayout = findViewById(R.id.photos_layout)
        val cancelButton: TextView = findViewById(R.id.cancel_text_view)
        overlayView = findViewById(R.id.overlay_view)
        takePhotoButton = findViewById(R.id.take_photo_button)
        deleteClientButton = findViewById(R.id.delete_client_report_button)

        idDb = intent.getLongExtra("ID", -1L)

        miniImage = findViewById(R.id.mini_image)

        backReportsButton.setOnClickListener {
            finish()
        }

        deleteReportButton.setOnClickListener {
            if (idDb != -1L) {
                db.deleteReport(idDb)
            }
            finish()
        }

        mainClientLayout.setOnClickListener {
            val intent = Intent(this, ReportDataClientsActivity::class.java)
            intent.putExtra("ID", idDb)
            startActivity(intent)
        }

        mainPhotoLayout.setOnClickListener {
            photosLayout.visibility = View.VISIBLE
            overlayView.visibility = View.VISIBLE

        }

        overlayView.setOnClickListener {
            photosLayout.visibility = View.GONE
            overlayView.visibility = View.GONE
        }

        cancelButton.setOnClickListener {
            photosLayout.visibility = View.GONE
            overlayView.visibility = View.GONE
        }

        deleteClientButton.setOnClickListener {
            if (idDb != -1L) {
                db.updateReport(idDb, "clientId", "")
                db.updateReport(idDb, "measurementId", "")
                refreshClientData()
            }
        }

        takePhotoButton.setOnClickListener {
            checkCameraPermissionAndOpen()
        }
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            androidx.core.content.ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        refreshClientData()
    }


    private fun refreshClientData() {
        if (idDb != -1L) {
            db.getReportDataEntryById(idDb).use { cursor ->
                if (cursor.moveToFirst()) {
                    val timeDb = cursor.getString(cursor.getColumnIndexOrThrow("reportTime"))
                    createReportDate.text = timeDb

                    val clientIdDb = cursor.getLong(cursor.getColumnIndexOrThrow("clientId"))
                    val measurementIdDb = cursor.getLong(cursor.getColumnIndexOrThrow("measurementId"))

                    var clientNameString = ""
                    var measurementPointString = ""

                    val photoPathIndex = cursor.getColumnIndex("photoPath")
                    val photoPath = if (photoPathIndex != -1) cursor.getString(photoPathIndex) else null

                    db.getClientDataEntryById(clientIdDb).use { clientCursor ->
                        if (clientCursor.moveToFirst()){
                            val name = clientCursor.getString(clientCursor.getColumnIndexOrThrow("name"))
                            clientNameString = if (!name.isNullOrEmpty()) name else "Имя клиента"
                        }
                    }


                    db.getMeasurementEntryById(measurementIdDb).use { measurementCursor ->
                        if (measurementCursor.moveToFirst()) {
                            val pointName = measurementCursor.getString(measurementCursor.getColumnIndexOrThrow("pointName"))
                            measurementPointString = if (!pointName.isNullOrEmpty()) pointName else ""
                        }
                    }

                    if (clientNameString.isNotEmpty()) {
                        clientSelectedNameStatic.visibility = View.VISIBLE
                        deleteClientButton.visibility = View.VISIBLE

                        val fullText = if (measurementPointString.isNotEmpty()) {
                            "$clientNameString $measurementPointString"
                        } else {
                            clientNameString
                        }
                        clientSelectedNameStatic.text = fullText
                    } else {
                        clientSelectedNameStatic.visibility = View.GONE
                        deleteClientButton.visibility = View.GONE
                        clientSelectedNameStatic.text = "Клиент не выбран"
                    }

                    if (!photoPath.isNullOrEmpty()) {
                        val imgFile = File(photoPath)
                        if (imgFile.exists()) {
                            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                            miniImage.setImageBitmap(myBitmap)
                            miniImage.visibility = View.VISIBLE
                        } else {
                            miniImage.visibility = View.GONE
                        }
                    } else {
                        miniImage.visibility = View.GONE
                    }

                }
            }
        }
    }
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureLauncher.launch(takePictureIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Не удалось открыть камеру", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): File? {
        return try {
            val fileName = "report_img_${System.currentTimeMillis()}.jpg"

            val directory = File(filesDir, "report_images")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)
            val stream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

