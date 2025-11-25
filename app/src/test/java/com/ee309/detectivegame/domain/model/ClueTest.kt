package com.ee309.detectivegame.domain.model

import kotlinx.serialization.InternalSerializationApi
import org.junit.Test
import org.junit.Assert.*

class ClueTest {
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `isUnlocked returns true when no unlock conditions`() {
        val clue = Clue(
            id = "clue1",
            content = "Test clue",
            unlockConditions = emptyList()
        )
        
        assertTrue(clue.isUnlocked(emptyMap()))
        assertTrue(clue.isUnlocked(mapOf("flag1" to false)))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `isUnlocked returns true when all conditions met`() {
        val clue = Clue(
            id = "clue1",
            content = "Test clue",
            unlockConditions = listOf("flag1", "flag2")
        )
        val flags = mapOf("flag1" to true, "flag2" to true)
        
        assertTrue(clue.isUnlocked(flags))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `isUnlocked returns false when any condition not met`() {
        val clue = Clue(
            id = "clue1",
            content = "Test clue",
            unlockConditions = listOf("flag1", "flag2")
        )
        val flags = mapOf("flag1" to true, "flag2" to false)
        
        assertFalse(clue.isUnlocked(flags))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `isUnlocked returns false when flag missing`() {
        val clue = Clue(
            id = "clue1",
            content = "Test clue",
            unlockConditions = listOf("flag1")
        )
        val flags = emptyMap<String, Boolean>()
        
        assertFalse(clue.isUnlocked(flags))
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `clue can have nullable properties`() {
        val clue = Clue(
            id = "clue1",
            content = "Test clue",
            who = null,
            whom = null,
            time = null,
            place = null
        )
        
        assertNull(clue.who)
        assertNull(clue.whom)
        assertNull(clue.time)
        assertNull(clue.place)
        assertEquals("Test clue", clue.content)
    }
    
    @OptIn(InternalSerializationApi::class)
    @Test
    fun `clue can have all properties set`() {
        val clue = Clue(
            id = "clue1",
            content = "Test clue",
            who = "John",
            whom = "Jane",
            time = GameTime(30),
            place = "place1"
        )
        
        assertEquals("John", clue.who)
        assertEquals("Jane", clue.whom)
        assertEquals(30, clue.time?.minutes)
        assertEquals("place1", clue.place)
    }
}

