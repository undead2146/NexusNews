---
title: User Experience Master Guide
description: The definitive manual for the NexusNews user experience - a comprehensive guide to every feature and interaction from the user's perspective.
category: report
lastUpdated: 2026-01-16
aiContext: true
mermaid: true
tags: [user-guide, ux, ai, offline-first, comprehensive]
---

# User Experience Master Guide

> [!IMPORTANT]
> This document is the **complete user manual** for NexusNews. Every feature, button, and interaction is explained here so you can get the most out of your news reading experience.

---

## 01. Getting Started

### 1.1 What is NexusNews?

NexusNews is an intelligent news reader app for Android that combines traditional news browsing with powerful AI-powered analysis features. It's designed to help you:

- **Stay Informed**: Browse the latest headlines from trusted sources
- **Read Smarter**: Use AI to summarize, analyze, and understand articles
- **Save for Later**: Bookmark articles for offline reading
- **Personalize**: Customize your reading experience with themes and preferences

### 1.2 First Launch Experience

When you first open NexusNews:

1. The app immediately begins loading the latest headlines
2. You'll see a brief loading indicator (typically 1-3 seconds on a good connection)
3. Once loaded, you can start browsing articles right away

**No account required!** NexusNews works immediately without any sign-up or login.

---

## 02. The Home Screen (News Feed)

The home screen is your central hub for discovering news. Here's what you'll see:

### 2.1 Screen Layout

| Element | Location | Purpose |
|:--------|:---------|:--------|
| **App Title** | Top left | Shows "NexusNews" |
| **Search Icon** | Top right | Opens search screen |
| **Category Chips** | Below title | Filter news by category |
| **Article Cards** | Main content area | Browse news articles |
| **Bottom Navigation** | Bottom of screen | Switch between main sections |

### 2.2 Category Filters

At the top of the news feed, you'll find category chips to filter headlines:

| Category | What You'll Find |
|:---------|:-----------------|
| **All** | All headlines, no filter |
| **General** | General news and current events |
| **Business** | Markets, economy, corporate news |
| **Entertainment** | Movies, music, celebrity news |
| **Health** | Medical, wellness, fitness |
| **Science** | Research, discoveries, technology |
| **Sports** | Scores, games, athlete news |
| **Technology** | Tech industry, gadgets, apps |

**To filter**: Simply tap any category chip. The selected category highlights and the feed updates automatically.

### 2.3 What Each Article Card Shows

Each article in the feed displays:

- **Thumbnail Image**: Visual preview of the article (if available)
- **Headline**: The article's main title
- **Source**: Which publication the article is from
- **Time**: How long ago the article was published
- **Preview Snippet**: First few lines of the article

### 2.4 Refreshing the Feed

**Pull down** on the article list to refresh and get the latest headlines. You'll see:

1. A refresh indicator appears at the top
2. New articles load in the background
3. The indicator disappears when complete

> [!TIP]
> **Your current articles stay visible** during refresh. You won't lose your place or see a blank screen.

---

## 03. Gestures and Quick Actions

NexusNews uses intuitive swipe gestures for quick actions without needing to open menus.

### 3.1 Swipe Actions on Article Cards

| Gesture | Action | Visual Feedback |
|:--------|:-------|:----------------|
| **Swipe Right** | Add to Bookmarks | Blue background with heart icon |
| **Swipe Right** (if bookmarked) | Remove from Bookmarks | Red background |
| **Swipe Left** (if bookmarked) | Toggle Favorite status | Purple background with star |

**How it works:**
1. Place your finger on an article card
2. Swipe in the desired direction
3. Release when you see the colored background
4. Feel a gentle vibration confirming the action

> [!NOTE]
> **Favorites are VIP Bookmarks**: You can only mark a bookmarked article as a Favorite. This helps you prioritize your most important saved articles.

### 3.2 Tapping Articles

- **Tap anywhere on the article card** to open the full article detail view
- **Tap the source name** to open the full article
- Each tap action provides immediate visual feedback

---

## 04. Reading Articles (Detail View)

When you tap an article, you enter the full reading experience.

### 4.1 What You'll See

The article detail screen shows content in this order:

1. **Back Button**: Returns you to the previous screen
2. **Hero Image**: Large image at the top (if available)
3. **Headline**: The full article title
4. **Author & Date**: Who wrote it and when
5. **AI Summary Button**: Generate a quick summary
6. **Full Article Content**: The complete article text. NB: NexusNews automatically attempts to fetch the full text from the web if the API only provides a preview.
7. **Read Full Article Button**: Opens the original webpage in your browser for the full original experience.

### 4.2 AI Summary Feature

The standout feature on the detail screen is the **AI Summary** button:

**To generate a summary:**
1. Tap the "Generate AI Summary" button
2. Wait briefly while the AI analyzes the article
3. A summary card appears showing the key points

**Summary benefits:**
- Understand the main points in seconds
- Decide if you want to read the full article
- Summaries are saved automatically for instant access later

> [!TIP]
> **Summaries are cached**: If you've generated a summary before, it appears instantly when you return to the article—no waiting, no using additional AI credits.

### 4.3 Current AI Features

**Currently Available:**

| Feature | What It Does | How to Use |
|:--------|:-------------|:-----------|
| **AI Summary** | Compresses the article to ~150 characters of key facts | Tap "AI Summary" in the context menu (three-dot icon) on article detail screen |
| **Sentiment Analysis** | Analyzes the emotional tone of the article (Positive/Neutral/Negative) | Tap "Sentiment Analysis" in the context menu on article detail screen |
| **Key Points** | Extracts 3-5 main points with importance scores | Tap "Key Points" in the context menu on article detail screen |
| **Entities** | Identifies people, places, and organizations mentioned | Tap "Entities" in the context menu on article detail screen |
| **Topics** | Classifies article topics and subtopics | Tap "Topics" in the context menu on article detail screen |
| **Bias Analysis** | Detects bias level and credibility indicators | Tap "Bias Analysis" in the context menu on article detail screen |
| **Deep AI Analysis** | Runs all analyses at once for comprehensive insights | Tap "Deep AI Analysis" in the context menu on article detail screen |
| **Chat Assistant** | Conversational AI for discussing articles | Navigate to `chat` route (available via navigation) |
| **Recommendations** | Personalized article suggestions based on your interests | Navigate to `recommendations` route (available via navigation) |

> [!TIP]
> **Quick Analysis**: For the most comprehensive understanding of an article, use "Deep AI Analysis" which runs all 5 individual analyses (Sentiment, Key Points, Entities, Topics, and Bias) in parallel and displays results progressively as they complete.

> [!NOTE]
> **Coming Soon**: Two additional AI features (Translation and Content Generation) are fully implemented in the backend and will be exposed in the UI in future updates.

---

## 05. Bookmarks and Favorites

Your personal reading list is always accessible and never leaves your device.

### 5.1 Accessing Bookmarks

Tap the **Bookmarks** icon in the bottom navigation bar to see all your saved articles.

### 5.2 Bookmark Organization

| Filter | What It Shows |
|:-------|:--------------|
| **All** | Every bookmarked article |
| **Favorites** | Only articles marked as favorites |

### 5.3 Managing Bookmarks

**In the Bookmarks screen:**
- **Tap an article** to read it
- **Swipe right** to remove from bookmarks
- **Swipe left** to toggle favorite status

> [!NOTE]
> **Offline Reading**: Bookmarked articles are stored on your device. You can read them anytime, even without an internet connection.

---

## 06. Search

Find specific articles or topics with the search feature.

### 6.1 Opening Search

Tap the **Search** icon at the top of the home screen or in the bottom navigation.

### 6.2 Search Features

| Feature | Description |
|:--------|:------------|
| **Text Search** | Type keywords to find matching articles |
| **Recent Searches** | Your search history for quick re-access |
| **Clear History** | Remove your search history |

### 6.3 Search Results

Results display as article cards, just like the home feed. Tap any result to open the full article.

---

## 07. Settings

Customize NexusNews to your preferences.

### 7.1 Accessing Settings

Tap the **Settings** icon in the bottom navigation bar.

### 7.2 Appearance Settings

**Theme Options:**

| Theme | Behavior |
|:------|:---------|
| **Light** | Always uses light colors |
| **Dark** | Always uses dark colors |
| **System** | Follows your device's theme setting |

### 7.3 Notification Settings

Control when and how NexusNews notifies you:

| Setting | Description | Default |
|:--------|:------------|:--------|
| **Breaking News** | Alerts for major stories | On |
| **Daily Digest** | Summary at your chosen time | Off |
| **Digest Time** | When to receive daily digest | 8:00 AM |
| **Sound** | Play notification sounds | On |
| **Vibration** | Vibrate for notifications | On |

### 7.4 Storage Settings

Manage your cached data:

| Information | Description |
|:------------|:------------|
| **Total Cache** | How much storage articles are using |
| **Cached Articles** | Number of articles stored locally |
| **Clear Cache** | Delete all cached articles |

> [!WARNING]
> **Clearing cache removes saved articles** (except bookmarks). You'll need to re-download them next time.

### 7.5 AI Settings

Configure the AI analysis features:

**API Key Setup:**
1. Enter your OpenRouter API key in the text field
2. Tap "Save Key" to store it securely
3. Use "Test Connection" to verify it works

**Model Selection:**
Choose which AI model to use for analysis. Recommended models are highlighted.

> [!TIP]
> **Free tier available**: OpenRouter offers 50 free requests per day—enough for casual usage!

### 7.6 About Section

View app information including version number and build type.

---

## 08. Data and Privacy

Your privacy is protected by design.

### 8.1 What Stays on Your Device

| Data Type | Stored Where | Shared Externally? |
|:----------|:-------------|:-------------------|
| Cached Articles | Your device | ❌ Never |
| Bookmarks | Your device | ❌ Never |
| AI Summaries | Your device | ❌ Never |
| Reading History | Your device | ❌ Never |
| API Key | Your device (encrypted) | ❌ Never |
| Preferences | Your device | ❌ Never |

### 8.2 When Data Is Sent Externally

| Action | What's Sent | Where |
|:-------|:------------|:------|
| Fetching headlines | Your country/category filter | NewsAPI.org |
| AI Analysis | Article text (for analysis) | OpenRouter AI |

> [!NOTE]
> **API Key Security**: Your OpenRouter API key is encrypted using military-grade AES-256 encryption and stored in Android's secure keystore. Even if someone gained access to your device, they couldn't extract the key.

---

## 09. Troubleshooting

Common issues and how to resolve them.

### 9.1 Error Messages Explained

| Message | Meaning | Solution |
|:--------|:--------|:---------|
| "No internet connection" | Device is offline | Connect to WiFi or mobile data |
| "Connection timeout" | Server took too long | Try again later |
| "Network error" | General connection problem | Check your internet |
| "Unauthorized" | Invalid API key | Re-enter your API key in Settings |
| "Too many requests" | Rate limit reached | Wait a few minutes |
| "Server error" | Problem with the news service | Try again later |

### 9.2 App Automatically Retries

When something fails, NexusNews automatically tries again:

- **Attempt 1**: Waits 1 second
- **Attempt 2**: Waits 2 seconds
- **Attempt 3**: Waits 4 seconds
- **Then**: Shows you the error message

Most temporary problems resolve themselves during these retries.

### 9.3 When AI Features Don't Work

1. **Check your API key**: Go to Settings > AI Features and verify your key
2. **Test connection**: Use the "Test Connection" button
3. **Check rate limits**: Free tier has 50 requests/day
4. **Try a different model**: Some models may be temporarily unavailable

---

## 10. Tips for Power Users

### 10.1 Keyboard-Free Reading

| Gesture | Location | Effect |
|:--------|:---------|:-------|
| Pull down | News feed | Refresh articles |
| Swipe right | Article card | Toggle bookmark |
| Swipe left | Article card (bookmarked) | Toggle favorite |
| Tap | Article card | Open full article |
| Back gesture | Detail view | Return to list |

### 10.2 Maximizing AI Features

**Save tokens by:**
- Using cached summaries (instant and free)
- Choosing the right tool for your need
- Reading the summary before generating more analysis

### 10.3 Offline Reading

**Best practices:**
- Bookmark articles you want to read later
- Open articles once while online to cache them
- Your entire bookmark collection works offline

### 10.4 Data Persistence

| Data | Survives App Restart? | Survives Reinstall? |
|:-----|:---------------------|:--------------------|
| Cached articles | ✅ Yes | ❌ No |
| Bookmarks | ✅ Yes | ❌ No |
| AI Summaries | ✅ Yes | ❌ No |
| Theme setting | ✅ Yes | ❌ No |
| API Key | ✅ Yes | ❌ No |

---

## 11. Accessibility Features

NexusNews is designed to be accessible to all users.

### 11.1 Available Options

| Feature | Range/Options | How to Access |
|:--------|:--------------|:--------------|
| **Font Size** | 80% to 200% | Settings > Accessibility |
| **High Contrast** | On/Off | Settings > Accessibility |
| **Reduce Animations** | On/Off | Settings > Accessibility |

### 11.2 Screen Reader Support

All buttons and interactive elements have proper labels for screen readers. The app follows Android accessibility best practices.

---

## 12. Quick Reference Card

### Navigation

| Button | Location | Takes You To |
|:-------|:---------|:-------------|
| 🏠 **Home** | Bottom bar | News feed |
| 🔍 **Search** | Bottom bar | Search screen |
| 🔖 **Bookmarks** | Bottom bar | Saved articles |
| ⚙️ **Settings** | Bottom bar | App settings |

### Gestures

| Gesture | On What | Result |
|:--------|:--------|:-------|
| **Pull Down** | Article list | Refresh news |
| **Swipe Right** | Article card | Bookmark/Unbookmark |
| **Swipe Left** | Bookmarked card | Toggle favorite |
| **Tap** | Article card | Open article |

### AI Features Summary

All AI features are accessible via the context menu (three-dot icon) on any article detail screen:

| Feature | Input | Output | Status |
|:--------|:------|:-------|:--------|
| **Summary** | Full article | ~150 character summary | ✅ Available |
| **Sentiment** | Full article | Positive/Neutral/Negative with explanation | ✅ Available |
| **Key Points** | Full article | 3-5 bullet points with importance scores | ✅ Available |
| **Topics** | Full article + title | Primary/secondary topics and subtopics | ✅ Available |
| **Entities** | Full article | People, Places, Organizations with confidence | ✅ Available |
| **Bias** | Full article + title | Low/Medium/High with credibility indicators | ✅ Available |
| **Deep Analysis** | Full article + title | All above analyses at once | ✅ Available |
| **Chat** | Your question + article | Contextual answer | ✅ Available |
| **Recommendations** | Reading history | Personalized article suggestions | ✅ Available |
| **Translation** | Full article | Article in target language | ⏳ Coming Soon |
| **Content Generation** | Article | Social posts, captions, notes | ⏳ Coming Soon |
