package analex;

import java.util.HashMap;

/*
 * Analizador Lexico (Scanner) para el lenguaje Pascal+Java hibrido.
 * INF329-SA Compiladores - Gestion 2-2026
 *
 * El lenguaje es CASE-INSENSITIVE: todo se convierte a minusculas
 * antes de comparar con la TPC.
 *
 * Tokens reconocidos:
 *   Palabras clave: program, var, procedure/void, begin/{, end/},
 *                   if, then, else, for, to, do, downto, while,
 *                   case, of, repeat, until, read, readln, print,
 *                   println, not/!, and/&, or/|, mod/%, div
 *   Tipos: boolean, integer
 *   Operadores: + - * , : ; ( ) :=
 *   Operadores relacionales: = < > <= >= <> !=
 *   NUM     (numeros enteros sin signo)
 *   ID      (empieza con letra, sigue letras/digitos)
 *   LITERAL (chars entre comillas simples o dobles)
 *   Comentarios // y comentarios bloque - se ignoran
 */
public class Analex {   // Es una cinta de tokens

    private Cinta M;
    private Token R;        // Resultado del dt (Preanálisis)
    private String ac;      // Acumulador de lexema
    private int pos;        // Posición de inicio del lexema

    // ─── TPC — Tabla de Palabras Clave ─────────────────────────────────────
    // Mapea: lexema en minúsculas → nombre del token (entero)
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
        // Agregados en defensa (pizarra)
        TPC.put("por",       Token.POR);    // "por" → <POR,_>
        TPC.put("sumi",      Token.SUMA);   // "sumi" → <SUMA,_>
    }

    // Atributo del TIPO según lexema
    private int tipoAtr(String lexema) {
        return lexema.equals("boolean") ? Token.BOOLEAN : Token.INTEGER;
    }

    // ─── Constructor ────────────────────────────────────────────────────────
    public Analex(Cinta c) {
        M = c;
        R = new Token();
        init();
    }

    public final void init() {
        M.init();
        avanzar();      // Calcular el primer preanálisis
    }

    public Token Preanalisis() {
        return R;
    }

    public String lexema() {
        return ac;
    }

    public void avanzar() {
        dt();
        resaltar();
    }

    // ─── DT — Diagrama de Transiciones ──────────────────────────────────────
    /*
     * Estados:
     *   0  - Inicial / entre tokens (ignora espacios y comentarios)
     *   1  - Acumulando NUM (digitos)
     *   2  - Acumulando ID o palabra clave (letra + letras/digitos)
     *   3  - Leimos ':', puede ser DOSPTOS o ASSIGN (:=)
     *   4  - Leimos '<', puede ser MEN, MEI (<=) o DIS (<>)
     *   5  - Leimos '>', puede ser MAY o MAI (>=)
     *   6  - Leimos '!', puede ser DIS (!=) o NOT (!)
     *   7  - Leimos '/', puede ser DIV, comentario // o comentario /*
     *   8  - Dentro de comentario de linea (//)
     *   9  - Dentro de comentario de bloque
     *   10 - Dentro del comentario de bloque, leimos '*' (posible cierre)
     *   11 - Acumulando LITERAL con comilla simple
     *   12 - Acumulando LITERAL con comilla doble
     */
    private void dt() {
        int estado = 0;
        ac = "";
        char c;

        while (true) {
            c = M.cc();

            switch (estado) {

                // ── Estado 0: inicio / entre tokens ──────────────────────
                case 0:
                    // Ignorar espacios
                    if (esEspacio(c)) {
                        M.avanzar();
                        break;
                    }

                    // Marcar posición de inicio del lexema
                    pos = M.getPos();

                    if (c == Cinta.EOF) {
                        R.set(Token.FIN, 0);
                        return;
                    }

                    // Dígito → NUM
                    if (esDigito(c)) {
                        ac += c;
                        M.avanzar();
                        estado = 1;
                        break;
                    }

                    // Letra → ID o palabra clave
                    // CASE-SENSITIVE: NO convertir a minusculas
                    if (esLetra(c)) {
                        ac += c;   // respetar mayusculas/minusculas exactas
                        M.avanzar();
                        estado = 2;
                        break;
                    }

                    // Comilla simple → LITERAL
                    if (c == '\'') {
                        M.avanzar();
                        estado = 11;
                        break;
                    }

                    // Comilla doble → LITERAL
                    if (c == '"') {
                        M.avanzar();
                        estado = 12;
                        break;
                    }

                    // Operadores de un solo carácter
                    if (c == '+') { ac = "+"; M.avanzar(); R.set(Token.MAS,   0); return; }
                    if (c == '-') { ac = "-"; M.avanzar(); R.set(Token.MENOS, 0); return; }
                    if (c == '*') { ac = "*"; M.avanzar(); R.set(Token.POR,   0); return; }
                    if (c == '%') { ac = "%"; M.avanzar(); R.set(Token.MOD,   0); return; }
                    if (c == ',') { ac = ","; M.avanzar(); R.set(Token.COMA,  0); return; }
                    if (c == ';') { ac = ";"; M.avanzar(); R.set(Token.PTOCOMA, 0); return; }
                    if (c == '(') { ac = "("; M.avanzar(); R.set(Token.PA,    0); return; }
                    if (c == ')') { ac = ")"; M.avanzar(); R.set(Token.PC,    0); return; }
                    if (c == '{') { ac = "{"; M.avanzar(); R.set(Token.BEGIN, 0); return; }
                    if (c == '}') { ac = "}"; M.avanzar(); R.set(Token.END,   0); return; }
                    if (c == '&') { ac = "&"; M.avanzar(); R.set(Token.AND,   0); return; }
                    if (c == '|') { ac = "|"; M.avanzar(); R.set(Token.OR,    0); return; }

                    // Operadores de uno o dos chars
                    if (c == ':') { ac = ":"; M.avanzar(); estado = 3; break; }
                    if (c == '<') { ac = "<"; M.avanzar(); estado = 4; break; }
                    if (c == '>') { ac = ">"; M.avanzar(); estado = 5; break; }
                    if (c == '!') { ac = "!"; M.avanzar(); estado = 6; break; }

                    // '/' → DIV o comentario
                    if (c == '/') { M.avanzar(); estado = 7; break; }

                    // '=' → OPREL IGUAL
                    if (c == '=') { ac = "="; M.avanzar(); R.set(Token.OPREL, Token.IGUAL); return; }

                    // Char no reconocido → ERROR
                    ac = "" + c;
                    M.avanzar();
                    R.set(Token.ERROR, 0);
                    return;

                // ── Estado 1: acumulando NUM ──────────────────────────────
                case 1:
                    if (esDigito(c)) {
                        ac += c;
                        M.avanzar();
                    } else {
                        // Retroceso implicito: no avanzamos
                        // Enunciado: <NUM, 30> — atributo SIEMPRE es 30
                        R.set(Token.NUM, 30);
                        return;
                    }
                    break;

                // ── Estado 2: acumulando ID / palabra clave ───────────────
                case 2:
                    if (esLetra(c) || esDigito(c)) {
                        ac += c;   // CASE-SENSITIVE: sin toLowerCase
                        M.avanzar();
                    } else {
                        // Retroceso implícito
                        // Consultar TPC
                        if (TPC.containsKey(ac)) {
                            int nom = TPC.get(ac);
                            int atr = (nom == Token.TIPO) ? tipoAtr(ac) : 0;
                            R.set(nom, atr);
                        } else {
                            // No está en la TPC → es un ID
                            // Atributo del ID = -1 (definido en Token.java)
                            R.set(Token.ID, -1);
                        }
                        return;
                    }
                    break;

                // ── Estado 3: leímos ':' ──────────────────────────────────
                case 3:
                    if (c == '=') {
                        ac = ":=";
                        M.avanzar();
                        R.set(Token.ASSIGN, 0);
                    } else {
                        // Retroceso implícito — solo era ':'
                        R.set(Token.DOSPTOS, 0);
                    }
                    return;

                // ── Estado 4: leímos '<' ──────────────────────────────────
                case 4:
                    if (c == '=') {
                        ac = "<=";
                        M.avanzar();
                        R.set(Token.OPREL, Token.MEI);
                    } else if (c == '>') {
                        ac = "<>";
                        M.avanzar();
                        R.set(Token.OPREL, Token.DIS);
                    } else {
                        // Solo era '<'
                        R.set(Token.OPREL, Token.MEN);
                    }
                    return;

                // ── Estado 5: leímos '>' ──────────────────────────────────
                case 5:
                    if (c == '=') {
                        ac = ">=";
                        M.avanzar();
                        R.set(Token.OPREL, Token.MAI);
                    } else {
                        // Solo era '>'
                        R.set(Token.OPREL, Token.MAY);
                    }
                    return;

                // ── Estado 6: leímos '!' ──────────────────────────────────
                case 6:
                    if (c == '=') {
                        ac = "!=";
                        M.avanzar();
                        R.set(Token.OPREL, Token.DIS);
                    } else {
                        // '!' solo → alias de NOT
                        R.set(Token.NOT, 0);
                    }
                    return;

                // ── Estado 7: leímos '/' ──────────────────────────────────
                case 7:
                    if (c == '/') {
                        // Comentario de línea //
                        M.avanzar();
                        estado = 8;
                    } else if (c == '*') {
                        // Comentario de bloque /*
                        M.avanzar();
                        estado = 9;
                    } else {
                        // Solo era '/' → DIV
                        ac = "/";
                        R.set(Token.DIV, 0);
                        return;
                    }
                    break;

                // ── Estado 8: dentro de comentario de linea // ────────────
                // Sale con EOLN, EOF, o al encontrar '//' (cierre explicito)
                case 8:
                    if (c == Cinta.EOF) {
                        // Llegó el EOF dentro del comentario → FIN
                        R.set(Token.FIN, 0);
                        return;
                    } else if (c == Cinta.EOLN) {
                        // Fin de linea → termina el comentario
                        M.avanzar();
                        pos = M.getPos();
                        estado = 0;
                    } else if (c == '/') {
                        // Posible cierre explicito '//'
                        M.avanzar();
                        estado = 13;   // verificar si el siguiente tambien es '/'
                    } else {
                        M.avanzar();
                    }
                    break;

                // ── Estado 13: dentro del comentario //, leimos '/' ───────
                // Si el siguiente char es '/' → cierre explicito del comentario
                // Si no → seguir ignorando (volver a q8)
                case 13:
                    if (c == '/') {
                        // Encontramos '//' → cierra el comentario
                        M.avanzar();
                        pos = M.getPos();
                        estado = 0;   // volver al estado inicial
                    } else if (c == Cinta.EOF) {
                        R.set(Token.FIN, 0);
                        return;
                    } else if (c == Cinta.EOLN) {
                        // Fin de linea tambien cierra
                        M.avanzar();
                        pos = M.getPos();
                        estado = 0;
                    } else {
                        // No era '//' — seguir dentro del comentario
                        M.avanzar();
                        estado = 8;
                    }
                    break;

                // ── Estado 9: dentro de comentario de bloque /* ... */ ────
                case 9:
                    if (c == Cinta.EOF) {
                        // EOF dentro de comentario sin cerrar → ERROR
                        R.set(Token.ERROR, 0);
                        return;
                    } else if (c == '*') {
                        // Posible cierre */
                        M.avanzar();
                        estado = 10;
                    } else {
                        M.avanzar();
                    }
                    break;

                // ── Estado 10: dentro de /* y leímos '*' ─────────────────
                case 10:
                    if (c == '/') {
                        // Cierre del comentario */
                        M.avanzar();
                        pos = M.getPos();   // actualizar pos
                        estado = 0;         // volvemos a buscar tokens
                    } else if (c == '*') {
                        // Otro '*' — puede seguir siendo cierre
                        M.avanzar();
                        // Seguimos en estado 10
                    } else if (c == Cinta.EOF) {
                        R.set(Token.ERROR, 0);
                        return;
                    } else {
                        // No era '/' — seguimos dentro del comentario
                        M.avanzar();
                        estado = 9;
                    }
                    break;

                // ── Estado 11: acumulando LITERAL con comilla simple ──────
                case 11:
                    if (c == '\'') {
                        // Cierra el literal
                        M.avanzar();
                        R.set(Token.LITERAL, 0);
                        return;
                    } else if (c == Cinta.EOF || c == Cinta.EOLN) {
                        // Literal sin cerrar → ERROR
                        R.set(Token.ERROR, 0);
                        return;
                    } else {
                        ac += c;
                        M.avanzar();
                    }
                    break;

                // ── Estado 12: acumulando LITERAL con comilla doble ───────
                case 12:
                    if (c == '"') {
                        // Cierra el literal
                        M.avanzar();
                        R.set(Token.LITERAL, 0);
                        return;
                    } else if (c == Cinta.EOF || c == Cinta.EOLN) {
                        // Literal sin cerrar → ERROR
                        R.set(Token.ERROR, 0);
                        return;
                    } else {
                        ac += c;
                        M.avanzar();
                    }
                    break;

                default:
                    // Estado desconocido — no debería llegar aquí
                    R.set(Token.ERROR, 0);
                    return;
            }
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private boolean esEspacio(char c) {
        return c == ' ' || c == '\t' || c == Cinta.EOLN;
    }

    private boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean esLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    // ─── Resaltado (para la Interfaz) ─────────────────────────────────────
    public void resaltar() {
        M.onComunicate(pos, lexema());
    }

    // ─── Posicion del token actual (fila y columna) ────────────────────────
    /** Devuelve la fila donde empieza el lexema actual (base 0) */
    public int getFila() {
        return M.getFila();
    }

    /** Devuelve la columna donde empieza el lexema actual (base 0) */
    public int getColumna() {
        return M.getColumna();
    }
}
