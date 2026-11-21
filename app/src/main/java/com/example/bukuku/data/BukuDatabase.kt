package com.example.bukuku.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database Room utama untuk aplikasi BukuKu.
 * Menggunakan pola Singleton agar hanya ada satu instance database yang aktif.
 */
@Database(entities = [Buku::class], version = 1, exportSchema = false)
abstract class BukuDatabase : RoomDatabase() {

    // Fungsi abstrak untuk mendapatkan instance BukuDao
    abstract fun bukuDao(): BukuDao

    companion object {
        @Volatile
        private var INSTANCE: BukuDatabase? = null

        // Mengambil atau membuat instance database secara aman (thread-safe)
        fun getDatabase(context: Context): BukuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BukuDatabase::class.java,
                    "buku_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
