package com.example.calculator.pdf

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient

fun createPdfFromHtml(context: Context, htmlContent: String) {
    val webView = WebView(context)

    webView.settings.apply {
        javaScriptEnabled = true
        allowFileAccess = true
    }

    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            // МЕТОД .print() — ЕДИНСТВЕННЫЙ, КОТОРЫЙ РАБОТАЕТ БЕЗ ОШИБОК ДОСТУПА
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val jobName = "Report_${System.currentTimeMillis()}"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)

            val attributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .build()

            // Запуск системного окна (это работает всегда)
            printManager.print(jobName, printAdapter, attributes)
        }
    }

    webView.loadDataWithBaseURL("file:///", htmlContent, "text/html", "UTF-8", null)
}