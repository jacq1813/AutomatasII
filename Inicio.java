import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class Inicio extends JFrame {
    private JTextArea codigo, consola;
    private JTable tokensResultado, declaracionesTabla;
    private DefaultTableModel tokensModel, declaracionesModel;
    private JButton analizar;
    List<String> errores;
    List<String> erroresS;

    public Inicio() {
        setTitle("Lenguaje J");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem menuNuevo = new JMenuItem("Nuevo");
        JMenuItem menuAbrir = new JMenuItem("Abrir");
        JMenuItem menuGuardar = new JMenuItem("Guardar");

        menuNuevo.addActionListener(e -> limpiarTodo());
        menuAbrir.addActionListener(e -> abrirArchivo());
        menuGuardar.addActionListener(e -> guardarArchivo());

        menuArchivo.add(menuNuevo);
        menuArchivo.add(menuAbrir);
        menuArchivo.add(menuGuardar);
        menuBar.add(menuArchivo);

        codigo = new JTextArea(20, 30);
        JScrollPane scrollCodigo = new JScrollPane(codigo);

        tokensModel = new DefaultTableModel(new String[] { "Token", "Tipo", "Error" }, 0);
        tokensResultado = new JTable(tokensModel);
        JScrollPane scrollTokens = new JScrollPane(tokensResultado);

        declaracionesModel = new DefaultTableModel(new String[] { "Tipo", "Nombre", "Valor" }, 0);
        declaracionesTabla = new JTable(declaracionesModel);
        JScrollPane scrollDeclaraciones = new JScrollPane(declaracionesTabla);

        consola = new JTextArea(5, 50);
        consola.setEditable(false);
        JScrollPane scrollConsola = new JScrollPane(consola);

        analizar = new JButton("Analizar");
        analizar.addActionListener(e -> analizarCodigo());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, scrollTokens);
        splitPane.setDividerLocation(300);

        JPanel panelTablas = new JPanel(new GridLayout(2, 1));
        panelTablas.add(scrollTokens);
        panelTablas.add(scrollDeclaraciones);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(scrollConsola, BorderLayout.CENTER);
        panelInferior.add(analizar, BorderLayout.SOUTH);

        setJMenuBar(menuBar);
        add(splitPane, BorderLayout.CENTER);
        add(panelTablas, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void limpiarTodo() {
        codigo.setText("");
        tokensModel.setRowCount(0);
        declaracionesModel.setRowCount(0);
        consola.setText("g");
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

    private void analizarCodigo() {

        String codigoFuente = codigo.getText();
        Parser parser = new Parser(codigoFuente);
        Escaner escaner = new Escaner(codigoFuente);
        errores = parser.getListaErrores();

        try {
            parser.Inicia();
            int i = 0;
            String token = escaner.getToken(true);

            tokensModel.setRowCount(0);
            while (!token.equals("EOF")) {
                String tipo = escaner.getTipo();
                String error = (i < errores.size()) ? errores.get(i) : "";
                tokensModel.addRow(new Object[] { token, tipo, error });
                i++;
                token = escaner.getToken(true);
            }

            Semantico semantico = new Semantico(parser);
            declaracionesModel.setRowCount(0);
            for (List<String> declaracion : semantico.declaraciones) {
                String valor = (declaracion.size() > 2) ? declaracion.get(2) : "";

                declaracionesModel.addRow(new Object[] { declaracion.get(0), declaracion.get(1), valor });
            }

            if (errores.stream().anyMatch(e -> e.startsWith("Error"))) {
                JOptionPane.showMessageDialog(this, "Sintaxis incorrecta", "Resultado", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Sintaxis correcta", "Resultado", JOptionPane.INFORMATION_MESSAGE);
            }

            erroresS = semantico.errores;
            consola.setText("");
            if (erroresS.isEmpty()) {
                consola.append("Análisis semántico correcto\n");
            } else {
                consola.append("Errores semánticos:\n");
            }

            for (String s : erroresS) {
                consola.append(s + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        Inicio ide = new Inicio();
        ide.setVisible(true);
    }
}
