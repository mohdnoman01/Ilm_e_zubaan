package com.ilmezubaan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ilmezubaan.app.ui.navigation.AppNavGraph
import com.ilmezubaan.app.ui.theme.IlmEZubaanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IlmEZubaanTheme {
                AppNavGraph()
            }
        }
    }
}
