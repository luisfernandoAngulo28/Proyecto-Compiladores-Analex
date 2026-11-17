# Proyecto #1 — Analizador Léxico
### INF329-SA Compiladores · Gestión 2-2026

Analizador Léxico (Scanner) para un lenguaje híbrido **Pascal + Java**, implementado en Java con interfaz gráfica Swing.

---

## Descripción

El Analex lee un programa fuente carácter a carácter usando una **Cinta de Caracteres** y produce tokens uno a uno. Implementa un **Diagrama de Transición (DT)** de 13 estados y una **TPC (Tabla de Palabras Clave)** privada.

## Tokens reconocidos

| Categoría | Tokens |
|---|---|
| Palabras clave | `program var procedure/void begin/{ end/} if then else for to do downto while case of repeat until read readln print println` |
| Operadores lógicos | `not/! and/& or/\|` |
| Operadores aritméticos | `+ - * mod/% div/` |
| Puntuación | `, : ; ( ) :=` |
| Relacionales | `= < > <= >= <> !=` |
| Tipos | `boolean integer` |
| Especiales | `NUM ID LITERAL` |
| Defensa | `por sumi` |

## Características

- ✅ **Case-Sensitive** (palabras clave en minúsculas)
- ✅ **TPC privada** (`HashMap<String, Integer>`) en la clase `Analex`
- ✅ **Comentarios de línea** `//` (cierran con EOLN, EOF o `//`)
- ✅ **Comentarios de bloque** `/* ... */`
- ✅ **78/78 tests** pasando

## Estructura del proyecto

```
src/analex/
├── Token.java      ← Define los 43 tokens <nom, atr>
├── Cinta.java      ← Cinta de caracteres (cc, avanzar, init)
├── Analex.java     ← Scanner: DT de 13 estados + TPC
├── Interfaz.java   ← GUI Swing con tabla coloreada y estadísticas
└── Main.java       ← Tests de verificación del enunciado
```

## Interfaz gráfica

- Editor de código fuente con resaltado por tipo de token
- Tabla de tokens con columnas: `#`, `Token`, `Atributo`, `Lexema`, `Fila:Col`
- Colores por categoría: 🟢 KW · 🔵 OP · 🟡 ID · 🟣 NUM · 🩵 LIT · 🔴 ERR
- Panel de estadísticas: Total · KW · ID · NUM · LIT · ERR
- Botones: **Iniciar** · **Siguiente** (token a token) · **Todos** · **Reiniciar**

## Cómo ejecutar

```bash
# Compilar
javac -encoding UTF-8 -cp src -d build/classes src/analex/*.java

# Ejecutar la interfaz
java -cp build/classes analex.Interfaz

# Ejecutar los tests
java -cp build/classes analex.Main
```

## Cambios aplicados en la defensa

| Cambio | Descripción |
|---|---|
| Case-Sensitive | `"While"` → ID, `"while"` → WHILE |
| Nueva KW `por` | `"por"` → `<POR,_>` |
| Nueva KW `sumi` | `"sumi"` → `<SUMA,_>` |
| Comentario `// ... //` | Cierre explícito con `//` |

## Autor

**Luis Fernando Angulo** · INF329-SA · Gestión 2-2026
