package com.ee309.detectivegame.domain.model

import org.junit.Test
import org.junit.Assert.*

class PlayerTest {
    
    @Test
    fun `default constructor creates player with default values`() {
        val player = Player()
        
        assertEquals("Detective", player.name)
        assertEquals("", player.currentLocation)
        assertTrue(player.collectedClues.isEmpty())
        assertTrue(player.tools.isEmpty())
        assertTrue(player.flags.isEmpty())
    }
    
    @Test
    fun `hasClue returns true when clue is collected`() {
        val player = Player(collectedClues = listOf("clue1", "clue2"))
        
        assertTrue(player.hasClue("clue1"))
        assertTrue(player.hasClue("clue2"))
    }
    
    @Test
    fun `hasClue returns false when clue not collected`() {
        val player = Player(collectedClues = listOf("clue1"))
        
        assertFalse(player.hasClue("clue2"))
    }
    
    @Test
    fun `hasClue returns false when no clues collected`() {
        val player = Player()
        
        assertFalse(player.hasClue("clue1"))
    }
    
    @Test
    fun `hasTool returns true when tool is collected`() {
        val player = Player(tools = listOf("flashlight", "magnifying_glass"))
        
        assertTrue(player.hasTool("flashlight"))
        assertTrue(player.hasTool("magnifying_glass"))
    }
    
    @Test
    fun `hasTool returns false when tool not collected`() {
        val player = Player(tools = listOf("flashlight"))
        
        assertFalse(player.hasTool("magnifying_glass"))
    }
    
    @Test
    fun `addClue adds clue to collected clues`() {
        val player = Player(collectedClues = listOf("clue1"))
        
        val updated = player.addClue("clue2")
        
        assertEquals(2, updated.collectedClues.size)
        assertTrue(updated.collectedClues.contains("clue1"))
        assertTrue(updated.collectedClues.contains("clue2"))
        assertEquals(1, player.collectedClues.size) // Original unchanged
    }
    
    @Test
    fun `addClue maintains immutability`() {
        val player = Player(collectedClues = listOf("clue1"))
        val updated = player.addClue("clue2")
        
        // Original unchanged
        assertEquals(1, player.collectedClues.size)
        // New player has both clues
        assertEquals(2, updated.collectedClues.size)
    }
    
    @Test
    fun `addTool adds tool to tools list`() {
        val player = Player(tools = listOf("flashlight"))
        
        val updated = player.addTool("magnifying_glass")
        
        assertEquals(2, updated.tools.size)
        assertTrue(updated.tools.contains("flashlight"))
        assertTrue(updated.tools.contains("magnifying_glass"))
        assertEquals(1, player.tools.size) // Original unchanged
    }
    
    @Test
    fun `addTool maintains immutability`() {
        val player = Player(tools = listOf("flashlight"))
        val updated = player.addTool("magnifying_glass")
        
        // Original unchanged
        assertEquals(1, player.tools.size)
        // New player has both tools
        assertEquals(2, updated.tools.size)
    }
}

