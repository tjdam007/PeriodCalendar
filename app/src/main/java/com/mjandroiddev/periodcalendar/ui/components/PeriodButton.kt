package com.mjandroiddev.periodcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.theme.PeriodRedGradientEnd
import com.mjandroiddev.periodcalendar.ui.theme.PeriodRedGradientStart

@Composable
fun PeriodButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = 16.dp,
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(PeriodRedGradientStart, PeriodRedGradientEnd)
    )
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = if (enabled) gradient else Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = if (enabled) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(name = "Light Theme")
@Composable
private fun PeriodButtonLightPreview() {
    PeriodCalendarTheme(darkTheme = false) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PeriodButton(
                    text = "Start Period",
                    onClick = { }
                )
                PeriodButton(
                    text = "End Period",
                    onClick = { },
                    enabled = false
                )
                PeriodButton(
                    text = "Custom Corner",
                    onClick = { },
                    cornerRadius = 8.dp
                )
            }
        }
    }
}

@Preview(name = "Dark Theme")
@Composable
private fun PeriodButtonDarkPreview() {
    PeriodCalendarTheme(darkTheme = true) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PeriodButton(
                    text = "Start Period",
                    onClick = { }
                )
                PeriodButton(
                    text = "End Period",
                    onClick = { },
                    enabled = false
                )
                PeriodButton(
                    text = "Custom Corner",
                    onClick = { },
                    cornerRadius = 8.dp
                )
            }
        }
    }
}