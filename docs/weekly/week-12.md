# Report 13/01 - 19/01 Week 12 - Stabilization & Debugging

## Overview

Week 12 focuses on stabilizing the application, resolving persistent network issues, and ensuring robust error handling across the app.

## Debugging: Network & 404 Fixes

| Description | Commit Message | Files |
|-------------|----------------|-------|
| Fixed HTTP 404 by removing empty `apiKey` query param in favor of headers. | `fix(api): remove empty apiKey query param to fix 404` | `NewsApiService.kt`, `NewsRemoteDataSource.kt`, `NewsRemoteDataSourceTest.kt` |
| Added detailed API key logging to debug persistent 404s. | `feat(debug): add AuthInterceptor logging` | `AuthInterceptor.kt` |
| DISABLED network security config temporarily to investigate SecurityException/EPERM. | `fix(config): disable network security config for debugging` | `AndroidManifest.xml` |
| **RESOLVED**: Network requests now working (HTTP 200). Fixed Room database schema mismatch. | `fix(db): increment database version to resolve schema mismatch` | `DatabaseConstants.kt` |

## UI Improvements

| Description | Commit Message | Files |
|-------------|----------------|-------|
| Removed debug metadata banner from AI summary cards. | `fix(ui): remove debug metadata from SummaryCard` | `SummaryCard.kt` |
| Added "Read Full Article" button since NewsAPI only provides previews. | `feat(ui): add Read Full Article button to detail screen` | `NewsDetailScreen.kt` |

---
*End of Report*
