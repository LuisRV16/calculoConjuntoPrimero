package modelo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorDeGramatica {

    protected String gramatica;
    protected LinkedHashSet<String> variables;
    protected LinkedHashSet<String> terminales;
    protected HashMap<String, String[]> cadenasGeneradas;
    protected String inicial;
    protected boolean mayusApost;
    protected String[] lineasDeGramatica;

    public AnalizadorDeGramatica(){
        inic();
    }
    public AnalizadorDeGramatica(String gramatica) {
        setGramatica(gramatica);
        inic();
    }

    private void inic() {
        variables = new LinkedHashSet<>();
        terminales = new LinkedHashSet<>();
        cadenasGeneradas = new HashMap<>();
    }

    public void setGramatica(String gramatica){
        this.gramatica = gramatica;
        eliminarEspacios();
    }

    public String getGramatica(){return gramatica;}

    public LinkedHashSet<String> getVariables() {return variables;}

    public void setVariables(LinkedHashSet<String> variables) {this.variables = variables;}

    public LinkedHashSet<String> getTerminales() {return terminales;}

    public void setTerminales(LinkedHashSet<String> terminales) {this.terminales = terminales;}

    public String getInicial() {return inicial;}

    public void setInicial() {inicial = variables.getFirst();}

    public HashMap<String, String[]> getCadenasGeneradas() {
        return cadenasGeneradas;
    }

    public String getGramaticaCompleta() {
        String s = "";
        String[] var = variables.toArray(new String[0]);
        for (int i = 0; i < var.length; i++) {
            s += var[i] + " → ";
            String[] cadenas = cadenasGeneradas.get(var[i]);
            for (int j = 0; j < cadenas.length; j++) {
                s += cadenas[j];
                if (j < cadenas.length - 1)
                    s += " | ";
            }
            if (i < var.length - 1)
                s += "\n";
        }
        return s;
    }

    // Elimina los espacios en blanco reemplazando los espacios en blanco por cadenas vacias.
    private void eliminarEspacios(){gramatica = gramatica.replaceAll("[ \\t]", "");}

    // Separa las lineas de la gramatica en un arreglo cada que hay un salto de linea
    public void almacenarLineasDeGramatica(){
        lineasDeGramatica = gramatica.split("\n");
    }

    public void almacenarVariables() {
        for (String linea: lineasDeGramatica) {
            int posicion = linea.indexOf("→"); // Obtiene la posicion de la cadena en la que se encuentra el simbolo

            // Extrae la parte de la cadena que se considera variable poniendo como limite el simbolo de flecha
            String variable = linea.substring(0, posicion);
            variables.add(variable);
        }
    }

    public void almacenarCadenas(){
        String[] temp = variables.toArray(new String[0]); // Ingreso las variables a un arreglo temporal
        for (int i = 0; i < lineasDeGramatica.length; i++) {
            String linea = lineasDeGramatica[i];
            int posicion = linea.indexOf("→"); // Obtiene la posicion de la cadena en la que se encuentra el simbolo de flecha
            String cadenas = linea.substring(posicion + 1); // Extra las cadenas que genera una variable
            String[] cadenasGener = cadenas.split("\\|"); // Extrae cada cadena de forma individual y las almacena
            // Se ingresan las cadenas que puede generar cada variable dentro de un HashMap que tiene como llave esa variable
            cadenasGeneradas.put(temp[i], cadenasGener);
        }
    }

    public void almacernarVariablesRestantes(){
        String[] temp = variables.toArray(new String[0]); // Ingreso las variables a un arreglo temporal
        for (int i = 0; i < temp.length; i++) { // Recorre la lista de cadenas generadas por cada variable
            String[] cadenas = cadenasGeneradas.get(temp[i]); // Obtiene las cadenas generadas por cada variable
            for (int j = 0; j < cadenas.length; j++) { // Recorre cada una de las cadenas
                if (verificarCadena(cadenas[j])) { // Analiza si hay una letra mayuscula seguida opcionalmente con apostrofe
                    // Lee la cadena encontrando todas las coincidencias del tipo "A" o "A'" para guardar en el conjunto
                    // de variables la letra encontrada
                    extraerVariables(cadenas[j]); // extrae las variables dentro de la cadena
                }
            }

            for (int j = 0; j < cadenas.length; j++) { // Recorre cada una de las cadenas
                // Analiza si hay una letra mayuscula seguida por un apostrofe
                mayusApost = verificarCadenaMayusApost(cadenas[j]);
                if (mayusApost) break;
            }
        }
    }

    private boolean verificarCadena(String cadena) {
        // Expresión regular: al menos una letra mayúscula o una letra mayúscula seguida por un apóstrofe
        String regex = "[A-Z]('|[A-Z])?";
        Pattern patron = Pattern.compile(regex);
        Matcher matcher = patron.matcher(cadena);

        return matcher.find();
    }

    private boolean verificarCadenaMayusApost(String cadena) {
        // Expresión regular: al menos una letra mayúscula seguida por un apóstrofe
        String regex = "^[A-Z]'";
        Pattern patron = Pattern.compile(regex);
        Matcher matcher = patron.matcher(cadena);

        return matcher.find();
    }

    // Almacena todas las letras mayusculas opcionalmente seguidas de un apostrofe
    // Esto es porque es la representacion de las variables dentro de las cadenas generadas en una gramatica
    private void extraerVariables(String cadena) {
        Pattern patron = Pattern.compile("[A-Z](?:')?");
        Matcher matcher = patron.matcher(cadena);

        /*
          Se almacenara solo un vacio en caso de encontrar una variable dentro de las cadenas
          que no haya sido encontrada en el metodo almacenarVariables
        */
        String[] aux = {"[]"};

        while (matcher.find()) {
            // Se almacenara solo un vacio en caso de encontrar una variable dentro de las cadenas
            if (variables.add(matcher.group()))
                cadenasGeneradas.put(variables.getLast(), aux); // Guarda la variable con su respectivo vacio
        }
    }

    // Hasta aquí finaliza todo lo de variables
    // Inician las terminales

    // Almacenar terminables: 40 lineas, mi record | Actualizacion 15/02/2024: ahora son 53 pipipipi pero funciona al maximo
    public void almacenarTerminales () {
        String[] temp = variables.toArray(new String[0]);
        ArrayList <String> cadenas = todasLasCadenas();

        if (mayusApost)
            temp = ordenamiento(temp);

        String subcadena = "";
        for (int i = 0; i < cadenas.size(); i++) {
            String cadena = cadenas.get(i);

            for (int j = 0; j < temp.length; j++) {
                if (cadena.contains(temp[j]))
                    cadena = cadena.replace(temp[j], "|");
            }
            cadena += "|";

            subcadena += cadena;
        }
        String[] subcadenas = subcadena.split("\\|");
        HashSet<String> subcadenasSinRep = new HashSet<>(Arrays.asList(subcadenas));
        subcadenas = subcadenasSinRep.toArray(new String[0]);

        ArrayList<String> cadenasDeUnSimbolo = new ArrayList<>();
        ArrayList<String> cadenasDeVarioSimbolos = new ArrayList<>();

        for (int i = 0; i < subcadenas.length; i++){
            subcadena = subcadenas[i];
            if (subcadena.length() == 1) {
                if (!(subcadena.equals("ε") || subcadena.equals("'"))) {
                    cadenasDeUnSimbolo.add(subcadena);
                    terminales.add(subcadena);
                }
            } else if (subcadena.length() > 1) {
                cadenasDeVarioSimbolos.add(subcadena);
            }
        }
        if (cadenasDeVarioSimbolos.size() > 0) {
            for (int i = 0; i < cadenasDeVarioSimbolos.size(); i++) {
                subcadena = cadenasDeVarioSimbolos.get(i);
                for (String simbolo : cadenasDeUnSimbolo) {
                    if (subcadena.contains(simbolo))
                        subcadena = subcadena.replace(simbolo, "|");
                }
                String[] terminalesApartadas = subcadena.split("\\|");

                for (String terminal: terminalesApartadas) {
                    if (!terminal.equals(""))
                        terminales.add(terminal);
                }
            }
        }
    }

    private String[] ordenamiento(String[] variables) {
        int j = 0;
        String[] temp = new String[variables.length];
        for (int i = 0; i < variables.length; i++) {
            if (verificarCadenaMayusApost(variables[i])) {
                temp[j] = variables[i];
                j++;
            }
        }

        for (int i = 0; i < variables.length; i++) {
            if (!verificarCadenaMayusApost(variables[i])) {
                temp[j] = variables[i];
                j++;
            }
        }

        return temp;
    }

    private ArrayList<String> todasLasCadenas() {
        ArrayList<String> cadenas = new ArrayList<>();
        String[] temp = variables.toArray(new String[0]);
        for (int i = 0; i < temp.length; i++) {
            String[] cadenasPorVar = cadenasGeneradas.get(temp[i]);
            for (int j = 0; j < cadenasPorVar.length; j++) {
                if (!cadenasPorVar[j].equals("[]"))
                    cadenas.add(cadenasPorVar[j]);
            }
        }
        return cadenas;
    }
}
