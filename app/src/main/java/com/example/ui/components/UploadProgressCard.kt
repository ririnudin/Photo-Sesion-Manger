package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusUploading

@Composable
fun UploadProgressCard(
    totalPhotos: Int,
    uploadedPhotos: Int,
    pendingPhotos: Int,
    isUploading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val progressRatio = if (totalPhotos > 0) uploadedPhotos.toFloat() / totalPhotos else 0f
    val animatedProgress by animateFloatAsState(targetValue = progressRatio, label = "uploadProgress")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("upload_progress_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusIcon = when {
                        isUploading -> Icons.Default.CloudUpload
                        pendingPhotos > 0 -> Icons.Default.CloudQueue
                        else -> Icons.Default.CloudDone
                    }
                    val statusColor = when {
                        isUploading -> StatusUploading
                        pendingPhotos > 0 -> StatusPending
                        else -> StatusSuccess
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = "Sync Status",
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Status Sinkronisasi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when {
                                isUploading -> "Mengunggah foto ke Google Drive..."
                                pendingPhotos > 0 -> "$pendingPhotos foto dalam antrean upload"
                                totalPhotos == 0 -> "Belum ada foto dalam sesi ini"
                                else -> "Seluruh foto tersimpan di Google Drive"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Sync status badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isUploading -> StatusUploading.copy(alpha = 0.12f)
                        pendingPhotos > 0 -> StatusPending.copy(alpha = 0.12f)
                        else -> StatusSuccess.copy(alpha = 0.12f)
                    }
                ) {
                    Text(
                        text = when {
                            isUploading -> "UPLOADING"
                            pendingPhotos > 0 -> "TERANTRE"
                            else -> "TERHUBUG"
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isUploading -> StatusUploading
                            pendingPhotos > 0 -> StatusPending
                            else -> StatusSuccess
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Total Foto",
                    value = totalPhotos.toString(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                StatItem(
                    label = "Berhasil Upload",
                    value = uploadedPhotos.toString(),
                    color = StatusSuccess
                )
                StatItem(
                    label = "Menunggu",
                    value = pendingPhotos.toString(),
                    color = if (pendingPhotos > 0) StatusPending else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
