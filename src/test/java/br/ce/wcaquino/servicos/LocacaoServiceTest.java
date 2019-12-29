package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.matchers.DiaSemanaMatchers;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.DiagnosingMatcher;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import sun.plugin2.main.client.CALayerProvider;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.matchers.MatchersProprias.caiEm;
import static br.ce.wcaquino.matchers.MatchersProprias.caiNumaSegunda;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private LocacaoService service;

    /**
     * Before roda antes de cada teste
     * O método sempre inicializa o que for repassado no ponto de início
     * assim, a variável contador sempre ficará com o valor 1
     */
    @Before
    public void setUp() {
        service = new LocacaoService();
    }

    @Test
    public void verificaValoresDelocacaoData_precos() throws Exception {
        //cenario
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 2, 5.0);
        Filme filme2 = new Filme("Filme 2", 2, 5.0);
        Filme filme3 = new Filme("Filme 3", 2, 5.0);
        List<Filme> listaFilmes = Arrays.asList(filme1, filme2, filme3);
        //acao
        Locacao locacao = null;
        locacao = service.alugarFilme(usuario, listaFilmes);
        //verificacao
        error.checkThat(locacao.getValor(), is(equalTo(13.75)));
        error.checkThat(locacao.getValor(), is(not(6.0)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void deveLancarExcecaoAoLocarFilmeSemEstoque() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme1 = new Filme("Filme 1", 1, 5.0);
        Filme filme2 = new Filme("Filme 2", 0, 5.0);
        Filme filme3 = new Filme("Filme 3", 4, 5.0);
        List<Filme> listaFilmes = Arrays.asList(filme1, filme2, filme3);
        //acao
        service.alugarFilme(usuario, listaFilmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        Filme filme1 = new Filme("Filme 1", 2, 5.0);
        Filme filme2 = new Filme("Filme 2", 2, 5.0);
        Filme filme3 = new Filme("Filme 3", 2, 5.0);
        List<Filme> listaFilmes = Arrays.asList(filme1, filme2, filme3);
        try {
            service.alugarFilme(null, listaFilmes);
            fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuário vazio"));
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("Usuario 1");

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");
        service.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {

        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        Usuario usuario = new Usuario("Thiago");
        List<Filme> filmes = Arrays.asList(new Filme("Armageddon", 2, 4.0));

        Locacao retorno = service.alugarFilme(usuario, filmes);

//        assertThat(retorno.getDataRetorno(), new DiaSemanaMatchers(Calendar.MONDAY));
//        assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));

        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }
}
