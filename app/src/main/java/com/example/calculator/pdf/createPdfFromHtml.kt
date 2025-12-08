package com.example.calculator.pdf

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import android.print.PrintAttributes
import android.print.PrintManager
import android.os.Handler
import android.os.Looper

fun createPdfFromHtml(context: Context, htmlContent: String) {
    Handler(Looper.getMainLooper()).post {
        val webView = WebView(context)

        webView.settings.apply {
            javaScriptEnabled = false
            loadWithOverviewMode = true
            useWideViewPort = true

            allowFileAccess = true
            allowContentAccess = true


            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
        }



        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val jobName = "Report_${System.currentTimeMillis()}"

                val printAdapter = webView.createPrintDocumentAdapter(jobName)

                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()

                printManager?.print(jobName, printAdapter, printAttributes)
            }
        }
    }
}
