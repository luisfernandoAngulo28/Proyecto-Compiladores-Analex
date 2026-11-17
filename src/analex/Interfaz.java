package analex;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

/**
 * Interfaz grafica del Analizador Lexico.
 * INF329-SA Compiladores - Gestion 2-2026
 *
 * Mejoras aplicadas:
 *   #1 - Columna Fila:Col en la tabla de tokens
 *   #2 - Filas coloreadas por tipo de token
 *   #3 - Panel de estadisticas en la barra de titulo
 *   #4 - Fix: SUMA incluido en tokenNombre()
 *   #5 - Resaltado de color distinto por tipo en el editor
 *   #6 - Codigo de ejemplo en minusculas (case-sensitive)
 */
public class Interfaz extends JFrame {

    // ─── Analex y Cinta ──────────────────────────────────────────────────────
    private Cinta  cinta;
    private Analex analex;
    private boolean iniciado = false;

    // ─── Componentes principales ─────────────────────────────────────────────
    private JTextArea        txtCodigo;
    private JScrollPane      scrollCodigo;
    private JButton          btnIniciar, btnSiguiente, btnTodos, btnReiniciar;
    private DefaultTableModel modeloTabla;
    private JTable           tablaTokens;
    private JScrollPane      scrollTabla;
    private JLabel           lblEstado, lblToken, lblLexema, lblPos;

    // ─── Estadisticas (#3) ────────────────────────────────────────────────────
    private JLabel lblTotal, lblKW, lblID, lblNum, lblLit, lblErr;
    private int cntTotal, cntKW, cntID, cntNum, cntLit, cntErr;

    // ─── Paleta de colores ────────────────────────────────────────────────────
    // Fondo general
    private static final Color BG       = new Color(13, 17, 23);
    private static final Color BG_PANEL = new Color(22, 27, 34);
    private static final Color TEXT     = new Color(226, 232, 240);

    // Colores de acento
    private static final Color ACCENT   = new Color(125, 211, 252);  // azul
    private static final Color GREEN    = new Color(134, 239, 172);  // verde
    private static final Color YELLOW   = new Color(251, 191, 36);   // amarillo
    private static final Color PURPLE   = new Color(167, 139, 250);  // violeta
    private static final Color CYAN     = new Color(94, 234, 212);   // cyan
    private static final Color RED      = new Color(248, 113, 113);  // rojo
    private static final Color GRAY     = new Color(100, 116, 139);  // gris

    // Colores de fondo de fila en la tabla (#2)
    private static final Color ROW_KW  = new Color(14, 42, 20);
    private static final Color ROW_OP  = new Color(15, 23, 42);
    private static final Color ROW_ID  = new Color(32, 25, 10);
    private static final Color ROW_NUM = new Color(26, 15, 42);
    private static final Color ROW_LIT = new Color(10, 28, 28);
    private static final Color ROW_ERR = new Color(60, 10, 10);
    private static final Color ROW_FIN = new Color(20, 20, 20);
    private static final Color ROW_DEF = new Color(13, 17, 23);

    // Colores de texto de fila en la tabla (#2)
    private static final Color TXT_KW  = new Color(134, 239, 172);
    private static final Color TXT_OP  = new Color(125, 211, 252);
    private static final Color TXT_ID  = new Color(251, 191, 36);
    private static final Color TXT_NUM = new Color(192, 132, 252);
    private static final Color TXT_LIT = new Color(94, 234, 212);
    private static final Color TXT_ERR = new Color(248, 113, 113);
    private static final Color TXT_FIN = new Color(100, 116, 139);
    private static final Color TXT_DEF = new Color(203, 213, 225);

    // Colores de resaltado en el editor (#5)
    private static final Color HI_KW  = new Color(14, 80, 30);
    private static final Color HI_OP  = new Color(20, 50, 100);
    private static final Color HI_ID  = new Color(80, 60, 10);
    private static final Color HI_NUM = new Color(60, 20, 90);
    private static final Color HI_LIT = new Color(10, 70, 70);
    private static final Color HI_ERR = new Color(120, 10, 10);
    private static final Color HI_DEF = new Color(60, 50, 80);

    // ─── Categorias de token ──────────────────────────────────────────────────
    private static final int CAT_KW  = 0;
    private static final int CAT_OP  = 1;
    private static final int CAT_ID  = 2;
    private static final int CAT_NUM = 3;
    private static final int CAT_LIT = 4;
    private static final int CAT_ERR = 5;
    private static final int CAT_FIN = 6;
    private static final int CAT_OTR = 7;

    // ─── Constructor ─────────────────────────────────────────────────────────
    public Interfaz() {
        super("Analizador Lexico  |  INF329-SA  |  2-2026");
        construirUI();
        inicializarAnalex();
        setSize(1120, 720);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ─── Inicializar Analex ───────────────────────────────────────────────────
    private void inicializarAnalex() {
        cinta = new Cinta() {
            @Override
            public void onComunicate(int pos, String lexema) {
                resaltarEnEditor(pos, lexema.length());
            }
        };
        analex = new Analex(cinta);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CONSTRUCCION DE LA UI
    // ═════════════════════════════════════════════════════════════════════════
    private void construirUI() {
        setLayout(new BorderLayout(4, 4));
        getContentPane().setBackground(BG);
        add(construirTitulo(),   BorderLayout.NORTH);
        add(construirCentro(),   BorderLayout.CENTER);
        add(construirInferior(), BorderLayout.SOUTH);
    }

    // ── Barra de titulo con estadisticas ──────────────────────────────────────
    private JPanel construirTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 52, 72)));

        // Nombre del proyecto (izquierda)
        JLabel titulo = new JLabel("   Analizador Lexico  |  INF329-SA  |  2-2026");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(ACCENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));
        p.add(titulo, BorderLayout.WEST);

        // Estadisticas (#3) (derecha)
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 7));
        stats.setBackground(BG_PANEL);

        lblTotal = statLabel("Total: 0",  TEXT);
        lblKW    = statLabel("KW: 0",     GREEN);
        lblID    = statLabel("ID: 0",     YELLOW);
        lblNum   = statLabel("NUM: 0",    PURPLE);
        lblLit   = statLabel("LIT: 0",    CYAN);
        lblErr   = statLabel("ERR: 0",    GRAY);

        stats.add(lblTotal);
        stats.add(sep()); stats.add(lblKW);
        stats.add(lblID);  stats.add(lblNum);
        stats.add(lblLit);
        stats.add(sep()); stats.add(lblErr);
        stats.add(new JLabel("   "));
        p.add(stats, BorderLayout.EAST);
        return p;
    }

    private JLabel statLabel(String txt, Color color) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Cascadia Code", Font.BOLD, 11));
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return l;
    }

    private JLabel sep() {
        JLabel l = new JLabel("|");
        l.setForeground(new Color(45, 52, 72));
        return l;
    }

    // ── Panel central: editor | tabla ─────────────────────────────────────────
    private JSplitPane construirCentro() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                construirPanelEditor(),
                construirPanelTabla());
        split.setDividerLocation(460);
        split.setDividerSize(5);
        split.setBackground(BG);
        split.setBorder(BorderFactory.createEmptyBorder(4, 6, 2, 6));
        return split;
    }

    // ── Panel editor ──────────────────────────────────────────────────────────
    private JPanel construirPanelEditor() {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        p.add(panelLabel("  Codigo Fuente", GREEN), BorderLayout.NORTH);

        txtCodigo = new JTextArea();
        txtCodigo.setFont(new Font("Cascadia Code", Font.PLAIN, 13));
        txtCodigo.setBackground(new Color(10, 13, 20));
        txtCodigo.setForeground(TEXT);
        txtCodigo.setCaretColor(ACCENT);
        txtCodigo.setLineWrap(false);
        txtCodigo.setTabSize(4);
        txtCodigo.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        txtCodigo.setText(codigoEjemplo()); // Fix #6

        scrollCodigo = new JScrollPane(txtCodigo);
        scrollCodigo.setBorder(BorderFactory.createLineBorder(new Color(45, 52, 72)));
        p.add(scrollCodigo, BorderLayout.CENTER);
        return p;
    }

    // ── Panel tabla ───────────────────────────────────────────────────────────
    private JPanel construirPanelTabla() {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        p.add(panelLabel("  Tokens Reconocidos", GREEN), BorderLayout.NORTH);

        // Columnas con Fila:Col (#1)
        String[] cols = {"#", "Token", "Atributo", "Lexema", "Fila:Col"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaTokens = new JTable(modeloTabla);
        tablaTokens.setBackground(BG);
        tablaTokens.setForeground(TEXT);
        tablaTokens.setGridColor(new Color(26, 32, 44));
        tablaTokens.setSelectionBackground(new Color(30, 58, 95));
        tablaTokens.setSelectionForeground(TEXT);
        tablaTokens.setRowHeight(23);
        tablaTokens.setFont(new Font("Cascadia Code", Font.PLAIN, 11));
        tablaTokens.setShowHorizontalLines(true);
        tablaTokens.setShowVerticalLines(false);
        tablaTokens.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tablaTokens.getTableHeader();
        header.setBackground(new Color(22, 27, 34));
        header.setForeground(ACCENT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Anchos
        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(32);
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaTokens.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaTokens.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaTokens.getColumnModel().getColumn(4).setPreferredWidth(70);

        // Renderizador con colores (#2)
        tablaTokens.setDefaultRenderer(Object.class, new CeldaColorRenderer());

        scrollTabla = new JScrollPane(tablaTokens);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(45, 52, 72)));
        p.add(scrollTabla, BorderLayout.CENTER);
        p.add(construirLeyenda(), BorderLayout.SOUTH);
        return p;
    }

    // ── Leyenda de colores debajo de la tabla ─────────────────────────────────
    private JPanel construirLeyenda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(45, 52, 72)));
        p.add(chip("KW",  TXT_KW,  ROW_KW));
        p.add(chip("OP",  TXT_OP,  ROW_OP));
        p.add(chip("ID",  TXT_ID,  ROW_ID));
        p.add(chip("NUM", TXT_NUM, ROW_NUM));
        p.add(chip("LIT", TXT_LIT, ROW_LIT));
        p.add(chip("ERR", TXT_ERR, ROW_ERR));
        p.add(chip("FIN", TXT_FIN, ROW_FIN));
        return p;
    }

    private JLabel chip(String txt, Color fg, Color bg) {
        JLabel l = new JLabel("  " + txt + "  ");
        l.setFont(new Font("Cascadia Code", Font.BOLD, 10));
        l.setForeground(fg);
        l.setBackground(bg);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createLineBorder(fg.darker(), 1));
        return l;
    }

    // ── Panel inferior: botones + status bar ──────────────────────────────────
    private JPanel construirInferior() {
        JPanel p = new JPanel(new BorderLayout(6, 4));
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(45, 52, 72)),
                BorderFactory.createEmptyBorder(6, 10, 10, 10)));

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setBackground(BG_PANEL);

        btnIniciar   = boton("   Iniciar",   new Color(22, 101, 52), GREEN);
        btnSiguiente = boton("  Siguiente",  new Color(30, 58, 138), ACCENT);
        btnTodos     = boton("  Todos",      new Color(45, 27, 78),  PURPLE);
        btnReiniciar = boton("  Reiniciar",  new Color(60, 20, 20),  RED);

        btnSiguiente.setEnabled(false);
        btnTodos.setEnabled(false);

        botones.add(btnIniciar);
        botones.add(btnSiguiente);
        botones.add(btnTodos);
        botones.add(btnReiniciar);
        p.add(botones, BorderLayout.WEST);

        // Status bar
        JPanel status = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        status.setBackground(BG_PANEL);

        lblToken  = label("Token: —",  ACCENT);
        lblLexema = label("Lexema: —", GREEN);
        lblPos    = label("Pos: —",    YELLOW);
        lblEstado = label("Listo.",    TEXT);

        status.add(lblToken);
        status.add(lblLexema);
        status.add(lblPos);
        status.add(Box.createHorizontalStrut(14));
        status.add(lblEstado);
        p.add(status, BorderLayout.EAST);

        // Eventos
        btnIniciar  .addActionListener(e -> accionIniciar());
        btnSiguiente.addActionListener(e -> accionSiguiente());
        btnTodos    .addActionListener(e -> accionTodos());
        btnReiniciar.addActionListener(e -> accionReiniciar());

        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ACCIONES DE LOS BOTONES
    // ═════════════════════════════════════════════════════════════════════════

    private void accionIniciar() {
        String fuente = txtCodigo.getText();
        if (fuente.trim().isEmpty()) {
            lblEstado.setText("Escribi algo en el editor primero.");
            return;
        }
        modeloTabla.setRowCount(0);
        desResaltar();
        resetContadores();
        actualizarStats();

        cinta.init(fuente);
        analex.init();
        iniciado = true;

        btnSiguiente.setEnabled(true);
        btnTodos.setEnabled(true);
        txtCodigo.setEditable(false);
        lblEstado.setText("Iniciado. Presiona Siguiente o Todos.");
        actualizarStatusBar();
    }

    private void accionSiguiente() {
        if (!iniciado) return;
        Token  t   = analex.Preanalisis();
        String lx  = analex.lexema();
        int    fil = analex.getFila();
        int    col = analex.getColumna();

        agregarFila(t, lx, fil, col);
        contarToken(t);
        scrollToUltima();
        actualizarStats();
        actualizarStatusBar();

        if (t.getNom() == Token.FIN) {
            lblEstado.setText("FIN — " + cntTotal + " tokens reconocidos.");
            btnSiguiente.setEnabled(false);
            btnTodos.setEnabled(false);
            desResaltar();
            return;
        }
        analex.avanzar();
    }

    private void accionTodos() {
        if (!iniciado) return;
        btnSiguiente.setEnabled(false);
        btnTodos.setEnabled(false);

        while (true) {
            Token  t   = analex.Preanalisis();
            String lx  = analex.lexema();
            int    fil = analex.getFila();
            int    col = analex.getColumna();

            agregarFila(t, lx, fil, col);
            contarToken(t);
            scrollToUltima();

            if (t.getNom() == Token.FIN || t.getNom() == Token.ERROR) {
                desResaltar();
                break;
            }
            analex.avanzar();
        }
        actualizarStats();
        lblEstado.setText("Listo — " + cntTotal + " tokens  |  " + cntErr + " errores.");
    }

    private void accionReiniciar() {
        modeloTabla.setRowCount(0);
        desResaltar();
        iniciado = false;
        txtCodigo.setEditable(true);
        btnSiguiente.setEnabled(false);
        btnTodos.setEnabled(false);
        resetContadores();
        actualizarStats();
        lblEstado.setText("Reiniciado.");
        lblToken.setText("Token: —");
        lblLexema.setText("Lexema: —");
        lblPos.setText("Pos: —");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS DE TABLA Y STATUS
    // ═════════════════════════════════════════════════════════════════════════

    private void agregarFila(Token t, String lx, int fila, int col) {
        int num = modeloTabla.getRowCount() + 1;
        // Fila:Col en base 1 para que sea legible
        String posStr = (fila + 1) + ":" + (col + 1);
        modeloTabla.addRow(new Object[]{
            num,
            tokenNombre(t.getNom()),
            tokenAtributo(t),
            lx == null ? "" : lx,
            posStr
        });
    }

    private void scrollToUltima() {
        int last = tablaTokens.getRowCount() - 1;
        if (last >= 0) {
            tablaTokens.scrollRectToVisible(tablaTokens.getCellRect(last, 0, true));
            tablaTokens.setRowSelectionInterval(last, last);
        }
    }

    private void actualizarStatusBar() {
        Token t = analex.Preanalisis();
        lblToken .setText("  Token: "  + tokenNombre(t.getNom()));
        lblLexema.setText("  Lexema: \"" + analex.lexema() + "\"");
        lblPos   .setText("  " + (analex.getFila() + 1) + ":" + (analex.getColumna() + 1));
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ESTADISTICAS (#3)
    // ═════════════════════════════════════════════════════════════════════════

    private void resetContadores() {
        cntTotal = cntKW = cntID = cntNum = cntLit = cntErr = 0;
    }

    private void contarToken(Token t) {
        cntTotal++;
        int cat = categoria(t.getNom());
        if      (cat == CAT_KW)  cntKW++;
        else if (cat == CAT_ID)  cntID++;
        else if (cat == CAT_NUM) cntNum++;
        else if (cat == CAT_LIT) cntLit++;
        else if (cat == CAT_ERR) cntErr++;
    }

    private void actualizarStats() {
        lblTotal.setText("Total: " + cntTotal);
        lblKW   .setText("KW: "   + cntKW);
        lblID   .setText("ID: "   + cntID);
        lblNum  .setText("NUM: "  + cntNum);
        lblLit  .setText("LIT: "  + cntLit);
        lblErr  .setText("ERR: "  + cntErr);
        lblErr  .setForeground(cntErr > 0 ? RED : GRAY);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  RESALTADO EN EL EDITOR (#5)
    // ═════════════════════════════════════════════════════════════════════════

    private void resaltarEnEditor(int pos, int longitud) {
        desResaltar();
        if (analex == null) return;
        Color color = colorResaltado(categoria(analex.Preanalisis().getNom()));
        HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(color);
        try {
            txtCodigo.getHighlighter().addHighlight(pos, pos + longitud, painter);
            if (pos < txtCodigo.getText().length())
                txtCodigo.setCaretPosition(pos);
        } catch (Exception ignored) {}
    }

    private void desResaltar() {
        txtCodigo.getHighlighter().removeAllHighlights();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CLASIFICACION DE TOKENS (categoria)
    // ═════════════════════════════════════════════════════════════════════════

    private int categoria(int nom) {
        if (nom == Token.FIN)                       return CAT_FIN;
        if (nom == Token.ERROR)                      return CAT_ERR;
        if (nom == Token.NUM)                        return CAT_NUM;
        if (nom == Token.ID)                         return CAT_ID;
        if (nom == Token.LITERAL)                    return CAT_LIT;
        if (nom == Token.OPREL)                      return CAT_OP;
        if (nom == Token.TIPO)                       return CAT_KW;
        // Operadores de puntuacion: COMA(23) hasta DIV(36)
        if (nom >= Token.COMA && nom <= Token.DIV)   return CAT_OP;
        // Resto: palabras reservadas
        if (nom >= Token.PROGRAM && nom <= Token.SUMA) return CAT_KW;
        return CAT_OTR;
    }

    private Color colorResaltado(int cat) {
        switch (cat) {
            case CAT_KW:  return HI_KW;
            case CAT_OP:  return HI_OP;
            case CAT_ID:  return HI_ID;
            case CAT_NUM: return HI_NUM;
            case CAT_LIT: return HI_LIT;
            case CAT_ERR: return HI_ERR;
            default:      return HI_DEF;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  RENDERIZADOR DE CELDAS CON COLORES (#2)
    // ═════════════════════════════════════════════════════════════════════════

    private class CeldaColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);

            int cat = CAT_OTR;
            if (row < modeloTabla.getRowCount()) {
                Object tokObj = modeloTabla.getValueAt(row, 1); // columna Token
                if (tokObj != null) cat = categoriaDeNombre(tokObj.toString());
            }

            if (sel) {
                setBackground(new Color(30, 58, 95));
                setForeground(TEXT);
            } else {
                setBackground(rowBg(cat));
                setForeground(rowFg(cat));
            }
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            return this;
        }
    }

    /** Convierte el nombre de texto del token a su categoria */
    private int categoriaDeNombre(String nombre) {
        switch (nombre) {
            case "FIN":               return CAT_FIN;
            case "ERROR":             return CAT_ERR;
            case "NUM":               return CAT_NUM;
            case "ID":                return CAT_ID;
            case "LITERAL":           return CAT_LIT;
            case "OPREL":             return CAT_OP;
            case "TIPO":              return CAT_KW;
            // Operadores / puntuacion
            case "COMA": case "DOSPTOS": case "PTOCOMA":
            case "PA":   case "PC":      case "ASSIGN":
            case "NOT":  case "AND":     case "OR":
            case "MAS":  case "MENOS":   case "POR":
            case "MOD":  case "DIV":      return CAT_OP;
            // Todo lo demas son palabras clave
            default:                  return CAT_KW;
        }
    }

    private Color rowBg(int cat) {
        switch (cat) {
            case CAT_KW:  return ROW_KW;
            case CAT_OP:  return ROW_OP;
            case CAT_ID:  return ROW_ID;
            case CAT_NUM: return ROW_NUM;
            case CAT_LIT: return ROW_LIT;
            case CAT_ERR: return ROW_ERR;
            case CAT_FIN: return ROW_FIN;
            default:      return ROW_DEF;
        }
    }

    private Color rowFg(int cat) {
        switch (cat) {
            case CAT_KW:  return TXT_KW;
            case CAT_OP:  return TXT_OP;
            case CAT_ID:  return TXT_ID;
            case CAT_NUM: return TXT_NUM;
            case CAT_LIT: return TXT_LIT;
            case CAT_ERR: return TXT_ERR;
            case CAT_FIN: return TXT_FIN;
            default:      return TXT_DEF;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  NOMBRE Y ATRIBUTO DEL TOKEN (Fix #4: SUMA incluido)
    // ═════════════════════════════════════════════════════════════════════════

    private String tokenNombre(int nom) {
        // Indices coinciden exactamente con Token.java
        String[] nombres = {
            "FIN",    "ERROR",
            "PROGRAM","VAR",    "PROCEDURE","BEGIN", "END",
            "IF",     "THEN",   "ELSE",     "FOR",   "TO",   "DO",  "DOWNTO","WHILE",
            "CASE",   "OF",     "REPEAT",   "UNTIL",
            "READ",   "READLN", "PRINT",    "PRINTLN",
            "COMA",   "DOSPTOS","PTOCOMA",  "PA",    "PC",   "ASSIGN",
            "NOT",    "AND",    "OR",       "MAS",   "MENOS","POR",  "MOD",  "DIV",
            "NUM",    "ID",     "LITERAL",  "OPREL", "TIPO",
            "SUMA"   // Fix #4: nom=42
        };
        if (nom >= 0 && nom < nombres.length) return nombres[nom];
        return "??(" + nom + ")";
    }

    private String tokenAtributo(Token t) {
        int nom = t.getNom();
        int atr = t.getAtr();
        if (nom == Token.OPREL) {
            String[] o = {"IGUAL","MEN","MAY","MEI","MAI","DIS"};
            return (atr >= 0 && atr < o.length) ? o[atr] : "??";
        }
        if (nom == Token.TIPO)    return atr == Token.BOOLEAN ? "BOOLEAN" : "INTEGER";
        if (nom == Token.NUM)     return "30";
        if (nom == Token.ID)      return "-1";
        if (nom == Token.LITERAL) return "0";
        return "_";
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS DE CONSTRUCCION
    // ═════════════════════════════════════════════════════════════════════════

    private JLabel panelLabel(String txt, Color color) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(color);
        l.setBackground(BG_PANEL);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 52, 72)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return l;
    }

    private JButton boton(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg.darker(), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(bg.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private JLabel label(String txt, Color color) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Cascadia Code", Font.PLAIN, 11));
        l.setForeground(color);
        return l;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CODIGO DE EJEMPLO (Fix #6: minusculas = case-sensitive)
    // ═════════════════════════════════════════════════════════════════════════
    private String codigoEjemplo() {
        return
            "// Programa factorial - Pascal+Java\n" +
            "// case-sensitive: palabras clave en minusculas\n" +
            "program factorial;\n" +
            "var\n" +
            "  n         : integer;\n" +
            "  resultado : integer;\n" +
            "\n" +
            "begin\n" +
            "  read(n);\n" +
            "  resultado := 1;\n" +
            "  while (n > 0) do begin  // loop factorial //\n" +
            "    resultado := resultado * n;\n" +
            "    n := n - 1;\n" +
            "  end;\n" +
            "  println(resultado);\n" +
            "end";
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { new Interfaz(); }
        });
    }
}
