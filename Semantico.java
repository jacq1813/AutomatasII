import java.util.ArrayList;
import java.util.List;

public class Semantico {
    List<String> tokens;
    Parser parser;
    int i;

    // matriz para guardar los valores de las declaraciones con su tipo, nombre y
    // valor
    List<List<String>> declaraciones = new ArrayList<>();

    public Semantico(Parser parser) {
        System.out.println("entro a semantico2 ");
        this.parser = parser;
        tokens = parser.getListaTokens();
        System.out.println("recibe " + tokens);
        declaraciones();
        valoresDeclaraciones();

    }

    // si estan declarados
    public void declaraciones() {

        System.out.println("estoy en declaraciones");

        for (i = 0; i < tokens.size(); i++) { // Recorremos la lista con un índice
            System.out.println("entro : " + i);
            System.out.println("token actual for:" + tokens.get(i));
            if (tokens.get(i).equals("int") || tokens.get(i).equals("float") || tokens.get(i).equals("string")) {

                List<String> declaracion = new ArrayList<>();
                declaracion.add(tokens.get(i)); // Guardamos el tipo de dato
                System.out.println(tokens.get(i));
                i++; // Avanzamos al siguiente token
                declaracion.add(tokens.get(i));
                System.out.println(tokens.get(i));
                i++; // Avanzamos al siguiente token

                if (tokens.get(i).equals(";")) {
                    System.out.println(declaracion);
                    declaraciones.add(declaracion);

                    if (i + 1 < tokens.size() && (tokens.get(i + 1).equals("int") || tokens.get(i + 1).equals("float")
                            || tokens.get(i + 1).equals("string"))) {

                        // aqui quite un i++ que estaba de mas
                        System.out.println("siguiente: " + tokens.get(i));
                        System.out.println("siguiente: " + i);
                        continue;
                    } else {
                        // i++;
                        System.out.println("siguiente: " + tokens.get(i));
                        System.out.println("siguiente: " + i);
                        break;
                    }
                }
            }
        }

        // imprimir la lista declaraciones
        for (List<String> decla : declaraciones) {
            for (String dec : decla) {
                System.out.println(dec);
            }
        }

    }

    public void valoresDeclaraciones() {
        while (i < tokens.size() - 1) {
            String tokenActual = tokens.get(i);
            Escaner escane = new Escaner(tokenActual);
            escane.getToken(true);

            System.out.println("Token actual: " + tokenActual);
            System.out.println("Tipo: " + escane.getTipo());

            if (escane.getTipo().equals("Identificador")) { // Si es un identificador
                String id = tokenActual;
                System.out.println("Identificador: " + id);
                i++;

                if (i < tokens.size() && tokens.get(i).equals("=")) { // Si hay una asignación
                    System.out.println("Asignación " + tokens.get(i));
                    i++;
                    String valor = tokens.get(i); // Tomamos el valor asignado
                    System.out.println("Valor: " + valor);
                    String tipo = obtenerTipoDeclaracion(id); // Obtener el tipo del identificador
                    System.out.println("Tipo: " + tipo);

                    if (tipo != null) { // Si el identificador está declarado
                        Escaner escaner = new Escaner(valor);
                        escaner.getToken(true); // Analizar el valor
                        System.out.println("Tipo valor: " + escaner.getTipo());
                        System.out.println("Valor: " + escane.getToken(true));
                        if (!esCompatible(tipo, escaner.getTipo())) { // Validar compatibilidad
                            System.out.println("Error: Asignación incompatible para " + id);
                        } else {
                            System.out.println("Correcto: Asignación compatible para " + id);
                        }
                    } else {
                        System.out.println("Error: La variable " + id + " no está declarada.");
                    }

                    i++; // Avanzar después del valor
                }

            }

            // si es una entrada, aqui no estoy segura de como deba ser ya que la entrada la
            // da el usuario cosa que todabia no se como hacer
            if (i < tokens.size() && tokens.get(i).equals("in >")) {
                String id = tokenActual;
                System.out.println("Identificador: " + id);
                i++;

                System.out.println("Entrada: " + tokens.get(i));
                i++;
                String valor = tokens.get(i); // Tomamos el valor asignado
                System.out.println("Valor: " + valor);
                String tipo = obtenerTipoDeclaracion(id); // Obtener el tipo del identificador
                System.out.println("Tipo: " + tipo);

                if (tipo != null) { // Si el identificador está declarado
                    Escaner escaner = new Escaner(valor);
                    escaner.getToken(true); // Analizar el valor
                    System.out.println("Tipo valor: " + escaner.getTipo());
                    System.out.println("Valor: " + escane.getToken(true));
                    if (!esCompatible(tipo, escaner.getTipo())) { // Validar compatibilidad
                        System.out.println("Error: Asignación incompatible para " + id);
                    } else {
                        System.out.println("Correcto: Asignación compatible para " + id);
                    }
                } else {
                    System.out.println("Error: La variable " + id + " no está declarada.");
                }
                i++;
                // Avanzar después del valor
            }
            if (i < tokens.size() && tokens.get(i).equals("out <")) {
                String id = tokenActual;
                System.out.println("Identificador: " + id);
                i++;
                System.out.println("Salida: " + tokens.get(i));
                // obtener el tipo de tokens.get(i)

                Escaner s = new Escaner(tokens.get(i));
                String tip = s.getToken(true);
                System.out.println("Tipo: " + s.getTipo());

                if (s.getTipo() == "Identificador") {
                    System.out.println("Entrada: " + tokens.get(i));

                    String tipo = obtenerTipoDeclaracion(tip); // Obtener el tipo del identificador
                    System.out.println("Tipo: " + tipo);

                    if (tipo != null) { // Si el identificador está declarado
                        System.out.println("Correcto: La variable " + id + " está declaradaTATA.");
                    } else {
                        System.out.println("Error: La variable " + id + " no está declarada.");
                    }
                    i++; // Avanzar después del valor
                }

                System.out.println("Salida: " + tokens.get(i));
                i++;
                String valor = tokens.get(i); // Tomamos el valor asignado
                System.out.println("Valor: " + valor);

                i++; // Avanzar después del valor
            }
            if (i < tokens.size() && (tokens.get(i).equals("if") || escane.getTipo().equals("Operador relacional"))) {

                System.out.println("if: " + tokens.get(i));
                i++;
                System.out.println("siguiente " + tokens.get(i));

                Escaner s = new Escaner(tokens.get(i));
                String tip = s.getToken(true);
                System.out.println("Tipo: " + s.getTipo());

                if (s.getTipo().equals("Identificador")) {
                    // VERIFICAR SI EL IDENTIFICADOR ESTA DECLARADO
                    String tipo = obtenerTipoDeclaracion(tip); // Obtener el tipo del identificador
                    System.out.println("Tipo: " + tipo);

                    if (tipo != null) { // Si el identificador está declarado
                        System.out.println("Correcto: La variable " + tip + " está declarada.IFFF");
                    } else {
                        System.out.println("Error: La variable " + tip + " no está declarada.");
                    }
                }
                i++;
                System.out.println("dentro del inf siguiente " + tokens.get(i));

            } else {
                i++; // Seguir al siguiente token
            }
        }
    }

    // Método para obtener el tipo de una variable declarada
    private String obtenerTipoDeclaracion(String id) {
        for (List<String> declaracion : declaraciones) {
            if (declaracion.get(1).equals(id)) {
                return declaracion.get(0); // Retorna el tipo

            }
        }
        return null; // No está declarada
    }

    // Método para validar si el tipo del valor coincide con el tipo de la variable
    private boolean esCompatible(String tipo, String tipoValor) {
        return (tipo.equals("int") && tipoValor.equals("Numero")) ||
                (tipo.equals("float") && tipoValor.equals("Numero decimal")) ||
                (tipo.equals("string") && tipoValor.equals("Cadena"));
    }

}
