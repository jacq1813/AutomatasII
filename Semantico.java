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
        this.parser = parser;
        tokens = parser.getListaTokens();
        declaraciones();
        valoresDeclaraciones();

    }

    // si estan declarados
    public void declaraciones() {

        for (i = 0; i < tokens.size(); i++) { // Recorremos la lista con un índice

            if (tokens.get(i).equals("int") || tokens.get(i).equals("float") || tokens.get(i).equals("string")) {

                List<String> declaracion = new ArrayList<>();
                declaracion.add(tokens.get(i)); // Guardamos el tipo de dato
                i++; // Avanzamos al siguiente token
                declaracion.add(tokens.get(i));
                i++; // Avanzamos al siguiente token

                if (tokens.get(i).equals(";")) {
                    declaraciones.add(declaracion);
                    continue;
                }

            }
            break;
        }

    }

    public void valoresDeclaraciones() {
        while (i < tokens.size()) {
            String tokenActual = tokens.get(i);
            Escaner escane = new Escaner(tokenActual);
            escane.getToken(true);

            if (escane.getTipo() == "Identificador") { // Si es un identificador
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
                        }
                    } else {
                        System.out.println("Error: La variable " + id + " no está declarada.");
                    }

                    i++; // Avanzar después del valor
                }
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
