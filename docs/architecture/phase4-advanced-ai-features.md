# Phase 4: Advanced AI Features

## Overview

Phase 4 extends the AI capabilities introduced in Phase 3 with advanced article analysis, smart recommendations, AI chat assistant, and content generation features. These features provide users with deeper insights into news content and more personalized experiences.

## Architecture

### Domain Layer

#### AI Models

Located in [`domain/ai/`](../../app/src/main/java/com/example/nexusnews/domain/ai/):

- **[`AiService.kt`](../../app/src/main/java/com/example/nexusnews/domain/ai/AiService.kt)**: Core interface defining all AI operations
- **[`FreeAiModel.kt`](../../app/src/main/java/com/example/nexusnews/domain/ai/FreeAiModel.kt)**: Configuration for available AI models
- **[`AnalysisModels.kt`](../../app/src/main/java/com/example/nexusnews/domain/ai/AnalysisModels.kt)**: Domain models for analysis results

#### Analysis Result Models

- **`KeyPoint`**: Represents a key point extracted from an article with importance and position
- **`KeyPointsResult`**: Container for key points with summary
- **`RecognizedEntity`**: Represents an identified entity (person, organization, location, etc.)
- **`EntityRecognitionResult`**: Container for all recognized entities
- **`TopicClassification`**: Represents a topic with confidence and subtopics
- **`TopicClassificationResult`**: Container for primary and secondary topics
- **`BiasAnalysis`**: Represents bias level, type, explanation, and examples
- **`BiasDetectionResult`**: Container for bias analysis with objectivity score
- **`UserInterest`**: Represents a user interest with score and timestamp
- **`ArticleRecommendation`**: Represents a recommended article with relevance score
- **`RecommendationResult`**: Container for recommendations and user profile
- **`ChatMessage`**: Represents a message in AI chat conversation
- **`ChatResponse`**: Container for AI response with suggested questions
- **`ContentGenerationResult`**: Container for generated content with variations

### Data Layer

#### AI Service Implementation

Located in [`data/ai/OpenRouterAiService.kt`](../../app/src/main/java/com/example/nexusnews/data/ai/OpenRouterAiService.kt):

Implements all Phase 4 AI operations using OpenRouter API with model fallback logic:

- **`extractKeyPoints()`**: Extracts key points from articles
- **`recognizeEntities()`**: Identifies entities (people, organizations, locations, dates, events, products)
- **`classifyTopic()`**: Classifies article topics with confidence scores
- **`detectBias()`**: Analyzes articles for bias with objectivity scoring
- **`generateRecommendations()`**: Generates personalized article recommendations
- **`chatWithAssistant()`**: Enables AI-powered chat conversations
- **`generateContent()`**: Generates various types of content (headlines, captions, tags, notes)

#### DataStore for AI Preferences

Located in [`data/local/datastore/AiFeaturePreferencesDataStore.kt`](../../app/src/main/java/com/example/nexusnews/data/local/datastore/AiFeaturePreferencesDataStore.kt):

Manages AI feature settings:

- Feature toggles for each AI capability
- Quality level settings (Low/Medium/High)
- Token limit configurations
- Behavior customization (auto-generate, indicators, caching, notifications)

### Presentation Layer

#### UI Components

Located in [`ui/components/ArticleAnalysisComponents.kt`](../../app/src/main/java/com/example/nexusnews/ui/components/ArticleAnalysisComponents.kt):

- **`KeyPointsSection`**: Displays extracted key points with importance indicators
- **`EntityRecognitionSection`**: Shows recognized entities grouped by type
- **`TopicClassificationSection`**: Displays topic classification with confidence scores
- **`BiasDetectionSection`**: Shows bias analysis with objectivity score and credibility indicators

#### Screens

Located in [`presentation/screens/chat/ChatAssistantScreen.kt`](../../app/src/main/java/com/example/nexusnews/presentation/screens/chat/ChatAssistantScreen.kt):

- **`ChatAssistantScreen`**: Full-featured AI chat interface with:
  - Message history display
  - Suggested questions
  - Real-time input
  - Loading states
  - Error handling

Located in [`presentation/screens/recommendations/RecommendationsScreen.kt`](../../app/src/main/java/com/example/nexusnews/presentation/screens/recommendations/RecommendationsScreen.kt):

- **`RecommendationsScreen`**: Personalized recommendations display with:
  - User interests visualization
  - Relevance scores
  - Match reasons
  - Bookmark functionality
  - Load more capability

## Features

### Epic 4.1: AI-Powered Article Analysis

#### Key Points Extraction

Extracts the most important points from articles with:
- Importance scoring (0.0 - 1.0)
- Position tracking within article
- Concise summaries

**Prompt Engineering**: Uses structured JSON prompts to extract 5 key points with importance scores and positions.

**Response Parsing**: Uses Moshi to parse JSON responses into `KeyPointsResult` domain models.

#### Entity Recognition

Identifies and categorizes entities:
- **Person**: People mentioned in the article
- **Organization**: Companies, institutions, groups
- **Location**: Places, cities, countries
- **Date**: Temporal references
- **Event**: Named events
- **Product**: Products or services mentioned

Each entity includes:
- Confidence score (0.0 - 1.0)
- List of mention positions

#### Topic Classification

Categorizes articles with:
- Primary topic with confidence score
- Secondary topics
- Subtopics for detailed classification
- All topics as tags

Common topics include: Politics, Technology, Business, Health, Sports, Entertainment, Science, Environment, World News, Crime, Education.

#### Bias Detection

Analyzes articles for potential bias:
- **Bias Level**: LOW, MEDIUM, HIGH, UNKNOWN
- **Bias Type**: Political, Corporate, Confirmation, Other
- **Objectivity Score**: 0.0 - 1.0 (1.0 = completely objective)
- **Credibility Indicators**: Positive indicators like multiple sources, factual claims, balanced perspective
- **Examples**: Specific examples of bias found

### Epic 4.2: Smart Recommendations

Generates personalized article recommendations based on:
- User interests with relevance scores
- Available article metadata
- Matched interests for each recommendation

Each recommendation includes:
- Article ID
- Relevance score (0.0 - 1.0)
- Explanation of why it was recommended
- List of matched interests

### Epic 4.3: AI Chat Assistant

Provides conversational AI interface for:
- Asking questions about articles
- Explaining news topics
- Providing context and background
- Multi-turn conversations

Features:
- Conversation history (last 5 messages)
- Suggested follow-up questions
- Article context awareness
- Real-time responses

### Epic 4.4: Content Generation

Generates various types of content from articles:

#### Headlines
- 3 engaging headline variations
- Catchy and accurate
- Under 80 characters

#### Social Media Captions
- 3 caption variations
- Includes relevant hashtags
- Platform-optimized length

#### Tags
- 5-10 relevant tags
- Multiple variations
- SEO-friendly

#### Reading Notes
- Key takeaways
- Important facts
- Questions to consider
- Study-friendly format

#### Custom Queries
- User-defined prompts
- Flexible content generation
- JSON-structured responses

### Epic 4.5: AI Settings & Customization

#### Feature Toggles

Enable/disable individual AI features:
- Summarization
- Sentiment Analysis
- Key Points Extraction
- Entity Recognition
- Topic Classification
- Bias Detection
- Chat Assistant
- Recommendations
- Content Generation

#### Quality Settings

- **Quality Level**: Low (faster), Medium (balanced), High (more detailed)
- **Token Limits**: Configurable max tokens for different operation types

#### Behavior Customization

- **Auto-generate summaries**: Automatically generate on article load
- **Show AI indicators**: Display AI-generated content badges
- **Cache AI responses**: Store responses to avoid regeneration
- **AI Notifications**: Enable notifications for AI features

## Usage Tracking

All AI operations are tracked in the database via [`AiUsageEntity`](../../app/src/main/java/com/example/nexusnews/data/local/entity/AiUsageEntity.kt):

Tracked metrics:
- Request type (SUMMARIZATION, KEY_POINTS_EXTRACTION, ENTITY_RECOGNITION, etc.)
- Model used
- Prompt tokens
- Completion tokens
- Total tokens
- Timestamp

## Error Handling

### Model Fallback

Primary model failures automatically trigger fallback to secondary models:
1. Llama 3.3 70B (default)
2. Gemma 2 27B
3. Mistral Small 3.1
4. Gemma 2 9B
5. Llama 3.1 8B

### Response Validation

All JSON responses are validated before parsing:
- Type-safe parsing with Moshi
- Graceful error handling
- Default values for missing fields
- Logging for debugging

## Future Enhancements

### Planned Improvements

1. **Offline Support**: Cache AI responses for offline access
2. **Multi-language Support**: Extend analysis to more languages
3. **Advanced Bias Detection**: Fine-grained bias type classification
4. **User Feedback**: Allow users to rate AI responses
5. **Context Awareness**: Better article context in chat
6. **Recommendation Learning**: Improve recommendations based on user behavior

### Technical Debt

1. Add unit tests for response parsers
2. Implement proper error recovery strategies
3. Add performance monitoring for AI operations
4. Optimize prompt templates for better results
5. Implement rate limiting for API calls

## Testing

### Unit Tests Needed

- Response parser tests for each AI feature
- DataStore preference tests
- ViewModel state management tests
- Error handling tests

### Integration Tests Needed

- End-to-end AI service tests
- UI component tests
- Navigation flow tests

## Performance Considerations

### API Usage

- Free tier limits: ~50 requests/day
- Token limits vary by model
- Implement request queuing for high usage

### Caching Strategy

- Cache AI responses by article ID and request type
- Cache expiration: 24 hours
- Manual cache clear option in settings

## Security

### API Key Management

- Encrypted storage using EncryptedSharedPreferences
- Secure transmission via HTTPS
- Key rotation support
- Connection test validation

### Data Privacy

- User interests stored locally
- Chat history not persisted (optional)
- No personal data sent to AI beyond article content

## References

- [OpenRouter API Documentation](https://openrouter.ai/docs)
- [Project Roadmap](../project/roadmap.md)
- [Phase 3 Documentation](./ai-integration.md)
