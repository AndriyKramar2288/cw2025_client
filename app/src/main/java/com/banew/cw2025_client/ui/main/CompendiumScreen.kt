package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banew.cw2025_backend_common.dto.courses.TopicCompendiumDto
import com.banew.cw2025_client.R
import com.banew.cw2025_client.ui.components.AlertDialogWrap
import com.banew.cw2025_client.ui.components.PagerIndicator
import com.banew.cw2025_client.ui.theme.AppTypography

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Preview() {
    CompendiumScreen(1703L, MainPageModel(true), CourseViewModel(true))
}

class ConceptForm (
    nameInit: String = "",
    descInit: String = ""
) {
    var id by mutableStateOf<Long?>(null)
    var name by mutableStateOf(nameInit)
    var desc by mutableStateOf(descInit)
    var isFlashCard by mutableStateOf(true)

    override fun equals(other: Any?) =
        if (other is ConceptForm)
            name == other.name && desc == other.desc && isFlashCard == other.isFlashCard
        else
            false

    constructor(concept: TopicCompendiumDto.ConceptBasicDto)
            : this(concept.name, concept.description) {
                id = concept.id
                isFlashCard = concept.isFlashCard
            }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + desc.hashCode()
        return result
    }
}

@Composable
fun CompendiumScreen(topicId: Long, viewModel: MainPageModel, courseModel: CourseViewModel) {
    val verticalScroll = rememberScrollState()

    courseModel.course?.let { course ->
        val topics = course.compendiums.map { it.topic }

        val topicIndex = topics
            .indexOfFirst { it.id == topicId }

        val compendium = course.compendiums.first { it.topic.id == topicId }

        val type: TopicProgressType = compendium.status.toProgressType()

        var notesText by remember { mutableStateOf(compendium.notes ?: "") }
        val concepts = remember { mutableStateListOf<ConceptForm>() }

        val isUnsavedChanges = notesText != compendium.notes
                || compendium.concepts.map { ConceptForm(it) } != concepts

        LaunchedEffect(compendium) {
            concepts.clear()
            compendium.concepts.map { concepts.add(ConceptForm(it)) }
        }

        val onClickUpdate = {
            val updatedCompendium = TopicCompendiumDto(
                compendium.id, notesText.ifBlank { null },
                compendium.topic, concepts.map {
                    TopicCompendiumDto.ConceptBasicDto(
                        it.id, it.name, it.desc, it.isFlashCard
                    )
                }, compendium.status
            )

            courseModel.updateCompendium(updatedCompendium, viewModel)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScroll)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.Start
        ) {
            IconButton (
                onClick = {
                    viewModel.preferredRoute = "course/${course.coursePlan.id}"
                }
            ) {
                Icon (
                    painterResource(R.drawable.arrow_circle_left_48px),
                    tint = Color.LightGray,
                    contentDescription = "return button",
                    modifier = Modifier.requiredSize(40.dp)
                )
            }

            // Статус теми
            StatusBadge(type = type)

            Spacer(modifier = Modifier.height(16.dp))

            // Назва теми
            Text(
                text = compendium.topic.name,
                style = AppTypography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // Опис теми
            if (!compendium.topic.description.isNullOrBlank()) {
                HorizontalDivider(
                    thickness = 3.dp,
                    color = colorResource(R.color.navbar_button)
                )
                Text(
                    text = compendium.topic.description,
                    style = AppTypography.bodyMedium,
                    textAlign = TextAlign.Justify,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Gray.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CompendiumTextField(
                notesText, "Власні нотатки", type,
                largeText = true
            ) { notesText = it }

            Spacer(modifier = Modifier.height(24.dp))

            // Секція концептів
            ConceptsSection(
                concepts,
                type = type
            ) {
                concepts.add(ConceptForm())
            }

            if (isUnsavedChanges) {
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Color.Gray.copy(alpha = 0.05f)
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "У вас є незбережені зміни!",
                        style = AppTypography.bodySmall,
                    )
                    Spacer(Modifier.width(5.dp))
                    Button(
                        onClick = onClickUpdate,
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = type.backgroundColor.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(
                            style = AppTypography.bodySmall,
                            text = "Зберегти"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Перейти на наступну тему
            val isNextOrEnd =
                if (topics.size > topicIndex + 1)
                    if (course.currentCompendiumId == compendium.id)
                        BottomElementType.START_NEXT
                    else
                        BottomElementType.NEXT
                else
                    BottomElementType.END
            val showAlertNextTopic = remember { mutableStateOf(false) }
            AlertDialogWrap(showAlertNextTopic) {
                if (isNextOrEnd == BottomElementType.START_NEXT) {
                    courseModel.beginTopic(topics[topicIndex + 1].id, viewModel)
                }
                else {
                    TODO("Завершення курсу!")
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (topicIndex - 1 >= 0) {
                    BottomPanelElement(BottomElementType.PREV) {
                        viewModel.preferredRoute = "compendium/${topics[topicIndex - 1].id}"
                    }
                } else Box {}
                BottomPanelElement(isNextOrEnd) {
                    if (isNextOrEnd == BottomElementType.NEXT)
                        viewModel.preferredRoute = "compendium/${topics[topicIndex + 1].id}"
                    else
                        showAlertNextTopic.value = true
                }
            }

            // Інформаційна панель
            InfoPanel(compendium = compendium, type = type)

            Spacer(modifier = Modifier.height(48.dp))
        }
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

    Row (
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(40.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        colorResource(R.color.navbar_button),
                        colorResource(R.color.navbar_button2)
                    )
                )
            )
    ) {
        Spacer(Modifier
            .background(Color.White)
            .fillMaxHeight()
            .width(5.dp))
        Row(
            modifier = Modifier
                .border(
                    2.dp, type.borderColor,
                    shape = RoundedCornerShape(3.dp)
                )
                .background(
                    color = type.backgroundColor,
                    shape = RoundedCornerShape(3.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(type.buttonIconId),
                contentDescription = "Topic status",
                tint = type.borderColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                style = AppTypography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = type.borderColor
            )
        }
        Spacer(Modifier
            .background(Color.White)
            .fillMaxHeight()
            .width(5.dp))
    }
}

@Composable
fun ConceptsSection(concepts: SnapshotStateList<ConceptForm>, type: TopicProgressType, onClick: () -> Unit) {

    val pagerState = rememberPagerState { concepts.size }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Концепти",
                    style = AppTypography.bodyLarge
                )
                Text(
                    text = "${concepts.size} шт.",
                    style = AppTypography.bodyMedium,
                    color = Color.Gray
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray.copy(alpha = 0.5f)
            )
            if (type == TopicProgressType.CURRENT) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.navbar_button)
                    ),
                    shape = RoundedCornerShape(5.dp),
                    onClick = onClick
                ) {
                    Icon(
                        painterResource(R.drawable.add_diamond_40px),
                        contentDescription = "Add icon",
                        Modifier.requiredSize(30.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (concepts.isEmpty()) {
            Card(
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    state = pagerState
                ) {
                    ConceptCard(concept = concepts[it], type = type) {
                        concepts.remove(concepts[it])
                    }
                }
                Spacer(Modifier.height(2.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.5f)
                )
                PagerIndicator(pagerState.currentPage, concepts.size)
            }
        }
    }
}

@Composable
fun ConceptCard(concept: ConceptForm, type: TopicProgressType, onDeleteClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = type.backgroundColor.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, type.borderColor.copy(alpha = 0.1f)),
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (type == TopicProgressType.CURRENT) Button(
                onDeleteClick,
                shape = RoundedCornerShape(3.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = 0.2f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painterResource(R.drawable.disabled_by_default_40px),
                    contentDescription = "delete concept icon",
                    modifier = Modifier.requiredSize(30.dp)
                )
            } else Box {}
            Button(
                {
                    concept.isFlashCard = !concept.isFlashCard
                },
                enabled = type == TopicProgressType.CURRENT,
                shape = RoundedCornerShape(3.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(
                        alpha = if (concept.isFlashCard) 0.4f else 0.3f
                    )
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painterResource(R.drawable.wand_stars_40px),
                    contentDescription = "switch isFlashCard icon",
                    modifier = Modifier.requiredSize(30.dp),
                    tint =
                        if (concept.isFlashCard)
                            colorResource(R.color.navbar_back)
                        else
                            colorResource(R.color.navbar_back2)
                )
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            CompendiumTextField(
                concept.name, "Назва", type
            ) { concept.name = it }

            CompendiumTextField(
                concept.desc, "Опис", type,
                largeText = true
            ) { concept.desc = it }
        }
    }
}

@Composable
fun CompendiumTextField(
    field : String, label : String, type: TopicProgressType,
    isError: (String) -> Boolean = { false }, errorMessage: String = "",
    largeText: Boolean = false,
    onChange : (String) -> Unit
) {
    TextField(
        readOnly = type != TopicProgressType.CURRENT,
        textStyle = AppTypography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .background(
                shape = RoundedCornerShape(10.dp),
                brush = Brush.horizontalGradient(
                    listOf(
                        colorResource(R.color.navbar_back),
                        colorResource(R.color.navbar_back2)
                    )
                )
            )
            .then(
                if (largeText)
                    Modifier.heightIn(min = 120.dp)
                else Modifier
            ),
        value = field,
        onValueChange = onChange,
        singleLine = !largeText,
        maxLines = if (largeText) Int.MAX_VALUE else 1,

        label = {
            Text(text = label, style = AppTypography.bodySmall)
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedLabelColor = Color.Gray,
            focusedLabelColor = Color.LightGray
        )
    )
    if (field.isNotBlank() && isError(field)) {
        Text(
            textAlign = TextAlign.Center,
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = AppTypography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(
                    Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun InfoPanel(compendium: TopicCompendiumDto, type: TopicProgressType) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = type.backgroundColor.copy(alpha = 0.075f)
        ),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoItem(
                iconId = R.drawable.award_star_40px,
                label = "Концептів",
                value = compendium.concepts.size.toString(),
                isActive = compendium.concepts.isEmpty()
            )

            VerticalDivider(
                modifier = Modifier.height(40.dp),
                thickness = 1.dp,
                color = type.borderColor.copy(alpha = 0.3f)
            )

            InfoItem(
                iconId = R.drawable.fact_check_40px,
                label = "Нотатки",
                value = if (compendium.notes.isNullOrBlank()) "Немає" else "Є",
                isActive = compendium.notes.isNullOrBlank()
            )
        }
    }
}

@Composable
fun InfoItem(iconId: Int, label: String, value: String, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            tint = if (isActive)
                        Color.LightGray
                    else
                        colorResource(R.color.navbar_button),
            painter = painterResource(iconId),
            contentDescription = "InfoItem-icon"
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTypography.titleMedium,
            color = Color.DarkGray
        )
        Text(
            text = label,
            style = AppTypography.bodySmall,
            color = Color.Gray
        )
    }
}

private enum class BottomElementType {
    PREV, NEXT, START_NEXT, END
}

@Composable
private fun BottomPanelElement(type: BottomElementType, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = colorResource(R.color.navbar_back),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 20.dp, vertical = 5.dp)
    ) {
        IconButton(
            onClick
        ) {
            Icon(
                painterResource(when (type) {
                    BottomElementType.PREV -> R.drawable.turn_slight_left_40px
                    BottomElementType.NEXT -> R.drawable.turn_slight_right_40px
                    BottomElementType.START_NEXT -> R.drawable.azm_40px
                    BottomElementType.END -> R.drawable.book_2_24px
                }),
                contentDescription = "bottom panel element icon",
                tint = colorResource(R.color.navbar_button2)
            )
        }
        Box(
            modifier = Modifier
                .padding(bottom = 7.dp)
                .requiredSize(30.dp, 2.dp)
                .background(colorResource(R.color.navbar_button2))
        )
        Text(
            textAlign = TextAlign.Center,
            text = when (type) {
                BottomElementType.PREV -> "Переглянути\nпопередню"
                BottomElementType.NEXT -> "Переглянути\nнаступну"
                BottomElementType.START_NEXT -> "Почати\nнаступну"
                BottomElementType.END -> "Завершити\nкурс"
            },
            style = AppTypography.bodySmall
        )
    }
}