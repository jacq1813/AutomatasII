import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;

public class Inicio extends JFrame {
    private JTextArea codigo, consola, codigoIntermedio;
    private JTable tokensResultado, declaracionesTabla;
    private DefaultTableModel tokensModel, declaracionesModel;
    private JButton btnNuevo, btnAbrir, btnGuardar;
    private JButton btnEscaner, btnParser, btnSemantico, btnCodigoIntermedio;
    List<String> errores;
    List<String> erroresS;
    private Parser parser;
    private Escaner escaner;
    private Semantico semantico;

    public Inicio() {
        setTitle("Lenguaje J");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior para los botones de archivo
        JPanel botonesArchivoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevo = new JButton("Nuevo");
        btnAbrir = new JButton("Abrir");
        btnGuardar = new JButton("Guardar");

        btnNuevo.addActionListener(e -> limpiarTodo());
        btnAbrir.addActionListener(e -> abrirArchivo());
        btnGuardar.addActionListener(e -> guardarArchivo());

        botonesArchivoPanel.add(btnNuevo);
        botonesArchivoPanel.add(btnAbrir);
        botonesArchivoPanel.add(btnGuardar);

        // Panel para botones de análisis
        JPanel botonesAnalisisPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEscaner = new JButton("Análisis Léxico (Escáner)");
        btnParser = new JButton("Análisis Sintáctico (Parser)");
        btnSemantico = new JButton("Análisis Semántico");
        btnCodigoIntermedio = new JButton("Generar Código Intermedio");

        btnEscaner.addActionListener(e -> analizarEscaner());
        btnParser.addActionListener(e -> analizarParser());
        btnSemantico.addActionListener(e -> analizarSemantico());
        btnCodigoIntermedio.addActionListener(e -> generarCodigoIntermedio());

        botonesAnalisisPanel.add(btnEscaner);
        botonesAnalisisPanel.add(btnParser);
        botonesAnalisisPanel.add(btnSemantico);
        botonesAnalisisPanel.add(btnCodigoIntermedio);

        // Panel combinado para todos los botones
        JPanel botonesCompletos = new JPanel(new GridLayout(2, 1));
        botonesCompletos.add(botonesArchivoPanel);
        botonesCompletos.add(botonesAnalisisPanel);

        // Área de código
        codigo = new JTextArea(20, 30);
        JScrollPane scrollCodigo = new JScrollPane(codigo);
        scrollCodigo.setBorder(BorderFactory.createTitledBorder("Código Fuente"));

        // Área de código intermedio
        codigoIntermedio = new JTextArea(20, 30);
        JScrollPane scrollCodigoIntermedio = new JScrollPane(codigoIntermedio);
        scrollCodigoIntermedio.setBorder(BorderFactory.createTitledBorder("Código Intermedio"));

        // Tabla de tokens
        tokensModel = new DefaultTableModel(new String[] { "Token", "Tipo", "Error" }, 0);
        tokensResultado = new JTable(tokensModel);
        JScrollPane scrollTokens = new JScrollPane(tokensResultado);
        scrollTokens.setBorder(BorderFactory.createTitledBorder("Tokens (Análisis Léxico)"));

        // Tabla de declaraciones
        declaracionesModel = new DefaultTableModel(new String[] { "Tipo", "Nombre", "Valor" }, 0);
        declaracionesTabla = new JTable(declaracionesModel);
        JScrollPane scrollDeclaraciones = new JScrollPane(declaracionesTabla);
        scrollDeclaraciones.setBorder(BorderFactory.createTitledBorder("Declaraciones (Análisis Semántico)"));

        // Consola
        consola = new JTextArea(8, 50);
        consola.setEditable(false);
        JScrollPane scrollConsola = new JScrollPane(consola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Consola"));

        // Panel para código fuente y código intermedio
        JSplitPane splitCodigos = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, scrollCodigoIntermedio);
        splitCodigos.setDividerLocation(450);

        // Panel para tablas de análisis
        JPanel panelTablas = new JPanel(new GridLayout(2, 1));
        panelTablas.add(scrollTokens);
        panelTablas.add(scrollDeclaraciones);

        // Panel central con código fuente, código intermedio y tablas
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitCodigos, panelTablas);
        splitPrincipal.setDividerLocation(700);

        // Agregar todos los componentes al frame
        add(botonesCompletos, BorderLayout.NORTH);
        add(splitPrincipal, BorderLayout.CENTER);
        add(scrollConsola, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void limpiarTodo() {
        codigo.setText("");
        codigoIntermedio.setText("");
        tokensModel.setRowCount(0);
        declaracionesModel.setRowCount(0);
        consola.setText("");
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo de texto (*.txt)", "txt"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                limpiarTodo();
                codigo.read(reader, null);
            } catch (IOException e) {
                mostrarError("Error al abrir el archivo");
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!archivo.getName().toLowerCase().endsWith(".txt")) {
                archivo = new File(archivo.getAbsolutePath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
                codigo.write(writer);
            } catch (IOException e) {
                mostrarError("Error al guardar el archivo");
            }
        }
    }

    private void analizarEscaner() {
        String codigoFuente = codigo.getText();
        escaner = new Escaner(codigoFuente);

        tokensModel.setRowCount(0);
        consola.setText("Realizando análisis léxico...\n");

        try {
            String token = escaner.getToken(true);
            while (!token.equals("EOF")) {
                String tipo = escaner.getTipo();
                tokensModel.addRow(new Object[] { token, tipo, "" });
                token = escaner.getToken(true);
            }
            consola.append("Análisis léxico completado.\n");
        } catch (Exception e) {
            consola.append("Error en el análisis léxico: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void analizarParser() {
        String codigoFuente = codigo.getText();
        parser = new Parser(codigoFuente);
        escaner = new Escaner(codigoFuente);
        errores = parser.getListaErrores();

        tokensModel.setRowCount(0);
        consola.setText("Realizando análisis sintáctico...\n");

        try {
            parser.Inicia();
            int i = 0;
            String token = escaner.getToken(true);

            while (!token.equals("EOF")) {
                String tipo = escaner.getTipo();
                String error = (i < errores.size()) ? errores.get(i) : "";
                tokensModel.addRow(new Object[] { token, tipo, error });
                i++;
                token = escaner.getToken(true);
            }

            if (errores.stream().anyMatch(e -> e.startsWith("Error"))) {
                consola.append("Análisis sintáctico completado con errores.\n");
                for (String error : errores) {
                    if (error.startsWith("Error")) {
                        consola.append(error + "\n");
                    }
                }
                JOptionPane.showMessageDialog(this, "Sintaxis incorrecta", "Resultado", JOptionPane.ERROR_MESSAGE);
            } else {
                consola.append("Análisis sintáctico completado correctamente.\n");
                JOptionPane.showMessageDialog(this, "Sintaxis correcta", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            consola.append("Error en el análisis sintáctico: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void analizarSemantico() {
        // Primero verificamos si ya se hizo el análisis sintáctico
        if (parser == null) {
            consola.setText("Debe realizar el análisis sintáctico primero.\n");
            return;
        }

        consola.setText("Realizando análisis semántico...\n");

        try {
            semantico = new Semantico(parser);
            declaracionesModel.setRowCount(0);
            for (List<String> declaracion : semantico.declaraciones) {
                String valor = (declaracion.size() > 2) ? declaracion.get(2) : "";
                declaracionesModel.addRow(new Object[] { declaracion.get(0), declaracion.get(1), valor });
            }

            erroresS = semantico.errores;
            if (erroresS.isEmpty()) {
                consola.append("Análisis semántico completado correctamente.\n");
            } else {
                consola.append("Análisis semántico completado con errores:\n");
                for (String s : erroresS) {
                    consola.append(s + "\n");
                }
            }
        } catch (Exception e) {
            consola.append("Error en el análisis semántico: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void generarCodigoIntermedio() {
        // Verificamos si ya se hizo el análisis semántico
        if (semantico == null) {
            consola.setText("Debe realizar el análisis semántico primero.\n");
            return;
        }

        // Si hay errores semánticos, no generamos código intermedio
        if (erroresS != null && !erroresS.isEmpty()) {
            consola.setText("No se puede generar código intermedio debido a errores semánticos.\n");
            return;
        }

        consola.setText("Generando código intermedio...\n");

        try {
            // Usamos la nueva clase CodIntermedio
            CodIntermedio generador = new CodIntermedio(codigo.getText());

            // Por ahora solo generamos código para las declaraciones (regla D)
            String codigoGenerado = generador.generarCodigoDeclaraciones();

            // Obtenemos el código generado como un string

            // Lo mostramos en el área de código intermedio
            codigoIntermedio.setText(codigoGenerado);
            consola.append("Código intermedio para declaraciones generado correctamente.\n");
        } catch (Exception e) {
            consola.append("Error al generar código intermedio: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    // Simulación de generación de código intermedio (reemplazar con la
    // implementación real)
    private String generarCodigoIntermedioSimulado() {
        StringBuilder codigo = new StringBuilder();
        codigo.append("# Código Intermedio Generado\n\n");

        // Obtener las declaraciones del análisis semántico
        for (int i = 0; i < declaracionesModel.getRowCount(); i++) {
            String tipo = (String) declaracionesModel.getValueAt(i, 0);
            String nombre = (String) declaracionesModel.getValueAt(i, 1);
            String valor = (String) declaracionesModel.getValueAt(i, 2);

            codigo.append("DECL ").append(tipo).append(" ").append(nombre);
            if (valor != null && !valor.isEmpty()) {
                codigo.append(" = ").append(valor);
            }
            codigo.append("\n");
        }

        codigo.append("\n# Instrucciones\n");
        // Aquí iría tu lógica real para generar el código intermedio basado en el AST
        // o cualquier otra estructura que uses para representar el programa

        return codigo.toString();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Inicio ide = new Inicio();
        });
    }
}