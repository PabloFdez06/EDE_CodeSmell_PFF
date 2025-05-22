package data

import io.mockk.*
import model.Seguro
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import utils.IUtilFicheros
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RepoSegurosFichTest {

    private lateinit var ficheroMock: IUtilFicheros
    private lateinit var repo: RepoSegurosFich
    private val ruta = "seguros.txt"

    @BeforeTest
    fun setUp() {
        ficheroMock = mockk()
        repo = RepoSegurosFich(ruta, ficheroMock)
    }

    @Test
    fun `agregar deberia devolver true si agregarLinea y agregar en memoria son exitosos`() {
        val seguro = mockk<Seguro>()
        every { seguro.serializar() } returns "AUTO;1;123ABC"
        every { ficheroMock.agregarLinea(any(), any()) } returns true
        every { seguro.numPoliza } returns 1
        every { seguro.tipoSeguro() } returns "SeguroAuto"

        assertTrue(repo.agregar(seguro))
    }

    @Test
    fun `eliminar deberia devolver false si escribirArchivo falla`() {
        val seguro = mockk<Seguro>()
        every { ficheroMock.escribirArchivo(ruta, any()) } returns false

        assertFalse(repo.eliminar(seguro))
    }
}
