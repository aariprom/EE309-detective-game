package com.ee309.detectivegame.domain.model

/**
 * Defines time costs for different game actions.
 * All time costs are in minutes (5-minute minimum unit).
 * 
 * This object provides constants and helper methods for calculating
 * time consumption when players perform various actions in the game.
 */
object ActionTimeCosts {
    
    /**
     * Represents the type of action being performed
     */
    enum class ActionType {
        INVESTIGATION,  // Investigating a place
        QUESTIONING,   // Questioning/interrogating a character
        MOVEMENT,      // Moving between locations
        ACCUSATION,    // Making an accusation
        FREE_ACTION    // Free-form player action
    }
    
    // Base time costs for actions (in minutes)
    // All values are multiples of 5 (5-minute minimum unit)
    
    /**
     * Time cost for investigating a place.
     * Default: 15 minutes
     */
    const val INVESTIGATION_TIME = 15
    
    /**
     * Time cost for questioning/interrogating a character.
     * Default: 10 minutes
     */
    const val QUESTIONING_TIME = 10
    
    /**
     * Base time cost for movement between locations.
     * Additional time may be added based on distance.
     * Default: 5 minutes
     */
    const val MOVEMENT_BASE_TIME = 5
    
    /**
     * Time cost per distance unit when moving between locations.
     * Default: 5 minutes per unit
     */
    const val MOVEMENT_DISTANCE_TIME = 5
    
    /**
     * Time cost for making an accusation.
     * Accusations are typically quick actions.
     * Default: 0 minutes (instant)
     */
    const val ACCUSATION_TIME = 0
    
    /**
     * Default time cost for free-form actions.
     * Can be overridden based on action complexity.
     * Default: 10 minutes
     */
    const val FREE_ACTION_DEFAULT_TIME = 10
    
    /**
     * Minimum time unit allowed in the game.
     * All time costs must be multiples of this value.
     */
    const val MIN_TIME_UNIT = 5
    
    /**
     * Gets the time cost for a specific action type.
     * 
     * @param actionType The type of action being performed.
     * @return The time cost in minutes for the action.
     */
    fun getActionTime(actionType: ActionType): Int {
        return when (actionType) {
            ActionType.INVESTIGATION -> INVESTIGATION_TIME
            ActionType.QUESTIONING -> QUESTIONING_TIME
            ActionType.MOVEMENT -> MOVEMENT_BASE_TIME
            ActionType.ACCUSATION -> ACCUSATION_TIME
            ActionType.FREE_ACTION -> FREE_ACTION_DEFAULT_TIME
        }
    }
    
    /**
     * Calculates the time cost for movement between two places.
     * Time is calculated as: base time + (distance * distance time)
     * 
     * @param distance The distance between places (in distance units).
     * @return The total time cost in minutes for the movement.
     */
    fun getMovementTime(distance: Int): Int {
        val totalTime = MOVEMENT_BASE_TIME + (distance * MOVEMENT_DISTANCE_TIME)
        // Ensure time is a multiple of MIN_TIME_UNIT
        return roundToTimeUnit(totalTime)
    }
    
    /**
     * Calculates movement time between two Place objects.
     * Uses Place.getDistanceTo() to determine distance.
     * 
     * @param from The starting place.
     * @param to The destination place.
     * @return The total time cost in minutes for the movement.
     */
    fun getMovementTime(from: Place, to: Place): Int {
        val distance = from.getDistanceTo(to)
        return getMovementTime(distance)
    }
    
    /**
     * Rounds a time value to the nearest multiple of MIN_TIME_UNIT.
     * This ensures all time costs respect the 5-minute minimum unit.
     * 
     * @param minutes The time in minutes to round.
     * @return The rounded time value (multiple of MIN_TIME_UNIT).
     */
    fun roundToTimeUnit(minutes: Int): Int {
        return (minutes / MIN_TIME_UNIT) * MIN_TIME_UNIT
    }
    
    /**
     * Validates that a time value is a valid multiple of MIN_TIME_UNIT.
     * 
     * @param minutes The time value to validate.
     * @return true if the time is a valid multiple of MIN_TIME_UNIT, false otherwise.
     */
    fun isValidTimeUnit(minutes: Int): Boolean {
        return minutes % MIN_TIME_UNIT == 0 && minutes >= 0
    }
}

