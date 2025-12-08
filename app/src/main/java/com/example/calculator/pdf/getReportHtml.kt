package com.example.calculator.pdf

import com.example.calculator.storage.ReportPdfData

fun getReportHtml(data: ReportPdfData): String {


    fun renderRow(label: String, value: String?): String {
        if (value.isNullOrBlank()) return ""
        return """
            <div class="data-grid">
                <span class="label">$label</span>
                <span class="value">$value</span>
            </div>
        """.trimIndent()
    }

    val imagesHtml = if (data.images.isNotEmpty()) {
        val imgs = data.images.joinToString("") { imgPath ->
            val src = if (imgPath.startsWith("file://")) imgPath else "file://$imgPath"
            """<div class="image-container"><img src="$src" alt="Image" /></div>"""
        }
        """
        <div class="section-title">ИЗОБРАЖЕНИЯ</div>
        <div class="images-grid">
            $imgs
        </div>
        """
    } else {
        ""
    }

    val style = """
        <style>
            body { font-family: 'Arial', sans-serif; padding: 20px; line-height: 1.4; font-size: 10pt; }
            h1 { text-align: center; color: #000; font-size: 18pt; margin-bottom: 20px; text-transform: uppercase; }
            
            .section-title { 
                font-size: 12pt; 
                color: #B00; 
                margin-top: 20px; 
                margin-bottom: 10px;
                font-weight: bold;
                border-bottom: 2px solid #B00;
                padding-bottom: 2px;
                text-transform: uppercase;
            }
            
            .data-grid { 
                display: flex; /* Используем Flexbox для лучшей совместимости с некоторыми PDF движками */
                justify-content: space-between;
                border-bottom: 1px solid #eee;
                padding: 4px 0;
            }
            
            .label { font-weight: bold; color: #444; width: 40%; }
            .value { color: #000; width: 60%; text-align: left; }
            
            .dates-section { margin-top: 30px; border-top: 1px solid #000; padding-top: 10px; display: flex; justify-content: space-between; }
            .signature { margin-top: 50px; text-align: right; font-style: italic; }
            
            .images-grid { display: block; text-align: center; margin-top: 10px; }
            .image-container { display: inline-block; width: 45%; margin: 5px; vertical-align: top; }
            img { max-width: 100%; height: auto; border: 1px solid #ddd; }
            
            .comment-box { background-color: #f9f9f9; padding: 10px; border: 1px solid #ddd; margin-top: 5px; }
        </style>
    """

    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            $style
        </head>
        <body>
            <h1>ОТЧЕТ О РАСЧЕТЕ ПАРАМЕТРОВ ПОТОКА</h1>
            
            <!-- РАСЧЕТ -->
            <div class="section-title">РАСЧЕТ</div>
            ${renderRow("Компания:", data.calculation.company)}
            ${renderRow("Температура:", data.calculation.temperature)}
            ${renderRow("Отн. влажность:", data.calculation.relativeHumidity)}
            ${renderRow("Атм. давление:", data.calculation.atmosphericPressure)}
            ${renderRow("Стат. давление:", data.calculation.staticPressure)}
            ${renderRow("Калибровочный фактор:", data.calculation.calibrationFactor)}
            ${renderRow("Перепад давления:", data.calculation.pressureDrop)}
            ${renderRow("ПЛОТНОСТЬ:", data.calculation.density)}
            ${renderRow("РАСХОД:", data.calculation.flowRate)}

            <!-- КЛИЕНТ -->
            <div class="section-title">КЛИЕНТ</div>
            ${renderRow("Имя клиента:", data.client.name)}
            ${renderRow("Улица:", data.client.street)}
            ${renderRow("Город:", data.client.city)}
            ${renderRow("Страна:", data.client.country)}
            ${renderRow("Телефон:", data.client.phone)}
            ${renderRow("E-mail:", data.client.email)}
            ${renderRow("Контактные лица:", data.client.contactPersons)}
            ${renderRow("Данные заказчика:", data.client.customerData)}

            <!-- ТОЧКА ИЗМЕРЕНИЯ -->
            <div class="section-title">ТОЧКА ИЗМЕРЕНИЯ</div>
            ${renderRow("Имя точки:", data.measurementPoint.name)}
            ${renderRow("Номер установки:", data.measurementPoint.installationNumber)}
            ${renderRow("Наименование:", data.measurementPoint.installationName)}
            ${renderRow("Производитель:", data.measurementPoint.manufacturer)}
            ${renderRow("Телефон произв.:", data.measurementPoint.phone)}
            ${renderRow("Год выпуска:", data.measurementPoint.yearOfManufacture)}
            ${renderRow("Серийный номер:", data.measurementPoint.serialNumber)}
            ${renderRow("Заметка:", data.measurementPoint.note)}

            <!-- ИЗОБРАЖЕНИЯ -->
            $imagesHtml

            <!-- КОММЕНТАРИЙ -->
            ${if (!data.comment.isNullOrBlank()) """
                <div class="section-title">КОММЕНТАРИЙ</div>
                <div class="comment-box">${data.comment}</div>
            """ else ""}

            <!-- ДАТЫ -->
            <div class="dates-section">
                <div><strong>Дата расчета:</strong> ${data.calculationDate}</div>
                <div><strong>Дата отчета:</strong> ${data.reportDate}</div>
            </div>
            
            <div class="signature">_______________________<br>(Подпись ответственного)</div>
        </body>
        </html>
    """
}
