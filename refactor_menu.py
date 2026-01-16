
import os
import re

file_path = r'z:\NexusNews\app\src\main\java\com\example\nexusnews\presentation\screens\NewsDetailScreen.kt'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Replace IconButton onClick
old_button = 'onClick = { showMenu = !showMenu }'
new_button = 'onClick = { showActionSheet = true }'
content = content.replace(old_button, new_button)

# 2. Check for ModalBottomSheet content
layout_chunk = """            // Modal Bottom Sheet for Actions
            if (showActionSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showActionSheet = false },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "AI Actions",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )

                        androidx.compose.material3.HorizontalDivider()

                        // Deep AI Analysis (All at once) - Promoted to top
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "Deep AI Analysis",
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            supportingContent = { Text("Run all analyses at once") },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.clickable {
                                showActionSheet = false
                                viewModel.analyzeArticle(uiState.article!!)
                            }
                        )

                        androidx.compose.material3.HorizontalDivider()

                        // AI Summary
                        if (summaryState is SummaryState.Idle || summaryState is SummaryState.Error) {
                            ListItem(
                                headlineContent = { Text("AI Summary") },
                                supportingContent = { Text("Generate article summary") },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.clickable {
                                    showActionSheet = false
                                    viewModel.generateSummary(uiState.article!!)
                                }
                            )
                        }

                        // Sentiment Analysis
                        ListItem(
                            headlineContent = { Text("Sentiment Analysis") },
                            supportingContent = { Text("Analyze emotional tone") },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Face,
                                    contentDescription = null,
                                    tint = Color(0xFF9C27B0)
                                )
                            },
                            modifier = Modifier.clickable {
                                showActionSheet = false
                                viewModel.analyzeSentiment(uiState.article!!)
                            }
                        )

                        // Key Points
                        ListItem(
                            headlineContent = { Text("Key Points") },
                            supportingContent = { Text("Extract main points") },
                            leadingContent = {
                                Icon(
                                    Icons.Default.FormatListBulleted,
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3)
                                )
                            },
                            modifier = Modifier.clickable {
                                showActionSheet = false
                                viewModel.extractKeyPoints(uiState.article!!)
                            }
                        )

                        // Topic Classification
                        ListItem(
                            headlineContent = { Text("Topics") },
                            supportingContent = { Text("Classify article topics") },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Label,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50)
                                )
                            },
                            modifier = Modifier.clickable {
                                showActionSheet = false
                                viewModel.classifyTopic(uiState.article!!)
                            }
                        )

                        // Entity Recognition
                        ListItem(
                            headlineContent = { Text("Entities") },
                            supportingContent = { Text("People, places, organizations") },
                            leadingContent = {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800)
                                )
                            },
                            modifier = Modifier.clickable {
                                showActionSheet = false
                                viewModel.recognizeEntities(uiState.article!!)
                            }
                        )

                        // Bias Detection
                        ListItem(
                            headlineContent = { Text("Bias Analysis") },
                            supportingContent = { Text("Detect bias and objectivity") },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Balance,
                                    contentDescription = null,
                                    tint = Color(0xFFF44336)
                                )
                            },
                            modifier = Modifier.clickable {
                                showActionSheet = false
                                viewModel.detectBias(uiState.article!!)
                            }
                        )
                    }
                }
            }
"""

# 3. Locate and remove DropdownMenu
# The DropdownMenu starts with comment "// Dropdown Menu" and ends...
# We can find the start index
start_marker = "// Dropdown Menu"
start_idx = content.find(start_marker)

if start_idx != -1:
    # We need to find where this block ends.
    # It seems to span from line 174 to roughly 393.
    # The block ends with a closing brace indentation match?
    # Let's count braces from the start of DropdownMenu

    # Scanning forward from start_idx
    balance = 0
    i = start_idx

    # Skip until we hit the first { (which belongs to DropdownMenu content)
    # DropdownMenu( ... ) {
    open_brace_found = False

    while i < len(content):
        char = content[i]
        if char == '{':
            balance += 1
            open_brace_found = True
        elif char == '}':
            balance -= 1
            if open_brace_found and balance == 0:
                # This could be the end of DropdownMenu content lambda?
                # DropdownMenu also has standard args before lambda.
                pass

        # Heuristic: DropdownMenu(...) { ... }
        # So when balance returns to 0 after being positive, we are done with the lambda.
        if open_brace_found and balance == 0:
             # End of DropdownMenu
            end_idx = i + 1
            break
        i += 1

    if i < len(content):
        # Remove the block, including the whitespace before it presumably?
        # Actually, let's keep it safe. We replace [start_idx : end_idx] with nothing.
        # But we need to check indentation.

        # Grab the indentation before start_marker
        line_start = content.rfind('\n', 0, start_idx) + 1
        indentation = content[line_start:start_idx]

        # We remove from line_start to end_idx.
        content = content[:line_start] + content[end_idx:]

        # Now Insert the ModalBottomSheet
        # We want to insert it AFTER the Row closes.
        # The Row closes right after the menu button Box closes.
        # The code was:
        # Box {
        #    Surface { ... } (Menu Button)
        #    DropdownMenu { ... } (Removed now)
        # } (Box ends)
        # } (Row ends, wait logic check)

        pass

# 4. Insert ModalBottomSheet
# Find "if (uiState.article != null) {" which marks the start of Menu Button block
menu_block_start = content.find("if (uiState.article != null) {")
# That block contains the Box that contained Dropdown.
# We want to insert AFTER the Row that contains this block.
# The Row ends... later.

# Safer strategy: Insert at the end of the root Box content.
# Searching for " FAB Removed " comment or similar distinctive closing?
# Matches:
#        // FAB Removed as per user request to reduce clutter
#    }
# The closing brace } before that comment is the Row? No.
# Use string replacement on a known anchor.

anchor = "        // FAB Removed as per user request to reduce clutter"
content = content.replace(anchor, layout_chunk + "\n" + anchor)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Refactor complete.")
