package com.example.calculator

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class OpenPdfActivity : AppCompatActivity() {

    private lateinit var pdfView: PDFView
    private var pdfUri: Uri? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var pageNumberText: TextView

    companion object {
        const val EXTRA_PDF_URI = "com.example.calculator.PDF_URI"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_pdf)

        pdfView = findViewById(R.id.pdfView)
        val menuButton: ImageButton = findViewById(R.id.open_menu_pdf_button)
        val pdfMenuBg: LinearLayout = findViewById(R.id.pdf_menu_bg)
        val shareView: ConstraintLayout = findViewById(R.id.share_pdf)
        val downloadView: ConstraintLayout = findViewById(R.id.download_pdf)
        val openPdfView: ConstraintLayout = findViewById(R.id.open_pdf)
        val cancelButton: ImageButton = findViewById(R.id.pdf_back_to_report_button)


        progressBar = findViewById(R.id.progressBar)
        pageNumberText = findViewById(R.id.page_number_text)

        val uriString = intent.getStringExtra(EXTRA_PDF_URI)
        if (uriString == null) {
            Toast.makeText(this, "Ошибка: файл отчета не найден", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        pdfUri = Uri.parse(uriString)

        progressBar.visibility = View.VISIBLE
        pageNumberText.visibility = View.GONE

        menuButton.setOnClickListener { view ->
            pdfMenuBg.visibility = View.VISIBLE
        }

        pdfView.setOnClickListener {
            if (pdfMenuBg.visibility == View.VISIBLE) {
                pdfMenuBg.visibility = View.GONE
            }
        }

        shareView.setOnClickListener {
            pdfMenuBg.visibility = View.GONE

            sharePdf()
        }

        downloadView.setOnClickListener {
            pdfMenuBg.visibility = View.GONE
            downloadPdf()
        }

        openPdfView.setOnClickListener {
            pdfMenuBg.visibility = View.GONE
            openInAnotherApp()
        }

        cancelButton.setOnClickListener {
            finish()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Для Android 14+
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
            } else {
                // Для старых версий
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
            }
            super.onBackPressed()
        }

        pdfView.fromUri(pdfUri)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .defaultPage(0)
            .onLoad(OnLoadCompleteListener {
                progressBar.visibility = View.GONE
                pageNumberText.visibility = View.VISIBLE
                Toast.makeText(this@OpenPdfActivity, "Отчет загружен", Toast.LENGTH_SHORT).show()
            })
            .onPageChange(OnPageChangeListener { page, pageCount ->
                pageNumberText.text = "${page + 1} / $pageCount"
            })
            .onPageError(OnPageErrorListener { page, t ->
                Log.e("OpenPdfActivity", "Cannot load page $page", t)
                Toast.makeText(this@OpenPdfActivity, "Ошибка отображения страницы $page", Toast.LENGTH_SHORT).show()
            })
            .load()
    }

    private fun sharePdf() {
        pdfUri?.let { uri ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Поделиться отчетом..."))
        }
    }

    private fun openInAnotherApp() {
        pdfUri?.let { uri ->
            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                startActivity(Intent.createChooser(openIntent, "Открыть с помощью..."))
            } catch (e: Exception) {
                Toast.makeText(this, "Не найдено приложений для открытия PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadPdf() {
        val sourceUri = pdfUri ?: return
        val fileName = "Report_${System.currentTimeMillis()}.pdf"

        try {
            val contentResolver = contentResolver
            val inputStream = contentResolver.openInputStream(sourceUri) ?: throw Exception("Не удалось открыть исходный файл")

            var outputStream: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                outputStream = uri?.let { contentResolver.openOutputStream(it) }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                outputStream = FileOutputStream(file)
            }

            outputStream?.use {
                inputStream.copyTo(it)
            }

            inputStream.close()
            Toast.makeText(this, "Файл сохранен в папку 'Загрузки'", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Log.e("OpenPdfActivity", "Ошибка при скачивании файла", e)
            Toast.makeText(this, "Не удалось скачать файл", Toast.LENGTH_SHORT).show()
        }
    }
}
