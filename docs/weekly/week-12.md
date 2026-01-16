# Report 13/01 - 19/01 Week 12 - Stabilization & Debugging

## Overview

Week 12 focuses on stabilizing the application, resolving persistent network issues, and ensuring robust error handling across the app.

## Debugging: Network & 404 Fixes

| Description | Commit Message | Files |
|-------------|----------------|-------|
| Fixed HTTP 404 by removing empty `apiKey` query param in favor of headers. | `fix(api): remove empty apiKey query param to fix 404` | `NewsApiService.kt`, `NewsRemoteDataSource.kt` |
| Updated unit tests for remote data source. | `test(data): update NewsRemoteDataSourceTest for API changes` | `NewsRemoteDataSourceTest.kt` |
| Added detailed API key logging to debug persistent 404s. | `feat(debug): add AuthInterceptor logging` | `AuthInterceptor.kt` |
| DISABLED network security config temporarily to investigate SecurityException/EPERM. | `fix(config): disable network security config for debugging` | `AndroidManifest.xml` |

---
*End of Report*
