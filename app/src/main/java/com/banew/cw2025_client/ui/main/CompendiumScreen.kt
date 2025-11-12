package com.banew.cw2025_client.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.banew.cw2025_backend_common.dto.courses.TopicCompendiumDto
import com.banew.cw2025_client.ui.theme.AppTypography

@Composable
fun CompendiumScreen(topicId: Long, viewModel: MainPageModel) {
    val verticalScroll = rememberScrollState()

    var compendium by remember {
        mutableStateOf(
            viewModel.currentCourses.value.flatMap { it.compendiums }.first { it.topic.id == topicId }
        )
    }

    val type: TopicProgressType = compendium.status.toProgressType()

    // Стан для редагування нотаток
    var isEditingNotes by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf(compendium.notes ?: "") }
    var showSaveDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)
            .padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Статус теми
        StatusBadge(type = type)

        Spacer(modifier = Modifier.height(16.dp))

        // Назва теми
        Text(
            text = compendium.topic.name,
            style = AppTypography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Опис теми
        if (!compendium.topic.description.isNullOrBlank()) {
            Text(
                text = compendium.topic.description,
                style = AppTypography.bodyMedium,
                textAlign = TextAlign.Justify,
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                type.backgroundColor,
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Секція концептів
        ConceptsSection(
            concepts = compendium.concepts,
            type = type
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Секція нотаток (тільки для CURRENT)
        if (type == TopicProgressType.CURRENT) {
            NotesSection(
                notes = notesText,
                isEditing = isEditingNotes,
                onEditClick = { isEditingNotes = true },
                onNotesChange = { notesText = it },
                onSaveClick = { showSaveDialog = true },
                onCancelClick = {
                    isEditingNotes = false
                    notesText = compendium.notes ?: ""
                },
                borderColor = type.borderColor
            )
        } else if (!compendium.notes.isNullOrBlank()) {
            // Показати нотатки тільки для перегляду
            NotesReadOnly(
                notes = compendium.notes,
                borderColor = type.borderColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Інформаційна панель
        InfoPanel(compendium = compendium, type = type)
    }

    // Діалог підтвердження збереження
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Зберегти зміни?") },
            text = { Text("Ви впевнені, що хочете зберегти оновлені нотатки?") },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedCompendium = TopicCompendiumDto(
                            compendium.id, notesText.ifBlank { null },
                            compendium.topic, compendium.concepts, compendium.status
                        )
                        viewModel.updateCompendium(updatedCompendium)
                        compendium = updatedCompendium
                        isEditingNotes = false
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = type.buttonColor
                    )
                ) {
                    Text("Зберегти")
                }
            },
            dismissButton = {
                TextButton (onClick = { showSaveDialog = false }) {
                    Text("Скасувати")
                }
            }
        )
    }
}

@Composable
fun StatusBadge(type: TopicProgressType) {
    val statusText = when (type) {
        TopicProgressType.LOCKED -> "Заблоковано"
        TopicProgressType.CAN_START -> "Можна почати"
        TopicProgressType.COMPLETED -> "Завершено"
        TopicProgressType.CURRENT -> "Поточна тема"
    }

    val statusIcon = when (type) {
        TopicProgressType.LOCKED -> "🔒"
        TopicProgressType.CAN_START -> "▶️"
        TopicProgressType.COMPLETED -> "✅"
        TopicProgressType.CURRENT -> "📍"
    }

    Surface (
        shape = RoundedCornerShape(20.dp),
        color = type.backgroundColor,
        border = BorderStroke(2.dp, type.borderColor),
        shadowElevation = type.elavulationSize
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = statusIcon,
                style = AppTypography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                style = AppTypography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = type.borderColor
            )
        }
    }
}

@Composable
fun ConceptsSection(concepts: List<TopicCompendiumDto.ConceptBasicDto>, type: TopicProgressType) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Концепти",
                style = AppTypography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${concepts.size} шт.",
                style = AppTypography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (concepts.isEmpty()) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Немає концептів для цієї теми",
                    style = AppTypography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                concepts.forEach { concept ->
                    ConceptCard(concept = concept, type = type)
                }
            }
        }
    }
}

@Composable
fun ConceptCard(concept: TopicCompendiumDto.ConceptBasicDto, type: TopicProgressType) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, type.borderColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(type.borderColor, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = concept.name,
                    style = AppTypography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            if (!concept.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = concept.description,
                    style = AppTypography.bodyMedium,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
        }
    }
}

@Composable
fun NotesSection(
    notes: String,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    borderColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Мої нотатки",
                style = AppTypography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            if (!isEditing) {
                IconButton (onClick = onEditClick) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_edit),
                        contentDescription = "Редагувати нотатки",
                        tint = borderColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isEditing) {
            Card(
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(2.dp, borderColor),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        placeholder = { Text("Додайте ваші нотатки тут...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = borderColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        textStyle = AppTypography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onCancelClick) {
                            Text("Скасувати", color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onSaveClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = borderColor
                            )
                        ) {
                            Text("Зберегти")
                        }
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(
                    containerColor = borderColor.copy(alpha = 0.05f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = notes.ifBlank { "Нотаток ще немає. Натисніть на іконку редагування, щоб додати." },
                    style = AppTypography.bodyMedium,
                    color = if (notes.isBlank()) Color.Gray else Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun NotesReadOnly(notes: String, borderColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Нотатки",
            style = AppTypography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)),
            colors = CardDefaults.cardColors(
                containerColor = borderColor.copy(alpha = 0.05f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = notes,
                style = AppTypography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun InfoPanel(compendium: TopicCompendiumDto, type: TopicProgressType) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = type.backgroundColor
        ),
        border = BorderStroke(1.dp, type.borderColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoItem(
                icon = "📚",
                label = "Концептів",
                value = compendium.concepts.size.toString()
            )

            VerticalDivider(
                modifier = Modifier.height(40.dp),
                thickness = 1.dp,
                color = type.borderColor.copy(alpha = 0.3f)
            )

            InfoItem(
                icon = "📝",
                label = "Нотатки",
                value = if (compendium.notes.isNullOrBlank()) "Немає" else "Є"
            )
        }
    }
}

@Composable
fun InfoItem(icon: String, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = AppTypography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTypography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = AppTypography.bodySmall,
            color = Color.Gray
        )
    }
}