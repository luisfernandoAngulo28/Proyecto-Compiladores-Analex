# Proyecto #1 — Analizador Léxico
### INF329-SA Compiladores · Gestión 2-2026

Analizador Léxico (Scanner) para un lenguaje híbrido **Pascal + Java**, implementado en Java con interfaz gráfica Swing.

---

## Descripción

El Analex lee un programa fuente carácter a carácter usando una **Cinta de Caracteres** y produce tokens uno a uno. Implementa un **Diagrama de Transición (DT)** de 14 estados (q0 a q13) y una **TPC (Tabla de Palabras Clave)** privada en la clase `Analex`.

## Tokens reconocidos

| Categoría | Tokens |
|---|---|
| Palabras clave | `program var procedure/void begin/{ end/} if then else for to do downto while case of repeat until read readln print println` |
| Operadores lógicos | `not/! and/& or/\|` |
| Operadores aritméticos | `+ - * mod/% div/` |
| Puntuación | `, : ; ( ) :=` |
| Relacionales | `= < > <= >= <> !=` |
| Tipos | `boolean integer` |
| Con atributo | `NUM → <NUM,30>` · `ID → <ID,-1>` · `LITERAL → <LITERAL,0>` · `OPREL` · `TIPO` |
| Defensa | `por → <POR,_>` · `sumi → <SUMA,_>` |

## Características

- **Case-sensitive** (palabras clave solo en minúsculas)
- **TPC privada** (`HashMap<String, Integer>`) en la clase `Analex`
- **Comentarios de línea** `//` (terminan con EOLN, con EOF o con otro `//`)
- **Comentarios de bloque** `/* ... */`
- **Literales** entre comillas dobles o simples (sin EOLN ni EOF)
- **Checklist automático**: 87 verificaciones del enunciado, 0 fallos (`analex.Main`)
- `Token.toString()` con el formato del enunciado: `<NUM,30>`, `<OPREL,IGUAL>`, `<TIPO,INTEGER>`
- **Código legible**: nombres descriptivos (`acumuladorLexema`, `caracterActual`, `estadoActual`), estados del DT con el mismo número que el dibujo (`Q8_COMENTARIO_DE_LINEA`) y Javadoc en cada clase y método
- **`toString()` en `Cinta`, `Token` y `Analex`**: se imprimen con `System.out.println(objeto)` y dibujan la cinta con el cabezal

## Interruptores (al inicio de `Analex.java`)

La defensa pidió algunos comportamientos distintos al enunciado escrito. Quedan como constantes para cambiarlos con un solo valor:

| Constante | `true` (defensa / pizarra) | `false` (enunciado escrito) |
|---|---|---|
| `CASE_SENSITIVE` | `WHILE` es ID | `WHILE` y `While` son palabra clave |
| `COMENTARIO_CIERRA_CON_DOBLE_BARRA` | `// x // y` ignora solo `x` | `//` comenta hasta fin de línea |
| `EXTRAS_DEFENSA` | `por` y `sumi` son tokens | son IDs |
| `ID_ACEPTA_GUION_BAJO` | `_x` es ID | `_x` es error |

## Estructura del proyecto

```
Analex2-2026/
├── src/analex/
│   ├── Token.java      ← Define los 43 tokens <nom, atr>
│   ├── Cinta.java      ← Cinta de caracteres (cc, avanzar, init)
│   ├── Analex.java     ← Scanner: DT de 14 estados + TPC
│   ├── Interfaz.java   ← GUI Swing (formulario de NetBeans: Interfaz.form)
│   └── Main.java       ← Checklist del enunciado (consola)
├── ejemplos/           ← Programas de prueba (válidos y con errores)
├── DT-Analex-2026.html ← Dibujo del Diagrama de Transiciones (4 hojas A4, para imprimir)
├── GUIA-CODIGO.md      ← Explicación de cada clase y método
├── GUIA-DEFENSA.md     ← Cambios de la pizarra y cómo rehacerlos
├── EnunciadoProy1.pdf  ← Enunciado
└── dist/Analex.jar     ← Ejecutable
```

Cada clase (`Cinta`, `Token`, `Analex`) tiene además un `main` de demostración que imprime ejemplos de cada método por consola (clic derecho → Run File). Por ejemplo, `System.out.println(cinta)` muestra:

```
Cinta{ cabezal=3, fila=0, columna=3, cc=c }
  [a][b][EOLN][c][d][EOF]
               ^
```

Los nombres que usa el curso (`cc()`, `avanzar()`, `init()`, `Preanalisis()`, `lexema()`, `dt()`, `TPC`, `EOF`, `EOLN`, `onComunicate()`) se conservaron; el resto del código usa nombres descriptivos. La tabla *antes → ahora* está en `GUIA-CODIGO.md`, sección 6.2.

## Interfaz gráfica

- Editor de código fuente con resaltado por tipo de token
- Tabla de tokens con columnas: `#`, `Token`, `Atributo`, `Lexema`, `Fila:Col`
- Colores por categoría: verde KW · azul OP · amarillo ID · violeta NUM · celeste LIT · rojo ERR
- Panel de estadísticas: Total · KW · ID · NUM · LIT · ERR
- Botones: **Abrir...** (carga un archivo) · **Iniciar** (acción principal) · **Siguiente** (token a token) · **Todos** · **Reiniciar**
- Atajos de teclado: `F5` Iniciar · `F6` Siguiente · `F7` Todos · `F8` Reiniciar · `Ctrl+O` Abrir · `Alt+letra` (letra subrayada)
- Números de línea en el editor (coinciden con la columna `Fila:Col` de la tabla)
- Barra de estado con color: verde = bien, amarillo = aviso, rojo = error, con el motivo del error léxico (carácter no reconocido, literal sin cerrar, comentario sin cerrar)
- Mensaje guía cuando la tabla está vacía, tema oscuro con contraste AA y estados visibles en cada control (hover, foco, presionado, deshabilitado)

## Cómo ejecutar

```bash
# Ejecutar la interfaz (jar)
java -jar dist/Analex.jar

# Compilar desde el código fuente (Java 8 o superior)
javac -encoding UTF-8 -d build/classes src/analex/*.java

# Ejecutar la interfaz
java -cp build/classes analex.Interfaz

# Ejecutar el checklist del enunciado
java -cp build/classes analex.Main
```

Nota: la carpeta `dist/` (el `.jar`) no se sube a GitHub; se genera con **Clean and Build** (Shift+F11 en NetBeans) o compilando con `javac` como se muestra arriba.

En NetBeans: **Run Project** (F6) abre la interfaz. Para el checklist, clic derecho sobre `Main.java` → Run File.

## Ejemplos (`ejemplos/`)

En la interfaz pulsa **Abrir...** (`Ctrl+O`), elige un archivo de esta carpeta y luego **Iniciar** (`F5`) y **Todos** (`F7`).

| Archivo | Qué muestra | Resultado esperado |
|---|---|---|
| `01_factorial.txt` | Programa completo | 51 tokens, 0 errores |
| `02_palabras_y_operadores.txt` | Todas las categorías, `div mod por sumi`, literales | 97 tokens, 0 errores |
| `03_case_sensitive.txt` | `while` vs `WHILE`, `Div` vs `div` | solo las minúsculas son palabra clave |
| `04_comentarios.txt` | `//…EOLN`, `/*…*/`, `//…//`, `//…EOF` | 7 tokens (los comentarios no aparecen) |
| `05_error_caracter.txt` | Carácter no reconocido `@` | `ERROR` en 2:9 |
| `06_error_literal.txt` | Literal sin cerrar | `ERROR` en la línea 2 |
| `07_error_comentario.txt` | `/*` sin cerrar | `ERROR` al llegar al EOF |

## Autor

**Luis Fernando Angulo** · INF329-SA · Gestión 2-2026
