package com.example.efishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.efishapp.core.designsystem.EfishAppTheme
import com.example.efishapp.navigation.EfishNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.efishapp.feature.profile.data.repository.UserProfileRepositoryImpl
import com.example.efishapp.feature.profile.domain.usecase.GetProfileUseCase
import com.example.efishapp.feature.profile.domain.usecase.UpdateProfileUseCase

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            EfishAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EfishNavGraph(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
