package com.example.efishapp.feature.folder.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.efishapp.feature.vocabulary.presentation.VocabularyNavGraph
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class FolderRoute(val route: String) {
    data object FolderList : FolderRoute("folder_list")
}

@Composable
fun FolderNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = FolderRoute.FolderList.route
    ) {
        composable(FolderRoute.FolderList.route) {
            FolderScreen(
                onNavigateToVocabulary = { folderId, folderName, folderColor ->
                    navController.navigate(
                        "vocabulary_list/" +
                                "${URLEncoder.encode(folderId, StandardCharsets.UTF_8)}/" +
                                "${URLEncoder.encode(folderName, StandardCharsets.UTF_8)}/" +
                                "$folderColor"
                    )
                }
            )
        }

        composable(
            route = "vocabulary_list/{folderId}/{folderName}/{folderColor}",
            arguments = listOf(
                navArgument("folderId") { type = NavType.StringType },
                navArgument("folderName") { type = NavType.StringType },
                navArgument("folderColor") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments
                ?.getString("folderId")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                .orEmpty()

            val folderName = backStackEntry.arguments
                ?.getString("folderName")
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                .orEmpty()

            val folderColor = backStackEntry.arguments?.getLong("folderColor") ?: 0xFF4C58BAL

            VocabularyNavGraph(
                folderId = folderId,
                folderName = folderName,
                folderColor = folderColor,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
