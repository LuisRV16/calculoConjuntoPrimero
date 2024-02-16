package modelo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class ConjuntoSiguiente extends ConjuntoPrimero {

    public ConjuntoSiguiente() {}

    public ConjuntoSiguiente(String gramatica) {super(gramatica);}

    public String conjuntoSiguienteVariable(String variable) {
        String[] temp = variables.toArray(new String[0]);
        String res = "";

        if (variable.equals(inicial))
            res += "$,";

        for (String s : temp) {
            String[] cadenas = cadenasGeneradas.get(s);
            for (String cadena : cadenas) {
                String subcadena = "";
                if (cadena.contains(variable) && !variable.equals(s)) {
                    subcadena = cadena.replace(variable, "|");
                    int index = subcadena.indexOf("|");
                    if (index == subcadena.length() - 1) {
                        res += conjuntoSiguienteVariable(s) + ", ";
                    } else {
                        String preRes = "";
                        if (!(subcadena.charAt(index + 1) + "").equals("'")) {
                            preRes += conjuntoPrimeroCadena(subcadena.substring(index + 1)) + ", ";
                            res += preRes;
                            boolean isEpsilonGenerado = false;
                            for (int i = 0; i < res.length(); i++) {
                                if (res.charAt(i) == 'ε') {
                                    isEpsilonGenerado = true;
                                    break;
                                }
                            }
                            if (isEpsilonGenerado) {
                                res += conjuntoSiguienteVariable(s) + ", ";
                            }
                        }
                    }
                }
            }
        }

        String[] simbolosResultantes = res.split(",");
        res = Arrays.stream(simbolosResultantes).toList().toString();
        LinkedHashSet<String> terminalesResultantes = new LinkedHashSet<>();

        for (int i = 0; i < res.length(); i++) {
            String s = res.charAt(i) + "";
            if (terminales.contains(s) || s.equals("$"))
                terminalesResultantes.add(s);
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
            res += "Siguiente("+temp[i]+") = {"+conjuntoSiguienteVariable(temp[i]) + "}";
            if (i < temp.length - 1)
                res += "\n";
        }
        return res;
    }

}
