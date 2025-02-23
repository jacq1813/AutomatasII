// LENGUAJES Y AUTÓMATAS II                                                     
// JACQUELIN ROBLES RIOS
// 12 PM - 01 PM  

import java.util.List;
import java.util.ArrayList;

public class Parser {

    private Escaner escaner;
    private String token;

    // lista de tokens que guarda el token y el error (si lo hay)
    List<String> listat = new ArrayList<>();
    private List<String> listaErrores = new ArrayList<>();

    private final String rInt = "int", rFloat = "float", rString = "string", rID = "Identificador", rEnt = "in >",
            rSal = "out <", rif = "if", rThen = "then", rElse = "else", rSuma = "+", rResta = "-", rMult = "*",
            rDiv = "/", rIgual = "=", rIgualdad = "==", rDiferente = "!=", rMenor = "<", rMenorIgual = "<=",
            rMayor = ">", rMayorIgual = ">=", rDelimitador = ";", rNum = "Numero decimal", rNumn = "Numero",
            rCadena = "Cadena";

    public Parser(String codigo) {
        try {
            if (codigo.isEmpty()) {
                throw new RuntimeException("Error: el código está vacío.");
            } else {
                this.escaner = new Escaner(codigo);
                avanza();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void avanza() {
        this.token = escaner.getToken(true);
        listat.add(token); // Agregar el token a la lista
    }

    public void Inicia() {

        listaErrores.add("Inicio del análisis sintáctico.");
        P();
        if (!token.equals("EOF")) {
            listaErrores.add("Error: se esperaba EOF");
        }
    }

    public void P() {
        D();
        S();

    }

    public void D() {

        while (token.equals(rInt) || token.equals(rString) || token.equals(rFloat)) {
            avanza();
            if (!escaner.getTipo().equals(rID)) {
                listaErrores.add("Error: Se esperaba un identificador después del tipo de dato.");
            } else {
                listaErrores.add("Correcto: Identificador después del tipo de dato.");
            }
            avanza();
            if (!token.equals(rDelimitador)) {
                listaErrores.add("Error: Se esperaba ';' al final de la declaración.");
            } else {
                listaErrores.add("Correcto: ';' al final de la declaración.");
            }
            avanza();
        }
    }

    public void S() {
        switch (token) {
            case rif:
                listaErrores.add("Correcto: 'if' encontrado.");

                avanza();
                C();

                if (!token.equals(rThen)) {
                    listaErrores.add("Error: Se esperaba 'then' después de la condición.");
                } else {
                    listaErrores.add("Correcto: 'then' después de la condición.");
                }
                avanza();
                S();
                if (token.equals(rElse)) {
                    avanza();
                    S();
                }
                break;

            case rEnt:
                listaErrores.add("Correcto: 'in >' encontrado.");
                avanza();
                if (!escaner.getTipo().equals(rID)) {
                    listaErrores.add("Error: Se esperaba un identificador después de 'in >'.");
                } else {
                    listaErrores.add("Correcto: Identificador después de 'in >'.");
                }
                avanza();
                if (!token.equals(rDelimitador)) {
                    listaErrores.add("Error: Se esperaba ';' después de la entrada.");
                } else {
                    listaErrores.add("Correcto: ';' después de la entrada.");
                }
                avanza();
                break;

            case rSal:
                listaErrores.add("Correcto: 'out <' encontrado.");
                avanza();
                E();
                if (!token.equals(rDelimitador)) {
                    listaErrores.add("Error: Se esperaba ';' después de la salida.");
                } else {
                    listaErrores.add("Correcto: ';' después de la salida.");
                }
                avanza();
                break;

            case "EOF":
                break;

            default:

                if (escaner.getTipo().equals(rID)) {

                    listaErrores.add("Correcto: Identificador encontrado.");
                    avanza();
                    if (!token.equals(rIgual)) {
                        listaErrores.add("Error: Se esperaba '=' después del identificador.");
                    } else {
                        listaErrores.add("Correcto: '=' después del identificador.");
                    }
                    avanza();
                    E();
                    if (!token.equals(rDelimitador)) {
                        listaErrores.add("Error: Se esperaba ';' al final de la asignación.");
                    } else {
                        listaErrores.add("Correcto: ';' al final de la asignación.");
                    }
                    avanza();
                    break;
                } else {
                    listaErrores.add("Error: Se esperaba una sentencia válida.");
                }
        }
    }

    public void E() {

        if (escaner.getTipo().equals(rID) || escaner.getTipo().equals(rNum)) {

            listaErrores.add("Correcto: Identificador o número encontrado.");
            avanza();

            if (token.equals(rIgualdad) || token.equals(rDiferente) || token.equals(rMenor) ||
                    token.equals(rMenorIgual) || token.equals(rMayor) || token.equals(rMayorIgual)) {
                return; // Salir de E() para que C() maneje la comparación
            }

            while (token.equals(rSuma) || token.equals(rResta) || token.equals(rMult) || token.equals(rDiv)) {
                listaErrores.add("Correcto: Operador aritmético encontrado.");
                avanza();
                if (!escaner.getTipo().equals(rID) && !escaner.getTipo().equals(rNum)) {
                    listaErrores.add("Error: Se esperaba un identificador o número.");
                } else {
                    listaErrores.add("Correcto: Identificador o número válido.");
                }
                avanza();
            }
            return;
        }
        if (escaner.getTipo().equals(rCadena)) {
            listaErrores.add("Correcto: Cadena encontrada.");
            avanza();
            return;
        } else {
            listaErrores.add("Error: Se esperaba una expresión válida.");
        }
    }

    public void C() {
        E();
        if (!token.equals(rIgualdad) && !token.equals(rDiferente) && !token.equals(rMenor) &&
                !token.equals(rMenorIgual) && !token.equals(rMayor) && !token.equals(rMayorIgual)) {
            listaErrores.add("Error: Se esperaba un operador relacional.");
        } else {
            listaErrores.add("Correcto: Operador relacional válido.");
        }
        avanza();
        E();

        return;
    }

    public List<String> getListaErrores() {
        return listaErrores;
    }

    public List<String> getListaTokens() {
        return listat;
    }
}
