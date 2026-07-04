package com.example.animewatch.data.local

import android.content.Context
import java.io.File

/**
 * Утилита для экспорта/импорта файла базы данных Room.
 * Экспорт — простое копирование .db файла в выбранную пользователем директорию (например, через SAF/Downloads).
 * Импорт — замена текущего файла базы данных выбранным пользователем файлом с последующим переоткрытием базы.
 */
object DatabaseExportImport {

    /** Возвращает файл текущей базы данных приложения */
    private fun currentDbFile(context: Context): File =
        context.getDatabasePath(AppDatabase.DATABASE_NAME)

    /**
     * Экспортирует базу данных в указанный путь (destinationPath — абсолютный путь к файлу назначения).
     * Перед экспортом закрываем БД, чтобы не скопировать файл в промежуточном состоянии.
     */
    fun export(context: Context, destinationPath: String): Result<Unit> {
        return try {
            AppDatabase.closeInstance()
            val source = currentDbFile(context)
            if (!source.exists()) return Result.failure(IllegalStateException("Файл базы данных не найден"))
            val destination = File(destinationPath)
            destination.parentFile?.mkdirs()
            source.copyTo(destination, overwrite = true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            // Переоткрываем БД сразу после операции, чтобы приложение продолжило работать
            AppDatabase.getInstance(context)
        }
    }

    /**
     * Импортирует базу данных из указанного файла, заменяя текущую.
     * После импорта база переоткрывается автоматически при следующем обращении.
     */
    fun import(context: Context, sourcePath: String): Result<Unit> {
        return try {
            AppDatabase.closeInstance()
            val source = File(sourcePath)
            if (!source.exists()) return Result.failure(IllegalStateException("Файл для импорта не найден"))
            val destination = currentDbFile(context)
            destination.parentFile?.mkdirs()
            source.copyTo(destination, overwrite = true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            AppDatabase.getInstance(context)
        }
    }
}
