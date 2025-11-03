import { defineConfig } from 'vitepress'
import { withMermaid } from 'vitepress-plugin-mermaid'
import fs from 'fs'
import path from 'path'

// Function to dynamically generate weekly report items
function generateWeeklyItems() {
    const weeklyDir = path.join(__dirname, 'weekly')
    const items = [{ text: 'Overview', link: '/weekly/' }]
    
    if (fs.existsSync(weeklyDir)) {
        const files = fs.readdirSync(weeklyDir)
            .filter(file => file.endsWith('.md') && file !== 'index.md')
            .sort()
            .map(file => {
                const weekName = path.basename(file, '.md').replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase())
                return { text: weekName, link: `/weekly/${path.basename(file, '.md')}` }
            })
        items.push(...files)
    }
    
    return items
}

export default withMermaid(
    defineConfig({
        title: 'NexusNews',
        description: 'Android News App Documentation',
        base: '/NexusNews/',

        ignoreDeadLinks: true,

        head: [
            ['link', { rel: 'icon', href: '/assets/icon.png' }]
        ],

        themeConfig: {
            logo: './assets/logo.png',

            nav: [
                { text: 'Home', link: '/' },
                { text: 'Quick Ref', link: '/QUICK-REFERENCE' },
                { text: 'Project', link: '/project/' },
                { text: 'Architecture', link: '/architecture/' },
                { text: 'Development', link: '/development/' },
                { text: 'API', link: '/api/' },
                { text: 'Weekly Reports', link: '/weekly/' },
                { text: 'Context', link: '/context/' }
            ],

            sidebar: {
                '/architecture/': [
                    {
                        text: 'Architecture',
                        items: [
                            { text: 'Overview', link: '/architecture/' },
                            { text: 'News Sources', link: '/architecture/news-sources' },
                            { text: 'AI Integration', link: '/architecture/ai-integration' },
                            { text: 'Data Management', link: '/architecture/data-management' },
                            { text: 'Dependency Injection', link: '/architecture/dependency-injection' }
                        ]
                    }
                ],
                '/development/': [
                    {
                        text: 'Development',
                        items: [
                            { text: 'Overview', link: '/development/' },
                            { text: 'Setup Guide', link: '/development/setup' },
                            { text: 'Coding Style', link: '/development/coding-style' },
                            { text: 'Testing Guide', link: '/development/testing' },
                            { text: 'Git Workflow', link: '/development/git-workflow' }
                        ]
                    }
                ],
                '/api/': [
                    {
                        text: 'API Documentation',
                        items: [
                            { text: 'Overview', link: '/api/' },
                            { text: 'Dependencies', link: '/api/dependencies' },
                            { text: 'NewsAPI', link: '/api/newsapi' },
                            { text: 'Guardian API', link: '/api/guardian' },
                            { text: 'OpenRouter', link: '/api/openrouter' },
                            { text: 'Web Scraping', link: '/api/scraping' }
                        ]
                    }
                ],
                '/project/': [
                    {
                        text: 'Project Management',
                        items: [
                            { text: 'Overview', link: '/project/overview' },
                            { text: 'PRD', link: '/project/prd' },
                            { text: 'Roadmap', link: '/project/roadmap' }
                        ]
                    }
                ],
                '/weekly/': [
                    {
                        text: 'Weekly Reports',
                        items: generateWeeklyItems()
                    }
                ],
                '/context/': [
                    {
                        text: 'Context',
                        items: [
                            { text: 'Project Metadata', link: '/context/' }
                        ]
                    }
                ],
                '/': [
                    {
                        text: 'Getting Started',
                        items: [
                            { text: 'Introduction', link: '/' },
                            { text: 'Quick Reference', link: '/QUICK-REFERENCE' },
                            { text: 'Project Overview', link: '/project/overview' },
                            { text: 'Setup Guide', link: '/development/setup' }
                        ]
                    },
                    {
                        text: 'Documentation',
                        items: [
                            { text: 'Architecture', link: '/architecture/' },
                            { text: 'Development', link: '/development/' },
                            { text: 'API Reference', link: '/api/' },
                            { text: 'Project Info', link: '/project/' }
                        ]
                    },
                    {
                        text: 'Guides',
                        items: [
                            { text: 'Documentation Structure', link: '/STRUCTURE' },
                            { text: 'Weekly Reports', link: '/weekly/' }
                        ]
                    }
                ]
            },

            socialLinks: [
                { icon: 'github', link: 'https://github.com/undead2146/NexusNews' }
            ],

            footer: {
                message: 'NexusNews Docs',
                copyright: '© 2025 NexusNews'
            }
        },

        // Mermaid configuration
        mermaid: {
            theme: 'default',
            themeVariables: {
                primaryColor: '#7c3aed',
                primaryTextColor: '#fff',
                primaryBorderColor: '#6b46c1',
                lineColor: '#5f5f5f',
                secondaryColor: '#2ed573',
                tertiaryColor: '#1e90ff'
            }
        },

        // Optional: Configure mermaid for dark mode
        mermaidPlugin: {
            class: 'mermaid my-class'
        }
    }),

    // Mermaid configuration
    {
        theme: 'default'
    }
)
