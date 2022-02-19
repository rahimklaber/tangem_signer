package me.rahimklaber.tangemsigner.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tangem.Message
import com.tangem.TangemSdk
import com.tangem.common.CompletionResult
import com.tangem.operations.sign.SignHashCommand
import com.tangem.tangem_sdk_new.extensions.init
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.rahimklaber.tangemsigner.ui.theme.Purple200
import me.rahimklaber.tangemsigner.ui.theme.Shapes
import org.stellar.sdk.Network
import org.stellar.sdk.Transaction
import org.stellar.sdk.xdr.Operation
import org.stellar.sdk.xdr.PaymentOp

/**
 * @param encodedXdr: the xdr is encoded so it can be used with jetpack navigation, since the xdr
 * can contain "/".
 */
@Composable
fun TransactionScreen(navController: NavHostController, encodedXdr: String?) {
    val scope = rememberCoroutineScope { Dispatchers.Default }
    val tangemSdk = TangemSdk.init(LocalContext.current as ComponentActivity)
    val xdr = encodedXdr?.replace("_", "/")
    val txv1 = Transaction.fromEnvelopeXdr(xdr, Network.TESTNET).toEnvelopeXdr().v1.tx
    val tx = Transaction.fromEnvelopeXdr(xdr, Network.TESTNET)
    var signed by remember { mutableStateOf(false) }
    var signature by remember {
        mutableStateOf(byteArrayOf())
    }
    Column(Modifier.fillMaxSize()) {
        Surface(
            Modifier
                .fillMaxWidth()
                .shadow(10.dp),
            color = Purple200
        ) {
            Text(
                "Operations",
                fontSize = 40.sp,
                modifier = Modifier.padding(PaddingValues(start = 10.dp))
            )
        }
        LazyColumn(
            Modifier
                .fillMaxHeight(0.9f)
                .padding(PaddingValues(top = 10.dp))
        ) {
            items(txv1.operations) { op ->
                OperationRow(Modifier.padding(4.dp), op)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                modifier = Modifier.fillMaxWidth(0.33f),
                onClick = { scope.launch(Dispatchers.Main) { navController.popBackStack() } }) {
                Text("Reject")
            }
            Button(modifier = Modifier.fillMaxWidth(0.5f), onClick = {
                scope.launch {
                    withContext(scope.coroutineContext) {
                        tangemSdk.startSession { session, error ->
                            if (error != null) {
                                session.stopWithError(error)
                                return@startSession
                            }
                            val wallet = session.environment.card?.wallets?.get(0)
                            if (wallet == null) {
                                session.stop(Message("wallet is null"))
                                return@startSession
                            }

                            val publicKey = wallet.publicKey

                            SignHashCommand(tx.hash(), publicKey).run(session) { res ->
                                when (res) {
                                    is CompletionResult.Success -> {
                                        signed = true
                                        signature = res.data.signature
                                        session.stop(Message("Transaction Signed!"))
                                        scope.launch(Dispatchers.Main) {
                                            navController.popBackStack()
                                        }
                                    }
                                    is CompletionResult.Failure -> {
                                        println("ERROR")
                                        session.stopWithError(res.error)
                                    }
                                }
                            }

                        }
                    }


                }
            }) {
                Text("Accept")
            }
        }
    }


}


@Composable
fun OperationRow(modifier: Modifier = Modifier, operation: Operation) = when {
    operation.body.paymentOp != null -> PaymentRow(modifier, operation.body.paymentOp)
    else -> throw NotImplementedError()
}

@Composable
fun PaymentRow(modifier: Modifier = Modifier, payment: PaymentOp) {
    Row(
        modifier
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Surface(
            shape = Shapes.medium,
            color = Color.White,
            elevation = 5.dp
        ) {
            Text("Payment", fontSize = 18.sp)
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Column {
                    Text("Amount")
                    Text(payment.amount.int64.toString())
                }
            }
        }
    }
}