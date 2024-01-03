package com.example.androidexam2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.Circle


class UserComposeFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeContent()
            }
        }
    }
}


@Composable
fun ComposeContent() {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    )
    {
        Row(modifier = Modifier
        ) {
            profileImage()
            Text(text = "UserName",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_bold))
                ),
                modifier = Modifier
                    .align(Bottom)
                    .padding(20.dp)
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, Color.Black)
                    )
                    .padding(5.dp)



            )
        }
        Text(text = "Hello from Compose Fragment!")
        Text(text = "Test")
    }
}

@Composable
fun profileImage(){
    Image(painter = painterResource(id = R.drawable.baseline_question_mark_24),
        contentDescription = stringResource(id = R.string.imageNotSet),
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .padding(10.dp)
            .border(
                BorderStroke(1.dp, Color.Black),
                CircleShape
            )
            .fillMaxWidth()
            .clickable {
                onProfileClick()
            }

    )
}

fun onProfileClick() {
    Log.d("!!!", "ProfileClicked")
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeContent()
}
