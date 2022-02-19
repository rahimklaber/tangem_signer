package me.rahimklaber.tangemsigner.ui.screens

import android.app.Activity
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.htmlEncode
import androidx.navigation.NavHostController
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.tangem.Log
import com.tangem.TangemSdk
import com.tangem.common.CompletionResult
import com.tangem.tangem_sdk_new.extensions.init
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


//@Composable()
//fun Home() {
//    val tangemSdk: TangemSdk = TangemSdk.init(LocalContext.current as ComponentActivity)
//    val scope = rememberCoroutineScope()
//    val mainScope = rememberCoroutineScope { Dispatchers.Main + SupervisorJob() }
//    var scanResult by remember { mutableStateOf("") }
//   Column(
//       verticalArrangement = Arrangement.Center,
//       horizontalAlignment = Alignment.CenterHorizontally,
//   ) {
//       Button(onClick = {
//           scope.launch {
//               tangemSdk.scanCard { result ->
//                   when (result) {
//                       is CompletionResult.Success -> {
//                           scope.launch {
//                               scanResult = "success"
//                           }
//                       }
//                       is CompletionResult.Failure -> {
//                           scope.launch {
//                               scanResult = "failure"
//                           }
//                       }
//                   }
//               }
//           }
//       }) {
//           Text("scan card")
//       }
//       Spacer(modifier = Modifier.height(200.dp))
//       Text("scan was a : $scanResult")
//   }
//}


@Composable()
fun Home(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val mainScope = rememberCoroutineScope { Dispatchers.Main + SupervisorJob() }
    var scanResult by remember { mutableStateOf("") }
    var scanning by remember { mutableStateOf(false) }
    var scanned by remember { mutableStateOf(false) }

    if (scanning) {
        Scanner(navController, onScan = {
            scanResult = it
            scanned = true
            val replacedXdr = scanResult.replace("/","_")
            println(replacedXdr)
            navController.navigate("transaction/${replacedXdr}")
            scanning = false
        }) {
            scanning = false
        }
    } else if(!scanned){
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Tangem signer", fontSize = 30.sp)
            Spacer(modifier = Modifier.height(15.dp))
            Button(onClick = {
                scanning = true
            }) {
                Text("scan qr code")
            }
        }
    }
}