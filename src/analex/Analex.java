package analex;

import java.util.HashMap;

/**
 * Analizador Lexico (Scanner) para el lenguaje hibrido Pascal + Java.
 * INF329-SA Compiladores - Gestion 2-2026.
 *
 * <p>Analex es una "cinta de tokens": lee la {@link Cinta} de caracteres y va
 * entregando los tokens de uno en uno.
 * <ul>
 *   <li>{@link #Preanalisis()} = el token actual (no lo consume).</li>
 *   <li>{@link #lexema()}      = el texto con el que se formo ese token.</li>
 *   <li>{@link #avanzar()}     = pasa al token siguiente.</li>
 * </ul>
 *
 * <p>El lenguaje es CASE-SENSITIVE (ver interruptor {@link #CASE_SENSITIVE}): las palabras
 * clave se reconocen solo en minusculas.
 *
 * <p>Tokens reconocidos:
 * <pre>
 *   Palabras clave: program, var, procedure/void, begin/{, end/},
 *                   if, then, else, for, to, do, downto, while,
 *                   case, of, repeat, until, read, readln, print,
 *                   println, not/!, and/&amp;, or/|, mod/%, div
 *   Tipos: boolean, integer
 *   Operadores: + - * , : ; ( ) :=
 *   Operadores relacionales: = &lt; &gt; &lt;= &gt;= &lt;&gt; !=
 *   NUM     (numeros enteros sin signo)
 *   ID      (empieza con letra, sigue letras/digitos)
 *   LITERAL (chars entre comillas simples o dobles)
 *   Comentarios // y comentarios bloque - se ignoran
 * </pre>
 */
public class Analex {

    // ─── INTERRUPTORES: enunciado (PDF) vs. cambios de la defensa ───────────
    // Valor actual = comportamiento de la defensa. Entre parentesis: lo que dice el PDF.
    /** true: distingue MAYUSCULAS/minusculas.  (PDF: false = NO es case-sensitive) */
    public static final boolean CASE_SENSITIVE = true;
    /** true: "// texto //" cierra el comentario con otro "//".  (PDF: false = hasta fin de linea) */
    public static final boolean COMENTARIO_CIERRA_CON_DOBLE_BARRA = true;
    /** true: reconoce "por" y "sumi" como palabras clave.  (PDF: false = no existen) */
    public static final boolean EXTRAS_DEFENSA = true;
    /** true: el ID acepta '_'.  (PDF: false = solo letras y digitos) */
    public static final boolean ID_ACEPTA_GUION_BAJO = true;

    // ─── Campos de la clase ──────────────────────────────────────────────────
    /** La cinta de caracteres que se esta leyendo. */
    private Cinta cinta;

    /** Preanalisis: el token que dejo listo el ultimo dt() (el token actual). */
    private Token tokenPreanalisis;

    /** Acumulador del lexema: los chars que el dt() va juntando mientras lee un token. */
    private String acumuladorLexema;

    /** Celda de la cinta donde empieza el lexema actual (para resaltarlo en el editor). */
    private int posicionInicioLexema;

    /** Explicacion del ultimo ERROR (vacia si el token actual no es un ERROR). La usa la Interfaz. */
    private String mensajeDeError = "";

    // ─── TPC — Tabla de Palabras Clave ───────────────────────────────────────
    /**
     * TPC (OBLIGADA por el enunciado: tabla private de Analex).
     * Mapea: lexema de la palabra clave → nombre del token (entero).
     * Se consulta al terminar de leer un identificador (estado q2 del DT).
     */
    private static final HashMap<String, Integer> TPC = new HashMap<>();

    static {
        // Palabras reservadas
        TPC.put("program",   Token.PROGRAM);
        TPC.put("var",       Token.VAR);
        TPC.put("procedure", Token.PROCEDURE);
        TPC.put("void",      Token.PROCEDURE);   // alias Java
        TPC.put("begin",     Token.BEGIN);
        TPC.put("end",       Token.END);
        TPC.put("if",        Token.IF);
        TPC.put("then",      Token.THEN);
        TPC.put("else",      Token.ELSE);
        TPC.put("for",       Token.FOR);
        TPC.put("to",        Token.TO);
        TPC.put("do",        Token.DO);
        TPC.put("downto",    Token.DOWNTO);
        TPC.put("while",     Token.WHILE);
        TPC.put("case",      Token.CASE);
        TPC.put("of",        Token.OF);
        TPC.put("repeat",    Token.REPEAT);
        TPC.put("until",     Token.UNTIL);
        TPC.put("read",      Token.READ);
        TPC.put("readln",    Token.READLN);
        TPC.put("print",     Token.PRINT);
        TPC.put("println",   Token.PRINTLN);
        TPC.put("not",       Token.NOT);
        TPC.put("and",       Token.AND);
        TPC.put("or",        Token.OR);
        TPC.put("mod",       Token.MOD);
        TPC.put("div",       Token.DIV);
        // Tipos de datos
        TPC.put("boolean",   Token.TIPO);
        TPC.put("integer",   Token.TIPO);
        // Agregados en defensa (pizarra) — ver interruptor EXTRAS_DEFENSA
        if (EXTRAS_DEFENSA) {
            TPC.put("por",   Token.POR);    // "por" → <POR,_>
            TPC.put("sumi",  Token.SUMA);   // "sumi" → <SUMA,_>
        }
    }

    /** Atributo del token TIPO segun su lexema: "boolean" → BOOLEAN, "integer" → INTEGER. */
    private int atributoDelTipo(String lexemaDelTipo) {
        return lexemaDelTipo.equals("boolean") ? Token.BOOLEAN : Token.INTEGER;
    }

    // ─── Constructor ─────────────────────────────────────────────────────────

    /**
     * Crea el analizador sobre una cinta y calcula de inmediato el primer token
     * (queda listo para consultarse con {@link #Preanalisis()}).
     * @param cintaDeCaracteres la cinta con el programa fuente.
     */
    public Analex(Cinta cintaDeCaracteres) {
        cinta = cintaDeCaracteres;
        tokenPreanalisis = new Token();
        init();
    }

    /** Reinicia la cinta al principio y calcula otra vez el primer token. */
    public final void init() {
        cinta.init();
        avanzar();      // Calcular el primer preanalisis
    }

    // ─── Consultar el token actual ───────────────────────────────────────────

    /** @return el token actual (preanalisis). No lo consume: se puede llamar muchas veces. */
    public Token Preanalisis() {
        return tokenPreanalisis;
    }

    /** @return el texto (lexema) con el que se formo el token actual. */
    public String lexema() {
        return acumuladorLexema;
    }

    /** @return por que fallo el analisis si el token actual es ERROR (ej. "literal sin cerrar"); si no, "". */
    public String getMensajeDeError() {
        return mensajeDeError;
    }

    /** Pasa al token siguiente: corre el DT y avisa a la interfaz para resaltarlo. */
    public void avanzar() {
        dt();
        resaltar();
    }

    // ─── Estados del DT (el numero coincide con el dibujo: q0 ... q13) ───────
    private static final int Q0_INICIAL                     = 0;   // inicial / entre tokens (ignora espacios y comentarios)
    private static final int Q1_NUMERO                      = 1;   // acumulando NUM (digitos)
    private static final int Q2_IDENTIFICADOR               = 2;   // acumulando ID o palabra clave (letra + letras/digitos)
    private static final int Q3_LEIDO_DOS_PUNTOS            = 3;   // leimos ':'  → DOSPTOS o ASSIGN (:=)
    private static final int Q4_LEIDO_MENOR                 = 4;   // leimos '<'  → MEN, MEI (<=) o DIS (<>)
    private static final int Q5_LEIDO_MAYOR                 = 5;   // leimos '>'  → MAY o MAI (>=)
    private static final int Q6_LEIDO_ADMIRACION            = 6;   // leimos '!'  → DIS (!=) o NOT (!)
    private static final int Q7_LEIDO_BARRA                 = 7;   // leimos '/'  → DIV, comentario // o comentario /*
    private static final int Q8_COMENTARIO_DE_LINEA         = 8;   // dentro de un comentario //
    private static final int Q9_COMENTARIO_DE_BLOQUE        = 9;   // dentro de un comentario /* ... */
    private static final int Q10_BLOQUE_LEIDO_ASTERISCO     = 10;  // dentro de /* y leimos '*' (posible cierre)
    private static final int Q11_LITERAL_COMILLA_SIMPLE     = 11;  // acumulando LITERAL con comilla simple
    private static final int Q12_LITERAL_COMILLA_DOBLE      = 12;  // acumulando LITERAL con comilla doble
    private static final int Q13_LINEA_LEIDA_SEGUNDA_BARRA  = 13;  // dentro de //, leimos '/' (¿cierra con "//"?)

    // ─── DT — Diagrama de Transiciones ───────────────────────────────────────
    /**
     * El DT: lee chars de la cinta hasta formar UN token y lo deja en el preanalisis.
     * Cada {@code case} del switch es un estado del dibujo (q0 ... q13).
     * Variables: {@code estadoActual} (en que estado estamos), {@code caracterActual}
     * (el char bajo el cabezal) y {@code acumuladorLexema} (el texto que se va juntando).
     */
    private void dt() {
        int estadoActual = Q0_INICIAL;
        acumuladorLexema = "";
        mensajeDeError = "";
        char caracterActual;

        while (true) {
            caracterActual = cinta.cc();

            switch (estadoActual) {

                // ── Estado 0: inicio / entre tokens ──────────────────────
                case Q0_INICIAL:
                    // Ignorar espacios
                    if (esEspacio(caracterActual)) {
                        cinta.avanzar();
                        break;
                    }

                    // Marcar posición de inicio del lexema
                    posicionInicioLexema = cinta.getPos();

                    if (caracterActual == Cinta.EOF) {
                        tokenPreanalisis.set(Token.FIN, 0);
                        return;
                    }

                    // Dígito → NUM
                    if (esDigito(caracterActual)) {
                        acumuladorLexema += caracterActual;
                        cinta.avanzar();
                        estadoActual = Q1_NUMERO;
                        break;
                    }

                    // Letra → ID o palabra clave
                    // CASE-SENSITIVE: NO convertir a minusculas
                    if (esLetra(caracterActual)) {
                        acumuladorLexema += caracterActual;   // respetar mayusculas/minusculas exactas
                        cinta.avanzar();
                        estadoActual = Q2_IDENTIFICADOR;
                        break;
                    }

                    // Comilla simple → LITERAL
                    if (caracterActual == '\'') {
                        cinta.avanzar();
                        estadoActual = Q11_LITERAL_COMILLA_SIMPLE;
                        break;
                    }

                    // Comilla doble → LITERAL
                    if (caracterActual == '"') {
                        cinta.avanzar();
                        estadoActual = Q12_LITERAL_COMILLA_DOBLE;
                        break;
                    }

                    // Operadores de un solo carácter
                    if (caracterActual == '+') { acumuladorLexema = "+"; cinta.avanzar(); tokenPreanalisis.set(Token.MAS,   0); return; }
                    if (caracterActual == '-') { acumuladorLexema = "-"; cinta.avanzar(); tokenPreanalisis.set(Token.MENOS, 0); return; }
                    if (caracterActual == '*') { acumuladorLexema = "*"; cinta.avanzar(); tokenPreanalisis.set(Token.POR,   0); return; }
                    if (caracterActual == '%') { acumuladorLexema = "%"; cinta.avanzar(); tokenPreanalisis.set(Token.MOD,   0); return; }
                    if (caracterActual == ',') { acumuladorLexema = ","; cinta.avanzar(); tokenPreanalisis.set(Token.COMA,  0); return; }
                    if (caracterActual == ';') { acumuladorLexema = ";"; cinta.avanzar(); tokenPreanalisis.set(Token.PTOCOMA, 0); return; }
                    if (caracterActual == '(') { acumuladorLexema = "("; cinta.avanzar(); tokenPreanalisis.set(Token.PA,    0); return; }
                    if (caracterActual == ')') { acumuladorLexema = ")"; cinta.avanzar(); tokenPreanalisis.set(Token.PC,    0); return; }
                    if (caracterActual == '{') { acumuladorLexema = "{"; cinta.avanzar(); tokenPreanalisis.set(Token.BEGIN, 0); return; }
                    if (caracterActual == '}') { acumuladorLexema = "}"; cinta.avanzar(); tokenPreanalisis.set(Token.END,   0); return; }
                    if (caracterActual == '&') { acumuladorLexema = "&"; cinta.avanzar(); tokenPreanalisis.set(Token.AND,   0); return; }
                    if (caracterActual == '|') { acumuladorLexema = "|"; cinta.avanzar(); tokenPreanalisis.set(Token.OR,    0); return; }

                    // Operadores de uno o dos chars
                    if (caracterActual == ':') { acumuladorLexema = ":"; cinta.avanzar(); estadoActual = Q3_LEIDO_DOS_PUNTOS; break; }
                    if (caracterActual == '<') { acumuladorLexema = "<"; cinta.avanzar(); estadoActual = Q4_LEIDO_MENOR; break; }
                    if (caracterActual == '>') { acumuladorLexema = ">"; cinta.avanzar(); estadoActual = Q5_LEIDO_MAYOR; break; }
                    if (caracterActual == '!') { acumuladorLexema = "!"; cinta.avanzar(); estadoActual = Q6_LEIDO_ADMIRACION; break; }

                    // '/' → DIV o comentario
                    if (caracterActual == '/') { cinta.avanzar(); estadoActual = Q7_LEIDO_BARRA; break; }

                    // '=' → OPREL IGUAL
                    if (caracterActual == '=') { acumuladorLexema = "="; cinta.avanzar(); tokenPreanalisis.set(Token.OPREL, Token.IGUAL); return; }

                    // Char no reconocido → ERROR
                    acumuladorLexema = "" + caracterActual;
                    cinta.avanzar();
                    marcarError("carácter no reconocido '" + caracterActual + "'");
                    return;

                // ── Estado 1: acumulando NUM ──────────────────────────────
                case Q1_NUMERO:
                    if (esDigito(caracterActual)) {
                        acumuladorLexema += caracterActual;
                        cinta.avanzar();
                    } else {
                        // Retroceso implicito: no avanzamos
                        // Enunciado: <NUM, 30> — atributo SIEMPRE es 30
                        tokenPreanalisis.set(Token.NUM, 30);
                        return;
                    }
                    break;

                // ── Estado 2: acumulando ID / palabra clave ───────────────
                case Q2_IDENTIFICADOR:
                    if (esLetra(caracterActual) || esDigito(caracterActual)) {
                        acumuladorLexema += caracterActual;   // CASE-SENSITIVE: sin toLowerCase
                        cinta.avanzar();
                    } else {
                        // Retroceso implícito
                        // Consultar TPC (si NO es case-sensitive, se busca en minusculas;
                        // el lexema se conserva tal como se escribio)
                        String clave = CASE_SENSITIVE ? acumuladorLexema : acumuladorLexema.toLowerCase();
                        if (TPC.containsKey(clave)) {
                            int nombreDelToken = TPC.get(clave);
                            int atributoDelToken = (nombreDelToken == Token.TIPO) ? atributoDelTipo(clave) : 0;
                            tokenPreanalisis.set(nombreDelToken, atributoDelToken);
                        } else {
                            // No está en la TPC → es un ID
                            // Atributo del ID = -1 (definido en Token.java)
                            tokenPreanalisis.set(Token.ID, -1);
                        }
                        return;
                    }
                    break;

                // ── Estado 3: leímos ':' ──────────────────────────────────
                case Q3_LEIDO_DOS_PUNTOS:
                    if (caracterActual == '=') {
                        acumuladorLexema = ":=";
                        cinta.avanzar();
                        tokenPreanalisis.set(Token.ASSIGN, 0);
                    } else {
                        // Retroceso implícito — solo era ':'
                        tokenPreanalisis.set(Token.DOSPTOS, 0);
                    }
                    return;

                // ── Estado 4: leímos '<' ──────────────────────────────────
                case Q4_LEIDO_MENOR:
                    if (caracterActual == '=') {
                        acumuladorLexema = "<=";
                        cinta.avanzar();
                        tokenPreanalisis.set(Token.OPREL, Token.MEI);
                    } else if (caracterActual == '>') {
                        acumuladorLexema = "<>";
                        cinta.avanzar();
                        tokenPreanalisis.set(Token.OPREL, Token.DIS);
                    } else {
                        // Solo era '<'
                        tokenPreanalisis.set(Token.OPREL, Token.MEN);
                    }
                    return;

                // ── Estado 5: leímos '>' ──────────────────────────────────
                case Q5_LEIDO_MAYOR:
                    if (caracterActual == '=') {
                        acumuladorLexema = ">=";
                        cinta.avanzar();
                        tokenPreanalisis.set(Token.OPREL, Token.MAI);
                    } else {
                        // Solo era '>'
                        tokenPreanalisis.set(Token.OPREL, Token.MAY);
                    }
                    return;

                // ── Estado 6: leímos '!' ──────────────────────────────────
                case Q6_LEIDO_ADMIRACION:
                    if (caracterActual == '=') {
                        acumuladorLexema = "!=";
                        cinta.avanzar();
                        tokenPreanalisis.set(Token.OPREL, Token.DIS);
                    } else {
                        // '!' solo → alias de NOT
                        tokenPreanalisis.set(Token.NOT, 0);
                    }
                    return;

                // ── Estado 7: leímos '/' ──────────────────────────────────
                case Q7_LEIDO_BARRA:
                    if (caracterActual == '/') {
                        // Comentario de línea //
                        cinta.avanzar();
                        estadoActual = Q8_COMENTARIO_DE_LINEA;
                    } else if (caracterActual == '*') {
                        // Comentario de bloque /*
                        cinta.avanzar();
                        estadoActual = Q9_COMENTARIO_DE_BLOQUE;
                    } else {
                        // Solo era '/' → DIV
                        acumuladorLexema = "/";
                        tokenPreanalisis.set(Token.DIV, 0);
                        return;
                    }
                    break;

                // ── Estado 8: dentro de comentario de linea // ────────────
                // Sale con EOLN, EOF, o al encontrar '//' (cierre explicito)
                case Q8_COMENTARIO_DE_LINEA:
                    if (caracterActual == Cinta.EOF) {
                        // Llegó el EOF dentro del comentario → FIN
                        tokenPreanalisis.set(Token.FIN, 0);
                        return;
                    } else if (caracterActual == Cinta.EOLN) {
                        // Fin de linea → termina el comentario
                        cinta.avanzar();
                        posicionInicioLexema = cinta.getPos();
                        estadoActual = Q0_INICIAL;
                    } else if (COMENTARIO_CIERRA_CON_DOBLE_BARRA && caracterActual == '/') {
                        // Posible cierre explicito '//'
                        cinta.avanzar();
                        estadoActual = Q13_LINEA_LEIDA_SEGUNDA_BARRA;   // verificar si el siguiente tambien es '/'
                    } else {
                        cinta.avanzar();
                    }
                    break;

                // ── Estado 13: dentro del comentario //, leimos '/' ───────
                // Si el siguiente char es '/' → cierre explicito del comentario
                // Si no → seguir ignorando (volver a q8)
                case Q13_LINEA_LEIDA_SEGUNDA_BARRA:
                    if (caracterActual == '/') {
                        // Encontramos '//' → cierra el comentario
                        cinta.avanzar();
                        posicionInicioLexema = cinta.getPos();
                        estadoActual = Q0_INICIAL;   // volver al estado inicial
                    } else if (caracterActual == Cinta.EOF) {
                        tokenPreanalisis.set(Token.FIN, 0);
                        return;
                    } else if (caracterActual == Cinta.EOLN) {
                        // Fin de linea tambien cierra
                        cinta.avanzar();
                        posicionInicioLexema = cinta.getPos();
                        estadoActual = Q0_INICIAL;
                    } else {
                        // No era '//' — seguir dentro del comentario
                        cinta.avanzar();
                        estadoActual = Q8_COMENTARIO_DE_LINEA;
                    }
                    break;

                // ── Estado 9: dentro de comentario de bloque /* ... */ ────
                case Q9_COMENTARIO_DE_BLOQUE:
                    if (caracterActual == Cinta.EOF) {
                        // EOF dentro de comentario sin cerrar → ERROR
                        marcarError("comentario de bloque sin cerrar (falta */)");
                        return;
                    } else if (caracterActual == '*') {
                        // Posible cierre */
                        cinta.avanzar();
                        estadoActual = Q10_BLOQUE_LEIDO_ASTERISCO;
                    } else {
                        cinta.avanzar();
                    }
                    break;

                // ── Estado 10: dentro de /* y leímos '*' ─────────────────
                case Q10_BLOQUE_LEIDO_ASTERISCO:
                    if (caracterActual == '/') {
                        // Cierre del comentario */
                        cinta.avanzar();
                        posicionInicioLexema = cinta.getPos();   // actualizar posicionInicioLexema
                        estadoActual = Q0_INICIAL;         // volvemos a buscar tokens
                    } else if (caracterActual == '*') {
                        // Otro '*' — puede seguir siendo cierre
                        cinta.avanzar();
                        // Seguimos en estado 10
                    } else if (caracterActual == Cinta.EOF) {
                        marcarError("comentario de bloque sin cerrar (falta */)");
                        return;
                    } else {
                        // No era '/' — seguimos dentro del comentario
                        cinta.avanzar();
                        estadoActual = Q9_COMENTARIO_DE_BLOQUE;
                    }
                    break;

                // ── Estado 11: acumulando LITERAL con comilla simple ──────
                case Q11_LITERAL_COMILLA_SIMPLE:
                    if (caracterActual == '\'') {
                        // Cierra el literal
                        cinta.avanzar();
                        tokenPreanalisis.set(Token.LITERAL, 0);
                        return;
                    } else if (caracterActual == Cinta.EOF || caracterActual == Cinta.EOLN) {
                        // Literal sin cerrar → ERROR
                        marcarError("literal sin cerrar (falta la comilla de cierre)");
                        return;
                    } else {
                        acumuladorLexema += caracterActual;
                        cinta.avanzar();
                    }
                    break;

                // ── Estado 12: acumulando LITERAL con comilla doble ───────
                case Q12_LITERAL_COMILLA_DOBLE:
                    if (caracterActual == '"') {
                        // Cierra el literal
                        cinta.avanzar();
                        tokenPreanalisis.set(Token.LITERAL, 0);
                        return;
                    } else if (caracterActual == Cinta.EOF || caracterActual == Cinta.EOLN) {
                        // Literal sin cerrar → ERROR
                        marcarError("literal sin cerrar (falta la comilla de cierre)");
                        return;
                    } else {
                        acumuladorLexema += caracterActual;
                        cinta.avanzar();
                    }
                    break;

                default:
                    // Estado desconocido — no debería llegar aquí
                    marcarError("estado desconocido del DT");
                    return;
            }
        }
    }

    /** Deja el token ERROR en el preanalisis y recuerda el motivo (se muestra en la Interfaz). */
    private void marcarError(String motivo) {
        mensajeDeError = motivo;
        tokenPreanalisis.set(Token.ERROR, 0);
    }

    // ─── Helpers: clasificacion de chars ─────────────────────────────────────

    /** Espacio en blanco para el DT: espacio, TAB o fin de linea. */
    private boolean esEspacio(char caracter) {
        return caracter == ' ' || caracter == '\t' || caracter == Cinta.EOLN;
    }

    /** Digito decimal: 0 a 9. */
    private boolean esDigito(char caracter) {
        return caracter >= '0' && caracter <= '9';
    }

    /** Letra para identificadores: a-z, A-Z (y '_' si {@link #ID_ACEPTA_GUION_BAJO}). */
    private boolean esLetra(char caracter) {
        return (caracter >= 'a' && caracter <= 'z') || (caracter >= 'A' && caracter <= 'Z')
                || (ID_ACEPTA_GUION_BAJO && caracter == '_');
    }

    // ─── Resaltado (para la Interfaz) ────────────────────────────────────────

    /** Avisa a la cinta que se leyo un lexema; la Interfaz lo usa para pintarlo en el editor. */
    public void resaltar() {
        cinta.onComunicate(posicionInicioLexema, lexema());
    }

    // ─── Posicion en la cinta (fila y columna) ───────────────────────────────

    /** @return la fila donde esta el cabezal de la cinta (base 0), justo despues de leer el token. */
    public int getFila() {
        return cinta.getFila();
    }

    /** @return la columna donde esta el cabezal de la cinta (base 0), justo despues de leer el token. */
    public int getColumna() {
        return cinta.getColumna();
    }

    // ─── toString: para ver el estado del analizador con un print ────────────

    /**
     * Muestra el token actual, su lexema y la cinta con el cabezal, por ejemplo:
     * <pre>
     * Analex{ preanalisis=&lt;ID,-1&gt;, lexema="x", inicioDelLexema=0 }
     *   Cinta{ cabezal=1, fila=0, columna=1, cc=' ' }
     *     [x][' '][:][=][' '][1][0][;][EOF]
     *            ^
     * </pre>
     */
    @Override
    public String toString() {
        return "Analex{ preanalisis=" + tokenPreanalisis + ", lexema=\"" + acumuladorLexema
                + "\", inicioDelLexema=" + posicionInicioLexema + " }\n  "
                + cinta.toString().replace("\n", "\n  ");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DEMO POR CONSOLA — una llamada a cada metodo de Analex, con ejemplos
    //  Ejecutar: clic derecho en Analex.java → Run File (Shift+F6)
    // ═════════════════════════════════════════════════════════════════════════

    /** Recorre el programa fuente completo imprimiendo un token por linea. */
    private static void mostrarTodosLosTokens(String programaFuente) {
        System.out.println("   fuente: \"" + programaFuente.replace("\n", "\\n") + "\"");
        Analex analizador = new Analex(new Cinta(programaFuente));
        while (true) {
            Token tokenActual = analizador.Preanalisis();
            System.out.printf("      lexema=%-10s nombre=%-2d atributo=%-3d %s%n",
                    "\"" + analizador.lexema() + "\"", tokenActual.getNom(), tokenActual.getAtr(), tokenActual);
            if (tokenActual.getNom() == Token.FIN || tokenActual.getNom() == Token.ERROR) break;
            analizador.avanzar();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Constructor: new Analex(cinta) calcula el 1er preanálisis ===");
        Cinta cinta = new Cinta("x := 10;");
        Analex analizador = new Analex(cinta);
        System.out.println("   Ya hay un token sin llamar a avanzar():");
        System.out.println(analizador);

        System.out.println("\n=== 2. Preanalisis() y lexema() — NO consumen nada ===");
        System.out.println("   Preanalisis() dos veces: " + analizador.Preanalisis() + " " + analizador.Preanalisis());
        System.out.println("   lexema() = \"" + analizador.lexema() + "\"");

        System.out.println("\n=== 3. avanzar() — pasa al siguiente token (se imprime el analizador completo) ===");
        while (analizador.Preanalisis().getNom() != Token.FIN) {
            analizador.avanzar();
            System.out.println(analizador);
        }

        System.out.println("\n=== 4. init() — reinicia cinta y vuelve a calcular el primer token ===");
        analizador.init();
        System.out.println(analizador);

        System.out.println("\n=== 5. getFila() / getColumna() — posición de la cinta ===");
        Analex dosLineas = new Analex(new Cinta("a\nb"));
        System.out.println("   token " + dosLineas.Preanalisis() + " → fila=" + dosLineas.getFila() + " columna=" + dosLineas.getColumna());
        dosLineas.avanzar();
        System.out.println("   token " + dosLineas.Preanalisis() + " → fila=" + dosLineas.getFila() + " columna=" + dosLineas.getColumna());
        System.out.println("   (es la posición del CABEZAL tras leer el token, no donde empieza)");

        System.out.println("\n=== 6. resaltar() → Cinta.onComunicate(posicion, lexema) ===");
        Cinta cintaEspia = new Cinta("hola mundo") {
            @Override
            public void onComunicate(int posicion, String lexema) {
                System.out.println("   [onComunicate] posicion=" + posicion + " lexema=\"" + lexema + "\"");
            }
        };
        Analex analizadorConAviso = new Analex(cintaEspia);   // el constructor ya resalta "hola"
        analizadorConAviso.avanzar();                          // resalta "mundo"
        analizadorConAviso.avanzar();                          // resalta FIN

        System.out.println("\n=== 7. dt() por ejemplos: palabras clave, ID, NUM (TPC) ===");
        mostrarTodosLosTokens("while x1 123");
        mostrarTodosLosTokens("integer boolean void");

        System.out.println("\n=== 8. Case-sensitive: solo minúsculas son palabra clave ===");
        mostrarTodosLosTokens("while WHILE While");

        System.out.println("\n=== 9. Operadores de 1 y 2 caracteres (estados q3, q4, q5, q6) ===");
        mostrarTodosLosTokens(": := < <= <> > >= ! != =");

        System.out.println("\n=== 10. Alias Java/Pascal ===");
        mostrarTodosLosTokens("{ } & | % / begin end and or mod div");

        System.out.println("\n=== 11. Comentarios (estados q7, q8, q9, q10, q13) ===");
        mostrarTodosLosTokens("a // linea\nb");
        mostrarTodosLosTokens("a /* bloque\nmultilinea */ b");
        mostrarTodosLosTokens("a // texto // b");

        System.out.println("\n=== 12. LITERAL y ERRORES ===");
        mostrarTodosLosTokens("\"hola\" 'pascal'");
        mostrarTodosLosTokens("\"sin cerrar");
        mostrarTodosLosTokens("/* sin cerrar");
        mostrarTodosLosTokens("x @ y");

        System.out.println("\n=== 13. Extras de la defensa ===");
        mostrarTodosLosTokens("por sumi");

        System.out.println("\n=== 14. Programa completo ===");
        mostrarTodosLosTokens("program p;\nvar n : integer;\nbegin\n  read(n);\n  if (n > 0) then println(n);\nend");
    }
}
