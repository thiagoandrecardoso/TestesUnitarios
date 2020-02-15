package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataDiferencaDiasMatchers extends TypeSafeMatcher<Date> {

    private Integer qtdDias;

    public DataDiferencaDiasMatchers(Integer qtdDias) {
        this.qtdDias = qtdDias;
    }

    @Override
    public void describeTo(Description description) {
        Date dataEsperada = DataUtils.obterDataComDiferencaDias(qtdDias);
        DateFormat format = new SimpleDateFormat("dd/MM/YYYY");
        description.appendText(format.format(dataEsperada));
    }

    @Override
    protected boolean matchesSafely(Date data) {
        return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(qtdDias));
    }
}
