package com.example.calculator.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.exifinterface.media.ExifInterface
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.example.calculator.storage.YourCompanyData
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class YourCompanyActivity : AppCompatActivity() {
    private val dbHelper by lazy { DbHelper(this, null) }


    private lateinit var insertCompanyLogoButton: Button

    private lateinit var yourCompanyName: TextInputLayout
    private lateinit var yourCompanyINN: TextInputLayout
    private lateinit var yourInitials: TextInputLayout
    private lateinit var yourAddress: TextInputLayout
    private lateinit var yourCity: TextInputLayout
    private lateinit var yourCountry: TextInputLayout
    private lateinit var yourPhone: TextInputLayout
    private lateinit var yourFax: TextInputLayout
    private lateinit var yourEmail: TextInputLayout
    private lateinit var yourWebsite: TextInputLayout


    private lateinit var yourPhotosLayout: ConstraintLayout
    private lateinit var yourTakePhotoButton: ImageButton
    private lateinit var yourChoosePhotoButton: ImageButton
    private lateinit var yourCompanyCancelTextView: TextView
    private lateinit var overlayViewYourImage: View
    private lateinit var companyLogo: ImageView

    private lateinit var fullPhotoLayout: View
    private lateinit var fullPhotoView: PhotoView
    private lateinit var warningDeletePhotoButton: ImageButton




    private var currentPhotoFile: File? = null
    private var currentOpenedPhotoPath: String? = null




    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == RESULT_OK) {
            currentPhotoFile?.let { file ->
                if (file.exists()) {
                    Toast.makeText(this, file.absolutePath, Toast.LENGTH_SHORT).show()

                    dbHelper.addYourImageIcon(file.absolutePath)

                    refreshData()
                }
            }
        }
        if (::yourPhotosLayout.isInitialized && ::overlayViewYourImage.isInitialized) {
            yourPhotosLayout.visibility = View.GONE
            overlayViewYourImage.visibility = View.GONE
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

                    dbHelper.addYourImageIcon(photoFile.absolutePath)
                    Toast.makeText(this, "Фото добавлено!", Toast.LENGTH_SHORT).show()

                    refreshData()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Ошибка при загрузке фото", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (::yourPhotosLayout.isInitialized && ::overlayViewYourImage.isInitialized) {
            yourPhotosLayout.visibility = View.GONE
            overlayViewYourImage.visibility = View.GONE
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
        setContentView(R.layout.activity_your_company)

        val mainView: View = findViewById(R.id.main_your_company)
        val toolBar: View = findViewById(R.id.company_tool_bar)


        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
            toolBar.updatePadding(top = systemBars.top)
            val bottomPadding = ime.bottom.coerceAtLeast(systemBars.bottom)
            view.updatePadding(bottom = bottomPadding)
            windowInsets
        }



        val yourCompanyBackButton: ImageButton = findViewById(R.id.your_company_back_button)
        val warningLayout: View = findViewById(R.id.warning_layout)
        val overlayViewPhoto: View = findViewById(R.id.overlay_view_photo)
        val deleteCancelPhotoButton: Button = findViewById(R.id.delete_cancel_photo_button)




        yourPhotosLayout = findViewById(R.id.your_company_photos_layout)
        yourCompanyCancelTextView = findViewById(R.id.your_company_cancel_text_view)
        overlayViewYourImage = findViewById(R.id.overlay_view_your_image)
        insertCompanyLogoButton = findViewById(R.id.insert_company_logo)

        yourCompanyName = findViewById(R.id.your_company_name)
        yourCompanyINN = findViewById(R.id.your_company_INN)
        yourInitials = findViewById(R.id.your_initials)
        yourAddress = findViewById(R.id.your_address)
        yourCity = findViewById(R.id.your_city)
        yourCountry = findViewById(R.id.your_country)
        yourPhone = findViewById(R.id.your_phone)
        yourFax = findViewById(R.id.your_fax)
        yourEmail = findViewById(R.id.your_email)
        yourWebsite = findViewById(R.id.your_website)

        yourTakePhotoButton = findViewById(R.id.your_company_take_photo_button)
        yourChoosePhotoButton = findViewById(R.id.your_company_choose_photo_button)
        insertCompanyLogoButton = findViewById(R.id.insert_company_logo)
        companyLogo = findViewById(R.id.company_logo)

        fullPhotoLayout = findViewById(R.id.open_photo)
        fullPhotoView = findViewById(R.id.full_photo)
        warningDeletePhotoButton = findViewById(R.id.warning_delete_photo_button)


        yourCompanyBackButton.setOnClickListener {
            updateYourCompanyData()
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                updateYourCompanyData()
                finish()
            }
        })

        dbHelper.getYourCompanyData().use { cursor ->
            if (cursor.moveToFirst()) {
                val yourCompanyNameDb = cursor.getString(cursor.getColumnIndexOrThrow("yourCompanyName"))
                yourCompanyName.editText?.setText(yourCompanyNameDb)

                val yourCompanyINNDb = cursor.getString(cursor.getColumnIndexOrThrow("INN"))
                yourCompanyINN.editText?.setText(yourCompanyINNDb)

                val yourInitialsDb = cursor.getString(cursor.getColumnIndexOrThrow("yourInitials"))
                yourInitials.editText?.setText(yourInitialsDb)

                val yourAddressDb = cursor.getString(cursor.getColumnIndexOrThrow("yourAddress"))
                yourAddress.editText?.setText(yourAddressDb)

                val yourCityDb = cursor.getString(cursor.getColumnIndexOrThrow("yourCity"))
                yourCity.editText?.setText(yourCityDb)

                val yourCountryDb = cursor.getString(cursor.getColumnIndexOrThrow("yourCountry"))
                yourCountry.editText?.setText(yourCountryDb)

                val yourPhoneDb = cursor.getString(cursor.getColumnIndexOrThrow("yourPhoneNumber"))
                yourPhone.editText?.setText(yourPhoneDb)

                val yourFaxDb = cursor.getString(cursor.getColumnIndexOrThrow("yourFax"))
                yourFax.editText?.setText(yourFaxDb)

                val yourEmailDb = cursor.getString(cursor.getColumnIndexOrThrow("yourEMail"))
                yourEmail.editText?.setText(yourEmailDb)

                val yourWebsiteDb = cursor.getString(cursor.getColumnIndexOrThrow("yourWebsite"))
                yourWebsite.editText?.setText(yourWebsiteDb)
            }
        }


        val focusListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val editText = view as? TextInputEditText
                if (editText == null) return@OnFocusChangeListener

                updateYourCompanyData()
            }
        }

        yourCompanyName.editText?.onFocusChangeListener = focusListener
        yourCompanyINN.editText?.onFocusChangeListener = focusListener
        yourInitials.editText?.onFocusChangeListener = focusListener
        yourAddress.editText?.onFocusChangeListener = focusListener
        yourCity.editText?.onFocusChangeListener = focusListener
        yourCountry.editText?.onFocusChangeListener = focusListener
        yourPhone.editText?.onFocusChangeListener = focusListener
        yourFax.editText?.onFocusChangeListener = focusListener
        yourEmail.editText?.onFocusChangeListener = focusListener
        yourWebsite.editText?.onFocusChangeListener = focusListener


        insertCompanyLogoButton.setOnClickListener {
            yourPhotosLayout.visibility = View.VISIBLE
            overlayViewYourImage.visibility = View.VISIBLE
        }

        yourCompanyCancelTextView.setOnClickListener {
            yourPhotosLayout.visibility = View.GONE
            overlayViewYourImage.visibility = View.GONE
        }

        overlayViewYourImage.setOnClickListener {
            yourPhotosLayout.visibility = View.GONE
            overlayViewYourImage.visibility = View.GONE
        }


        yourTakePhotoButton.setOnClickListener {
            checkCameraPermissionAndOpen()
        }

        yourChoosePhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        companyLogo.setOnClickListener {
            val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
            fullPhotoLayout.startAnimation(slideIn)
            fullPhotoLayout.visibility = View.VISIBLE

            if (currentOpenedPhotoPath != null) {
                val imgFile = File(currentOpenedPhotoPath!!)
                if (imgFile.exists()) {
                    var myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    myBitmap = rotateImageIfRequired(imgFile.absolutePath, myBitmap)
                    fullPhotoView.setImageBitmap(myBitmap)
                }
            }
        }

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

        findViewById<View>(R.id.delete_photo_button).setOnClickListener {
            currentOpenedPhotoPath?.let { path ->
                dbHelper.deleteYourImageIcon(path)

                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }

                overlayViewPhoto.visibility = View.GONE
                warningLayout.visibility = View.GONE
                Toast.makeText(this, "Фото удалено", Toast.LENGTH_SHORT).show()
                fullPhotoLayout.visibility = View.GONE

                refreshData()
            }

        }

        warningDeletePhotoButton.setOnClickListener {
            warningLayout.visibility = View.VISIBLE
            overlayViewPhoto.visibility = View.VISIBLE
        }

        deleteCancelPhotoButton.setOnClickListener {
            warningLayout.visibility = View.GONE
            overlayViewPhoto.visibility = View.GONE
        }

        refreshData()
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


    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }


    private fun updateYourCompanyData(){
        dbHelper.updateYourCompanyData(YourCompanyData(
            yourCompanyName.editText?.text.toString(),
            yourCompanyINN.editText?.text.toString(),
            yourInitials.editText?.text.toString(),
            yourAddress.editText?.text.toString(),
            yourCity.editText?.text.toString(),
            yourCountry.editText?.text.toString(),
            yourPhone.editText?.text.toString(),
            yourFax.editText?.text.toString(),
            yourEmail.editText?.text.toString(),
            yourWebsite.editText?.text.toString()
            )
        )
    }
    private fun refreshData() {
        var imagePath: String? = null

        dbHelper.getYourCompanyData().use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex("imagePath")
                if (index != -1) {
                    imagePath = cursor.getString(index)
                }
            }
        }

        if (!imagePath.isNullOrEmpty()) {
            val imgFile = File(imagePath!!)

            if (imgFile.exists()) {

                currentOpenedPhotoPath = imgFile.absolutePath

                var myBitmap = android.graphics.BitmapFactory.decodeFile(imgFile.absolutePath)

                myBitmap = rotateImageIfRequired(imgFile.absolutePath, myBitmap)

                companyLogo.setImageBitmap(myBitmap)

                companyLogo.visibility = View.VISIBLE
                insertCompanyLogoButton.visibility = View.GONE
            } else {
                companyLogo.visibility = View.GONE
                insertCompanyLogoButton.visibility = View.VISIBLE
            }
        } else {
            companyLogo.visibility = View.GONE
            insertCompanyLogoButton.visibility = View.VISIBLE
        }
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = File(filesDir, "your_company_logo_images")
        if (!storageDir.exists()) storageDir.mkdirs()

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
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
            val photoURI: Uri = FileProvider.getUriForFile(
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

    private fun rotateImageIfRequired(photoPath: String, bitmap: Bitmap): Bitmap {
        try {
            val ei = ExifInterface(photoPath)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: IOException) {
            return bitmap
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}

