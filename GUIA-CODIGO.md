# Guía de Código — Analex2-2026
## INF329 SA Compiladores · Gestión 2-2026

> **Nombres actualizados.** Esta guía se escribió con los nombres originales del código
> (`M`, `R`, `ac`, `pos`, `nom`, `atr`, `estado`, `chk`…). Después se renombraron para que se
> entiendan solos. **El comportamiento no cambió.** La tabla *antes → ahora* está en la
> [sección 6.2](#62-glosario-de-nombres-antes--ahora): úsala para leer los fragmentos de código de esta guía.

> **Propósito de este documento:** explicar cada variable, constante y método del proyecto
> de forma clara, para que sea fácil estudiar el código y hacer cambios en la defensa.

---

## Índice

1. [Estructura del proyecto](#1-estructura-del-proyecto)
2. [Token.java — Qué es un Token](#2-tokenjava--qué-es-un-token)
   - [Constantes de nombre de token](#21-constantes-de-nombre-de-token-nom)
   - [Constantes de atributo OPREL](#22-constantes-de-atributo-oprel)
   - [Constantes de atributo TIPO](#23-constantes-de-atributo-tipo)
   - [Campos y métodos](#24-campos-y-métodos)
3. [Cinta.java — La Cinta de Caracteres](#3-cintajava--la-cinta-de-caracteres)
4. [Analex.java — El Analizador Léxico](#4-analexjava--el-analizador-léxico)
   - [Campos de la clase](#41-campos-de-la-clase)
   - [TPC — Tabla de Palabras Clave](#42-tpc--tabla-de-palabras-clave)
   - [El método dt() — Diagrama de Transición](#43-el-método-dt--diagrama-de-transición)
   - [Estados del DT](#44-estados-del-dt)
5. [Cómo hacer cambios frecuentes](#5-cómo-hacer-cambios-frecuentes)
6. [Estándares de programación usados](#6-estándares-de-programación-usados)

---

## 1. Estructura del proyecto

```
Analex2-2026/
├── src/
│   └── analex/
│       ├── Token.java      ← Define qué ES un token (nombre + atributo)
│       ├── Cinta.java      ← La "cinta magnética" de chars del fuente
│       ├── Analex.java     ← El scanner: lee la cinta y devuelve tokens
│       ├── Interfaz.java   ← La ventana gráfica (Swing)
│       └── Main.java       ← Tests de verificación
├── DT-Analex.html          ← Diagrama de Transición visual (para la defensa)
└── GUIA-CODIGO.md          ← Este archivo
```

**Flujo de datos:**
```
Programa Fuente (texto)
        ↓
   Cinta.java          ← serializa el texto char a char
        ↓
   Analex.java         ← lee la cinta y reconoce tokens
        ↓
   Token.java          ← objeto que representa cada token <nom, atr>
        ↓
   Interfaz / Parser   ← usa los tokens
```

---

## 2. Token.java — Qué es un Token

Un **Token** es una pareja `<nombre, atributo>`. Ejemplo: `<NUM, 30>`, `<ID, -1>`, `<IF, _>`.

### 2.1 Constantes de nombre de token (`nom`)

Cada constante es un número entero que identifica el **tipo** de token.

```java
// ── Tokens especiales ──────────────────────────────────────────
FIN        = 0   // Fin de archivo (EOF alcanzado)
ERROR      = 1   // Carácter no reconocido por el lenguaje

// ── Palabras reservadas — Estructura del programa ──────────────
PROGRAM    = 2   // "program"
VAR        = 3   // "var"
PROCEDURE  = 4   // "procedure" o su alias Java "void"
BEGIN      = 5   // "begin" o su alias Java "{"
END        = 6   // "end"   o su alias Java "}"

// ── Palabras reservadas — Control de flujo ─────────────────────
IF         = 7   // "if"
THEN       = 8   // "then"
ELSE       = 9   // "else"
FOR        = 10  // "for"
TO         = 11  // "to"
DO         = 12  // "do"
DOWNTO     = 13  // "downto"
WHILE      = 14  // "while"
CASE       = 15  // "case"
OF         = 16  // "of"
REPEAT     = 17  // "repeat"
UNTIL      = 18  // "until"

// ── Palabras reservadas — E/S ──────────────────────────────────
READ       = 19  // "read"
READLN     = 20  // "readln"
PRINT      = 21  // "print"
PRINTLN    = 22  // "println"

// ── Signos de puntuación ───────────────────────────────────────
COMA       = 23  // ","
DOSPTOS    = 24  // ":"
PTOCOMA    = 25  // ";"
PA         = 26  // "("   paréntesis abre
PC         = 27  // ")"   paréntesis cierra

// ── Operadores ─────────────────────────────────────────────────
ASSIGN     = 28  // ":="  asignación Pascal
NOT        = 29  // "not" o alias Java "!"
AND        = 30  // "and" o alias Java "&"
OR         = 31  // "or"  o alias Java "|"
MAS        = 32  // "+"
MENOS      = 33  // "-"
POR        = 34  // "*"
MOD        = 35  // "mod" o alias Java "%"
DIV        = 36  // "div" o alias Java "/"

// ── Tokens con valor variable ──────────────────────────────────
NUM        = 37  // Número entero sin signo      — atributo = 30 (fijo)
ID         = 38  // Identificador (nombre)        — atributo = -1 (fijo)
LITERAL    = 39  // String entre comillas         — atributo = 0  (fijo)
OPREL      = 40  // Operador relacional           — atributo = IGUAL/MEN/etc.
TIPO       = 41  // Tipo de dato (boolean/integer)— atributo = BOOLEAN/INTEGER

// ── Agregados en defensa ───────────────────────────────────────
SUMA       = 42  // "sumi" — operación suma (pedido en la defensa)
```

> **¿Por qué números enteros y no Strings?**
> Porque los enteros ocupan 4 bytes y la comparación `==` es O(1).
> Comparar Strings como `"WHILE".equals(...)` es más lento y propenso a errores tipográficos.

---

### 2.2 Constantes de atributo OPREL

Cuando el nombre del token es `OPREL`, el atributo indica **cuál** operador relacional es:

```java
IGUAL = 0  // "="   igual
MEN   = 1  // "<"   menor que
MAY   = 2  // ">"   mayor que
MEI   = 3  // "<="  menor o igual
MAI   = 4  // ">="  mayor o igual
DIS   = 5  // "<>" o "!="  distinto (alias Pascal y Java)
```

**Ejemplo de uso:**
```java
if (token.getNom() == Token.OPREL && token.getAtr() == Token.MEI) {
    // es el operador <=
}
```

---

### 2.3 Constantes de atributo TIPO

Cuando el nombre del token es `TIPO`, el atributo indica qué tipo de dato es:

```java
BOOLEAN = -3  // "boolean"
INTEGER = -2  // "integer"
```

> **¿Por qué negativos?** Para que no colisionen con los valores positivos de otras constantes del token.

---

### 2.4 Campos y métodos

```java
// ── Campos (los dos únicos datos de un Token) ──────────────────
private int nom   // El NOMBRE del token  → qué tipo de token es
private int atr   // El ATRIBUTO del token → información adicional

// ── Constructores ──────────────────────────────────────────────
Token()                      // Crea token FIN por defecto
Token(int nombre)            // Crea token con atributo 0
Token(int nombre, int atr)   // Crea token completo

// ── Setters ────────────────────────────────────────────────────
set(int nombre, int atributo) // Cambiar nombre Y atributo de una vez
setNom(int nom)               // Cambiar solo el nombre
setAtr(int atr)               // Cambiar solo el atributo

// ── Getters ────────────────────────────────────────────────────
getNom()   // Devuelve el nombre del token (para comparar con Token.IF, etc.)
getAtr()   // Devuelve el atributo del token

// ── toString ───────────────────────────────────────────────────
toString() // Devuelve el token como texto, ej: "<IF,_>" o "<NUM,30>"
```

---

## 3. Cinta.java — La Cinta de Caracteres

La **Cinta** es el ADT (Máquina Abstracta) que contiene el programa fuente
y permite al Analex leerlo **carácter a carácter**.

### Variables internas

```java
// ── Constantes especiales ──────────────────────────────────────
EOF  = 0    // Carácter que marca el fin de la cinta
EOLN = LF   // Carácter que marca el fin de línea (char 10, Linux)
CR   = 13   // Carry-Return de Windows — se ignora automáticamente

// ── Estado de la cinta ─────────────────────────────────────────
String celdas    // El texto completo del programa fuente
int    cabezal   // Posición actual del cabezal (índice en 'celdas')

// ── Información de posición ────────────────────────────────────
int  fila        // Fila actual del cabezal (empieza en 0)
int  col         // Columna actual del cabezal
char charAnt     // El último carácter que se leyó (para detectar EOLN)
```

### Métodos

```java
// ── Carga y reset ──────────────────────────────────────────────
init(String programaFuente)  // Carga texto nuevo y resetea el cabezal al inicio
init()                       // Resetea el cabezal sin cambiar el texto

// ── Lectura (los 3 métodos que usa el Analex) ──────────────────
char cc()       // Current Char — devuelve el char que ve el cabezal SIN avanzar
void avanzar()  // Mueve el cabezal una posición a la derecha
                // ¡NO llamar si cc() == EOF!

// ── Información de posición ────────────────────────────────────
int getPos()      // Posición absoluta del cabezal en la cinta
int getFila()     // Fila del cabezal (para mensajes de error)
int getColumna()  // Columna del cabezal (para mensajes de error)

// ── Comunicación con la Interfaz ───────────────────────────────
void onComunicate(int pos, String lexema)
// Llamado por el Analex cuando reconoce un token.
// La Interfaz lo sobreescribe (override) para resaltar el lexema en el editor.
```

> **Regla de oro:** siempre verificar `cc() != Cinta.EOF` antes de llamar `avanzar()`.

---

## 4. Analex.java — El Analizador Léxico

El Analex es una **máquina de estados** (Diagrama de Transición) que lee la
Cinta carácter a carácter y agrupa los chars en Tokens.

### 4.1 Campos de la clase

```java
// ── Dependencias ───────────────────────────────────────────────
private Cinta M
// La cinta de caracteres.
// Nombre corto "M" = "Máquina" (convención del docente).
// Se llama M porque conceptualmente el Analex "monta" la cinta como una máquina.

private Token R
// El resultado del preanálisis — el token que fue reconocido más recientemente.
// R = "Result". Se actualiza cada vez que el dt() reconoce un token.
// El Parser accede a él con Preanalisis().

private String ac
// Acumulador de lexema — va guardando los chars del token actual.
// Ejemplo: al leer "while", ac va siendo "w" → "wh" → "whi" → "whil" → "while"
// Se resetea a "" al inicio de cada llamada a dt().
// ac = "accumulator" (convención de compiladores).

private int pos
// Posición de inicio del lexema actual en la cinta.
// Se usa para resaltar el lexema correcto en la Interfaz gráfica.
// Se actualiza con M.getPos() justo antes de empezar a acumular chars.
```

---

### 4.2 TPC — Tabla de Palabras Clave

```java
private static final HashMap<String, Integer> TPC = new HashMap<>();
```

La **TPC (Tabla de Palabras Clave)** es un `HashMap` que mapea:
```
lexema en minúsculas  →  nombre del token (entero)
```

**¿Por qué `static final`?**
- `static` → es compartida por todas las instancias de Analex (solo existe una copia en memoria)
- `final` → no se puede reasignar (la referencia al HashMap no cambia)

**¿Por qué las claves en minúsculas?**
- Porque el lenguaje es **case-insensitive** → al acumular el lexema siempre
  hacemos `Character.toLowerCase(c)`, entonces la TPC solo necesita las versiones en minúsculas.

**Contenido de la TPC:**

| Clave (lexema) | Valor (Token.NOM) | Lexemas alternativos |
|---|---|---|
| `"program"` | `Token.PROGRAM` | — |
| `"var"` | `Token.VAR` | — |
| `"procedure"` | `Token.PROCEDURE` | `"void"` (Java) |
| `"void"` | `Token.PROCEDURE` | alias |
| `"begin"` | `Token.BEGIN` | (el `{` se maneja en q0 directo) |
| `"end"` | `Token.END` | (el `}` se maneja en q0 directo) |
| `"if"` | `Token.IF` | — |
| `"then"` | `Token.THEN` | — |
| `"else"` | `Token.ELSE` | — |
| `"for"` | `Token.FOR` | — |
| `"to"` | `Token.TO` | — |
| `"do"` | `Token.DO` | — |
| `"downto"` | `Token.DOWNTO` | — |
| `"while"` | `Token.WHILE` | — |
| `"case"` | `Token.CASE` | — |
| `"of"` | `Token.OF` | — |
| `"repeat"` | `Token.REPEAT` | — |
| `"until"` | `Token.UNTIL` | — |
| `"read"` | `Token.READ` | — |
| `"readln"` | `Token.READLN` | — |
| `"print"` | `Token.PRINT` | — |
| `"println"` | `Token.PRINTLN` | — |
| `"not"` | `Token.NOT` | `"!"` (Java, en q0 directo) |
| `"and"` | `Token.AND` | `"&"` (Java, en q0 directo) |
| `"or"` | `Token.OR` | `"\|"` (Java, en q0 directo) |
| `"mod"` | `Token.MOD` | `"%"` (Java, en q0 directo) |
| `"div"` | `Token.DIV` | `"/"` (Java, en q0 directo) |
| `"boolean"` | `Token.TIPO` | — |
| `"integer"` | `Token.TIPO` | — |
| `"sumi"` | `Token.SUMA` | _(agregado en defensa)_ |
| `"por"` | `Token.POR` | _(agregado en defensa)_ |

> **¿Cómo funciona la TPC al reconocer un ID?**
> ```
> Leemos chars hasta que ya no es letra ni dígito (estado 2)
>       ↓
> ac = "while"  (todo en minúsculas)
>       ↓
> TPC.containsKey("while") == true
>       ↓
> R.set(Token.WHILE, 0)   ← es palabra clave, no ID
>
> Si ac = "suma123" → TPC.containsKey("suma123") == false → es ID
> R.set(Token.ID, -1)
> ```

---

### 4.3 El método `dt()` — Diagrama de Transición

```java
private void dt()
```

Este método es **el corazón del Analex**. Implementa el Diagrama de Transición (DT).

Cada llamada a `dt()` reconoce **exactamente un token** y lo guarda en `R`.

**Estructura general:**
```java
private void dt() {
    int estado = 0;   // Siempre empieza en el estado inicial
    ac = "";          // Limpiar el acumulador de lexema

    while (true) {
        char c = M.cc();   // Leer el char actual SIN avanzar

        switch (estado) {
            case 0:  // Estado inicial — decidir a qué estado ir
                ...
            case 1:  // Acumulando NUM
                ...
            // etc.
        }
    }
}
```

**Regla de retroceso (muy importante):**
Cuando el DT llega a un estado final por "Otro", el carácter que disparó la transición
**NO se consume** (no llamamos `M.avanzar()`). Ese char pertenece al **siguiente** token.

```java
// EJEMPLO — reconocer NUM
case 1:
    if (esDigito(c)) {
        ac += c;
        M.avanzar();     // ← consumir el dígito
    } else {
        // "Otro" — el char 'c' NO es un dígito
        // No avanzamos → 'c' será leído por el próximo token
        R.set(Token.NUM, 30);
        return;          // ← devolver el token NUM
    }
```

---

### 4.4 Estados del DT

```
Estado  │ Descripción                                │ Entra cuando...
────────┼────────────────────────────────────────────┼──────────────────────────────
  0     │ Estado inicial / entre tokens              │ Siempre al inicio de dt()
        │ Loop en Espacio (β, TAB, EOLN)             │
────────┼────────────────────────────────────────────┼──────────────────────────────
  1     │ Acumulando NUM (dígitos)                   │ q0 lee un Dígito
        │ Loop: sigue leyendo dígitos                │
        │ Sale: Otro → devuelve <NUM, 30>            │
────────┼────────────────────────────────────────────┼──────────────────────────────
  2     │ Acumulando ID o palabra clave              │ q0 lee una Letra
        │ Loop: sigue con letras o dígitos           │
        │ Sale: Otro → consulta TPC → ID o KW       │
────────┼────────────────────────────────────────────┼──────────────────────────────
  3     │ Leímos ':' — esperando '=' para ASSIGN     │ q0 lee ':'
        │ Si '=' → devuelve <ASSIGN, _>              │
        │ Si Otro → devuelve <DOSPTOS, _>  (retrocede)│
────────┼────────────────────────────────────────────┼──────────────────────────────
  4     │ Leímos '<' — puede ser MEN, MEI o DIS      │ q0 lee '<'
        │ Si '=' → devuelve <OPREL, MEI>             │
        │ Si '>' → devuelve <OPREL, DIS>             │
        │ Si Otro → devuelve <OPREL, MEN>  (retrocede)│
────────┼────────────────────────────────────────────┼──────────────────────────────
  5     │ Leímos '>' — puede ser MAY o MAI           │ q0 lee '>'
        │ Si '=' → devuelve <OPREL, MAI>             │
        │ Si Otro → devuelve <OPREL, MAY>  (retrocede)│
────────┼────────────────────────────────────────────┼──────────────────────────────
  6     │ Leímos '!' — puede ser NOT o DIS (!=)      │ q0 lee '!'
        │ Si '=' → devuelve <OPREL, DIS>             │
        │ Si Otro → devuelve <NOT, _>      (retrocede)│
────────┼────────────────────────────────────────────┼──────────────────────────────
  7     │ Leímos '/' — puede ser DIV o comentario    │ q0 lee '/'
        │ Si '/' → entra a comentario de línea (q8)  │
        │ Si '*' → entra a comentario bloque  (q9)   │
        │ Si Otro → devuelve <DIV, _>      (retrocede)│
────────┼────────────────────────────────────────────┼──────────────────────────────
  8     │ Dentro de comentario de línea //            │ q7 lee '/'
        │ Loop: ignora todo hasta EOLN o EOF         │
        │ Sale: EOLN → vuelve a q0 (fin del coment.) │
        │       EOF  → devuelve <FIN, _>             │
────────┼────────────────────────────────────────────┼──────────────────────────────
  9     │ Dentro de comentario de bloque /* ... */   │ q7 lee '*'
        │ Loop: ignora todo hasta encontrar '*'      │
        │ Sale: '*' → va a q10 (posible cierre)      │
        │       EOF  → devuelve <ERROR, _>           │
────────┼────────────────────────────────────────────┼──────────────────────────────
 10     │ Dentro del bloque, leímos '*' — ¿cierra?   │ q9 lee '*'
        │ Si '/' → cierra el comentario, vuelve a q0 │
        │ Si '*' → sigue en q10 (otro asterisco)     │
        │ Si Otro → vuelve a q9 (no era el cierre)   │
        │ Si EOF  → devuelve <ERROR, _>              │
────────┼────────────────────────────────────────────┼──────────────────────────────
 11     │ Acumulando LITERAL con comilla simple ' '  │ q0 lee '\''
        │ Loop: acumula hasta encontrar '\''         │
        │ Sale: '\'' → devuelve <LITERAL, 0>         │
        │       EOF/EOLN → devuelve <ERROR, _>       │
────────┼────────────────────────────────────────────┼──────────────────────────────
 12     │ Acumulando LITERAL con comilla doble " "   │ q0 lee '"'
        │ Loop: acumula hasta encontrar '"'          │
        │ Sale: '"' → devuelve <LITERAL, 0>          │
        │       EOF/EOLN → devuelve <ERROR, _>       │
```

---

## 5. Cómo hacer cambios frecuentes

### Agregar una nueva palabra clave

**Ejemplo:** quiero agregar la palabra `"loop"` como token nuevo.

**Paso 1 — `Token.java`:** agregar la constante y actualizar el array de nombres.
```java
// Agregar después de SUMA:
public static final int LOOP = 43;   // "loop" — nueva palabra clave

// En NOMtokenSTR[] agregar al final:
"...", "SUMA", "LOOP"
```

**Paso 2 — `Analex.java`:** agregar a la TPC.
```java
// En el bloque static{} de TPC:
TPC.put("loop", Token.LOOP);
```

**Listo.** El estado 2 del DT ya consulta la TPC automáticamente. No hay que tocar el `dt()`.

---

### Cambiar de case-insensitive a case-sensitive

Buscar en `Analex.java` las dos líneas con `Character.toLowerCase(c)`:

```java
// Estado 0 — primer char del ID:
ac += Character.toLowerCase(c);   // ← cambiar a: ac += c;

// Estado 2 — resto del ID:
ac += Character.toLowerCase(c);   // ← cambiar a: ac += c;
```

Y cambiar las claves de la TPC a la forma exacta del lenguaje:
```java
// Si el lenguaje es case-sensitive y las palabras son mayúsculas:
TPC.put("PROGRAM", Token.PROGRAM);   // en vez de "program"
TPC.put("VAR", Token.VAR);           // etc.
```

---

### Agregar un nuevo tipo de comentario

**Ejemplo:** comentario que va de `//` hasta `//` (en vez de EOLN).

En el estado 8, cambiar:
```java
case 8:
    // Antes: sale con EOLN
    // Ahora: necesitamos detectar "//"
    if (c == Cinta.EOF) {
        R.set(Token.FIN, 0);
        return;
    } else if (c == '/') {
        // Posible cierre "//"
        M.avanzar();
        // Verificar si el siguiente también es '/'
        if (M.cc() == '/') {
            M.avanzar();
            estado = 0;   // Cerró el comentario
        }
        // Si no, seguir ignorando
    } else if (c == Cinta.EOLN) {
        M.avanzar();
        estado = 0;    // Cierre original con EOLN — mantener compatibilidad
    } else {
        M.avanzar();
    }
    break;
```

---

### Agregar un nuevo operador de un solo char

**Ejemplo:** quiero que `^` sea el operador potencia `POT`.

**Paso 1 — `Token.java`:**
```java
public static final int POT = 44;   // "^"
// Agregar "POT" al final de NOMtokenSTR[]
```

**Paso 2 — `Analex.java`, estado 0:**
```java
// En la sección de operadores de un solo char:
if (c == '^') { ac = "^"; M.avanzar(); R.set(Token.POT, 0); return; }
```

---

### Agregar un nuevo operador de dos chars

**Ejemplo:** quiero que `**` sea potencia.

**Paso 1 — `Token.java`:** igual que arriba.

**Paso 2 — `Analex.java`:** agregar un nuevo estado (ej: estado 13).

En el estado 0:
```java
if (c == '*') { M.avanzar(); estado = 13; break; }
```

Nuevo estado 13:
```java
case 13:
    if (c == '*') {
        ac = "**";
        M.avanzar();
        R.set(Token.POT, 0);
    } else {
        // Solo era '*' → POR
        ac = "*";
        R.set(Token.POR, 0);
    }
    return;
```

---

## 6. Estándares de programación usados

### 6.1 Convenciones

| Elemento | Convención | Ejemplo |
|---|---|---|
| Clases | `PascalCase` | `Token`, `Cinta`, `Analex` |
| Constantes de clase | `MAYUSCULAS_CON_GUION_BAJO` | `Token.WHILE`, `Cinta.EOF`, `CASE_SENSITIVE` |
| Estados del DT | `Q<numero>_<QUE_HACE>` (el número es el del dibujo) | `Q8_COMENTARIO_DE_LINEA` |
| Campos y variables | `camelCase` que diga qué guardan | `acumuladorLexema`, `caracterActual`, `estadoActual` |
| Métodos | `camelCase` que diga qué hacen (si es largo, no importa) | `saltarRetornosDeCarro()`, `atributoDelTipo()` |
| Booleanos / preguntas | prefijo `es...` | `esDigito()`, `esLetra()`, `esEspacio()` |
| Javadoc | cada clase y método explica *qué hace*, en palabras simples | `/** Dibuja la cinta alrededor del cabezal */` |
| `toString()` | cada clase se puede imprimir con `System.out.println(objeto)` | `System.out.println(cinta)` |

**Vocabulario del curso que se conservó** (porque así lo usa el docente, el DT y la pizarra):
`cc()`, `avanzar()`, `init()`, `Preanalisis()`, `lexema()`, `dt()`, `TPC`, `EOF`, `EOLN`,
`onComunicate()`, `getNom()`, `getAtr()`, `getPos()`, `getFila()`, `getColumna()`.

### 6.2 Glosario de nombres (antes → ahora)

**Cinta.java**

| Antes | Ahora |
|---|---|
| `charAnt` | `caracterAnterior` |
| `col` | `columna` |
| `CR` / `LF` | `RETORNO_DE_CARRO` / `SALTO_DE_LINEA` |
| `ignoreCR()` | `saltarRetornosDeCarro()` |
| `forward()` | `moverCabezalUnaCeldaALaDerecha()` |
| *(nuevo)* | `toString()` dibuja la cinta con el cabezal · `nombreVisible(char)` |

**Token.java**

| Antes | Ahora |
|---|---|
| `nom` / `atr` (campos) | `nombre` / `atributo` |
| `atrToString()` | `atributoComoTexto()` |
| `get(lista, i)` | `elementoODesconocido(lista, i)` |
| `NOMtokenSTR` / `OPRELstr` / `TIPOstr` | `NOMBRES_DE_TOKEN` / `NOMBRES_ATRIBUTO_OPREL` / `NOMBRES_ATRIBUTO_TIPO` |
| `DESCONOCIDO` | `TEXTO_DESCONOCIDO` |
| *(nuevo)* | `ULTIMO_NOMBRE_DE_TOKEN` (subirlo al agregar un token) · `descripcionDetallada()` |

**Analex.java**

| Antes | Ahora |
|---|---|
| `M` | `cinta` |
| `R` | `tokenPreanalisis` |
| `ac` | `acumuladorLexema` |
| `pos` | `posicionInicioLexema` |
| `estado` | `estadoActual` |
| `c` (en `dt()`) | `caracterActual` |
| `nom` / `atr` (en `dt()`) | `nombreDelToken` / `atributoDelToken` |
| `tipoAtr()` | `atributoDelTipo()` |
| `case 0 … case 13` | `Q0_INICIAL`, `Q1_NUMERO`, `Q2_IDENTIFICADOR`, `Q3_LEIDO_DOS_PUNTOS`, `Q4_LEIDO_MENOR`, `Q5_LEIDO_MAYOR`, `Q6_LEIDO_ADMIRACION`, `Q7_LEIDO_BARRA`, `Q8_COMENTARIO_DE_LINEA`, `Q9_COMENTARIO_DE_BLOQUE`, `Q10_BLOQUE_LEIDO_ASTERISCO`, `Q11_LITERAL_COMILLA_SIMPLE`, `Q12_LITERAL_COMILLA_DOBLE`, `Q13_LINEA_LEIDA_SEGUNDA_BARRA` |
| *(nuevo)* | `toString()` muestra el token actual, el lexema y la cinta |

**Main.java**

| Antes | Ahora |
|---|---|
| `chk` | `verificarTokenCompleto` |
| `chkNom` | `verificarSoloNombre` |
| `chkNomNot` | `verificarNombreDistinto` |
| `chkAtrNot` | `verificarAtributoDistinto` |
| `chkStr` | `verificarTextoDelToken` |
| `chkSeq` | `verificarSecuenciaDeNombres` |
| `titulo` | `imprimirTitulo` |
| `ok` / `fail` | `verificacionesCorrectas` / `verificacionesFallidas` |

### 6.3 `toString()` para entender desde el `main`

Cada clase se imprime con un `println` y muestra su estado:

```
System.out.println(cinta);
Cinta{ cabezal=3, fila=0, columna=3, cc=c }
  [a][b][EOLN][c][d][EOF]
               ^

System.out.println(analizador);
Analex{ preanalisis=<NUM,30>, lexema="10", inicioDelLexema=5 }
  Cinta{ cabezal=7, fila=0, columna=7, cc=; }
    [x][' '][:][=][' '][1][0][;][EOF]
                              ^

System.out.println(token);                       // <OPREL,MAI>
System.out.println(token.descripcionDetallada()); // Token{ nombre=OPREL(40), atributo=4 → <OPREL,MAI> }
```
`Cinta`, `Token` y `Analex` tienen además un `main` de demostración: clic derecho sobre el archivo → *Run File*.

### 6.4 Principios aplicados

- **Nombres que explican:** una variable o método se entiende sin leer su cuerpo.
- **Un solo nivel de abstracción por método:** `dt()` solo decide estados; `esDigito()`, `esLetra()`, `esEspacio()` encapsulan la clasificación de chars.
- **Constantes en lugar de números mágicos:** `case Q8_COMENTARIO_DE_LINEA`, no `case 8`; `c == '/'`, no `c == 47`.
- **Separación de responsabilidades:** `Token` = datos, `Cinta` = entrada, `Analex` = lógica, `Interfaz` = pantalla.
- **Fail-fast:** estados desconocidos devuelven `ERROR` inmediatamente en el `default` del switch.

---

> **Nota para la defensa:**
> Si el docente pide un cambio en el momento, buscá la sección
> "Cómo hacer cambios frecuentes" de este documento.
> Los cambios más comunes son: agregar palabra clave (solo TPC),
> cambiar case sensitivity (solo 2 líneas), o agregar operador (solo estado 0).
