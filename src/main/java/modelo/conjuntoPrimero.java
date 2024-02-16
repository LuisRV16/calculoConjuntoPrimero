package modelo;

import java.util.ArrayList;

public class conjuntoPrimero extends AnalizadorDeGramatica {

    public conjuntoPrimero() {super();}

    public conjuntoPrimero(String gramatica) {super(gramatica);}

    protected boolean isRecursivoIzq(String v, String[] cadenas) {
        for (String cadena : cadenas) {
            if (cadena.length() >= v.length()) {
                String subcadena = cadena.substring(0, v.length());
                if (subcadena.equals(v)) return true;
            }
        }
        return false;
    }

    public int eliminarRecursividadIzq() {
        int status = 1;
        // Si status es 1, entonces se realizo la eliminacion de la recursividad a la izquierda
        // Si status es 0, entonces el tipo de gramatica es A → A|Bn
        // Si status es -1, entonces el tipo de gramatica es A → A
        String[] temp = variables.toArray(new String[0]);
        for (int i = 0; i < temp.length; i++) {
             String[] cadenas = cadenasGeneradas.get(temp[i]);
             if (isRecursivoIzq(temp[i], cadenas)){
                 ArrayList<String> an = new ArrayList<>();
                 ArrayList<String> Bn = new ArrayList<>();
                 for (String cadena : cadenas) {
                     if (cadena.length() >= temp[i].length()) {
                         if (!cadena.equals(temp[i])) {
                             String subcadena = cadena.substring(0, temp[i].length());
                             if (subcadena.equals(temp[i])) {
                                 subcadena = cadena.substring(temp[i].length(), cadena.length());
                                 an.add(subcadena);
                             } else {
                                 Bn.add(cadena);
                             }
                         } else {
                             an.add("");
                         }
                     } else {
                         Bn.add(!cadena.equals("ε") ? cadena : "");
                     }
                 }
                 if (an.contains("") && an.size() > 1) {
                     an.remove("");
                 } else if (an.contains("") && Bn.isEmpty()) {
                     status = -1;
                 } else if (an.contains("") && an.size() == 1) {
                     status = 0;
                 }

                 if (status == 1) {
                     variables.add(temp[i]+"'");
                     String[] bn = {temp[i] + "'"};
                     an.add("ε");
                     String[] am = an.toArray(new String[0]);

                     if (!Bn.isEmpty()) {
                         bn = Bn.toArray(new String[0]);
                         for (int j = 0; j < bn.length; j++) {
                             bn[j] = bn[j] + temp[i] + "'";
                         }
                     }

                     for (int j = 0; j < am.length; j++) {
                         if (j < am.length - 1)
                            am[j] = am[j] + temp[i] + "'";
                     }

                     cadenasGeneradas.put(temp[i], bn);
                     cadenasGeneradas.put(temp[i]+"'", am);
                 }
             }
        }
        return status;
    }

    private String conjuntoPrimeroCadena (String cadena) {
        String[] var = variables.toArray(new String[0]);
        String[] term = terminales.toArray(new String[0]);
        String subcadena = "";
        boolean isVar = false;
        if (!cadena.equals("[]")) {
            for (int i = 0; i < var.length; i++) {
                if (cadena.length() >= 2) {
                    subcadena = cadena.substring(0, 2);
                    if (variables.contains(subcadena)) {
                        isVar = true;
                        break;
                    }
                }
                if (cadena.length() >= var[i].length()) {
                    subcadena = cadena.substring(0, var[i].length());
                    if (subcadena.equals(var[i])) {
                        isVar = true;
                        break;
                    }
                }
            }

            if (!isVar) {
                for (int i = 0; i < term.length; i++) {
                    if (cadena.length() >= term[i].length()) {
                        subcadena = cadena.substring(0, term[i].length());
                        if (subcadena.equals(term[i])) break;
                    }
                }
            } else {
                String[] cadenas = cadenasGeneradas.get(subcadena);
                subcadena = "";
                for (int i = 0; i < cadenas.length; i++) {
                    subcadena += (conjuntoPrimeroCadena(cadenas[i]) + ", ");
                }
            }
        } else {
            subcadena = cadena;
        }
        return subcadena;

    }

    public String calculoConjuntoPrimero() {

        StringBuilder resultadoCompleto = new StringBuilder();

        resultadoCompleto.append("Gramatica ingresada: \n").append(gramatica).append("\n");
        almacenarLineasDeGramatica();
        almacenarVariables();
        almacenarCadenas();
        almacernarVariablesRestantes();
        almacenarTerminales();
        setInicial();

        resultadoCompleto.append("V = {").append(getVariables()).append("}\n").append("T = {").append(getTerminales())
                .append("}\n").append("S = ").append(getInicial()).append("\n");

        String s = resultadoCompleto.toString().replace("[", "").replace("]", "");

        resultadoCompleto = new StringBuilder(s);

        int n = eliminarRecursividadIzq();

        if (n == 0) {
            resultadoCompleto.append("\n\n").append("El tipo de gramatica es A → A|Bn, no se puede resolver");
        } else if (n == -1) {
            resultadoCompleto.append("\n\n").append("El tipo de gramatica es A → A, no se puede resolver");
        } else {
            resultadoCompleto.append("\nGramatica despues del analisis de recursividad a la izquiera: \n").
                    append(getGramaticaCompleta()).append("\n");

            String[] var = variables.toArray(new String[0]);
            for (int i = 0; i < var.length; i++) {
                String[] cadenas = cadenasGeneradas.get(var[i]);
                for (int j = 0; j < cadenas.length; j++) {
                    String cadena = conjuntoPrimeroCadena(cadenas[j]);
                    if (cadena.contains(", ,")) {
                        cadena = cadena.replaceAll(", ,", ",");
                    }
                    if (cadena.length() >= 3){
                        String s1 = cadena.substring(cadena.length() - 2, cadena.length());;
                        if (s1.equals(", ")) {
                            cadena = cadena.substring(0, cadena.length() - 2);
                        }
                    }
                    cadena = "{"+cadena+"}";
                    resultadoCompleto.append("\n").append("Primera(").append(cadenas[j]).append(") = ")
                            .append(cadena);
                }
            }
        }

        return resultadoCompleto.toString().replace("[]", " ");
    }

}
