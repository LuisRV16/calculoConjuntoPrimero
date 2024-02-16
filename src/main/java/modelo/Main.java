package modelo;

public class Main {
    public static void main(String[] args) {

        String gramatica =
                """
                        E → TE'
                        E' → TE'|ε
                        T → FT'
                        T' → *FT'|ε
                        F → num | (E)
                        """;

//        System.out.println(gramatica);

        ConjuntoSiguiente gram = new ConjuntoSiguiente(gramatica);

        String s = gram.calculoConjuntoSiguiente();
        System.out.println(s);
    }
}
