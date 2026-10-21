package com.zaryif.minimalos.domain.timer

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.zaryif.minimalos.MinimalApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Global Pomodoro and countdown timer manager.
 * Runs independently of UI lifecycle so countdowns persist across screen transitions.
 */
object PomodoroTimerManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var tickerJob: Job? = null

    // Work & Break durations in seconds
    private val _workDurationSeconds = MutableStateFlow(25 * 60)
    val workDurationSeconds: StateFlow<Int> = _workDurationSeconds.asStateFlow()

    private val _breakDurationSeconds = MutableStateFlow(5 * 60)
    val breakDurationSeconds: StateFlow<Int> = _breakDurationSeconds.asStateFlow()

    // Countdown state
    private val _remainingSeconds = MutableStateFlow(25 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _isBreakMode = MutableStateFlow(false)
    val isBreakMode: StateFlow<Boolean> = _isBreakMode.asStateFlow()

    private val _timerLabel = MutableStateFlow("pomodoro")
    val timerLabel: StateFlow<String> = _timerLabel.asStateFlow()

    fun setWorkMinutes(minutes: Int) {
        val safeMinutes = minutes.coerceIn(1, 180)
        _workDurationSeconds.value = safeMinutes * 60
        if (!_isRunning.value && !_isBreakMode.value) {
            _remainingSeconds.value = safeMinutes * 60
        }
    }

    fun setBreakMinutes(minutes: Int) {
        val safeMinutes = minutes.coerceIn(1, 60)
        _breakDurationSeconds.value = safeMinutes * 60
        if (!_isRunning.value && _isBreakMode.value) {
            _remainingSeconds.value = safeMinutes * 60
        }
    }

    fun start(seconds: Int? = null, label: String = "pomodoro") {
        if (seconds != null) {
            _remainingSeconds.value = seconds
            _workDurationSeconds.value = seconds
        }
        _timerLabel.value = label
        _isRunning.value = true
        startTicker()
    }

    fun pause() {
        _isRunning.value = false
        tickerJob?.cancel()
        tickerJob = null
    }

    fun toggle() {
        if (_isRunning.value) {
            pause()
        } else {
            if (_remainingSeconds.value <= 0) {
                reset()
            }
            _isRunning.value = true
            startTicker()
        }
    }

    fun reset() {
        pause()
        _remainingSeconds.value = if (_isBreakMode.value) _breakDurationSeconds.value else _workDurationSeconds.value
    }

    fun setBreakMode(isBreak: Boolean) {
        pause()
        _isBreakMode.value = isBreak
        _timerLabel.value = if (isBreak) "break" else "pomodoro"
        _remainingSeconds.value = if (isBreak) _breakDurationSeconds.value else _workDurationSeconds.value
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (_isRunning.value && _remainingSeconds.value > 0) {
                delay(1000)
                if (_remainingSeconds.value > 0) {
                    _remainingSeconds.value--
                }
                if (_remainingSeconds.value == 0) {
                    _isRunning.value = false
                    onTimerFinished()
                    break
                }
            }
        }
    }

    private fun onTimerFinished() {
        try {
            val context = MinimalApp.instance
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.vibrate(CombinedVibration.createParallel(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1)))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                v?.vibrate(longArrayOf(0, 400, 200, 400), -1)
            }
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            ringtone?.play()
        } catch (_: Exception) {}

        if (!_isBreakMode.value) {
            _isBreakMode.value = true
            _timerLabel.value = "break"
            _remainingSeconds.value = _breakDurationSeconds.value
        } else {
            _isBreakMode.value = false
            _timerLabel.value = "pomodoro"
            _remainingSeconds.value = _workDurationSeconds.value
        }
    }
}
