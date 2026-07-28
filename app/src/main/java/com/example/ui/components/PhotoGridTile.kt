package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.PhotoItem
import com.example.data.model.UploadStatus
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusUploading
import java.io.File

@Composable
fun PhotoGridTile(photo: PhotoItem) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = File(photo.localPath),
            contentDescription = photo.fileName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Upload Status Badge Overlay
        val statusBg = when (photo.uploadStatus) {
            UploadStatus.UPLOADED -> StatusSuccess
            UploadStatus.UPLOADING -> StatusUploading
            UploadStatus.QUEUED -> StatusPending
            UploadStatus.FAILED -> StatusError
        }

        val statusIcon = when (photo.uploadStatus) {
            UploadStatus.UPLOADED -> Icons.Default.CheckCircle
            UploadStatus.UPLOADING -> Icons.Default.CloudUpload
            UploadStatus.QUEUED -> Icons.Default.CloudQueue
            UploadStatus.FAILED -> Icons.Default.ErrorOutline
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp),
            shape = CircleShape,
            color = statusBg
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = photo.uploadStatus.name,
                tint = Color.White,
                modifier = Modifier
                    .padding(4.dp)
                    .size(14.dp)
            )
        }
    }
}
