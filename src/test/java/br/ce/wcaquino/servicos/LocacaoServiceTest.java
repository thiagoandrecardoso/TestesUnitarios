package br.ce.wcaquino.servicos;

import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprias.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
//@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @InjectMocks
    private LocacaoService service;
    @Mock
    private SPCService spcService;
    @Mock
    private LocacaoDAO dao;
    @Mock
    private EmailServices emailServices;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = PowerMockito.spy(service);
    }

    @Test
    public void verificaValoresDelocacaoData_precos() throws Exception {
        //cenario
//        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
//        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.YEAR, 2017);

        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        Usuario usuario = umUsuario().agora();
        Filme filme1 = umFilme().comValor(5.0).agora();
        Filme filme2 = umFilme().comValor(5.0).agora();
        Filme filme3 = umFilme().comValor(5.0).agora();
        List<Filme> listaFilmes = Arrays.asList(filme1, filme2, filme3);
        //acao
        Locacao locacao;
        locacao = service.alugarFilme(usuario, listaFilmes);
        //verificacao
        error.checkThat(locacao.getValor(), is(equalTo(13.75)));
        error.checkThat(locacao.getValor(), is(not(6.0)));

//        error.checkThat(locacao.getDataLocacao(), ehHoje());
//        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));

    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void deveLancarExcecaoAoLocarFilmeSemEstoque() throws Exception {
        //cenario
        Usuario usuario = umUsuario().agora();
        Filme filme1 = umFilme().agora();
        Filme filme2 = umFilme().semEstoque().agora();
        Filme filme3 = umFilme().agora();
        List<Filme> listaFilmes = Arrays.asList(filme1, filme2, filme3);
        //acao
        service.alugarFilme(usuario, listaFilmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        Filme filme1 = umFilme().agora();
        Filme filme2 = umFilme().agora();
        Filme filme3 = umFilme().agora();
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
        Usuario usuario = umUsuario().agora();

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");
        service.alugarFilme(usuario, null);
    }

    //    @Ignore
    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {

//        assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(umFilme().agora());

//        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.YEAR, 2017);

        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

        Locacao retorno = service.alugarFilme(usuario, filmes);

//        assertThat(retorno.getDataRetorno(), new DiaSemanaMatchers(Calendar.MONDAY));
//        assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));

        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
//        PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();

        PowerMockito.verifyStatic(Mockito.times(2));
        Calendar.getInstance();
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativado() throws Exception {
        Usuario usuario = umUsuario().agora();

        List<Filme> filmes = Collections.singletonList(umFilme().agora());

        when(spcService.possuiNegativacao(usuario)).thenReturn(true);

        exception.expect(LocadoraException.class);
        exception.expectMessage("Usuário Negativado");

        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas(){
        //cenario
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuário em dias").agora();
        Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
        List<Locacao> locacoes = Arrays.asList(
                umLocacao().atrasado().comUsuario(usuario).agora(),
                umLocacao().comUsuario(usuario2).agora(),
                umLocacao().atrasado().comUsuario(usuario3).agora(),
                umLocacao().atrasado().comUsuario(usuario3).agora());
        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        //acao
        service.notificarAtrasos();

        //verificacao
        verify(emailServices).notificarAtraso(usuario);
        verify(emailServices, never()).notificarAtraso(usuario2);
        verify(emailServices, atLeastOnce()).notificarAtraso(usuario3);

        // verifica se qualquer instancia de usuario enviou recebeu e-mail pelo menos duas vezes
        verify(emailServices, times(3)).notificarAtraso(Mockito.any(Usuario.class));

        // verificar se nem um outro e-mail foi lancado
        Mockito.verifyNoMoreInteractions(emailServices);
    }

    @Test
    public void deveTratarErrosSPC() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(umFilme().agora());

        when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha SPC"));

        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas com SPC, tente novamente");

        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveProrrogarLocacao(){
        Locacao locacao = LocacaoBuilder.umLocacao().agora();

        service.prorrogarLocacao(locacao, 3);

        /**
         * Captura o argumento que eh repassado no metodo
         */
        ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argumentCaptor.capture());
        Locacao locacaoRetornada = argumentCaptor.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(12.00));
        error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
    }

    @Test
    public void deveAlugarFilmeSemCalcularValor() throws Exception {
        // cenario
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());
        PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);

        // acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        // verificacao
        assertThat(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
    }

}
