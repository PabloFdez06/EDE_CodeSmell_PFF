

import model.IExportable
import kotlin.test.*
import java.io.File

class FicherosTest {

    private val ficheros = Ficheros()
    private val testFilePath = "test_file.txt"

    @BeforeTest
    fun setUp() {
        File(testFilePath).writeText("") // Crea o limpia el archivo
    }

    @AfterTest
    fun cleanUp() {
        File(testFilePath).delete()
    }

    @Test
    fun `agregarLinea deberia agregar una linea al archivo`() {
        val linea = "contenido de prueba"
        assertTrue(ficheros.agregarLinea(testFilePath, linea))
        assertTrue(File(testFilePath).readText().contains(linea))
    }

    @Test
    fun `leerArchivo deberia devolver lista con contenido`() {
        File(testFilePath).writeText("Línea1\nLínea2")
        val resultado = ficheros.leerArchivo(testFilePath)
        assertEquals(2, resultado.size)
        assertEquals("Línea1", resultado[0])
    }

    @Test
    fun `escribirArchivo deberia escribir objetos serializados`() {
        val obj = object : IExportable {
            override fun serializar(separador: String): String {
                return "obj1"
            }
        }
        assertTrue(ficheros.escribirArchivo(testFilePath, listOf(obj)))
        assertEquals("obj1", File(testFilePath).readText())
    }
}
