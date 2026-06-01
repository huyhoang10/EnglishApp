package com.example.efishapp.feature.folder.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.efishapp.feature.folder.domain.repository.FolderRepository
import com.example.efishapp.feature.vocabulary.domain.repository.VocabularyRepository
import com.example.efishapp.feature.vocabulary.domain.usecase.CreateVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.DeleteVocabularyUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.GetVocabulariesUseCase
import com.example.efishapp.feature.vocabulary.domain.usecase.UpdateVocabularyUseCase
import com.example.efishapp.feature.vocabulary.presentation.VocabularyScreen
import com.example.efishapp.feature.vocabulary.presentation.VocabularyViewModel

sealed class FolderRoute(val route: String) {
    data object FolderList : FolderRoute("folder_list")
    data object VocabularyList : FolderRoute("vocabulary_list/{folderId}/{folderName}/{folderColor}") {
        fun createRoute(folderId: String, folderName: String, folderColor: Long) =
            "vocabulary_list/$folderId/$folderName/$folderColor"
    }
}

@Composable
fun FolderNavGraph(
    folderRepository: FolderRepository,
    vocabularyRepository: VocabularyRepository,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = FolderRoute.FolderList.route
    ) {
        composable(FolderRoute.FolderList.route) {
            FolderScreen(
                folderRepository = folderRepository,
                vocabularyRepository = vocabularyRepository,
                onFolderClick = { folder ->
                    navController.navigate(
                        FolderRoute.VocabularyList.createRoute(
                            folderId = folder.id,
                            folderName = folder.name,
                            folderColor = folder.colorHex
                        )
                    )
                }
            )
        }

        composable(
            route = FolderRoute.VocabularyList.route,
            arguments = listOf(
                navArgument("folderId") { type = NavType.StringType },
                navArgument("folderName") { type = NavType.StringType },
                navArgument("folderColor") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getString("folderId") ?: ""
            val folderName = backStackEntry.arguments?.getString("folderName") ?: ""
            val folderColor = backStackEntry.arguments?.getLong("folderColor") ?: 0xFF4C58BA

            val viewModel = remember {
                VocabularyViewModel(
                    folderId = folderId,
                    vocabularyRepository = vocabularyRepository,
                    getVocabulariesUseCase = GetVocabulariesUseCase(vocabularyRepository),
                    createVocabularyUseCase = CreateVocabularyUseCase(vocabularyRepository),
                    updateVocabularyUseCase = UpdateVocabularyUseCase(vocabularyRepository),
                    deleteVocabularyUseCase = DeleteVocabularyUseCase(vocabularyRepository)
                )
            }

            VocabularyScreen(
                folderName = folderName,
                folderColor = folderColor,
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
