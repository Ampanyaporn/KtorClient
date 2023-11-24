import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlinx.coroutines.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Compose Desktop") {
        MainScreen()
    }
}

@Composable
fun MainScreen() {
    var message by remember { mutableStateOf("") }
    var log by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        BasicTextField(
            value = log,
            onValueChange = { log = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BasicTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            try {
//                                val socket = Socket("127.0.0.1", 9002)
                                val socket = Socket("10.0.2.2", 9002)
                                val writer = PrintWriter(socket.getOutputStream(), true)
                                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                                writer.println(message)
                                val response = reader.readLine()
                                withContext(Dispatchers.Main) {
                                    log += "Server: $response\n"
                                }
                                socket.close()
                            } catch (e: Exception) {
                                e.printStackTrace()  // Handle exceptions appropriately
                            }
                        }
                    }
                    message = ""
                }
            ) {
                Text("Send")
            }

        }
    }
}
