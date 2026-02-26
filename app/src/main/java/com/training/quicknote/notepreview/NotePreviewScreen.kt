package com.training.quicknote.notepreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.training.quicknote.R
import com.training.quicknote.util.Category

@Composable
fun NoteDetail(
    taskTitle: String,
    taskDescription: String,
    onShareClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val previewScreenBg = colorResource(id = R.color.previewScreenBgClr)
    val cardBg = colorResource(id = R.color.taskDetailBgClr)
    val previewTitleBgColor = colorResource(id = R.color.notePreviewBgClr)
    val shareBtnColor = colorResource(id = R.color.blueBtnClr)
    val descriptionText = colorResource(id = R.color.taskDetailTxtClr)
    val taskTitleColor = colorResource(id = R.color.black)

    val categoryColor = when (taskTitle) {
        Category.PERSONAL.toString() -> colorResource(id = R.color.personalCatClr)
        Category.WORK.toString() -> colorResource(id = R.color.workCatClr)
        Category.STUDY.toString() -> colorResource(id = R.color.studyCatClr)
        else -> colorResource(id = R.color.personalCatClr)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(previewScreenBg)
    ) {
        NotePreviewHeader(
            background = previewTitleBgColor,
            onBackClick = onBackClick,
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(previewScreenBg)
                .padding(horizontal = 14.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBg)
                        .padding(horizontal = 20.dp, vertical = 44.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(categoryColor)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "$taskTitle Note:",
                            color = taskTitleColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "$taskDescription",
                        color = descriptionText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onShareClick,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(54.dp)
                    .shadow(10.dp, RoundedCornerShape(18.dp), clip = false)
                    .clip(RoundedCornerShape(18.dp))
                    .background(shareBtnColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Share Note",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun NotePreviewHeader(
    background: Color,
    titleColor: Color = Color.Black,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back",
                tint = titleColor,
            )
        }
        Text(
            text = "Note Preview",
            color = titleColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
        )
    }
}
