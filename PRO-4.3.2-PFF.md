# <p align="center">EDE_CodeSmell_PFF</p>


# COMO ANALIZAR EL CÓDIGO

1. Seleccionamos Codigo/Code - Analizar Codigo/Analyze Code | Esto te generara un informe de errores, posibles mejoras, etc.
2. Una vez nos haya generado este informe, podremos exportarlo como HTML, o visualizarlo en el mismo IDE.



# DETECCIÓN DE 5 ERRORES


### 1. Código Duplicado en ConfiguracionesApp
  
- **Ubicación:** Clase ConfiguracionesApp.kt, líneas 82-88, 115-121, 139-145.
- **Tipo:** Duplicate Code (Código Duplicado).
- **Descripción:** Fragmentos de código similares aparecen repetidamente en la misma clase, lo que incrementa el riesgo de errores al mantener el código.
- **Solución sugerida:** Extraer los fragmentos repetidos a un método común reutilizable.

### 2. Método que Siempre Retorna el Mismo Valor
  
- **Ubicación:** Interfaz IRepoUsuarios, método cambiarClave.
- **Tipo: Redundancia Declarativa.**
- **Descripción:** Todas las implementaciones del método siempre retornan true, lo cual hace innecesario su retorno como valor dinámico.
- **Solución sugerida:** Revisar si el valor de retorno es necesario. Si no lo es, cambiar el tipo de retorno a void.

### 3. Propiedad Privado No Utilizado
   
- **Ubicación:** Enum Cobertura, método getCobertura(valor: String).
- **Tipo:** Control de flujo inapropiado / Código frágil basado en excepciones.
- **Descripción:** El método getCobertura usaba una llamada a valueOf(valor.uppercase()) seguida de un bloque try-catch para manejar posibles errores de entrada. Esta forma de control de flujo mediante excepciones es enrevesada y poco clara. Además, se fijaba el comportamiento a los nombres exactos del enum, lo que puede fallar con entradas incorrectas.
- **Solución sugerida:** Eliminar si no se planea utilizar, o documentar explícitamente su propósito si es para uso futuro.

### 4. Declaraciones Redundantes No Utilizadas
   
- **Ubicación:** Clase Main.kt, método main().
- **Tipo:** Entry Point sin lógica o Código Inerte.
- **Descripción:** El método main() existe, pero no contiene ninguna lógica significativa ni es utilizado como punto de entrada real.
- **Solución sugerida:** Eliminar o implementar si se prevé como punto de inicio.

### 5. Método que siempre retorna el mismo valor

- **Ubicación:** Interfaz IRepoUsuarios, método cambiarClave().
- **Tipo: Código Inflado** / Interfaz Mal Diseñada.
- **Descripción:** Todas las implementaciones del método cambiarClave() retornan siempre true, lo que sugiere que el método no aporta lógica significativa ni diferenciación funcional. Esto rompe principios como Tell, Don’t Ask y podría reflejar una mala definición de responsabilidades o un diseño superficial.
- **Solución sugerida:**
  
    - Eliminar el método si no tiene utilidad real.
    - Refactorizar para que implemente lógica concreta (validaciones, persistencia, etc.).
    - Considerar retornar un objeto de resultado (como un Result, Either, o lanzar excepciones controladas) para representar correctamente el resultado de la operación.

# USO DE REFACTORIZACIÓN

1. En RepoSegurosFich, el método cargarSeguros() tiene demasiadas responsabilidades (cargar datos, limpiar lista, transformar, insertar, actualizar contadores). Esto es un code smell tipo "Long Method" y rompe el principio de responsabilidad única.

**Código Sin Refactorizar:**

```
    override fun cargarSeguros(mapa: Map<String, (List<String>) -> Seguro?>): Boolean {
        val lineas = fich.leerArchivo(rutaArchivo)

        if (lineas.isNotEmpty()) {
            seguros.clear()
            for (linea in lineas) {
                val datos = linea.split(";")
                if (datos.isNotEmpty()) {
                    val tipo = datos[0]
                    val parametros = datos.drop(1)
                    val seguro = mapa[tipo]?.invoke(parametros)
                    if (seguro != null) {
                        super.agregar(seguro)
                    }
                }
            }
            actualizarContadores(seguros)
            return seguros.isNotEmpty()
        }
        return false
    }
```

**Código Refactorizado:**

```
override fun cargarSeguros(mapa: Map<String, (List<String>) -> Seguro?>): Boolean {
    val lineas = fich.leerArchivo(rutaArchivo)
    return cargarSegurosDesdeLineas(lineas, mapa)
}

private fun cargarSegurosDesdeLineas(lineas: List<String>, mapa: Map<String, (List<String>) -> Seguro?>): Boolean {
    if (lineas.isNotEmpty()) {
        seguros.clear()
        for (linea in lineas) {
            val seguro = parsearLinea(linea, mapa)
            if (seguro != null) super.agregar(seguro)
        }
        actualizarContadores(seguros)
        return seguros.isNotEmpty()
    }
    return false
}

private fun parsearLinea(linea: String, mapa: Map<String, (List<String>) -> Seguro?>): Seguro? {
    val datos = linea.split(";")
    if (datos.isNotEmpty()) {
        val tipo = datos[0]
        val parametros = datos.drop(1)
        return mapa[tipo]?.invoke(parametros)
    }
    return null
}

```

2. El método cambiarClave() en Usuario acepta una clave ya encriptada. Esto rompe la consistencia del modelo, porque la encriptación debería ser responsabilidad de la clase, no del exterior. También va contra el principio Tell, Don’t Ask.

**Código Sin Refactorizar:**

```
    fun cambiarClave(nuevaClaveEncriptada: String) {
        this.clave = nuevaClaveEncriptada
    }
```

**Código Refactorizado:**

```
fun cambiarClaveNueva(nuevaClave: String) {
    this.clave = Seguridad().encriptarClave(nuevaClave)
}
```

3. En Ficheros.agregarLinea: El método agregarLinea() repite la lógica de verificar y abrir el archivo. Es un code smell tipo "Código Duplicado" y también viola el principio DRY.

**Código Sin Refactorizar:**

```
    override fun agregarLinea(ruta: String, linea: String): Boolean {
        if (!existeFichero(ruta)) {
            return false
        } else {
            val file = File(ruta)
            if (file.length().toInt() == 0) {
                file.appendText(linea)
            } else {
                file.appendText("\n" + linea)
            }
            return true
        }
    }
```

**Código Refactorizado:**

```
override fun agregarLinea(ruta: String, linea: String): Boolean {
    val file = obtenerArchivoSeguro(ruta) ?: return false
    val texto = if (file.length().toInt() == 0) linea else "\n$linea"
    file.appendText(texto)
    return true
}

private fun obtenerArchivoSeguro(ruta: String): File? {
    return if (existeFichero(ruta)) File(ruta) else null
}

```

# PRUEBAS UNITARIAS

### TEST 1: Carga de seguros (refactorización 1)

```
@Test
fun `test cargarSeguros con datos validos`() {
    val mockFich = mockk<IUtilFicheros>()
    val repo = RepoSegurosFich("seguros.txt", mockFich)

    val linea = "SeguroAuto;Juan;1234;1234ABC;123.0;5"
    every { mockFich.leerArchivo(any()) } returns listOf(linea)

    val mapa = mapOf("SeguroAuto" to { datos: List<String> -> SeguroAuto.crearSeguro(datos) })
    val resultado = repo.cargarSeguros(mapa)

    assertTrue(resultado)
    assertEquals(1, repo.obtenerTodos().size)
}

```

### TEST 2: Cambio de clave del usuario (refactorización 2)

```
@Test
fun `test cambiarClaveNueva cambia y encripta clave correctamente`() {
    val usuario = Usuario("Ana", "1234", Perfil.ADMIN)
    val claveAnterior = usuario.clave

    usuario.cambiarClaveNueva("nuevaClave123")
    val claveNueva = usuario.clave

    assertNotEquals(claveAnterior, claveNueva)
    assertTrue(Seguridad().verificarClave("nuevaClave123", claveNueva))
}

```

### TEST 3: Agregar línea a archivo vacío (refactorización 3)

```
@Test
fun `test agregarLinea archivo vacio`() {
    val ruta = "archivo.txt"
    val file = mockk<File>(relaxed = true)
    every { file.length() } returns 0
    mockkStatic(File::class)
    every { File(ruta) } returns file
    val fich = Ficheros()

    val resultado = fich.agregarLinea(ruta, "Línea de prueba")
    assertTrue(resultado)
}

```

# RESPUESTA A LAS PREGUNTAS

## [1] Refactorización y code smells

### 1.a ¿Qué code smell y patrones de refactorización has aplicado?

- Código duplicado → *Extraer método* (ej. en `Ficheros.agregarLinea`)
- Método con demasiadas responsabilidades → *Extraer método* (ej. en `RepoSegurosFich.cargarSeguros`)
- Método que siempre retorna el mismo valor → revisión y posible eliminación (ej. `IRepoUsuarios.cambiarClave`)
- Dependencia concreta → *Inyección de dependencias* para facilitar testing (ej. `RepoSegurosFich` recibe `IUtilFicheros`)
- Código con lógica externa para encriptación → encapsulamiento de lógica (*Tell, Don’t Ask*), en `Usuario.cambiarClaveNueva`

### 1.b Patrón de refactorización cubierto por tests y su mejora

**Patrón:** *Inyección de dependencias* en `RepoSegurosFich` (recibe interfaz `IUtilFicheros`).

- Mejora la testabilidad y desacopla la clase del acceso directo a ficheros.
- Permite usar mocks en tests para aislar la lógica de negocio.
- Código relacionado:
  - RepoSegurosFich.kt
  - RepoSegurosFichTest.kt

Los tests validan la correcta carga de seguros simulando la lectura de ficheros.

---

## [2] Proceso para asegurar que la refactorización no afecta código existente

- Ejecutar todos los tests antes de la refactorización para confirmar funcionalidad inicial.
- Aplicar refactorización usando herramientas automáticas del IDE (IntelliJ).
- Volver a ejecutar todos los tests unitarios y de integración después del cambio.
- Verificar que no hay fallos ni inconvenientes.
- Usar mocks para aislar dependencias externas y comprobar interacciones.
- Revisar manualmente cambios críticos si es necesario.

---

## [3] Funcionalidades del IDE usadas para refactorización

- **Extract Method / Extract Interface:** Refactor → Extract → Interface/Method para separar responsabilidades.
- **Rename (Shift+F6):** Renombrar variables o métodos sin romper referencias.
- **Intention Actions (Alt+Enter):** Sugerencias para mejorar código o eliminar duplicaciones.
- **Soporte para mocks en tests:** Integración con frameworks de mocking (ej. MockK) para pruebas unitarias aisladas.

---


