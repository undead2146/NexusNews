# Report 17/11 Week 4 - Code Review Fixes & Testing Improvements

| Description | Commit Message |
|-------------|----------------|
| Implemented secure API key injection by adding NEWS_API_KEY to BuildConfig from local.properties and configuring AuthInterceptor to inject it as query parameter. | `fix(security): implement API key injection via BuildConfig and AuthInterceptor` |
| Simplified NewsRepositoryImpl by removing incomplete network-bound resource pattern and implementing direct Flow&lt;Result&lt;List&lt;Article&gt;&gt;&gt;&gt; emission until Room caching is ready. | `refactor(repo): simplify NewsRepositoryImpl to direct network calls` |
| Implemented logging infrastructure by initializing Timber DebugTree in NexusNewsApplication for debug builds. | `feat(logging): initialize Timber logging in NexusNewsApplication` |
| Added network security configuration to enforce HTTPS for NewsAPI domain with cleartext traffic permitted. | `feat(security): add network security config for HTTPS enforcement` |
| Fixed RetryInterceptorTest by updating mock setup to use proper argument matchers (any(), eq()) instead of exact instance matching for exception messages. | `fix(test): update RetryInterceptorTest to use proper argument matchers` |
| Removed incompatible NetworkIntegrationTest.kt that was using non-existent classes and causing build failures. | `fix(test): remove incompatible NetworkIntegrationTest.kt` |
| Updated gradle/libs.versions.toml with additional testing dependencies for Hilt and MockWebServer. | `feat(deps): add Hilt testing and MockWebServer dependencies` |
| Minor updates to ErrorInterceptor, RetryInterceptor, and RetryPolicy for consistency and improved error handling. | `refactor: minor updates to interceptors and retry policy` |
