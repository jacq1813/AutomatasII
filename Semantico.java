import java.util.ArrayList;
import java.util.List;

public class Semantico {
    private List<String> tokens;
    private Parser parser;
    private int i;
    List<String> errores = new ArrayList<>();
    List<List<String>> declaraciones = new ArrayList<>(); // Almacena las declaraciones

    public Semantico(Parser parser) {
        this.parser = parser;
        tokens = parser.getListaTokens();
        procesarDeclaraciones();
        analizarValores();
    }

    private void procesarDeclaraciones() {
        int conD = 0;
        for (i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (esTipoDato(token)) {
                List<String> declaracion = new ArrayList<>();
                conD++;
                declaracion.add(token); // Tipo de dato
                if (++i < tokens.size())
                    declaracion.add(tokens.get(i)); // Identificador
                if (++i < tokens.size() && tokens.get(i).equals(";")) {
                    declaraciones.add(declaracion);
                }
            } else {
                break;
            }
        }
    }

    private void analizarValores() {
        while (i < tokens.size()) {
            String tokenActual = tokens.get(i);
            Escaner escaner = new Escaner(tokenActual);
            escaner.getToken(true);
            System.out.println("Token actual: " + tokenActual);
            System.out.println("Tipo: " + escaner.getTipo());

            if (esIdentificador(escaner) && !(tokens.get(i - 1).equals("then") || tokens.get(i - 1).equals("else"))) {

                System.out.println("Identificadores sin else o then");
                System.out.println("aqui " + tokenActual);
                System.out.println(tokens.get(i - 1));

                procesarAsignacion(tokenActual);
            } else if (tokens.get(i).equals("in >")) {
                procesarEntrada(tokenActual);
            } else if (tokens.get(i).equals("out <")) {
                procesarSalida(tokenActual);
            } else if (tokens.get(i).equals("if") || escaner.getTipo().equals("Operador relacional")) {
                procesarCondicional();
            } else if (tokens.get(i).equals("else") || tokens.get(i).equals("then")) {
                procesarElseThen();
            } else {
                i++;
            }
        }
    }

    private void procesarAsignacion(String id) {
        if (++i < tokens.size() && tokens.get(i).equals("=")) {
            System.out.println("Asignación: " + id + " = " + tokens.get(i));
            if (++i < tokens.size()) {

                String valor = tokens.get(i);
                System.out.println("Valor: " + valor);

                String tipo = obtenerTipoDeclaracion(id);

                System.out.println("Tipo: " + tipo);
                if (tipo != null) {

                    validarCompatibilidad(id, tipo, valor);
                    BuscarDeclaraciones(id, valor);

                } else {
                    errores.add("Error: La variable " + id + " no está declarada.");
                    System.out.println("Error: La variable " + id + " no está declarada.");
                }
            }
        }
        i++;
    }

    public void BuscarDeclaraciones(String id, String valor) {

        for (List<String> declaracion : declaraciones) {
            if (declaracion.contains(id)) {
                declaracion.add(valor);
            }
        }
    }

    private void procesarEntrada(String id) {
        if (++i < tokens.size()) {
            String valor = tokens.get(i);
            String tipo = obtenerTipoDeclaracion(id);
            validarCompatibilidad(id, tipo, valor);
        }
        i++;
    }

    private void procesarSalida(String id) {
        if (++i < tokens.size()) {
            String valor = tokens.get(i);
            System.out.println("Salida: " + valor);
        }
        i++;
    }

    private void procesarCondicional() {
        if (++i < tokens.size()) {
            String condicion = tokens.get(i);
            String tipo = obtenerTipoDeclaracion(condicion);
            if (tipo != null) {
                System.out.println("Correcto: La variable " + condicion + " está declarada.");

            } else {

                errores.add("Error: La variable " + condicion + " no está declarada.");
                System.out.println("Error: La variable " + condicion + " no está declarada.");
            }
        }
        i++;
    }

    private void procesarElseThen() {

        if (++i < tokens.size() && (tokens.get(i).equals("then") || tokens.get(i).equals("else"))) {
            String ife = tokens.get(i);
            String tipo = obtenerTipoDeclaracion(ife);

            if (tipo != null) {
                System.out.println("Correcto: La variable " + ife + " está declarada.");

            } else {
                errores.add("Error: La variable " + ife + " no está declarada.");
                System.out.println("Error: La variable " + ife + " no está declarada.");
            }
        }

    }

    private void validarCompatibilidad(String id, String tipo, String valor) {
        if (tipo != null) {
            Escaner escaner = new Escaner(valor);
            escaner.getToken(true);
            if (!esCompatible(tipo, escaner.getTipo())) {
                errores.add("Error: Asignación incompatible para " + id);
                System.out.println("Error: Asignación incompatible para " + id);
            } else {

                System.out.println("Correcto: Asignación compatible para " + id);
            }
        } else {
            errores.add("Error: La variable " + id + " no está declarada.");
            System.out.println("Error: La variable " + id + " no está declarada.");
        }
    }

    private String obtenerTipoDeclaracion(String id) {
        for (List<String> declaracion : declaraciones) {
            if (declaracion.get(1).equals(id)) {
                return declaracion.get(0);
            }
        }
        return null;
    }

    private boolean esCompatible(String tipo, String tipoValor) {
        return (tipo.equals("int") && tipoValor.equals("Numero")) ||
                (tipo.equals("float") && tipoValor.equals("Numero decimal")) ||
                (tipo.equals("string") && tipoValor.equals("Cadena"));
    }

    private boolean esTipoDato(String token) {
        return token.equals("int") || token.equals("float") || token.equals("string");
    }

    private boolean esIdentificador(Escaner escaner) {
        return escaner.getTipo().equals("Identificador");
    }
}