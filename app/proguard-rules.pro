# Правила ProGuard/R8 для релизной сборки

# Gson использует рефлексию для десериализации DTO — не обфусцируем их поля
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.example.animewatch.data.api.** { *; }

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keepattributes Exceptions

# Room
-keep class * extends androidx.room.RoomDatabase
