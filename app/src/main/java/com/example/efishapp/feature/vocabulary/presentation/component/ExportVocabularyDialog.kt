package com.example.efishapp.feature.vocabulary.presentation.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.efishapp.feature.vocabulary.domain.model.Vocabulary
import java.io.File

@Composable
fun ExportVocabularyDialog(
    folderName: String,
    vocabularies: List<Vocabulary>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Export từ vựng",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Xuất ${vocabularies.size} từ vựng từ \"$folderName\"",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "File sẽ được lưu dưới dạng: $folderName.txt",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Định dạng: word|meaning|pronunciation|example|description",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    exportToFile(context, folderName, vocabularies)
                    onDismiss()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chia sẻ file")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

private fun exportToFile(
    context: Context,
    folderName: String,
    vocabularies: List<Vocabulary>
) {
    val content = buildString {
        for (vocab in vocabularies) {
            val line = listOf(
                vocab.word,
                vocab.meaning,
                vocab.pronunciation,
                vocab.example,
                vocab.description
            ).joinToString("|")
            appendLine(line)
        }
    }

    try {
        val sanitizedName = folderName.replace(Regex("[^a-zA-Z0-9\\-_]"), "_")
        val fileName = "$sanitizedName.txt"
        val file = File(context.cacheDir, fileName)
        file.writeText(content)

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Từ vựng: $folderName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ từ vựng"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
