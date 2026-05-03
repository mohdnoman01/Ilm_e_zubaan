package com.ilmezubaan.app.ui.screens

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.ilmezubaan.app.ui.viewmodel.HomeViewModel
import com.ilmezubaan.app.utils.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

@Composable
fun LoginScreen(
    onLoginSuccess: (isNewUser: Boolean) -> Unit,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    
    var encryptedPrefs by remember { mutableStateOf<android.content.SharedPreferences?>(null) }
    var isLoadingSecurity by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val prefs = SecurityUtils.getEncryptedPrefs(context)
            encryptedPrefs = prefs
            isLoadingSecurity = false
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Email", "Phone")

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val handleAction = {
        val prefs = encryptedPrefs
        if (prefs == null) {
            errorMessage = "Security initialization in progress..."
        } else {
            val identifier = if (selectedTabIndex == 0) email else phoneNumber
            val isValidIdentifier = if (selectedTabIndex == 0) {
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            } else {
                phoneNumber.length >= 10
            }

            if (isSignUp) {
                if (name.isEmpty() || identifier.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    errorMessage = "Please fill all fields"
                } else if (!isValidIdentifier) {
                    errorMessage = if (selectedTabIndex == 0) "Invalid email format" else "Invalid phone number"
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    val (hashedPassword, salt) = SecurityUtils.hashPassword(password)
                    prefs.edit()
                        .putString(identifier, hashedPassword)
                        .putString("${identifier}_salt", salt)
                        .putString("${identifier}_name", name)
                        .apply()
                    
                    homeViewModel.saveUser(name) {
                        onLoginSuccess(true)
                    }
                }
            } else {
                val savedHashedPassword = prefs.getString(identifier, null)
                val savedSalt = prefs.getString("${identifier}_salt", null)
                val savedName = prefs.getString("${identifier}_name", "User")
                
                if (identifier.isEmpty() || password.isEmpty()) {
                    errorMessage = "Please enter your details"
                } else if (savedHashedPassword != null && savedSalt != null && 
                    SecurityUtils.verifyPassword(password, savedHashedPassword, savedSalt)) {
                    homeViewModel.saveUser(savedName ?: "User") {
                        onLoginSuccess(false)
                    }
                } else {
                    errorMessage = "Invalid credentials"
                }
            }
        }
    }

    if (isLoadingSecurity) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isSignUp) "Create Account" else "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { 
                            selectedTabIndex = index 
                            errorMessage = null
                        },
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (index == 0) Icons.Default.Email else Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(title)
                            }
                        }
                    )
                }
            }

            if (isSignUp) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        errorMessage = null 
                    },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (selectedTabIndex == 0) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        errorMessage = null 
                    },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
            } else {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            phoneNumber = input
                            errorMessage = null
                        }
                    },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { input ->
                    password = input
                    errorMessage = null
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isSignUp) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    onDone = { 
                        focusManager.clearFocus()
                        handleAction() 
                    }
                )
            )

            if (isSignUp) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { input ->
                        confirmPassword = input
                        errorMessage = null
                    },
                    label = { Text("Confirm Password") },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { 
                            focusManager.clearFocus()
                            handleAction() 
                        }
                    )
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { handleAction() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isSignUp) "Sign Up" else "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { 
                isSignUp = !isSignUp 
                errorMessage = null
                email = ""
                phoneNumber = ""
                password = ""
                confirmPassword = ""
            }) {
                Text(if (isSignUp) "Already have an account? Login" else "New user? Create an account")
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Or continue with", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialIcon(
                    iconUrl = "google",
                    onClick = { 
                        coroutineScope.launch {
                            try {
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId("100091555033-ggg0p1mb8omqv8p75q6j37qas1vudi34.apps.googleusercontent.com")
                                    .setAutoSelectEnabled(false)
                                    .setNonce(java.util.UUID.randomUUID().toString())
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                Timber.d("Starting Google Sign In with 10s timeout...")
                                val result = withTimeoutOrNull(10000) {
                                    credentialManager.getCredential(context, request)
                                }

                                if (result == null) {
                                    Timber.e("Google Sign In Timed Out")
                                    errorMessage = "Sign in timed out. Please check your internet connection or Google Play Services."
                                    return@launch
                                }
                                
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                                val displayName = googleIdTokenCredential.displayName ?: "Google User"
                                Timber.d("Google Sign In Success: $displayName")
                                
                                homeViewModel.saveUser(displayName) {
                                    onLoginSuccess(false)
                                }
                            } catch (e: GetCredentialException) {
                                Timber.e(e, "Google Sign In Failed: ${e.type}")
                                errorMessage = "Google Sign In Failed: ${e.message}"
                            } catch (e: Exception) {
                                Timber.e(e, "Unexpected error during Google Sign In")
                                errorMessage = "An error occurred: ${e.message}"
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.width(32.dp))
                SocialIcon(
                    iconUrl = "facebook",
                    onClick = { onLoginSuccess(false) }
                )
            }
        }
    }
}

@Composable
fun SocialIcon(iconUrl: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .border(1.dp, Color.LightGray, androidx.compose.foundation.shape.CircleShape)
            .clickable { onClick() },
        color = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = if (iconUrl == "google") "G" else "F",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (iconUrl == "google") Color(0xFFDB4437) else Color(0xFF4267B2)
            )
        }
    }
}
