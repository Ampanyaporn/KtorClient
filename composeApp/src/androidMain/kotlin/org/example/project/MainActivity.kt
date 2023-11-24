package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    var messageLog by remember { mutableStateOf("") }
    var currentMessage by remember { mutableStateOf(TextFieldValue("")) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, Color.Black)
                .padding(8.dp)
        ) {
            Text(text = messageLog)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = currentMessage,
                onValueChange = { currentMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray)
                    .padding(8.dp)
            )
            Button(
                onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            try {
                                val socket = Socket("10.0.2.2", 9002)
                                val writer = PrintWriter(socket.getOutputStream(), true)
                                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                                writer.println(currentMessage.text)
                                currentMessage = TextFieldValue("")
                                while (true) {
                                    val serverMessage = reader.readLine() ?: break
                                    withContext(Dispatchers.Main) {
                                        messageLog += "Server: $serverMessage\n"
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    messageLog += "Error: ${e.localizedMessage}\n"
                                }
                            }
                        }
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}
