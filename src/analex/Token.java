package analex;

/**
 * Un token es un par {@code <nombre, atributo>}.
 * <ul>
 *   <li><b>nombre</b>: de que clase de token se trata (PROGRAM, NUM, ID, OPREL...).
 *       Se representa con un entero; las constantes de abajo le ponen nombre a cada numero.</li>
 *   <li><b>atributo</b>: informacion extra, solo para algunos tokens
 *       (NUM → 30, ID → -1, LITERAL → 0, OPREL → IGUAL/MEN/..., TIPO → BOOLEAN/INTEGER).</li>
 * </ul>
 * Ejemplos que imprime {@link #toString()}: {@code <PROGRAM,_>}, {@code <NUM,30>},
 * {@code <OPREL,MEI>}, {@code <TIPO,INTEGER>}.
 */
public class Token {

    // ─── Para el NOMBRE del token ────────────────────────────────────────────
    public static final int FIN        = 0;
    public static final int ERROR      = 1;
    public static final int PROGRAM    = 2;
    public static final int VAR        = 3;
    public static final int PROCEDURE  = 4; //"procedure" y "void
    public static final int BEGIN      = 5; //"begin" y "{"
    public static final int END        = 6; //"end" y "}"
    public static final int IF         = 7;
    public static final int THEN       = 8;
    public static final int ELSE       = 9;
    public static final int FOR        = 10;
    public static final int TO         = 11;
    public static final int DO         = 12;
    public static final int DOWNTO     = 13;
    public static final int WHILE      = 14;
    public static final int CASE       = 15;
    public static final int OF         = 16;
    public static final int REPEAT     = 17;
    public static final int UNTIL      = 18;
    public static final int READ       = 19;
    public static final int READLN     = 20;
    public static final int PRINT      = 21;
    public static final int PRINTLN    = 22;
    public static final int COMA       = 23;
    public static final int DOSPTOS    = 24;     //":"
    public static final int PTOCOMA    = 25;
    public static final int PA         = 26;     //"("
    public static final int PC         = 27;     //")"
    public static final int ASSIGN     = 28;    //":="
    public static final int NOT        = 29;
    public static final int AND        = 30;
    public static final int OR         = 31;
    public static final int MAS        = 32;
    public static final int MENOS      = 33;
    public static final int POR        = 34;
    public static final int MOD        = 35;
    public static final int DIV        = 36;
    public static final int NUM        = 37;
    public static final int ID         = 38;
    public static final int LITERAL    = 39;
    public static final int OPREL      = 40;
    public static final int TIPO       = 41;
    public static final int SUMA       = 42;    // "sumi" segun defensa

    /** El ultimo nombre de token que existe (si se agrega un token nuevo, subir este valor). */
    public static final int ULTIMO_NOMBRE_DE_TOKEN = SUMA;

    // ─── Para el ATRIBUTO del token OPREL ────────────────────────────────────
    public static final int IGUAL = 0;
    public static final int MEN   = 1;
    public static final int MAY   = 2;
    public static final int MEI   = 3;
    public static final int MAI   = 4;
    public static final int DIS   = 5;  //"<>" y "!="

    // ─── Para el ATRIBUTO del token TIPO ─────────────────────────────────────
    public static final int BOOLEAN = -3;
    public static final int INTEGER = -2;

    // ─── Campos de la clase ──────────────────────────────────────────────────
    /** Nombre del token (una de las constantes de arriba). */
    private int nombre;

    /** Atributo del token (su significado depende del nombre). */
    private int atributo;

    // ─── Constructores ───────────────────────────────────────────────────────

    /** Crea un token {@code <FIN,_>}. */
    public Token() {
        this(FIN);
    }

    /** Crea un token sin atributo (atributo = 0), por ejemplo {@code new Token(Token.PROGRAM)}. */
    public Token(int nombre) {
        this(nombre, 0);
    }

    /** Crea un token completo, por ejemplo {@code new Token(Token.NUM, 30)}. */
    public Token(int nombre, int atributo) {
        this.nombre = nombre;
        this.atributo = atributo;
    }

    // ─── Modificar ───────────────────────────────────────────────────────────

    /** Cambia nombre y atributo a la vez (lo usa Analex al terminar de leer un lexema). */
    public void set(int nombre, int atributo) {
        this.nombre = nombre;
        this.atributo = atributo;
    }

    /** Cambia solo el nombre del token. */
    public void setNom(int nombre) {
        this.nombre = nombre;
    }

    /** Cambia solo el atributo del token. */
    public void setAtr(int atributo) {
        this.atributo = atributo;
    }

    // ─── Consultar ───────────────────────────────────────────────────────────

    /** @return el nombre del token (se compara con las constantes, p.ej. {@code t.getNom() == Token.IF}). */
    public int getNom() {
        return nombre;
    }

    /** @return el atributo del token. */
    public int getAtr() {
        return atributo;
    }

    // ─── toString: formato del enunciado <NOMBRE,atributo> ───────────────────

    /** Formato del enunciado: {@code <NOMBRE,atributo>}; el atributo es {@code _} si el token no tiene. */
    @Override
    public String toString() {
        return "<" + elementoODesconocido(NOMBRES_DE_TOKEN, nombre) + "," + atributoComoTexto() + ">";
    }

    /**
     * Descripcion para depurar, con los numeros internos incluidos, por ejemplo
     * {@code Token{ nombre=NUM(37), atributo=30 → <NUM,30> }}.
     */
    public String descripcionDetallada() {
        return "Token{ nombre=" + elementoODesconocido(NOMBRES_DE_TOKEN, nombre) + "(" + nombre + ")"
                + ", atributo=" + atributo + " → " + this + " }";
    }

    /** Corrutina de toString(): el atributo escrito como lo pide el enunciado. */
    private String atributoComoTexto() {
        if (nombre == OPREL)
            return elementoODesconocido(NOMBRES_ATRIBUTO_OPREL, atributo);              // <OPREL,IGUAL> ... <OPREL,DIS>

        if (nombre == TIPO)
            return elementoODesconocido(NOMBRES_ATRIBUTO_TIPO, atributo - BOOLEAN);     // <TIPO,BOOLEAN> / <TIPO,INTEGER>

        if (nombre == NUM || nombre == ID || nombre == LITERAL)
            return "" + atributo;                                                        // <NUM,30> <ID,-1> <LITERAL,0>

        if (FIN <= nombre && nombre <= ULTIMO_NOMBRE_DE_TOKEN)
            return "_";                                                                  // los demas no tienen atributo

        return "" + atributo;
    }

    /** Devuelve {@code lista[indice]}, o "??" si el indice no existe. */
    private String elementoODesconocido(String[] lista, int indice) {
        try {
            return lista[indice];
        } catch (Exception e) {
            return TEXTO_DESCONOCIDO;
        }
    }

    // ─── Nombres para imprimir (el orden coincide con los numeros de arriba) ─
    private static final String TEXTO_DESCONOCIDO = "??";

    private static final String NOMBRES_ATRIBUTO_OPREL[] = {"IGUAL", "MEN", "MAY", "MEI", "MAI", "DIS"};
    private static final String NOMBRES_ATRIBUTO_TIPO[]  = {"BOOLEAN", "INTEGER"};

    private static final String NOMBRES_DE_TOKEN[] = {
        "FIN","ERROR",
        "PROGRAM","VAR","PROCEDURE","BEGIN","END",
        "IF","THEN","ELSE","FOR","TO","DO","DOWNTO","WHILE","CASE","OF","REPEAT","UNTIL",
        "READ","READLN","PRINT","PRINTLN",
        "COMA","DOSPTOS","PTOCOMA","PA","PC","ASSIGN",
        "NOT","AND","OR","MAS","MENOS","POR","MOD","DIV",
        "NUM","ID","LITERAL","OPREL","TIPO","SUMA"
    };

    // ═════════════════════════════════════════════════════════════════════════
    //  DEMO POR CONSOLA — una llamada a cada metodo de Token, con ejemplos
    //  Ejecutar: clic derecho en Token.java → Run File (Shift+F6)
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.out.println("=== 1. Constructores ===");
        Token tokenFin      = new Token();                 // <FIN,_>
        Token tokenProgram  = new Token(PROGRAM);          // atributo 0 por defecto
        Token tokenNumero   = new Token(NUM, 30);          // <NUM,30>
        System.out.println("   new Token()            → " + tokenFin.descripcionDetallada());
        System.out.println("   new Token(PROGRAM)     → " + tokenProgram.descripcionDetallada());
        System.out.println("   new Token(NUM, 30)     → " + tokenNumero.descripcionDetallada());

        System.out.println("\n=== 2. set(nombre, atributo) — lo usa Analex.dt() ===");
        Token tokenResultado = new Token();
        tokenResultado.set(ID, -1);
        System.out.println("   set(ID,-1)             → " + tokenResultado);
        tokenResultado.set(OPREL, MEI);
        System.out.println("   set(OPREL,MEI)         → " + tokenResultado);

        System.out.println("\n=== 3. setNom / setAtr por separado ===");
        tokenResultado.setNom(TIPO);
        tokenResultado.setAtr(INTEGER);
        System.out.println("   setNom(TIPO)+setAtr(INTEGER) → " + tokenResultado);

        System.out.println("\n=== 4. getNom() / getAtr() — comparar con las constantes ===");
        Token tokenIf = new Token(IF);
        System.out.println("   tokenIf.getNom() == Token.IF     ? " + (tokenIf.getNom() == IF));
        System.out.println("   tokenIf.getNom() == Token.WHILE  ? " + (tokenIf.getNom() == WHILE));

        System.out.println("\n=== 5. Los valores numéricos de las constantes ===");
        System.out.println("   FIN=" + FIN + " ERROR=" + ERROR + " PROGRAM=" + PROGRAM
                + " NUM=" + NUM + " ID=" + ID + " OPREL=" + OPREL + " SUMA=" + SUMA);
        System.out.println("   Atributos OPREL: IGUAL=" + IGUAL + " MEN=" + MEN + " MAY=" + MAY
                + " MEI=" + MEI + " MAI=" + MAI + " DIS=" + DIS);
        System.out.println("   Atributos TIPO : BOOLEAN=" + BOOLEAN + " INTEGER=" + INTEGER);

        System.out.println("\n=== 6. toString() — formato <NOMBRE,atributo> ===");
        Token[] tokensDeEjemplo = {
            new Token(PROGRAM), new Token(NUM, 30), new Token(ID, -1), new Token(LITERAL, 0),
            new Token(OPREL, MEI), new Token(OPREL, DIS),
            new Token(TIPO, BOOLEAN), new Token(TIPO, INTEGER),
            new Token(FIN), new Token(ERROR)
        };
        for (Token token : tokensDeEjemplo)
            System.out.println("   nombre=" + token.getNom() + " atributo=" + token.getAtr() + "  →  " + token);
        System.out.println("   (NUM/ID/LITERAL/OPREL/TIPO muestran su atributo; los demás '_')");

        System.out.println("\n=== 7. descripcionDetallada() — para depurar ===");
        System.out.println("   " + new Token(OPREL, MAI).descripcionDetallada());

        System.out.println("\n=== 8. Un nombre fuera de rango: new Token(99) ===");
        System.out.println("   " + new Token(99));    // elementoODesconocido() atrapa la excepcion → "??"
    }
}
