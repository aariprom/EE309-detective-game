package com.ee309.detectivegame.llm.config

object LLMSchema {
    // LLM 1: Game Initialize

    // currentTime is not set here
    // player.name, collectedClues, flags are not set here
    // character.knownClues, mentalState, hidden, items are not set here
    // place.hidden is not set here

    object GameInitializer {
        val SCHEMA = """
            {
              "name": "detective_scenario",
              "strict": true,
              "schema": {
                "type": "object",
                "additionalProperties": false,
                "properties": {
                  "title": {
                    "type": "string",
                    "description": "A short, descriptive title of the detective case."
                  },
                  "description": {
                    "type": "string",
                    "description": "A brief overview of the story, setting, and incident."
                  },
                  "phase": {
                    "type": "string",
                    "description": "Current phase of the scenario. Set to INTRODUCTION by default.",
                    "enum": ["START", "TUTORIAL", "INTRODUCTION", "INVESTIGATION", "GAME_OVER", "WIN", "LOSE"]
                  },
                  "player": {
                    "type": "object",
                    "additionalProperties": false,
                    "description": "Represents the player state at the start of the scenario.",
                    "properties": {
                      "currentLocation": {
                        "type": "string",
                        "description": "ID of the Place where the player currently is."
                      },
                      "tools": {
                        "type": "array",
                        "description": "List of items currently held by the player.",
                        "items": {
                          "type": "string"
                        }
                      }
                    },
                    "required": ["currentLocation", "tools"]
                  },
                  "characters": {
                    "type": "array",
                    "description": "List of all characters involved in the case.",
                    "items": {
                      "type": "object",
                      "additionalProperties": false,
                      "properties": {
                        "id": {
                          "type": "string",
                          "description": "Unique ID of the character."
                        },
                        "name": {
                          "type": "string",
                          "description": "Display name of the character."
                        },
                        "traits": {
                          "type": "array",
                          "description": "List of traits that the character possesses.",
                          "items": {
                            "type": "string"
                          }
                        },
                        "initialLocation": {
                          "type": "string",
                          "description": "ID of a Place where the character starts."
                        },
                        "isCriminal": {
                          "type": "boolean",
                          "description": "True if this character is the criminal; false otherwise."
                        },
                        "unlockConditions": {
                          "type": "array",
                          "description": "List of flag IDs required.",
                          "items": {
                            "type": "string"
                          }
                        }
                      },
                      "required": ["id", "name", "description", "initialLocation", "isCriminal", "unlockConditions"]
                    }
                  },
                  "places": {
                    "type": "array",
                    "description": "List of locations that exist in the scenario.",
                    "items": {
                      "type": "object",
                      "additionalProperties": false,
                      "properties": {
                        "id": {
                          "type": "string",
                          "description": "Unique ID of the place."
                        },
                        "name": {
                          "type": "string",
                          "description": "Display name of the place."
                        },
                        "description": {
                          "type": "string",
                          "description": "What the place looks like, its purpose, and notable details."
                        },
                        "availableClues": {
                          "type": "array",
                          "description": "List of Clue IDs that the player can discover at this place. Can be empty",
                          "items": {
                            "type": "string"
                          }
                        },
                        "unlockConditions": {
                          "type": "array",
                          "description": "List of flag IDs required.",
                          "items": {
                            "type": "string"
                          }
                        },
                        "connections": {
                          "type": "array",
                          "description": "List of Place IDs that this place connects to.",
                          "items": {
                            "type": "string"
                          }
                        }
                      },
                      "required": ["id", "name", "description", "availableClues", "unlockConditions", "connections"]
                    }
                  },
                  "clues": {
                    "type": "array",
                    "description": "List of clues that the player can discover.",
                    "items": {
                      "type": "object",
                      "additionalProperties": false,
                      "properties": {
                        "id": {
                          "type": "string",
                          "description": "Unique ID of the clue."
                        },
                        "name": {
                          "type": "string",
                          "description": "Display name of the clue."
                        },
                        "description": {
                          "type": "string",
                          "description": "Description of what the clue is and what it suggests."
                        },
                        "location": {
                          "type": "string",
                          "description": "ID of a Place or Character where this clue is located."
                        },
                        "unlockConditions": {
                          "type": "array",
                          "description": "List of flag IDs required.",
                          "items": {
                            "type": "string"
                          }
                        }
                      },
                      "required": ["id", "name", "description", "location", "unlockConditions"]
                    }
                  },
                  "timeline": {
                    "type": "object",
                    "additionalProperties": false,
                    "description": "Defines the temporal structure of the case, including key events. All times (baseTime, startTime, endTime, events) are stored as absolute times in minutes from midnight.",
                    "properties": {
                      "baseTime": {
                        "type": "object",
                        "additionalProperties": false,
                        "properties": {
                          "minutes": {
                            "type": "number",
                            "description": "Absolute time reference point in minutes from midnight. For example, 960 minutes = 16:00 (4 PM). This is the earliest point in the timeline. Crime events occur between baseTime and startTime. Must be < startTime."
                          }
                        },
                        "required": ["minutes"]
                      },
                      "startTime": {
                        "type": "object",
                        "additionalProperties": false,
                        "properties": {
                          "minutes": {
                            "type": "number",
                            "description": "Absolute time in minutes from midnight when the game starts. For example, 1080 minutes = 18:00 (6 PM). Must be > baseTime and < endTime. The crime event MUST occur between baseTime and startTime."
                          }
                        },
                        "required": ["minutes"]
                      },
                      "endTime": {
                        "type": "object",
                        "additionalProperties": false,
                        "properties": {
                          "minutes": {
                            "type": "number",
                            "description": "Absolute time in minutes from midnight when the game ends. For example, 1440 minutes = 24:00 (midnight). Must be > startTime."
                          }
                        },
                        "required": ["minutes"]
                      },
                      "events": {
                        "type": "array",
                        "description": "Chronological list of events that modify the game state. Events are stored as absolute time in minutes from midnight (same format as baseTime, startTime, endTime). You MUST include exactly one CRIME event with eventType='CRIME' that occurs between baseTime and startTime.",
                        "items": {
                          "type": "object",
                          "additionalProperties": false,
                          "properties": {
                            "id": {
                              "type": "string",
                              "description": "Unique ID of the event."
                            },
                            "time": {
                              "type": "object",
                              "additionalProperties": false,
                              "properties": {
                                "minutes": {
                                  "type": "number",
                                  "description": "Absolute time in minutes from midnight (same format as baseTime, startTime, endTime). For crime events, this must be between baseTime.minutes and startTime.minutes. For game events, this must be between startTime.minutes and endTime.minutes."
                                }
                              },
                              "required": ["minutes"]
                            },
                            "eventType": {
                              "type": "string",
                              "description": "Type of event. CRIME events must occur before the game starts (between baseTime and startTime, all absolute times).",
                              "enum": ["CHARACTER_MOVEMENT", "PLACE_CHANGE", "CRIME", "CUSTOM"]
                            },
                            "description": {
                              "type": "string",
                              "description": "Narrative description of what happens during this event."
                            },
                            "characterId": {
                              "type": "string",
                              "description": "ID of character if event is CHARACTER_MOVEMENT. Can be empty."
                            },
                            "placeId": {
                              "type": "string",
                              "description": "ID of place as destination/target depending on eventType. Can be empty."
                            }
                          },
                          "required": ["id", "time", "eventType", "description", "characterId", "placeId"]
                        }
                      }
                    },
                    "required": ["baseTime", "startTime", "endTime", "events"]
                  },
                  "flags": {
                    "type": "array",
                    "description": "Initial flags and their boolean states.",
                    "items": {
                      "type": "object",
                      "additionalProperties": false,
                      "properties": {
                        "id": {
                          "type": "string",
                          "description": "Flag identifier."
                        },
                        "value": {
                          "type": "boolean",
                          "description": "Whether this flag is initially active."
                        }
                      },
                      "required": ["id", "value"]
                    }
                  }
                },
                "required": [
                  "title",
                  "description",
                  "phase",
                  "player",
                  "characters",
                  "places",
                  "clues",
                  "timeline",
                  "flags"
                ]
              }
            }
        """.trimIndent()
    }

    // Todo: Implement followings
    // LLM 2: Intro Generator
    object IntroGenerator {
        val SCHEMA = """
            {
              "name": "intro",
              "strict": true,
              "schema": {
                "type": "object",
                "additionalProperties": false,
                "properties": {
                  "text": {
                    "type": "string",
                    "description": "The intro text to show to the player."
                  }
                }
              }
            }
        """.trimIndent()

        // LLM 3: Dialogue Generator
        // LLM 4: Description Generator
    }
}

