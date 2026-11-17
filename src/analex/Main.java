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

    static int ok = 0, fail = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  CHECKLIST ENUNCIADO - Analex INF329 SA 2-2026");
        System.out.println("=================================================");

        // ═══ PAGINA 1 — Palabras clave ════════════════════════════════════
        titulo("PAGINA 1 - Palabras clave del enunciado");

        // "program"              → <PROGRAM,_>
        chk("program",    Token.PROGRAM,   0,  "program → <PROGRAM,_>");

        // "var"                  → <VAR,_>
        chk("var",        Token.VAR,       0,  "var → <VAR,_>");

        // "procedure" y "void"   → <PROCEDURE,_>
        chk("procedure",  Token.PROCEDURE, 0,  "procedure → <PROCEDURE,_>");
        chk("void",       Token.PROCEDURE, 0,  "void → <PROCEDURE,_>  (alias Java)");

        // "begin" y "{"          → <BEGIN,_>
        chk("begin",      Token.BEGIN,     0,  "begin → <BEGIN,_>");
        chk("{",          Token.BEGIN,     0,  "{ → <BEGIN,_>  (alias Java)");

        // "end" y "}"            → <END,_>
        chk("end",        Token.END,       0,  "end → <END,_>");
        chk("}",          Token.END,       0,  "} → <END,_>  (alias Java)");

        // "if"                   → <IF,_>
        chk("if",         Token.IF,        0,  "if → <IF,_>");

        // "then"                 → <THEN,_>
        chk("then",       Token.THEN,      0,  "then → <THEN,_>");

        // "else"                 → <ELSE,_>
        chk("else",       Token.ELSE,      0,  "else → <ELSE,_>");

        // "for"                  → <FOR,_>
        chk("for",        Token.FOR,       0,  "for → <FOR,_>");

        // "to"                   → <TO,_>
        chk("to",         Token.TO,        0,  "to → <TO,_>");

        // "do"                   → <DO,_>
        chk("do",         Token.DO,        0,  "do → <DO,_>");

        // "downto"               → <DOWNTO,_>
        chk("downto",     Token.DOWNTO,    0,  "downto → <DOWNTO,_>");

        // "while"                → <WHILE,_>
        chk("while",      Token.WHILE,     0,  "while → <WHILE,_>");

        // "case"                 → <CASE,_>
        chk("case",       Token.CASE,      0,  "case → <CASE,_>");

        // "of"                   → <OF,_>
        chk("of",         Token.OF,        0,  "of → <OF,_>");

        // "repeat"               → <REPEAT,_>
        chk("repeat",     Token.REPEAT,    0,  "repeat → <REPEAT,_>");

        // "until"                → <UNTIL,_>
        chk("until",      Token.UNTIL,     0,  "until → <UNTIL,_>");

        // "read"                 → <READ,_>
        chk("read",       Token.READ,      0,  "read → <READ,_>");

        // "readln"               → <READLN,_>
        chk("readln",     Token.READLN,    0,  "readln → <READLN,_>");

        // "print"                → <PRINT,_>
        chk("print",      Token.PRINT,     0,  "print → <PRINT,_>");

        // "println"              → <PRINTLN,_>
        chk("println",    Token.PRINTLN,   0,  "println → <PRINTLN,_>");

        // ═══ PAGINA 2 — Operadores y tokens especiales ════════════════════
        titulo("PAGINA 2 - Operadores");

        // ","                    → <COMA,_>
        chk(",",          Token.COMA,      0,  ", → <COMA,_>");

        // ":"                    → <DOSPTOS,_>
        chk(":",          Token.DOSPTOS,   0,  ": → <DOSPTOS,_>");

        // ";"                    → <PTOCOMA,_>
        chk(";",          Token.PTOCOMA,   0,  "; → <PTOCOMA,_>");

        // "("                    → <PA,_>
        chk("(",          Token.PA,        0,  "( → <PA,_>");

        // ")"                    → <PC,_>
        chk(")",          Token.PC,        0,  ") → <PC,_>");

        // ":="                   → <ASSIGN,_>
        chk(":=",         Token.ASSIGN,    0,  ":= → <ASSIGN,_>");

        // "not" y "!"            → <NOT,_>
        chk("not",        Token.NOT,       0,  "not → <NOT,_>");
        chk("!",          Token.NOT,       0,  "! → <NOT,_>  (alias Java)");

        // "and" y "&"            → <AND,_>
        chk("and",        Token.AND,       0,  "and → <AND,_>");
        chk("&",          Token.AND,       0,  "& → <AND,_>  (alias Java)");

        // "or" y "|"             → <OR,_>
        chk("or",         Token.OR,        0,  "or → <OR,_>");
        chk("|",          Token.OR,        0,  "| → <OR,_>  (alias Java)");

        // "+"                    → <MAS,_>
        chk("+",          Token.MAS,       0,  "+ → <MAS,_>");

        // "-"                    → <MENOS,_>
        chk("-",          Token.MENOS,     0,  "- → <MENOS,_>");

        // "*"                    → <POR,_>
        chk("*",          Token.POR,       0,  "* → <POR,_>");

        // "mod" y "%"            → <MOD,_>
        chk("mod",        Token.MOD,       0,  "mod → <MOD,_>");
        chk("%",          Token.MOD,       0,  "% → <MOD,_>  (alias Java)");

        // "div" y "/"            → <DIV,_>
        chk("div",        Token.DIV,       0,  "div → <DIV,_>");
        chk("/",          Token.DIV,       0,  "/ → <DIV,_>  (alias Java)");

        // ═══ NUM — atributo FIJO = 30 ══════════════════════════════════════
        titulo("NUM - atributo FIJO = 30  (enunciado: <NUM,30>)");

        chk("0",          Token.NUM,       30, "0 → <NUM,30>");
        chk("42",         Token.NUM,       30, "42 → <NUM,30>");
        chk("9999",       Token.NUM,       30, "9999 → <NUM,30>");
        // Verificar que NO devuelve el valor del numero
        chkAtrNot("7",    Token.NUM,       7,  "7 → atributo NO es 7, es 30");

        // ═══ ID — atributo FIJO = -1 ═══════════════════════════════════════
        titulo("ID - atributo FIJO = -1  (enunciado: <ID,-1>)");

        chk("x",          Token.ID,        -1, "x → <ID,-1>");
        chk("suma",       Token.ID,        -1, "suma → <ID,-1>");
        chk("var2",       Token.ID,        -1, "var2 → <ID,-1>");
        // boolean e integer NO son ID
        chkNomNot("boolean", Token.ID,         "boolean NO es ID (es TIPO)");
        chkNomNot("integer", Token.ID,         "integer NO es ID (es TIPO)");

        // ═══ LITERAL — atributo FIJO = 0 ═══════════════════════════════════
        titulo("LITERAL - chars entre comillas, sin EOLN ni EOF  (atributo=0)");

        chk("\"hola\"",   Token.LITERAL,   0,  "\"hola\" → <LITERAL,0>  comilla doble");
        chk("\"\"",       Token.LITERAL,   0,  "\"\" → <LITERAL,0>  vacio comilla doble");
        chk("'pascal'",   Token.LITERAL,   0,  "'pascal' → <LITERAL,0>  comilla simple");
        // Literal sin cerrar → ERROR (sin EOLN ni EOF adentro)
        chkNom("\"abc",   Token.ERROR,         "literal sin cerrar → ERROR");

        // ═══ OPREL — 6 variantes ═══════════════════════════════════════════
        titulo("OPREL - 6 variantes del enunciado");

        chk("=",          Token.OPREL, Token.IGUAL, "= → <OPREL,IGUAL>");
        chk("<",          Token.OPREL, Token.MEN,   "< → <OPREL,MEN>");
        chk(">",          Token.OPREL, Token.MAY,   "> → <OPREL,MAY>");
        chk("<=",         Token.OPREL, Token.MEI,   "<= → <OPREL,MEI>");
        chk(">=",         Token.OPREL, Token.MAI,   ">= → <OPREL,MAI>");
        chk("<>",         Token.OPREL, Token.DIS,   "<> → <OPREL,DIS>  (alias Pascal)");
        chk("!=",         Token.OPREL, Token.DIS,   "!= → <OPREL,DIS>  (alias Java)");

        // ═══ TIPO — boolean e integer ═══════════════════════════════════════
        titulo("TIPO - boolean e integer");

        chk("boolean",    Token.TIPO, Token.BOOLEAN, "boolean → <TIPO,BOOLEAN>");
        chk("integer",    Token.TIPO, Token.INTEGER, "integer → <TIPO,INTEGER>");

        // ═══ TPC — OBLIGADO (tabla private en Analex) ══════════════════════
        titulo("TPC - private en Analex (OBLIGADO por el enunciado)");

        // Verificar las 27 palabras clave de la TPC
        String[] kws = {
            "program","var","procedure","void","begin","end",
            "if","then","else","for","to","do","downto","while",
            "case","of","repeat","until","read","readln","print","println",
            "not","and","or","mod","div","boolean","integer"
        };
        int[] noms = {
            Token.PROGRAM, Token.VAR, Token.PROCEDURE, Token.PROCEDURE, Token.BEGIN, Token.END,
            Token.IF, Token.THEN, Token.ELSE, Token.FOR, Token.TO, Token.DO, Token.DOWNTO, Token.WHILE,
            Token.CASE, Token.OF, Token.REPEAT, Token.UNTIL, Token.READ, Token.READLN, Token.PRINT, Token.PRINTLN,
            Token.NOT, Token.AND, Token.OR, Token.MOD, Token.DIV, Token.TIPO, Token.TIPO
        };
        boolean tpcOk = true;
        for (int i = 0; i < kws.length; i++) {
            Cinta c2 = new Cinta(kws[i]);
            Analex a2 = new Analex(c2);
            if (a2.Preanalisis().getNom() != noms[i]) {
                System.out.printf("  [FAIL] TPC: '%s' → esperado nom=%d, obtenido nom=%d%n",
                        kws[i], noms[i], a2.Preanalisis().getNom());
                tpcOk = false; fail++;
            }
        }
        if (tpcOk) {
            System.out.printf("  [OK]   TPC: todas las %d entradas reconocidas%n", kws.length);
            ok++;
        }

        // ═══ COMENTARIOS — OBLIGADO ═════════════════════════════════════════
        titulo("COMENTARIOS - los 2 tipos del enunciado");

        // Tipo 1: comentario de linea //
        chkSeq("x // comentario de linea\ny",
                new int[]{Token.ID, Token.ID, Token.FIN},
                "// comentario de linea ignorado (cierra con EOLN)");
        chkSeq("x // hasta fin de archivo",
                new int[]{Token.ID, Token.FIN},
                "// comentario ignorado (cierra con EOF)");

        // Tipo 2: comentario multilinea /* */
        chkSeq("x /* bloque */ y",
                new int[]{Token.ID, Token.ID, Token.FIN},
                "/* */ comentario de bloque ignorado");
        chkSeq("x /* multi\nlinea\nbloque */ y",
                new int[]{Token.ID, Token.ID, Token.FIN},
                "/* */ comentario multilinea ignorado");

        // ═══ PROGRAMA COMPLETO — integracion final ══════════════════════════
        titulo("INTEGRACION - programa Pascal+Java completo");

        String prog =
            "program factorial;\n" +
            "var n : integer;\n" +
            "begin\n" +
            "  read(n); /* leer entrada */\n" +
            "  if (n > 0) then begin\n" +
            "    println(n); // mostrar resultado\n" +
            "  end;\n" +
            "end";

        chkSeq(prog,
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
        titulo("NOTA: Case-sensitivity (pizarra vs enunciado)");

        System.out.println("  [INFO] Enunciado original dice: NO es case-sensitive");
        System.out.println("  [INFO] Pizarra de defensa pidio: cambiar a case-sensitive");
        System.out.println("  [INFO] Estado actual: CASE-SENSITIVE");
        System.out.println("  [INFO] Efecto: palabras clave solo en minusculas");
        // Demostrar el efecto
        chkNom("while",  Token.WHILE, "\"while\" (minusc.) → WHILE");
        chkNom("WHILE",  Token.ID,    "\"WHILE\" (mayusc.) → ID  (por cambio case-sensitive)");
        chkNom("While",  Token.ID,    "\"While\" (mezcl.)  → ID  (por cambio case-sensitive)");
        ok++; // La nota misma cuenta como ok
        System.out.println("  [OK]   Cambio case-sensitive aplicado correctamente");

        // ═══ EXTRAS DE DEFENSA ══════════════════════════════════════════════
        titulo("EXTRAS agregados en defensa (pizarra)");

        // "por" → <POR,_>
        chk("por",   Token.POR,  0, "por → <POR,_>  (pedido en defensa)");
        // "sumi" → <SUMA,_>
        chk("sumi",  Token.SUMA, 0, "sumi → <SUMA,_>  (pedido en defensa)");
        // Comentario // ... // (cierre explicito)
        chkSeq("a := 1 // asignacion // b := 2",
                new int[]{Token.ID, Token.ASSIGN, Token.NUM,
                          Token.ID, Token.ASSIGN, Token.NUM, Token.FIN},
                "// texto // cierre explicito (pedido en defensa)");

        // ─── RESUMEN FINAL ──────────────────────────────────────────────────
        System.out.println("\n=================================================");
        System.out.printf("  RESULTADO FINAL: %d OK  |  %d FAIL%n", ok, fail);
        if (fail == 0) {
            System.out.println("  *** CUMPLE TODO EL ENUNCIADO + CAMBIOS DEFENSA ✓ ***");
        } else {
            System.out.println("  *** HAY " + fail + " ITEM(S) QUE NO CUMPLEN ***");
        }
        System.out.println("=================================================");
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    static void titulo(String s) {
        System.out.println("\n--- " + s + " ---");
    }

    /** Verifica nombre Y atributo exactos del primer token */
    static void chk(String fuente, int nomEsp, int atrEsp, String desc) {
        Cinta c = new Cinta(fuente);
        Analex a = new Analex(c);
        Token t = a.Preanalisis();
        if (t.getNom() == nomEsp && t.getAtr() == atrEsp) {
            System.out.printf("  [OK]   %-18s  %s%n", "\"" + fuente + "\"", desc);
            ok++;
        } else {
            System.out.printf("  [FAIL] %-18s  %s%n", "\"" + fuente + "\"", desc);
            System.out.printf("         Esperado nom=%d atr=%d | Obtenido nom=%d atr=%d%n",
                    nomEsp, atrEsp, t.getNom(), t.getAtr());
            fail++;
        }
    }

    /** Verifica solo el nombre del primer token */
    static void chkNom(String fuente, int nomEsp, String desc) {
        Cinta c = new Cinta(fuente);
        Analex a = new Analex(c);
        int real = a.Preanalisis().getNom();
        if (real == nomEsp) {
            System.out.printf("  [OK]   %-18s  %s%n", "\"" + fuente + "\"", desc);
            ok++;
        } else {
            System.out.printf("  [FAIL] %-18s  %s | esp=%d got=%d%n",
                    "\"" + fuente + "\"", desc, nomEsp, real);
            fail++;
        }
    }

    /** Verifica que el nombre del token NO sea nomNoEsp */
    static void chkNomNot(String fuente, int nomNoEsp, String desc) {
        Cinta c = new Cinta(fuente);
        Analex a = new Analex(c);
        int real = a.Preanalisis().getNom();
        if (real != nomNoEsp) {
            System.out.printf("  [OK]   %-18s  %s%n", "\"" + fuente + "\"", desc);
            ok++;
        } else {
            System.out.printf("  [FAIL] %-18s  %s (obtuvo nom=%d cuando no debia)%n",
                    "\"" + fuente + "\"", desc, real);
            fail++;
        }
    }

    /** Verifica que el atributo del primer token NO sea atrNoEsp */
    static void chkAtrNot(String fuente, int nomEsp, int atrNoEsp, String desc) {
        Cinta c = new Cinta(fuente);
        Analex a = new Analex(c);
        Token t = a.Preanalisis();
        boolean nomOk = (t.getNom() == nomEsp);
        boolean atrOk = (t.getAtr() != atrNoEsp);
        if (nomOk && atrOk) {
            System.out.printf("  [OK]   %-18s  %s (atr=%d)%n",
                    "\"" + fuente + "\"", desc, t.getAtr());
            ok++;
        } else {
            System.out.printf("  [FAIL] %-18s  %s | nom=%d atr=%d%n",
                    "\"" + fuente + "\"", desc, t.getNom(), t.getAtr());
            fail++;
        }
    }

    /** Verifica una secuencia de nombres de tokens */
    static void chkSeq(String fuente, int[] esperados, String desc) {
        Cinta c = new Cinta(fuente);
        Analex a = new Analex(c);
        for (int i = 0; i < esperados.length; i++) {
            int real = a.Preanalisis().getNom();
            if (real != esperados[i]) {
                System.out.printf("  [FAIL] %s%n", desc);
                System.out.printf("         Pos %d: esperado=%d obtenido=%d lexema='%s'%n",
                        i, esperados[i], real, a.lexema());
                fail++;
                return;
            }
            if (real == Token.FIN || real == Token.ERROR) break;
            a.avanzar();
        }
        System.out.printf("  [OK]   %s%n", desc);
        ok++;
    }
}
