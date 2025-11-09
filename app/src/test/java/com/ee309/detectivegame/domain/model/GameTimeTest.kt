package com.ee309.detectivegame.domain.model

import org.junit.Test
import org.junit.Assert.*

class GameTimeTest {
    
    @Test
    fun `default constructor creates time at 0 minutes`() {
        val time = GameTime()
        assertEquals(0, time.minutes)
        assertEquals(0, time.hours)
        assertEquals(0, time.minutesOfHour)
    }
    
    @Test
    fun `constructor with minutes sets correct value`() {
        val time = GameTime(30)
        assertEquals(30, time.minutes)
    }
    
    @Test
    fun `hours property calculates correctly`() {
        assertEquals(0, GameTime(0).hours)
        assertEquals(0, GameTime(30).hours)
        assertEquals(1, GameTime(60).hours)
        assertEquals(1, GameTime(90).hours)
        assertEquals(2, GameTime(120).hours)
        assertEquals(8, GameTime(480).hours)
    }
    
    @Test
    fun `minutesOfHour property calculates correctly`() {
        assertEquals(0, GameTime(0).minutesOfHour)
        assertEquals(30, GameTime(30).minutesOfHour)
        assertEquals(0, GameTime(60).minutesOfHour)
        assertEquals(30, GameTime(90).minutesOfHour)
        assertEquals(45, GameTime(105).minutesOfHour)
    }
    
    @Test
    fun `addMinutes adds correctly`() {
        val time1 = GameTime(30)
        val time2 = time1.addMinutes(15)
        
        assertEquals(30, time1.minutes) // Original unchanged
        assertEquals(45, time2.minutes) // New time has sum
    }
    
    @Test
    fun `addMinutes with negative value subtracts`() {
        val time1 = GameTime(30)
        val time2 = time1.addMinutes(-10)
        
        assertEquals(30, time1.minutes)
        assertEquals(20, time2.minutes)
    }
    
    @Test
    fun `addMinutes can go to zero`() {
        val time1 = GameTime(10)
        val time2 = time1.addMinutes(-10)
        
        assertEquals(0, time2.minutes)
    }
    
    @Test
    fun `isAfter returns true when time is after other`() {
        val time1 = GameTime(30)
        val time2 = GameTime(15)
        
        assertTrue(time1.isAfter(time2))
        assertFalse(time2.isAfter(time1))
    }
    
    @Test
    fun `isAfter returns false when time is equal`() {
        val time1 = GameTime(30)
        val time2 = GameTime(30)
        
        assertFalse(time1.isAfter(time2))
        assertFalse(time2.isAfter(time1))
    }
    
    @Test
    fun `isBefore returns true when time is before other`() {
        val time1 = GameTime(15)
        val time2 = GameTime(30)
        
        assertTrue(time1.isBefore(time2))
        assertFalse(time2.isBefore(time1))
    }
    
    @Test
    fun `isBefore returns false when time is equal`() {
        val time1 = GameTime(30)
        val time2 = GameTime(30)
        
        assertFalse(time1.isBefore(time2))
        assertFalse(time2.isBefore(time1))
    }
    
    @Test
    fun `format formats correctly for zero time`() {
        val time = GameTime(0)
        assertEquals("00:00", time.format())
    }
    
    @Test
    fun `format formats correctly for minutes only`() {
        val time = GameTime(30)
        assertEquals("00:30", time.format())
    }
    
    @Test
    fun `format formats correctly for hours only`() {
        val time = GameTime(60)
        assertEquals("01:00", time.format())
    }
    
    @Test
    fun `format formats correctly for hours and minutes`() {
        val time = GameTime(90)
        assertEquals("01:30", time.format())
    }
    
    @Test
    fun `format formats correctly for large hours`() {
        val time = GameTime(480) // 8 hours
        assertEquals("08:00", time.format())
    }
    
    @Test
    fun `format formats correctly with single digit minutes`() {
        val time = GameTime(125) // 2 hours 5 minutes
        assertEquals("02:05", time.format())
    }
}

