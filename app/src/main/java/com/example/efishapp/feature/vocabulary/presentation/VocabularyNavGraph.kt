package com.example.efishapp.feature.vocabulary.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository
import com.example.efishapp.feature.vocabulary.domain.usecase.CreateVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.DeleteVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.GetVocabulariesUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.UpdateVocabularyUseCase

sealed class VocabularyRoute(val route: String) {
    data object VocabularyList : VocabularyRoute("vocabulary_list/{folderId}/{folderName}/{folderColor}") {
        fun createRoute(folderId: String, folderName: String, folderColor: Long) =
            "vocabulary_list/$folderId/$folderName/$folderColor"
    }
}

@Composable
fun VocabularyNavGraph(
    vocabularyRepository: VocabularyRepository,
    onNavigateBack: () -> Unit,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = VocabularyRoute.VocabularyList.route
    ) {
        composable(
            route = VocabularyRoute.VocabularyList.route,
            arguments = listOf(
                navArgument("folderId") { type = NavType.StringType },
                navArgument("folderName") { type = NavType.StringType },
                navArgument("folderColor") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getString("folderId") ?: ""
            val folderName = backStackEntry.arguments?.getString("folderName") ?: ""
            val folderColor = backStackEntry.arguments?.getLong("folderColor") ?: 0xFF4C58BA

            val viewModel = remember(folderId) {
                VocabularyViewModel(
                    folderId = folderId,
                    getVocabulariesUseCase = GetVocabulariesUseCase(vocabularyRepository),
                    createVocabularyUseCase = CreateVocabularyUseCase(vocabularyRepository),
                    updateVocabularyUseCase = UpdateVocabularyUseCase(vocabularyRepository),
                    deleteVocabularyUseCase = DeleteVocabularyUseCase(vocabularyRepository)
                )
            }

            VocabularyScreen(
                folderName = folderName,
                folderColor = folderColor,
                onNavigateBack = onNavigateBack,
                viewModel = viewModel
            )
        }
    }
}
