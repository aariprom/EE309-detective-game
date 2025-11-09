# LLM Architecture Design

## Overview

This document explores two architectural approaches for LLM usage in the detective game: **Single LLM (Upfront Generation)** vs. **Chain of LLMs (Lazy Loading)**.

---

## Approach 1: Single LLM - Upfront Generation

### Architecture

```
User Input → Single LLM Call → Complete Game State
                              ↓
         [Characters, Places, Clues, Timeline, 
          All Dialogue, All Descriptions]
```

### How It Works

1. **Initial Generation (Single Large Call)**
   - User provides game content/keywords
   - Single comprehensive LLM call generates:
     - All characters with full details
     - All places with full descriptions
     - All clues with complete information
     - Complete timeline with all events
     - Pre-generated dialogue for all possible conversations
     - All place descriptions and character appearances
     - All possible action outcomes

2. **Game Runtime**
   - Game engine reads from pre-generated data
   - No LLM calls during gameplay
   - Fast response times
   - Deterministic experience

### Pros

✅ **Fast Gameplay**: No waiting for LLM responses during play  
✅ **Consistent Experience**: Same game state every time (replayable)  
✅ **Lower API Costs**: Single LLM call at game start  
✅ **Offline Capability**: Once generated, can play without internet  
✅ **Predictable**: Easier to debug and test  
✅ **Complete Context**: LLM has full game picture when generating  

### Cons

❌ **Large Initial Call**: May hit token limits or be very expensive  
❌ **Storage Requirements**: Need to store all pre-generated content  
❌ **Less Dynamic**: Cannot adapt to unexpected player actions  
❌ **Memory Limitations**: May forget details across the entire game  
❌ **Rigid**: Pre-generated dialogue may feel unnatural  
❌ **Initial Wait Time**: Player waits for long generation at start  

---

## Approach 2: Chain of LLMs - Lazy Loading

### Architecture

```
User Input → LLM 1 (Initializer) → Core Game Structure
                                    ↓
                    [Characters, Places, Clues, Timeline - Skeleton Only]
                                    ↓
                    ┌───────────────┴───────────────┐
                    ↓                               ↓
            LLM 2 (Dialogue)              LLM 3 (Descriptions)
            On-demand conversations      On-demand place/character details
                    ↓                               ↓
            LLM 4 (Action Handler)        LLM 5 (Component Updater)
            Validates & executes          Updates game state dynamically
```

### How It Works

1. **Initial Generation (LLM 1 - Initializer)**
   - User provides game content/keywords
   - **Skeleton Generation**: Creates structured game framework
     - Characters (names, traits, is_criminal, basic info)
     - Places (names, basic traits, connections)
     - Clues (structured data: who, what, when, where, why)
     - Timeline (event schedule with structured data)
   - **NO** full dialogue, descriptions, or detailed content

2. **Runtime Generation (Lazy Loading)**
   - **LLM 2 - Dialogue Generator**: When player questions a character
     - Generates conversation on-the-fly
     - Uses character's traits, known_clues, mental_state
     - Context-aware (time, player's clues, cooperation level)
     - Extracts clues from conversation
   
   - **LLM 3 - Description Generator**: When player investigates a place or views character
     - Generates place description with current state
     - Generates character appearance/expression
     - Context-aware (time, events, timeline)
   
   - **LLM 4 - Action Handler**: When player performs free-form action
     - Validates action feasibility
     - Generates action outcome
     - Updates game state
   
   - **LLM 5 - Component Updater**: When timeline events trigger
     - Updates character states
     - Updates place states
     - Generates narrative for events
     - Updates clue availability

### Pros

✅ **Dynamic & Adaptive**: Content generated based on player's actual path  
✅ **Natural Conversations**: Dialogue feels more organic and responsive  
✅ **Lower Initial Cost**: Small initial generation, pay-as-you-go  
✅ **Memory Efficient**: Each call focuses on specific context  
✅ **Flexible**: Can handle unexpected player actions  
✅ **No Token Limits**: Each call is focused and small  
✅ **Better Context**: Each LLM call has specific, focused context  

### Cons

❌ **Slower Gameplay**: Wait times for LLM responses during play  
❌ **Higher Total API Costs**: Many LLM calls throughout game  
❌ **Requires Internet**: Constant connectivity needed  
❌ **Inconsistent**: Same action might produce different results  
❌ **Complex State Management**: Need to track what's been generated  
❌ **Potential Inconsistencies**: Different LLM calls might contradict  

---

## Approach 3: Hybrid Approach (Recommended)

### Architecture

```
User Input → LLM 1 (Initializer) → Core Game Structure
                                    ↓
                    [Characters, Places, Clues, Timeline - Full Structure]
                                    ↓
                    ┌───────────────┴───────────────┐
                    ↓                               ↓
            Pre-generate Some              Lazy Load Dynamic
            (Templates, Base)              (Dialogue, Adaptations)
```

### How It Works

1. **Initial Generation (LLM 1 - Comprehensive Initializer)**
   - Generate complete game structure:
     - **Characters**: Full details (traits, is_criminal, known_clues, mental_state, items)
     - **Places**: Full details (traits, available_clues, items, connections)
     - **Clues**: Complete structured data (who, what, when, where, why, unlock_conditions)
     - **Timeline**: Complete event schedule with structured data
   - **NOT** generating:
     - Full dialogue (too much, too rigid)
     - Detailed descriptions (better when contextual)

2. **Runtime Generation (Lazy Loading for Dynamic Content)**
   - **LLM 2 - Dialogue Generator**: Generate conversations on-demand
   - **LLM 3 - Description Generator**: Generate contextual descriptions
   - **LLM 4 - Action Handler**: Handle free-form actions
   - **LLM 5 - Component Updater**: Update states based on timeline/actions

3. **Caching Strategy**
   - Cache generated dialogue for same character + context
   - Cache descriptions for same place + time state
   - Reuse when possible, regenerate when context changes

### Pros

✅ **Best of Both Worlds**: 
   - Complete game structure upfront (no gaps)
   - Dynamic content when needed (natural feel)
   
✅ **Balanced Costs**: 
   - Initial generation is manageable
   - Runtime calls are focused and necessary
   
✅ **Predictable Core**: 
   - Game structure is consistent
   - Dynamic content adds flavor
   
✅ **Good Performance**: 
   - Most content is cached/pre-generated
   - Only dynamic interactions need LLM calls

### Cons

⚠️ **Moderate Complexity**: Need to manage both upfront and lazy generation  
⚠️ **Moderate Costs**: Balance between approaches  

---

## Detailed Comparison

### Initial Generation Scope

| Content Type | Single LLM | Chain LLM | Hybrid |
|--------------|------------|-----------|--------|
| **Characters (Structure)** | Full | Skeleton | Full |
| **Places (Structure)** | Full | Skeleton | Full |
| **Clues (Structure)** | Full | Skeleton | Full |
| **Timeline** | Full | Skeleton | Full |
| **Character Dialogue** | Pre-generated | None | None |
| **Place Descriptions** | Pre-generated | None | None |
| **Action Outcomes** | Pre-generated | None | None |

### Runtime LLM Calls

| Action | Single LLM | Chain LLM | Hybrid |
|--------|------------|-----------|--------|
| **Question Character** | 0 (pre-gen) | 1 (LLM 2) | 1 (LLM 2, cached) |
| **Investigate Place** | 0 (pre-gen) | 1 (LLM 3) | 1 (LLM 3, cached) |
| **Free Action** | 0 (pre-gen) | 1 (LLM 4) | 1 (LLM 4) |
| **Timeline Event** | 0 (pre-gen) | 1 (LLM 5) | 1 (LLM 5) |
| **Component Update** | 0 (pre-gen) | 1 (LLM 5) | 1 (LLM 5) |

### Cost Estimation (Example: 1-hour game)

**Single LLM:**
- Initial: 1 large call (~50k tokens) = ~$0.50-1.00
- Runtime: $0
- **Total: ~$0.50-1.00**

**Chain LLM:**
- Initial: 1 small call (~5k tokens) = ~$0.05-0.10
- Runtime: ~50-100 calls (~2k tokens each) = ~$0.50-2.00
- **Total: ~$0.55-2.10**

**Hybrid:**
- Initial: 1 medium call (~20k tokens) = ~$0.20-0.40
- Runtime: ~30-50 calls (~2k tokens each) = ~$0.30-1.00
- **Total: ~$0.50-1.40**

*Note: Costs are rough estimates and vary by provider/model*

---

## Recommendation: Hybrid Approach

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
   - Not too slow (caching helps)
   - Not too rigid (dynamic content)

### Implementation Strategy

```
┌─────────────────────────────────────────────────┐
│ Initial Game Setup (LLM 1)                      │
│ - Generate complete game structure               │
│ - All characters, places, clues, timeline        │
│ - Store in game state                            │
└─────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────┐
│ Runtime Gameplay                                 │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │ LLM 2: Dialogue Generator                 │  │
│  │ - Called when player questions character  │  │
│  │ - Input: character data, player clues,    │  │
│  │          context, player question          │  │
│  │ - Output: dialogue response, new clues    │  │
│  │ - Cache: same character + same context    │  │
│  └──────────────────────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │ LLM 3: Description Generator              │  │
│  │ - Called when player investigates place   │  │
│  │ - Input: place data, current time,       │  │
│  │          timeline events, player clues    │  │
│  │ - Output: place description, clues found  │  │
│  │ - Cache: same place + same time state    │  │
│  └──────────────────────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │ LLM 4: Action Handler                    │  │
│  │ - Called when player performs free action│  │
│  │ - Input: action description, game state,  │  │
│  │          current location, player tools   │  │
│  │ - Output: action validation, outcome,     │  │
│  │          state changes                    │  │
│  │ - Cache: rarely (actions are unique)     │  │
│  └──────────────────────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │ LLM 5: Component Updater                 │  │
│  │ - Called when timeline event triggers    │  │
│  │ - Input: timeline event, current state   │  │
│  │ - Output: updated components, narrative  │  │
│  │ - Cache: same event + same state         │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
```

---

## Technical Considerations

### LLM Selection

All approaches can use the same LLM provider, but consider:

- **Initial Generation**: May need larger context window (GPT-4, Claude)
- **Runtime Generation**: Can use faster/cheaper models (GPT-3.5, Claude Haiku)
- **Consistency**: Using same model family helps consistency

### Prompt Engineering

- **Initial Generator**: Needs structured output (JSON schema, function calling)
- **Dialogue Generator**: Needs personality consistency, context awareness
- **Description Generator**: Needs visual detail, contextual relevance
- **Action Handler**: Needs validation logic, game state awareness

### Caching Strategy

- **What to Cache**: Dialogue, descriptions, action outcomes
- **Cache Key**: Character/Place + Context (time, player clues, etc.)
- **Cache Invalidation**: When context changes significantly
- **Storage**: In-memory for session, persistent for save games

### Error Handling

- **LLM Failures**: Retry logic, fallback responses
- **Invalid Output**: Validation, default responses
- **Rate Limiting**: Queue system, user feedback
- **Timeout**: Progress indicators, cancellation options

---

## Conclusion

**Recommended: Hybrid Approach**

- Generate complete game structure upfront (characters, places, clues, timeline)
- Generate dynamic content on-demand (dialogue, descriptions, actions)
- Use caching to improve performance and reduce costs
- Balance between consistency and dynamism

This provides the best user experience while managing costs and complexity effectively.

