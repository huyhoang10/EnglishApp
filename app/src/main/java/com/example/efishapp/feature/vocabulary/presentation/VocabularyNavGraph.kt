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
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class VocabularyRoute(val route: String) {
    data object VocabularyList : VocabularyRoute("vocabulary_list/{folderId}/{folderName}/{folderColor}") {
        fun createRoute(folderId: String, folderName: String, folderColor: Long) =
            "vocabulary_list/$folderId/${URLEncoder.encode(folderName, StandardCharsets.UTF_8)}/$folderColor"
    }

    data object VocabularyDetail : VocabularyRoute("vocabulary_detail/{vocabularyId}/{folderColor}") {
        fun createRoute(vocabularyId: String, folderColor: Long) =
            "vocabulary_detail/$vocabularyId/$folderColor"
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
            val folderName = backStackEntry.arguments?.getString("folderName")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            } ?: ""
            val folderColor = backStackEntry.arguments?.getLong("folderColor") ?: 0xFF4C58BA

            val viewModel = remember(folderId) {
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
                onNavigateBack = onNavigateBack,
                onVocabularyClick = { vocabulary ->
                    navController.navigate(VocabularyRoute.VocabularyDetail.createRoute(vocabulary.id, folderColor))
                },
                viewModel = viewModel
            )
        }

        composable(
            route = VocabularyRoute.VocabularyDetail.route,
            arguments = listOf(
                navArgument("vocabularyId") { type = NavType.StringType },
                navArgument("folderColor") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val vocabularyId = backStackEntry.arguments?.getString("vocabularyId") ?: ""
            val folderColor = backStackEntry.arguments?.getLong("folderColor") ?: 0xFF4C58BA

            val viewModel = remember {
                VocabularyDetailViewModel(
                    vocabularyId = vocabularyId,
                    vocabularyRepository = vocabularyRepository
                )
            }

            VocabularyDetailScreen(
                vocabularyId = vocabularyId,
                folderColor = folderColor,
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
