package com.eduroman87.pomodorojc.viewmodel

// Importaciones necesarias para arquitectura MVVM y coroutines
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduroman87.pomodorojc.domain.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la lógica del temporizador Pomodoro.
 * Se encarga de controlar el tiempo, los ciclos, el estado actual y exponerlo a la UI.
 */
class PomodoroViewModel : ViewModel() {

    private var initialWorkMinutes: Int = 0
    private var initialRestMinutes: Int = 0
    private var initialCycles: Int = 0

        fun initialize(workMillis: Long, restMillis: Long, cycles: Int) {
            initialWorkMinutes = (workMillis / 60_000L).toInt()
            initialRestMinutes = (restMillis / 60_000L).toInt()
            initialCycles = cycles

            timerState = TimerState(
                studyTime = workMillis,
                restTime = restMillis,
                totalCycles = cycles
            )

            _timeLeft.value = timerState.timeLeft
            _currentState.value = timerState.currentState
            _currentCycle.value = timerState.currentCycle
        }



    // Instancia del estado del temporizador, que guarda tiempos, ciclos y estados.
    private var timerState = TimerState(0L, 0L, 0)


    // Flow mutable que representa el tiempo restante en milisegundos.
    // Usado para actualizar la UI cada segundo.
    private val _timeLeft = MutableStateFlow(timerState.timeLeft)
    val timeLeft: StateFlow<Long> get() = _timeLeft // Estado observable para la pantalla

    // Estado actual del temporizador (ESTUDIANDO, DESCANSANDO, etc.)
    private val _currentState = MutableStateFlow(timerState.currentState)
    val currentState: StateFlow<TimerState.State> get() = _currentState

    // Ciclo actual (del 1 al número máximo configurado)
    private val _currentCycle = MutableStateFlow(timerState.currentCycle)
    val currentCycle: StateFlow<Int> get() = _currentCycle

    // Job que representa el temporizador activo. Se cancela al pausar o reiniciar.
    private var timerJob: Job? = null

    /**
     * Inicia el temporizador si aún no ha finalizado o si no está corriendo.
     * Se lanza una coroutine que decrementa el tiempo cada segundo.
     * Al finalizar el tiempo, se avanza al siguiente estado (descanso o nuevo ciclo).
     */
    fun start() {
        // Evita iniciar si ya está corriendo o el Pomodoro ha terminado.
        if (timerState.currentState == TimerState.State.FINISHED || timerJob?.isActive == true) return

        // Inicia la coroutine del temporizador dentro del scope del ViewModel.
        timerJob = viewModelScope.launch {
            _currentState.value = timerState.currentState
            while (timerState.timeLeft > 0) {
                delay(1000)                         // Espera 1 segundo
                timerState.timeLeft -= 1000         // Reduce tiempo restante
                _timeLeft.emit(timerState.timeLeft) // Notifica nuevo valor a la UI
            }

            // Cuando el tiempo llega a cero, cambia de estado (descanso / estudio / fin).
            timerState.nextState()

            // Actualiza los valores observables que la UI usa para renderizar.
            _currentState.emit(timerState.currentState)
            _currentCycle.emit(timerState.currentCycle)
            _timeLeft.emit(timerState.timeLeft)

            // Si aún no ha finalizado, inicia automáticamente el siguiente ciclo.
            if (timerState.currentState != TimerState.State.FINISHED) {
                timerJob = null
                start()
            }
        }
    }

    /**
     * Pausa el temporizador cancelando el Job activo.
     */
    fun pause() {
        timerJob?.cancel() // Cancela la coroutine del temporizador
        timerJob = null    // Limpia la referencia para evitar reinicios accidentales
        _currentState.value = TimerState.State.PAUSED
    }

    /**
     * Reinicia el temporizador al estado inicial (primer ciclo, tiempo completo).
     * También cancela cualquier temporizador en curso.
     */
    fun reset() {
        timerJob?.cancel()
        timerJob = null
        timerState.reset() // Vuelve al estado inicial en TimerState
        _timeLeft.value = timerState.timeLeft       // Actualiza valores mostrados en UI
        _currentState.value = timerState.currentState
        _currentCycle.value = timerState.currentCycle
    }

    fun setToIdle() {
        _currentState.value = TimerState.State.IDLE
    }

    /**
     * Se ejecuta automáticamente cuando el ViewModel es destruido.
     * Garantiza que el temporizador no quede corriendo en segundo plano.
     */
    override fun onCleared() {
        super.onCleared()
        pause()
    }
}
