package me.rahimklaber.tangemsigner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.rahimklaber.tangemsigner.ui.screens.Home
import me.rahimklaber.tangemsigner.ui.screens.TransactionScreen
import me.rahimklaber.tangemsigner.ui.theme.TangemSignerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            TangemSignerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home"){
                            Home(navController)
                        }
                        composable("transaction/{xdr}"){
                            TransactionScreen(navController,it.arguments?.getString("xdr"))
                        }
                    }
                }


            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    val tangemSdk: TangemSdk = TangemSdk.init(LocalContext.current as ComponentActivity)
//    TangemSignerTheme {
//        Home(navController)
//    }
//}