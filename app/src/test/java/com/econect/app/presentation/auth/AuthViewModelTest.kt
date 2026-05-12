package com.econect.app.presentation.auth

import app.cash.turbine.test
import com.econect.app.domain.model.AuthError
import com.econect.app.domain.model.LatLng
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.User
import com.econect.app.domain.model.UserType
import com.econect.app.domain.usecase.GetCurrentUserUseCase
import com.econect.app.domain.usecase.LoginUseCase
import com.econect.app.domain.usecase.LogoutUseCase
import com.econect.app.domain.usecase.RegisterUseCase
import com.econect.app.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        loginUseCase = mockk()
        registerUseCase = mockk()
        logoutUseCase = mockk()
        getCurrentUserUseCase = mockk()
        viewModel = AuthViewModel(loginUseCase, registerUseCase, logoutUseCase, getCurrentUserUseCase)
    }

    // ───────────────────────── Login ─────────────────────────

    @Test
    fun `loginSuccess citizen sets success true and userType CITIZEN`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.Success(fakeUser(UserType.CITIZEN))

        viewModel.loginState.test {
            assertFalse(awaitItem().success) // estado inicial

            viewModel.login("user@test.com", "password123")

            assertTrue(awaitItem().isLoading) // cargando
            with(awaitItem()) {             // resultado final
                assertTrue(success)
                assertFalse(isLoading)
                assertEquals(UserType.CITIZEN, userType)
                assertNull(error)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loginSuccess recycler sets userType RECYCLER`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns Result.Success(fakeUser(UserType.RECYCLER))

        viewModel.loginState.test {
            awaitItem() // inicial

            viewModel.login("recycler@test.com", "password123")

            awaitItem() // loading
            with(awaitItem()) {
                assertTrue(success)
                assertEquals(UserType.RECYCLER, userType)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loginFailure invalidCredentials sets error message in spanish`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns
            Result.Error(AuthError.InvalidCredentials)

        viewModel.loginState.test {
            awaitItem() // inicial

            viewModel.login("user@test.com", "wrongpass")

            awaitItem() // loading
            with(awaitItem()) {
                assertFalse(success)
                assertFalse(isLoading)
                assertNull(userType)
                assertEquals("Correo o contraseña incorrectos", error)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loginFailure userNotFound sets appropriate error message`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns
            Result.Error(AuthError.UserNotFound)

        viewModel.loginState.test {
            awaitItem()
            viewModel.login("noexiste@test.com", "pass")
            awaitItem()
            assertEquals("No existe una cuenta con ese correo", awaitItem().error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loginFailure networkError sets appropriate error message`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns
            Result.Error(AuthError.NetworkError)

        viewModel.loginState.test {
            awaitItem()
            viewModel.login("user@test.com", "pass")
            awaitItem()
            assertEquals("Sin conexión a internet. Verifica tu red", awaitItem().error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loginFailure unknownError sets generic error message`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns
            Result.Error(RuntimeException("boom"))

        viewModel.loginState.test {
            awaitItem()
            viewModel.login("user@test.com", "pass")
            awaitItem()
            assertEquals("Ocurrió un error inesperado. Intenta de nuevo", awaitItem().error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearLoginError resets error to null`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns
            Result.Error(AuthError.InvalidCredentials)

        viewModel.loginState.test {
            awaitItem()
            viewModel.login("user@test.com", "wrong")
            awaitItem() // loading
            awaitItem() // error state

            viewModel.clearLoginError()

            assertNull(awaitItem().error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─────────────────────── Register ───────────────────────

    @Test
    fun `registerSuccess citizen sets success and userType CITIZEN`() = runTest {
        coEvery {
            registerUseCase(any(), any(), any(), any(), any())
        } returns Result.Success(fakeUser(UserType.CITIZEN))

        viewModel.registerState.test {
            assertFalse(awaitItem().success) // inicial

            viewModel.register(
                email = "new@test.com",
                password = "pass123",
                name = "Nuevo Usuario",
                phone = "3001234567",
                userType = UserType.CITIZEN
            )

            assertTrue(awaitItem().isLoading) // cargando
            with(awaitItem()) {             // resultado
                assertTrue(success)
                assertFalse(isLoading)
                assertEquals(UserType.CITIZEN, userType)
                assertNull(error)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `registerSuccess recycler sets userType RECYCLER`() = runTest {
        coEvery {
            registerUseCase(any(), any(), any(), any(), any())
        } returns Result.Success(fakeUser(UserType.RECYCLER))

        viewModel.registerState.test {
            awaitItem()
            viewModel.register("r@test.com", "pass123", "Reciclador", "300", UserType.RECYCLER)
            awaitItem()
            with(awaitItem()) {
                assertTrue(success)
                assertEquals(UserType.RECYCLER, userType)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `registerFailure emailAlreadyInUse sets appropriate error message`() = runTest {
        coEvery {
            registerUseCase(any(), any(), any(), any(), any())
        } returns Result.Error(AuthError.EmailAlreadyInUse)

        viewModel.registerState.test {
            awaitItem()
            viewModel.register("dup@test.com", "pass", "Test", "123", UserType.CITIZEN)
            awaitItem()
            with(awaitItem()) {
                assertFalse(success)
                assertEquals("Ya existe una cuenta con ese correo", error)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearRegisterError resets error to null`() = runTest {
        coEvery {
            registerUseCase(any(), any(), any(), any(), any())
        } returns Result.Error(AuthError.EmailAlreadyInUse)

        viewModel.registerState.test {
            awaitItem()
            viewModel.register("dup@test.com", "pass", "Test", "123", UserType.CITIZEN)
            awaitItem()
            assertNotNull(awaitItem().error) // estado con error

            viewModel.clearRegisterError()
            assertNull(awaitItem().error)    // error limpiado

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─────────────────────── Helpers ────────────────────────

    private fun fakeUser(userType: UserType) = User(
        id = "uid_test",
        name = "Test User",
        email = "user@test.com",
        phone = "3001234567",
        userType = userType,
        preferredLocations = emptyList(),
        availableSchedules = emptyList(),
        rating = 0f,
        ratingCount = 0
    )
}
