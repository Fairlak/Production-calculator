package com.example.calculator.activity.reports

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.DbHelper
import com.example.calculator.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ReportToolDataActivity : AppCompatActivity() {

    private var idDb: Long = -1L
    private val db by lazy { DbHelper(this, null) }

    private lateinit var toolName: TextInputLayout
    private lateinit var serialNumber: TextInputLayout
    private lateinit var certificateNumber: TextInputLayout
    private lateinit var endDate: TextInputLayout
    private lateinit var toolNameStatic: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_tool_data)


        val comeBack: ImageButton = findViewById(R.id.tool_back_to_tools_button)
        val openDeleteWarningButton: ImageButton = findViewById(R.id.open_warning_delete_tool_button)
        val warningDeleteLayout: LinearLayout = findViewById(R.id.warning_delete_tool_layout)
        val deleteToolButton: Button = findViewById(R.id.delete_tool_button)
        val deleteCancelToolButton: Button = findViewById(R.id.delete_cancel_tool_button)
        val overlayViewDeleteTool: View = findViewById(R.id.overlay_view_delete_tool)


        toolName = findViewById(R.id.tool_name)
        serialNumber = findViewById(R.id.tool_serial_number)
        certificateNumber = findViewById(R.id.tool_certificate_number)
        endDate = findViewById(R.id.tool_end_date)
        toolNameStatic = findViewById(R.id.tool_name_text_static)


        idDb = intent.getLongExtra("ID", -1L)


        comeBack.setOnClickListener {
            saveAllFields()
            finish()
        }

        openDeleteWarningButton.setOnClickListener {
            overlayViewDeleteTool.visibility = View.VISIBLE
            warningDeleteLayout.visibility = View.VISIBLE
        }

        deleteCancelToolButton.setOnClickListener {
            overlayViewDeleteTool.visibility = View.GONE
            warningDeleteLayout.visibility = View.GONE
        }

        deleteToolButton.setOnClickListener {
            if (idDb != -1L) {
                db.deleteTool(idDb)
            }
            finish()
        }

        if (idDb != -1L) {
            db.getToolEntryById(idDb).use { cursor ->
                if (cursor.moveToFirst()) {
                    val toolNameDb = cursor.getString(cursor.getColumnIndexOrThrow("toolName"))
                    toolName.editText?.setText(toolNameDb)
                    toolNameStatic.text = if (toolNameDb == "") "Название прибора" else toolNameDb

                    val serialNumberDb = cursor.getString(cursor.getColumnIndexOrThrow("serialNumber"))
                    serialNumber.editText?.setText(serialNumberDb)

                    val certificateNumberDb = cursor.getString(cursor.getColumnIndexOrThrow("certificateNumber"))
                    certificateNumber.editText?.setText(certificateNumberDb)

                    val endDateDb = cursor.getString(cursor.getColumnIndexOrThrow("endDate"))
                    endDate.editText?.setText(endDateDb)
                }
            }
        }


        val focusListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val editText = view as? TextInputEditText
                if (editText == null) return@OnFocusChangeListener
                val inputText = editText.text.toString()

                val nameField = when ((editText.parent.parent as? TextInputLayout)?.id) {

                    R.id.tool_name -> "toolName"
                    R.id.tool_serial_number -> "serialNumber"
                    R.id.tool_certificate_number -> "certificateNumber"
                    R.id.tool_end_date -> "endDate"
                    else -> null
                }
                if (nameField != null) {
                    db.updateTool(idDb, nameField, inputText)
                }
                if (nameField == "toolName") {
                        toolNameStatic.text = inputText
                }

            }
        }


        toolName.editText?.onFocusChangeListener = focusListener
        serialNumber.editText?.onFocusChangeListener = focusListener
        certificateNumber.editText?.onFocusChangeListener = focusListener
        endDate.editText?.onFocusChangeListener = focusListener
    }

    fun saveAllFields() {
        if (idDb == -1L) {
            return
        }

        val toolName = toolName.editText?.text.toString()
        val serialNumber = serialNumber.editText?.text.toString()
        val certificateNumber = certificateNumber.editText?.text.toString()
        val endDate = endDate.editText?.text.toString()


        val toolUpdates = mapOf(
            "toolName" to toolName,
            "serialNumber" to serialNumber,
            "certificateNumber" to certificateNumber,
            "endDate" to endDate
        )

        toolUpdates.forEach { (fieldName, value) ->
            db.updateTool(idDb, fieldName, value)
        }
    }
}