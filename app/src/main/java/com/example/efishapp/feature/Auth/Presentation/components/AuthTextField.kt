package com.example.efishapp.feature.Auth.Presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
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
                val iconImageVector = if (passwordVisible) {
                    Icons.Default.Visibility // Icon mắt mở
                } else {
                    Icons.Default.VisibilityOff // Icon mắt nhắm (có đường gạch chéo)
                }

                IconButton(onClick = onPasswordToggle) {
                    Icon(imageVector = iconImageVector, contentDescription = "Ẩn/Hiện mật khẩu")
                }
            }
        }
    )
}