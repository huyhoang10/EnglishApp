package com.example.efishapp.feature.folder.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.efishapp.feature.folder.domain.model.Topic
import com.example.efishapp.feature.folder.presentation.theme.TopicColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicSelector(
    selectedTopic: Topic,
    onTopicSelected: (Topic) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Chọn chủ đề",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1A1A2E).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Topic.entries.forEach { topic ->
                TopicChip(
                    topic = topic,
                    isSelected = topic == selectedTopic,
                    onClick = { onTopicSelected(topic) }
                )
            }
        }
    }
}

@Composable
fun TopicChip(
    topic: Topic,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topicColor = Color(topic.colorHex)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) topicColor
                else topicColor.copy(alpha = 0.15f)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) topicColor else topicColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = topic.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.White else Color(0xFF1A1A2E)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSelector(
    selectedColorHex: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Chọn màu",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1A1A2E).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TopicColors.forEach { color ->
                ColorChip(
                    color = color,
                    isSelected = color.value.toLong() == selectedColorHex,
                    onClick = { onColorSelected(color.value.toLong()) }
                )
            }
        }
    }
}

@Composable
fun ColorChip(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = Color.White,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
