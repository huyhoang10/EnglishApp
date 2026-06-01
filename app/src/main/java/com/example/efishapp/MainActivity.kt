package com.example.efishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.efishapp.feature.Auth.Presentation.AuthNavGraph
import com.example.efishapp.feature.Auth.Presentation.AuthViewModel
import com.example.efishapp.feature.folder.presentation.FolderNavGraph
import com.example.efishapp.feature.onboarding.presentation.OnboardingScreen
import com.example.efishapp.ui.theme.EfishAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EfishAppTheme {
                var showOnboarding by remember { mutableStateOf(true) }
                var showAuth by remember { mutableStateOf(false) }
                var showFolder by remember { mutableStateOf(false) }

                when {
                    showOnboarding -> {
                        OnboardingScreen(
                            onNavigateToAuth = {
                                showOnboarding = false
                                showAuth = false
                                showFolder = true
                            }
                        )
                    }
                    showAuth -> {
                        AuthNavGraph(
                            viewModel = authViewModel,
                            onAuthSuccess = {
                                showAuth = false
                                showFolder = true
                            }
                        )
                    }
                    showFolder -> {
                        FolderNavGraph()
                    }
                }
            }
        }
    }
}
