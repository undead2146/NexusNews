---
title: NexusNews Documentation
description: Comprehensive documentation for NexusNews AI-powered news aggregator
lastUpdated: 2025-11-03
version: 1.0.0
aiContext: true
---

# NexusNews Documentation

Welcome to the comprehensive documentation for **NexusNews** - a multi-source Belgian & International news aggregator powered by OpenRouter AI.

## 📚 Documentation Structure

This documentation is organized into several key sections, designed to be both human-readable and AI-agent friendly.

### For Developers

<div class="grid">
  <div class="card">
    <h3>📊 Application Reports</h3>
    <p>Comprehensive analysis from both user and developer perspectives.</p>
    <a href="/report/">Read Reports →</a>
  </div>

  <div class="card">
    <h3>🏛️ Architecture</h3>
    <p>Clean Architecture, MVVM, and offline-first data management.</p>
    <a href="./architecture/">Technical Documentation →</a>
  </div>

  <div class="card">
    <h3>💻 Development</h3>
    <p>Setup guides, coding standards, and workflows</p>
    <a href="./development/">Development Guide →</a>
  </div>

  <div class="card">
    <h3>📦 API Reference</h3>
    <p>Dependencies, packages, and integrations</p>
    <a href="./api/">API Documentation →</a>
  </div>

  <div class="card">
    <h3>📋 Project Management</h3>
    <p>Roadmap, issues, and progress tracking</p>
    <a href="./project/">Project Info →</a>
  </div>
</div>

### Progress Tracking

<div class="weekly-section">
  <h3>📅 Weekly Reports</h3>
  <p>Development logs and progress updates</p>
  <a href="./weekly/">View Reports →</a>
</div>

## 🚀 Quick Start

New to NexusNews? Start here:

1. **[Project Overview](./project/overview)** - Understand what NexusNews is
2. **[Architecture Overview](./architecture/)** - Learn the system design
3. **[Setup Guide](./development/setup)** - Get your development environment ready
4. **[Dependencies](./api/dependencies)** - Understand the tech stack

## 🔗 Important Links

- [GitHub Repository](https://github.com/undead2146/NexusNews)
- [Product Requirements Document](./project/prd)
- [Roadmap](./project/roadmap)
- [Coding Style Guide](./development/coding-style)

## 📖 Documentation Philosophy

This documentation follows these principles:

- **AI-Friendly**: All pages include frontmatter metadata for easy parsing by AI agents
- **Cross-Linked**: Related content is interconnected for easy navigation
- **Living Document**: Updated alongside code changes
- **Version Tracked**: Dependencies and APIs are version-tracked
- **Multi-Purpose**: Serves developers, project managers, and AI assistants

## 🆘 Getting Help

- Check the relevant documentation section
- Check [GitHub Issues](https://github.com/undead2146/NexusNews/issues)
- Review the [Weekly Reports](./weekly/) for recent changes
- Read the [Documentation Structure Guide](./STRUCTURE)

---

<style>
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin: 2rem 0;
}

.card {
  border: 1px solid var(--vp-c-divider);
  border-radius: 8px;
  padding: 1.5rem;
  transition: all 0.3s;
}

.card:hover {
  border-color: var(--vp-c-brand);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.card h3 {
  margin-top: 0;
  color: var(--vp-c-brand);
}

.ai-section, .weekly-section {
  background: var(--vp-c-bg-soft);
  border-radius: 8px;
  padding: 1.5rem;
  margin: 2rem 0;
}
</style>
