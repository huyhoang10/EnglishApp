package com.example.efishapp.feature.vocabulary.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class VocabularyRoute(val route: String) {
    data object VocabularyList : VocabularyRoute("vocabulary_list/{folderId}/{folderName}/{folderColor}") {
        fun createRoute(folderId: String, folderName: String, folderColor: Long): String =
            "vocabulary_list/${URLEncoder.encode(folderId, StandardCharsets.UTF_8)}/${URLEncoder.encode(folderName, StandardCharsets.UTF_8)}/$folderColor"
    }

    data object VocabularyDetail : VocabularyRoute("vocabulary_detail/{vocabularyId}/{folderColor}") {
        fun createRoute(vocabularyId: String, folderColor: Long): String =
            "vocabulary_detail/$vocabularyId/$folderColor"
    }
}

@Composable
fun VocabularyNavGraph(
    folderId: String,
    folderName: String,
    folderColor: Long,
    onNavigateBack: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = VocabularyRoute.VocabularyList.createRoute(folderId, folderName, folderColor)
    ) {
        composable(
            route = VocabularyRoute.VocabularyList.route,
            arguments = listOf(
                navArgument("folderId") { type = NavType.StringType },
                navArgument("folderName") { type = NavType.StringType },
                navArgument("folderColor") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val actualFolderName = backStackEntry.arguments
                ?.getString("folderName")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                ?: folderName

            val actualFolderColor = backStackEntry.arguments?.getLong("folderColor") ?: folderColor

            VocabularyScreen(
                folderName = actualFolderName,
                folderColor = actualFolderColor,
                onNavigateBack = onNavigateBack,
                onNavigateToDetail = { vocabularyId ->
                    navController.navigate(VocabularyRoute.VocabularyDetail.createRoute(vocabularyId, actualFolderColor))
                }
            )
        }

        composable(
            route = VocabularyRoute.VocabularyDetail.route,
            arguments = listOf(
                navArgument("vocabularyId") { type = NavType.StringType },
                navArgument("folderColor") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val folderColorArg = backStackEntry.arguments?.getLong("folderColor") ?: 0xFF4C58BA

            VocabularyDetailScreen(
                folderColor = folderColorArg,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
