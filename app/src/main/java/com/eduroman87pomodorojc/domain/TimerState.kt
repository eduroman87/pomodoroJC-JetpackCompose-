package com.eduroman87.pomodorojc.domain

/**
 * Gestiona el estado interno del temporizador Pomodoro.
 * Mantiene las duraciones de estudio y descanso, el total de ciclos
 * y el estado dinámico del temporizador.
 *
 * @param studyTime   Duración de cada sesión de estudio en milisegundos.
 * @param restTime    Duración de cada sesión de descanso en milisegundos.
 * @param totalCycles Número total de ciclos (estudio + descanso) configurados.
 */
class TimerState(
    val studyTime: Long,
    val restTime: Long,
    val totalCycles: Int
) {

    /**
     * Estados posibles del temporizador:
     *  - IDLE     : No iniciado.
     *  - WORKING  : En fase de estudio.
     *  - RESTING  : En fase de descanso.
     *  - PAUSED   : Pausado manualmente.
     *  - FINISHED : Todos los ciclos completados.
     */
    enum class State {
        IDLE,
        WORKING,
        RESTING,
        PAUSED,
        FINISHED
    }

    // Estado actual; comienza en WORKING para iniciar el primer ciclo automáticamente
    var currentState: State = State.WORKING

    // Índice del ciclo activo, arranca en 1 y avanza tras cada descanso completado
    var currentCycle: Int = 1

    // Tiempo restante de la fase activa; inicializado con studyTime para el primer ciclo
    var timeLeft: Long = studyTime

    /**
     * Avanza el temporizador al siguiente estado lógico:
     *
     * - Si está en WORKING → pasa a RESTING y carga restTime.
     * - Si está en RESTING → incrementa currentCycle:
     *     • Si supera totalCycles → marca FINISHED y pone timeLeft a 0.
     *     • En caso contrario → vuelve a WORKING y carga studyTime.
     * - Los estados IDLE, PAUSED y FINISHED no cambian aquí.
     */
    fun nextState() {
        when (currentState) {
            State.WORKING -> {
                // Trabajo finalizado: inicia descanso
                currentState = State.RESTING
                timeLeft = restTime
            }
            State.RESTING -> {
                // Descanso finalizado: cuenta un ciclo y decide siguiente paso
                currentCycle++
                if (currentCycle > totalCycles) {
                    // No quedan más ciclos: completa el temporizador
                    currentState = State.FINISHED
                    timeLeft = 0
                } else {
                    // Inicia un nuevo ciclo de estudio
                    currentState = State.WORKING
                    timeLeft = studyTime
                }
            }
            else -> {
                // IDLE, PAUSED y FINISHED no provocan transición automática
            }
        }
    }

    /**
     * Reinicia el temporizador al estado inicial:
     *  - Vuelve al primer ciclo (currentCycle = 1).
     *  - Pone el estado en WORKING.
     *  - Restaura timeLeft al valor de estudio (studyTime).
     */
    fun reset() {
        currentState = State.WORKING
        currentCycle = 1
        timeLeft = studyTime
    }
}
