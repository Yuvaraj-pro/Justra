package com.justra.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.R
import com.justra.app.ui.theme.SovereignNavy
import com.justra.app.ui.theme.TextSecondaryDark

/**
 * Reusable Production Logo & Typography Composable for Justra (The Justice Astra).
 * Renders the official Vector Shield Emblem alongside bold Sovereign Navy typography.
 */
@Composable
fun JustraBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showWordmark: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_justra_logo),
            contentDescription = "Justra Justice Astra Logo",
            modifier = Modifier.size(size)
        )

        if (showWordmark) {
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "JUSTRA",
                    fontWeight = FontWeight.Black,
                    fontSize = (size.value * 0.42f).sp,
                    color = SovereignNavy,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "WORLDWIDE LEGAL INTELLIGENCE",
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.18f).sp,
                    color = TextSecondaryDark,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
