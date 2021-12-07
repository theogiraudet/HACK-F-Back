package fr.istic.tp.spring.rest;

import java.util.Arrays;

public class Test {

    public static void main(String... args) {
        int a = Arrays.stream("1 3".split(" ")).map(Integer::parseInt).reduce((b, c) -> b * c).orElse(0);
        System.out.println(a);
    }

}
