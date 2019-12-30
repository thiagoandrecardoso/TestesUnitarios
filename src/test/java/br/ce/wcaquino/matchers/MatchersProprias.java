package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprias {
    public static DiaSemanaMatchers caiEm(Integer diaSemana) {
        return new DiaSemanaMatchers(diaSemana);
    }

    public static DiaSemanaMatchers caiNumaSegunda() {
        return new DiaSemanaMatchers(Calendar.MONDAY);
    }

    public static DataDiferencaDiasMatchers ehHojeComDiferencaDias(Integer qtsDias) {
        return new DataDiferencaDiasMatchers(qtsDias);
    }

    public static DataDiferencaDiasMatchers ehHoje() {
        return new DataDiferencaDiasMatchers(0);
    }
}
