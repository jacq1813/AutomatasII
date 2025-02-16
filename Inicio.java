import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Inicio extends JFrame {

    private JTextArea codigo;
    private JTable tokensResultado;
    private DefaultTableModel tableModel;
    private JButton analizar;

    public Inicio() {
        setTitle("Lenguaje J");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear la barra de menú
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem menuNuevo = new JMenuItem("Nuevo");
        JMenuItem menuAbrir = new JMenuItem("Abrir");
        JMenuItem menuGuardar = new JMenuItem("Guardar");

        // Agregar acciones a los menús
        menuNuevo.addActionListener(e -> limpiarTodo());
        menuAbrir.addActionListener(e -> abrirArchivo());
        menuGuardar.addActionListener(e -> guardarArchivo());

        menuArchivo.add(menuNuevo);
        menuArchivo.add(menuAbrir);
        menuArchivo.add(menuGuardar);
        menuBar.add(menuArchivo);

        // area para escribir el código
        codigo = new JTextArea(20, 30);
        JScrollPane scrollCodigo = new JScrollPane(codigo);

        // tabla de tokens y tipos
        String[] columnas = { "Token", "Tipo", "Error" };
        tableModel = new DefaultTableModel(columnas, 0);
        tokensResultado = new JTable(tableModel);
        JScrollPane scrollTabla = new JScrollPane(tokensResultado);

        analizar = new JButton("Analizar");
        analizar.addActionListener(e -> analizarCodigo());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, scrollTabla);
        splitPane.setDividerLocation(300);

        setJMenuBar(menuBar);
        add(splitPane, BorderLayout.CENTER);
        add(analizar, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void limpiarTodo() {
        codigo.setText("");
        tableModel.setRowCount(0);
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
        tableModel.setRowCount(0);
        String codigoFuente = codigo.getText();
        Parser parser = new Parser(codigoFuente);
        Escaner escaner = new Escaner(codigoFuente);

        try {
            parser.Inicia();
            int i = 0;
            String token = escaner.getToken(true); // Obtén el primer token
            while (!token.equals("EOF")) {
                String tipo = escaner.getTipo();

                // Verifica si hay errores en la lista de errores y accede correctamente
                String error = (i < parser.listaErrores.size()) ? parser.listaErrores.get(i) : "";

                // Imprime el token y el error (si hay)
                tableModel.addRow(new Object[] { token, tipo, error });
                i++;

                // Avanza al siguiente token
                token = escaner.getToken(true); // Obtén el siguiente token
            }
        } catch (Exception e) {
            mostrarError(e.getMessage());
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
