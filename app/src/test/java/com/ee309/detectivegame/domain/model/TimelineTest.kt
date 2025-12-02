package com.ee309.detectivegame.domain.model

import org.junit.Test
import org.junit.Assert.*

class TimelineTest {
    
    // Test timeline constants (absolute times)
    private val baseTime = GameTime(960)   // 16:00
    private val startTime = GameTime(1080)  // 18:00
    private val endTime = GameTime(1440)    // 24:00
    
    private fun createTestEvent(id: String, time: GameTime): TimelineEvent {
        return TimelineEvent(
            id = id,
            time = time,
            eventType = TimelineEvent.EventType.CHARACTER_MOVEMENT,
            description = "Test event $id",
            characterId = "char1",
            placeId = "place1"
        )
    }
    
    private fun createCrimeEvent(id: String, time: GameTime): TimelineEvent {
        return TimelineEvent(
            id = id,
            time = time,
            eventType = TimelineEvent.EventType.CRIME,
            description = "Crime event $id",
            characterId = "char1",
            placeId = "place1"
        )
    }
    
    @Test
    fun `getPastEvents returns events before or at current time`() {
        // Events at absolute times: 30, 60, 90 minutes after startTime (1080)
        val event1 = createTestEvent("event1", GameTime(1080 + 30))  // 1110
        val event2 = createTestEvent("event2", GameTime(1080 + 60))  // 1140
        val event3 = createTestEvent("event3", GameTime(1080 + 90))  // 1170
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(event1, event2, event3)
        )
        
        // currentTime = 75 minutes relative to startTime (1080)
        // Absolute time = 1080 + 75 = 1155
        val pastEvents = timeline.getPastEvents(GameTime(75))
        
        assertEquals(2, pastEvents.size)
        assertTrue(pastEvents.any { it.id == "event1" })
        assertTrue(pastEvents.any { it.id == "event2" })
        assertFalse(pastEvents.any { it.id == "event3" })
    }
    
    @Test
    fun `getPastEvents includes events at current time`() {
        val event1 = createTestEvent("event1", GameTime(1080 + 30))  // 1110
        val event2 = createTestEvent("event2", GameTime(1080 + 60))  // 1140
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(event1, event2)
        )
        
        // currentTime = 60 minutes relative to startTime
        // Absolute time = 1080 + 60 = 1140 (same as event2)
        val pastEvents = timeline.getPastEvents(GameTime(60))
        
        assertEquals(2, pastEvents.size) // Both events included (event2 is at current time)
    }
    
    @Test
    fun `getPastEvents returns sorted events`() {
        val event1 = createTestEvent("event1", GameTime(1080 + 60))  // 1140
        val event2 = createTestEvent("event2", GameTime(1080 + 30))  // 1110
        val event3 = createTestEvent("event3", GameTime(1080 + 90))  // 1170
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(event1, event2, event3)
        )
        
        val pastEvents = timeline.getPastEvents(GameTime(100))
        
        assertEquals(3, pastEvents.size)
        assertEquals("event2", pastEvents[0].id) // Sorted by time (1110)
        assertEquals("event1", pastEvents[1].id)  // 1140
        assertEquals("event3", pastEvents[2].id)  // 1170
    }
    
    @Test
    fun `getFutureEvents returns events after current time`() {
        val event1 = createTestEvent("event1", GameTime(1080 + 30))  // 1110
        val event2 = createTestEvent("event2", GameTime(1080 + 60))  // 1140
        val event3 = createTestEvent("event3", GameTime(1080 + 90))  // 1170
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(event1, event2, event3)
        )
        
        // currentTime = 45 minutes relative to startTime
        // Absolute time = 1080 + 45 = 1125
        val futureEvents = timeline.getFutureEvents(GameTime(45))
        
        assertEquals(2, futureEvents.size)
        assertTrue(futureEvents.any { it.id == "event2" })
        assertTrue(futureEvents.any { it.id == "event3" })
        assertFalse(futureEvents.any { it.id == "event1" })
    }
    
    @Test
    fun `getFutureEvents returns sorted events`() {
        val event1 = createTestEvent("event1", GameTime(1080 + 90))   // 1170
        val event2 = createTestEvent("event2", GameTime(1080 + 60))   // 1140
        val event3 = createTestEvent("event3", GameTime(1080 + 120))  // 1200
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(event1, event2, event3)
        )
        
        val futureEvents = timeline.getFutureEvents(GameTime(45))
        
        assertEquals(3, futureEvents.size)
        assertEquals("event2", futureEvents[0].id) // Sorted by time (1140)
        assertEquals("event1", futureEvents[1].id) // 1170
        assertEquals("event3", futureEvents[2].id) // 1200
    }
    
    @Test
    fun `getEventsAtTime returns events at exact time`() {
        val absoluteTime = GameTime(1080 + 60)  // 1140
        val event1 = createTestEvent("event1", absoluteTime)
        val event2 = createTestEvent("event2", absoluteTime)
        val event3 = createTestEvent("event3", GameTime(1080 + 90))  // 1170
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(event1, event2, event3)
        )
        
        val events = timeline.getEventsAtTime(absoluteTime)
        
        assertEquals(2, events.size)
        assertTrue(events.any { it.id == "event1" })
        assertTrue(events.any { it.id == "event2" })
        assertFalse(events.any { it.id == "event3" })
    }
    
    @Test
    fun `getUpcomingEvents returns limited future events`() {
        val events = (1..10).map { i ->
            createTestEvent("event$i", GameTime(1080 + i * 10))
        }
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = events
        )
        
        val upcoming = timeline.getUpcomingEvents(GameTime(0), limit = 5)
        
        assertEquals(5, upcoming.size)
        assertEquals("event1", upcoming[0].id)
        assertEquals("event5", upcoming[4].id)
    }
    
    @Test
    fun `getUpcomingEvents returns fewer events when limit exceeds available`() {
        val event1 = createTestEvent("event1", GameTime(1080 + 30))  // 1110
        val event2 = createTestEvent("event2", GameTime(1080 + 60))  // 1140
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(event1, event2)
        )
        
        val upcoming = timeline.getUpcomingEvents(GameTime(0), limit = 10)
        
        assertEquals(2, upcoming.size) // Only 2 available, not 10
    }
    
    @Test
    fun `getPastEvents returns empty list when no past events`() {
        val event = createTestEvent("event1", GameTime(1080 + 60))  // 1140
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(event)
        )
        
        // currentTime = 30 minutes relative to startTime
        // Absolute time = 1080 + 30 = 1110, which is before event at 1140
        val pastEvents = timeline.getPastEvents(GameTime(30))
        
        assertTrue(pastEvents.isEmpty())
    }
    
    @Test
    fun `getFutureEvents returns empty list when no future events`() {
        val event = createTestEvent("event1", GameTime(1080 + 30))  // 1110
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(event)
        )
        
        // currentTime = 60 minutes relative to startTime
        // Absolute time = 1080 + 60 = 1140, which is after event at 1110
        val futureEvents = timeline.getFutureEvents(GameTime(60))
        
        assertTrue(futureEvents.isEmpty())
    }
    
    @Test
    fun `getCrimeEvents returns only CRIME events`() {
        val crimeEvent1 = createCrimeEvent("crime1", GameTime(1020))  // Between baseTime and startTime
        val crimeEvent2 = createCrimeEvent("crime2", GameTime(1050))  // Between baseTime and startTime
        val normalEvent = createTestEvent("normal1", GameTime(1080 + 30))  // After startTime
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(crimeEvent1, crimeEvent2, normalEvent)
        )
        
        val crimeEvents = timeline.getCrimeEvents()
        
        assertEquals(2, crimeEvents.size)
        assertTrue(crimeEvents.any { it.id == "crime1" })
        assertTrue(crimeEvents.any { it.id == "crime2" })
        assertFalse(crimeEvents.any { it.id == "normal1" })
    }
    
    @Test
    fun `getCrimeEvents returns sorted crime events`() {
        val crimeEvent1 = createCrimeEvent("crime1", GameTime(1050))
        val crimeEvent2 = createCrimeEvent("crime2", GameTime(1020))
        val crimeEvent3 = createCrimeEvent("crime3", GameTime(1035))
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(crimeEvent1, crimeEvent2, crimeEvent3)
        )
        
        val crimeEvents = timeline.getCrimeEvents()
        
        assertEquals(3, crimeEvents.size)
        assertEquals("crime2", crimeEvents[0].id) // Sorted by time (1020)
        assertEquals("crime3", crimeEvents[1].id) // 1035
        assertEquals("crime1", crimeEvents[2].id) // 1050
    }
    
    @Test
    fun `getCrimeEvents returns empty list when no crime events`() {
        val normalEvent1 = createTestEvent("normal1", GameTime(1080 + 30))
        val normalEvent2 = createTestEvent("normal2", GameTime(1080 + 60))
        val timeline = Timeline(
            baseTime = baseTime,
            startTime = startTime,
            endTime = endTime,
            events = listOf(normalEvent1, normalEvent2)
        )
        
        val crimeEvents = timeline.getCrimeEvents()
        
        assertTrue(crimeEvents.isEmpty())
    }
}

