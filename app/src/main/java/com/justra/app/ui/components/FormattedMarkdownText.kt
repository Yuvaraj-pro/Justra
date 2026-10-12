package com.justra.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Rich Markdown Text Parser Composable for Justra Legal Assistant.
 * Transforms raw markdown strings containing headers, bold text (**text**),
 * checklist items (* []), bullet points (* item), and horizontal dividers (---)
 * into beautiful, structured Jetpack Compose UI elements.
 */
@Composable
fun FormattedMarkdownText(
    text: String,
    textColor: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    modifier: Modifier = Modifier
) {
    val lines = text.lines()

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        lines.forEach { line ->
            val trimmed = line.trim()

            when {
                // Horizontal Divider (--- or ***)
                trimmed == "---" || trimmed == "***" -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        color = (if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Headers (###, ##, #)
                trimmed.startsWith("### ") -> {
                    val content = trimmed.removePrefix("### ").trim()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = parseBoldText(content),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = textColor,
                            fontSize = 16.sp
                        )
                    )
                }
                trimmed.startsWith("## ") -> {
                    val content = trimmed.removePrefix("## ").trim()
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = parseBoldText(content),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = textColor,
                            fontSize = 18.sp
                        )
                    )
                }
                trimmed.startsWith("# ") -> {
                    val content = trimmed.removePrefix("# ").trim()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = parseBoldText(content),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Serif,
                            color = textColor,
                            fontSize = 20.sp
                        )
                    )
                }

                // Checkbox items (* [], * [ ], - [], - [ ])
                trimmed.startsWith("* []") || trimmed.startsWith("* [ ]") ||
                trimmed.startsWith("- []") || trimmed.startsWith("- [ ]") -> {
                    val content = trimmed
                        .removePrefix("* []")
                        .removePrefix("* [ ]")
                        .removePrefix("- []")
                        .removePrefix("- [ ]")
                        .trim()

                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = null,
                            tint = (if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.primary).copy(alpha = 0.8f),
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(16.dp)
                        )
                        Text(
                            text = parseBoldText(content),
                            style = style.copy(color = textColor)
                        )
                    }
                }

                // Bullet points (* , - , • )
                trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("• ") -> {
                    val content = trimmed
                        .removePrefix("* ")
                        .removePrefix("- ")
                        .removePrefix("• ")
                        .trim()

                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 1.dp)
                    ) {
                        Text(
                            text = "•",
                            style = style.copy(
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Text(
                            text = parseBoldText(content),
                            style = style.copy(color = textColor)
                        )
                    }
                }

                // Empty line spacer
                trimmed.isEmpty() -> {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Regular Paragraph
                else -> {
                    Text(
                        text = parseBoldText(trimmed),
                        style = style.copy(color = textColor)
                    )
                }
            }
        }
    }
}

/**
 * Parses bold markup like **text** into a Compose AnnotatedString with Bold font style.
 */
private fun parseBoldText(input: String): AnnotatedString {
    return buildAnnotatedString {
        val parts = input.split("**")
        for (i in parts.indices) {
            if (i % 2 == 1) {
                // Odd index -> inside **bold**
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(parts[i])
                }
            } else {
                // Even index -> normal text
                append(parts[i])
            }
        }
    }
}
