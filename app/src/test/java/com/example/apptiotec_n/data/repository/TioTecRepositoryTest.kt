package com.example.apptiotec_n.data.repository

import com.example.apptiotec_n.data.local.AppDatabase
import com.example.apptiotec_n.data.local.PerfilDao
import com.example.apptiotec_n.model.Perfil
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class TioTecRepositoryTest : StringSpec({

    fun perfilDePrueba() = Perfil(
        alias = "seba_dev",
        nivelExperiencia = "Básico",
        dispositivo = "Galaxy A54",
        sistemaOperativo = "Android 14"
    )

    "el perfil se obtiene desde el DAO" {
        val perfil = perfilDePrueba()
        val perfilDao = mockk<PerfilDao>()
        every { perfilDao.observarPerfil() } returns flowOf(perfil)
        val baseDatos = mockk<AppDatabase>(relaxed = true)
        every { baseDatos.perfilDao() } returns perfilDao

        val repositorio = TioTecRepository(baseDatos)

        runTest {
            repositorio.perfil.first() shouldBe perfil
        }
    }

    "guardarPerfil llama al DAO" {
        val perfil = perfilDePrueba()
        val perfilDao = mockk<PerfilDao>(relaxed = true)
        val baseDatos = mockk<AppDatabase>(relaxed = true)
        every { baseDatos.perfilDao() } returns perfilDao

        val repositorio = TioTecRepository(baseDatos)

        runTest {
            repositorio.guardarPerfil(perfil)
        }

        coVerify { perfilDao.guardar(perfil) }
    }
})
