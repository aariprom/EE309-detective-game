# Task Summary - Quick Reference

## LLM Architecture: Hybrid Approach

The project uses a **Hybrid LLM Architecture**:
- **LLM 1 (Initializer)**: Generates complete game structure upfront (characters, places, clues, timeline)
- **LLM 2-5 (Runtime)**: Generate dynamic content on-demand (dialogue, descriptions, actions, updates)
- **Caching**: Cache generated content to improve performance and reduce costs

See [LLM_ARCHITECTURE.md](./LLM_ARCHITECTURE.md) for detailed architecture documentation.

---

## Module Breakdown

| Module | Priority | Difficulty | Main Components |
|--------|----------|------------|----------------|
| **1. Core Game Engine & State Management** | HIGH | MEDIUM | State models, Time system, Game flow, Validation |
| **2. LLM Integration Layer (Hybrid)** | HIGH | MEDIUM-HIGH | API client, LLM 1-5, Caching, Clue extraction |
| **3. Game Content System** | HIGH | MEDIUM | Characters, Places, Clues, Timeline, Flags |
| **4. Android UI/UX Components** | HIGH | MEDIUM | Game screens, Action UI, Conversation UI, Configuration |
| **5. Game Logic & Validation** | HIGH | MEDIUM-HIGH | Action handlers, Timeline processing, Win/lose conditions |

---

## Detailed Task List

### Module 1: Core Game Engine & State Management

| Task | Priority | Difficulty | Feasibility | Dependencies |
|------|----------|------------|-------------|--------------|
| 1.1 Game State Data Models | HIGH | MEDIUM | ✅ POSSIBLE | - |
| 1.2 Time Management System | HIGH | MEDIUM | ✅ POSSIBLE | 1.1 |
| 1.3 Game Flow Controller | HIGH | MEDIUM | ✅ POSSIBLE | 1.1, 1.2 |
| 1.4 Action Validation System | HIGH | HIGH | ⚠️ CHALLENGING | 1.1, 2.1 |

### Module 2: LLM Integration Layer (Hybrid Architecture)

| Task | Priority | Difficulty | Feasibility | Dependencies |
|------|----------|------------|-------------|--------------|
| 2.1 LLM API Client | HIGH | LOW-MEDIUM | ✅ POSSIBLE | - |
| 2.2 LLM 1: Initial Content Generator | HIGH | MEDIUM | ✅ POSSIBLE | 2.1, 3.1, 3.2, 3.3 |
| 2.3 LLM 2: Dialogue Generator | HIGH | MEDIUM-HIGH | ✅ POSSIBLE | 2.1, 3.1, 2.8 |
| 2.4 LLM 3: Description Generator | MEDIUM | MEDIUM | ✅ POSSIBLE | 2.1, 2.8 |
| 2.5 LLM 4: Action Handler | MEDIUM | HIGH | ⚠️ CHALLENGING | 2.1, 1.4 |
| 2.6 LLM 5: Component Updater | HIGH | MEDIUM-HIGH | ✅ POSSIBLE | 2.1, 3.1, 3.2, 3.3, 2.8 |
| 2.7 Clue Extraction System | MEDIUM | MEDIUM-HIGH | ⚠️ CHALLENGING | 2.1, 3.3 |
| 2.8 LLM Response Caching System | MEDIUM | MEDIUM | ✅ POSSIBLE | 2.1 |

### Module 3: Game Content System

| Task | Priority | Difficulty | Feasibility | Dependencies |
|------|----------|------------|-------------|--------------|
| 3.1 Character Management | HIGH | MEDIUM | ✅ POSSIBLE | - |
| 3.2 Place Management | HIGH | MEDIUM | ✅ POSSIBLE | - |
| 3.3 Clue Management | HIGH | MEDIUM | ✅ POSSIBLE | - |
| 3.4 Timeline System | HIGH | MEDIUM-HIGH | ✅ POSSIBLE | 1.2 |
| 3.5 Flag System | MEDIUM | LOW-MEDIUM | ✅ POSSIBLE | 1.1 |

### Module 4: Android UI/UX Components

| Task | Priority | Difficulty | Feasibility | Dependencies |
|------|----------|------------|-------------|--------------|
| 4.1 Main Game Screen Layout | HIGH | MEDIUM | ✅ POSSIBLE | 1.1 |
| 4.2 Text Display System | HIGH | LOW-MEDIUM | ✅ POSSIBLE | - |
| 4.3 Action Selection UI | HIGH | MEDIUM | ✅ POSSIBLE | 4.1 |
| 4.4 Free Action Input System | MEDIUM | MEDIUM-HIGH | ✅ POSSIBLE | 4.1 |
| 4.5 Character & Place Selection UI | HIGH | MEDIUM | ✅ POSSIBLE | 3.1, 3.2, 4.1 |
| 4.6 Conversation/Interrogation UI | HIGH | MEDIUM | ✅ POSSIBLE | 2.3, 4.1 |
| 4.7 Game Start & Configuration | MEDIUM | LOW-MEDIUM | ✅ POSSIBLE | 2.2 |
| 4.8 Tutorial Screen | MEDIUM | LOW | ✅ POSSIBLE | - |
| 4.9 Game Over Screen | MEDIUM | LOW-MEDIUM | ✅ POSSIBLE | 5.7 |
| 4.10 Save/Load System UI | LOW | LOW-MEDIUM | ✅ POSSIBLE | 1.3 |

### Module 5: Game Logic & Validation

| Task | Priority | Difficulty | Feasibility | Dependencies |
|------|----------|------------|-------------|--------------|
| 5.1 Investigation Handler | HIGH | MEDIUM | ✅ POSSIBLE | 3.2, 3.3, 1.2, 2.4, 2.8 |
| 5.2 Questioning Handler | HIGH | MEDIUM-HIGH | ✅ POSSIBLE | 2.3, 3.1, 3.3, 2.7, 2.8 |
| 5.3 Movement Handler | HIGH | LOW-MEDIUM | ✅ POSSIBLE | 3.2, 1.2 |
| 5.4 Accusation Handler | HIGH | MEDIUM-HIGH | ✅ POSSIBLE | 3.1, 3.3 |
| 5.5 Free Action Handler | MEDIUM | HIGH | ⚠️ CHALLENGING | 1.4, 2.5, 2.8 |
| 5.6 Timeline Event Processor | HIGH | MEDIUM-HIGH | ✅ POSSIBLE | 3.4, 2.6, 2.8 |
| 5.7 Win/Lose Condition Checker | HIGH | MEDIUM | ✅ POSSIBLE | 5.4, 1.2 |
| 5.8 Component Update Logic | HIGH | MEDIUM | ✅ POSSIBLE | 3.1, 3.2, 3.3, 2.6, 2.8 |

---

## Feasibility Legend

- ✅ **POSSIBLE**: Standard implementation, well-understood patterns
- ⚠️ **CHALLENGING**: Requires careful design, may need iteration or alternative approaches

---

## Critical Path (Minimum Viable Product)

To build a playable game, these tasks must be completed in order:

1. **Foundation** (Week 1)
   - 1.1 Game State Data Models
   - 1.2 Time Management System
   - 2.1 LLM API Client
   - 3.1, 3.2, 3.3 Character/Place/Clue Management

2. **Content Generation (Hybrid Approach)** (Week 2)
   - **2.2 LLM 1: Initial Content Generator** - Upfront generation (complete structure)
   - 3.4 Timeline System
   - **2.8 LLM Response Caching System** - Performance optimization

3. **Runtime LLMs & Game Logic** (Week 3)
   - **2.3 LLM 2: Dialogue Generator** - Lazy loading for conversations
   - **2.4 LLM 3: Description Generator** - Lazy loading for descriptions
   - **2.6 LLM 5: Component Updater** - Timeline-based updates
   - 2.7 Clue Extraction System
   - 1.3 Game Flow Controller
   - 5.1, 5.2, 5.3, 5.4 Core Action Handlers
   - 5.6 Timeline Event Processor
   - 5.7 Win/Lose Conditions
   - 5.8 Component Updates

4. **UI Integration** (Week 4)
   - 4.1 Main Game Screen
   - 4.2 Text Display
   - 4.3 Action Selection
   - 4.5 Character/Place Selection
   - 4.6 Conversation UI (Integration with LLM 2)

5. **Advanced Features** (Week 5+)
   - **2.5 LLM 4: Action Handler** - Free action system
   - 4.7 Game Configuration
   - 1.4, 5.5 Free Action System (if time permits)

---

## Risk Areas

### High Risk (Requires Careful Planning)
- **Action Validation (1.4, 5.5)**: Balancing flexibility with game integrity
- **LLM 4: Action Handler (2.5)**: Balancing flexibility with game integrity
- **Clue Extraction (2.7)**: Ensuring reliable structured output from LLM
- **Timeline Event Processing (5.6)**: Managing complex state updates

### Medium Risk
- **LLM Response Quality**: Ensuring consistent, game-appropriate responses across LLM 2-5
- **Caching Strategy (2.8)**: Managing cache invalidation correctly
- **Component Updates (2.6, 5.8)**: Maintaining state consistency across updates
- **Free Action System**: Preventing exploits while maintaining fun

### Low Risk
- **UI Components**: Standard Android development
- **Data Models**: Straightforward data structures
- **Basic Action Handlers**: Clear logic flow

---

## Estimated Timeline

- **Minimum Viable Product**: 4-5 weeks
- **Full Feature Set**: 6-8 weeks
- **Polished Release**: 8-10 weeks

*Note: Timeline assumes 1 developer working part-time. Adjust based on team size and availability.*

