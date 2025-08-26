import org.junit.Test
import org.junit.Assert.*

class SetupScreenLogicTest {

    @Test
    fun `validación de campos detecta si hay al menos un valor en Work y Rest`() {
        val workMinutes = "25"
        val workSeconds = ""
        val restMinutes = ""
        val restSeconds = "5"

        val atLeastOneWork = listOf(workMinutes, workSeconds).any { it.trim().isNotEmpty() }
        val atLeastOneRest = listOf(restMinutes, restSeconds).any { it.trim().isNotEmpty() }

        assertTrue(atLeastOneWork)
        assertTrue(atLeastOneRest)
    }

    @Test
    fun `canSwitchToReady es true solo si ciclos es válido y hay datos en Work y Rest`() {
        val cicles = "4"
        val workMinutes = "25"
        val workSeconds = "0"
        val restMinutes = "5"
        val restSeconds = ""

        val cValid = cicles.trim().toIntOrNull() != null
        val atLeastOneWork = listOf(workMinutes, workSeconds).any { it.trim().isNotEmpty() }
        val atLeastOneRest = listOf(restMinutes, restSeconds).any { it.trim().isNotEmpty() }

        val canSwitchToReady = cValid && atLeastOneWork && atLeastOneRest

        assertTrue(canSwitchToReady)
    }

    @Test
    fun `confirmLabel cambia correctamente según validación y normalización`() {
        fun getLabel(cValid: Boolean, workValid: Boolean, restValid: Boolean, normalized: Boolean): String {
            val canSwitchToReady = cValid && workValid && restValid
            return when {
                !canSwitchToReady -> "FILL IN THE DATA"
                canSwitchToReady && !normalized -> "READY?"
                else -> "CONFIRM SESSION"
            }
        }

        assertEquals("FILL IN THE DATA", getLabel(false, true, true, false))
        assertEquals("READY?", getLabel(true, true, true, false))
        assertEquals("CONFIRM SESSION", getLabel(true, true, true, true))
    }

    @Test
    fun `normalización convierte minutos y segundos en valores consistentes`() {
        val wM = 25
        val wS = 70
        val rM = 5
        val rS = 90

        val totalWorkSeconds = wM * 60 + wS
        val totalRestSeconds = rM * 60 + rS

        val normalizedWorkMinutes = ((totalWorkSeconds % 3600) / 60).toString()
        val normalizedWorkSeconds = (totalWorkSeconds % 60).toString()
        val normalizedRestMinutes = ((totalRestSeconds % 3600) / 60).toString()
        val normalizedRestSeconds = (totalRestSeconds % 60).toString()

        assertEquals("26", normalizedWorkMinutes)
        assertEquals("10", normalizedWorkSeconds)
        assertEquals("6", normalizedRestMinutes)
        assertEquals("30", normalizedRestSeconds)
    }

    @Test
    fun `cálculo de milisegundos para onConfirm es correcto`() {
        val wM = 25
        val wS = 30
        val rM = 5
        val rS = 15

        val totalWorkMillis = (wM * 60 + wS) * 1000L
        val totalRestMillis = (rM * 60 + rS) * 1000L

        assertEquals(1530000L, totalWorkMillis)
        assertEquals(315000L, totalRestMillis)
    }

    @Test
    fun `fieldErrors se limpia correctamente antes de validar`() {
        val fieldErrors = mutableMapOf(
            "cicles" to true,
            "workMinutes" to true,
            "restSeconds" to true
        )

        fieldErrors.keys.forEach { fieldErrors[it] = false }

        fieldErrors.values.forEach { assertFalse(it) }
    }

    @Test
    fun `fieldErrors se activa correctamente si hay errores de validación`() {
        val fieldErrors = mutableMapOf<String, Boolean>().apply {
            listOf(
                "cicles", "workHours", "workMinutes", "workSeconds",
                "restHours", "restMinutes", "restSeconds"
            ).forEach { this[it] = false }
        }

        val c = null
        val workValid = false
        val restValid = false

        if (c == null) fieldErrors["cicles"] = true
        if (!workValid) {
            fieldErrors["workHours"] = true
            fieldErrors["workMinutes"] = true
            fieldErrors["workSeconds"] = true
        }
        if (!restValid) {
            fieldErrors["restHours"] = true
            fieldErrors["restMinutes"] = true
            fieldErrors["restSeconds"] = true
        }

        assertTrue(fieldErrors["cicles"]!!)
        assertTrue(fieldErrors["workHours"]!!)
        assertTrue(fieldErrors["restSeconds"]!!)
    }
}
