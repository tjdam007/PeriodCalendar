package com.mjandroiddev.periodcalendar.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme

@Composable
fun RatingAndSupportDialog(
    onDismiss: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onRateApp: () -> Unit,
    onNotNow: () -> Unit
) {
    val context = LocalContext.current
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header with app icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌸",
                        fontSize = 36.sp
                    )
                }
                
                // Title and message
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Enjoying Period Calendar?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Your support helps us keep this app free, private, and constantly improving for women everywhere.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                
                // Rating section
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        Text(
                            text = "Rate us 5 stars on Play Store",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        
                        Button(
                            onClick = {
                                onRateApp()
                                openPlayStore(context)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rate Now")
                        }
                    }
                }
                
                // Support section
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "💝",
                            fontSize = 28.sp
                        )
                        
                        Text(
                            text = "Support Development",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Help us add new features and keep the app running",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        
                        OutlinedButton(
                            onClick = {
                                onNavigateToSupport()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Support Us")
                        }
                    }
                }
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onNotNow,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Not Now")
                    }
                    
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Don't Show Again")
                    }
                }
                
                // Small print
                Text(
                    text = "We respect your choice and will never show ads or sell your data.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun openPlayStore(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // If Play Store app is not installed, open in browser
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

@Composable
fun RatingAndSupportDialogManager(
    shouldShow: Boolean,
    onDialogShown: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onDismissDialog: () -> Unit
) {
    val context = LocalContext.current
    
    if (shouldShow) {
        RatingAndSupportDialog(
            onDismiss = {
                // User clicked "Don't Show Again"
                saveDontShowAgainPreference(context, true)
                onDismissDialog()
            },
            onNavigateToSupport = {
                onNavigateToSupport()
                onDismissDialog()
            },
            onRateApp = {
                // Track rating action
                onDialogShown()
                onDismissDialog()
            },
            onNotNow = {
                // Show again after some time
                updateLastShownTime(context)
                onDismissDialog()
            }
        )
    }
}

// Helper functions for managing dialog preferences
fun shouldShowRatingDialog(context: Context): Boolean {
    val sharedPrefs = context.getSharedPreferences("rating_prefs", Context.MODE_PRIVATE)
    val dontShowAgain = sharedPrefs.getBoolean("dont_show_rating_dialog", false)
    if (dontShowAgain) return false
    
    val lastShown = sharedPrefs.getLong("last_rating_dialog_shown", 0)
    val currentTime = System.currentTimeMillis()
    val daysSinceLastShown = (currentTime - lastShown) / (1000 * 60 * 60 * 24)
    
    // Show after 7 days if not shown before, or after 30 days if shown before
    val daysToWait = if (lastShown == 0L) 7 else 30
    
    return daysSinceLastShown >= daysToWait
}

private fun saveDontShowAgainPreference(context: Context, dontShow: Boolean) {
    context.getSharedPreferences("rating_prefs", Context.MODE_PRIVATE)
        .edit()
        .putBoolean("dont_show_rating_dialog", dontShow)
        .apply()
}

private fun updateLastShownTime(context: Context) {
    context.getSharedPreferences("rating_prefs", Context.MODE_PRIVATE)
        .edit()
        .putLong("last_rating_dialog_shown", System.currentTimeMillis())
        .apply()
}

@Preview(name = "Rating and Support Dialog")
@Composable
private fun RatingAndSupportDialogPreview() {
    PeriodCalendarTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                RatingAndSupportDialog(
                    onDismiss = { },
                    onNavigateToSupport = { },
                    onRateApp = { },
                    onNotNow = { }
                )
            }
        }
    }
}