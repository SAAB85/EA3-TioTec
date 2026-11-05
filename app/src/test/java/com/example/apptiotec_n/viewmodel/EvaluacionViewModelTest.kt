package com.example.apptiotec_n.viewmodel

import android.app.Application
import com.example.apptiotec_n.data.repository.TioTecRepository
import com.example.apptiotec_n.model.Pregunta
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class EvaluacionViewModelTest : StringSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    afterTest {
        Dispatchers.resetMain()
    }

    fun crearViewModel(
        repositorio: TioTecRepository = mockk(relaxed = true)
    ): EvaluacionViewModel =
        EvaluacionViewModel(mockk<Application>(relaxed = true), repositorio)

    fun responderTodo(viewModel: EvaluacionViewModel, valor: Int) {
        Pregunta.lista.forEach { viewModel.responder(it.id, valor) }
    }

    "responder guarda el valor y limpia el error de esa pregunta" {
        val viewModel = crearViewModel()
        viewModel.validarFormulario()

        viewModel.responder(1, 10)

        viewModel.uiState.value.respuestas[1] shouldBe 10
        viewModel.uiState.value.respondidas shouldBe 1
        viewModel.uiState.value.errores.contains(1) shouldBe false
    }

    "validar sin respuestas marca todas las preguntas" {
        val viewModel = crearViewModel()

        viewModel.validarFormulario() shouldBe false

        viewModel.uiState.value.errores shouldBe Pregunta.lista.map { it.id }.toSet()
        viewModel.uiState.value.mensajeError shouldBe
            "Te faltan 10 preguntas por responder"
    }

    "validar con todas las respuestas es correcto" {
        val viewModel = crearViewModel()
        responderTodo(viewModel, 10)

        viewModel.validarFormulario() shouldBe true

        viewModel.uiState.value.errores shouldBe emptySet()
        viewModel.uiState.value.mensajeError shouldBe null
    }

    "finalizar con 100 puntos guarda riesgo Bajo" {
        val repositorio = mockk<TioTecRepository>(relaxed = true)
        coEvery { repositorio.guardarEvaluacion(any(), any(), any()) } returns 1L
        val viewModel = crearViewModel(repositorio)
        responderTodo(viewModel, 10)

        viewModel.finalizar()

        viewModel.uiState.value.terminada shouldBe true
        coVerify {
            repositorio.guardarEvaluacion(
                match { it.puntaje == 100 && it.riesgo == "Bajo" },
                any(),
                any()
            )
        }
    }

    "finalizar con 50 puntos guarda riesgo Medio" {
        val repositorio = mockk<TioTecRepository>(relaxed = true)
        coEvery { repositorio.guardarEvaluacion(any(), any(), any()) } returns 1L
        val viewModel = crearViewModel(repositorio)
        Pregunta.lista.take(5).forEach { viewModel.responder(it.id, 10) }
        Pregunta.lista.drop(5).forEach { viewModel.responder(it.id, 0) }

        viewModel.finalizar()

        coVerify {
            repositorio.guardarEvaluacion(
                match { it.puntaje == 50 && it.riesgo == "Medio" },
                any(),
                any()
            )
        }
    }

    "finalizar con 0 puntos guarda riesgo Alto" {
        val repositorio = mockk<TioTecRepository>(relaxed = true)
        coEvery { repositorio.guardarEvaluacion(any(), any(), any()) } returns 1L
        val viewModel = crearViewModel(repositorio)
        responderTodo(viewModel, 0)

        viewModel.finalizar()

        coVerify {
            repositorio.guardarEvaluacion(
                match { it.puntaje == 0 && it.riesgo == "Alto" },
                any(),
                any()
            )
        }
    }
})
