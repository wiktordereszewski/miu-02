package miu.miu_02

import android.content.res.Configuration
import miu.miu_02.ui.theme.AppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.text.format

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val isDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDarkTheme // jasne ikony na ciemnym tle i odwrotnie
        }

        setContent {
            AppTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DateCalculatorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorScreen() {
    var firstDate by remember { mutableStateOf(Calendar.getInstance()) }
    var secondDate by remember { mutableStateOf(Calendar.getInstance()) }
    var resultDays by remember { mutableStateOf("") }
    var resultHours by remember { mutableStateOf("") }
    var resultMinutes by remember { mutableStateOf("") }
    var resultSeconds by remember { mutableStateOf("") }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showFirstDatePicker by remember { mutableStateOf(false) }
    var showSecondDatePicker by remember { mutableStateOf(false) }
    var showFirstTimePicker by remember { mutableStateOf(false) }
    var showSecondTimePicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun calculateDate() {
        // Get the time difference in milliseconds
        val diffInMillis = kotlin.math.abs(secondDate.timeInMillis - firstDate.timeInMillis)

        // Convert to seconds
        val totalSeconds = diffInMillis / 1000

        // Calculate days, hours, minutes, and remaining seconds
        val days = totalSeconds / (24 * 3600)
        val remainingAfterDays = totalSeconds % (24 * 3600)
        val hours = remainingAfterDays / 3600
        val remainingAfterHours = remainingAfterDays % 3600
        val minutes = remainingAfterHours / 60
        val seconds = remainingAfterHours % 60

        // Update the result variables
        resultDays = days.toString()
        resultHours = hours.toString()
        resultMinutes = minutes.toString()
        resultSeconds = seconds.toString()
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Informacje o aplikacji") },
            text = {
                Column {
                    Text("Aplikacja umożliwia obliczenie różnicy dat. Podana róźnica pomiędzy datami wyrażona jest co do sekundy.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Jak korzystać z kalkulatora dat:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Wybierz datę początkową klikając na pole daty. Możesz wybrać ją z kalendarza lub wpisać ręcznie. Format wprowadzania daty jest zależy od języka urządzenia. Format godziny podany jest w formacie 24-godzinnym.")
                    Text("2. Wybierz datę końcową klikając na pole daty. Możesz wybrać ją z kalendarza lub wpisać ręcznie. Format wprowadzania daty jest zależy od języka urządzenia. Format godziny podany jest w formacie 24-godzinnym.")
                    Text("Zakres dat: 01.01.1900 00:00:00 - 31.12.2100 23:59:59.")
                    Text("3. Naciśnij przycisk 'Oblicz' aby obliczyć różnicę między datami. W przypadku błędu zostanie wyświetlony komunikat.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Zamknij")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("Kalkulator Daty") },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Informacje o aplikacji"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState, modifier = Modifier.offset(y = (-10).dp)) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Kalkulator róźnicy dat",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))


            // Pierwsza data
            DateTimeSelector(
                label = "Data początkowa:",
                currentDate = firstDate,
                onDateTimeSelected = { firstDate = it },
                showDatePicker = showFirstDatePicker,
                onShowDatePickerChange = { showFirstDatePicker = it },
                showTimePicker = showFirstTimePicker,
                onShowTimePickerChange = { showFirstTimePicker = it }
            )

            // Druga data
            DateTimeSelector(
                label = "Data końcowa:",
                currentDate = secondDate,
                onDateTimeSelected = { secondDate = it },
                showDatePicker = showSecondDatePicker,
                onShowDatePickerChange = { showSecondDatePicker = it },
                showTimePicker = showSecondTimePicker,
                onShowTimePickerChange = { showSecondTimePicker = it }
            )

            // Przycisk oblicz
            Button(
                onClick = {
                    if (firstDate > secondDate) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Data początkowa nie może być późniejsza niż końcowa",
                                duration = SnackbarDuration.Short,

                                )
                        }
                    } else {
                        calculateDate()
                    }
                }
            ) {
                Text("Oblicz różnicę")
            }

            // Wyświelanie wyniku w postaci pól tekstowych
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = resultDays,
                    onValueChange = { resultDays = it },
                    label = { Text("Dni") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = resultHours,
                    onValueChange = { resultHours = it },
                    label = { Text("Godziny") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = resultMinutes,
                    onValueChange = { resultMinutes = it },
                    label = { Text("Minuty") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = resultSeconds,
                    onValueChange = { resultSeconds = it },
                    label = { Text("Sekundy") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = true
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeSelector(
    label: String,
    currentDate: Calendar,
    onDateTimeSelected: (Calendar) -> Unit,
    showDatePicker: Boolean,
    onShowDatePickerChange: (Boolean) -> Unit,
    showTimePicker: Boolean,
    onShowTimePickerChange: (Boolean) -> Unit
) {
    // Date selection card
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onShowDatePickerChange(true) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(currentDate.time),
                fontSize = 18.sp
            )
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentDate.timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = { onShowDatePickerChange(false) },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val tempCalendar = Calendar.getInstance().apply {
                                timeInMillis = it
                                set(Calendar.HOUR_OF_DAY, currentDate.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, currentDate.get(Calendar.MINUTE))
                                set(Calendar.SECOND, currentDate.get(Calendar.SECOND))
                            }
                            onDateTimeSelected(tempCalendar)
                            onShowDatePickerChange(false)
                            onShowTimePickerChange(true)
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { onShowDatePickerChange(false) }) {
                    Text("Anuluj")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time picker dialog
    if (showTimePicker) {
        Material3TimePickerWithSeconds(
            initialHour = currentDate.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentDate.get(Calendar.MINUTE),
            initialSecond = currentDate.get(Calendar.SECOND),
            onTimeSelected = { hour, minute, second ->
                val updatedCalendar = currentDate.clone() as Calendar
                updatedCalendar.apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, second)
                }
                onDateTimeSelected(updatedCalendar)
                onShowTimePickerChange(false)
            },
            onDismiss = { onShowTimePickerChange(false) }
        )
    }
}

@Composable
fun Material3TimePickerWithSeconds(
    onTimeSelected: (hour: Int, minute: Int, second: Int) -> Unit,
    onDismiss: () -> Unit,
    initialHour: Int = 0,
    initialMinute: Int = 0,
    initialSecond: Int = 0
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }
    var selectedSecond by remember { mutableIntStateOf(initialSecond) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(328.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Wybierz czas",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )

                // Time display
                val timeText = String.format(Locale.US,
                    "%02d:%02d:%02d",
                    selectedHour,
                    selectedMinute,
                    selectedSecond
                )

                Text(
                    text = timeText,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Divider line
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Time picker
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(vertical = 12.dp)
                ) {
                    CyclicTimePickerColumn(
                        label = "Godzina",
                        maxValue = 23,
                        initialValue = selectedHour,
                        onValueChanged = { selectedHour = it }
                    )
                    CyclicTimePickerColumn(
                        label = "Minuta",
                        maxValue = 59,
                        initialValue = selectedMinute,
                        onValueChanged = { selectedMinute = it }
                    )
                    CyclicTimePickerColumn(
                        label = "Sekunda",
                        maxValue = 59,
                        initialValue = selectedSecond,
                        onValueChanged = { selectedSecond = it }
                    )
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Cancel Button (Text Button)
                    Button(onClick = onDismiss) {
                        Text("Anuluj")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // OK Button (Filled Button)
                    Button(
                        onClick = {
                            onTimeSelected(selectedHour, selectedMinute, selectedSecond)
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun CyclicTimePickerColumn(
    label: String,
    maxValue: Int,
    initialValue: Int,
    onValueChanged: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Thin line separator
        HorizontalDivider(
            modifier = Modifier
                .width(40.dp)
                .padding(vertical = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        val itemHeight = 50.dp
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .height(150.dp)
                .width(60.dp)
        ) {
            // Highlighted center item
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(itemHeight)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )

            val listState = rememberLazyListState()
            var currentValue by remember { mutableIntStateOf(initialValue) }

            val itemCount = 10000
            val middleIndex = itemCount / 2
            val initialIndex = middleIndex - (middleIndex % (maxValue + 1)) + initialValue

            // Set initial index
            LaunchedEffect(Unit) {
                listState.scrollToItem(initialIndex)
            }

            // Custom snapping behavior
            LaunchedEffect(listState.isScrollInProgress) {
                if (!listState.isScrollInProgress) {
                    // Scroll ended - snap to nearest item
                    val firstItemIndex = listState.firstVisibleItemIndex
                    val firstVisibleItemOffset = listState.firstVisibleItemScrollOffset

                    // Calculate which item should be centered
                    val targetIndex = if (firstVisibleItemOffset > itemHeight.value / 2) {
                        firstItemIndex + 1
                    } else {
                        firstItemIndex
                    }

                    // Animate scroll to perfectly center the target item
                    coroutineScope.launch {
                        listState.animateScrollToItem(targetIndex)
                    }

                    // Update the selected value
                    val newValue = targetIndex % (maxValue + 1)
                    if (newValue != currentValue) {
                        currentValue = newValue
                        onValueChanged(newValue)
                    }
                }
            }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = itemHeight),
                modifier = Modifier.fillMaxSize()
            ) {
                items(itemCount) { index ->
                    val value = index % (maxValue + 1)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = String.format(Locale.US, "%02d", value),
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (value == currentValue)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}