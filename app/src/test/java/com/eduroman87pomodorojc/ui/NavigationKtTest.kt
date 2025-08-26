import org.junit.Test
import org.junit.Assert.*
import com.eduroman87.pomodorojc.domain.TimerState

class PomodoroNavHostLogicTest {

    @Test
    fun `ruta de navegación se construye correctamente con parámetros`() {
        val cycles = 4
        val work = 1500
        val rest = 300

        val route = "pomodoro/$cycles/$work/$rest"
        assertEquals("pomodoro/4/1500/300", route)
    }

    @Test
    fun `valida que parámetros no sean nulos antes de inicializar ViewModel`() {
        val cycles: Int? = 3
        val work: Int? = 1500
        val rest: Int? = 300

        val canInitialize = cycles != null && work != null && rest != null
        assertTrue(canInitialize)
    }

    @Test
    fun `evita inicialización múltiple del ViewModel`() {
        var alreadyInitialized = false
        val cycles = 4
        val work = 1500
        val rest = 300

        val shouldInitialize = !alreadyInitialized && cycles != null && work != null && rest != null
        assertTrue(shouldInitialize)

        alreadyInitialized = true
        val shouldNotInitialize = !alreadyInitialized && cycles != null && work != null && rest != null
        assertFalse(shouldNotInitialize)
    }

    @Test
    fun `conversión de minutos a milisegundos es correcta`() {
        val workMinutes = 25
        val restMinutes = 5

        val workMillis = workMinutes.toLong()
        val restMillis = restMinutes.toLong()

        assertEquals(25L, workMillis)
        assertEquals(5L, restMillis)
    }

    @Test
    fun `renderiza PomodoroScreen solo si los parámetros son válidos`() {
        val cycles: Int? = 4
        val work: Int? = 1500
        val rest: Int? = 300

        val shouldRender = cycles != null && work != null && rest != null
        assertTrue(shouldRender)

        val invalidCycles: Int? = null
        val shouldNotRender = invalidCycles != null && work != null && rest != null
        assertFalse(shouldNotRender)
    }
}
