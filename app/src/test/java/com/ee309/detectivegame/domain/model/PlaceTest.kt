package com.ee309.detectivegame.domain.model

import kotlinx.serialization.InternalSerializationApi
import org.junit.Test
import org.junit.Assert.*

class PlaceTest {
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `isUnlocked returns true when no unlock conditions`() {
        val place = Place(
            id = "place1",
            name = "Library",
            unlockConditions = emptyList()
        )
        
        assertTrue(place.isUnlocked(emptyMap()))
        assertTrue(place.isUnlocked(mapOf("flag1" to false)))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `isUnlocked returns true when all conditions met`() {
        val place = Place(
            id = "place1",
            name = "Library",
            unlockConditions = listOf("flag1", "flag2")
        )
        val flags = mapOf("flag1" to true, "flag2" to true)
        
        assertTrue(place.isUnlocked(flags))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `isUnlocked returns false when any condition not met`() {
        val place = Place(
            id = "place1",
            name = "Library",
            unlockConditions = listOf("flag1", "flag2")
        )
        val flags = mapOf("flag1" to true, "flag2" to false)
        
        assertFalse(place.isUnlocked(flags))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getDistanceTo returns 1 for connected places`() {
        val place1 = Place(
            id = "place1",
            name = "Library",
            connectedPlaces = listOf("place2")
        )
        val place2 = Place(
            id = "place2",
            name = "Garden",
            connectedPlaces = listOf("place1")
        )
        
        assertEquals(1, place1.getDistanceTo(place2))
        assertEquals(1, place2.getDistanceTo(place1))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getDistanceTo returns 2 for non-connected places`() {
        val place1 = Place(
            id = "place1",
            name = "Library",
            connectedPlaces = emptyList()
        )
        val place2 = Place(
            id = "place2",
            name = "Garden",
            connectedPlaces = emptyList()
        )
        
        assertEquals(2, place1.getDistanceTo(place2))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `getDistanceTo returns 2 when place not in connected list`() {
        val place1 = Place(
            id = "place1",
            name = "Library",
            connectedPlaces = listOf("place3") // place2 not in list
        )
        val place2 = Place(
            id = "place2",
            name = "Garden",
            connectedPlaces = emptyList()
        )
        
        assertEquals(2, place1.getDistanceTo(place2))
    }
}

