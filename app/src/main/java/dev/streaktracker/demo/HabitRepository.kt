package dev.streaktracker.demo

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate

private val Context.habitStore by preferencesDataStore("habits")

data class Habit(val id: String, val label: String)
val habits = listOf(Habit("read", "Read"), Habit("study", "Study"), Habit("walk", "Walk"))
data class TrackerState(val days: Set<String> = emptySet(), val reminders: Boolean = false) {
    fun completed(habit: Habit, date: LocalDate) = "${habit.id}:$date" in days
}
class HabitRepository(private val context: Context) {
    private val entries = stringSetPreferencesKey("entries")
    private val reminders = booleanPreferencesKey("reminders")
    val state: Flow<TrackerState> = context.habitStore.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }.map { TrackerState(it[entries] ?: emptySet(), it[reminders] ?: false) }
    suspend fun toggle(habit: Habit, date: LocalDate) {
        context.habitStore.edit { prefs ->
            val key = "${habit.id}:$date"
            val updated = (prefs[entries] ?: emptySet()).toMutableSet()
            if (!updated.add(key)) updated.remove(key)
            prefs[entries] = updated
        }
    }
    suspend fun setReminders(enabled: Boolean) { context.habitStore.edit { it[reminders] = enabled } }
}

fun currentStreak(state: TrackerState, habit: Habit, today: LocalDate): Int {
    var day = if (state.completed(habit, today)) today else today.minusDays(1)
    var count = 0
    while (state.completed(habit, day)) { count++; day = day.minusDays(1) }
    return count
}
