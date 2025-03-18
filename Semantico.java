// JACQUELIN ROBLES RIOS 12 PM - 01 PM  

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
        for (i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (esTipoDato(token)) {
                List<String> declaracion = new ArrayList<>();

                declaracion.add(token); // Tipo de dato
                if (++i < tokens.size()) {
                    String identificador = tokens.get(i); // Identificador
                    declaracion.add(identificador);

                    boolean yaDeclarado = false;
                    for (List<String> decl : declaraciones) {
                        if (decl.get(1).equals(identificador)) {
                            yaDeclarado = true;
                            errores.add(
                                    "Error: La variable '" + identificador + "' ya ha sido declarada anteriormente.");
                            break;
                        }
                    }
                }

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

            if (esIdentificador(escaner) && !(tokens.get(i - 1).equals("then") || tokens.get(i - 1).equals("else"))) {
                procesarAsignacion(tokenActual);
            } else if (tokens.get(i).equals("in >")) {
                procesarEntrada(tokenActual);
            } else if (tokens.get(i).equals("out <")) {
                procesarSalida(tokenActual);
            } else if (tokens.get(i).equals("if") || escaner.getTipo().equals("Operador relacional")) {
                procesarCondicional();
            } else if ((tokens.get(i).equals("else") || tokens.get(i).equals("then"))) {
                procesarElseThen();
            } else {
                i++;
            }
        }
    }

    private void procesarAsignacion(String id) {
        if (++i < tokens.size() && tokens.get(i).equals("=")) {
            if (++i < tokens.size()) {
                String valor = tokens.get(i);
                Escaner e = new Escaner(tokens.get(i + 1));
                e.getToken(true);

                while (e.getTipo().equals("Operador")) {
                    i++;
                    String ope = tokens.get(i);
                    e = new Escaner(tokens.get(i + 1));
                    e.getToken(true);

                    if (e.getTipo().equals("Numero")) {
                        switch (ope) {
                            case "+":
                                e = new Escaner(tokens.get(i + 1));
                                e.getToken(true);
                                String tipo = e.getTipo();

                                if (tipo.equals("Numero")) {
                                    valor = String
                                            .valueOf(Integer.parseInt(valor) + Integer.parseInt(tokens.get(i + 1)));
                                } else {
                                    errores.add(
                                            "Error: Operación no válida, no se pueden sumar un número con una cadena.");
                                }
                                i++;
                                break;
                            case "-":
                                e = new Escaner(tokens.get(i + 1));
                                e.getToken(true);
                                tipo = e.getTipo();

                                if (tipo.equals("Numero")) {
                                    valor = String
                                            .valueOf(Integer.parseInt(valor) + Integer.parseInt(tokens.get(i + 1)));
                                } else {
                                    errores.add(
                                            "Error: Operación no válida, no se pueden sumar un número con una cadena.");
                                }
                                i++;
                                break;
                            case "*":
                                e = new Escaner(tokens.get(i + 1));
                                e.getToken(true);
                                tipo = e.getTipo();

                                if (tipo.equals("Numero")) {
                                    valor = String
                                            .valueOf(Integer.parseInt(valor) + Integer.parseInt(tokens.get(i + 1)));
                                } else {
                                    errores.add(
                                            "Error: Operación no válida, no se pueden sumar un número con una cadena.");
                                }
                                i++;
                                break;
                            case "/":
                                e = new Escaner(tokens.get(i + 1));
                                e.getToken(true);
                                tipo = e.getTipo();

                                if (tipo.equals("Numero")) {
                                    valor = String
                                            .valueOf(Integer.parseInt(valor) + Integer.parseInt(tokens.get(i + 1)));
                                } else {
                                    errores.add(
                                            "Error: Operación no válida, no se pueden sumar un número con una cadena.");
                                }
                                i++;
                                break;
                            default:
                                break;
                        }
                    } else {
                        errores.add("Error: Operación no válida.");
                        valor = "";
                    }
                    e = new Escaner(tokens.get(i + 1));
                    e.getToken(true);
                }

                String tipo = obtenerTipoDeclaracion(id);

                if (tipo != null) {
                    validarCompatibilidad(id, tipo, valor);
                    BuscarDeclaraciones(id, valor);
                } else {
                    errores.add("Error: La variable " + id + " no está declarada.");
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
            obtenerTipoDeclaracion(valor);
        }
        i++;
    }

    private void procesarSalida(String id) {
        if (++i < tokens.size()) {
            String valor = tokens.get(i);
        }
        i++;
    }

    private void procesarCondicional() {
        if (++i < tokens.size()) {
            String condicion = tokens.get(i);
            Escaner e = new Escaner(condicion);
            e.getToken(true);

            if (e.getTipo().equals("Identificador")) {
                String tipo = obtenerTipoDeclaracion(condicion);
                if (tipo == null) {
                    errores.add("Error: La variable " + condicion + " no está declarada.");
                }
            }
        }
        i++;
    }

    private void procesarElseThen() {
        if (++i < tokens.size()) {
            String ife = tokens.get(i);
            Escaner e = new Escaner(ife);
            e.getToken(true);

            if (e.getTipo().equals("Identificador")) {
                String tipo = obtenerTipoDeclaracion(ife);
                validarCompatibilidad(ife, tipo, tokens.get(i + 2));
            }
            i++;
        }
    }

    private void validarCompatibilidad(String id, String tipo, String valor) {
        if (tipo != null) {
            Escaner escaner = new Escaner(valor);
            escaner.getToken(true);
            if (!esCompatible(tipo, escaner.getTipo())) {
                errores.add("Error: Asignación de " + valor + "incompatible para " + id);
            }
        } else {
            errores.add("Error: La variable " + id + " no está declarada.");
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