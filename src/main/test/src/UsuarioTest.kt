package model

import kotlin.test.*

class UsuarioTest {

    @Test
    fun `crearUsuario lanza excepcion si faltan datos`() {
        val datosInvalidos = listOf("nombre", "clave")
        val exception = assertFailsWith<IllegalArgumentException> {
            Usuario.crearUsuario(datosInvalidos)
        }
        assertTrue(exception.message!!.contains("No hay suficientes datos"))
    }

    @Test
    fun `verificarClave devuelve true con clave correcta`() {
        val usuario = Usuario("Juan", "1234", Perfil.ADMIN)
        val clave = usuario.clave
        assertTrue(usuario.verificarClave(clave))
    }

    @Test
    fun `cambiarClave modifica la clave del usuario`() {
        val usuario = Usuario("Ana", "claveAntigua", Perfil.GESTOR)
        val nuevaClave = "nuevaClaveEncriptada"
        usuario.cambiarClave(nuevaClave)
        assertEquals(nuevaClave, usuario.clave)
    }
}
