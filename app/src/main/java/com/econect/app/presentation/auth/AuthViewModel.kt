package com.econect.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.econect.app.data.local.datastore.UserDataStore
import com.econect.app.domain.model.AuthError
import com.econect.app.domain.model.Result
import com.econect.app.domain.model.UserType
import com.econect.app.domain.usecase.GetCurrentUserUseCase
import com.econect.app.domain.usecase.LoginUseCase
import com.econect.app.domain.usecase.LogoutUseCase
import com.econect.app.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val userType: UserType? = null
)

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val userType: UserType? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, error = null) }
            when (val result = loginUseCase(email, password)) {
                is Result.Success -> {
                    userDataStore.saveActiveUser(result.data.id, result.data.userType)
                    _loginState.update {
                        it.copy(isLoading = false, success = true, userType = result.data.userType)
                    }
                }
                is Result.Error -> _loginState.update {
                    it.copy(isLoading = false, error = mapError(result.exception))
                }
                Result.Loading -> Unit
            }
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        phone: String,
        userType: UserType
    ) {
        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true, error = null) }
            when (val result = registerUseCase(email, password, name, phone, userType)) {
                is Result.Success -> {
                    userDataStore.saveActiveUser(result.data.id, result.data.userType)
                    _registerState.update {
                        it.copy(isLoading = false, success = true, userType = result.data.userType)
                    }
                }
                is Result.Error -> _registerState.update {
                    it.copy(isLoading = false, error = mapError(result.exception))
                }
                Result.Loading -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            userDataStore.clearActiveUser()
        }
    }

    fun clearLoginError() = _loginState.update { it.copy(error = null) }

    fun clearRegisterError() = _registerState.update { it.copy(error = null) }

    private fun mapError(exception: Throwable): String = when (exception) {
        is AuthError.InvalidCredentials -> "Correo o contraseña incorrectos"
        is AuthError.UserNotFound -> "No existe una cuenta con ese correo"
        is AuthError.EmailAlreadyInUse -> "Ya existe una cuenta con ese correo"
        is AuthError.NetworkError -> "Sin conexión a internet. Verifica tu red"
        is AuthError.ProfileNotFound -> "Tu cuenta no tiene perfil completo. Por favor regístrate de nuevo"
        else -> "Ocurrió un error inesperado. Intenta de nuevo"
    }
}
