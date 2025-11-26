package com.ee309.detectivegame.domain.model

import org.junit.Test
import org.junit.Assert.*

class TimelineTest {
    
    private fun createTestEvent(id: String, time: GameTime): TimelineEvent {
        return TimelineEvent(
            id = id,
            time = time,
            eventType = TimelineEvent.EventType.CHARACTER_ACTION,
            description = "Test event $id"
        )
    }
    
    @Test
    fun `getPastEvents returns events before or at current time`() {
        val event1 = createTestEvent("event1", GameTime(30))
        val event2 = createTestEvent("event2", GameTime(60))
        val event3 = createTestEvent("event3", GameTime(90))
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(event1, event2, event3)
        )
        
        val pastEvents = timeline.getPastEvents(GameTime(75))
        
        assertEquals(2, pastEvents.size)
        assertTrue(pastEvents.any { it.id == "event1" })
        assertTrue(pastEvents.any { it.id == "event2" })
        assertFalse(pastEvents.any { it.id == "event3" })
    }
    
    @Test
    fun `getPastEvents includes events at current time`() {
        val event1 = createTestEvent("event1", GameTime(30))
        val event2 = createTestEvent("event2", GameTime(60))
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(event1, event2)
        )
        
        val pastEvents = timeline.getPastEvents(GameTime(60))
        
        assertEquals(2, pastEvents.size) // Both events included (event2 is at current time)
    }
    
    @Test
    fun `getPastEvents returns sorted events`() {
        val event1 = createTestEvent("event1", GameTime(60))
        val event2 = createTestEvent("event2", GameTime(30))
        val event3 = createTestEvent("event3", GameTime(90))
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(event1, event2, event3)
        )
        
        val pastEvents = timeline.getPastEvents(GameTime(100))
        
        assertEquals(3, pastEvents.size)
        assertEquals("event2", pastEvents[0].id) // Sorted by time
        assertEquals("event1", pastEvents[1].id)
        assertEquals("event3", pastEvents[2].id)
    }
    
    @Test
    fun `getFutureEvents returns events after current time`() {
        val event1 = createTestEvent("event1", GameTime(30))
        val event2 = createTestEvent("event2", GameTime(60))
        val event3 = createTestEvent("event3", GameTime(90))
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(event1, event2, event3)
        )
        
        val futureEvents = timeline.getFutureEvents(GameTime(45))
        
        assertEquals(2, futureEvents.size)
        assertTrue(futureEvents.any { it.id == "event2" })
        assertTrue(futureEvents.any { it.id == "event3" })
        assertFalse(futureEvents.any { it.id == "event1" })
    }
    
    @Test
    fun `getFutureEvents returns sorted events`() {
        val event1 = createTestEvent("event1", GameTime(90))
        val event2 = createTestEvent("event2", GameTime(60))
        val event3 = createTestEvent("event3", GameTime(120))
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(event1, event2, event3)
        )
        
        val futureEvents = timeline.getFutureEvents(GameTime(45))
        
        assertEquals(3, futureEvents.size)
        assertEquals("event2", futureEvents[0].id) // Sorted by time
        assertEquals("event1", futureEvents[1].id)
        assertEquals("event3", futureEvents[2].id)
    }
    
    @Test
    fun `getEventsAtTime returns events at exact time`() {
        val event1 = createTestEvent("event1", GameTime(60))
        val event2 = createTestEvent("event2", GameTime(60))
        val event3 = createTestEvent("event3", GameTime(90))
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(event1, event2, event3)
        )
        
        val events = timeline.getEventsAtTime(GameTime(60))
        
        assertEquals(2, events.size)
        assertTrue(events.any { it.id == "event1" })
        assertTrue(events.any { it.id == "event2" })
        assertFalse(events.any { it.id == "event3" })
    }
    
    @Test
    fun `getUpcomingEvents returns limited future events`() {
        val events = (1..10).map { i ->
            createTestEvent("event$i", GameTime(i * 10))
        }
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = events
        )
        
        val upcoming = timeline.getUpcomingEvents(GameTime(0), limit = 5)
        
        assertEquals(5, upcoming.size)
        assertEquals("event1", upcoming[0].id)
        assertEquals("event5", upcoming[4].id)
    }
    
    @Test
    fun `getUpcomingEvents returns fewer events when limit exceeds available`() {
        val event1 = createTestEvent("event1", GameTime(30))
        val event2 = createTestEvent("event2", GameTime(60))
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(event1, event2)
        )
        
        val upcoming = timeline.getUpcomingEvents(GameTime(0), limit = 10)
        
        assertEquals(2, upcoming.size) // Only 2 available, not 10
    }
    
    @Test
    fun `getPastEvents returns empty list when no past events`() {
        val event = createTestEvent("event1", GameTime(60))
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(event)
        )
        
        val pastEvents = timeline.getPastEvents(GameTime(30))
        
        assertTrue(pastEvents.isEmpty())
    }
    
    @Test
    fun `getFutureEvents returns empty list when no future events`() {
        val event = createTestEvent("event1", GameTime(30))
        val timeline = Timeline(
            startTime = GameTime(0),
            endTime = GameTime(480),
            events = listOf(event)
        )
        
        val futureEvents = timeline.getFutureEvents(GameTime(60))
        
        assertTrue(futureEvents.isEmpty())
    }
}

