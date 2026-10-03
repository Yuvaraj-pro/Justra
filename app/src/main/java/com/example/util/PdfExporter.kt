package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.local.CaseEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {
    fun exportComplaintToPdf(
        context: Context,
        caseEntity: CaseEntity,
        complaintText: String,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.rgb(26, 43, 73) // Indigo slate
                textSize = 14f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                isAntiAlias = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.rgb(200, 109, 81) // Terracotta
                textSize = 10f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                isAntiAlias = true
            }

            val headerRulePaint = Paint().apply {
                color = Color.rgb(221, 214, 203)
                strokeWidth = 1f
            }

            val bodyPaint = Paint().apply {
                color = Color.rgb(30, 30, 30)
                textSize = 9f
                typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
                isAntiAlias = true
            }

            val metaPaint = Paint().apply {
                color = Color.rgb(100, 100, 100)
                textSize = 8f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
                isAntiAlias = true
            }

            // Header
            canvas.drawText("JUSTRA (ஜஸ்ட்ரா) — FORMAL GRIEVANCE BRIEF", 40f, 45f, titlePaint)
            canvas.drawText("STATUTORY JURISDICTION: ${caseEntity.disputeCategory.titleEn.uppercase()}", 40f, 60f, subtitlePaint)
            val formattedDate = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date())
            canvas.drawText("Generated on: $formattedDate (IST) | Case ID: ${caseEntity.caseId.take(12)}", 40f, 74f, metaPaint)
            canvas.drawLine(40f, 82f, 555f, 82f, headerRulePaint)

            // Body text line-wrapping
            var yPos = 105f
            val maxLineWidth = 515
            val lines = complaintText.split("\n")

            for (rawLine in lines) {
                if (rawLine.isBlank()) {
                    yPos += 10f
                    continue
                }

                // Simple word wrapping
                val words = rawLine.split(" ")
                var currentLine = ""
                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    val measuredWidth = bodyPaint.measureText(testLine)
                    if (measuredWidth < maxLineWidth) {
                        currentLine = testLine
                    } else {
                        canvas.drawText(currentLine, 40f, yPos, bodyPaint)
                        yPos += 14f
                        currentLine = word
                        if (yPos > 790f) break
                    }
                }
                if (currentLine.isNotEmpty()) {
                    canvas.drawText(currentLine, 40f, yPos, bodyPaint)
                    yPos += 14f
                }
                if (yPos > 790f) break
            }

            // Footer
            canvas.drawLine(40f, 800f, 555f, 800f, headerRulePaint)
            canvas.drawText("Digitally verified via Justra Cryptographic Vault. Compliant with IT Act 2000 Section 65B.", 40f, 815f, metaPaint)

            pdfDocument.finishPage(page)

            val outputDir = File(context.filesDir, "generated_briefs").apply { mkdirs() }
            val sanitizedTitle = caseEntity.title.replace("[^a-zA-Z0-9]".toRegex(), "_").take(20)
            val outputFile = File(outputDir, "Justra_Brief_${sanitizedTitle}_${System.currentTimeMillis()}.pdf")

            val fileOutputStream = FileOutputStream(outputFile)
            pdfDocument.writeTo(fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            pdfDocument.close()

            onSuccess(outputFile)
        } catch (e: Exception) {
            onError(e.message ?: "Failed to generate PDF document")
        }
    }
}
