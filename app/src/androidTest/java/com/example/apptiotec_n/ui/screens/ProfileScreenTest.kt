package com.example.apptiotec_n.ui.screens

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.apptiotec_n.ui.theme.AppTioTec_NTheme
import com.example.apptiotec_n.viewmodel.MainViewModel
import com.example.apptiotec_n.viewmodel.PerfilViewModel
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun muestraElFormularioDelPerfil() {
        val aplicacion = ApplicationProvider.getApplicationContext<Application>()
        val perfilViewModel = PerfilViewModel(aplicacion)

        composeRule.setContent {
            AppTioTec_NTheme {
                ProfileScreen(
                    viewModel = MainViewModel(),
                    perfilViewModel = perfilViewModel
                )
            }
        }

        composeRule.onNodeWithText("Mi perfil").assertIsDisplayed()
        composeRule.onNodeWithText("Alias").assertIsDisplayed()
        composeRule.onNodeWithText("Guardar perfil").assertIsDisplayed()
    }
}
