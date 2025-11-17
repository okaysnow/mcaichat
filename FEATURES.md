# AI Chat Mod - Complete Feature Documentation

**Version:** 1.0  
**Author:** snow  
**Minecraft Version:** 1.8.9 (Forge)  
**Last Updated:** November 16, 2025

---

## Table of Contents

1. [Overview](#overview)
2. [Core Features](#core-features)
3. [AI Services](#ai-services)
4. [Chat Detection](#chat-detection)
5. [Commands](#commands)
6. [Configuration](#configuration)
7. [Advanced Features](#advanced-features)
8. [Hypixel Integration](#hypixel-integration)
9. [Analytics & Tracking](#analytics--tracking)
10. [Moderation & Safety](#moderation--safety)
11. [File Structure](#file-structure)

---

## Overview

The AI Chat Mod is a client-side Minecraft 1.8.9 Forge mod that uses AI to automatically respond to chat messages on Hypixel. It detects messages in different channels (guild, party, whispers, public) and generates contextually appropriate responses using various AI services.

### Key Capabilities
- ✅ Multi-channel support (guild, party, whisper, all chat)
- ✅ Four AI service options (Gemini FREE, Ollama, OpenAI, Claude)
- ✅ Conversation context tracking
- ✅ Friend management with whitelist mode
- ✅ Emotion detection and adaptive responses
- ✅ Learning system for custom facts
- ✅ Response time analytics
- ✅ AFK mode for away detection
- ✅ Chain limiting to prevent spam
- ✅ Multi-language support with translation
- ✅ Party management (invite, kick, warp)
- ✅ Game actions (join games, return to lobby)
- ✅ Death detection and mocking
- ✅ Persistent memory across restarts
- ✅ 5-minute conversation windows

---

## Core Features

### 1. **Automatic Chat Detection**
**Files:** `ChatHandler.java`, `ChatParser.java`

The mod listens to all incoming chat messages and parses them to determine:
- **Channel type** (guild/party/whisper/public)
- **Sender name**
- **Message content**
- **Whether your name was mentioned**

**How it works:**
- Uses regex patterns to match Hypixel's chat formats
- Detects patterns like `[Guild] PlayerName: message`
- Extracts sender and message from matched patterns
- Determines if response is needed based on mention or triggers

### 2. **AI Response Generation**
**Files:** `AIService.java`, `GeminiService.java`, `OpenAIService.java`, `ClaudeService.java`, `OllamaService.java`

The mod supports four AI services: | Service | API | Cost | Local | Speed | |---------|-----|------|-------|-------| | **Gemini** (default) | Google AI | Free | ❌ No | ⚡ Very Fast | | **Ollama** | REST API | Free | ✅ Yes | 🐌 Slow | | **OpenAI** | GPT-3.5/4 | Paid | ❌ No | ⚡ Fast | | **Claude** | Claude 3 | Paid | ❌ No | ⚡ Fast |

**How it works:**
- Sends HTTP POST requests to AI service APIs
- Includes conversation context for continuity
- Uses async `CompletableFuture` to prevent game lag
- Applies personality settings to modify responses
- Limits response length based on config

### 3. **Conversation Context**
**Files:** `ConversationManager.java`

Tracks conversation history per player for natural dialogue.

**Features:**
- Stores last N messages per player (configurable)
- Includes both user messages and AI responses
- Expires context after timeout period
- Separate context for each player

**How it works:**
```java
// Add user message to context
ConversationManager.addMessage(playerName, "user", message);

// Get context for AI prompt
List<ConversationMessage> context = ConversationManager.getContext(playerName);

// Context expires after contextTimeoutMinutes
```

### 4. **Friend Management**
**Files:** `FriendManager.java`, `FriendRequestHandler.java`

Control who can trigger AI responses.

**Modes:**
- **Normal:** Everyone can trigger responses
- **Whitelist:** Only friends can trigger responses
- **Auto-accept:** Automatically accepts friend requests

**Commands:**
- `/aichat friend add <player>` - Add friend
- `/aichat friend remove <player>` - Remove friend
- `/aichat friend list` - Show all friends
- `/aichat friend whitelist` - Toggle whitelist mode
- `/aichat friend autoaccept` - Toggle auto-accept

---

## AI Services

### Google Gemini (Default) ⭐
**Free cloud AI - No installation needed!**

**Setup:**
1. Get a free API key from https://aistudio.google.com/app/apikey
2. Edit `config/aichat.json`:
   ```json
   {
     "aiService": "gemini",
     "geminiApiKey": "AIza..."
   }
   ```

**Pros:**
- ✅ **Completely FREE** with generous rate limits
- ✅ **Very fast responses** (cloud-based)
- ✅ **No installation** - works immediately
- ✅ **Zero resource usage** on your PC
- ✅ High quality, natural responses

**Cons:**
- ❌ Requires internet connection
- ❌ Google tracks usage (but it's free!)

**Rate Limits:** 60 requests per minute (free tier) - more than enough for Minecraft chat!

---

### Ollama
**Local AI that runs on your computer**

**Setup:**
1. Install Ollama from https://ollama.ai
2. Run `ollama pull llama2` in terminal
3. Default URL: `http://localhost:11434`

**Recommended Hardware:**
- **CPU:** Modern multi-core processor (Intel i5/i7/i9 or AMD Ryzen 5/7/9)
  - Minimum: 4 cores, 8 threads
  - Recommended: 6+ cores for smooth performance
- **RAM:** 16GB+ (8GB minimum, but 16GB+ recommended for larger models)
- **GPU (Optional but highly recommended):**
  - NVIDIA: GTX 1660 Ti or better (RTX 3060+ ideal)
  - AMD: RX 6600 XT or better
  - Minimum VRAM: 4GB (8GB+ for better models)
- **Storage:** 10GB+ free space for models

**Performance Tips:**
- Use GPU acceleration if available (much faster than CPU)
- Close other applications to free up RAM
- Use smaller models like `llama2:7b` for faster responses
- Larger models like `llama2:13b` or `llama2:70b` need more VRAM

**Pros:** Free, private, no API keys needed  
**Cons:** Requires good CPU/GPU, uses lots of resources, slower than cloud services

---

### OpenAI
**Cloud-based AI (GPT-3.5-turbo)**

**Setup:**
1. Get API key from https://platform.openai.com
2. Edit `config/aichat.json`:
   ```json
   {
     "aiService": "openai",
     "openaiApiKey": "sk-..."
   }
   ```

**Pros:** Fast, high quality responses  
**Cons:** Costs money per request (~$0.002 per conversation)

### Claude
**Anthropic's AI (Claude 3 Haiku)**

**Setup:**
1. Get API key from https://console.anthropic.com
2. Edit `config/aichat.json`:
   ```json
   {
     "aiService": "claude",
     "claudeApiKey": "sk-ant-..."
   }
   ```

**Pros:** Good at following instructions, creative  
**Cons:** Costs money per request (~$0.0008 per conversation)

---

## Quick Comparison | Feature | Gemini (Default) | Ollama | OpenAI | Claude | |---------|------------------|--------|--------|--------| | **Cost** | FREE ✅ | FREE ✅ | Paid ❌ | Paid ❌ | | **Speed** | Very Fast ⚡ | Slow 🐌 | Fast ⚡ | Fast ⚡ | | **Install** | None ✅ | Required ❌ | None ✅ | None ✅ | | **Resources** | 0% CPU/RAM ✅ | High ❌ | 0% ✅ | 0% ✅ | | **Quality** | Excellent | Good | Excellent | Excellent | | **Privacy** | Google sees logs | Private | OpenAI sees logs | Anthropic sees logs |

**Recommendation:** Use **Gemini** (default) - it's free, fast, and requires no installation!
2. Edit `config/aichat.json`:
   ```json
   {
     "aiService": "claude",
     "claudeApiKey": "sk-ant-..."
   }
   ```

**Pros:** Good at following instructions, creative  
**Cons:** Costs money per request

---

## Chat Detection

### Supported Channels
**File:** `ChatChannel.java` | Channel | Format | Auto-responds? | Prefix | |---------|--------|----------------|--------| | **Guild** | `[Guild] Name: msg` | Yes (no mention needed) | `/gc` | | **Party** | `Party > Name: msg` | When mentioned | `/pc` | | **Whisper** | `From Name: msg` | Always | `/r` | | **Public** | `Name: msg` | When mentioned | `/ac` |

### Channel Detection Logic
**File:** `ChatParser.java`

```java
// Example: Guild chat detection
if (message.startsWith("§2Guild >") || message.contains("§3[G]")) {
    return new ChatChannel(ChannelType.GUILD, senderName, "/gc " + response);
}
```

### Mention Detection
**File:** `ChatHandler.java`

The mod checks if your name appears in the message:
```java
String playerName = Minecraft.getMinecraft().thePlayer.getName();
if (message.toLowerCase().contains(playerName.toLowerCase())) {
    // Respond!
}
```

You can also add **custom triggers**:
```
/aichat addtrigger ai
/aichat addtrigger bot
/aichat addtrigger help
```

---

## Commands

### Main Command: `/aichat`
**File:** `AIChatCommand.java`

Shows help menu with all available commands.

### Command List

#### **Basic Controls**
```
/aichat toggle              - Enable/disable AI responses
/aichat mute                - Quick disable (same as toggle off)
/aichat unmute              - Quick enable (same as toggle on)
/aichat personality <type>  - Change AI personality
/aichat delay <seconds>     - Set response cooldown (0-60)
/aichat config              - View current configuration
/aichat debug               - Toggle debug mode (shows decision logs)
```

#### **Personality Types**
- `friendly` - Warm and welcoming (default)
- `sarcastic` - Sarcastic and witty
- `professional` - Formal and business-like
- `funny` - Humorous and joking
- `casual` - Relaxed and informal
- `mocking` - Mocks and makes fun of messages (use with caution!)

#### **Friend Management**
```
/aichat friend add <name>      - Add friend
/aichat friend remove <name>   - Remove friend
/aichat friend list            - Show all friends
/aichat friend whitelist       - Toggle whitelist mode
/aichat friend autoaccept      - Toggle auto-accept requests
```

#### **Guild Settings**
```
/aichat guild              - Show guild settings
/aichat guild toggle       - Enable/disable guild responses
/aichat guild personality  - Set guild-specific personality
/aichat guild mention      - Toggle mention requirement in guild
```

#### **Visual Settings**
```
/aichat visual prefix <text>   - Change chat prefix (default: [AI])
/aichat visual color <color>   - Change prefix color
/aichat visual thinking        - Toggle "thinking..." indicator
```

**Available colors:** GOLD, YELLOW, GREEN, AQUA, BLUE, RED, LIGHT_PURPLE, WHITE

#### **Trigger Management**
```
/aichat addtrigger <text>     - Add custom trigger word
/aichat removetrigger <text>  - Remove trigger word
/aichat listtriggers          - Show all triggers
```

#### **In-Game Configuration**
```
/aichat set maxWords <1-100>           - Max response length
/aichat set minWords <1-50>            - Min response length
/aichat set contextMessages <0-50>    - Context history size
/aichat set contextTimeout <mins>     - Context expiration time
/aichat set rememberContext <true|false>  - Enable/disable context
/aichat set maxPerHour <1-1000>       - Max responses per hour
/aichat set aiService <name>          - Change AI service
/aichat set autoTranslate <true|false>  - Enable auto-translation
/aichat set targetLanguage <code>     - Set target language (en, es, fr, etc.)
/aichat set streaming <true|false>    - Enable streaming responses
/aichat set badges <true|false>       - Show status badges
```

#### **Statistics**
```
/aichat stats              - Show performance statistics
/aichat stats player <name>  - Show stats for specific player
```

#### **Learning System**
```
/aichat learn <key> <value>    - Teach AI a fact
/aichat forget <key>           - Remove learned fact
/aichat recall <key>           - Show learned fact
/aichat nickname <name>        - Set your nickname
```

#### **AFK Mode**
```
/aichat afk              - Toggle AFK mode manually
/aichat afk auto         - Toggle auto-AFK detection
/aichat afk time <mins>  - Set AFK timeout
```

#### **Party Management**
```
/aichat party status           - View party members
/aichat party invite <player>  - Invite to party
/aichat party kick <player>    - Kick from party (leader only)
/aichat party warp             - Warp party to your lobby
/aichat party leave            - Leave party
/aichat party autoinvite <on/off>  - AI auto-invites based on conversation
```

#### **Game Actions**
```
/aichat game play <mode>   - Join a game mode
/aichat game lobby         - Return to lobby
/aichat game list          - Show available game modes
/aichat game toggleplay    - Toggle AI /play permission
/aichat game togglelobby   - Toggle AI /lobby permission
/aichat game togglewarp    - Toggle AI /p warp permission
```

#### **Memory Management**
```
/aichat memory save        - Manually save memory to disk
/aichat memory clear [player]  - Clear memory (all or specific player)
/aichat memory stats       - View memory statistics
/aichat convo              - View active conversation windows
```

---

## Configuration

### Config File: `config/aichat.json`
**File:** `ModConfig.java`

Auto-generated on first run. All settings can be edited in-game or by editing the JSON file.

### Default Configuration
```json
{
  "aiService": "ollama",
  "openaiApiKey": "your-api-key-here",
  "openaiModel": "gpt-3.5-turbo",
  "claudeApiKey": "your-api-key-here",
  "claudeModel": "claude-3-haiku-20240307",
  
  "personality": "friendly",
  "maxResponseWords": 20,
  "minResponseWords": 3,
  
  "maxContextMessages": 10,
  "rememberContext": true,
  "contextTimeoutMinutes": 30,
  
  "autoTranslate": false,
  "targetLanguage": "en",
  "detectLanguage": true,
  
  "cooldownSeconds": 3,
  "maxResponsesPerHour": 50,
  
  "guildChatEnabled": true,
  "guildPersonality": "friendly",
  "guildRequiresMention": false,
  
  "chatPrefix": "[AI]",
  "chatColor": "GOLD",
  "showThinking": true,
  "enableBadges": true,
  "streamingEnabled": false,
  "streamingSpeed": 5,
  
  "chainLimit": 5,
  "chainResetMinutes": 5,
  "afkThresholdMinutes": 5,
  "autoAFK": true,
  "emotionDetection": true,
  "learningEnabled": true
}
```

### Learning Data File: `config/learning.json`
**File:** `LearningSystem.java`

Stores learned facts and player profiles.

```json
{
  "facts": {
    "server": "Hypixel",
    "owner": "snow"
  },
  "profiles": {
    "PlayerName": {
      "preferredName": "Nick",
      "interests": ["bedwars", "skyblock"],
      "dislikes": ["lag"],
      "interactionCount": 42,
      "customFacts": {
        "age": "15",
        "timezone": "EST"
      }
    }
  }
}
```

---

## Advanced Features

### 1. Response Variation
**File:** `ResponseVariation.java`

Prevents the AI from giving identical responses.

**How it works:**
- Tracks last 5 responses to each player
- Calculates similarity score between new and old responses
- Regenerates response if too similar (>70% match)
- Uses Levenshtein distance algorithm

### 2. Streaming Responses
**File:** `StreamingResponse.java`

Displays responses word-by-word like typing.

**Configuration:**
```java
streamingEnabled = true
streamingSpeed = 5  // words per second
```

### 3. Retry Logic
**File:** `RetryLogic.java`

Auto-retries failed AI requests with exponential backoff.

**Retry schedule:**
- Attempt 1: Immediate
- Attempt 2: 1 second delay
- Attempt 3: 2 seconds delay
- Attempt 4: 4 seconds delay

**Fallback messages:**
- "Sorry, I'm having trouble thinking right now!"
- "My brain is lagging, try again in a moment."

### 4. Topic Tracking
**File:** `TopicTracker.java`

Identifies conversation topics to maintain context.

**Detected topics:**
- Gaming (game modes, pvp, skills)
- Trading (buying, selling, prices)
- Help (questions, tutorials)
- Social (greetings, chatting)
- Technical (bugs, mods, performance)
- Casual (random chat)

### 5. Chat Badges
**File:** `ChatBadges.java`

Visual status indicators in chat.

**Badge types:** | Badge | Symbol | Meaning | |-------|--------|---------| | ENABLED | `[+]` | AI is active | | DISABLED | `[-]` | AI is disabled | | THINKING | `[>]` | Processing response | | ERROR | `[!]` | Error occurred | | AFK | `[~]` | User is AFK | | RATE_LIMITED | `[*]` | Rate limit hit |

### 6. Translation
**File:** `TranslationService.java`

Auto-translates messages between languages.

**Features:**
- Auto-detects source language
- Translates to target language
- Uses LibreTranslate API
- Preserves original message

**Configuration:**
```json
{
  "autoTranslate": true,
  "targetLanguage": "en"
}
```

### 7. Conversation Starters
**File:** `ConversationStarter.java`

AI can proactively initiate conversations instead of only responding.

**Features:**
- Greets players who return after being idle
- Tracks player activity and idle time
- Generates contextual greetings
- Can congratulate achievements

**How it works:**
- Monitors player activity in chat
- Detects when players have been idle for 10+ minutes
- Generates personalized greeting when they return
- 30% chance to greet (prevents spam)
- 1-hour cooldown between greetings

**Enable/disable:**
```
/aichat set starters true
```

**Example greetings:**
- "Hey PlayerName! Haven't seen you in a while. How's it going?"
- "PlayerName! Welcome back! What have you been up to?"
- "Oh hey PlayerName, long time no see! Everything good?"

**Integration:**
The AI receives conversation starter context in every prompt when enabled, allowing natural greeting behavior.

### 8. Confidence Scoring
**File:** `ConfidenceTracker.java`

Shows AI certainty levels with each response.

**Features:**
- AI rates its own confidence (0-100%)
- Displays colored confidence badges
- Detects uncertain language patterns
- Warns about low confidence answers

**Confidence levels:**
- **90-100% (Dark Green):** Very confident, factual information
- **70-89% (Green):** High confidence, likely accurate
- **50-69% (Yellow):** Moderate confidence, educated guess
- **30-49% (Gold):** Low confidence, speculative
- **0-29% (Red):** Very uncertain or don't know

**How it works:**
1. System prompt asks AI to include CONFIDENCE:XX% at end of response
2. AI analyzes its certainty and adds score
3. ConfidenceTracker extracts score and formats response
4. Badge is added showing confidence level
5. If score unavailable, estimates based on uncertain words

**Uncertain language detection:**
Detects words like "maybe", "perhaps", "possibly", "might", "could be", "not sure", "think", "probably", "guess"

**Enable/disable:**
```
/aichat set confidence true
```

**Example output:**
```
[High Confidence] Bed Wars is located in the main lobby! (87%)
[Low Confidence] I think it might be under the parkour section... (42%)
```

### 9. Rate Limit Warnings
**File:** `RateLimitMonitor.java`

Proactive warnings before hitting hourly response limits.

**Features:**
- Tracks hourly usage against limit
- Warns at configurable threshold (default 80%)
- Shows countdown to rate limit reset
- Suggests increasing limits

**How it works:**
- Monitors responses per hour
- Calculates current usage percentage
- Shows warning when reaching threshold
- Displays time until hourly reset
- Only warns once per hour

**Configuration:**
```
/aichat set ratelimitwarning 80   # Warn at 80%
/aichat set ratelimitwarning 0    # Disable warnings
```

**Check current status:**
```
/aichat ratelimitstatus
```

**Example warning:**
```
[AI Chat Warning] You've used 42/50 responses this hour (84%). Approaching rate limit!
  Rate limit resets in: 23:15
  Tip: Use /aichat ratelimit <number> to increase the hourly limit.
```

**Color-coded status:**
- **Green:** <80% - Safe usage
- **Yellow:** 80-89% - Approaching limit
- **Red:** 90%+ - Nearly at limit

---

## Hypixel Integration

### 1. Party Management
**File:** `PartyManager.java`

Automatically tracks and manages Hypixel party state.

**Features:**
- Detects party joins/leaves via chat messages
- Tracks all current party members
- Detects party leader status
- Monitors kicks, disbands, and leader transfers
- Provides party invite/kick/warp functionality

**Chat patterns detected:**
- `PlayerName joined the party.`
- `PlayerName has left the party.`
- `PlayerName was removed from the party.`
- `The party was disbanded because all invites expired and the party was empty`
- `The party leader, PlayerName, warped the party to their lobby.`
- `The party was transferred to PlayerName by OldLeader`

**Party commands:**
```
/aichat party status          # View current party info
/aichat party invite <player> # Invite player to party
/aichat party kick <player>   # Kick player (leader only)
/aichat party warp            # Warp party to your lobby
/aichat party leave           # Leave current party
/aichat party autoinvite <on/off> # AI auto-invites based on conversation
```

**AI capabilities:**
- Suggests party invites when conversation is friendly
- Auto-invites players if `autoInviteToParty` is enabled
- Can warp party if you are leader (requires toggle)
- Tracks party size and member list for context

### 2. Game Actions
**File:** `GameActionManager.java`

Allows AI to execute Hypixel game commands based on conversation.

**Supported actions:**
- Join games via `/play <mode>`
- Return to lobby via `/lobby`
- Warp party via `/p warp` (if party leader)

**Safety toggles:**
All game actions are disabled by default for safety. Enable via commands:
```
/aichat game toggleplay   # Allow AI to use /play command
/aichat game togglelobby  # Allow AI to use /lobby command
/aichat game togglewarp   # Allow AI to warp party
```

**Supported game modes:**
- `bedwars_eight_one` - Bed Wars Solo
- `bedwars_eight_two` - Bed Wars Doubles
- `bedwars_four_four` - Bed Wars 4v4
- `skywars_solo_normal` - SkyWars Solo
- `skywars_teams_normal` - SkyWars Teams
- `duels_bridge_duel` - Bridge Duels
- `duels_uhc_duel` - UHC Duels
- `duels_sw_duel` - SkyWars Duels
- `murder_mystery` - Murder Mystery
- `buildbattle` - Build Battle
- `tntgames` - TNT Games
- `arcade_party_games` - Party Games
- `pit` - The Pit
- `skyblock` - SkyBlock
- `prototype_lobby` - Prototype Lobby

**Game commands:**
```
/aichat game play <mode>  # Join a game mode
/aichat game lobby        # Return to lobby
/aichat game list         # Show all available modes
/aichat game toggleplay   # Toggle AI /play permission
/aichat game togglelobby  # Toggle AI /lobby permission
/aichat game togglewarp   # Toggle AI /p warp permission
```

**AI capabilities:**
- Suggests game modes based on conversation ("want to play bedwars?")
- Can queue for games if `allowPlayCommand` is enabled
- Can return to lobby if `allowLobbyCommand` is enabled
- Validates game modes before executing
- Only works on Hypixel (no /warp command exists)

**Configuration:**
```json
{
  "allowPlayCommand": false,
  "allowLobbyCommand": false,
  "allowWarpCommand": false,
  "autoInviteToParty": false
}
```

### 3. Death Detection & Mocking
**File:** `ChatParser.java`

Detects Hypixel game events for enhanced AI responses.

**Detected events:**
- Player deaths
- Final kills
- Bed destroys
- Game losses

**Mocking personality integration:**
When personality is set to "mocking", the AI will:
- Detect when players die in chat
- Generate roasts about their gameplay
- Send roasts in all chat mentioning the player
- Only mock during active conversation windows

**Example death messages detected:**
- `PlayerName was killed by OtherPlayer.`
- `PlayerName fell into the void.`
- `PlayerName was blown up by OtherPlayer.`
- `FINAL KILL! PlayerName was slain by OtherPlayer.`

**Command:**
```
/aichat personality mocking  # Enable death roasts
```

### 4. Party Leader AI Assistant
**File:** `PartyLeaderAssistant.java`

Provides intelligent suggestions when you become party leader.

**Features:**
- Detects when you become party leader
- Tracks party member activity and idle time
- Suggests actions based on party state
- Provides game mode recommendations by party size

**Suggestions triggered:**
- **Idle party members:** If 50%+ members are idle for 3+ minutes, suggests warping party
- **Ready party:** After 60 seconds with 2+ members, suggests appropriate game modes
- **Party size optimization:** Recommends games that fit your party size

**Party size recommendations:**
- 2 players: Bed Wars Doubles, Bridge Duels
- 3 players: Murder Mystery, SkyWars Trios
- 4 players: Bed Wars 4v4 (perfect size!)
- 5-8 players: Murder Mystery, TNT Games
- 8+ players: Party games or splitting up

**AI context:**
The AI receives party status in every response:
- Current party size
- How long party has been formed
- Number of idle members
- Available actions (warp/queue if enabled)

**Configuration:**
```json
{
  "allowWarpCommand": true,   # AI can suggest warping
  "allowPlayCommand": true     # AI can suggest queueing
}
```

### 5. Smart Party Invites
**File:** `SmartInvites.java`

Analyzes conversation sentiment to detect when players want to play together.

**Detection patterns:**
- Direct requests: "invite me", "can i join", "add me to party"
- Play together: "let's play", "wanna play", "down to play"
- Game questions: "what game", "what should we play", "ready to queue"
- Keywords: Mentions of "party" or "team"

**Scoring system:**
- Direct invite request: +5 score (instant invite)
- "Play together" phrase: +2 score
- Game question: +1 score
- Party/team mention: +1 score
- Threshold for suggestion: 3 points

**Auto-invite behavior:**
- Scores decay by 1 point per minute
- Suggestions expire after 5 minutes
- Auto-invites when score reaches 5+ points
- Checks party capacity (max 8 players)
- Won't invite if player already in party

**AI integration:**
The AI receives context when a player shows interest:
```
[SOCIAL CONTEXT: PlayerName has shown interest in playing together (score: 4). 
You can suggest adding them to your current party (3/8).]
```

**Suggestions:**
- "PlayerName seems interested in playing. Should I send them a party invite?"
- "It seems like PlayerName wants to play together. Should I invite them to a party?"
- "PlayerName wants to join, but the party is full (8/8)."

**Configuration:**
```json
{
  "autoInviteToParty": true   # Enable smart invite system
}
```

**Example flow:**
1. PlayerX: "hey wanna play bedwars?" (+2 score)
2. PlayerX: "let's party up" (+2 score = 4 total)
3. AI: "PlayerX seems interested in playing. Should I send them a party invite?"
4. User says "yes" or PlayerX says "invite me" (+2 score = 6 total)
5. Auto-invite executes: `/party invite PlayerX`

---

## Analytics & Tracking

### Response Time Tracking
**File:** `ResponseTimeTracker.java`

Tracks performance metrics for all AI responses.

**Tracked metrics:**
- Average response time
- Fastest/slowest response
- Total successful responses
- Total failed responses
- Success rate percentage
- Per-player response times
- Token usage (for paid APIs)
- Most active player

**View stats:**
```
/aichat stats
```

**Example output:**
```
========== AI Chat Statistics ==========
Total Responses: 156
Failed Responses: 4
Success Rate: 97.5%
Average Response Time: 1.2s
Fastest Response: 0.3s
Slowest Response: 4.1s
Most Active Player: PlayerName (23 conversations)
Total Tokens Used: 45,230
```

### Chain Limiting
**File:** `ChainLimiter.java`

Prevents spam loops and excessive responses to same player.

**How it works:**
- Tracks consecutive responses per player
- Blocks responses after limit reached
- Resets after time window expires
- Default: 5 responses in 5 minutes

**Configuration:**
```json
{
  "chainLimit": 5,
  "chainResetMinutes": 5
}
```

**Check chain status:**
```java
ChainLimiter.getChainLength(playerName)  // Current chain length
ChainLimiter.getTimeUntilReset(playerName)  // Time until reset
```

### Emotion Detection
**File:** `EmotionDetector.java`

Analyzes message sentiment to adjust response tone.

**Detected emotions:**
-  **Happy** - joy, excitement, positive words
-  **Sad** - disappointment, sadness, crying
-  **Angry** - frustration, anger, insults
-  **Excited** - hype, OMG, multiple exclamation marks
-  **Confused** - questions, "huh?", uncertainty
-  **Grateful** - thanks, appreciation
-  **Worried** - anxiety, stress, concern
-  **Bored** - "meh", "whatever", disinterest

**How it works:**
- Uses regex patterns to match emotion keywords
- Counts emotion indicators in message
- Calculates intensity (0-10)
- Modifies AI prompt to acknowledge emotion

**Example:**
```
User: "I'm so sad, I just lost my game :("
AI detects: SAD emotion, intensity 6
AI response: "I sense you might be feeling down. Don't worry, you'll win the next one!"
```

### AFK Mode
**File:** `AFKManager.java`

Detects when you're away and adjusts responses.

**Features:**
- Auto-detection based on inactivity
- Manual toggle with `/aichat afk`
- Different personality when AFK
- Brief responses (1-5 words)
- Configurable timeout

**How it works:**
```java
// Auto-detects AFK after 5 minutes of inactivity
AFKManager.checkAFK()

// Updates on any activity (chat, movement, etc.)
AFKManager.updateActivity()

// Modifies AI prompt
String prompt = AFKManager.modifyPromptForAFK(originalPrompt)
```

**Configuration:**
```json
{
  "afkThresholdMinutes": 5,
  "autoAFK": true
}
```

### Learning System
**File:** `LearningSystem.java`

Allows AI to remember facts and player preferences.

**Features:**
- **Global facts** - Knowledge shared across all conversations
- **Player profiles** - Individual data per player
- **Nicknames** - Preferred names
- **Interests/dislikes** - Topics they like/dislike
- **Custom facts** - Any user-defined information
- **Interaction tracking** - Count of conversations

**Commands:**
```
/aichat learn server Hypixel           - Teach global fact
/aichat learn owner snow               - Another global fact
/aichat recall server                  - Shows "Hypixel"
/aichat forget server                  - Removes fact

/aichat nickname Nick                  - Set your nickname
/aichat interest skyblock             - Add interest
/aichat dislike lag                   - Add dislike
```

**How it works:**
```java
// Save fact
LearningSystem.learnFact("server", "Hypixel");

// Get fact
String server = LearningSystem.getFact("server");

// Set nickname
LearningSystem.setNickname("PlayerName", "Nick");

// Build context for AI
String context = LearningSystem.buildContextForPlayer("PlayerName");
// Returns: "Prefers to be called: Nick\nInterests: skyblock\nYou've talked 15 times before."
```

**Storage:**
All learning data saves to `config/learning.json` automatically.

## Learning System Deep Dive

### How the AI Learns Over Time

The learning system is the mod's memory - it allows the AI to remember facts, player preferences, and conversation history. This creates a more personalized and context-aware experience.

### Architecture

**Core File:** `LearningSystem.java`  
**Storage File:** `config/learning.json`  
**Integration:** Automatically loaded into every AI prompt

### Data Structure

The learning system stores two types of data:

#### 1. Global Facts
Universal knowledge shared across all conversations.

```json
{
  "facts": {
    "server": "Hypixel",
    "owner": "snow",
    "mod_version": "1.0",
    "favorite_game": "Bed Wars",
    "custom_key": "custom_value"
  }
}
```

**Use cases:**
- Server information
- Owner details
- Guild information
- Shared preferences
- Common knowledge

**Commands:**
```
/aichat learn <key> <value>   # Teach a fact
/aichat recall <key>           # Show fact value
/aichat forget <key>           # Remove fact
```

**Example:**
```
/aichat learn server Hypixel
/aichat learn guild MyGuild
/aichat recall server          # Shows: "Hypixel"
```

#### 2. Player Profiles
Individual memory for each player you interact with.

```json
{
  "profiles": {
    "PlayerName": {
      "preferredName": "Nick",
      "interests": ["bedwars", "skyblock", "pvp"],
      "dislikes": ["lag", "hackers"],
      "interactionCount": 42,
      "lastInteraction": 1700000000000,
      "customFacts": {
        "age": "15",
        "timezone": "EST",
        "rank": "VIP+"
      }
    }
  }
}
```

**Tracked data:**
- **Preferred nickname** - How they want to be called
- **Interests** - Topics they like discussing
- **Dislikes** - Topics they dislike
- **Interaction count** - Number of conversations
- **Last interaction** - Timestamp of last chat
- **Custom facts** - Any player-specific information

**Commands:**
```
/aichat nickname <name>        # Set your preferred name
/aichat interest <topic>       # Add interest
/aichat dislike <topic>        # Add dislike
```

### How Learning Enhances Conversations

#### Before Learning:
```
User: "Hey, what's the server name again?"
AI: "I'm not sure what server you're referring to. Can you provide more context?"
```

#### After Learning:
```
/aichat learn server Hypixel

User: "Hey, what's the server name again?"
AI: "The server is Hypixel! We discussed this before."
```

### Integration with AI Prompts

Every AI request automatically includes learned context:

```java
// Build context string
String context = LearningSystem.buildContextForPlayer("PlayerName");

// Example output:
"LEARNED FACTS:
- server: Hypixel
- owner: snow

PLAYER PROFILE (PlayerName):
- Prefers to be called: Nick
- Interests: bedwars, skyblock, pvp
- Dislikes: lag
- You've talked 42 times before
- Last talked: 2 hours ago"
```

This context is injected into every prompt, allowing the AI to:
- Use preferred nicknames naturally
- Reference shared knowledge
- Avoid disliked topics
- Build on previous conversations
- Understand relationship history

### Persistence & Saving

**Auto-save triggers:**
- After learning new fact
- After modifying profile
- After each conversation
- On mod shutdown
- Every 5 minutes (auto-backup)

**Manual save:**
```
/aichat memory save
```

**File location:** `config/learning.json`

### Memory Management

**View memory stats:**
```
/aichat memory stats
```

**Output:**
```
========== Memory Statistics ==========
Total learned facts: 12
Player profiles: 8
Total interactions: 156
Most active player: PlayerName (42 conversations)
Memory file size: 2.3 KB
Last saved: 5 minutes ago
```

**Clear memory:**
```
/aichat memory clear              # Clear ALL memory
/aichat memory clear PlayerName   # Clear specific player
```

### Advanced Learning Examples

#### Building a Guild Knowledge Base
```
/aichat learn guild MyGuild
/aichat learn guild_master GuildLeader123
/aichat learn guild_motto "Win every game!"
/aichat learn guild_members 150
```

Now the AI knows:
```
User: "What's our guild motto?"
AI: "Our guild motto is 'Win every game!' We have 150 members led by GuildLeader123."
```

#### Personalized Player Experience
```
# Player sets preferences
/aichat nickname Ace
/aichat interest skyblock
/aichat interest trading
/aichat dislike lag

# Later in conversation:
User: "What should I do?"
AI: "Hey Ace! Since you're into Skyblock and trading, maybe check out the auction house 
     for some deals? Just avoid laggy servers if possible!"
```

#### Teaching Game-Specific Knowledge
```
/aichat learn bedwars_tips "Rush with 40 iron, get sharpness sword"
/aichat learn skyblock_money "Farm sugarcane for easy coins"
/aichat learn best_pvp_mode "Duels for 1v1 practice"

# AI can now reference these:
User: "How do I make money in Skyblock?"
AI: "Farm sugarcane for easy coins! It's one of the most reliable methods."
```

### Learning System Best Practices

1. **Use clear, simple keys:**
   - ✅ Good: `server`, `guild`, `owner`
   - ❌ Bad: `the_server_we_play_on_most`

2. **Store factual information:**
   - ✅ Good: Names, numbers, dates, preferences
   - ❌ Bad: Opinions, rumors, unverified info

3. **Update outdated facts:**
   ```
   /aichat learn guild_members 200  # Updates from 150 to 200
   ```

4. **Use profiles for personal data:**
   - Store player-specific info in profiles, not global facts
   - Profiles are private per player

5. **Regular backups:**
   - `config/learning.json` contains all learned data
   - Back up this file to preserve memory

### Technical Implementation

**Loading on startup:**
```java
// In ChatHandler constructor
MemoryPersistence.loadMemory();
```

**Adding to AI context:**
```java
// In ChatHandler.onChatReceived
String learnedContext = LearningSystem.buildContextForPlayer(sender);
String fullPrompt = basePrompt + learnedContext;
aiService.generateResponse(fullPrompt, ...);
```

**Saving after interaction:**
```java
// After successful AI response
MemoryPersistence.addToMemory(sender, "assistant", response);
MemoryPersistence.saveMemory();
```

### Commands Summary

**Global Facts:**
- `/aichat learn <key> <value>` - Teach new fact
- `/aichat recall <key>` - Show fact
- `/aichat forget <key>` - Remove fact

**Player Profiles:**
- `/aichat nickname <name>` - Set preferred name
- `/aichat interest <topic>` - Add interest
- `/aichat dislike <topic>` - Add dislike

**Memory Management:**
- `/aichat memory save` - Manual save
- `/aichat memory stats` - View statistics
- `/aichat memory clear [player]` - Clear memory

**Learning Feature Toggle:**
```
/aichat set learning true   # Enable learning system
/aichat set learning false  # Disable (facts still stored but not used)
```

### Why Learning Matters

Without learning:
- AI forgets everything between conversations
- Repeats same questions
- No personalization
- Generic responses

With learning:
- ✅ Remembers your preferences
- ✅ Builds on past conversations
- ✅ Uses your nickname
- ✅ Knows shared context
- ✅ Avoids repeating information
- ✅ Creates continuity across sessions

The learning system transforms the AI from a stateless responder into a conversational partner with memory and context awareness.

---

## Moderation & Safety

### Spam Detection
**File:** `SpamDetector.java`

Prevents abuse and excessive usage.

**Features:**
- Detects duplicate messages
- Rate limiting per player
- Hourly response caps
- Cooldown between responses

**How it works:**
```java
// Check if spam
if (SpamDetector.isSpam(playerName, message)) {
    return; // Don't respond
}

// Check rate limit
if (!SpamDetector.canRespond(playerName)) {
    return; // Hit rate limit
}
```

### Chain Limiting
Prevents responding to same player too many times in a row (see Analytics section).

### Whitelist Mode
Only respond to approved friends (see Friend Management).

---

## File Structure

### Package: `com.aichat`
```
src/main/java/com/aichat/
├── AIChatMod.java              # Main mod entry point
├── AIChatCommand.java          # Command handler
├── ChatHandler.java            # Core event handler
│
├── ai/
│   ├── AIService.java          # AI service interface
│   ├── GeminiService.java      # Google Gemini (DEFAULT)
│   ├── OpenAIService.java      # OpenAI integration
│   ├── ClaudeService.java      # Claude integration
│   └── OllamaService.java      # Ollama integration
│
├── config/
│   └── ModConfig.java          # Configuration management
│
├── chat/
│   ├── ChatParser.java         # Parse Hypixel chat formats + death detection
│   ├── ChatChannel.java        # Channel types and formatting
│   └── ConversationManager.java # Context tracking
│
├── context/
│   ├── ConversationWindow.java # 5-minute conversation windows
│   └── MemoryPersistence.java  # Persistent memory across restarts
│
├── friends/
│   ├── FriendManager.java      # Friend list management
│   └── FriendRequestHandler.java # Auto-accept friend requests
│
├── hypixel/
│   ├── PartyManager.java       # Party management (invite/kick/warp)
│   ├── GameActionManager.java  # Game actions (play/lobby/warp)
│   ├── PartyLeaderAssistant.java # Smart party leader suggestions
│   └── SmartInvites.java       # Conversation-based invite detection
│
├── moderation/
│   ├── SpamDetector.java       # Spam detection
│   └── ChainLimiter.java       # Chain limit system
│
├── features/
│   ├── EmotionDetector.java    # Emotion analysis
│   ├── AFKManager.java         # AFK detection
│   ├── LearningSystem.java     # Learning & memory
│   ├── ResponseVariation.java  # Prevent duplicate responses
│   ├── StreamingResponse.java  # Word-by-word responses
│   ├── ChatBadges.java         # Status indicators
│   ├── TopicTracker.java       # Conversation topics
│   ├── RetryLogic.java         # Auto-retry failed requests
│   ├── ConversationStarter.java # Proactive conversation initiation
│   ├── ConfidenceTracker.java  # AI confidence scoring
│   └── RateLimitMonitor.java   # Rate limit warnings
│
├── analytics/
│   └── ResponseTimeTracker.java # Performance tracking
│
└── translation/
    └── TranslationService.java  # Multi-language support
```

### Configuration Files
```
config/
├── aichat.json       # Main configuration
├── learning.json     # Learned facts and profiles
├── memory.json       # Persistent conversation history
└── friends.json      # Friend list
```

---

## How It All Works Together

### Response Flow

1. **Message Received** (`ChatHandler.onChatReceived`)
   - Parse message to detect channel and sender
   - Check if message should trigger response

2. **Validation Checks**
   - SpamDetector: Is this spam?
   - ChainLimiter: Too many consecutive responses?
   - FriendManager: Is sender authorized?
   - AFKManager: Are we AFK?

3. **Context Building**
   - ConversationManager: Get chat history
   - EmotionDetector: Analyze sentiment
   - LearningSystem: Load player profile
   - TopicTracker: Identify conversation topic

4. **AI Request** (Async)
   - ResponseTimeTracker: Start timer
   - AIService: Send request to AI
   - RetryLogic: Retry if failed

5. **Response Processing**
   - ResponseVariation: Check if too similar to past responses
   - TranslationService: Translate if needed
   - ChatBadges: Add status indicator
   - StreamingResponse: Display word-by-word (if enabled)

6. **Send Response**
   - ChatChannel: Format with correct prefix (/gc, /pc, /r)
   - Send to Minecraft chat
   - Record in ConversationManager
   - Update analytics

### Example Flow
```
User in guild: "Hey snow, how do I get better at PvP?"

↓ ChatParser detects: GUILD channel, sender="User"
↓ Check spam: ✓ Not spam
↓ Check chain: ✓ Only 2 responses to this user
↓ Check friends: ✓ User is authorized
↓ Check AFK: ✓ Not AFK

↓ Load context: 3 previous messages with User
↓ Detect emotion: NEUTRAL
↓ Load profile: User likes pvp, talked 5 times before
↓ Track topic: GAMING

↓ Build prompt:
   "You are a friendly Minecraft AI. User asked: 'how do I get better at PvP?'
    LEARNED CONTEXT: User interests: pvp
    You've talked 5 times before.
    Topic: GAMING
    Respond in 20 words or less."

↓ Send to Ollama API (async)
↓ Retry logic active (max 3 attempts)
↓ Response received: "Practice your strafing and combo hitting! Watch PvP tutorials and..."

↓ Check variation: ✓ Not similar to past responses
↓ Add badge: [+] (enabled)
↓ Format: "/gc [AI] Practice your strafing and combo hitting!"

↓ Send to chat
↓ Record in context
↓ Update stats: +1 response, 1.2s response time
```

---

## Troubleshooting

### AI Not Responding
1. Check if enabled: `/aichat config`
2. Check chain limit: May have responded too many times
3. Check whitelist: Are you in friend list?
4. Check spam filter: Wait a few seconds and try again

### Slow Responses
1. Check AI service: Ollama may be slow on weak hardware
2. Check response time stats: `/aichat stats`
3. Try switching to OpenAI/Claude for faster responses

### Errors in Console
1. Check API keys in `config/aichat.json`
2. Verify Ollama is running (for Ollama service)
3. Check internet connection (for OpenAI/Claude)
4. Enable retry logic for auto-recovery

### Translation Not Working
1. Ensure `autoTranslate: true` in config
2. Check target language code (must be 2 letters)
3. LibreTranslate API must be accessible

---

## Performance Tips

1. **Use Ollama** for free, unlimited responses
2. **Lower contextMessages** to reduce token usage
3. **Enable chainLimit** to prevent spam loops
4. **Use whitelist mode** in crowded chats
5. **Disable streaming** for instant responses
6. **Set lower maxResponseWords** for faster generation

---

## Credits

**Author:** snow  
**Built with:** Minecraft Forge 1.8.9  
**AI Services:** OpenAI, Anthropic, Ollama  
**Libraries:** Gson for JSON handling

---

**End of Documentation**

For questions or issues, check the console output or edit `config/aichat.json` manually. 

For further erros or misunderstandings contact "aughsnow" on discord! 

Have fun with the Mod that I put my heart in. <3