package modelo;

public class Main {
    public static void main(String[] args) {
        String gramatica =
                """
                        S → baBA|a
                        A → Sb|ε
                        B → abba
                        """;

        ConjuntoSiguiente gram = new ConjuntoSiguiente(gramatica);

        String s = gram.calculoConjuntoSiguiente();
        System.out.println(s);
    }
}
