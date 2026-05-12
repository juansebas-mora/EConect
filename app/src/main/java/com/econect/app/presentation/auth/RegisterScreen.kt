package com.econect.app.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.econect.app.domain.model.MaterialPrice
import com.econect.app.domain.model.MaterialType
import com.econect.app.domain.model.MaterialUnit
import com.econect.app.domain.model.UserType

private val USER_TYPE_OPTIONS = listOf(
    UserType.CITIZEN to "Ciudadano",
    UserType.RECYCLER to "Reciclador",
    UserType.RECYCLING_CENTER to "Centro de reciclaje"
)

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (UserType) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.registerState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.success, uiState.userType) {
        if (uiState.success && uiState.userType != null) {
            onRegisterSuccess(uiState.userType!!)
        }
    }

    LaunchedEffect(uiState.error) {
        val msg = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearRegisterError()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        RegisterContent(
            isLoading = uiState.isLoading,
            onRegister = { email, password, name, phone, userType ->
                viewModel.register(email, password, name, phone, userType)
            },
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun RegisterContent(
    isLoading: Boolean,
    onRegister: (String, String, String, String, UserType) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var selectedUserType by rememberSaveable { mutableStateOf(UserType.CITIZEN) }
    var centerName by rememberSaveable { mutableStateOf("") }
    var materialPrices by rememberSaveable { mutableStateOf(emptyList<MaterialPrice>()) }
    var emailTouched by rememberSaveable { mutableStateOf(false) }

    val emailFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val emailError = if (emailTouched && !email.isValidEmail()) "Ingresa un correo válido" else null
    val formValid = name.isNotBlank() && email.isValidEmail() &&
                    phone.isNotBlank() && password.length >= 6 &&
                    (selectedUserType != UserType.RECYCLING_CENTER || centerName.isNotBlank())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre completo") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { emailFocusRequester.requestFocus() }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailTouched = true
            },
            label = { Text("Correo electrónico") },
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { phoneFocusRequester.requestFocus() }
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(phoneFocusRequester)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            supportingText = if (password.isNotEmpty() && password.length < 6) {
                { Text("Mínimo 6 caracteres") }
            } else null,
            isError = password.isNotEmpty() && password.length < 6,
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val desc = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = desc)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (formValid) onRegister(email, password, name, phone, selectedUserType)
                }
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester)
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Tipo de usuario",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            USER_TYPE_OPTIONS.forEach { (type, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected = selectedUserType == type,
                            onClick = { selectedUserType = type },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedUserType == type,
                        onClick = null // manejado por selectable
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        if (selectedUserType == UserType.RECYCLING_CENTER) {
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = centerName,
                onValueChange = { centerName = it },
                label = { Text("Nombre del centro") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            MaterialPriceSection(
                materialPrices = materialPrices,
                onMaterialPricesChange = { materialPrices = it }
            )
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = { onRegister(email, password, name, phone, selectedUserType) },
            enabled = !isLoading && formValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Crear cuenta", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialPriceSection(
    materialPrices: List<MaterialPrice>,
    onMaterialPricesChange: (List<MaterialPrice>) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Precios de materiales",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        materialPrices.forEachIndexed { index, price ->
            MaterialPriceItem(
                materialPrice = price,
                onMaterialPriceChange = { newPrice ->
                    val newList = materialPrices.toMutableList()
                    newList[index] = newPrice
                    onMaterialPricesChange(newList)
                },
                onRemove = {
                    val newList = materialPrices.toMutableList()
                    newList.removeAt(index)
                    onMaterialPricesChange(newList)
                }
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val newList = materialPrices.toMutableList()
                newList.add(MaterialPrice(MaterialType.PAPER, 0.0, MaterialUnit.KG))
                onMaterialPricesChange(newList)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar")
            Spacer(Modifier.size(8.dp))
            Text("Agregar material")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialPriceItem(
    materialPrice: MaterialPrice,
    onMaterialPriceChange: (MaterialPrice) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = materialPrice.materialType.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Material") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                MaterialType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            onMaterialPriceChange(materialPrice.copy(materialType = type))
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = materialPrice.pricePerUnit.toString(),
            onValueChange = { newValue ->
                val price = newValue.toDoubleOrNull() ?: 0.0
                onMaterialPriceChange(materialPrice.copy(pricePerUnit = price))
            },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
        )

        Column {
            listOf(MaterialUnit.KG, MaterialUnit.GR).forEach { unit ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectable(
                        selected = materialPrice.unit == unit,
                        onClick = { onMaterialPriceChange(materialPrice.copy(unit = unit)) },
                        role = Role.RadioButton
                    )
                ) {
                    RadioButton(
                        selected = materialPrice.unit == unit,
                        onClick = null
                    )
                    Text(unit.name, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
        }
    }
}

private fun String.isValidEmail(): Boolean = contains("@") && contains(".")
