package com.introduce.hotel.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.introduce.hotel.model.HotelEntity

@Database(entities = [HotelEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun hotelDao(): HotelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 3 to 4
        val migration3to4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE HotelEntity ADD COLUMN new_column_name TEXT DEFAULT ''")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hotel_database"
                ).addMigrations(migration3to4) // Add migration from version 3 to 4
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
