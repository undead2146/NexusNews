# NexusNews Documentation Structure

This directory contains the complete documentation for the NexusNews project, designed to be both human-readable and AI-agent friendly.

## � Quick Access

- **[Quick Reference Guide](./QUICK-REFERENCE.md)** - Fast access to all docs
- **[Documentation Structure Guide](./STRUCTURE.md)** - How to organize and create docs
- **[Documentation Home](./index.md)** - Main documentation website

## �📁 Directory Structure

```
docs/
├── .vitepress/          # VitePress configuration
│   ├── config.js        # Site configuration
│   └── theme/           # Custom theme
│
├── index.md             # Documentation homepage
│
├── architecture/        # System Architecture
│   ├── index.md         # Architecture overview
│   ├── news-sources.md  # Multi-source adapter pattern
│   ├── ai-integration.md # OpenRouter AI architecture
│   ├── data-management.md # Room, DataStore, caching
│   └── dependency-injection.md # Hilt setup
│
├── development/         # Development Guides
│   ├── index.md         # Development overview
│   ├── setup.md         # Environment setup guide
│   ├── coding-style.md  # Code standards
│   ├── testing.md       # Testing strategies
│   └── git-workflow.md  # Git conventions
│
├── api/                 # API Documentation
│   ├── index.md         # API overview
│   ├── dependencies.md  # Complete dependency reference
│   ├── newsapi.md       # NewsAPI integration
│   ├── guardian.md      # Guardian API integration
│   ├── openrouter.md    # OpenRouter AI API
│   └── scraping.md      # Jsoup web scraping guide
│
├── project/             # Project Management
│   ├── index.md         # Project overview
│   ├── overview.md      # High-level summary
│   ├── prd.md           # Product Requirements Document
│   └── roadmap.md       # Development roadmap
│
├── weekly/              # Progress Tracking
│   ├── index.md         # Weekly reports overview
│   ├── week-1.md        # Week 1 report
│   ├── week-2.md        # Week 2 report
│   └── ...              # Additional weeks
│
└── ai-context/          # AI Agent Context
    ├── index.md         # Structured project metadata
    └── linking.md       # Context linking guide
```

## 📖 Documentation Categories

### 🏗️ Architecture (`/architecture/`)

Technical system design, patterns, and architectural decisions.

**Key Files:**
- `index.md` - Overall architecture overview with diagrams
- `news-sources.md` - Multi-adapter pattern for news sources
- `ai-integration.md` - OpenRouter AI service layer
- `data-management.md` - Room database and caching strategies

**For AI Agents**: Use these to understand system design and layer interactions.

### 💻 Development (`/development/`)

Setup guides, coding standards, and development workflows.

**Key Files:**
- `setup.md` - Complete environment setup (⭐ Start here for new developers)
- `coding-style.md` - Kotlin coding conventions
- `testing.md` - Unit and UI testing guides
- `git-workflow.md` - Branch strategy and commit conventions

**For AI Agents**: Reference `coding-style.md` for code generation standards.

### 📦 API (`/api/`)

Dependencies, external APIs, and integration guides.

**Key Files:**
- `dependencies.md` - Complete dependency reference (⭐ Most important for AI)
- `newsapi.md` - NewsAPI integration guide
- `openrouter.md` - OpenRouter AI API usage
- `scraping.md` - Web scraping implementation

**For AI Agents**: Check `dependencies.md` for all library versions and usage context.

### 📋 Project (`/project/`)

Project planning, requirements, and roadmap.

**Key Files:**
- `overview.md` - High-level project summary (⭐ Read this first)
- `prd.md` - Complete Product Requirements Document
- `roadmap.md` - Development phases and milestones

**For AI Agents**: Use `prd.md` for complete feature specifications.

### 📅 Weekly (`/weekly/`)

Development logs and progress updates.

**Key Files:**
- `index.md` - Weekly reports overview
- `week-N.md` - Individual week reports

**For AI Agents**: Check latest week for recent changes and context.

### 🤖 AI Context (`/ai-context/`)

**Structured metadata specifically for AI agent consumption.**

**Key Files:**
- `index.md` - JSON-formatted project metadata
- `linking.md` - Guide to linking between documentation

**For AI Agents**: ⭐ **START HERE** - This provides structured context.

## 🎯 Using This Documentation

### For Human Developers

1. **New to the project?**
   - Start: `/project/overview.md`
   - Then: `/development/setup.md`
   - Finally: `/architecture/index.md`

2. **Need to understand a feature?**
   - Check: `/project/prd.md`
   - Then: `/architecture/` for implementation details

3. **Adding dependencies?**
   - Check: `/api/dependencies.md`
   - Update: `gradle/libs.versions.toml`
   - Document: Update `/api/dependencies.md`

### For AI Agents

1. **Getting project context?**
   - Start: `/ai-context/index.md` (structured metadata)
   - Then: `/project/overview.md` (human-readable summary)

2. **Understanding architecture?**
   - Read: `/architecture/index.md`
   - Check: Layer-specific pages

3. **Generating code?**
   - Check: `/development/coding-style.md` (code standards)
   - Check: `/api/dependencies.md` (available libraries)
   - Check: `/architecture/` (architectural patterns)

4. **Finding specific information?**
   - Dependencies: `/api/dependencies.md`
   - Setup: `/development/setup.md`
   - Features: `/project/prd.md`
   - Current phase: `/project/roadmap.md`

## 📝 Documentation Standards

### Frontmatter (Required)

All documentation pages must include frontmatter:

```yaml
---
title: Page Title
description: Brief description
category: architecture | development | api | project
lastUpdated: YYYY-MM-DD
aiContext: true
tags: [tag1, tag2, tag3]
---
```

### Linking Conventions

- **Absolute paths**: Use `/section/page` format
- **Cross-references**: Link related documentation
- **External links**: Use full URLs with HTTPS

### Update Protocol

When making changes:

1. Update relevant documentation pages
2. Update `lastUpdated` in frontmatter
3. If dependencies change, update `/api/dependencies.md`
4. If architecture changes, update `/architecture/`
5. Add entry to latest weekly report

## 🔗 Key Documentation Links

### Most Important Pages

1. **[Project Overview](/project/overview)** - Start here
2. **[AI Context](/ai-context/)** - For AI agents
3. **[Dependencies](/api/dependencies)** - All libraries
4. **[Setup Guide](/development/setup)** - Getting started
5. **[Architecture](/architecture/)** - System design

### Quick References

- **Tech Stack**: `/ai-context/index.md` (JSON format)
- **Current Phase**: `/project/roadmap.md`
- **Code Style**: `/development/coding-style.md`
- **All APIs**: `/api/dependencies.md`

## 🌐 Website

Live documentation: [undead2146.github.io/NexusNews](https://undead2146.github.io/NexusNews/)

Built with [VitePress](https://vitepress.dev/).

## 🔄 Building Documentation Locally

```bash
# Install dependencies
npm install

# or with pnpm
pnpm install

# Run dev server
npm run docs:dev

# Build for production
npm run docs:build

# Preview production build
npm run docs:preview
```

## 📚 Contributing to Documentation

1. Follow the frontmatter standard
2. Use Markdown formatting
3. Include code examples where relevant
4. Link to related documentation
5. Update `lastUpdated` date
6. Test locally before committing

## 🆘 Need Help?

- Check the [Setup Guide](/development/setup)
- Review the [Architecture Overview](/architecture/)
- Browse [GitHub Issues](https://github.com/undead2146/NexusNews/issues)
- Create a new issue for questions

---

**Last Updated**: November 3, 2025  
**Version**: 1.0.0  
**Maintained by**: undead2146
