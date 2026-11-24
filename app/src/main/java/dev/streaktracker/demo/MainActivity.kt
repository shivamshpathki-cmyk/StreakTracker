package dev.streaktracker.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF226343), background = Color(0xFFF5F8F5), surface = Color.White)) { TrackerScreen() } }
    }
}

@Composable
fun TrackerScreen(vm: TrackerViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val today = LocalDate.now()
    var selected by remember { mutableStateOf(habits.first()) }
    var month by remember { mutableStateOf(YearMonth.from(today)) }
    val context = LocalContext.current
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) vm.reminders(true) }
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("STREAKTRACKER", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text("Small steps, daily.", style = MaterialTheme.typography.headlineMedium)
            Text("Build routines at your own pace.", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                habits.forEach { habit -> FilterChip(selected = selected == habit, onClick = { selected = habit }, label = { Text(habit.label) }) }
            }
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("${currentStreak(state, selected, today)} days", style = MaterialTheme.typography.displaySmall)
                    Text("Current ${selected.label.lowercase()} streak")
                    Text("Missed days are okay. Start again anytime.", style = MaterialTheme.typography.bodySmall)
                }
            }
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = { month = month.minusMonths(1) }) { Text("Prev") }
                        Text(month.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.US)), style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { month = month.plusMonths(1) }) { Text("Next") }
                    }
                    Row(Modifier.fillMaxWidth()) { listOf("M", "T", "W", "T", "F", "S", "S").forEach { day -> Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { Text(day, style = MaterialTheme.typography.labelMedium) } } }
                    val offset = month.atDay(1).dayOfWeek.value - 1
                    val rows = (offset + month.lengthOfMonth() + 6) / 7
                    repeat(rows) { week ->
                        Row(Modifier.fillMaxWidth()) {
                            repeat(7) { column ->
                                val number = week * 7 + column - offset + 1
                                Box(Modifier.weight(1f).height(49.dp), contentAlignment = Alignment.Center) {
                                    if (number in 1..month.lengthOfMonth()) {
                                        val date = month.atDay(number)
                                        val done = state.completed(selected, date)
                                        FilledTonalButton(onClick = { vm.toggle(selected, date) }, enabled = !date.isAfter(today),
                                            modifier = Modifier.fillMaxWidth().padding(1.dp).height(44.dp).semantics { contentDescription = "$date, ${if (done) "completed" else "not completed"}"; this.selected = done },
                                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = if (done) Color(0xFF226343) else Color(0xFFE8EEE9), contentColor = if (done) Color.White else Color(0xFF203628)),
                                            contentPadding = PaddingValues(0.dp)) { Text("$number", style = MaterialTheme.typography.labelMedium) }
                                    }
                                }
                            }
                        }
                    }
                    Text("Tap a day to mark or undo completion.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                }
            }
            Button(onClick = { vm.toggle(selected, today) }, modifier = Modifier.fillMaxWidth()) { Text(if (state.completed(selected, today)) "Undo today's check-in" else "Mark today complete") }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) { Text("Daily reminder", style = MaterialTheme.typography.titleMedium); Text("Around 10 PM • delivery may vary", style = MaterialTheme.typography.bodySmall) }
                Switch(checked = state.reminders, onCheckedChange = { enabled ->
                    if (enabled && Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else vm.reminders(enabled)
                })
            }
            Text("Demo • Your habit data stays on this device.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
