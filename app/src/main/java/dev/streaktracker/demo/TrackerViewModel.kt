package dev.streaktracker.demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class TrackerViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = HabitRepository(app)
    val state = repository.state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrackerState())
    fun toggle(habit: Habit, date: LocalDate) = viewModelScope.launch { repository.toggle(habit, date) }
    fun reminders(enabled: Boolean) = viewModelScope.launch {
        repository.setReminders(enabled)
        if (enabled) ReminderWorker.schedule(getApplication<Application>())
        else ReminderWorker.cancel(getApplication<Application>())
    }
}
