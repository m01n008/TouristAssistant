package com.project.ta
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContent {
            MessageCard("Pakistan")
        }
    }
}


@Composable
fun MessageCard(name: String){
    Text(text = "Salam $name")
}
@Preview
@Composable
fun previewMessageCard(){
    MessageCard("Moin")
}