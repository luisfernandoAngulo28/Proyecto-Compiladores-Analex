package analex;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

/**
 * Interfaz grafica del Analizador Lexico.
 * INF329-SA Compiladores - Gestion 2-2026
 *
 * Esta clase es un formulario del GUI Builder de NetBeans:
 *   - La pestana "Design" (Interfaz.form) arma los componentes.
 *   - initComponents() y las variables del final los GENERA NetBeans
 *     (bloques con GEN-BEGIN / GEN-END, NO editarlos a mano).
 *   - personalizar() agrega lo que el Design no cubre: estilos, tabla, atajos.
 *   - Los botones llaman a accionAbrir/Iniciar/Siguiente/Todos/Reiniciar.
 *
 * Decisiones de diseno (UI/UX):
 *   - Una sola accion principal (Iniciar, verde); las demas son secundarias.
 *   - Todo control tiene estados visibles: normal, hover, presionado, foco (teclado) y deshabilitado.
 *   - Espaciado en multiplos de 4 (SP_1..SP_4) y colores con contraste AA sobre el fondo oscuro.
 *   - Atajos: F5 Iniciar, F6 Siguiente, F7 Todos, F8 Reiniciar, Ctrl+O Abrir.
 *   - La barra de estado usa color para el resultado: verde = bien, amarillo = aviso, rojo = error.
 */
public class Interfaz extends javax.swing.JFrame {

    // ─── Analex y Cinta ──────────────────────────────────────────────────────
    private Cinta  cinta;
    private Analex analex;
    private boolean iniciado = false;

    // ─── Modelo de la tabla (se asigna en personalizar()) ────────────────────
    private DefaultTableModel modeloTabla;

    // ─── Estadisticas (#3) ────────────────────────────────────────────────────
    private int cntTotal, cntKW, cntID, cntNum, cntLit, cntErr;

    // ─── Tokens de diseno: espaciado (multiplos de 4) y forma ────────────────
    private static final int SP_1 = 4;
    private static final int SP_2 = 8;
    private static final int SP_3 = 12;
    private static final int SP_4 = 16;
    private static final int RADIO = 8;          // esquinas redondeadas

    // Color de bordes/lineas
    private static final Color LINEA = new Color(45, 52, 72);

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
    private static final Color TXT_FIN = new Color(148, 163, 184);
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

    // ─── Colores nuevos del sistema de diseno ────────────────────────────────
    /** Texto secundario (contraste 6.8:1 sobre el fondo, cumple AA). */
    private static final Color MUTED          = new Color(148, 163, 184);
    private static final Color SUPERFICIE_2   = new Color(30, 36, 48);
    private static final Color PRIMARIO       = new Color(34, 197, 94);
    private static final Color PRIMARIO_HOVER = new Color(74, 222, 128);
    private static final Color PRIMARIO_PRESS = new Color(22, 163, 74);
    private static final Color SOBRE_PRIMARIO = new Color(8, 28, 16);
    private static final Color DESHABILITADO  = new Color(110, 120, 135);

    // ─── Constructor ─────────────────────────────────────────────────────────
    public Interfaz() {
        initComponents();       // generado por el GUI Builder (pestana Design)
        personalizar();         // estilos, tabla, numeros de linea, atajos, codigo de ejemplo
        inicializarAnalex();
        setSize(1200, 720);
        setLocationRelativeTo(null);
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
    //  PERSONALIZACION (lo que el GUI Builder no genera por si solo)
    // ═════════════════════════════════════════════════════════════════════════
    private void personalizar() {
        getContentPane().setBackground(BG);

        // Barra de titulo + estadisticas
        pnlTitulo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINEA));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(SP_3, SP_4, SP_3, SP_4));
        JLabel[] stats = {lblTotal, lblKW, lblID, lblNum, lblLit, lblErr};
        for (JLabel l : stats)
            l.setBorder(BorderFactory.createEmptyBorder(SP_1, SP_1, SP_1, SP_1));
        lblErr.setForeground(MUTED);
        lblTotal.setToolTipText("Total de tokens reconocidos");
        lblKW.setToolTipText("Palabras clave y tipos");
        lblID.setToolTipText("Identificadores");
        lblNum.setToolTipText("Numeros");
        lblLit.setToolTipText("Literales (texto entre comillas)");
        lblErr.setToolTipText("Errores lexicos");

        // Centro: divisor, titulos de panel
        estilizarDivisor();
        pnlEditor.setBorder(BorderFactory.createEmptyBorder(SP_1, SP_1, SP_1, SP_1));
        pnlTabla .setBorder(BorderFactory.createEmptyBorder(SP_1, SP_1, SP_1, SP_1));
        JLabel[] titulos = {lblEditor, lblTabla};
        for (JLabel l : titulos) {
            l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, LINEA),
                    BorderFactory.createEmptyBorder(SP_2, SP_3, SP_2, SP_3)));
        }

        // Editor de codigo, con numeros de linea (coinciden con la columna Fila:Col de la tabla)
        txtCodigo.setLineWrap(false);
        txtCodigo.setBorder(BorderFactory.createEmptyBorder(SP_2, SP_3, SP_2, SP_3));
        txtCodigo.setText(codigoEjemplo());                 // Fix #6
        txtCodigo.setSelectionColor(new Color(30, 58, 95));
        txtCodigo.setSelectedTextColor(TEXT);
        scrollCodigo.setBorder(BorderFactory.createLineBorder(LINEA));
        scrollCodigo.setRowHeaderView(new NumerosDeLinea(txtCodigo));
        estilizarScroll(scrollCodigo);

        // Tabla de tokens (columnas con Fila:Col (#1))
        String[] cols = {"#", "Token", "Atributo", "Lexema", "Fila:Col"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaTokens.setModel(modeloTabla);
        tablaTokens.setFont(new Font("Cascadia Code", Font.PLAIN, 12));
        tablaTokens.setRowHeight(26);
        tablaTokens.setShowHorizontalLines(true);
        tablaTokens.setIntercellSpacing(new Dimension(0, 1));
        tablaTokens.setFillsViewportHeight(true);
        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(110);
        tablaTokens.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaTokens.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaTokens.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaTokens.setDefaultRenderer(Object.class, new CeldaColorRenderer());   // (#2)

        JTableHeader cabecera = tablaTokens.getTableHeader();
        cabecera.setDefaultRenderer(new CabeceraOscuraRenderer());
        cabecera.setReorderingAllowed(false);
        cabecera.setPreferredSize(new Dimension(cabecera.getPreferredSize().width, 32));

        // Estado vacio: mensaje que guia al usuario mientras la tabla no tiene filas
        scrollTabla.setViewport(new VistaTablaConMensaje());
        scrollTabla.setViewportView(tablaTokens);
        scrollTabla.setColumnHeaderView(cabecera);
        scrollTabla.setBorder(BorderFactory.createLineBorder(LINEA));
        scrollTabla.getViewport().setBackground(BG);
        estilizarScroll(scrollTabla);

        // Leyenda de colores
        pnlLeyenda.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LINEA));
        JLabel[] chips = {chipKW, chipOP, chipID, chipNUM, chipLIT, chipERR, chipFIN};
        for (JLabel l : chips)
            l.setBorder(BorderFactory.createLineBorder(l.getForeground().darker(), 1));
        chipKW.setToolTipText("Palabra clave o tipo");
        chipOP.setToolTipText("Operador o signo de puntuacion");
        chipID.setToolTipText("Identificador");
        chipNUM.setToolTipText("Numero");
        chipLIT.setToolTipText("Literal");
        chipERR.setToolTipText("Error lexico");
        chipFIN.setToolTipText("Fin del programa");

        // Panel inferior: botones + barra de estado
        pnlInferior.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, LINEA),
                BorderFactory.createEmptyBorder(SP_3, SP_4, SP_4, SP_4)));
        JLabel[] lecturaDelToken = {lblToken, lblLexema, lblPos};
        for (JLabel l : lecturaDelToken)
            l.setFont(new Font("Cascadia Code", Font.PLAIN, 11));
        lblEstado.setFont(new Font("Cascadia Code", Font.PLAIN, 12));
        lblEspacio2.setVisible(false);

        // Botones: Iniciar es la accion principal; el resto son secundarios
        estilizarBoton(btnAbrir,     new BotonUI(SUPERFICIE_2, new Color(38, 46, 62), BG_PANEL, new Color(70, 80, 100), TEXT),
                KeyEvent.VK_A, "Abrir un programa desde un archivo (Ctrl+O)");
        estilizarBoton(btnIniciar,   new BotonUI(PRIMARIO, PRIMARIO_HOVER, PRIMARIO_PRESS, PRIMARIO, SOBRE_PRIMARIO),
                KeyEvent.VK_I, "Analizar el programa desde el principio (F5)");
        estilizarBoton(btnSiguiente, estiloSecundario(ACCENT),
                KeyEvent.VK_S, "Mostrar el siguiente token (F6)");
        estilizarBoton(btnTodos,     estiloSecundario(PURPLE),
                KeyEvent.VK_T, "Analizar todo el programa de una vez (F7)");
        estilizarBoton(btnReiniciar, estiloSecundario(RED),
                KeyEvent.VK_R, "Limpiar la tabla y volver a editar (F8)");
        registrarAtajo("abrir",     KeyEvent.VK_O,  InputEvent.CTRL_DOWN_MASK, btnAbrir);
        registrarAtajo("iniciar",   KeyEvent.VK_F5, 0, btnIniciar);
        registrarAtajo("siguiente", KeyEvent.VK_F6, 0, btnSiguiente);
        registrarAtajo("todos",     KeyEvent.VK_F7, 0, btnTodos);
        registrarAtajo("reiniciar", KeyEvent.VK_F8, 0, btnReiniciar);

        mostrarEstado("Listo. Pulsa Iniciar (F5).", MUTED);
    }

    /** Estilo de boton secundario: relleno oscuro y borde/texto del color de acento. */
    private BotonUI estiloSecundario(Color acento) {
        return new BotonUI(SUPERFICIE_2, new Color(38, 46, 62), BG_PANEL, acento.darker(), acento);
    }

    /** Aplica el estilo, la tecla de acceso (Alt+letra), la ayuda y el cursor de un boton. */
    private void estilizarBoton(final JButton boton, BotonUI estilo, int teclaDeAcceso, String ayuda) {
        boton.setUI(estilo);
        boton.setText(boton.getText().trim());
        boton.setMnemonic(teclaDeAcceso);
        boton.setToolTipText(ayuda);
        boton.setRequestFocusEnabled(false);   // el clic no deja el anillo de foco; con Tab si aparece
        boton.setCursor(Cursor.getPredefinedCursor(boton.isEnabled() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        boton.addPropertyChangeListener("enabled", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evento) {
                boton.setCursor(Cursor.getPredefinedCursor(boton.isEnabled() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });
    }

    /** Asocia una tecla de la ventana con el clic de un boton (si esta habilitado). */
    private void registrarAtajo(String nombre, int tecla, int modificadores, final JButton boton) {
        JRootPane raiz = getRootPane();
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla, modificadores), nombre);
        raiz.getActionMap().put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                if (boton.isEnabled()) boton.doClick();
            }
        });
    }

    /** Divisor entre editor y tabla: oscuro, mas facil de agarrar, con tres puntos de agarre. */
    private void estilizarDivisor() {
        splitCentro.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(BG);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.setColor(new Color(70, 80, 100));
                        int centroX = getWidth() / 2, centroY = getHeight() / 2;
                        for (int i = -1; i <= 1; i++)
                            g.fillRect(centroX - 1, centroY + i * 8 - 1, 3, 3);
                    }
                };
            }
        });
        splitCentro.setBorder(BorderFactory.createEmptyBorder(SP_1, SP_2, SP_1, SP_2));
        splitCentro.setDividerSize(10);
        splitCentro.setDividerLocation(460);
        splitCentro.setResizeWeight(0.4);
    }

    /** Barras de desplazamiento oscuras y delgadas, y esquina del mismo color del fondo. */
    private void estilizarScroll(JScrollPane panel) {
        panel.getVerticalScrollBar().setUI(new ScrollBarOscuro());
        panel.getHorizontalScrollBar().setUI(new ScrollBarOscuro());
        panel.getVerticalScrollBar().setUnitIncrement(16);
        panel.getHorizontalScrollBar().setUnitIncrement(16);
        JPanel esquinaInferior = new JPanel();
        esquinaInferior.setBackground(BG);
        panel.setCorner(JScrollPane.LOWER_RIGHT_CORNER, esquinaInferior);
        JPanel esquinaSuperior = new JPanel();                // junto a la cabecera de la tabla
        esquinaSuperior.setBackground(BG_PANEL);
        panel.setCorner(JScrollPane.UPPER_RIGHT_CORNER, esquinaSuperior);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  COMPONENTES DE ESTILO (clases de apoyo)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Boton con esquinas redondeadas y estados visibles: normal, hover, presionado,
     * foco de teclado (anillo azul) y deshabilitado. Reemplaza el dibujo del Look and Feel
     * de Windows, que ignora los colores del boton.
     */
    private static class BotonUI extends BasicButtonUI {
        private final Color relleno, rellenoHover, rellenoPresionado, borde, texto;

        BotonUI(Color relleno, Color rellenoHover, Color rellenoPresionado, Color borde, Color texto) {
            this.relleno = relleno;
            this.rellenoHover = rellenoHover;
            this.rellenoPresionado = rellenoPresionado;
            this.borde = borde;
            this.texto = texto;
        }

        @Override
        public void installUI(JComponent componente) {
            super.installUI(componente);
            AbstractButton boton = (AbstractButton) componente;
            boton.setOpaque(false);
            boton.setContentAreaFilled(false);
            boton.setRolloverEnabled(true);
            boton.setFocusPainted(false);
            boton.setBorder(BorderFactory.createEmptyBorder(SP_2, SP_4, SP_2, SP_4));
        }

        @Override
        public Dimension getPreferredSize(JComponent componente) {
            Dimension tamano = super.getPreferredSize(componente);
            return new Dimension(Math.max(tamano.width, 88), Math.max(tamano.height, 36));   // alto minimo 36px
        }

        @Override
        public void paint(Graphics g, JComponent componente) {
            AbstractButton boton = (AbstractButton) componente;
            ButtonModel modelo = boton.getModel();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int ancho = componente.getWidth(), alto = componente.getHeight();
            Color colorRelleno, colorBorde, colorTexto;
            if (!boton.isEnabled()) {
                colorRelleno = BG_PANEL;  colorBorde = LINEA;  colorTexto = DESHABILITADO;
            } else {
                if (modelo.isPressed() && modelo.isArmed()) colorRelleno = rellenoPresionado;
                else if (modelo.isRollover())               colorRelleno = rellenoHover;
                else                                        colorRelleno = relleno;
                colorBorde = borde;  colorTexto = texto;
            }

            g2.setColor(colorRelleno);
            g2.fillRoundRect(0, 0, ancho - 1, alto - 1, RADIO * 2, RADIO * 2);
            g2.setColor(colorBorde);
            g2.drawRoundRect(0, 0, ancho - 1, alto - 1, RADIO * 2, RADIO * 2);

            if (boton.isFocusOwner() && boton.isEnabled()) {          // foco visible (teclado)
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(2, 2, ancho - 5, alto - 5, RADIO * 2 - 2, RADIO * 2 - 2);
            }

            g2.setFont(boton.getFont());
            FontMetrics medidas = g2.getFontMetrics();
            String etiqueta = boton.getText();
            int x = (ancho - medidas.stringWidth(etiqueta)) / 2;
            int y = (alto - medidas.getHeight()) / 2 + medidas.getAscent();
            g2.setColor(colorTexto);
            g2.drawString(etiqueta, x, y);

            int indiceMnemonico = boton.getDisplayedMnemonicIndex();     // letra subrayada de Alt+letra
            if (boton.isEnabled() && indiceMnemonico >= 0 && indiceMnemonico < etiqueta.length()) {
                int subrayadoX = x + medidas.stringWidth(etiqueta.substring(0, indiceMnemonico));
                int subrayadoAncho = medidas.charWidth(etiqueta.charAt(indiceMnemonico));
                g2.drawLine(subrayadoX, y + 2, subrayadoX + subrayadoAncho - 1, y + 2);
            }
            g2.dispose();
        }
    }

    /** Barra de desplazamiento oscura: sin flechas, pulgar redondeado que se aclara con el mouse. */
    private static class ScrollBarOscuro extends BasicScrollBarUI {
        @Override
        protected JButton createDecreaseButton(int orientacion) { return botonInvisible(); }

        @Override
        protected JButton createIncreaseButton(int orientacion) { return botonInvisible(); }

        private JButton botonInvisible() {
            JButton boton = new JButton();
            Dimension cero = new Dimension(0, 0);
            boton.setPreferredSize(cero);
            boton.setMinimumSize(cero);
            boton.setMaximumSize(cero);
            return boton;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent componente, Rectangle zona) {
            g.setColor(BG);
            g.fillRect(zona.x, zona.y, zona.width, zona.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent componente, Rectangle zona) {
            if (zona.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isThumbRollover() ? new Color(88, 100, 124) : new Color(58, 68, 88));
            g2.fillRoundRect(zona.x + 2, zona.y + 2, zona.width - 4, zona.height - 4, 8, 8);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent componente) {
            return new Dimension(12, 12);
        }
    }

    /** Cabecera de la tabla en el mismo tema oscuro (el Look and Feel de Windows la pinta blanca). */
    private static class CabeceraOscuraRenderer extends DefaultTableCellRenderer {
        CabeceraOscuraRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor,
                boolean seleccionada, boolean conFoco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, false, false, fila, columna);
            setBackground(BG_PANEL);
            setForeground(ACCENT);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setHorizontalAlignment((columna == 0 || columna == 4) ? SwingConstants.RIGHT : SwingConstants.LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, LINEA),
                    BorderFactory.createEmptyBorder(0, SP_2, 0, SP_2)));
            return this;
        }
    }

    /** Numeros de linea al costado del editor: la fila N de aqui es la fila N de la columna Fila:Col. */
    private static class NumerosDeLinea extends JComponent implements DocumentListener {
        private final JTextArea area;

        NumerosDeLinea(JTextArea area) {
            this.area = area;
            setFont(new Font("Cascadia Code", Font.PLAIN, 12));
            setToolTipText("Numero de linea");
            area.getDocument().addDocumentListener(this);
        }

        @Override
        public Dimension getPreferredSize() {
            int digitos = Math.max(2, String.valueOf(Math.max(1, area.getLineCount())).length());
            int anchoDigito = getFontMetrics(getFont()).charWidth('0');
            return new Dimension(anchoDigito * digitos + 2 * SP_3, area.getPreferredSize().height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(BG_PANEL);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setFont(getFont());
            FontMetrics medidas = g.getFontMetrics();
            Rectangle zona = g.getClipBounds();
            try {
                int primeraLinea = area.getLineOfOffset(area.viewToModel(new Point(0, zona.y)));
                int ultimaLinea  = area.getLineOfOffset(area.viewToModel(new Point(0, zona.y + zona.height)));
                g.setColor(MUTED);
                for (int linea = primeraLinea; linea <= ultimaLinea; linea++) {
                    Rectangle celda = area.modelToView(area.getLineStartOffset(linea));
                    if (celda == null) continue;
                    String numero = String.valueOf(linea + 1);
                    int x = getWidth() - SP_3 - medidas.stringWidth(numero);
                    int y = celda.y + (celda.height - medidas.getHeight()) / 2 + medidas.getAscent();
                    g.drawString(numero, x, y);
                }
            } catch (BadLocationException ignorada) {
                // el documento cambio mientras se pintaba: se repinta enseguida
            }
            g.setColor(LINEA);
            g.drawLine(getWidth() - 1, zona.y, getWidth() - 1, zona.y + zona.height);
        }

        @Override public void insertUpdate(DocumentEvent e)  { revalidate(); repaint(); }
        @Override public void removeUpdate(DocumentEvent e)  { revalidate(); repaint(); }
        @Override public void changedUpdate(DocumentEvent e) { revalidate(); repaint(); }
    }

    /** Area visible de la tabla: cuando no hay tokens dibuja un mensaje que dice que hacer. */
    private class VistaTablaConMensaje extends JViewport {
        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            if (modeloTabla == null || modeloTabla.getRowCount() > 0) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            String titulo = "Aún no hay tokens";
            String ayuda  = "Escribe o abre un programa, pulsa Iniciar y luego Siguiente o Todos.";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            FontMetrics medidasTitulo = g2.getFontMetrics();
            int yTitulo = getHeight() / 2 - SP_2;
            g2.setColor(TEXT);
            g2.drawString(titulo, (getWidth() - medidasTitulo.stringWidth(titulo)) / 2, yTitulo);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            FontMetrics medidasAyuda = g2.getFontMetrics();
            g2.setColor(MUTED);
            g2.drawString(ayuda, (getWidth() - medidasAyuda.stringWidth(ayuda)) / 2, yTitulo + SP_4 + SP_2);
            g2.dispose();
        }
    }

    // ─── Main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() { new Interfaz().setVisible(true); }
        });
    }


    /** This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTitulo = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlStats = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        lblSep1 = new javax.swing.JLabel();
        lblKW = new javax.swing.JLabel();
        lblID = new javax.swing.JLabel();
        lblNum = new javax.swing.JLabel();
        lblLit = new javax.swing.JLabel();
        lblSep2 = new javax.swing.JLabel();
        lblErr = new javax.swing.JLabel();
        lblEspacio1 = new javax.swing.JLabel();
        splitCentro = new javax.swing.JSplitPane();
        pnlEditor = new javax.swing.JPanel();
        lblEditor = new javax.swing.JLabel();
        scrollCodigo = new javax.swing.JScrollPane();
        txtCodigo = new javax.swing.JTextArea();
        pnlTabla = new javax.swing.JPanel();
        lblTabla = new javax.swing.JLabel();
        scrollTabla = new javax.swing.JScrollPane();
        tablaTokens = new javax.swing.JTable();
        pnlLeyenda = new javax.swing.JPanel();
        chipKW = new javax.swing.JLabel();
        chipOP = new javax.swing.JLabel();
        chipID = new javax.swing.JLabel();
        chipNUM = new javax.swing.JLabel();
        chipLIT = new javax.swing.JLabel();
        chipERR = new javax.swing.JLabel();
        chipFIN = new javax.swing.JLabel();
        pnlInferior = new javax.swing.JPanel();
        pnlBotones = new javax.swing.JPanel();
        btnAbrir = new javax.swing.JButton();
        btnIniciar = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        btnTodos = new javax.swing.JButton();
        btnReiniciar = new javax.swing.JButton();
        pnlStatus = new javax.swing.JPanel();
        lblToken = new javax.swing.JLabel();
        lblLexema = new javax.swing.JLabel();
        lblPos = new javax.swing.JLabel();
        lblEspacio2 = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Analizador léxico  |  INF329-SA  |  2-2026");
        setMinimumSize(new java.awt.Dimension(900, 580));
        getContentPane().setLayout(new java.awt.BorderLayout(4, 4));

        pnlTitulo.setBackground(new java.awt.Color(22, 27, 34));
        pnlTitulo.setLayout(new java.awt.BorderLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(125, 211, 252));
        lblTitulo.setText("Analizador léxico  |  INF329-SA  |  2-2026");
        pnlTitulo.add(lblTitulo, java.awt.BorderLayout.WEST);

        pnlStats.setBackground(new java.awt.Color(22, 27, 34));
        pnlStats.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 7));

        lblTotal.setFont(new java.awt.Font("Cascadia Code", 1, 11)); // NOI18N
        lblTotal.setText("Total: 0");
        lblTotal.setForeground(new java.awt.Color(226, 232, 240));
        pnlStats.add(lblTotal);

        lblSep1.setText("|");
        lblSep1.setForeground(new java.awt.Color(45, 52, 72));
        pnlStats.add(lblSep1);

        lblKW.setFont(new java.awt.Font("Cascadia Code", 1, 11)); // NOI18N
        lblKW.setText("KW: 0");
        lblKW.setForeground(new java.awt.Color(134, 239, 172));
        pnlStats.add(lblKW);

        lblID.setFont(new java.awt.Font("Cascadia Code", 1, 11)); // NOI18N
        lblID.setText("ID: 0");
        lblID.setForeground(new java.awt.Color(251, 191, 36));
        pnlStats.add(lblID);

        lblNum.setFont(new java.awt.Font("Cascadia Code", 1, 11)); // NOI18N
        lblNum.setText("NUM: 0");
        lblNum.setForeground(new java.awt.Color(167, 139, 250));
        pnlStats.add(lblNum);

        lblLit.setFont(new java.awt.Font("Cascadia Code", 1, 11)); // NOI18N
        lblLit.setText("LIT: 0");
        lblLit.setForeground(new java.awt.Color(94, 234, 212));
        pnlStats.add(lblLit);

        lblSep2.setText("|");
        lblSep2.setForeground(new java.awt.Color(45, 52, 72));
        pnlStats.add(lblSep2);

        lblErr.setFont(new java.awt.Font("Cascadia Code", 1, 11)); // NOI18N
        lblErr.setText("ERR: 0");
        lblErr.setForeground(new java.awt.Color(100, 116, 139));
        pnlStats.add(lblErr);

        lblEspacio1.setText("   ");
        pnlStats.add(lblEspacio1);

        pnlTitulo.add(pnlStats, java.awt.BorderLayout.EAST);

        getContentPane().add(pnlTitulo, java.awt.BorderLayout.NORTH);

        splitCentro.setBackground(new java.awt.Color(13, 17, 23));
        splitCentro.setDividerLocation(460);
        splitCentro.setDividerSize(5);

        pnlEditor.setBackground(new java.awt.Color(13, 17, 23));
        pnlEditor.setLayout(new java.awt.BorderLayout(2, 2));

        lblEditor.setBackground(new java.awt.Color(22, 27, 34));
        lblEditor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEditor.setForeground(new java.awt.Color(134, 239, 172));
        lblEditor.setText("Código fuente");
        lblEditor.setOpaque(true);
        pnlEditor.add(lblEditor, java.awt.BorderLayout.NORTH);

        txtCodigo.setBackground(new java.awt.Color(10, 13, 20));
        txtCodigo.setFont(new java.awt.Font("Cascadia Code", 0, 13)); // NOI18N
        txtCodigo.setForeground(new java.awt.Color(226, 232, 240));
        txtCodigo.setTabSize(4);
        txtCodigo.setCaretColor(new java.awt.Color(125, 211, 252));
        scrollCodigo.setViewportView(txtCodigo);

        pnlEditor.add(scrollCodigo, java.awt.BorderLayout.CENTER);

        splitCentro.setLeftComponent(pnlEditor);

        pnlTabla.setBackground(new java.awt.Color(13, 17, 23));
        pnlTabla.setLayout(new java.awt.BorderLayout(2, 2));

        lblTabla.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTabla.setText("Tokens reconocidos");
        lblTabla.setForeground(new java.awt.Color(134, 239, 172));
        lblTabla.setBackground(new java.awt.Color(22, 27, 34));
        lblTabla.setOpaque(true);
        pnlTabla.add(lblTabla, java.awt.BorderLayout.NORTH);

        tablaTokens.setBackground(new java.awt.Color(13, 17, 23));
        tablaTokens.setFont(new java.awt.Font("Cascadia Code", 0, 11)); // NOI18N
        tablaTokens.setForeground(new java.awt.Color(226, 232, 240));
        tablaTokens.setGridColor(new java.awt.Color(26, 32, 44));
        tablaTokens.setRowHeight(23);
        tablaTokens.setSelectionBackground(new java.awt.Color(30, 58, 95));
        tablaTokens.setSelectionForeground(new java.awt.Color(226, 232, 240));
        tablaTokens.setShowVerticalLines(false);
        scrollTabla.setViewportView(tablaTokens);

        pnlTabla.add(scrollTabla, java.awt.BorderLayout.CENTER);

        pnlLeyenda.setBackground(new java.awt.Color(22, 27, 34));
        pnlLeyenda.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 4));

        chipKW.setFont(new java.awt.Font("Cascadia Code", 1, 10)); // NOI18N
        chipKW.setForeground(new java.awt.Color(134, 239, 172));
        chipKW.setBackground(new java.awt.Color(14, 42, 20));
        chipKW.setText("  KW  ");
        chipKW.setOpaque(true);
        pnlLeyenda.add(chipKW);

        chipOP.setFont(new java.awt.Font("Cascadia Code", 1, 10)); // NOI18N
        chipOP.setForeground(new java.awt.Color(125, 211, 252));
        chipOP.setBackground(new java.awt.Color(15, 23, 42));
        chipOP.setText("  OP  ");
        chipOP.setOpaque(true);
        pnlLeyenda.add(chipOP);

        chipID.setFont(new java.awt.Font("Cascadia Code", 1, 10)); // NOI18N
        chipID.setForeground(new java.awt.Color(251, 191, 36));
        chipID.setBackground(new java.awt.Color(32, 25, 10));
        chipID.setText("  ID  ");
        chipID.setOpaque(true);
        pnlLeyenda.add(chipID);

        chipNUM.setFont(new java.awt.Font("Cascadia Code", 1, 10)); // NOI18N
        chipNUM.setForeground(new java.awt.Color(192, 132, 252));
        chipNUM.setBackground(new java.awt.Color(26, 15, 42));
        chipNUM.setText("  NUM  ");
        chipNUM.setOpaque(true);
        pnlLeyenda.add(chipNUM);

        chipLIT.setFont(new java.awt.Font("Cascadia Code", 1, 10)); // NOI18N
        chipLIT.setForeground(new java.awt.Color(94, 234, 212));
        chipLIT.setBackground(new java.awt.Color(10, 28, 28));
        chipLIT.setText("  LIT  ");
        chipLIT.setOpaque(true);
        pnlLeyenda.add(chipLIT);

        chipERR.setFont(new java.awt.Font("Cascadia Code", 1, 10)); // NOI18N
        chipERR.setForeground(new java.awt.Color(248, 113, 113));
        chipERR.setBackground(new java.awt.Color(60, 10, 10));
        chipERR.setText("  ERR  ");
        chipERR.setOpaque(true);
        pnlLeyenda.add(chipERR);

        chipFIN.setFont(new java.awt.Font("Cascadia Code", 1, 10)); // NOI18N
        chipFIN.setForeground(new java.awt.Color(100, 116, 139));
        chipFIN.setBackground(new java.awt.Color(20, 20, 20));
        chipFIN.setText("  FIN  ");
        chipFIN.setOpaque(true);
        pnlLeyenda.add(chipFIN);

        pnlTabla.add(pnlLeyenda, java.awt.BorderLayout.SOUTH);

        splitCentro.setRightComponent(pnlTabla);

        getContentPane().add(splitCentro, java.awt.BorderLayout.CENTER);

        pnlInferior.setBackground(new java.awt.Color(22, 27, 34));
        pnlInferior.setLayout(new java.awt.BorderLayout(6, 4));

        pnlBotones.setBackground(new java.awt.Color(22, 27, 34));
        pnlBotones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        btnAbrir.setBackground(new java.awt.Color(30, 36, 48));
        btnAbrir.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAbrir.setForeground(new java.awt.Color(226, 232, 240));
        btnAbrir.setText("Abrir...");
        btnAbrir.setFocusPainted(false);
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });
        pnlBotones.add(btnAbrir);

        btnIniciar.setBackground(new java.awt.Color(22, 101, 52));
        btnIniciar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnIniciar.setForeground(new java.awt.Color(134, 239, 172));
        btnIniciar.setText("Iniciar");
        btnIniciar.setFocusPainted(false);
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });
        pnlBotones.add(btnIniciar);

        btnSiguiente.setBackground(new java.awt.Color(30, 58, 138));
        btnSiguiente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSiguiente.setForeground(new java.awt.Color(125, 211, 252));
        btnSiguiente.setText("Siguiente");
        btnSiguiente.setFocusPainted(false);
        btnSiguiente.setEnabled(false);
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });
        pnlBotones.add(btnSiguiente);

        btnTodos.setBackground(new java.awt.Color(45, 27, 78));
        btnTodos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTodos.setForeground(new java.awt.Color(167, 139, 250));
        btnTodos.setText("Todos");
        btnTodos.setFocusPainted(false);
        btnTodos.setEnabled(false);
        btnTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTodosActionPerformed(evt);
            }
        });
        pnlBotones.add(btnTodos);

        btnReiniciar.setBackground(new java.awt.Color(60, 20, 20));
        btnReiniciar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnReiniciar.setForeground(new java.awt.Color(248, 113, 113));
        btnReiniciar.setText("Reiniciar");
        btnReiniciar.setFocusPainted(false);
        btnReiniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReiniciarActionPerformed(evt);
            }
        });
        pnlBotones.add(btnReiniciar);

        pnlInferior.add(pnlBotones, java.awt.BorderLayout.WEST);

        pnlStatus.setBackground(new java.awt.Color(22, 27, 34));
        pnlStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 12, 0));

        lblToken.setFont(new java.awt.Font("Cascadia Code", 0, 11)); // NOI18N
        lblToken.setText("Token: —");
        lblToken.setForeground(new java.awt.Color(125, 211, 252));
        pnlStatus.add(lblToken);

        lblLexema.setFont(new java.awt.Font("Cascadia Code", 0, 11)); // NOI18N
        lblLexema.setText("Lexema: —");
        lblLexema.setForeground(new java.awt.Color(134, 239, 172));
        pnlStatus.add(lblLexema);

        lblPos.setFont(new java.awt.Font("Cascadia Code", 0, 11)); // NOI18N
        lblPos.setText("Pos: —");
        lblPos.setForeground(new java.awt.Color(251, 191, 36));
        pnlStatus.add(lblPos);

        lblEspacio2.setText("     ");
        pnlStatus.add(lblEspacio2);

        lblEstado.setFont(new java.awt.Font("Cascadia Code", 0, 11)); // NOI18N
        lblEstado.setText("Listo.");
        lblEstado.setForeground(new java.awt.Color(226, 232, 240));
        pnlStatus.add(lblEstado);

        pnlInferior.add(pnlStatus, java.awt.BorderLayout.EAST);

        getContentPane().add(pnlInferior, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        accionAbrir();
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        accionIniciar();
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        accionSiguiente();
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTodosActionPerformed
        accionTodos();
    }//GEN-LAST:event_btnTodosActionPerformed

    private void btnReiniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReiniciarActionPerformed
        accionReiniciar();
    }//GEN-LAST:event_btnReiniciarActionPerformed

    // ═════════════════════════════════════════════════════════════════════════
    //  ACCIONES DE LOS BOTONES
    // ═════════════════════════════════════════════════════════════════════════

    /** Acorta un texto largo para que quepa en la barra de estado: "abcdefghij..." */
    private String recortar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 3) + "...";
    }

    /** Escribe un mensaje en la barra de estado con color: verde = bien, amarillo = aviso, rojo = error. */
    private void mostrarEstado(String mensaje, Color color) {
        lblEstado.setForeground(color);
        lblEstado.setText(mensaje);
    }

    /** Abre un archivo de texto y lo carga en el editor (reinicia el analisis anterior). */
    private void accionAbrir() {
        JFileChooser selector = new JFileChooser(carpetaInicialDeEjemplos());
        selector.setDialogTitle("Abrir programa fuente");
        selector.setFileFilter(new FileNameExtensionFilter("Programas (*.txt, *.pas, *.java)", "txt", "pas", "java"));
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File archivo = selector.getSelectedFile();
        try {
            String contenido = new String(Files.readAllBytes(archivo.toPath()), StandardCharsets.UTF_8).replace("\r", "");
            accionReiniciar();
            txtCodigo.setText(contenido);
            txtCodigo.setCaretPosition(0);
            mostrarEstado("Abierto: " + archivo.getName() + ". Pulsa Iniciar (F5).", GREEN);
        } catch (IOException error) {
            mostrarEstado("No se pudo abrir " + archivo.getName() + ": " + error.getMessage(), RED);
        }
    }

    /** Carpeta donde empieza el cuadro de abrir: "ejemplos" si existe, si no la carpeta actual. */
    private File carpetaInicialDeEjemplos() {
        File ejemplos = new File("ejemplos");
        return ejemplos.isDirectory() ? ejemplos : new File(System.getProperty("user.dir"));
    }

    private void accionIniciar() {
        String fuente = txtCodigo.getText();
        if (fuente.trim().isEmpty()) {
            mostrarEstado("Escribe o abre un programa antes de iniciar.", YELLOW);
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
        mostrarEstado("Listo para analizar. Pulsa Siguiente (F6) o Todos (F7).", TEXT);
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

        if (t.getNom() == Token.ERROR)
            mostrarEstado("Error léxico en " + (fil + 1) + ":" + (col + 1) + " - " + analex.getMensajeDeError(), RED);
        else if (t.getNom() != Token.FIN)
            mostrarEstado("Analizando... " + cntTotal + " tokens.", TEXT);

        if (t.getNom() == Token.FIN) {
            mostrarEstado("Análisis terminado: " + cntTotal + " tokens"
                    + (cntErr > 0 ? ", " + cntErr + " con error." : ", sin errores."),
                    cntErr > 0 ? RED : GREEN);
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
        String mensajeError = null;

        while (true) {
            Token  t   = analex.Preanalisis();
            String lx  = analex.lexema();
            int    fil = analex.getFila();
            int    col = analex.getColumna();

            agregarFila(t, lx, fil, col);
            contarToken(t);
            scrollToUltima();

            if (t.getNom() == Token.FIN || t.getNom() == Token.ERROR) {
                if (t.getNom() == Token.ERROR)
                    mensajeError = "Error léxico en " + (fil + 1) + ":" + (col + 1) + " - " + analex.getMensajeDeError();
                desResaltar();
                break;
            }
            analex.avanzar();
        }
        actualizarStats();
        actualizarStatusBar();
        if (mensajeError != null)
            mostrarEstado(mensajeError, RED);
        else
            mostrarEstado("Completo: " + cntTotal + " tokens, sin errores.", GREEN);
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
        mostrarEstado("Reiniciado. Puedes editar el programa.", MUTED);
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
        lblLexema.setText("  Lexema: \"" + recortar(analex.lexema(), 14) + "\"");
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
        lblErr  .setForeground(cntErr > 0 ? RED : MUTED);
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
        if (nom >= Token.PROGRAM && nom <= Token.ULTIMO_NOMBRE_DE_TOKEN) return CAT_KW;
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
            setBorder(BorderFactory.createEmptyBorder(0, SP_2, 0, SP_2));
            setHorizontalAlignment((col == 0 || col == 4) ? SwingConstants.RIGHT : SwingConstants.LEFT);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnReiniciar;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnTodos;
    private javax.swing.JLabel chipERR;
    private javax.swing.JLabel chipFIN;
    private javax.swing.JLabel chipID;
    private javax.swing.JLabel chipKW;
    private javax.swing.JLabel chipLIT;
    private javax.swing.JLabel chipNUM;
    private javax.swing.JLabel chipOP;
    private javax.swing.JLabel lblEditor;
    private javax.swing.JLabel lblErr;
    private javax.swing.JLabel lblEspacio1;
    private javax.swing.JLabel lblEspacio2;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblKW;
    private javax.swing.JLabel lblLexema;
    private javax.swing.JLabel lblLit;
    private javax.swing.JLabel lblNum;
    private javax.swing.JLabel lblPos;
    private javax.swing.JLabel lblSep1;
    private javax.swing.JLabel lblSep2;
    private javax.swing.JLabel lblTabla;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblToken;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlEditor;
    private javax.swing.JPanel pnlInferior;
    private javax.swing.JPanel pnlLeyenda;
    private javax.swing.JPanel pnlStats;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPanel pnlTabla;
    private javax.swing.JPanel pnlTitulo;
    private javax.swing.JScrollPane scrollCodigo;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JSplitPane splitCentro;
    private javax.swing.JTable tablaTokens;
    private javax.swing.JTextArea txtCodigo;
    // End of variables declaration//GEN-END:variables
}
