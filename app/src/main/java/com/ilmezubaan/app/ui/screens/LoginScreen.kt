package com.ilmezubaan.app.ui.screens

import android.content.Context
import android.util.Log
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
import com.ilmezubaan.app.data.local.AppDatabase
import com.ilmezubaan.app.data.local.entities.UserStats
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val database = AppDatabase.getDatabase(context)
    val userStatsDao = database.userStatsDao()
    
    val sharedPreferences = remember { 
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) 
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
                sharedPreferences.edit()
                    .putString(identifier, password)
                    .putString("${identifier}_name", name)
                    .apply()
                
                coroutineScope.launch {
                    userStatsDao.insertUserStats(UserStats(userName = name))
                    onLoginSuccess()
                }
            }
        } else {
            val savedPassword = sharedPreferences.getString(identifier, null)
            val savedName = sharedPreferences.getString("${identifier}_name", "User")
            
            if (identifier.isEmpty() || password.isEmpty()) {
                errorMessage = "Please enter your details"
            } else if (savedPassword == password) {
                coroutineScope.launch {
                    userStatsDao.insertUserStats(UserStats(userName = savedName ?: "User"))
                    onLoginSuccess()
                }
            } else {
                errorMessage = "Invalid credentials"
            }
        }
    }

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

        // Tab Switching for Login Methods
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
            // Name Input
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
            // Email Input
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
            // Phone Input
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
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
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
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (confirmPasswordVisible) "Hide password" else "Show password"
                    
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
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
            onClick = handleAction as () -> Unit,
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
            passwordVisible = false
            confirmPasswordVisible = false
        }) {
            Text(if (isSignUp) "Already have an account? Login" else "New user? Create an account")
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Social Media Options
        Text("Or continue with", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialIcon(
                iconUrl = "https://www.google.com/favicon.ico",
                onClick = { 
                    coroutineScope.launch {
                        try {
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId("YOUR_SERVER_CLIENT_ID")
                                .setAutoSelectEnabled(true)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(context, request)
                            onLoginSuccess()
                        } catch (e: GetCredentialException) {
                            errorMessage = "Google Sign In Failed: ${e.message}"
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.width(32.dp))
            SocialIcon(
                iconUrl = "https://www.facebook.com/favicon.ico",
                onClick = { onLoginSuccess() }
            )
        }
    }
}

@Composable
fun SocialIcon(iconUrl: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .border(1.dp, Color.LightGray, CircleShape)
            .clickable { onClick() },
        color = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = if (iconUrl.contains("google")) "G" else "F",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (iconUrl.contains("google")) Color(0xFFDB4437) else Color(0xFF4267B2)
            )
        }
    }
}
