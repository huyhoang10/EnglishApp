package com.example.efishapp.feature.Auth.Presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Modifier,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
){
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(text = label) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = {
            if (isPassword && onPasswordToggle != null) {
                // Hãy đảm bảo bạn đã thêm các icon tương ứng vào thư mục drawable của core hoặc feature
                val icon = if (passwordVisible) {
                    painterResource(id = android.R.drawable.ic_menu_view) // Thay bằng icon mắt mở của bạn
                } else {
                    painterResource(id = android.R.drawable.ic_secure) // Thay bằng icon mắt đóng của bạn
                }

                IconButton(onClick = onPasswordToggle) {
                    Icon(painter = icon, contentDescription = "Toggle Password Visibility")
                }
            }
        }
    )
}