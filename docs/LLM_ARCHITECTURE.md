# LLM Architecture Design

## Overview

This document describes the **Hybrid LLM Architecture** chosen for the detective game. This approach combines upfront generation of complete game structure with on-demand generation of dynamic content, providing the best balance of consistency, performance, and user experience.

---

## Chosen Architecture: Hybrid Approach

### Why Hybrid?

1. **Game Structure Needs to be Complete**
   - Characters, places, clues, timeline must be fully defined upfront
   - Player needs to know what's available
   - Game logic depends on complete structure

2. **Dynamic Content Benefits from Context**
   - Dialogue is better when generated with current context
   - Descriptions are better when contextual (time, events)
   - Actions are unpredictable and need runtime generation

3. **Balanced User Experience**
   - Player sees game structure immediately
   - Conversations feel natural and responsive
   - Not too slow (focused runtime calls)
   - Not too rigid (dynamic content)

---

## Architecture Overview

```
User Input → LLM 1 (Initializer) → Core Game Structure
                                    ↓
                    [Characters, Places, Clues, Timeline - Full Structure]
                                    ↓
                    ┌───────────────┴───────────────┐
                    ↓                               ↓
            Pre-generate Structure      Lazy Load Dynamic Content
            (Complete game data)        (Dialogue, Descriptions, Actions)
```

---

## Implementation Details

### Phase 1: Initial Generation (LLM 1 - Comprehensive Initializer)

**When**: Game start, before gameplay begins

**Purpose**: Generate complete game structure from user input/keywords

**Input**:
- User-provided game content/keywords
- Game configuration (difficulty, cooperation level, etc.)

**Output**: Complete game structure:
- **Characters**: Full details (name, traits, is_criminal, known_clues, mental_state, items, unlock_conditions, location)
- **Places**: Full details (name, traits, available_clues, items, connections, unlock_conditions)
- **Clues**: Complete structured data (who, what, when, where, why, unlock_conditions)
- **Timeline**: Complete event schedule with structured data (events, triggers, effects)

**NOT Generating**:
- Full dialogue (too much, too rigid)
- Detailed descriptions (better when contextual)

**Technical Requirements**:
- Use structured output (JSON schema or function calling)
- Ensure specificity and avoid generic responses
- Validate output structure and completeness

**Estimated Cost**: ~20k tokens = ~$0.20-0.40 per game

---

### Phase 2: Initial Intro Generation

#### LLM 2: Intro Generator

**When**: After LLM 1 generates GameState, before game starts

**Purpose**: Generate compelling, spoiler-free introduction text

**Input**:
- Public information from GameState (title, description, characters, places, timeline)
- No spoilers (isCriminal, hidden clues, etc. are excluded)

**Output**:
- Introduction text (3-7 paragraphs)
- Sets the scene, introduces incident, presents suspects and locations
- Explains player's role and objective
- Ends with a hook to motivate investigation

**Format**: JSON with `{"text": "..."}` field

**Estimated Cost**: ~2k tokens per call = ~$0.01-0.02 per game

---

### Phase 3: Runtime Generation (Lazy Loading)

#### LLM 3: Dialogue Generator

**When**: Player questions/interrogates a character

**Purpose**: Generate natural, context-aware conversations

**Input**:
- Character data (traits, known_clues, mental_state, location)
- Player's collected clues
- Current game time
- Player's question/topic
- Cooperation level (game setting)

**Output**:
- Character dialogue response
- Potential new clues (extracted from conversation via structured output)
- Character emotional state update (optional)

**Estimated Cost**: ~2k tokens per call = ~$0.01-0.02 per conversation

---

#### LLM 4: Description Generator

**When**: Player investigates a place or views a character

**Purpose**: Generate contextual descriptions based on current game state

**Input**:
- Place/Character data
- Current game time
- Timeline events (past and upcoming)
- Player's collected clues
- Recent player actions

**Output**:
- Place description (appearance, atmosphere, details)
- Character appearance/expression (if viewing character)
- Potential clues found (if investigating place)
- Event descriptions (if timeline events occurred)

**Estimated Cost**: ~2k tokens per call = ~$0.01-0.02 per investigation

---

#### Epilogue Generator

**When**: Game ends (win or lose)

**Purpose**: Generate epilogue text summarizing the game outcome

**Input**:
- Game state (characters, clues, timeline, outcome)
- Player's actions and collected clues
- Win/lose condition

**Output**:
- Epilogue text (narrative summary of the case resolution)

**Estimated Cost**: ~1.2k tokens per call = ~$0.01 per game end

---

**Note**: LLM 5 (Action Handler) and LLM 6 (Component Updater) are not implemented. The game uses predefined actions only, and timeline events are processed with basic logic (no LLM-based updates).

---

## Caching Strategy

**Status**: ❌ Not Implemented

**Reason**: Caching is inefficient for this game architecture. Each LLM call has unique context:
- Different game time
- Different player clues collected
- Different questions asked
- Different timeline events

Since prompts are unique per context, cache hit rate would be very low, making caching overhead not worthwhile.

**Note**: If save/load system is implemented, game state will be persisted, but LLM responses will not be cached.

---

## Technical Considerations

### LLM Selection

**Recommended: Upstage API (Solar LLM family)**

- **LLM 1 (Initializer)**: Use larger model (Solar Pro) for complete structure generation
- **LLM 2 (Intro Generator)**: Use larger model (Solar Pro) for quality intro text
- **LLM 3-4 (Runtime)**: Use larger model (Solar Pro) for quality dialogue and descriptions
- **Epilogue Generator**: Use larger model (Solar Pro) for quality epilogue text
- **Consistency**: Using same model family helps maintain consistency

**Alternatives**: OpenAI GPT-3.5-turbo/GPT-4, Anthropic Claude (fallback)

### Prompt Engineering

#### LLM 1: Initial Content Generator
- **Focus**: Structured output, completeness, specificity
- **Format**: JSON schema or function calling
- **Key**: Avoid generic responses, ensure all fields populated

#### LLM 2: Intro Generator
- **Focus**: Compelling narrative, spoiler-free, sets the scene
- **Format**: JSON with intro text
- **Key**: No spoilers, equal treatment of suspects, engaging hook

#### LLM 3: Dialogue Generator
- **Focus**: Personality consistency, context awareness, natural flow
- **Format**: JSON with dialogue and extracted clues
- **Key**: Maintain character voice, respond to player's clues

#### LLM 4: Description Generator
- **Focus**: Visual detail, contextual relevance, atmosphere
- **Format**: JSON with description and found clues
- **Key**: Reflect time, events, and player's knowledge

#### Epilogue Generator
- **Focus**: Narrative summary, case resolution, satisfying conclusion
- **Format**: JSON with epilogue text
- **Key**: Summarize case, acknowledge player's investigation, provide closure

### Error Handling

#### LLM Failures
- **Retry Logic**: 3 retries with exponential backoff
- **Fallback Responses**: Default responses if LLM fails
- **User Feedback**: Show loading indicators, error messages

#### Invalid Output
- **Validation**: Check output structure and required fields
- **Default Values**: Use sensible defaults for missing fields
- **Logging**: Log invalid outputs for debugging

#### Rate Limiting
- **Queue System**: Queue requests if rate limited
- **User Feedback**: Show "processing" indicators
- **Retry**: Automatic retry after rate limit window

#### Timeout
- **Timeout**: 30 seconds per LLM call
- **Progress Indicators**: Show loading state
- **Cancellation**: Allow user to cancel long operations

---

## Cost Estimation

### Per Game Session (1-hour game)

**Initial Generation (LLM 1)**:
- ~20k tokens = ~$0.20-0.40

**Intro Generation (LLM 2)**:
- ~2k tokens = ~$0.01-0.02 per game

**Runtime Generation (LLM 3-4)**:
- ~20-40 calls × ~2k tokens each = ~$0.20-0.80
- Dialogue calls: ~15-25 per game
- Description calls: ~5-15 per game

**Epilogue Generation**:
- 1 call × ~1.2k tokens = ~$0.01 per game

**Total Estimated Cost**: ~$0.40-1.20 per game session

*Note: Costs are rough estimates and vary by provider/model. No caching system - all calls are unique per context.*

---

## Implementation Flow

```
┌─────────────────────────────────────────────────┐
│ Initial Game Setup (LLM 1)                      │
│ - User provides game content/keywords            │
│ - Generate complete game structure               │
│ - All characters, places, clues, timeline        │
│ - Store in game state                            │
│ - Validate structure completeness                │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ Runtime Gameplay                                 │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │ LLM 3: Dialogue Generator                 │  │
│  │ - Called when player questions character  │  │
│  │ - Input: character data, player clues,    │  │
│  │          context, player question          │  │
│  │ - Output: dialogue response, new clues    │  │
│  └──────────────────────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │ LLM 4: Description Generator              │  │
│  │ - Called when player investigates place   │  │
│  │ - Input: place data, current time,       │  │
│  │          timeline events, player clues    │  │
│  │ - Output: place description, clues found  │  │
│  └──────────────────────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │ Timeline Event Processing                 │  │
│  │ - Basic logic (no LLM)                    │  │
│  │ - Character movement, place changes       │  │
│  │ - Flag updates                            │  │
│  └──────────────────────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │ Epilogue Generator                        │  │
│  │ - Called when game ends (win/lose)        │  │
│  │ - Input: game state, outcome              │  │
│  │ - Output: epilogue text                   │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

---

## Alternatives Considered

### Approach 1: Single LLM - Upfront Generation

**How It Works**: Single comprehensive LLM call generates everything upfront (including all dialogue and descriptions).

**Pros**:
- Fast gameplay (no waiting during play)
- Consistent experience
- Lower total API costs
- Offline capability

**Cons**:
- Large initial call (may hit token limits)
- Less dynamic (cannot adapt to player actions)
- Rigid dialogue (pre-generated feels unnatural)
- Initial wait time

**Why Not Chosen**: Too rigid, cannot adapt to player's actual path, dialogue feels unnatural.

---

### Approach 2: Chain of LLMs - Pure Lazy Loading

**How It Works**: Minimal initial generation (skeleton only), all content generated on-demand.

**Pros**:
- Dynamic and adaptive
- Natural conversations
- Lower initial cost
- Flexible

**Cons**:
- Slower gameplay (wait times during play)
- Higher total API costs
- Requires constant internet
- Potential inconsistencies

**Why Not Chosen**: Game structure needs to be complete upfront, too many runtime calls, slower user experience.

---

## Conclusion

The **Hybrid Approach** provides the best balance for the detective game:

- ✅ **Complete game structure** upfront (no gaps, predictable core)
- ✅ **Dynamic content** on-demand (natural feel, contextual)
- ✅ **Balanced costs** (manageable initial + focused runtime calls)
- ✅ **Simplified architecture** (no caching complexity, no free actions)
- ✅ **Playable and fun** (predefined actions are sufficient for engaging gameplay)

**Simplified Features**:
- ❌ No caching system (prompts are unique per context)
- ❌ No free-form actions (predefined actions only)
- ❌ No LLM-based component updates (basic timeline processing sufficient)

This architecture ensures a consistent, engaging user experience while keeping complexity manageable and the game fully playable.
