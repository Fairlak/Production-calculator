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
        data.yourCompanyData.initials?.takeIf { it.isNotBlank() },
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

    // --- ЛОГИКА ДЛЯ ДИНАМИЧЕСКОГО ОТОБРАЖЕНИЯ РАЗДЕЛОВ ---

    val clientHtml = with(data.client) {
        if (name.isNullOrBlank() && street.isNullOrBlank() && city.isNullOrBlank() && country.isNullOrBlank() && phone.isNullOrBlank() && email.isNullOrBlank() && contactPersons.isNullOrBlank() && customerData.isNullOrBlank()) {
            ""
        } else {
            """
            <div class="unbreakable">
                <div class="section-title">КЛИЕНТ</div>
                ${renderRow("Имя клиента:", name)}
                ${renderRow("Улица:", street)}
                ${renderRow("Город:", city)}
                ${renderRow("Страна:", country)}
                ${renderRow("Телефон:", phone)}
                ${renderRow("E-mail:", email)}
                ${renderRow("Контактные лица:", contactPersons)}
                ${renderRow("Данные заказчика:", customerData)}
            </div>
            """
        }
    }

    val measurementPointHtml = with(data.measurementPoint) {
        if (name.isNullOrBlank() && installationNumber.isNullOrBlank() && installationName.isNullOrBlank() && manufacturer.isNullOrBlank() && yearOfManufacture.isNullOrBlank() && serialNumber.isNullOrBlank() && note.isNullOrBlank()) {
            ""
        } else {
            """
            <div class="unbreakable">
                <div class="section-title">ТОЧКА ИЗМЕРЕНИЯ</div>
                ${renderRow("Имя точки:", name)}
                ${renderRow("Номер установки:", installationNumber)}
                ${renderRow("Наименование:", installationName)}
                ${renderRow("Производитель:", manufacturer)}
                ${renderRow("Год выпуска:", yearOfManufacture)}
                ${renderRow("Серийный номер:", serialNumber)}
                ${renderRow("Заметка:", note)}
            </div>
            """
        }
    }

    val toolsHtml = if (!data.tools.isNullOrEmpty()) {
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
        <div class="unbreakable">
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
        </div>
        """.trimIndent()
    } else {
        ""
    }

    val imagesHtml = if (data.images.isNotEmpty()) {
        // Группируем картинки по две для создания рядов таблицы
        val tableRows = data.images.chunked(2).joinToString("") { rowImages ->
            val cell1 = if (rowImages.isNotEmpty()) {
                val src = if (rowImages[0].startsWith("file://")) rowImages[0] else "file://${rowImages[0]}"
                """<td class="image-cell"><img src="$src" alt="Image" /></td>"""
            } else {
                "<td></td>" // Пустая ячейка, если картинка нечетная
            }

            val cell2 = if (rowImages.size > 1) {
                val src = if (rowImages[1].startsWith("file://")) rowImages[1] else "file://${rowImages[1]}"
                """<td class="image-cell"><img src="$src" alt="Image" /></td>"""
            } else {
                "<td></td>" // Пустая ячейка для второго места в ряду
            }

            "<tr>$cell1$cell2</tr>"
        }

        """
    <div class="unbreakable-images">
        <div class="section-title">ИЗОБРАЖЕНИЯ</div>
        <table class="images-table">
            <tbody>
                $tableRows
            </tbody>
        </table>
    </div>
    """.trimIndent()
    } else {
        ""
    }


    val formulas = mapOf(
        "FläktWoods" to "q = (1/k) * √ΔP",
        "Rosenberg" to "q = k * √(2 * ΔP / ρ)",
        "Nicotra-Gebhardt" to "q = k * √(2 * ΔP / ρ)",
        "Comefri" to "q = k * √(2 * ΔP / ρ)",
        "Ziehl" to "q = k * √ΔP",
        "ebm-papst" to "q = k * √ΔP",
        "Gebhardt" to "q = k * √(2 * ΔP / ρ)",
        "Nicotra" to "q = k * √ΔP",
        "Common probe (e.g. FloXact)" to "q = k * √ΔP"
    )
    val companyName = data.calculation.company
    val formula = formulas[companyName]
    val calculationFormulaDisplay = if (companyName != null && formula != null) {
        "\"$companyName\" $formula"
    } else {
        companyName
    }

    val measurementsHtml = with(data.calculation) {
        if (company.isNullOrBlank() && temperature.isNullOrBlank() && relativeHumidity.isNullOrBlank() && atmosphericPressure.isNullOrBlank() && staticPressure.isNullOrBlank() && calibrationFactor.isNullOrBlank() && pressureDrop.isNullOrBlank() && density.isNullOrBlank() && flowRate.isNullOrBlank()) {
            ""
        } else {
            """
            <div class="unbreakable">
                <div class="section-title">ИЗМЕРЕНИЯ</div>
                ${renderRow("Формула расчета:", calculationFormulaDisplay)}
                ${renderRow("Температура:", "$temperature °C")}
                ${renderRow("Отн. влажность:", "$relativeHumidity %")}
                ${renderRow("Атм. давление:", "$atmosphericPressure гПа")}
                ${renderRow("Стат. давление:", "$staticPressure -Па")}
                ${renderRow("Калибровочный фактор:", calibrationFactor)}
                ${renderRow("Перепад давления:", "$pressureDrop ПА")}
                ${renderRow("ПЛОТНОСТЬ:", "$density кг/м³")}
                ${renderRow("РАСХОД:", "$flowRate м³/${if (companyName == "FläktWoods") "c" else "ч"}")}
            </div>
            """
        }
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
                margin-bottom: 5px;
                font-weight: bold;
                border-bottom: 2px solid #B00;
                padding-bottom: 2px;
                text-transform: uppercase;
            }
            
            /* --- СТИЛЬ ДЛЯ ПРЕДОТВРАЩЕНИЯ РАЗРЫВОВ И ДОБАВЛЕНИЯ ОТСТУПА --- */
            .unbreakable {
                page-break-inside: avoid;
                padding-top: 15px; /* Отступ сверху для блока */
            }
             .unbreakable-images {
                padding-top: 15px; /* Отступ сверху для блока с изображениями */
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

            /* --- СТИЛИ ДЛЯ ТАБЛИЦЫ ИНСТРУМЕНТОВ --- */
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
                font-weight: bold;ДАТЫ И ПОДПИСЬ
                color: #333;
            }
          
            /* --- СТИЛИ ДЛЯ ИЗОБРАЖЕНИЙ (МЕТОД ТАБЛИЦЫ) --- */
            .images-table {
                width: 100%;
                border-collapse: separate; /* Важно для работы border-spacing */
                border-spacing: 15px 15px; /* Горизонтальный и вертикальный зазор */
                margin-top: 10px;
                page-break-inside: auto; /* Позволяем таблице разрываться между строками */
            }
            .images-table tr {
                page-break-inside: avoid; /* Но не разрываем саму строку */
            }
            .images-table td {
                width: 50%; /* Каждая ячейка занимает половину ширины */
                vertical-align: top;
                padding: 0;
            }
            .images-table img {
                width: 100%;
                height: auto;
                border: 1px solid #ddd;
                display: block;
            }
            
            /* Комментарий и подвал */
            .comment-box { background-color: #f9f9f9; padding: 10px; border: 1px solid #ddd; margin-top: 5px; white-space: pre-wrap;}
            .footer-wrapper { padding-top: 40px; /* Надежный отступ сверху для всего подвала */ }
            .dates-section { border-top: 1px solid #000; padding-top: 10px; display: flex; justify-content: space-between; font-size: 9pt; }
            .signature { margin-top: 50px; text-align: right; }
            .signature-line { display: inline-block;width: 250px; /* Немного увеличим для ФИО */border-top: 1px solid #000;padding-top: 5px;font-style: italic;text-align: center; /* Центрируем текст "Подпись..." */}
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

            <h1>ПРОТОКОЛ ИЗМЕРЕНИЯ</h1>

            <!-- 1. КЛИЕНТ -->
            $clientHtml

            <!-- 2. ТОЧКА ИЗМЕРЕНИЯ -->
            $measurementPointHtml

            <!-- 3. ИНСТРУМЕНТЫ -->
            $toolsHtml

            <!-- 4. ИЗМЕРЕНИЯ (РАСЧЕТ) -->
            $measurementsHtml

            <!-- 5. ИЗОБРАЖЕНИЯ -->
            $imagesHtml

            <!-- 6. КОММЕНТАРИЙ -->
            ${if (!data.comment.isNullOrBlank()) """
                <div class="unbreakable">
                    <div class="section-title">КОММЕНТАРИЙ</div>
                    <div class="comment-box">${data.comment.replace("\n", "<br>")}</div>
                </div>
            """ else ""}

            <!-- 7. ДАТЫ И ПОДПИСЬ -->
            <div class="footer-wrapper"><!-- Блок только для дат -->
                <div class="dates-section">
                    <div>
                        <strong>Дата расчета:</strong> ${data.calculationDate}<br>
                        <strong>Дата отчета:</strong> ${data.reportDate}
                    </div>
                </div>
                <!-- Блок подписи вынесен наружу и будет сам по себе -->
                <div class="signature">
                    <div class="signature-line">
                        Подпись ответственного<br>
                        ${data.yourCompanyData.initials ?: ""}
                    </div>
               </div>
           </div>
    
        </body>
        </html>
    """.trimIndent()
}
