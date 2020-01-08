package br.ce.wcaquino.suites;

import br.ce.wcaquino.servicos.CalcularValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;
import org.junit.runners.Suite;

//@RunWith(Suite.class)
@Suite.SuiteClasses({
        CalcularValorLocacaoTest.class,
        LocacaoServiceTest.class
})
public class SuiteExecucao {

}
