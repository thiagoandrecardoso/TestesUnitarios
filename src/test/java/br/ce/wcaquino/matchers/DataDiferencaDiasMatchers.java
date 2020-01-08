package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Date;

public class DataDiferencaDiasMatchers extends TypeSafeMatcher<Date> {

    private Integer qtdDias;

    public DataDiferencaDiasMatchers(Integer qtdDias) {
        this.qtdDias = qtdDias;
    }

    @Override
    public void describeTo(Description description) {

    }

    @Override
    protected boolean matchesSafely(Date data) {
        System.out.println("DATA " + data.toString());
        return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(qtdDias));
    }
}
