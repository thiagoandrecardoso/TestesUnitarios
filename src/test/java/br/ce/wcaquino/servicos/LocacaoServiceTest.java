package br.ce.wcaquino.servicos;


import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import java.util.Date;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Assinatura FixMethodOrder abaixo garante que os métodos sejam
 * testados em ordem alfabética.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void afterClass() {
    }

    @Test
    public void a_testeLocacao() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 2, 5.0);
        //acao
        Locacao locacao = null;
        locacao = service.alugarFilme(usuario, filme);
        //verificacao
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(locacao.getValor(), is(not(6.0)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void b_testeFilmeSemEstoqueComTestEsperandoExcessao() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 0, 5.0);
        //acao
//        Locacao locacao = null;
        service.alugarFilme(usuario, filme);
    }

    @Test
    public void c_testeUsuarioVazio() throws FilmeSemEstoqueException {
        Filme filme = new Filme("Lisbela", 2, 5.0);
        try {
            service.alugarFilme(null, filme);
            fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuário vazio"));
        }
    }

    @Test
    public void d_testeFilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
        Usuario usuario = new Usuario("Usuario 1");

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");
        service.alugarFilme(usuario, null);
    }


//    @Test
//    public void testeFilmeSemEstoqueTryCatch() {
//        //cenario
//        LocacaoService service = new LocacaoService();
//        Usuario usuario = new Usuario("Usuario 1");
//        Filme filme = new Filme("Filme 1", 0, 5.0);
//        //acao
//        Locacao locacao = null;
//        try {
////			Assert.fail("Deveria ter lançado uma exceção");
//            service.alugarFilme(usuario, filme);
//        } catch (Exception e) {
//            assertThat(e.getMessage(), is("Não possui em estoque"));
//        }
//    }

//    @Test
//    public void testeFilmeSemEstoqueUsandoRuleException() throws Exception {
//        //cenario
//        LocacaoService service = new LocacaoService();
//        Usuario usuario = new Usuario("Usuario 1");
//        Filme filme = new Filme("Filme 1", 0, 5.0);
//        //acao
//        exception.expect(Exception.class);
//        exception.expectMessage("Não possui em estoque");
//
//        Locacao locacao = null;
//        service.alugarFilme(usuario, filme);
//    }
}
