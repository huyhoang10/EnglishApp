package com.example.efishapp.feature.Auth.Presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White, // Google quy định nền trắng hoặc xanh dương đậm
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        // Logo Google - Bạn nhớ thêm ảnh logo vào thư mục drawable nhé
        // (Có thể dùng hệ thống drawable dùng chung của hệ thống ở nhánh <core>)
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_compass), // Thay bằng drawable logo Google của bạn
            contentDescription = "Google Logo",
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Tiếp tục với Google",
            fontSize = 16.sp,
            color = Color(0xFF1F1F1F) // Màu chữ xám đen chuẩn Google
        )
    }
}