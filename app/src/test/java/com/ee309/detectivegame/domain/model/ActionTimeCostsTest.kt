package com.ee309.detectivegame.domain.model

import org.junit.Test
import org.junit.Assert.*

class ActionTimeCostsTest {
    
    @Test
    fun `getActionTime returns correct time for INVESTIGATION`() {
        val time = ActionTimeCosts.getActionTime(ActionTimeCosts.ActionType.INVESTIGATION)
        assertEquals(ActionTimeCosts.INVESTIGATION_TIME, time)
        assertEquals(15, time)
    }
    
    @Test
    fun `getActionTime returns correct time for QUESTIONING`() {
        val time = ActionTimeCosts.getActionTime(ActionTimeCosts.ActionType.QUESTIONING)
        assertEquals(ActionTimeCosts.QUESTIONING_TIME, time)
        assertEquals(10, time)
    }
    
    @Test
    fun `getActionTime returns correct time for MOVEMENT`() {
        val time = ActionTimeCosts.getActionTime(ActionTimeCosts.ActionType.MOVEMENT)
        assertEquals(ActionTimeCosts.MOVEMENT_BASE_TIME, time)
        assertEquals(5, time)
    }
    
    @Test
    fun `getActionTime returns correct time for ACCUSATION`() {
        val time = ActionTimeCosts.getActionTime(ActionTimeCosts.ActionType.ACCUSATION)
        assertEquals(ActionTimeCosts.ACCUSATION_TIME, time)
        assertEquals(0, time)
    }
    
    @Test
    fun `getActionTime returns correct time for FREE_ACTION`() {
        val time = ActionTimeCosts.getActionTime(ActionTimeCosts.ActionType.FREE_ACTION)
        assertEquals(ActionTimeCosts.FREE_ACTION_DEFAULT_TIME, time)
        assertEquals(10, time)
    }
    
    @Test
    fun `getMovementTime calculates correctly for distance 0`() {
        val time = ActionTimeCosts.getMovementTime(0)
        assertEquals(ActionTimeCosts.MOVEMENT_BASE_TIME, time)
        assertEquals(5, time)
    }
    
    @Test
    fun `getMovementTime calculates correctly for distance 1`() {
        val time = ActionTimeCosts.getMovementTime(1)
        // base (5) + distance (1 * 5) = 10
        assertEquals(10, time)
    }
    
    @Test
    fun `getMovementTime calculates correctly for distance 2`() {
        val time = ActionTimeCosts.getMovementTime(2)
        // base (5) + distance (2 * 5) = 15
        assertEquals(15, time)
    }
    
    @Test
    fun `getMovementTime rounds to time unit when needed`() {
        // If calculation results in non-multiple of 5, it should round
        // For example, if base was 3 and distance was 2*5=10, total would be 13
        // But we're using base=5, so this shouldn't happen, but test the rounding function
        val rounded = ActionTimeCosts.roundToTimeUnit(13)
        assertEquals(10, rounded) // rounds down to nearest 5
    }
    
    @Test
    fun `getMovementTime with Place objects uses getDistanceTo`() {
        val place1 = Place(
            id = "place1",
            name = "Place 1",
            connectedPlaces = listOf("place2")
        )
        val place2 = Place(
            id = "place2",
            name = "Place 2",
            connectedPlaces = listOf("place1")
        )
        
        val time = ActionTimeCosts.getMovementTime(place1, place2)
        // Connected places have distance 1, so base (5) + (1 * 5) = 10
        assertEquals(10, time)
    }
    
    @Test
    fun `getMovementTime with Place objects for non-connected places`() {
        val place1 = Place(
            id = "place1",
            name = "Place 1",
            connectedPlaces = emptyList()
        )
        val place2 = Place(
            id = "place2",
            name = "Place 2",
            connectedPlaces = emptyList()
        )
        
        val time = ActionTimeCosts.getMovementTime(place1, place2)
        // Non-connected places have distance 2, so base (5) + (2 * 5) = 15
        assertEquals(15, time)
    }
    
    @Test
    fun `roundToTimeUnit rounds down to nearest multiple of 5`() {
        assertEquals(0, ActionTimeCosts.roundToTimeUnit(0))
        assertEquals(5, ActionTimeCosts.roundToTimeUnit(5))
        assertEquals(5, ActionTimeCosts.roundToTimeUnit(7)) // rounds down
        assertEquals(10, ActionTimeCosts.roundToTimeUnit(10))
        assertEquals(10, ActionTimeCosts.roundToTimeUnit(12)) // rounds down
        assertEquals(15, ActionTimeCosts.roundToTimeUnit(15))
        assertEquals(15, ActionTimeCosts.roundToTimeUnit(17)) // rounds down
    }
    
    @Test
    fun `isValidTimeUnit returns true for valid time units`() {
        assertTrue(ActionTimeCosts.isValidTimeUnit(0))
        assertTrue(ActionTimeCosts.isValidTimeUnit(5))
        assertTrue(ActionTimeCosts.isValidTimeUnit(10))
        assertTrue(ActionTimeCosts.isValidTimeUnit(15))
        assertTrue(ActionTimeCosts.isValidTimeUnit(30))
    }
    
    @Test
    fun `isValidTimeUnit returns false for invalid time units`() {
        assertFalse(ActionTimeCosts.isValidTimeUnit(1))
        assertFalse(ActionTimeCosts.isValidTimeUnit(3))
        assertFalse(ActionTimeCosts.isValidTimeUnit(7))
        assertFalse(ActionTimeCosts.isValidTimeUnit(13))
        assertFalse(ActionTimeCosts.isValidTimeUnit(-5)) // negative
    }
    
    @Test
    fun `all time cost constants are multiples of MIN_TIME_UNIT`() {
        assertTrue(ActionTimeCosts.INVESTIGATION_TIME % ActionTimeCosts.MIN_TIME_UNIT == 0)
        assertTrue(ActionTimeCosts.QUESTIONING_TIME % ActionTimeCosts.MIN_TIME_UNIT == 0)
        assertTrue(ActionTimeCosts.MOVEMENT_BASE_TIME % ActionTimeCosts.MIN_TIME_UNIT == 0)
        assertTrue(ActionTimeCosts.MOVEMENT_DISTANCE_TIME % ActionTimeCosts.MIN_TIME_UNIT == 0)
        assertTrue(ActionTimeCosts.ACCUSATION_TIME % ActionTimeCosts.MIN_TIME_UNIT == 0)
        assertTrue(ActionTimeCosts.FREE_ACTION_DEFAULT_TIME % ActionTimeCosts.MIN_TIME_UNIT == 0)
    }
}

