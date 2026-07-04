# AnimeWatch — приложение для просмотра аниме

Android-приложение на Kotlin + Jetpack Compose (Material 3) для просмотра аниме через
публичный API **AniLibria** (https://api.anilibria.tv/v3/).

## Технологии
- Kotlin, Jetpack Compose (Material 3), MVVM
- Retrofit 2.11.0 + Gson — сеть
- Room 2.6.1 — локальная БД (избранное, история просмотра)
- ExoPlayer / Media3 2.19.1 — видеоплеер (HLS)
- Coil 3.0.0 — загрузка и кеширование обложек
- Navigation Compose 2.7.7
- Kotlin Coroutines + StateFlow

## Минимальная версия Android
API 24 (Android 7.0)

## Как собрать проект

Проект не содержит бинарный `gradle-wrapper.jar` (бинарные файлы не включены в этот
пакет исходников). Чтобы собрать проект, выполните один из вариантов:

### Вариант 1 — Android Studio (рекомендуется)
1. Откройте папку `AnimeWatch` в Android Studio (Open an existing project).
2. Android Studio предложит сгенерировать `gradle-wrapper.jar` автоматически —
   согласитесь, либо выполните `gradle wrapper` из меню Gradle.
3. Дождитесь синхронизации Gradle и нажмите Run.

### Вариант 2 — из терминала (если у вас установлен Gradle локально)
```bash
cd AnimeWatch
gradle wrapper --gradle-version 8.7   # сгенерирует gradlew и gradle-wrapper.jar
./gradlew assembleDebug
```

APK появится в `app/build/outputs/apk/debug/`.

## Структура проекта

```
app/src/main/java/com/example/animewatch/
├── data/
│   ├── api/            # AniLibriaService, RetrofitClient, DTO, маппер DTO->Domain
│   ├── local/           # Room: AppDatabase, Entities, DAO, экспорт/импорт БД
│   └── repository/      # AnimeRepositoryImpl — единая точка доступа к данным
├── domain/
│   ├── models/          # Anime, Episode, WatchHistory, WatchStatus
│   └── repository/      # AnimeRepository — интерфейс репозитория
├── di/                  # Простой контейнер зависимостей (без Hilt)
├── util/                # ViewModelFactory
├── ui/
│   ├── theme/            # Color.kt, Theme.kt, Type.kt — тёмно-фиолетовая тема
│   ├── components/       # AnimeCard, AnimeCarousel
│   ├── navigation/        # NavGraph (маршруты, нижняя навигация)
│   └── screens/
│       ├── home/          # Главный экран (карусели)
│       ├── search/         # Поиск аниме
│       ├── detail/          # Экран деталей + список серий
│       ├── player/           # Экран плеера (ExoPlayer)
│       ├── favorites/         # Избранное
│       └── stats/              # Статистика + экспорт/импорт БД
├── AnimeApp.kt            # Application-класс (хранит DI-контейнер)
└── MainActivity.kt         # Единственная Activity (single-activity + Compose Navigation)
```

## Важные примечания по реализации

1. **API AniLibria и "озвучка".** Публичный API AniLibria v3 отдаёт один релиз с
   одной командой озвучки (поле `team.voice`) и тремя вариантами качества HLS-потока
   (`sd`/`hd`/`fhd`) для каждой серии. Поэтому в приложении "выбор озвучки" в плеере
   реализован как выбор качества потока (SD/HD/FHD) — это единственный вариант
   переключения видео-источника, который предоставляет сам API. Названия команд
   озвучки при этом отображаются на экране деталей аниме.

2. **Офлайн-режим.** Список избранного, история просмотра и статистика полностью
   локальны (Room) и работают без интернета. Обложки кешируются автоматически
   через Coil (дисковый кеш). Сам каталог аниме и видео требуют интернета, так как
   AniLibria не предоставляет офлайн-загрузку видео в публичном API.

3. **Экспорт/импорт БД.** Реализован через прямое копирование файла Room-базы
   данных (`animewatch.db`) в приватную директорию приложения
   (`getExternalFilesDir()/export`). Для отправки файла другому пользователю
   можно расширить функциональность через `FileProvider` (уже настроен в
   манифесте) и `Intent.ACTION_SEND`.

4. **Разрешения.** В `AndroidManifest.xml` заявлено только `INTERNET` — экспорт/импорт
   использует внутреннее хранилище приложения, поэтому `WRITE_EXTERNAL_STORAGE` не
   требуется.

5. **Иконка приложения.** Для простоты используется единый векторный drawable
   (`res/drawable/ic_launcher.xml`) вместо набора растровых mipmap-иконок.
