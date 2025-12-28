package com.example.calculator.pdf

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.calculator.OpenPdfActivity
import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import com.itextpdf.layout.font.FontProvider
import java.io.File
import java.io.FileOutputStream

fun createPdfFromHtml(context: Context, htmlContent: String) {
    try {
        val outputDir = context.cacheDir
        val outputFile = File.createTempFile("Report_", ".pdf", outputDir)

        val converterProperties = ConverterProperties()
        val fontProvider = FontProvider()
        fontProvider.addSystemFonts()
        fontProvider.addFont("assets/fonts/roboto.ttf")
        converterProperties.fontProvider = fontProvider

        val outputStream = FileOutputStream(outputFile)
        HtmlConverter.convertToPdf(htmlContent, outputStream, converterProperties)
        outputStream.close()

        Log.d("createPdfFromHtml", "PDF успешно создан: ${outputFile.absolutePath}")

        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            outputFile
        )

        val intent = Intent(context, OpenPdfActivity::class.java).apply {
            putExtra(OpenPdfActivity.EXTRA_PDF_URI, fileUri.toString())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)

    } catch (e: Exception) {
        Log.e("createPdfFromHtml", "Ошибка при создании PDF: ${e.message}", e)
        Toast.makeText(context, "Не удалось создать PDF-отчет", Toast.LENGTH_LONG).show()
    }
}
