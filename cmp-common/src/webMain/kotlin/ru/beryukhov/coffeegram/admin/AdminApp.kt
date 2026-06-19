package ru.beryukhov.coffeegram.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

internal fun isAdminRoute(): Boolean = locationSearch().contains("admin", ignoreCase = true)

@Composable
fun AdminApp() {
    val auth = remember { SupabaseAuth() }
    var session by remember { mutableStateOf(auth.captureSessionFromUrl() ?: auth.loadSession()) }

    if (session == null) {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginScreen(onSendLink = { email -> auth.sendMagicLink(email) })
        }
    } else {
        val repository = remember { AdminRepository(accessToken = { session?.accessToken.orEmpty() }) }
        AdminConsole(
            repository = repository,
            onSignOut = {
                auth.signOut()
                session = null
            },
        )
    }
}

private sealed interface LoginStatus {
    data object Idle : LoginStatus
    data object Sending : LoginStatus
    data object Sent : LoginStatus
    data class Error(val message: String) : LoginStatus
}

@Composable
private fun LoginScreen(onSendLink: suspend (String) -> Result<Unit>) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<LoginStatus>(LoginStatus.Idle) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text("Coffeegram admin", style = MaterialTheme.typography.headlineSmall)
        Text("Sign in to edit your collection", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            label = { Text("you@email.com") },
            modifier = Modifier.width(320.dp),
        )
        Button(
            onClick = {
                status = LoginStatus.Sending
                scope.launch {
                    status = onSendLink(email).fold(
                        onSuccess = { LoginStatus.Sent },
                        onFailure = { LoginStatus.Error(it.message ?: "Couldn't send the link") },
                    )
                }
            },
            enabled = email.isNotBlank() && status != LoginStatus.Sending,
        ) {
            Text("Send magic link")
        }
        when (val s = status) {
            LoginStatus.Sent -> Text(
                "Check your inbox for a one-tap sign-in link.",
                style = MaterialTheme.typography.bodyMedium,
            )
            is LoginStatus.Error -> Text(
                s.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
            else -> Unit
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    Surface {
        LoginScreen(onSendLink = { Result.success(Unit) })
    }
}
