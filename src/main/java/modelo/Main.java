package modelo;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String gramatica =
                """
                        if → (condición)| then |else
                        condición → expr1opexpr2
                        op → >|>= |< |<= |= |==|!=
                        M → Mpq | Mq |p
                        """;

//        System.out.println(gramatica);

        conjuntoPrimero gram = new conjuntoPrimero(gramatica);

        String s = gram.calculoConjuntoPrimero();
        System.out.println(s);
    }
}
