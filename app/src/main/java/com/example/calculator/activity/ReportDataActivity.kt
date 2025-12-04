package com.example.calculator.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.adapters.ReportPhotosAdapter
import java.io.File
import java.io.IOException

class ReportDataActivity : AppCompatActivity() {

    private var idDb: Long = -1L
    private val db by lazy { DbHelper(this, null) }


    private lateinit var createReportDate: TextView
    private lateinit var clientSelectedNameStatic: TextView
    private lateinit var deleteClientButton: ImageButton
    private lateinit var takePhotoButton: ImageButton
    private lateinit var choosePhotoButton: ImageButton
    private lateinit var warningDeletePhotoButton: ImageButton



    private lateinit var photosLayout: ConstraintLayout
    private lateinit var overlayViewImage: View
    private lateinit var overlayViewDeleteReport: View
    private lateinit var overlayViewPhoto: View

    private lateinit var mainPhotoLayout: ConstraintLayout

    private lateinit var photosRecyclerView: RecyclerView
    private lateinit var photoAdapter: ReportPhotosAdapter
    private var currentPhotoFile: File? = null


    private lateinit var fullPhotoLayout: View
    private lateinit var fullPhotoView: com.github.chrisbanes.photoview.PhotoView

    private var currentOpenedPhotoPath: String? = null


    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == RESULT_OK) {
            currentPhotoFile?.let { file ->
                if (file.exists()) {
                    Toast.makeText(this, "Фото сохранено!", Toast.LENGTH_SHORT).show()

                    db.addPhoto(idDb, file.absolutePath)

                    refreshClientData()
                }
            }
        }
        if (::photosLayout.isInitialized && ::overlayViewImage.isInitialized) {
            photosLayout.visibility = View.GONE
            overlayViewImage.visibility = View.GONE
        }
    }


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                try {
                    val photoFile = createImageFile()

                    contentResolver.openInputStream(selectedImageUri)?.use { inputStream ->
                        photoFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    db.addPhoto(idDb, photoFile.absolutePath)
                    Toast.makeText(this, "Фото добавлено!", Toast.LENGTH_SHORT).show()

                    refreshClientData()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Ошибка при загрузке фото", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (::photosLayout.isInitialized && ::overlayViewImage.isInitialized) {
            photosLayout.visibility = View.GONE
            overlayViewImage.visibility = View.GONE
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

        photosRecyclerView = findViewById(R.id.photos_recycler_view)
        photosRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mainPhotoLayout = findViewById(R.id.main_photo)

        createReportDate = findViewById(R.id.create_report_date)
        clientSelectedNameStatic = findViewById(R.id.selected_client_name)

        val backReportsButton: ImageButton = findViewById(R.id.back_to_reports_button)
        val deleteReportButton: Button = findViewById(R.id.delete_report_button)
        val openWarningDeleteReportButton: ImageButton = findViewById(R.id.open_warning_delete_report_button)
        val mainClientLayout: ConstraintLayout = findViewById(R.id.main_client)
        val warningLayout: View = findViewById(R.id.warning_layout)
        val deleteCancelPhotoButton: Button = findViewById(R.id.delete_cancel_photo_button)
        val deleteCancelReportButton: Button = findViewById(R.id.delete_cancel_report_button)
        val warningDeleteReportLayout: View = findViewById(R.id.warning_delete_report_layout)
        photosLayout = findViewById(R.id.photos_layout)
        val cancelButton: TextView = findViewById(R.id.cancel_text_view)
        overlayViewImage = findViewById(R.id.overlay_view_image)
        overlayViewPhoto = findViewById(R.id.overlay_view_photo)
        overlayViewDeleteReport = findViewById(R.id.overlay_view_delete_report)
        takePhotoButton = findViewById(R.id.take_photo_button)
        choosePhotoButton = findViewById(R.id.choose_photo_button)
        deleteClientButton = findViewById(R.id.delete_client_report_button)
        warningDeletePhotoButton = findViewById(R.id.warning_delete_photo_button)



        fullPhotoLayout = findViewById(R.id.open_photo)

        fullPhotoView = findViewById(R.id.full_photo)

        findViewById<View>(R.id.back_to_report_button).setOnClickListener {

            val slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
            slideOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    fullPhotoLayout.visibility = View.GONE
                }
            })
            fullPhotoLayout.startAnimation(slideOut)

        }


        warningDeletePhotoButton.setOnClickListener {
            warningLayout.visibility = View.VISIBLE
            overlayViewPhoto.visibility = View.VISIBLE
        }

        deleteCancelPhotoButton.setOnClickListener {
            warningLayout.visibility = View.GONE
            overlayViewPhoto.visibility = View.GONE
        }

        openWarningDeleteReportButton.setOnClickListener {
            overlayViewDeleteReport.visibility = View.VISIBLE
            warningDeleteReportLayout.visibility = View.VISIBLE
        }

        deleteCancelReportButton.setOnClickListener {
            overlayViewDeleteReport.visibility = View.GONE
            warningDeleteReportLayout.visibility = View.GONE

        }


        findViewById<View>(R.id.delete_photo_button).setOnClickListener {
            currentOpenedPhotoPath?.let { path ->
                db.deletePhoto(path)

                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }

                overlayViewPhoto.visibility = View.GONE
                warningLayout.visibility = View.GONE
                Toast.makeText(this, "Фото удалено", Toast.LENGTH_SHORT).show()
                fullPhotoLayout.visibility = View.GONE

                refreshClientData()
            }

        }



        idDb = intent.getLongExtra("ID", -1L)


        photoAdapter = ReportPhotosAdapter(
            onAddClick = {
                photosLayout.visibility = View.VISIBLE
                overlayViewImage.visibility = View.VISIBLE
            },
            onPhotoClick = { path ->
                currentOpenedPhotoPath = path

                val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                fullPhotoLayout.startAnimation(slideIn)
                fullPhotoLayout.visibility = View.VISIBLE



                fullPhotoLayout.visibility = View.VISIBLE

                val imgFile = File(path)
                if (imgFile.exists()) {
                    var myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    myBitmap = rotateImageIfRequired(imgFile.absolutePath, myBitmap)
                    fullPhotoView.setImageBitmap(myBitmap)
                }

            }
        )

        photosRecyclerView.adapter = photoAdapter




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
            overlayViewImage.visibility = View.VISIBLE

        }

        overlayViewImage.setOnClickListener {
            photosLayout.visibility = View.GONE
            overlayViewImage.visibility = View.GONE
        }

        cancelButton.setOnClickListener {
            photosLayout.visibility = View.GONE
            overlayViewImage.visibility = View.GONE
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

        choosePhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
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

    override fun onBackPressed() {
        if (::fullPhotoLayout.isInitialized && fullPhotoLayout.visibility == View.VISIBLE) {
            val slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)

            slideOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    fullPhotoLayout.visibility = View.GONE
                }
            })
            fullPhotoLayout.startAnimation(slideOut)
        } else {
            super.onBackPressed()
        }
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




                    val photoPaths = db.getPhotosByReportId(idDb)

                    if (photoPaths.isEmpty()) {
                        photosRecyclerView.visibility = View.GONE
                        mainPhotoLayout.isEnabled = true
                    } else {
                        photosRecyclerView.visibility = View.VISIBLE
                        mainPhotoLayout.isEnabled = false
                        photoAdapter.submitList(photoPaths)
                    }



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
                }
            }
        }
    }


    private fun rotateImageIfRequired(photoPath: String, bitmap: Bitmap): Bitmap {
        try {
            val ei = androidx.exifinterface.media.ExifInterface(photoPath)
            val orientation = ei.getAttributeInt(
                androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
            )

            return when (orientation) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: IOException) {
            return bitmap
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }


    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }

        photoFile?.also { file ->
            currentPhotoFile = file
            val photoURI: android.net.Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                file
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            try {
                takePictureLauncher.launch(takePictureIntent)
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка камеры", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File = File(filesDir, "report_images")
        if (!storageDir.exists()) storageDir.mkdirs()

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

}

