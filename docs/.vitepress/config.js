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
                { text: 'Weekly Reports', link: '/weekly/' }
            ],

            sidebar: [
                {
                    text: 'Getting Started',
                    items: [
                        { text: 'Introduction', link: '/' }
                    ]
                },
                {
                    text: 'Weekly Reports',
                    items: generateWeeklyItems()
                }
            ],

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
