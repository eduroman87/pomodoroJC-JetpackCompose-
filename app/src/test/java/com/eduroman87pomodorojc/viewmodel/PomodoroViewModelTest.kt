import com.eduroman87.pomodorojc.domain.TimerState
import com.eduroman87.pomodorojc.viewmodel.PomodoroViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class PomodoroViewModelTest {

    @Test
    fun `initialize configura correctamente los valores iniciales`() {
        val vm = PomodoroViewModel()
        val workMillis = 1500_000L // 25 min
        val restMillis = 300_000L  // 5 min
        val cycles = 4

        vm.initialize(workMillis, restMillis, cycles)

        assertEquals(workMillis, vm.timeLeft.value)
        assertEquals(TimerState.State.WORKING, vm.currentState.value)
        assertEquals(1, vm.currentCycle.value)
    }

    @Test
    fun `pause cancela el temporizador y cambia el estado a PAUSED`() {
        val vm = PomodoroViewModel()
        vm.initialize(1500_000L, 300_000L, 4)

        vm.pause()

        assertEquals(TimerState.State.PAUSED, vm.currentState.value)
    }

    @Test
    fun `reset reinicia el temporizador al estado inicial`() {
        val vm = PomodoroViewModel()
        vm.initialize(1500_000L, 300_000L, 4)

        // Simula avance de estado
        vm.pause()
        vm.reset()

        assertEquals(TimerState.State.WORKING, vm.currentState.value)
        assertEquals(1, vm.currentCycle.value)
        assertEquals(1500_000L, vm.timeLeft.value)
    }

    @Test
    fun `setToIdle cambia el estado a IDLE`() {
        val vm = PomodoroViewModel()
        vm.initialize(1500_000L, 300_000L, 4)

        vm.setToIdle()

        assertEquals(TimerState.State.IDLE, vm.currentState.value)
    }


}
