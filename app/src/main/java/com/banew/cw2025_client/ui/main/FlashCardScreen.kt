package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banew.cw2025_backend_common.dto.cards.FlashCardAnswer
import com.banew.cw2025_backend_common.dto.cards.FlashCardBasicDto
import com.banew.cw2025_backend_common.dto.courses.TopicCompendiumDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.data.Result
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class FlashCardViewModel(isMock: Boolean = false): ViewModel() {
    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource

    private var cardList by mutableStateOf(
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
                    "Абобус",
                    "EWwqwqwqqwq",
                    true
                ),
                Instant.now().minus(10L, ChronoUnit.MINUTES)
            )
        )
    )

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
                                || Duration.between(it.dueReview, Instant.now()).toMinutes() < 5
                            }

                        isFinished = cardList.isEmpty()

                        currentCard = cardList
                            //.filter { it.id != card.id }
                            .randomOrNull()
                    }
                }
            }
        }
    }

    fun init(contextModel: MainPageModel) {
        dataSource?.let { dataSource ->
            viewModelScope.launch {
                contextModel.isRefreshing.value = true
                val res = dataSource.getFlashCardList()
                if (res.isSuccess) {
                    cardList = res.asSuccess().data
                    currentCard = cardList.randomOrNull()
                }
                contextModel.isRefreshing.value = false
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Preview() {
    FlashCardScreen(
        MainPageModelMock(),
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
        onClick
    ) {
        Text(
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

    viewModel.currentCard?.let { card ->
        Column(
            Modifier.fillMaxSize()
        ) {
            Text(card.concept.name)
            Text(card.concept.description)
            Row {
                card.availableIntervals.forEach { each ->
                    FlashCardAnswerButton(each) {
                        viewModel.answer(each.key)
                    }
                }
            }
        }
    }
}