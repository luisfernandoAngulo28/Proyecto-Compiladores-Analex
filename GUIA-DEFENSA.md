# Guía de defensa — Modificaciones de la pizarra
INF329-SA Compiladores · Proyecto #1 Analex · Gestión 2-2026

La pizarra pide **3 cambios**. Los tres ya están implementados en el proyecto (interruptores al inicio de `Analex.java`). Esta guía sirve para **entender qué cambia en el DT y en el código**, y para poder **rehacerlos en vivo** si el ingeniero te lo pide.

| # | Pizarra | Interruptor en `Analex.java` | ¿Cambia el dibujo del DT? |
|---|---|---|---|
| 1 | "Que el lenguaje sea case-sensitive" | `CASE_SENSITIVE` | **No** |
| 2 | "Aumentar las palabras reservadas": `div, mod, por, sumi` | `EXTRAS_DEFENSA` | **No** |
| 3 | "Aumentar comentarios": `// … EOLN`, `// … EOF`, `// … //` | `COMENTARIO_CIERRA_CON_DOBLE_BARRA` | **Sí** (estados 8 y 13) |

> Idea clave para la defensa: **agregar palabras reservadas y cambiar mayúsculas NO toca el DT**. Solo la TPC. Solo los comentarios cambian el DT.

> **Nombres en el código:** el código usa nombres descriptivos (`acumuladorLexema`, `caracterActual`, `estadoActual`,
> `cinta`, `tokenPreanalisis`) y los estados del DT se llaman como en el dibujo (`Q8_COMENTARIO_DE_LINEA`, `Q13_...`).
> La tabla *antes → ahora* está en `GUIA-CODIGO.md`, sección 6.2.

---

## 1. Case-sensitive

**Qué significa:** `while` es palabra clave, pero `While` y `WHILE` son **ID**. Lo mismo con `Div`, `MOD`, `Integer`…

**Por qué no cambia el DT:** el DT acumula el lexema tal cual lo escribió el usuario (estado q2, `acumuladorLexema += caracterActual`). La decisión "¿palabra clave o ID?" se toma **al salir del estado 2**, consultando la TPC.

**Dónde está el cambio** (`Analex.java`, `case Q2_IDENTIFICADOR`, busca la línea con `CASE_SENSITIVE ?`):
```java
String clave = CASE_SENSITIVE ? acumuladorLexema : acumuladorLexema.toLowerCase();
if (TPC.containsKey(clave)) { ... }
```
- **Case-sensitive** → buscar `acumuladorLexema` tal cual (sin `toLowerCase`). Las claves de la TPC están en minúsculas, así que `While` no se encuentra → `<ID,-1>`.
- **NO case-sensitive** (enunciado original) → buscar `acumuladorLexema.toLowerCase()`.

**Si te piden hacerlo en vivo:** en el estado 2 reemplaza `acumuladorLexema.toLowerCase()` por `acumuladorLexema` (o al revés) en el `containsKey`/`get` y en `atributoDelTipo(...)`. Eso es todo.

**Pruebas** (escribe esto en la interfaz o en `Main`):

| Fuente | Resultado case-sensitive |
|---|---|
| `while WHILE While` | `WHILE ID ID` |
| `Program program` | `ID PROGRAM` |
| `Div MOD` | `ID ID` ← lo que dice la pizarra: "si escribo Div el analex reconoce como ID" |

---

## 2. Palabras reservadas nuevas (`div`, `mod`, `por`, `sumi`)

| Lexema | Token | Estado en el proyecto |
|---|---|---|
| `div` | `<DIV,_>` | ya estaba (también `/`) |
| `mod` | `<MOD,_>` | ya estaba (también `%`) |
| `por` | `<POR,_>` | agregado (también `*`) |
| `sumi` | `<SUMA,_>` | agregado (token nuevo `SUMA = 42`) |

**Por qué no cambia el DT:** `por` y `sumi` son letras, así que el DT las lee como ID (estado 2). Al terminar consulta la TPC y, si está, cambia el token.

### Receta: agregar una palabra reservada que **ya tiene token** (como `por`)
Una sola línea en el bloque `static { ... }` de `Analex.java` (busca `EXTRAS_DEFENSA`):
```java
TPC.put("por", Token.POR);
```

### Receta: agregar una palabra con **token NUEVO** (como `sumi`) — 4 pasos
Ejemplo: `mul` → `<MUL,_>`.
1. **`Token.java`** — constante nueva al final: `public static final int MUL = 43;` y subir `ULTIMO_NOMBRE_DE_TOKEN = MUL;`
2. **`Token.java`** — agregar `"MUL"` al final del arreglo `NOMBRES_DE_TOKEN` (el índice debe ser 43).
3. **`Analex.java`** — `TPC.put("mul", Token.MUL);`
4. **`Interfaz.java`** — agregar `"MUL"` al final del arreglo de `tokenNombre()` (para que la tabla muestre el nombre). El color y el atributo `_` ya salen solos porque usan `Token.ULTIMO_NOMBRE_DE_TOKEN`.

> Si olvidas el paso 4, el token funciona pero en la tabla de la interfaz aparece como `??(43)`.

**Pruebas:**

| Fuente | Resultado |
|---|---|
| `div mod por sumi` | `DIV MOD POR SUMA` |
| `Div MOD Por SUMI` | `ID ID ID ID` ← case-sensitive |
| `x := a div b mod c` | `ID ASSIGN ID DIV ID MOD ID` |
| `a / b` | `ID DIV ID` (el símbolo `/` sigue funcionando) |

---

## 3. Comentarios (lo único que cambia el DT)

Tres formas de terminar un comentario de línea:

| Forma | Termina con | Ejemplo | Resultado |
|---|---|---|---|
| `// … EOLN` | salto de línea | `a // hola\n b` | `ID ID` |
| `// … EOF` | fin de la cinta | `a // hola` | `ID FIN` |
| `// … //` | otro `//` | `a // hola // b` | `ID ID` (`b` sí se reconoce) |

Más los ya existentes: `/* … */` multilínea (estados 9 y 10).

### Cómo se dibuja (estados que intervienen)
```
q0 --'/'--> q7 --'/'--> q8      (q8 = dentro de un comentario //)
                         │
        EOLN  ───────────┼──► q0     cierra  (// … EOLN)
        EOF   ───────────┼──► FIN    cierra  (// … EOF)
        otro  ──► q8  (se queda; se ignora)
        '/'   ──► q13   (leí una '/' dentro del comentario)

q13:  '/'   ──► q0     cierra  (// … //)
      EOLN  ──► q0     cierra
      EOF   ──► FIN
      otro  ──► q8     (era una '/' suelta, sigo dentro)
```
- **q7**: leí una `/`. Si viene `/` → comentario de línea; si viene `*` → comentario de bloque; si viene otra cosa → es el operador `DIV`.
- **q13** es el estado que se **agrega** para `// … //`: sirve para distinguir una `/` suelta de la pareja `//` que cierra.

### Código (`Analex.java`, `case Q8_COMENTARIO_DE_LINEA` y `case Q13_LINEA_LEIDA_SEGUNDA_BARRA`)
- **Estado 8**: `EOF` → `FIN`; `EOLN` → vuelve a q0; `/` → pasa a q13; otro → sigue.
- **Estado 13**: `/` → vuelve a q0 (cerró); `EOLN` → q0; `EOF` → `FIN`; otro → vuelve a q8.

**Si te piden hacerlo en vivo** partiendo de solo `// … EOLN` y `// … EOF`:
1. En el estado q8, agrega la rama `else if (caracterActual == '/') { cinta.avanzar(); estadoActual = Q13_LINEA_LEIDA_SEGUNDA_BARRA; }`.
2. Crea `case Q13_LINEA_LEIDA_SEGUNDA_BARRA:` con las 4 salidas de arriba (`/`, `EOLN`, `EOF`, otro).
3. En cada cierre, haz `posicionInicioLexema = cinta.getPos();` y `estadoActual = Q0_INICIAL;`.

**Pruebas:**

| Fuente | Resultado |
|---|---|
| `a // hasta fin de linea\n b` | `ID ID` |
| `a // hasta EOF` | `ID FIN` |
| `a // texto // b` | `ID ID` |
| `a //texto// b // otro\n c` | `ID ID ID` |
| `a //// b` | `ID ID` (comentario vacío) |
| `a /* bloque */ b` | `ID ID` |

### Variantes que podrían pedirte
- **Comentario de línea con otro símbolo (ej. `#`)**: en el estado q0 agrega `if (caracterActual == '#') { cinta.avanzar(); estadoActual = Q8_COMENTARIO_DE_LINEA; break; }` y reutilizas el estado q8.
- **Comentarios `{ … }` o `(* … *)`**: ojo, `{` ya es `BEGIN` y `(` `*` ya son `PA` y `POR`; chocarían. Habría que decidir cuál tiene prioridad.
- **Que `/* */` se pueda anidar**: necesitas un contador de profundidad en los estados 9 y 10.

---

## 4. Plan para la defensa

1. **Lleva dibujado el DT** (pide el enunciado). Marca q7, q8 y q13 para los comentarios y una nota: "al terminar de leer un ID se consulta la TPC".
2. Ten abierto `Analex.java` al inicio del archivo (los 4 interruptores): ahí cambias los tres comportamientos con `true`/`false`.
3. **Demostración rápida en la interfaz** (botón *Todos*):
   ```
   program p;
   var Div : integer;   // Div es ID (case-sensitive)
   begin
     x := a div b mod c;
     y := a por b;
     z := a sumi b; // termina en EOLN
     w := 1 // cierra con doble barra // + 2
   end
   ```
   Debe mostrar `DIV`, `MOD`, `POR`, `SUMA` como palabras clave, `Div` como `ID` y no mostrar el texto de los comentarios.
4. Preguntas probables y respuesta corta:
   - *¿Por qué no cambió el DT al agregar `sumi`?* → Porque `sumi` es un ID para el DT; la TPC lo reclasifica al salir del estado 2.
   - *¿Qué cambia al pasar a case-sensitive?* → Solo la búsqueda en la TPC: sin `toLowerCase`.
   - *¿Para qué sirve q13?* → Para distinguir una `/` suelta de `//` que cierra el comentario.
   - *¿Qué pasa con `// …` sin salto de línea al final?* → Llega `EOF`, el estado 8 devuelve `FIN`.

## 5. Antes de entregar
- Los cuatro interruptores reflejan lo de la pizarra si están así: `CASE_SENSITIVE = true`, `COMENTARIO_CIERRA_CON_DOBLE_BARRA = true`, `EXTRAS_DEFENSA = true`. (`ID_ACEPTA_GUION_BAJO` no viene de la pizarra: el enunciado dice solo letras y dígitos, ponlo en `false` si quieres seguirlo al pie de la letra.)
- Corre `Main` (checklist): debe terminar en `0 FAIL`.
- Zip del proyecto **sin** `build/`, `dist/` ni `respaldo-interfaz/`.
