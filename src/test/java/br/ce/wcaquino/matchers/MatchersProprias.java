package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprias {
    public static DiaSemanaMatchers caiEm(Integer diaSemana){
        return new DiaSemanaMatchers(diaSemana);
    }

    public static DiaSemanaMatchers caiNumaSegunda(){
        return new DiaSemanaMatchers(Calendar.MONDAY);
    }
}
