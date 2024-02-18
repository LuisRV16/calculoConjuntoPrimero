package modelo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class ConjuntoSiguiente extends ConjuntoPrimero {

    public ConjuntoSiguiente() {}

    public ConjuntoSiguiente(String gramatica) {super(gramatica);}

    public String conjuntoSiguienteVariable(String variableAnterior, String variable) {
        String[] temp = variables.toArray(new String[0]);
        String res = "";

        if (variable.equals(inicial))
            res += "$,";

        for (String var : temp) {
            String[] cadenas = cadenasGeneradas.get(var);
            for (String cadena : cadenas) {
                String subcadena = "";
                if (cadena.contains(variable) && !variable.equals(var)) {
                    subcadena = cadena.replace(variable, "|");
                    int index = subcadena.indexOf("|");
                    if (index == subcadena.length() - 1) {
                        if (!variableAnterior.equals(var)) {
                            res += conjuntoSiguienteVariable(variable, var) + ", ";
                        }
                    } else {
                        String preRes = "";
                        if (!(subcadena.charAt(index + 1) + "").equals("'")) {
                            preRes += conjuntoPrimeroCadena(" ", subcadena.substring(index + 1)) + ", ";
                            res += preRes;
                            boolean isEpsilonGenerado = false;
                            for (int i = 0; i < res.length(); i++) {
                                if (res.charAt(i) == 'ε') {
                                    isEpsilonGenerado = true;
                                    break;
                                }
                            }
                            if (isEpsilonGenerado) {
                                if (!variableAnterior.equals(var)) {
                                    res += conjuntoSiguienteVariable(variable, var) + ", ";
                                }
                            }
                        }
                    }
                }
            }
        }

        res = res.replace(" ", "");
        String[] simbolosResultantes = res.split(",");
        LinkedHashSet<String> terminalesResultantes = new LinkedHashSet<>();

        for (String terminal : simbolosResultantes) {
            if (terminales.contains(terminal) || terminal.equals("$"))
                terminalesResultantes.add(terminal);
        }

        res = terminalesResultantes.toString();
        res = res.replace("[", "");
        res = res.replace("]", "");
        return res;
    }

    public String calculoConjuntoSiguiente() {
        almacenarLineasDeGramatica();
        almacenarVariables();
        almacenarCadenas();
        almacernarVariablesRestantes();
        almacenarTerminales();
        eliminarRecursividadIzq();
        setInicial();

        String[] temp = variables.toArray(new String[0]);

        String res = "";
        for (int i = 0; i < temp.length; i++) {
            res += "Siguiente("+temp[i]+") = {"+conjuntoSiguienteVariable("", temp[i]) + "}";
            if (i < temp.length - 1)
                res += "\n";
        }
        return res;
    }

}
