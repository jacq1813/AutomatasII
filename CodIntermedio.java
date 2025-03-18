public class CodIntermedio {

    private String[] tokens;
    private String codigoInt = "TITLE Programa\n" + //
            ".MODEL SMALL\n" + //
            ".STACK\n" + //
            ".DATA\n" + //
            "";
    Escaner es;

    public CodIntermedio(String codigo) {

        this.tokens = codigo.split("\\s+");
    }

    public String generarCodigoDeclaraciones() {

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("int") || tokens[i].equals("string") || tokens[i].equals("float")) {

                switch (tokens[i]) {
                    case "int":
                        i++;
                        codigoInt += "" + tokens[i];
                        codigoInt += " DW ?" + "\n";
                        i++;
                        break;
                    case "string":
                        i++;
                        codigoInt += tokens[i];
                        codigoInt += " DB 256 DUP(0DH,'$')" + "\n";
                        i++;
                        break;

                    case "float":
                        i++;
                        codigoInt += "" + tokens[i];
                        codigoInt += " DD ?" + "\n";
                        i++;
                        break;

                    default:
                        break;
                }
            }
        }

        System.out.println("Código intermedio de declaraciones: ");
        System.out.println(codigoInt);
        return codigoInt;
    }

}
