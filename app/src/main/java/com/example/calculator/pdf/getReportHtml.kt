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

    val yourLogoHtml = if (!data.yourCompanyData.imagePath.isNullOrBlank()) {
        val path = data.yourCompanyData.imagePath
        val src = if (path.startsWith("file://")) path else "file://$path"
        """<img src="$src" alt="Company Logo" class="company-logo"/>"""
    } else {
        ""
    }

    val yourCompanyInfoHtml = listOfNotNull(
        data.yourCompanyData.companyName?.takeIf { it.isNotBlank() }?.let { "<strong>$it</strong>" },
        data.yourCompanyData.INN?.takeIf { it.isNotBlank() }?.let { "ИНН: $it" },
        data.yourCompanyData.address?.takeIf { it.isNotBlank() },
        listOfNotNull(data.yourCompanyData.city, data.yourCompanyData.country)
            .filter { it.isNotBlank() }
            .joinToString(", ")
            .takeIf { it.isNotBlank() },
        data.yourCompanyData.phone?.takeIf { it.isNotBlank() }?.let { "Tel: $it" },
        data.yourCompanyData.fax?.takeIf { it.isNotBlank() }?.let { "Fax: $it" },
        data.yourCompanyData.email?.takeIf { it.isNotBlank() },
        data.yourCompanyData.website?.takeIf { it.isNotBlank() }
    ).joinToString("<br>")

    val toolsHtml = if (!data.tools.isNullOrEmpty()) { // <-- ИЗМЕНЕНИЕ ЗДЕСЬ
        val tableRows = data.tools.joinToString("") { tool ->
            """
            <tr>
                <td>${tool.toolName ?: ""}</td>
                <td>${tool.serialNumber ?: ""}</td>
                <td>${tool.certificateNumber ?: ""}</td>
                <td>${tool.endDate ?: ""}</td>
            </tr>
            """.trimIndent()
        }
        """
        <div class="section-title">ИСПОЛЬЗУЕМЫЕ ИНСТРУМЕНТЫ</div>
        <table class="tools-table">
            <thead>
                <tr>
                    <th>Наименование</th>
                    <th>Серийный номер</th>
                    <th>№ свидетельства о поверке сертификата</th>
                    <th>Срок окончания поверки</th>
                </tr>
            </thead>
            <tbody>
                $tableRows
            </tbody>
        </table>
        """.trimIndent()
    } else {
        ""
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
            body { font-family: 'Arial', sans-serif; padding: 20px; line-height: 1.4; font-size: 10pt; color: #333; }
            h1 { text-align: center; color: #000; font-size: 18pt; margin-bottom: 10px; text-transform: uppercase; border-bottom: 2px solid #333; padding-bottom: 10px; }
            
            /* Шапка с данными вашей компании */
            .header-section {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                margin-bottom: 30px;
                padding-bottom: 5px;
            }
            .header-left { width: 45%; }
            .header-right { width: 50%; text-align: right; font-size: 9pt; color: #555; }
            .company-logo { max-width: 100%; max-height: 120px; object-fit: contain; }

            /* Заголовки секций */
            .section-title { 
                font-size: 12pt; 
                color: #B00; 
                margin-top: 20px; 
                margin-bottom: 5px;
                font-weight: bold;
                border-bottom: 2px solid #B00;
                padding-bottom: 2px;
                text-transform: uppercase;
            }
            
            /* Таблица данных */
            .data-grid { 
                display: flex; 
                justify-content: space-between;
                border-bottom: 1px solid #eee;
                padding: 4px 0;
            }
            .label { font-weight: bold; color: #444; width: 40%; }
            .value { color: #000; width: 60%; text-align: left; }

            /* --- НОВЫЕ СТИЛИ ДЛЯ ТАБЛИЦЫ ИНСТРУМЕНТОВ --- */
            .tools-table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 5px;
                font-size: 9pt;
            }
            .tools-table th, .tools-table td {
                border: 1px solid #ddd;
                padding: 6px;
                text-align: left;
            }
            .tools-table th {
                background-color: #f2f2f2;
                font-weight: bold;
                color: #333;
            }
            
            /* Изображения */
            .images-grid { display: block; text-align: center; margin-top: 10px; }
            .image-container { display: inline-block; width: 45%; margin: 5px; vertical-align: top; }
            img { max-width: 100%; height: auto; border: 1px solid #ddd; }
            
            /* Комментарий и подвал */
            .comment-box { background-color: #f9f9f9; padding: 10px; border: 1px solid #ddd; margin-top: 5px; }
            .dates-section { margin-top: 40px; border-top: 1px solid #000; padding-top: 10px; display: flex; justify-content: space-between; font-size: 9pt; }
            .signature { margin-top: 50px; text-align: right; }
            .signature-line { display: inline-block; width: 200px; border-top: 1px solid #000; text-align: center; padding-top: 5px; font-style: italic; }
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
            <!-- ШАПКА: Логотип слева, Ваши данные справа -->
            <div class="header-section">
                <div class="header-left">
                    $yourLogoHtml
                </div>

                <div class="header-right">
                    $yourCompanyInfoHtml
                </div>
            </div>

            <h1>ОТЧЕТ О РАСЧЕТЕ</h1>

            <!-- 1. КЛИЕНТ -->
            <div class="section-title">КЛИЕНТ</div>
            ${renderRow("Имя клиента:", data.client.name)}
            ${renderRow("Улица:", data.client.street)}
            ${renderRow("Город:", data.client.city)}
            ${renderRow("Страна:", data.client.country)}
            ${renderRow("Телефон:", data.client.phone)}
            ${renderRow("E-mail:", data.client.email)}
            ${renderRow("Контактные лица:", data.client.contactPersons)}
            ${renderRow("Данные заказчика:", data.client.customerData)}

            <!-- 2. ТОЧКА ИЗМЕРЕНИЯ -->
            <div class="section-title">ТОЧКА ИЗМЕРЕНИЯ</div>
            ${renderRow("Имя точки:", data.measurementPoint.name)}
            ${renderRow("Номер установки:", data.measurementPoint.installationNumber)}
            ${renderRow("Наименование:", data.measurementPoint.installationName)}
            ${renderRow("Производитель:", data.measurementPoint.manufacturer)}
            ${renderRow("Телефон произв.:", data.measurementPoint.phone)}
            ${renderRow("Год выпуска:", data.measurementPoint.yearOfManufacture)}
            ${renderRow("Серийный номер:", data.measurementPoint.serialNumber)}
            ${renderRow("Заметка:", data.measurementPoint.note)}

            <!-- НОВЫЙ РАЗДЕЛ ВСТАВЛЕН ЗДЕСЬ -->
            $toolsHtml

            <!-- 3. ИЗМЕРЕНИЯ (РАСЧЕТ) -->
            <div class="section-title">ИЗМЕРЕНИЯ</div>
            ${renderRow("Компания (история):", data.calculation.company)}
            ${renderRow("Температура:", data.calculation.temperature)}
            ${renderRow("Отн. влажность:", data.calculation.relativeHumidity)}
            ${renderRow("Атм. давление:", data.calculation.atmosphericPressure)}
            ${renderRow("Стат. давление:", data.calculation.staticPressure)}
            ${renderRow("Калибровочный фактор:", data.calculation.calibrationFactor)}
            ${renderRow("Перепад давления:", data.calculation.pressureDrop)}
            ${renderRow("ПЛОТНОСТЬ:", data.calculation.density)}
            ${renderRow("РАСХОД:", data.calculation.flowRate)}

            <!-- 4. ИЗОБРАЖЕНИЯ -->
            $imagesHtml

            <!-- 5. КОММЕНТАРИЙ -->
            ${if (!data.comment.isNullOrBlank()) """
                <div class="section-title">КОММЕНТАРИЙ</div>
                <div class="comment-box">${data.comment}</div>
            """ else ""}

            <!-- 6. ДАТЫ И ПОДПИСЬ -->
            <div class="dates-section">
                <div>
                    <div><strong>Дата расчета:</strong> ${data.calculationDate}</div>
                    <div><strong>Дата отчета:</strong> ${data.reportDate}</div>
                </div>
                
                <div class="signature">
                    <div class="signature-line">
                        Подпись ответственного<br>
                        ${data.yourCompanyData.initials ?: ""}
                    </div>
                </div>
            </div>
        </body>
        </html>
    """
}
