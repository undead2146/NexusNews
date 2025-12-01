# Report 01/12 Week 6 - Epic 1.6: Basic Navigation Complete

| Description | Commit Message |
|-------------|----------------|
| Connected MainActivity to the application navigation graph by adding @AndroidEntryPoint annotation for Hilt dependency injection, setting up NavController with rememberNavController(), and replacing the default greeting UI with nexusNewsNavGraph. Removed unused greeting composable and preview functions. This completes Epic 1.6 (Basic Navigation) from the roadmap, making the app fully functional by wiring together all existing components (NewsListScreen, NewsDetailScreen, ViewModels, Repository, API integration). | `feat(ui): connect MainActivity to navigation graph and complete Epic 1.6` |
| Fixed critical dependency compatibility issue by upgrading desugar_jdk_libs from version 1.1.5 to 2.1.5 in gradle/libs.versions.toml. This upgrade is required for Android 15 (compileSdk 35) compatibility to prevent potential runtime crashes when using java.time APIs. The lint error CoreLibDesugaringV1 identified this blocker issue. | `fix(deps): upgrade desugar_jdk_libs to 2.1.5 for Android 15 compatibility` |
