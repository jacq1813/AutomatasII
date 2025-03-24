import java.util.HashMap;

public class CodIntermedio {
    private String[] tokens;
    private String codigoInt;
    private int contadorEtiqueta = 0;
    private HashMap<String, String> tablaSimbolos = new HashMap<>();

    public CodIntermedio(String codigo) {
        tokens = codigo.split("\\s+");
        codigoInt = "TITLE Programa\n" +
                ".MODEL SMALL\n" +
                ".STACK\n" +
                ".DATA\n";
    }

    public String generarCodigoDeclaraciones() {
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("int") || tokens[i].equals("string") || tokens[i].equals("float")) {
                String tipo = tokens[i];
                i++;
                String nombreVar = tokens[i];
                tablaSimbolos.put(nombreVar, tipo);

                switch (tipo) {
                    case "int":
                        codigoInt += "\t" + nombreVar + " DW ?\n";
                        break;
                    case "string":
                        codigoInt += "\t" + nombreVar + " DB 256 DUP('$')\n";
                        break;
                    case "float":
                        codigoInt += "\t" + nombreVar + " DD ?\n";
                        break;
                }
            }
        }

        codigoInt += ".CODE\n\tMAIN PROC FAR\n\t\t.STARTUP\n";
        return generarCodigoInstrucciones();
    }

    private String generarCodigoInstrucciones() {
        for (int i = 0; i < tokens.length; i++) {

            if (tokens[i].equals("in") && tokens[i + 1].equals(">")) {
                i++;
                i++;

                String tipo = tablaSimbolos.get(tokens[i]);
                switch (tipo) {
                    case "int":
                        codigoInt += "\tMOV AH, 01H\n";
                        codigoInt += "\tINT 21H\n";
                        codigoInt += "\tSUB AL, 30H\n";
                        codigoInt += "\tMOV " + tokens[i] + ", AL\n";
                        break;
                    case "string": // este ya creo
                        codigoInt += "\tMOV AH, 0AH\n";
                        codigoInt += "\tMOV DX, OFFSET " + tokens[i] + "\n";
                        codigoInt += "\tINT 21H\n";
                        break;
                    case "float":
                        codigoInt += "\tMOV AH, 01H\n";
                        codigoInt += "\tINT 21H\n";
                        codigoInt += "\tSUB AL, 30H\n";
                        codigoInt += "\tMOV " + tokens[i] + ", AL\n";
                        break;
                    default:
                        break;
                }

            } else if (tokens[i].equals("out") && tokens[i + 1].equals("<")) {
                i++;
                i++;

                String tipo = tablaSimbolos.get(tokens[i]);

                switch (tipo) {
                    case "int":
                        codigoInt += "\tMOV AH, 02H\n";
                        codigoInt += "\tMOV DL, " + tokens[i] + "\n";
                        codigoInt += "\tINT 21H\n";
                        break;
                    case "string": // este ya creo
                        codigoInt += "\tMOV BX, 001H\n";
                        codigoInt += "\tLEA DX, " + tokens[i] + "\n";
                        codigoInt += "\tMOV AH, 09H\n";
                        codigoInt += "\tINT 21H\n";

                        break;
                    case "float":
                        codigoInt += "\tMOV AH, 02H\n";
                        codigoInt += "\tMOV DL, " + tokens[i] + "\n";
                        codigoInt += "\tINT 21H\n";
                        break;
                    default:
                        break;
                }

            }

            if (tokens[i].equals("if")) {
                i++;
                String op1 = tokens[i]; // Primer operando
                i++;
                String operador = tokens[i]; // Operador
                i++;
                String op2 = tokens[i]; // Segundo operando
                i++;

                String etiquetaElse = "ETQ" + (contadorEtiqueta++);
                String etiquetaFinIf = "FIN_IF" + (contadorEtiqueta++);

                // Generar la comparación
                codigoInt += "\tCMP " + op1 + ", " + op2 + "\n";

                // Generar el salto condicional según el operador
                switch (operador) {
                    case "==":
                        codigoInt += "\tJNE " + etiquetaElse + "\n";
                        break;
                    case "!=":
                        codigoInt += "\tJE " + etiquetaElse + "\n";
                        break;
                    case "<":
                        codigoInt += "\tJGE " + etiquetaElse + "\n";
                        break;
                    case "<=":
                        codigoInt += "\tJG " + etiquetaElse + "\n";
                        break;
                    case ">":
                        codigoInt += "\tJLE " + etiquetaElse + "\n";
                        break;
                    case ">=":
                        codigoInt += "\tJL " + etiquetaElse + "\n";
                        break;
                }

                // Procesar el bloque "then"
                if (tokens[i].equals("then")) {
                    i++; // Avanzamos para procesar las instrucciones del bloque "then"

                    while (i < tokens.length && !tokens[i].equals("else") && !tokens[i].equals(";")) {
                        codigoInt += generarCodigoDesdeToken(i);
                        i++;
                    }
                }

                // Verificar si hay "else"
                if (i < tokens.length && tokens[i].equals("else")) {
                    codigoInt += "\tJMP " + etiquetaFinIf + "\n"; // Saltar al final después del bloque "then"
                    codigoInt += etiquetaElse + ":\n"; // Etiqueta de salto para "else"

                    i++; // Avanzar para procesar el bloque "else"
                    while (i < tokens.length && !tokens[i].equals(";")) {
                        codigoInt += generarCodigoDesdeToken(i);
                        i++;
                    }

                    codigoInt += etiquetaFinIf + ":\n"; // Fin del bloque "if-else"
                } else {
                    // Si no hay "else", solo colocamos la etiqueta para el fin del "if"
                    codigoInt += etiquetaElse + ":\n";
                }
            }

            // Asignaciones
            else if (tokens[i].equals("=") && i > 0) {
                String variable = tokens[i - 1];
                i++;
                String tipo = tablaSimbolos.get(variable);

                if (tipo.equals("string") && tokens[i].startsWith("\"") && tokens[i].endsWith("\"")) {
                    String valor = tokens[i].substring(1, tokens[i].length() - 1);
                    codigoInt += "\tLEA SI, " + variable + "\n";
                    for (int j = 0; j < valor.length(); j++) {
                        codigoInt += "\tMOV BYTE PTR [SI+" + j + "], '" + valor.charAt(j) + "'\n";
                    }
                    codigoInt += "\tMOV BYTE PTR [SI+" + valor.length() + "], '$'\n";
                } else if (tokens[i].matches("\\d+")) {
                    codigoInt += "\tMOV AX, " + tokens[i] + "\n";
                    codigoInt += "\tMOV " + variable + ", AX\n";
                } else if (tablaSimbolos.containsKey(tokens[i])) {
                    codigoInt += "\tMOV AX, " + tokens[i] + "\n";
                    codigoInt += "\tMOV " + variable + ", AX\n";
                }
            }
        }

        codigoInt += ".EXIT\n" +
                "MAIN    ENDP\n" +
                "END";
        return codigoInt;
    }

    private String generarCodigoDesdeToken(int i) {

        String codigo = "";

        if (tokens[i].equals("=") && i > 0) {
            String variable = tokens[i - 1];
            i++;
            String tipo = tablaSimbolos.get(variable);

            if (tipo.equals("string") && tokens[i].startsWith("\"") && tokens[i].endsWith("\"")) {
                String valor = tokens[i].substring(1, tokens[i].length() - 1);
                codigo += "\tLEA SI, " + variable + "\n";
                for (int j = 0; j < valor.length(); j++) {
                    codigo += "\tMOV BYTE PTR [SI+" + j + "], '" + valor.charAt(j) + "'\n";
                }
                codigo += "\tMOV BYTE PTR [SI+" + valor.length() + "], '$'\n";
            } else if (tokens[i].matches("\\d+")) {
                codigo += "\tMOV AX, " + tokens[i] + "\n";
                codigo += "\tMOV " + variable + ", AX\n";
            } else if (tablaSimbolos.containsKey(tokens[i])) {
                codigo += "\tMOV AX, " + tokens[i] + "\n";
                codigo += "\tMOV " + variable + ", AX\n";
            }

            if (tokens[i].matches("\\d+\\.\\d+")) {

                i++;
                // Verificamos si el token actual es una operación aritmética
                switch (tokens[i]) {
                    case "+":

                        String op1 = tokens[i - 1];
                        i++;
                        String op2 = tokens[i];
                        codigo += "\tFLD " + op1 + "\n"; // Cargar el primer operando en FPU
                        codigo += "\tFADD " + op2 + "\n"; // Sumar el segundo operando
                        codigo += "\tFSTP " + variable + "\n"; // Guardar el resultado en la variable
                        break;
                    case "-":

                        op1 = tokens[i - 1];
                        i++;
                        op2 = tokens[i];
                        codigo += "\tFLD " + op1 + "\n"; // Cargar el primer operando en FPU
                        codigo += "\tFSUB " + op2 + "\n"; // Restar el segundo operando
                        codigo += "\tFSTP " + variable + "\n"; // Guardar el resultado en la variable
                        break;
                    case "*":

                        op1 = tokens[i - 1];
                        i++;
                        op2 = tokens[i];
                        codigo += "\tFLD " + op1 + "\n"; // Cargar el primer operando en FPU
                        codigo += "\tFMUL " + op2 + "\n"; // Multiplicar por el segundo operando
                        codigo += "\tFSTP " + variable + "\n"; // Guardar el resultado en la variable
                        break;
                    case "/":

                        op1 = tokens[i - 1];
                        i++;
                        op2 = tokens[i];
                        codigo += "\tFLD " + op1 + "\n"; // Cargar el primer operando en FPU
                        codigo += "\tFDIV " + op2 + "\n"; // Dividir por el segundo operando
                        codigo += "\tFSTP " + variable + "\n"; // Guardar el resultado en la variable
                        break;
                    default:

                        break;
                }
            }

            if (tokens[i].matches("\\d+")) {
                i++;

                String op1 = "", op2 = "";
                // Verificamos si el token actual es una operación aritmética
                switch (tokens[i]) {
                    case "+":

                        op1 = tokens[i - 1];
                        i++;
                        op2 = tokens[i];
                        codigo += "\tMOV AX, " + op1 + "\n"; // Cargar el primer operando en AX
                        codigo += "\tADD AX, " + op2 + "\n"; // Sumar el segundo operando
                        codigo += "\tMOV " + variable + ", AX\n"; // Guardar el resultado en la variable
                        break;
                    case "-":

                        op1 = tokens[i - 1];
                        i++;
                        op2 = tokens[i];
                        codigo += "\tMOV AX, " + op1 + "\n"; // Cargar el primer operando en AX
                        codigo += "\tSUB AX, " + op2 + "\n"; // Restar el segundo operando
                        codigo += "\tMOV " + variable + ", AX\n"; // Guardar el resultado en la variable
                        break;
                    case "*":

                        op1 = tokens[i - 1];
                        i++;
                        op2 = tokens[i];
                        codigo += "\tMOV AX, " + op1 + "\n"; // Cargar el primer operando en AX
                        codigo += "\tIMUL AX, " + op2 + "\n"; // Multiplicar por el segundo operando
                        codigo += "\tMOV " + variable + ", AX\n"; // Guardar el resultado en la variable
                        break;
                    case "/":

                        op1 = tokens[i - 1];
                        i++;
                        op2 = tokens[i];
                        codigo += "\tMOV AX, " + op1 + "\n"; // Cargar el primer operando en AX
                        codigo += "\tMOV BX, " + op2 + "\n"; // Cargar el divisor en BX
                        codigo += "\tDIV BX\n"; // Dividir AX por BX
                        codigo += "\tMOV " + variable + ", AX\n"; // Guardar el cociente en la variable
                        break;
                    default:
                        // Si no es una operación válida, puedes agregar un manejo de error aquí

                        break;
                }
            }

            if (tokens[i].equals("in") && tokens[i + 1].equals(">")) {
                codigoInt += "tambien aqui";
                i++;
                i++;

                tipo = tablaSimbolos.get(tokens[i]);
                switch (tipo) {
                    case "int":
                        codigoInt += "\tMOV AH, 01H\n";
                        codigoInt += "\tINT 21H\n";
                        codigoInt += "\tSUB AL, 30H\n";
                        codigoInt += "\tMOV " + tokens[i] + ", AL\n";
                        break;
                    case "string": // este ya creo
                        codigoInt += "\tMOV AH, 0AH\n";
                        codigoInt += "\tMOV DX, OFFSET " + tokens[i] + "\n";
                        codigoInt += "\tINT 21H\n";
                        break;
                    case "float":
                        codigoInt += "\tMOV AH, 01H\n";
                        codigoInt += "\tINT 21H\n";
                        codigoInt += "\tSUB AL, 30H\n";
                        codigoInt += "\tMOV " + tokens[i] + ", AL\n";
                        break;
                    default:
                        break;
                }

            } else if (tokens[i].equals("out") && tokens[i + 1].equals("<")) {
                i++;
                i++;

                tipo = tablaSimbolos.get(tokens[i]);

                switch (tipo) {
                    case "int":
                        codigoInt += "\tMOV AH, 02H\n";
                        codigoInt += "\tMOV DL, " + tokens[i] + "\n";
                        codigoInt += "\tINT 21H\n";
                        break;
                    case "string": // este ya creo
                        codigoInt += "\tMOV BX, 001H\n";
                        codigoInt += "\tLEA DX, " + tokens[i] + "\n";
                        codigoInt += "\tMOV AH, 09H\n";
                        codigoInt += "\tINT 21H\n";

                        break;
                    case "float":
                        codigoInt += "\tMOV AH, 02H\n";
                        codigoInt += "\tMOV DL, " + tokens[i] + "\n";
                        codigoInt += "\tINT 21H\n";
                        break;
                    default:
                        break;
                }

            }

        }
        return codigo;
    }
}
