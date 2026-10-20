package com.esandi.healthmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.esandi.healthmate.ui.theme.HealthMateTheme
import com.esandi.healthmate.ui.theme.ThemeShowcaseScreen

/**
 * The app's single activity. Every screen is a Compose function shown inside it.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Shows the HealthMate splash screen (logo on brand background) while the app starts.
        // Must be called before super.onCreate().
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthMateTheme {
                // Temporary: replaced by the navigation host and real screens in tasks 2.3-2.4.
                ThemeShowcaseScreen()
            }
        }
    }
}
