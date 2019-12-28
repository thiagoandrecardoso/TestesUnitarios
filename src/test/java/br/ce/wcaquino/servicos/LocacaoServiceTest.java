package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    public void devePagar25porcentoNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("Thiago");
        List<Filme> filmes = Arrays.asList(new Filme("Armageddon", 2, 4.0),
                new Filme("Duro de Matar", 2, 4.0),
                new Filme("Lisbela", 2, 4.0));

        Locacao resultado = service.alugarFilme(usuario, filmes);

        assertThat(resultado.getValor(), is(11.00));
    }

    @Test
    public void devePagar50porcentoNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("Thiago");
        List<Filme> filmes = Arrays.asList(new Filme("Armageddon", 2, 4.0),
                new Filme("Duro de Matar", 2, 4.0),
                new Filme("Lisbela", 2, 4.0), new Filme("O quinto elemento", 2, 4.0));

        Locacao resultado = service.alugarFilme(usuario, filmes);

        assertThat(resultado.getValor(), is(13.00));
    }

    @Test
    public void devePagar75porcentoNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("Thiago");
        List<Filme> filmes = Arrays.asList(new Filme("Armageddon", 2, 4.0),
                new Filme("Duro de Matar", 2, 4.0),
                new Filme("Lisbela", 2, 4.0),
                new Filme("O quinto elemento", 2, 4.0),
                new Filme("Inception", 2, 4.0));

        Locacao resultado = service.alugarFilme(usuario, filmes);

        assertThat(resultado.getValor(), is(14.00));
    }

    @Test
    public void devePagar0porcentoNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("Thiago");
        List<Filme> filmes = Arrays.asList(new Filme("Armageddon", 2, 4.0),
                new Filme("Duro de Matar", 2, 4.0),
                new Filme("Lisbela", 2, 4.0),
                new Filme("O quinto elemento", 2, 4.0),
                new Filme("Inception", 2, 4.0),
                new Filme("A batalha de Abel", 2, 4.0));

        Locacao resultado = service.alugarFilme(usuario, filmes);

        assertThat(resultado.getValor(), is(14.00));
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("Thiago");
        List<Filme> filmes = Arrays.asList(new Filme("Armageddon", 2, 4.0));

        Locacao retorno = service.alugarFilme(usuario, filmes);

        /**
         * Verificar se o dia do aluguel é sábado
         */
        boolean ehSabado = DataUtils.verificarDiaSemana(retorno.getDataLocacao(), Calendar.SATURDAY);
        if (ehSabado) {
            boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
            Assert.assertTrue(ehSegunda);
        }else{
            Assert.assertTrue(true);
        }
    }
}
