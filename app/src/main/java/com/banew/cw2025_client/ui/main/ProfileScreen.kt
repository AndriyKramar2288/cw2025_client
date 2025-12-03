package com.banew.cw2025_client.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.banew.cw2025_backend_common.dto.coursePlans.CoursePlanBasicDto
import com.banew.cw2025_backend_common.dto.users.UserProfileBasicDto
import com.banew.cw2025_backend_common.dto.users.UserProfileCoursePlanDto
import com.banew.cw2025_backend_common.dto.users.UserProfileDetailedDto
import com.banew.cw2025_client.GlobalApplication
import com.banew.cw2025_client.R
import com.banew.cw2025_client.data.DataSource
import com.banew.cw2025_client.ui.theme.AppTypography
import kotlinx.coroutines.launch

class ProfileScreenViewModel(val isMock: Boolean = false): ViewModel() {
    private val dataSource: DataSource? = GlobalApplication.getInstance()?.dataSource

    var isProfileEdit by mutableStateOf(false)
    var profile by mutableStateOf(
        if (!isMock) null else UserProfileDetailedDto(
            1L,
            "User", "example@email.com", "src-example",
            listOf(
                UserProfileCoursePlanDto(
                    3L, "course plan name",
                    "course desc name", listOf(
                        CoursePlanBasicDto.TopicBasicDto(
                            null, "topic 1", "topic desc"
                        )
                    )
                )
            )
        )
    )
        private set

    fun initProfile(userId: Long? = null, contextModel: MainPageModel) {
        dataSource?.let { dataSource ->
            viewModelScope.launch {
                dataSource.userProfileDetailed(userId).asSuccess {
                    profile = it.data
                }.default(contextModel)
            }
        }
    }

    fun updateProfile(form: EditProfileForm, contextModel: MainPageModel) {
        dataSource?.let { dataSource ->
            viewModelScope.launch {
                contextModel.isRefreshing = true
                dataSource.updateProfile(form.toBasicDto()).asSuccess {
                    contextModel.refresh()
                    initProfile(null, contextModel)
                    isProfileEdit = false
                }.default(contextModel)
                contextModel.isRefreshing = false
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
private fun Aboba() {
    ProfilePageScreen(
        MainPageModel(true),
        ProfileScreenViewModel(true)
    )
}

@Composable
fun ProfilePageScreen(
    contextModel: MainPageModel,
    model: ProfileScreenViewModel = viewModel(),
    userId: Long? = null
) {
    LaunchedEffect(userId) {
        model.initProfile(userId, contextModel)
    }

    model.profile?.let { profile ->
        LazyColumn (
            Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(
                    Modifier.padding(vertical = 30.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row {
                        Box (
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(Color.Transparent)
                                .size(100.dp)
                                .border(
                                    2.dp, colorResource(R.color.navbar_button),
                                    RoundedCornerShape(10.dp)
                                ),
                        ) {
                            AsyncImage( // coil-compose
                                model = profile.photoSrc ?: "",
                                contentDescription = "Author",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.LightGray),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                style = AppTypography.titleMedium,
                                text = profile.username,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                colorResource(R.color.navbar_back),
                                                colorResource(R.color.navbar_back2)
                                            )
                                        ),
                                        RoundedCornerShape(5.dp)
                                    )
                                    .padding(horizontal = 30.dp, vertical = 20.dp)
                            )
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                colorResource(R.color.navbar_button),
                                                colorResource(R.color.navbar_button2)
                                            )
                                        ),
                                        RoundedCornerShape(5.dp)
                                    )
                                    .padding(5.dp)
                            ) {
                                Icon(
                                    painterResource(R.drawable.mail_24px),
                                    contentDescription = "email-icon",
                                    tint = Color.White,
                                    modifier = Modifier.requiredSize(20.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = profile.email,
                                    style = AppTypography.bodySmall,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                    if (userId == null) {
                        Row {
                            ProfileButton(
                                Color.Gray,
                                stringResource(R.string.profile_screen_logout)
                            ) {
                                contextModel.logout()
                            }
                            Spacer(Modifier.width(5.dp))
                            ProfileButton(
                                Color.Gray.copy(alpha = 0.8f),
                                stringResource(R.string.profile_screen_update_profile)
                            ) {
                                model.isProfileEdit = true
                            }
                        }
                        HorizontalDivider(
                            Modifier.fillMaxWidth(),
                            2.dp, colorResource(R.color.navbar_button)
                        )
                    }
                }
            }
            if (profile.coursePlans.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        style = AppTypography.bodyLarge,
                        text = stringResource(R.string.profile_screen_created_courses_label)
                    )
                }
                items(profile.coursePlans) { item ->
                    Spacer(Modifier.height(5.dp))
                    Card (
                        shape = RoundedCornerShape(3.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Gray.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            contextModel.preferredRoute = "coursePlan/${item.id}"
                        }
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.widthIn(max = 170.dp),
                                text = item.name,
                                style = AppTypography.bodySmall,
                                color = colorResource(R.color.navbar_button)
                            )
                            Row (verticalAlignment = Alignment.CenterVertically) {
                                VerticalDivider(
                                    Modifier.requiredHeight(30.dp),
                                    thickness = 2.dp,
                                    color = colorResource(R.color.navbar_button)
                                )
                                Icon(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    painter = painterResource(R.drawable.label_24px),
                                    contentDescription = "topic-icon",
                                    tint = colorResource(R.color.navbar_button)
                                )
                                Text(
                                    text = stringResource(
                                        R.string.profile_screen_topics_count,
                                        item.topics.size
                                    ),
                                    style = AppTypography.bodyMedium,
                                    color = colorResource(R.color.navbar_button)
                                )
                            }
                        }
                    }
                }
            }
        }
        EditProfileBox(model, contextModel)
    }
}

data class EditProfileForm(
    val id: Long,
    val username: String = "",
    val email: String = "",
    val photoSrc: String = ""
) {
    fun toBasicDto() = UserProfileBasicDto(id, username, email, photoSrc)
}

@Composable
private fun EditProfileBox(viewModel: ProfileScreenViewModel, contextModel: MainPageModel) {
    viewModel.profile.let { profile ->
        var form by remember { mutableStateOf(EditProfileForm(0)) }

        LaunchedEffect(profile) {
            form = EditProfileForm(
                profile!!.id,
                profile.username,
                profile.email,
                profile.photoSrc ?: ""
            )
        }

        if (viewModel.isProfileEdit) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x55000000))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier
                        .padding(10.dp)
                        .background(
                            colorResource(R.color.navbar_back).copy(alpha = .1f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CompendiumTextField(
                        form.username,
                        stringResource(R.string.profile_screen_new_username),
                    ) { form = form.copy(username = it) }

                    CompendiumTextField(
                        form.email,
                        stringResource(R.string.profile_screen_new_email),
                    ) { form = form.copy(email = it) }

                    CompendiumTextField(
                        form.photoSrc,
                        stringResource(R.string.profile_screen_new_avatar_src),
                    ) { form = form.copy(photoSrc = it) }

                    AsyncImage( // coil-compose
                        model = form.photoSrc,
                        contentDescription = "Photo",
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .size(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )

                    Row (
                        Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0x856D6D6D),
                                RoundedCornerShape(5.dp)
                            )
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileButton (
                            Color(0x654C1313),
                            stringResource(R.string.profile_screen_cancel)
                        ) {
                            viewModel.isProfileEdit = false
                        }
                        ProfileButton (
                            Color(0x65294510),
                            stringResource(R.string.profile_screen_update)
                        ) {
                            viewModel.updateProfile(form, contextModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileButton(color: Color, text: String, onClick: () -> Unit) {
    Button(
        contentPadding = PaddingValues(horizontal = 40.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Transparent,
            containerColor = color,
        ),
        shape = RoundedCornerShape(5.dp),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = AppTypography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}