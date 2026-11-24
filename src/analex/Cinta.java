package analex;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cinta de Caracteres: el programa fuente visto como una fila de celdas,
 * con un cabezal que marca la celda que se esta leyendo.
 *
 * <pre>
 *   celdas:   [a][b][EOLN][c][d][EOF]
 *   cabezal:           ^            (el cabezal esta sobre una celda)
 * </pre>
 *
 * Vocabulario del curso que se conserva en esta clase:
 * <ul>
 *   <li>{@link #cc()}      = "caracter corriente": el char bajo el cabezal (no lo mueve).</li>
 *   <li>{@link #avanzar()} = mueve el cabezal una celda a la derecha.</li>
 *   <li>{@link #EOF} y {@link #EOLN} = fin de archivo y fin de linea.</li>
 * </ul>
 */
public class Cinta {

    // ─── Chars especiales ────────────────────────────────────────────────────
    private static final char RETORNO_DE_CARRO = 13;   // CR (Carry-Return), se ignora
    private static final char SALTO_DE_LINEA   = 10;   // LF (Line-Feed)

    /** Fin de archivo: es lo que devuelve {@link #cc()} cuando el cabezal sale de la cinta. */
    public static final char EOF  = 0;

    /** Fin de linea (se usa el de Linux, LF). */
    public static final char EOLN = SALTO_DE_LINEA;

    // ─── Campos principales ──────────────────────────────────────────────────
    /** Todo el programa fuente: cada char es una celda. */
    private String celdas;

    /** Indice (desde 0) de la celda que se esta leyendo. */
    private int cabezal;

    // ─── Campos secundarios (para informar la posicion en pantalla) ──────────
    /** Fila donde esta el cabezal (desde 0). */
    private int fila;

    /** Columna donde esta el cabezal (desde 0). */
    private int columna;

    /** Ultimo char que se dejo atras; sirve para saber cuando empieza una fila nueva. */
    private char caracterAnterior;

    // ─── Constructores ───────────────────────────────────────────────────────

    /** Construye una cinta vacia (solo tiene el EOF). */
    public Cinta() {
        this("");
    }

    /**
     * Construye una cinta con el programa fuente dado y pone el cabezal al inicio.
     * @param programaFuente el codigo fuente a secuenciar.
     */
    public Cinta(String programaFuente) {
        init(programaFuente);
    }

    // ─── Cargar / reiniciar ──────────────────────────────────────────────────

    /**
     * (Re)carga la cinta con un nuevo programa fuente y pone el cabezal al inicio.
     * @param programaFuente el nuevo codigo fuente ({@code null} se trata como cinta vacia).
     */
    public final void init(String programaFuente) {
        celdas = (programaFuente == null ? "" : programaFuente);
        init();
    }

    /** Vuelve el cabezal a la primera celda, sin cambiar el contenido de la cinta. */
    public void init() {
        fila = columna = 0;
        caracterAnterior = EOF;

        cabezal = 0;
        saltarRetornosDeCarro();
    }

    // ─── Leer / mover ────────────────────────────────────────────────────────

    /**
     * Caracter corriente: el char de la celda bajo el cabezal. NO mueve el cabezal.
     * @return el char actual, o {@link #EOF} si el cabezal ya salio de la cinta.
     */
    public char cc() {
        if (cabezal < celdas.length())
            return celdas.charAt(cabezal);

        return EOF;
    }

    /**
     * Mueve el cabezal una celda a la derecha (y se salta los retornos de carro).
     * Si el cabezal ya esta en el EOF no se mueve y se deja un aviso en el log.
     */
    public void avanzar() {
        char caracterActual = cc();
        if (caracterActual == EOF) {
            String mensaje = "Cinta.avanzar: No se puede avanzar más allá del EOF.";

            try {
                throw new Exception(mensaje);
            } catch (Exception ex) {
                Logger.getLogger(Cinta.class.getName()).log(Level.INFO, mensaje, ex);
            }
            return;
        }

        if (caracterAnterior == EOLN) {   // Se termino de secuenciar la fila anterior.
            fila++;
            columna = 0;
        }

        caracterAnterior = caracterActual;
        moverCabezalUnaCeldaALaDerecha();
        saltarRetornosDeCarro();
    }

    // ─── Posicion del cabezal ────────────────────────────────────────────────

    /**
     * @return la fila donde se encuentra el cabezal (las filas se enumeran desde 0).
     */
    public int getFila() {
        return fila;
    }

    /**
     * @return la columna donde se encuentra el cabezal (las columnas se enumeran desde 0).
     */
    public int getColumna() {
        return columna;
    }

    /**
     * @return el numero de celda donde se encuentra el cabezal (las celdas se enumeran desde 0).
     */
    public int getPos() {
        return cabezal;
    }

    // ─── Gancho para la interfaz ─────────────────────────────────────────────

    /**
     * Aviso de que Analex termino de leer un lexema. No hace nada aqui: se sobrescribe
     * (override) en la Interfaz para resaltar el lexema en el editor.
     * @param posicion celda donde empieza el lexema.
     * @param lexema   texto del lexema leido.
     */
    public void onComunicate(int posicion, String lexema) {
        // Overridable. Para la interfaz.
    }

    // ─── Metodos privados de apoyo ───────────────────────────────────────────

    /** Mueve el cabezal pasando por encima de los retornos de carro (CR). */
    private void saltarRetornosDeCarro() {
        while (cc() == RETORNO_DE_CARRO)
            moverCabezalUnaCeldaALaDerecha();
    }

    /** Mueve el cabezal una posicion a la derecha (y cuenta una columna mas). */
    private void moverCabezalUnaCeldaALaDerecha() {
        cabezal++;
        columna++;
    }

    // ─── toString: para ver la cinta con un print ────────────────────────────

    /**
     * Nombre legible de un char para dibujarlo en una celda: los invisibles se
     * escriben con su nombre (EOF, EOLN, CR) y el espacio como ' '.
     */
    static String nombreVisible(char caracter) {
        if (caracter == EOF)             return "EOF";
        if (caracter == EOLN)            return "EOLN";
        if (caracter == RETORNO_DE_CARRO) return "CR";
        if (caracter == ' ')             return "' '";
        if (caracter == '\t')            return "TAB";
        return String.valueOf(caracter);
    }

    /**
     * Dibuja la cinta alrededor del cabezal, por ejemplo:
     * <pre>
     * Cinta{ cabezal=3, fila=1, columna=0, cc=c }
     *   [a][b][EOLN][c][d][EOF]
     *                ^
     * </pre>
     * Solo se muestran unas pocas celdas a cada lado del cabezal.
     */
    @Override
    public String toString() {
        final int CELDAS_VISIBLES_A_CADA_LADO = 7;

        int primeraCelda = Math.max(0, cabezal - CELDAS_VISIBLES_A_CADA_LADO);
        int ultimaCelda  = Math.min(celdas.length(), cabezal + CELDAS_VISIBLES_A_CADA_LADO);  // celdas.length() = la celda EOF

        StringBuilder dibujoDeCeldas = new StringBuilder("  ");
        StringBuilder marcaDelCabezal = new StringBuilder();

        if (primeraCelda > 0) dibujoDeCeldas.append("...");

        for (int indice = primeraCelda; indice <= ultimaCelda; indice++) {
            String contenido = (indice < celdas.length()) ? nombreVisible(celdas.charAt(indice)) : "EOF";
            if (indice == cabezal) {
                while (marcaDelCabezal.length() < dibujoDeCeldas.length() + 1)
                    marcaDelCabezal.append(' ');
                marcaDelCabezal.append('^');
            }
            dibujoDeCeldas.append('[').append(contenido).append(']');
        }

        if (ultimaCelda < celdas.length()) dibujoDeCeldas.append("...");

        return "Cinta{ cabezal=" + cabezal + ", fila=" + fila + ", columna=" + columna
                + ", cc=" + nombreVisible(cc()) + " }\n"
                + dibujoDeCeldas + "\n"
                + marcaDelCabezal;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DEMO POR CONSOLA — una llamada a cada metodo de Cinta, con ejemplos
    //  Ejecutar: clic derecho en Cinta.java → Run File (Shift+F6)
    // ═════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println("=== 1. Constructor vacío: new Cinta() ===");
        Cinta cintaVacia = new Cinta();
        System.out.println(cintaVacia);

        System.out.println("\n=== 2. Constructor con texto: new Cinta(\"ab\\ncd\") ===");
        Cinta cinta = new Cinta("ab\ncd");
        System.out.println(cinta);

        System.out.println("\n=== 3. cc() NO mueve el cabezal; avanzar() SÍ ===");
        System.out.println("   cc() dos veces seguidas: " + nombreVisible(cinta.cc()) + " " + nombreVisible(cinta.cc()));
        while (cinta.cc() != EOF) {
            System.out.println("\n   avanzar() sobre " + nombreVisible(cinta.cc()));
            cinta.avanzar();
            System.out.println(cinta);
        }
        System.out.println("\n   OJO: la fila sube en el avanzar() SIGUIENTE al EOLN (usa caracterAnterior).");

        System.out.println("\n=== 4. avanzar() estando en el EOF (no se puede) ===");
        System.out.println("   (el texto de Logger/Exception que aparece es normal: es el aviso de Cinta)");
        cinta.avanzar();
        System.out.println(cinta);

        System.out.println("\n=== 5. init() — vuelve el cabezal al inicio, misma cinta ===");
        cinta.init();
        System.out.println(cinta);

        System.out.println("\n=== 6. init(String) — carga otro programa fuente ===");
        cinta.init("x := 10");
        System.out.println(cinta);
        cinta.init(null);                        // null se trata como ""
        System.out.println("   init(null) →");
        System.out.println(cinta);

        System.out.println("\n=== 7. Los CR (Carry-Return) se saltan solos: \"a\\r\\nb\" ===");
        Cinta formatoWindows = new Cinta("a\r\nb");     // a CR LF b
        while (formatoWindows.cc() != EOF) {
            System.out.println("   cc()=" + nombreVisible(formatoWindows.cc()));
            formatoWindows.avanzar();
        }
        System.out.println("   (nunca se ve cc()=CR: saltarRetornosDeCarro() lo salta tras cada avanzar)");

        System.out.println("\n=== 8. onComunicate(posicion, lexema) — gancho para la Interfaz ===");
        cinta.onComunicate(0, "hola");           // en Cinta no hace nada (vacio)
        System.out.println("   Cinta.onComunicate no imprime nada: es 'overridable'.");
        Cinta cintaEspia = new Cinta("abc") {    // subclase anonima que SI lo usa
            @Override
            public void onComunicate(int posicion, String lexema) {
                System.out.println("   [onComunicate] posicion=" + posicion + " lexema=\"" + lexema + "\"");
            }
        };
        cintaEspia.onComunicate(2, "abc");
        System.out.println("   Analex lo llama en resaltar() para pintar el lexema en la Interfaz.");

        System.out.println("\n=== 9. Recorrer toda una cinta con while (cc() != EOF) ===");
        Cinta programa = new Cinta("if x>1");
        StringBuilder celdasLeidas = new StringBuilder();
        while (programa.cc() != EOF) {
            celdasLeidas.append(programa.cc()).append('|');
            programa.avanzar();
        }
        System.out.println("   celdas: " + celdasLeidas);

        System.out.println("\n=== 10. toString() con una cinta larga (muestra solo el entorno del cabezal) ===");
        Cinta cintaLarga = new Cinta("program factorial; var n : integer; begin end");
        for (int i = 0; i < 20; i++) cintaLarga.avanzar();
        System.out.println(cintaLarga);
    }
}
