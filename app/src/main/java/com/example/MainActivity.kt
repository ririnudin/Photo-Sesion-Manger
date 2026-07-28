package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.SettingsRepository
import com.example.ui.navigation.AppNavGraph
import com.example.ui.theme.PhotoSessionTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val settingsRepository = SettingsRepository(applicationContext)

    setContent {
      val settings by settingsRepository.settingsState.collectAsState()
      val systemDark = isSystemInDarkTheme()
      val isDarkTheme = when (settings.appTheme) {
        "DARK" -> true
        "LIGHT" -> false
        else -> systemDark
      }

      PhotoSessionTheme(darkTheme = isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
          val navController = rememberNavController()
          AppNavGraph(navController = navController)
        }
      }
    }
  }
}
