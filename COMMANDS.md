# AI Chat Mod - Complete Command Reference

## Main Commands

### `/aichat` or `/aichat help`
Displays the help menu with all available commands organized by category.

### `/aichat toggle`
Enables or disables AI responses entirely.

### `/aichat personality <type>`
Changes the AI's personality style.
- **Available types**: `friendly`, `sarcastic`, `professional`, `funny`, `casual`, `mocking`

### `/aichat delay <seconds>`
Sets the cooldown period between AI responses.
- **Range**: 0-300 seconds

### `/aichat config`
Displays the current configuration settings.

---

## Friend Management

### `/aichat friend add <player>`
Adds a player to your friends list.

### `/aichat friend remove <player>`
Removes a player from your friends list.

### `/aichat friend list`
Shows all players on your friends list.

### `/aichat friend whitelist <true|false>`
Enables/disables whitelist mode (AI only responds to friends).

### `/aichat friend autoaccept <true|false>`
Automatically accepts friend requests.

---

## Guild Settings

### `/aichat guild toggle`
Enables/disables AI responses in guild chat.

### `/aichat guild personality <type>`
Sets a different personality specifically for guild chat.

### `/aichat guild mention <true|false>`
Requires mentioning the bot to trigger responses in guild chat.

---

## Visual Settings

### `/aichat visual prefix <text>`
Sets the prefix that appears before AI messages.

### `/aichat visual color <color>`
Changes the color of AI messages.
- **Available colors**: `red`, `green`, `blue`, `yellow`, `aqua`, `white`, `gray`, `gold`, `dark_red`, `dark_green`, `dark_blue`, `dark_aqua`, `dark_gray`, `dark_purple`, `light_purple`

### `/aichat visual thinking <true|false>`
Shows/hides "thinking..." indicator while AI generates response.

---

## Trigger Management

### `/aichat addtrigger <text>`
Adds a custom trigger word/phrase that makes the AI respond.

### `/aichat removetrigger <text>`
Removes a custom trigger.

### `/aichat listtriggers`
Lists all active custom triggers.

---

## Advanced Features

### `/aichat stats`
Displays performance statistics:
- Total responses
- Failed responses
- Success rate
- Average response time
- Fastest/slowest response times

### `/aichat learn <key> <value>`
Teaches the AI a persistent fact that it will remember.
- **Example**: `/aichat learn server Hypixel`

### `/aichat afk`
Shows current AFK status and settings.

### `/aichat afk toggle`
Manually toggles AFK mode on/off.

### `/aichat afk auto <true|false>`
Enables/disables automatic AFK detection.

### `/aichat afk time <minutes>`
Sets the inactivity threshold for auto-AFK.
- **Range**: 1-60 minutes

---

## Configuration Settings (`/aichat set <option> <value>`)

### AI Service Settings

#### `aiservice <gemini|ollama|openai|claude>`
Switches between AI service providers.
- **Default**: `gemini` (free, no local resources needed)
- **Note**: Requires restart after changing

#### `geminiapikey <key>`
Sets your Google Gemini API key.
- Get a free key at: https://aistudio.google.com/app/apikey
- **Note**: Requires restart after setting

#### `maxwords <number>`
Maximum words per AI response.
- **Range**: 10-500

#### `minwords <number>`
Minimum words per AI response.
- **Range**: 1-100

---

### Context Settings

#### `contextmessages <number>`
Number of previous messages to include as context.
- **Range**: 0-50

#### `contexttimeout <minutes>`
How long to remember conversation context.
- **Range**: 1-60 minutes

#### `remembercontext <true|false>`
Enables/disables context memory across messages.

---

### Feature Toggles

#### `emotiondetection <true|false>`
Enables/disables emotion detection system.
- When enabled, AI detects and responds to 8 emotions: happy, sad, angry, excited, confused, grateful, worried, bored

#### `learning <true|false>`
Enables/disables the learning system.
- When enabled, AI remembers facts taught via `/aichat learn` and builds player profiles

#### `chainlimit <number>`
Sets maximum consecutive responses to prevent spam loops.
- **Range**: 1-20
- **Default**: 5 responses per 5 minutes

---

### Visual Features

#### `streaming <true|false>`
Enables/disables word-by-word streaming of AI responses.

#### `streamingspeed <number>`
Sets the speed of streaming responses.
- **Range**: 1-20 words per second
- **Default**: 5 words/sec

#### `badges <true|false>`
Enables/disables chat badges (role indicators).

---

### Translation

#### `autotranslate <true|false>`
Automatically translates AI responses to target language.

#### `targetlanguage <code>`
Sets the target language for translation.
- Use ISO 639-1 codes (2 letters)
- **Examples**: `en` (English), `es` (Spanish), `fr` (French), `de` (German), `ja` (Japanese)

---

### Rate Limiting

#### `maxperhour <number>`
Maximum AI responses allowed per hour.
- **Range**: 1-1000

---

## Quick Reference by Feature

| Feature | Commands |
|---------|----------|
| **Enable/Disable** | `/aichat toggle` |
| **Personality** | `/aichat personality <type>` |
| **Friends** | `/aichat friend <add\|remove\|list\|whitelist\|autoaccept>` |
| **Guild** | `/aichat guild <toggle\|personality\|mention>` |
| **Triggers** | `/aichat addtrigger/removetrigger/listtriggers` |
| **Visual** | `/aichat visual <prefix\|color\|thinking>` |
| **Performance** | `/aichat stats` |
| **Learning** | `/aichat learn <key> <value>`, `/aichat set learning <true\|false>` |
| **AFK Mode** | `/aichat afk <toggle\|auto\|time>` |
| **Emotion Detection** | `/aichat set emotiondetection <true\|false>` |
| **Anti-Spam** | `/aichat set chainlimit <number>` |
| **Streaming** | `/aichat set streaming <true\|false>`, `/aichat set streamingspeed <number>` |
| **Translation** | `/aichat set autotranslate <true\|false>`, `/aichat set targetlanguage <code>` |
| **AI Setup** | `/aichat set aiservice <service>`, `/aichat set geminiapikey <key>` |

---

## Notes

- Most settings are saved automatically and persist across game restarts
- Changing `aiservice` or `geminiapikey` requires a game restart
- The bot responds when your name is mentioned or when custom triggers are detected
- Statistics reset on each game restart
- All boolean settings accept `true` or `false` (case-insensitive)
