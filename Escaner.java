import java.util.Arrays;
import java.util.List;

public class Escaner {
    private String[] tokens;
    private List<String> palabrasReservadas = Arrays.asList("int", "string", "float", "in", "out", "if", "then",
            "else");
    private List<String> operadores = Arrays.asList("+", "-", "*", "/", "=");
    private List<String> comparadores = Arrays.asList("==", "!=", "<", "<=", ">", ">=");
    private String delimitador = ";";

    private int i;
    private String tipo;

    public Escaner(String codigo) {
        this.tokens = codigo.split("\\s+");
        this.i = 0;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    private boolean esReservada(String token) {
        if (palabrasReservadas.contains(token)) {
            this.tipo = "Palabra reservada";
            return true;
        }
        return false;
    }

    private boolean esOperador(String token) {
        if (operadores.contains(token)) {
            this.tipo = "Operador";
            return true;
        }
        return false;
    }

    private boolean esComparador(String token) {
        if (comparadores.contains(token)) {
            this.tipo = "Operador relacional";
            return true;
        }
        return false;
    }

    private boolean esDelimitador(String token) {
        if (token.equals(delimitador)) {
            this.tipo = "Delimitador";
            return true;
        }
        return false;
    }

    private boolean esIdentificador(String token) {
        if (token.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            this.tipo = "Identificador";
            return true;
        }
        return false;
    }

    private boolean esNumero(String token) {
        if (token.matches("\\d+(\\.\\d+)?")) {
            this.tipo = "Numero";
            return true;
        }
        return false;
    }

    private boolean esEntradaSalida(String token, String siguiente) {
        if (("in".equals(token) && ">".equals(siguiente)) ||
                ("out".equals(token) && "<".equals(siguiente))) {
            this.tipo = "Entrada/Salida";
            return true;
        }
        return false;
    }

    // vericifar si es una cadena con la regla de la gramatica, si el token es un "
    // sigue leyendo hasta encontrar otro " y guarda todo lo que esta dentro de las
    // comillas en un solo token
    // AGREGAR LO ANTERIOR

    private boolean esCadena(String token) {
        if (token.matches("\"[a-zA-Z0-9]*\"")) {
            this.tipo = "Cadena";
            return true;
        }
        return false;
    }

    public String getToken(boolean sigue) {
        if (i >= tokens.length) {
            this.tipo = "Fin de código";
            return "EOF";
        }

        String tokenActual = tokens[i];
        String siguienteToken = (i + 1 < tokens.length) ? tokens[i + 1] : "";

        if (esEntradaSalida(tokenActual, siguienteToken)) {
            if (sigue) {
                i += 2;
            }
            return tokenActual + " " + siguienteToken;
        }

        boolean aceptado = esReservada(tokenActual) ||
                esOperador(tokenActual) ||
                esComparador(tokenActual) ||
                esDelimitador(tokenActual) ||
                esIdentificador(tokenActual) ||
                esNumero(tokenActual) || esCadena(tokenActual);

        if (sigue) {
            i++;
        }

        if (aceptado) {
            return tokenActual;
        } else {
            this.tipo = "Token no válido";
            return "Error";
        }
    }
}
