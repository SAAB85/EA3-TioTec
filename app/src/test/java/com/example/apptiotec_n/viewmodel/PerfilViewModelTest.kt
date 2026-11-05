package com.example.apptiotec_n.viewmodel

import android.app.Application
import com.example.apptiotec_n.data.repository.TioTecRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun crearViewModel(repositorio: TioTecRepository): PerfilViewModel {
        every { repositorio.perfil } returns flowOf(null)
        return PerfilViewModel(mockk<Application>(relaxed = true), repositorio)
    }

    "el alias vacío muestra un error" {
        val viewModel = crearViewModel(mockk(relaxed = true))

        val valido = viewModel.validarFormulario()

        valido shouldBe false
        viewModel.uiState.value.errores.alias shouldBe "El alias es obligatorio"
    }

    "el alias de menos de 3 caracteres muestra un error" {
        val viewModel = crearViewModel(mockk(relaxed = true))
        viewModel.cambiarAlias("ab")

        viewModel.validarFormulario() shouldBe false

        viewModel.uiState.value.errores.alias shouldBe
            "El alias debe tener al menos 3 caracteres"
    }

    "un formulario completo es válido" {
        val viewModel = crearViewModel(mockk(relaxed = true))
        viewModel.cambiarAlias("seba_dev")
        viewModel.cambiarNivelExperiencia("Básico")
        viewModel.cambiarDispositivo("Galaxy A54")
        viewModel.cambiarSistemaOperativo("Android 14")

        viewModel.validarFormulario() shouldBe true

        viewModel.uiState.value.errores.alias shouldBe null
        viewModel.uiState.value.errores.dispositivo shouldBe null
    }

    "guardar el perfil lo envía al repositorio" {
        val repositorio = mockk<TioTecRepository>(relaxed = true)
        coEvery { repositorio.guardarPerfil(any()) } just Runs
        val viewModel = crearViewModel(repositorio)
        viewModel.cambiarAlias("seba_dev")
        viewModel.cambiarNivelExperiencia("Básico")
        viewModel.cambiarDispositivo("Galaxy A54")
        viewModel.cambiarSistemaOperativo("Android 14")

        viewModel.guardarPerfil()

        viewModel.uiState.value.perfilGuardado shouldBe true
        coVerify {
            repositorio.guardarPerfil(match { it.alias == "seba_dev" })
        }
    }

    "si el repositorio falla se muestra un mensaje de error" {
        val repositorio = mockk<TioTecRepository>(relaxed = true)
        coEvery { repositorio.guardarPerfil(any()) } throws
            RuntimeException("fallo al guardar")
        val viewModel = crearViewModel(repositorio)
        viewModel.cambiarAlias("seba_dev")
        viewModel.cambiarNivelExperiencia("Básico")
        viewModel.cambiarDispositivo("Galaxy A54")
        viewModel.cambiarSistemaOperativo("Android 14")

        viewModel.guardarPerfil()

        viewModel.uiState.value.mensajeError shouldBe "No se pudo guardar el perfil"
    }
})
