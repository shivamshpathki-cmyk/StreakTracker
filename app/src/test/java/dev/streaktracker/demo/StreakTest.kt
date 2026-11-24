package dev.streaktracker.demo
import org.junit.Test
import org.junit.Assert.assertEquals
import java.time.LocalDate
class StreakTest {
    private val today = LocalDate.of(2026, 11, 24)
    private val habit = Habit("read", "Read")
    @Test fun empty() { assertEquals(0, currentStreak(TrackerState(), habit, today)) }
    @Test fun includesToday() { assertEquals(2, currentStreak(TrackerState(setOf("read:2026-11-24", "read:2026-11-23")), habit, today)) }
    @Test fun yesterdayCountsUntilTodayEnds() { assertEquals(1, currentStreak(TrackerState(setOf("read:2026-11-23", "read:2026-11-21")), habit, today)) }
    @Test fun independentHabits() { assertEquals(0, currentStreak(TrackerState(setOf("study:2026-11-24")), habit, today)) }
}
