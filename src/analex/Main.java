package analex;

/**
 * CHECKLIST COMPLETO DEL ENUNCIADO — Analex INF329 SA 2-2026
 *
 * Verifica CADA fila de la tabla del PDF, en orden exacto de aparicion.
 *
 * NOTA IMPORTANTE SOBRE CASE-SENSITIVITY:
 * El enunciado original dice "NO es case-sensitive".
 * La pizarra de la defensa pidio cambiarlo a case-sensitive.
 * El codigo actual respeta la pizarra (case-sensitive).
 * Por eso las palabras clave se reconocen SOLO en minusculas.
 */
public class Main {

    static int verificacionesCorrectas = 0, verificacionesFallidas = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  CHECKLIST ENUNCIADO - Analex INF329 SA 2-2026");
        System.out.println("=================================================");

        // ═══ PAGINA 1 — Palabras clave ════════════════════════════════════
        imprimirTitulo("PAGINA 1 - Palabras clave del enunciado");

        // "program"              → <PROGRAM,_>
        verificarTokenCompleto("program",    Token.PROGRAM,   0,  "program → <PROGRAM,_>");

        // "var"                  → <VAR,_>
        verificarTokenCompleto("var",        Token.VAR,       0,  "var → <VAR,_>");

        // "procedure" y "void"   → <PROCEDURE,_>
        verificarTokenCompleto("procedure",  Token.PROCEDURE, 0,  "procedure → <PROCEDURE,_>");
        verificarTokenCompleto("void",       Token.PROCEDURE, 0,  "void → <PROCEDURE,_>  (alias Java)");

        // "begin" y "{"          → <BEGIN,_>
        verificarTokenCompleto("begin",      Token.BEGIN,     0,  "begin → <BEGIN,_>");
        verificarTokenCompleto("{",          Token.BEGIN,     0,  "{ → <BEGIN,_>  (alias Java)");

        // "end" y "}"            → <END,_>
        verificarTokenCompleto("end",        Token.END,       0,  "end → <END,_>");
        verificarTokenCompleto("}",          Token.END,       0,  "} → <END,_>  (alias Java)");

        // "if"                   → <IF,_>
        verificarTokenCompleto("if",         Token.IF,        0,  "if → <IF,_>");

        // "then"                 → <THEN,_>
        verificarTokenCompleto("then",       Token.THEN,      0,  "then → <THEN,_>");

        // "else"                 → <ELSE,_>
        verificarTokenCompleto("else",       Token.ELSE,      0,  "else → <ELSE,_>");

        // "for"                  → <FOR,_>
        verificarTokenCompleto("for",        Token.FOR,       0,  "for → <FOR,_>");

        // "to"                   → <TO,_>
        verificarTokenCompleto("to",         Token.TO,        0,  "to → <TO,_>");

        // "do"                   → <DO,_>
        verificarTokenCompleto("do",         Token.DO,        0,  "do → <DO,_>");

        // "downto"               → <DOWNTO,_>
        verificarTokenCompleto("downto",     Token.DOWNTO,    0,  "downto → <DOWNTO,_>");

        // "while"                → <WHILE,_>
        verificarTokenCompleto("while",      Token.WHILE,     0,  "while → <WHILE,_>");

        // "case"                 → <CASE,_>
        verificarTokenCompleto("case",       Token.CASE,      0,  "case → <CASE,_>");

        // "of"                   → <OF,_>
        verificarTokenCompleto("of",         Token.OF,        0,  "of → <OF,_>");

        // "repeat"               → <REPEAT,_>
        verificarTokenCompleto("repeat",     Token.REPEAT,    0,  "repeat → <REPEAT,_>");

        // "until"                → <UNTIL,_>
        verificarTokenCompleto("until",      Token.UNTIL,     0,  "until → <UNTIL,_>");

        // "read"                 → <READ,_>
        verificarTokenCompleto("read",       Token.READ,      0,  "read → <READ,_>");

        // "readln"               → <READLN,_>
        verificarTokenCompleto("readln",     Token.READLN,    0,  "readln → <READLN,_>");

        // "print"                → <PRINT,_>
        verificarTokenCompleto("print",      Token.PRINT,     0,  "print → <PRINT,_>");

        // "println"              → <PRINTLN,_>
        verificarTokenCompleto("println",    Token.PRINTLN,   0,  "println → <PRINTLN,_>");

        // ═══ PAGINA 2 — Operadores y tokens especiales ════════════════════
        imprimirTitulo("PAGINA 2 - Operadores");

        // ","                    → <COMA,_>
        verificarTokenCompleto(",",          Token.COMA,      0,  ", → <COMA,_>");

        // ":"                    → <DOSPTOS,_>
        verificarTokenCompleto(":",          Token.DOSPTOS,   0,  ": → <DOSPTOS,_>");

        // ";"                    → <PTOCOMA,_>
        verificarTokenCompleto(";",          Token.PTOCOMA,   0,  "; → <PTOCOMA,_>");

        // "("                    → <PA,_>
        verificarTokenCompleto("(",          Token.PA,        0,  "( → <PA,_>");

        // ")"                    → <PC,_>
        verificarTokenCompleto(")",          Token.PC,        0,  ") → <PC,_>");

        // ":="                   → <ASSIGN,_>
        verificarTokenCompleto(":=",         Token.ASSIGN,    0,  ":= → <ASSIGN,_>");

        // "not" y "!"            → <NOT,_>
        verificarTokenCompleto("not",        Token.NOT,       0,  "not → <NOT,_>");
        verificarTokenCompleto("!",          Token.NOT,       0,  "! → <NOT,_>  (alias Java)");

        // "and" y "&"            → <AND,_>
        verificarTokenCompleto("and",        Token.AND,       0,  "and → <AND,_>");
        verificarTokenCompleto("&",          Token.AND,       0,  "& → <AND,_>  (alias Java)");

        // "or" y "|"             → <OR,_>
        verificarTokenCompleto("or",         Token.OR,        0,  "or → <OR,_>");
        verificarTokenCompleto("|",          Token.OR,        0,  "| → <OR,_>  (alias Java)");

        // "+"                    → <MAS,_>
        verificarTokenCompleto("+",          Token.MAS,       0,  "+ → <MAS,_>");

        // "-"                    → <MENOS,_>
        verificarTokenCompleto("-",          Token.MENOS,     0,  "- → <MENOS,_>");

        // "*"                    → <POR,_>
        verificarTokenCompleto("*",          Token.POR,       0,  "* → <POR,_>");

        // "mod" y "%"            → <MOD,_>
        verificarTokenCompleto("mod",        Token.MOD,       0,  "mod → <MOD,_>");
        verificarTokenCompleto("%",          Token.MOD,       0,  "% → <MOD,_>  (alias Java)");

        // "div" y "/"            → <DIV,_>
        verificarTokenCompleto("div",        Token.DIV,       0,  "div → <DIV,_>");
        verificarTokenCompleto("/",          Token.DIV,       0,  "/ → <DIV,_>  (alias Java)");

        // ═══ NUM — atributo FIJO = 30 ══════════════════════════════════════
        imprimirTitulo("NUM - atributo FIJO = 30  (enunciado: <NUM,30>)");

        verificarTokenCompleto("0",          Token.NUM,       30, "0 → <NUM,30>");
        verificarTokenCompleto("42",         Token.NUM,       30, "42 → <NUM,30>");
        verificarTokenCompleto("9999",       Token.NUM,       30, "9999 → <NUM,30>");
        // Verificar que NO devuelve el valor del numero
        verificarAtributoDistinto("7",    Token.NUM,       7,  "7 → atributo NO es 7, es 30");

        // ═══ ID — atributo FIJO = -1 ═══════════════════════════════════════
        imprimirTitulo("ID - atributo FIJO = -1  (enunciado: <ID,-1>)");

        verificarTokenCompleto("x",          Token.ID,        -1, "x → <ID,-1>");
        verificarTokenCompleto("suma",       Token.ID,        -1, "suma → <ID,-1>");
        verificarTokenCompleto("var2",       Token.ID,        -1, "var2 → <ID,-1>");
        // boolean e integer NO son ID
        verificarNombreDistinto("boolean", Token.ID,         "boolean NO es ID (es TIPO)");
        verificarNombreDistinto("integer", Token.ID,         "integer NO es ID (es TIPO)");

        // ═══ LITERAL — atributo FIJO = 0 ═══════════════════════════════════
        imprimirTitulo("LITERAL - chars entre comillas, sin EOLN ni EOF  (atributo=0)");

        verificarTokenCompleto("\"hola\"",   Token.LITERAL,   0,  "\"hola\" → <LITERAL,0>  comilla doble");
        verificarTokenCompleto("\"\"",       Token.LITERAL,   0,  "\"\" → <LITERAL,0>  vacio comilla doble");
        verificarTokenCompleto("'pascal'",   Token.LITERAL,   0,  "'pascal' → <LITERAL,0>  comilla simple");
        // Literal sin cerrar → ERROR (sin EOLN ni EOF adentro)
        verificarSoloNombre("\"abc",   Token.ERROR,         "literal sin cerrar → ERROR");

        // ═══ OPREL — 6 variantes ═══════════════════════════════════════════
        imprimirTitulo("OPREL - 6 variantes del enunciado");

        verificarTokenCompleto("=",          Token.OPREL, Token.IGUAL, "= → <OPREL,IGUAL>");
        verificarTokenCompleto("<",          Token.OPREL, Token.MEN,   "< → <OPREL,MEN>");
        verificarTokenCompleto(">",          Token.OPREL, Token.MAY,   "> → <OPREL,MAY>");
        verificarTokenCompleto("<=",         Token.OPREL, Token.MEI,   "<= → <OPREL,MEI>");
        verificarTokenCompleto(">=",         Token.OPREL, Token.MAI,   ">= → <OPREL,MAI>");
        verificarTokenCompleto("<>",         Token.OPREL, Token.DIS,   "<> → <OPREL,DIS>  (alias Pascal)");
        verificarTokenCompleto("!=",         Token.OPREL, Token.DIS,   "!= → <OPREL,DIS>  (alias Java)");

        // ═══ TIPO — boolean e integer ═══════════════════════════════════════
        imprimirTitulo("TIPO - boolean e integer");

        verificarTokenCompleto("boolean",    Token.TIPO, Token.BOOLEAN, "boolean → <TIPO,BOOLEAN>");
        verificarTokenCompleto("integer",    Token.TIPO, Token.INTEGER, "integer → <TIPO,INTEGER>");

        // ═══ TPC — OBLIGADO (tabla private en Analex) ══════════════════════
        imprimirTitulo("TPC - private en Analex (OBLIGADO por el enunciado)");

        // Verificar las 27 palabras clave de la TPC
        String[] palabrasClave = {
            "program","var","procedure","void","begin","end",
            "if","then","else","for","to","do","downto","while",
            "case","of","repeat","until","read","readln","print","println",
            "not","and","or","mod","div","boolean","integer"
        };
        int[] nombresDeTokenEsperados = {
            Token.PROGRAM, Token.VAR, Token.PROCEDURE, Token.PROCEDURE, Token.BEGIN, Token.END,
            Token.IF, Token.THEN, Token.ELSE, Token.FOR, Token.TO, Token.DO, Token.DOWNTO, Token.WHILE,
            Token.CASE, Token.OF, Token.REPEAT, Token.UNTIL, Token.READ, Token.READLN, Token.PRINT, Token.PRINTLN,
            Token.NOT, Token.AND, Token.OR, Token.MOD, Token.DIV, Token.TIPO, Token.TIPO
        };
        boolean tpcCorrecta = true;
        for (int i = 0; i < palabrasClave.length; i++) {
            Cinta cintaDePalabra = new Cinta(palabrasClave[i]);
            Analex analizadorDePalabra = new Analex(cintaDePalabra);
            if (analizadorDePalabra.Preanalisis().getNom() != nombresDeTokenEsperados[i]) {
                System.out.printf("  [FAIL] TPC: '%s' → esperado nom=%d, obtenido nom=%d%n",
                        palabrasClave[i], nombresDeTokenEsperados[i], analizadorDePalabra.Preanalisis().getNom());
                tpcCorrecta = false; verificacionesFallidas++;
            }
        }
        if (tpcCorrecta) {
            System.out.printf("  [OK]   TPC: todas las %d entradas reconocidas%n", palabrasClave.length);
            verificacionesCorrectas++;
        }

        // ═══ COMENTARIOS — OBLIGADO ═════════════════════════════════════════
        imprimirTitulo("COMENTARIOS - los 2 tipos del enunciado");

        // Tipo 1: comentario de linea //
        verificarSecuenciaDeNombres("x // comentario de linea\ny",
                new int[]{Token.ID, Token.ID, Token.FIN},
                "// comentario de linea ignorado (cierra con EOLN)");
        verificarSecuenciaDeNombres("x // hasta fin de archivo",
                new int[]{Token.ID, Token.FIN},
                "// comentario ignorado (cierra con EOF)");

        // Tipo 2: comentario multilinea /* */
        verificarSecuenciaDeNombres("x /* bloque */ y",
                new int[]{Token.ID, Token.ID, Token.FIN},
                "/* */ comentario de bloque ignorado");
        verificarSecuenciaDeNombres("x /* multi\nlinea\nbloque */ y",
                new int[]{Token.ID, Token.ID, Token.FIN},
                "/* */ comentario multilinea ignorado");

        // ═══ PROGRAMA COMPLETO — integracion final ══════════════════════════
        imprimirTitulo("INTEGRACION - programa Pascal+Java completo");

        String programaFactorial =
            "program factorial;\n" +
            "var n : integer;\n" +
            "begin\n" +
            "  read(n); /* leer entrada */\n" +
            "  if (n > 0) then begin\n" +
            "    println(n); // mostrar resultado\n" +
            "  end;\n" +
            "end";

        verificarSecuenciaDeNombres(programaFactorial,
                new int[]{
                    Token.PROGRAM, Token.ID, Token.PTOCOMA,
                    Token.VAR, Token.ID, Token.DOSPTOS, Token.TIPO, Token.PTOCOMA,
                    Token.BEGIN,
                    Token.READ, Token.PA, Token.ID, Token.PC, Token.PTOCOMA,
                    Token.IF, Token.PA, Token.ID, Token.OPREL, Token.NUM, Token.PC, Token.THEN, Token.BEGIN,
                    Token.PRINTLN, Token.PA, Token.ID, Token.PC, Token.PTOCOMA,
                    Token.END, Token.PTOCOMA,
                    Token.END, Token.FIN
                },
                "Programa factorial completo (31 tokens)");

        // ═══ NOTA SOBRE CASE-SENSITIVITY ═══════════════════════════════════
        imprimirTitulo("NOTA: Case-sensitivity (pizarra vs enunciado)");

        System.out.println("  [INFO] Enunciado original dice: NO es case-sensitive");
        System.out.println("  [INFO] Pizarra de defensa pidio: cambiar a case-sensitive");
        System.out.println("  [INFO] Interruptor Analex.CASE_SENSITIVE = " + Analex.CASE_SENSITIVE);
        verificarSoloNombre("while",  Token.WHILE, "\"while\" (minusc.) → WHILE");
        if (Analex.CASE_SENSITIVE) {
            System.out.println("  [INFO] Efecto: palabras clave solo en minusculas");
            verificarSoloNombre("WHILE",  Token.ID,    "\"WHILE\" (mayusc.) → ID  (case-sensitive)");
            verificarSoloNombre("While",  Token.ID,    "\"While\" (mezcl.)  → ID  (case-sensitive)");
        } else {
            System.out.println("  [INFO] Efecto: palabras clave en cualquier combinacion (PDF)");
            verificarSoloNombre("WHILE",  Token.WHILE, "\"WHILE\" (mayusc.) → WHILE  (NO case-sensitive)");
            verificarSoloNombre("While",  Token.WHILE, "\"While\" (mezcl.)  → WHILE  (NO case-sensitive)");
            verificarSoloNombre("INTEGER", Token.TIPO, "\"INTEGER\" → TIPO  (NO case-sensitive)");
        }

        // ═══ EXTRAS DE DEFENSA ══════════════════════════════════════════════
        imprimirTitulo("EXTRAS agregados en defensa (pizarra)");

        System.out.println("  [INFO] EXTRAS_DEFENSA=" + Analex.EXTRAS_DEFENSA
                + "  COMENTARIO_CIERRA_CON_DOBLE_BARRA=" + Analex.COMENTARIO_CIERRA_CON_DOBLE_BARRA
                + "  ID_ACEPTA_GUION_BAJO=" + Analex.ID_ACEPTA_GUION_BAJO);
        if (Analex.EXTRAS_DEFENSA) {
            verificarTokenCompleto("por",   Token.POR,  0, "por → <POR,_>  (pedido en defensa)");
            verificarTokenCompleto("sumi",  Token.SUMA, 0, "sumi → <SUMA,_>  (pedido en defensa)");
        } else {
            verificarSoloNombre("por",  Token.ID, "por → ID  (no existe en el PDF)");
            verificarSoloNombre("sumi", Token.ID, "sumi → ID  (no existe en el PDF)");
        }
        if (Analex.COMENTARIO_CIERRA_CON_DOBLE_BARRA) {
            verificarSecuenciaDeNombres("a := 1 // asignacion // b := 2",
                    new int[]{Token.ID, Token.ASSIGN, Token.NUM,
                              Token.ID, Token.ASSIGN, Token.NUM, Token.FIN},
                    "// texto // cierre explicito (pedido en defensa)");
        } else {
            verificarSecuenciaDeNombres("a := 1 // asignacion // b := 2",
                    new int[]{Token.ID, Token.ASSIGN, Token.NUM, Token.FIN},
                    "// comenta hasta el fin de linea (PDF)");
        }
        if (Analex.ID_ACEPTA_GUION_BAJO)
            verificarSoloNombre("_x", Token.ID,    "_x → ID  (acepta guion bajo)");
        else
            verificarSoloNombre("_x", Token.ERROR, "_x → ERROR  (ID solo letras y digitos, PDF)");

        // ═══ Token.toString() — formato del enunciado ═══════════════════════
        imprimirTitulo("Token.toString() — formato <NOMBRE,atributo> del enunciado");
        verificarTextoDelToken(new Token(Token.NUM, 30),               "<NUM,30>");
        verificarTextoDelToken(new Token(Token.ID, -1),                "<ID,-1>");
        verificarTextoDelToken(new Token(Token.LITERAL, 0),            "<LITERAL,0>");
        verificarTextoDelToken(new Token(Token.OPREL, Token.IGUAL),    "<OPREL,IGUAL>");
        verificarTextoDelToken(new Token(Token.OPREL, Token.DIS),      "<OPREL,DIS>");
        verificarTextoDelToken(new Token(Token.TIPO, Token.BOOLEAN),   "<TIPO,BOOLEAN>");
        verificarTextoDelToken(new Token(Token.TIPO, Token.INTEGER),   "<TIPO,INTEGER>");
        verificarTextoDelToken(new Token(Token.PROGRAM),               "<PROGRAM,_>");
        verificarTextoDelToken(new Token(Token.FIN),                   "<FIN,_>");

        // ─── RESUMEN FINAL ──────────────────────────────────────────────────
        System.out.println("\n=================================================");
        System.out.printf("  RESULTADO FINAL: %d OK  |  %d FAIL%n", verificacionesCorrectas, verificacionesFallidas);
        if (verificacionesFallidas == 0) {
            System.out.println("  *** CUMPLE TODO EL ENUNCIADO + CAMBIOS DEFENSA ***");
        } else {
            System.out.println("  *** HAY " + verificacionesFallidas + " ITEM(S) QUE NO CUMPLEN ***");
        }
        System.out.println("=================================================");
    }

    // ─── Helpers de verificacion ─────────────────────────────────────────────
    // Cada helper analiza un programa fuente chiquito, compara lo obtenido con lo esperado
    // e imprime [OK] o [FAIL] (y suma al contador correspondiente).

    /** Imprime el titulo de una seccion del checklist. */
    static void imprimirTitulo(String titulo) {
        System.out.println("\n--- " + titulo + " ---");
    }

    /** Analiza un programa fuente y devuelve un analizador con el primer token ya calculado. */
    private static Analex analizarProgramaFuente(String programaFuente) {
        return new Analex(new Cinta(programaFuente));
    }

    /** Verifica nombre Y atributo exactos del primer token. */
    static void verificarTokenCompleto(String programaFuente, int nombreEsperado, int atributoEsperado, String descripcion) {
        Token primerToken = analizarProgramaFuente(programaFuente).Preanalisis();
        if (primerToken.getNom() == nombreEsperado && primerToken.getAtr() == atributoEsperado) {
            System.out.printf("  [OK]   %-18s  %s%n", "\"" + programaFuente + "\"", descripcion);
            verificacionesCorrectas++;
        } else {
            System.out.printf("  [FAIL] %-18s  %s%n", "\"" + programaFuente + "\"", descripcion);
            System.out.printf("         Esperado nom=%d atr=%d | Obtenido nom=%d atr=%d%n",
                    nombreEsperado, atributoEsperado, primerToken.getNom(), primerToken.getAtr());
            verificacionesFallidas++;
        }
    }

    /** Verifica solo el nombre del primer token. */
    static void verificarSoloNombre(String programaFuente, int nombreEsperado, String descripcion) {
        int nombreObtenido = analizarProgramaFuente(programaFuente).Preanalisis().getNom();
        if (nombreObtenido == nombreEsperado) {
            System.out.printf("  [OK]   %-18s  %s%n", "\"" + programaFuente + "\"", descripcion);
            verificacionesCorrectas++;
        } else {
            System.out.printf("  [FAIL] %-18s  %s | esp=%d got=%d%n",
                    "\"" + programaFuente + "\"", descripcion, nombreEsperado, nombreObtenido);
            verificacionesFallidas++;
        }
    }

    /** Verifica que el nombre del primer token NO sea nombreProhibido. */
    static void verificarNombreDistinto(String programaFuente, int nombreProhibido, String descripcion) {
        int nombreObtenido = analizarProgramaFuente(programaFuente).Preanalisis().getNom();
        if (nombreObtenido != nombreProhibido) {
            System.out.printf("  [OK]   %-18s  %s%n", "\"" + programaFuente + "\"", descripcion);
            verificacionesCorrectas++;
        } else {
            System.out.printf("  [FAIL] %-18s  %s (obtuvo nom=%d cuando no debia)%n",
                    "\"" + programaFuente + "\"", descripcion, nombreObtenido);
            verificacionesFallidas++;
        }
    }

    /** Verifica que el nombre sea el esperado y que el atributo del primer token NO sea atributoProhibido. */
    static void verificarAtributoDistinto(String programaFuente, int nombreEsperado, int atributoProhibido, String descripcion) {
        Token primerToken = analizarProgramaFuente(programaFuente).Preanalisis();
        boolean nombreCorrecto   = (primerToken.getNom() == nombreEsperado);
        boolean atributoCorrecto = (primerToken.getAtr() != atributoProhibido);
        if (nombreCorrecto && atributoCorrecto) {
            System.out.printf("  [OK]   %-18s  %s (atr=%d)%n",
                    "\"" + programaFuente + "\"", descripcion, primerToken.getAtr());
            verificacionesCorrectas++;
        } else {
            System.out.printf("  [FAIL] %-18s  %s | nom=%d atr=%d%n",
                    "\"" + programaFuente + "\"", descripcion, primerToken.getNom(), primerToken.getAtr());
            verificacionesFallidas++;
        }
    }

    /** Verifica el toString() de un token (formato del enunciado). */
    static void verificarTextoDelToken(Token token, String textoEsperado) {
        String textoObtenido = token.toString();
        if (textoObtenido.equals(textoEsperado)) {
            System.out.printf("  [OK]   %s%n", textoObtenido);
            verificacionesCorrectas++;
        } else {
            System.out.printf("  [FAIL] toString esperado %s | obtenido %s%n", textoEsperado, textoObtenido);
            verificacionesFallidas++;
        }
    }

    /** Verifica una secuencia de nombres de tokens (uno detras de otro, avanzando). */
    static void verificarSecuenciaDeNombres(String programaFuente, int[] nombresEsperados, String descripcion) {
        Analex analizador = analizarProgramaFuente(programaFuente);
        for (int posicion = 0; posicion < nombresEsperados.length; posicion++) {
            int nombreObtenido = analizador.Preanalisis().getNom();
            if (nombreObtenido != nombresEsperados[posicion]) {
                System.out.printf("  [FAIL] %s%n", descripcion);
                System.out.printf("         Pos %d: esperado=%d obtenido=%d lexema='%s'%n",
                        posicion, nombresEsperados[posicion], nombreObtenido, analizador.lexema());
                verificacionesFallidas++;
                return;
            }
            if (nombreObtenido == Token.FIN || nombreObtenido == Token.ERROR) break;
            analizador.avanzar();
        }
        System.out.printf("  [OK]   %s%n", descripcion);
        verificacionesCorrectas++;
    }
}
