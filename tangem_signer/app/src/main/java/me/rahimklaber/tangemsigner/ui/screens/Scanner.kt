package me.rahimklaber.tangemsigner.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView

/**
 * From : https://stackoverflow.com/questions/68139363/using-zxing-library-with-jetpack-compose
 */
@Composable
fun Scanner(navController: NavController,onScan : (String) -> Unit, onBack : () -> Unit) {
    val context = LocalContext.current
    var scanFlag by remember {
        mutableStateOf(false)
    }

    BackHandler{onBack()}

    val compoundBarcodeView = remember {
        CompoundBarcodeView(context).apply {
            val capture = CaptureManager(context as Activity, this)
            capture.initializeFromIntent(context.intent, null)
            this.setStatusText("")
            this.resume()
            capture.decode()
            this.decodeContinuous { result ->
                if(scanFlag){
                    return@decodeContinuous
                }
                scanFlag = true
                result.text?.let { scannedXdr->
                    onScan(scannedXdr)
                    scanFlag = false
                }

            }
        }
    }

    DisposableEffect(key1 = "_unneeded" ){
        compoundBarcodeView.resume()
        onDispose {
            compoundBarcodeView.pause()
        }
    }

    AndroidView(
        modifier = Modifier,
        factory = { compoundBarcodeView },
    )
}