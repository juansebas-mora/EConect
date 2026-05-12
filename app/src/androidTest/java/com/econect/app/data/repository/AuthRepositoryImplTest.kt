package com.econect.app.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.econect.app.domain.model.AuthError
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.UserType
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

/**
 * Test de integración contra Firebase Emulator Suite.
 *
 * Requisito: iniciar emuladores antes de correr los tests:
 *   firebase emulators:start --only auth,firestore
 *
 * Puertos por defecto:
 *   - Auth:      10.0.2.2:9099  (host → emulador Android)
 *   - Firestore: 10.0.2.2:8080
 */
@RunWith(AndroidJUnit4::class)
class AuthRepositoryImplTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Conectar a emuladores. El bloque try/catch evita error si ya se configuró
        // en una ejecución anterior dentro del mismo proceso.
        try {
            auth.useEmulator("10.0.2.2", 9099)
        } catch (_: IllegalStateException) { }

        try {
            firestore.useEmulator("10.0.2.2", 8080)
        } catch (_: IllegalStateException) { }

        repository = AuthRepositoryImpl(auth, firestore)
    }

    @After
    fun tearDown() = runBlocking {
        // Eliminar usuario de prueba para dejar el emulador limpio
        try {
            auth.currentUser?.delete()?.await()
        } catch (_: Exception) { }
        auth.signOut()
    }

    // ─────────────────────── Register ───────────────────────

    @Test
    fun register_withValidData_returnsSuccessAndCreatesUser() = runBlocking {
        val email = uniqueEmail()

        val result = repository.register(
            email = email,
            password = "password123",
            name = "Juan Prueba",
            phone = "3001234567",
            userType = UserType.CITIZEN
        )

        assertTrue("Resultado debe ser Success", result is Result.Success)
        with((result as Result.Success).data) {
            assertEquals(email, this.email)
            assertEquals("Juan Prueba", name)
            assertEquals(UserType.CITIZEN, userType)
            assertNotNull(id)
        }
        assertTrue(repository.isLoggedIn())
    }

    @Test
    fun register_withDuplicateEmail_returnsEmailAlreadyInUse() = runBlocking {
        val email = uniqueEmail()

        repository.register(email, "pass123", "Usuario 1", "300", UserType.CITIZEN)
        repository.logout()

        val result = repository.register(email, "otropass", "Usuario 2", "301", UserType.RECYCLER)

        assertTrue(result is Result.Error)
        assertTrue(
            "Debe ser EmailAlreadyInUse",
            (result as Result.Error).exception is AuthError.EmailAlreadyInUse
        )
    }

    // ─────────────────────── Login ───────────────────────

    @Test
    fun login_withCorrectCredentials_returnsUserWithCorrectType() = runBlocking {
        val email = uniqueEmail()
        repository.register(email, "pass123", "Reciclador Prueba", "310", UserType.RECYCLER)
        repository.logout()

        val result = repository.login(email, "pass123")

        assertTrue(result is Result.Success)
        with((result as Result.Success).data) {
            assertEquals(email, this.email)
            assertEquals(UserType.RECYCLER, userType)
        }
    }

    @Test
    fun login_withWrongPassword_returnsInvalidCredentials() = runBlocking {
        val email = uniqueEmail()
        repository.register(email, "correctpass", "Test", "300", UserType.CITIZEN)
        repository.logout()

        val result = repository.login(email, "wrongpass")

        assertTrue(result is Result.Error)
        assertTrue(
            "Debe ser InvalidCredentials",
            (result as Result.Error).exception is AuthError.InvalidCredentials
        )
    }

    @Test
    fun login_withNonExistentEmail_returnsUserNotFoundOrInvalidCredentials() = runBlocking {
        val result = repository.login("noexiste_${UUID.randomUUID()}@test.com", "pass")

        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).exception
        // Firebase puede devolver InvalidCredentials o UserNotFound según configuración
        assertTrue(
            "Debe ser error de auth tipado",
            exception is AuthError.InvalidCredentials || exception is AuthError.UserNotFound
        )
    }

    // ──────────────────── getCurrentUser ────────────────────

    @Test
    fun getCurrentUser_whenLoggedIn_returnsUser() = runBlocking {
        val email = uniqueEmail()
        repository.register(email, "pass123", "Current Test", "300", UserType.CITIZEN)

        val result = repository.getCurrentUser()

        assertTrue(result is Result.Success)
        assertNotNull((result as Result.Success).data)
        assertEquals(email, result.data?.email)
    }

    @Test
    fun getCurrentUser_whenNotLoggedIn_returnsSuccessWithNull() = runBlocking {
        repository.logout()

        val result = repository.getCurrentUser()

        assertTrue(result is Result.Success)
        assertTrue("Debe retornar null cuando no hay sesión", (result as Result.Success).data == null)
    }

    // ──────────────────── Logout / isLoggedIn ───────────────

    @Test
    fun logout_clearsSession_isLoggedInReturnsFalse() = runBlocking {
        val email = uniqueEmail()
        repository.register(email, "pass123", "Logout Test", "300", UserType.CITIZEN)
        assertTrue(repository.isLoggedIn())

        repository.logout()

        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun isLoggedIn_afterRegister_returnsTrue() = runBlocking {
        repository.register(uniqueEmail(), "pass123", "Test", "300", UserType.RECYCLER)

        assertTrue(repository.isLoggedIn())
    }

    // ─────────────────────── Helper ─────────────────────────

    private fun uniqueEmail() = "test_${UUID.randomUUID()}@econect.test"
}
