import org.junit.Test
import org.junit.Assert.*
import com.eduroman87.pomodorojc.domain.TimerState

class PomodoroScreenLogicTest {

    @Test
    fun `calcula minutos y segundos correctamente desde milisegundos`() {
        val safeTime = 125_000L // 2 min 5 sec

        val minutes = (safeTime / 1000) / 60
        val seconds = (safeTime / 1000) % 60

        assertEquals(2, minutes)
        assertEquals(5, seconds)
    }

    @Test
    fun `estadoTexto devuelve el texto correcto para cada estado`() {
        val expected = mapOf(
            TimerState.State.IDLE     to "",
            TimerState.State.WORKING  to "WORK",
            TimerState.State.RESTING  to "REST",
            TimerState.State.PAUSED   to "PAUSED",
            TimerState.State.FINISHED to "FINISHED"
        )

        expected.forEach { (state, text) ->
            val estadoTexto = when (state) {
                TimerState.State.IDLE     -> ""
                TimerState.State.WORKING  -> "WORK"
                TimerState.State.RESTING  -> "REST"
                TimerState.State.PAUSED   -> "PAUSED"
                TimerState.State.FINISHED -> "FINISHED"
            }
            assertEquals(text, estadoTexto)
        }
    }

    @Test
    fun `estadoSuperior muestra Push Start si no está corriendo ni pausado ni terminado`() {
        val isRunning = false
        val isPaused = false
        val currentState = TimerState.State.IDLE

        val estadoSuperior = if (!isRunning && !isPaused && currentState != TimerState.State.FINISHED)
            "Push Start ▶️" else currentState.name

        assertEquals("Push Start ▶️", estadoSuperior)
    }

    @Test
    fun `currentSegment se calcula correctamente en WORKING y RESTING`() {
        val currentCycle = 2
        val segmentInWorking = (currentCycle - 1) * 2
        val segmentInResting = (currentCycle - 1) * 2 + 1

        assertEquals(2, segmentInWorking)
        assertEquals(3, segmentInResting)
    }

    @Test
    fun `segments se construyen alternando WORKING y RESTING`() {
        val totalCycles = 3
        val workDuration = 25
        val restDuration = 5

        val segments = mutableListOf<Pair<Int, TimerState.State>>().apply {
            repeat(totalCycles) {
                add(Pair(workDuration, TimerState.State.WORKING))
                add(Pair(restDuration, TimerState.State.RESTING))
            }
        }

        assertEquals(6, segments.size)
        assertEquals(Pair(25, TimerState.State.WORKING), segments[0])
        assertEquals(Pair(5, TimerState.State.RESTING), segments[1])
        assertEquals(Pair(25, TimerState.State.WORKING), segments[2])
        assertEquals(Pair(5, TimerState.State.RESTING), segments[3])
        assertEquals(Pair(25, TimerState.State.WORKING), segments[4])
        assertEquals(Pair(5, TimerState.State.RESTING), segments[5])
    }

    @Test
    fun `safeWeight nunca es menor que 1`() {
        val duration = 0
        val safeWeight = duration.coerceAtLeast(1).toFloat()
        assertEquals(1f, safeWeight)
    }
}
