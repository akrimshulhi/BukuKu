package com.example.bukuku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bukuku.data.BukuDatabase
import com.example.bukuku.data.BukuRepository
import com.example.bukuku.data.SettingsRepository
import com.example.bukuku.ui.BukuViewModel
import com.example.bukuku.ui.BukuViewModelFactory
import com.example.bukuku.ui.DaftarScreen
import com.example.bukuku.ui.DetailScreen
import com.example.bukuku.ui.FormScreen
import com.example.bukuku.ui.theme.BukuKuTheme

/**
 * MainActivity: Activity tunggal (Single Activity Architecture) aplikasi BukuKu.
 * Bertanggung jawab menginisialisasi Database, Repository, ViewModel,
 * serta menyiapkan NavHost (Navigation Compose) untuk mengatur alur navigasi antar-layar.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inisialisasi Database Room (Singleton)
        val database = BukuDatabase.getDatabase(applicationContext)

        // 2. Inisialisasi Repository untuk Buku dan Settings (DataStore)
        val bukuRepository = BukuRepository(database.bukuDao())
        val settingsRepository = SettingsRepository(applicationContext)

        // 3. Siapkan ViewModelFactory untuk membuat BukuViewModel
        val viewModelFactory = BukuViewModelFactory(bukuRepository, settingsRepository)

        setContent {
            BukuKuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Dapatkan instance ViewModel menggunakan factory
                    val viewModel: BukuViewModel = viewModel(factory = viewModelFactory)

                    // Jalankan komponen navigasi utama
                    BukuKuNavHost(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * Pengatur rute navigasi aplikasi menggunakan Navigation Compose.
 * Mengelola perpindahan antara layar Daftar, Detail, dan Form (Tambah/Ubah).
 */
@Composable
fun BukuKuNavHost(
    viewModel: BukuViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "daftar",
        modifier = modifier
    ) {
        // Rute 1: Layar Daftar Buku (Halaman Utama)
        composable(route = "daftar") {
            DaftarScreen(
                viewModel = viewModel,
                onTambahClick = {
                    navController.navigate("form")
                },
                onBukuClick = { id ->
                    navController.navigate("detail/$id")
                }
            )
        }

        // Rute 2: Layar Detail Buku (Menerima argumen id bertipe Long)
        composable(
            route = "detail/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val bukuId = backStackEntry.arguments?.getLong("id") ?: 0L
            DetailScreen(
                bukuId = bukuId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditClick = { id ->
                    navController.navigate("form?id=$id")
                }
            )
        }

        // Rute 3: Layar Form Tambah / Ubah Buku
        // Menggunakan argumen opsional id dengan nilai default 0L
        composable(
            route = "form?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")?.takeIf { it > 0L }
            FormScreen(
                bukuId = id,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
