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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
    android.util.Log.d("LoginScreen", "LoginScreen composed")
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
            color = com.ilmezubaan.app.ui.theme.DarkBg
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = com.ilmezubaan.app.ui.theme.NeonCyan)
            }
        }
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = com.ilmezubaan.app.ui.theme.DarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Logo
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = com.ilmezubaan.app.ui.theme.DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, com.ilmezubaan.app.ui.theme.NeonCyan)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?q=80&w=200&auto=format&fit=crop",
                            contentDescription = "Logo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Ilm-e-Zubaan",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = com.ilmezubaan.app.ui.theme.TextWhite
                )
                
                Text(
                    text = if (isSignUp) "Create your account" else "Your journey starts here...",
                    fontSize = 14.sp,
                    color = com.ilmezubaan.app.ui.theme.TextGrey
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = com.ilmezubaan.app.ui.theme.DarkSurface
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            containerColor = Color.Transparent,
                            contentColor = com.ilmezubaan.app.ui.theme.NeonCyan,
                            indicator = { tabPositions ->
                                if (selectedTabIndex < tabPositions.size) {
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        color = com.ilmezubaan.app.ui.theme.NeonCyan
                                    )
                                }
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { 
                                        selectedTabIndex = index 
                                        errorMessage = null
                                    },
                                    text = { Text(title, fontSize = 14.sp) }
                                )
                            }
                        }

                        if (isSignUp) {
                            LoginTextField(
                                value = name,
                                onValueChange = { name = it; errorMessage = null },
                                label = "Full Name",
                                icon = Icons.Default.Person,
                                focusManager = focusManager
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (selectedTabIndex == 0) {
                            LoginTextField(
                                value = email,
                                onValueChange = { email = it; errorMessage = null },
                                label = "Email Address",
                                icon = Icons.Default.Email,
                                keyboardType = KeyboardType.Email,
                                focusManager = focusManager
                            )
                        } else {
                            LoginTextField(
                                value = phoneNumber,
                                onValueChange = { if (it.all { c -> c.isDigit() }) { phoneNumber = it; errorMessage = null } },
                                label = "Phone Number",
                                icon = Icons.Default.Phone,
                                keyboardType = KeyboardType.Phone,
                                focusManager = focusManager
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LoginTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onPasswordToggle = { passwordVisible = !passwordVisible },
                            imeAction = if (isSignUp) ImeAction.Next else ImeAction.Done,
                            onDone = { handleAction() },
                            focusManager = focusManager
                        )

                        if (isSignUp) {
                            Spacer(modifier = Modifier.height(16.dp))
                            LoginTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it; errorMessage = null },
                                label = "Confirm Password",
                                icon = Icons.Default.Lock,
                                isPassword = true,
                                passwordVisible = confirmPasswordVisible,
                                onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                                imeAction = ImeAction.Done,
                                onDone = { handleAction() },
                                focusManager = focusManager
                            )
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = com.ilmezubaan.app.ui.theme.NeonRed,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { handleAction() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = com.ilmezubaan.app.ui.theme.NeonCyan)
                        ) {
                            Text(if (isSignUp) "Sign Up" else "Login", color = com.ilmezubaan.app.ui.theme.DarkBg, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { 
                    isSignUp = !isSignUp 
                    errorMessage = null
                }) {
                    Text(
                        if (isSignUp) "Already have an account? Login" else "New user? Create an account",
                        color = com.ilmezubaan.app.ui.theme.TextGrey
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Join the Community", fontSize = 14.sp, color = com.ilmezubaan.app.ui.theme.TextGrey)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SocialButton(
                        text = "Continue with Google",
                        icon = "G",
                        modifier = Modifier.weight(1f),
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

                                    val result = withTimeoutOrNull(10000) {
                                        credentialManager.getCredential(context, request)
                                    }

                                    if (result != null) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                                        homeViewModel.saveUser(googleIdTokenCredential.displayName ?: "Google User") {
                                            onLoginSuccess(false)
                                        }
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Google Sign In Failed"
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {},
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = com.ilmezubaan.app.ui.theme.DarkSurfaceLighter,
            focusedBorderColor = com.ilmezubaan.app.ui.theme.NeonCyan,
            focusedLabelColor = com.ilmezubaan.app.ui.theme.NeonCyan,
            unfocusedLabelColor = com.ilmezubaan.app.ui.theme.TextGrey,
            focusedTextColor = com.ilmezubaan.app.ui.theme.TextWhite,
            unfocusedTextColor = com.ilmezubaan.app.ui.theme.TextWhite
        ),
        leadingIcon = { Icon(icon, contentDescription = null, tint = com.ilmezubaan.app.ui.theme.TextGrey) },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onPasswordToggle) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = com.ilmezubaan.app.ui.theme.TextGrey
                    )
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus(); onDone() }
        )
    )
}

@Composable
fun SocialButton(text: String, icon: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = com.ilmezubaan.app.ui.theme.DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, com.ilmezubaan.app.ui.theme.DarkSurfaceLighter)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (icon == "G") Color(0xFFDB4437) else Color(0xFF4267B2)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, color = com.ilmezubaan.app.ui.theme.TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
