package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banew.cw2025_backend_common.dto.cards.FlashCardAnswer
import com.banew.cw2025_backend_common.dto.cards.FlashCardBasicDto
import com.banew.cw2025_backend_common.dto.courses.TopicCompendiumDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.R
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.Result
import com.banew.cw2025_client.ui.theme.AppTypography
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class FlashCardViewModel(isMock: Boolean = false): ViewModel() {
    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource

    var cardList by mutableStateOf(
        if (!isMock) emptyList() else listOf(
            FlashCardBasicDto(
                1488L,
                mapOf(
                    FlashCardAnswer.FAIL to 0.0,
                    FlashCardAnswer.BAD to 3.0,
                    FlashCardAnswer.GOOD to 5.0,
                    FlashCardAnswer.EASY to 7.0,
                ),
                TopicCompendiumDto.ConceptBasicDto(
                    1400L,
                    "Concept1",
                    "concept desc",
                    true
                ),
                Instant.now().minus(10L, ChronoUnit.MINUTES)
            )
        )
    )
        private set

    var currentCard by mutableStateOf(
        if (!isMock) null else FlashCardBasicDto(
                1488L,
        mapOf(
            FlashCardAnswer.FAIL to 0.0,
            FlashCardAnswer.BAD to 3.0,
            FlashCardAnswer.GOOD to 5.0,
            FlashCardAnswer.EASY to 7.0,
        ),
        TopicCompendiumDto.ConceptBasicDto(
            1400L,
            "Абобус",
            "EWwqwqwqqwq",
            true
        ),
        Instant.now().minus(10L, ChronoUnit.MINUTES))
    )
        private set

    var isHide by mutableStateOf(true)
        private set

    var isFinished by mutableStateOf(false)
        private set

    var isEditConcept by mutableStateOf(false)

    fun show() {
        isHide = false
    }

    fun answer(answer: FlashCardAnswer) {
        currentCard?.let { card ->
            viewModelScope.launch {
                val res = dataSource!!
                    .answerFlashCard(card.id, answer)

                when (res) {
                    is Result.Success -> {
                        cardList = cardList
                            .map {
                                if (it.id == res.data.id) res.data else it
                            }
                            .filter {
                                it.dueReview?.isBefore(Instant.now()) ?: true
                                || Duration.between(it.dueReview, Instant.now()).abs().toMinutes() < 5
                            }

                        isFinished = cardList.isEmpty()

                        currentCard = cardList
                            .randomOrNull()

                        isHide = true
                    }
                }
            }
        }
    }

    fun updateConcept(name: String, desc: String) {
        currentCard?.let { card ->
            viewModelScope.launch {
                val res = dataSource!!
                    .updateConcept(card.id, TopicCompendiumDto.ConceptBasicDto(
                        card.concept.id, name, desc, true
                    ))
                
                if (res is Result.Success) {
                    cardList = cardList
                        .map {
                            if (it.id == res.data.id) res.data else it
                        }
                    currentCard = res.data
                    isEditConcept = false
                }
            }
        }
    }

    fun init(contextModel: MainPageModel) {
        dataSource?.let { dataSource ->
            viewModelScope.launch {
                contextModel.isRefreshing = true
                val res = dataSource.getFlashCardList()
                if (res.isSuccess) {
                    cardList = res.asSuccess().data
                    currentCard = cardList.randomOrNull()
                }
                contextModel.isRefreshing = false
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Preview() {
    FlashCardScreen(
        MainPageModel(true),
        FlashCardViewModel(true)
    )
}

private val FlashCardAnswer.color
        get() = when (this) {
    FlashCardAnswer.BAD -> Color.Gray
    FlashCardAnswer.EASY -> Color.Blue
    FlashCardAnswer.FAIL -> Color.Red
    FlashCardAnswer.GOOD -> Color.Green
}

private val FlashCardAnswer.text
    get() = when (this) {
        FlashCardAnswer.BAD -> "Погано"
        FlashCardAnswer.EASY -> "Легко"
        FlashCardAnswer.FAIL -> "Забув"
        FlashCardAnswer.GOOD -> "Норм"
    }

@Composable
private fun FlashCardAnswerButton(type: Map.Entry<FlashCardAnswer, Double>, onClick: () -> Unit) {
    Button(
        onClick,
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = type.key.color
                .copy(alpha = .1f)
                .compositeOver(colorResource(R.color.navbar_button))
        ),
        contentPadding = PaddingValues(vertical = 5.dp, horizontal = 20.dp),
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            textAlign = TextAlign.Center,
            style = AppTypography.bodyMedium,
            color = Color.White,
            text = "${type.key.text}\n${type.value} дня"
        )
    }
}

@Composable
fun FlashCardScreen(
    contextModel: MainPageModel,
    viewModel: FlashCardViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.init(contextModel)
    }

    if (viewModel.isFinished) {
        Toast
            .makeText(context, "Наразі це все!", Toast.LENGTH_SHORT)
            .show()
        contextModel.preferredRoute = "courses"
        contextModel.refresh()
    }

    Box(Modifier.fillMaxSize()) {
        viewModel.currentCard?.let { card ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x2FA5A5A5),
                                colorResource(R.color.navbar_button2).copy(alpha = .3f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1500f, 500f)
                        )
                    )
                    .padding(vertical = 15.dp)
            ) {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = colorResource(R.color.navbar_button)
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Залишилось: ${viewModel.cardList.size}",
                        style = AppTypography.titleMedium,
                        color = Color.White,
                        modifier = Modifier
                            .padding(10.dp)
                            .background(
                                colorResource(R.color.navbar_button2),
                                RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Box(
                    Modifier.padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier.fillMaxWidth()
                            .requiredHeight(25.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(
                                    Color(0x9FFFFFFF),
                                    Color(0x4FF1F1F1),
                                ))
                            )
                            .padding(3.dp),
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        text = card.concept.name,
                        style = AppTypography.titleMedium,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .border(
                                2.dp, colorResource(R.color.navbar_button),
                                RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 40.dp, vertical = 15.dp)
                    )
                    if (!viewModel.isHide) Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.isEditConcept = true
                            }
                        ) {
                            Icon(
                                painterResource(R.drawable.edit_square_40px),
                                contentDescription = "Edit concept",
                                tint = Color.Gray,
                                modifier = Modifier.requiredSize(30.dp)
                            )
                        }
                    }
                }
                if (viewModel.isHide) {
                    SimpleButton("Показати") {
                        viewModel.show()
                    }
                }
                else {
                    Text(
                        card.concept.description,
                        style = AppTypography.bodyMedium,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 25.dp)
                            .background(
                                colorResource(R.color.navbar_back),
                                RoundedCornerShape(5.dp)
                            )
                            .padding(10.dp)
                    )
                    Row {
                        card.availableIntervals
                            .toSortedMap()
                            .forEach { each ->
                            FlashCardAnswerButton(each) {
                                viewModel.answer(each.key)
                            }
                        }
                    }
                    HorizontalDivider(
                        thickness = 2.dp
                    )
                }
            }
        }
        EditConceptBox(viewModel)
    }
}

@Composable
fun EditConceptBox(viewModel: FlashCardViewModel) {
    viewModel.currentCard?.concept?.let { concept ->
        var name by remember { mutableStateOf(concept.name) }
        var desc by remember { mutableStateOf(concept.description) }

        if (viewModel.isEditConcept) {
            Box(
                Modifier.fillMaxSize().background(Color(0x55000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier
                        .padding(10.dp)
                        .background(
                            colorResource(R.color.navbar_back).copy(alpha = .1f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    CompendiumTextField(
                        name, "Назва", TopicProgressType.CURRENT
                    ) { name = it }

                    CompendiumTextField(
                        desc, "Опис", TopicProgressType.CURRENT,
                        largeText = true
                    ) { desc = it }
                    Row (
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SimpleButton("Скасувати") {
                            viewModel.isEditConcept = false
                        }
                        SimpleButton("Зберегти") {
                            viewModel.updateConcept(name, desc)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleButton(text: String, onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray
        ),
        shape = RoundedCornerShape(5.dp),
        onClick = onClick
    ) {
        Text(
            text,
            style = AppTypography.bodySmall
        )
    }
}