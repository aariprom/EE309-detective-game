package com.ee309.detectivegame.domain.model

import org.junit.Test
import org.junit.Assert.*

class CharacterTest {
    
    @Test
    fun `isUnlocked returns true when no unlock conditions`() {
        val character = Character(
            id = "char1",
            name = "John",
            unlockConditions = emptyList()
        )
        
        assertTrue(character.isUnlocked(emptyMap()))
        assertTrue(character.isUnlocked(mapOf("flag1" to false)))
    }
    
    @Test
    fun `isUnlocked returns true when all conditions met`() {
        val character = Character(
            id = "char1",
            name = "John",
            unlockConditions = listOf("flag1", "flag2")
        )
        val flags = mapOf("flag1" to true, "flag2" to true)
        
        assertTrue(character.isUnlocked(flags))
    }
    
    @Test
    fun `isUnlocked returns false when any condition not met`() {
        val character = Character(
            id = "char1",
            name = "John",
            unlockConditions = listOf("flag1", "flag2")
        )
        val flags = mapOf("flag1" to true, "flag2" to false)
        
        assertFalse(character.isUnlocked(flags))
    }
    
    @Test
    fun `isUnlocked returns false when flag missing`() {
        val character = Character(
            id = "char1",
            name = "John",
            unlockConditions = listOf("flag1")
        )
        val flags = emptyMap<String, Boolean>()
        
        assertFalse(character.isUnlocked(flags))
    }
    
    @Test
    fun `isAtLocation returns true when character at location`() {
        val character = Character(
            id = "char1",
            name = "John",
            currentLocation = "place1"
        )
        
        assertTrue(character.isAtLocation("place1"))
    }
    
    @Test
    fun `isAtLocation returns false when character not at location`() {
        val character = Character(
            id = "char1",
            name = "John",
            currentLocation = "place1"
        )
        
        assertFalse(character.isAtLocation("place2"))
    }
    
    @Test
    fun `isAtLocation returns false when location is empty`() {
        val character = Character(
            id = "char1",
            name = "John",
            currentLocation = ""
        )
        
        assertFalse(character.isAtLocation("place1"))
    }
}

