# FlowerWorld

Android app in Kotlin + Jetpack Compose. Package: `com.example.flowerworld`.
Communication with the user: in Russian.
Code comments, KDoc, commit messages and this file: in English.

## App idea
A plant tracker. The user adds their own plants and the app reminds them when a plant needs watering.
- The user enters everything themselves: plants, names (species and a personal name), photos, watering period (set per plant by the user), fertilizers, growth-stage photos, notes, etc.
- Watering reminders are the core feature. They are **local notifications** (`AlarmManager` / `WorkManager`), not push: the schedule is known on the device, so FCM is not needed for them.
  - Honor/Huawei firmwares kill background work aggressively: use exact alarms (`setExactAndAllowWhileIdle`, `SCHEDULE_EXACT_ALARM` on Android 12+), reschedule on `BOOT_COMPLETED`, ask for `POST_NOTIFICATIONS` on Android 13+, and show a hint about battery optimization / autostart. Verify reminders on the test device early.
- Photos are stored as compressed files in app storage; the database keeps only the path/id. This keeps backup simple.
- Stage 1 (first): the app works fully offline; the only server feature is **backup/restore** of the user's data (accounts exist only for that).
- Stage 2 (much later): a social part. Users connect by direct invitation (QR code or direct link, i.e. App Links on our own domain) and can view each other's plants and care history. This needs per-user access rules between accounts.
- The backend is not chosen yet (see below). Candidates: Supabase (relational model + RLS fits the social stage), Firebase (cheapest for photos at small scale, has FCM), or self-hosted Supabase/PocketBase on a VPS later.

## Environment
- Android Studio + SDK: `C:\Users\Longs\AppData\Local\Android\Sdk` (set in `local.properties`, which is not tracked by git; a git worktree does not have it, so pass `ANDROID_HOME` instead).
- JDK: use the one bundled with Android Studio; set it before calling Gradle:
  `JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"`.
- Versions: AGP 9.5.0, Kotlin 2.2.10, Compose BOM 2026.02.01 (see `gradle/libs.versions.toml`).
- Git: branch `main`, remote `origin` = https://github.com/TheHumankind/FW.git.
  Run `git push` only when the user asks for it.

## Commands
```bash
./gradlew assembleDebug      # build
./gradlew installDebug       # build and install on the connected device
adb devices -l               # check the device
adb shell am start -n com.example.flowerworld/.MainActivity   # launch the app
```
`adb` lives in `<sdk>\platform-tools`; it is not on PATH.

## Test device
Huawei YAL-L21 (Nova 5T / Honor 20, Android 10), connected over USB, USB debugging enabled.
It **has Google services** (Play Services), so FCM and Google sign-in can be tested on it.
Do not assume every target user has them: newer Huawei/Honor devices may not.

## Architecture (agreements)
- MVVM + unidirectional data flow: Compose screen -> `ViewModel` with `StateFlow<UiState>` -> repository.
- Layers: `ui`, `data`, and `domain` when needed. Packages by feature (`feature/<name>/`).
- A single `app` module until there is a reason to split it.
- Theme, colors, fonts and spacing live in `ui/theme`; strings live in `strings.xml`.
- Dependency versions live in `libs.versions.toml`. Add libraries only when needed.

### Localization
- Languages: English (default, `res/values/`), Polish (`values-pl`), Belarusian (`values-be`), Russian (`values-ru`), Ukrainian (`values-uk`).
- All user-facing text goes through string resources (`stringResource`); no hardcoded text in code.
- A new string is added to all five `strings.xml` files at once; find the translations yourself, do not ask the user.
- Use `<plurals>` for quantities and positional `%1$s` for substitutions. A ViewModel never returns ready-made strings, only `@StringRes` or a state type.

### ViewModel and global state
- A screen that has state or logic gets its own ViewModel (one screen, one ViewModel, one `UiState`). A screen without logic (e.g. `WelcomeScreen`) does not need one.
- Small components (`AppButton`, `AppTextField`, `AppPasswordField`, etc.) are "dumb": state comes in as parameters, events go out as lambdas. They have no ViewModel.
- Global state does not live in a ViewModel or in global variables (`object` holding a `var` is forbidden). It lives in singleton repositories; screen ViewModels subscribe to them via `Flow`/`StateFlow`.
  - User settings (theme, language, etc.): `DataStore` + `SettingsRepository`; applied from the top: a root ViewModel passes the values to `FlowerWorldTheme`.
  - Current user and session: `SessionRepository` with `StateFlow<User?>`.
  - App data: Room as the single source of truth (local-first).
- Repositories are created once per app. While there are few screens, use a manual `AppContainer`; pick a DI library (Koin or Hilt) when the first real shared state appears, and only on the user's command.

### The server will come, but not right away
For now we work with templates and local data. The code must be ready for a server:
- Screens and ViewModels depend on a repository interface, not on a data source.
  First fake data, then Room, later a network implementation.
- Keep the domain model, the Room entity and the future network DTO separate, with mappers between them.
- Local-first: the UI reads only from the local database; the server later just syncs it.
- Entity ids are string UUIDs, not auto-increment.
- Tables get sync fields from the start (modification time, deleted flag).
- UI state must provide `Loading` / `Success` / `Error`.

### What we do NOT add without the user's command
Firebase, any server or networking, Google services. The backend choice is postponed until the local MVP
(Room, screens, reminders) works. Keep all server access behind repository interfaces (e.g. `SyncRepository`)
so any backend can be plugged in later by one new class; do not tie the code to Firestore-specific features.

## Checkpoints
- [x] Android Studio and SDK installed, `adb` works
- [x] Project created (Empty Activity, Compose), `assembleDebug` passes
- [x] App installed and launched on the phone
- [x] Git repository created, first commit, remote added
- [x] Feature branch `claude/android-app-first-feature-9ead0f` pushed to GitHub (not merged into `main` yet)
- [x] Theme (palette, font, light by default), localization (5 languages)
- [x] Components: `AppButton`, `AppTextButton`, `AppTextField`, `AppPasswordField`, `AppRoundButton`, `AppFooter`
- [x] Screens: welcome, login form, main (footer only); state-based navigation
- [x] User describes the app idea (plant tracker, see "App idea")
- [ ] Skeleton: fake repository, real navigation
- [ ] Room and local data
- [ ] Backend choice and server integration (later)
- [ ] Emulator system image (download was interrupted; optional, a phone is available)
