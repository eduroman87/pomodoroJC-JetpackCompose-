package com.eduroman87.pomodorojc

import com.eduroman87.pomodorojc.domain.TimerState
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias para la clase TimerState.
 * Verificamos los cambios de estado y reinicio del temporizador.
 */
class TimerStateTest {

    @Test
    fun testInitialState() {
        val timer = TimerState(studyTime = 1500000, restTime = 300000, totalCycles = 4)
        assertEquals(TimerState.State.WORKING, timer.currentState)
        assertEquals(1, timer.currentCycle)
        assertEquals(1500000, timer.timeLeft)
    }

    @Test
    fun testNextStateCycle() {
        val timer = TimerState(studyTime = 1500000, restTime = 300000, totalCycles = 2)

        // Del estado estudiar a descansar
        timer.nextState()
        assertEquals(TimerState.State.RESTING, timer.currentState)
        assertEquals(1, timer.currentCycle)
        assertEquals(300000, timer.timeLeft)

        // Del estado descansar a estudiar (siguiente ciclo)
        timer.nextState()
        assertEquals(TimerState.State.WORKING, timer.currentState)
        assertEquals(2, timer.currentCycle)
        assertEquals(1500000, timer.timeLeft)

        // Avanzar ciclo final (cuando sobrepasa totalCycles)
        timer.nextState()  // descanso del ciclo 2
        timer.nextState()  // debería finalizar
        assertEquals(TimerState.State.FINISHED, timer.currentState)
        assertEquals(0, timer.timeLeft)
    }

    @Test
    fun testReset() {
        val timer = TimerState(studyTime = 1500000, restTime = 300000, totalCycles = 4)
        timer.nextState()
        timer.reset()
        assertEquals(TimerState.State.WORKING, timer.currentState)
        assertEquals(1, timer.currentCycle)
        assertEquals(1500000, timer.timeLeft)
    }
}
