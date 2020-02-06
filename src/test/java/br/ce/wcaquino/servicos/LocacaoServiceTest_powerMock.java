package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprias.caiNumaSegunda;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocacaoService.class)
public class LocacaoServiceTest_powerMock {

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
        Mockito.doReturn(DataUtils.obterData(28, 4, 2017)).when(service).ObterData();

        Usuario usuario = umUsuario().agora();
        Filme filme1 = umFilme().comValor(5.0).agora();
        Filme filme2 = umFilme().comValor(5.0).agora();
        Filme filme3 = umFilme().comValor(5.0).agora();
        List<Filme> listaFilmes = Arrays.asList(filme1, filme2, filme3);

        Locacao locacao;
        locacao = service.alugarFilme(usuario, listaFilmes);

        error.checkThat(locacao.getValor(), is(equalTo(13.75)));
        error.checkThat(locacao.getValor(), is(not(6.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));

    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Collections.singletonList(umFilme().agora());

        Mockito.doReturn(DataUtils.obterData(29, 4, 2017)).when(service).ObterData();

        Locacao retorno = service.alugarFilme(usuario, filmes);

        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
        PowerMockito.verifyStatic(Mockito.times(2));
        Calendar.getInstance();
    }

    @Test
    public void deveAlugarFilmeSemCalcularValor() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());
        PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);

        Locacao locacao = service.alugarFilme(usuario, filmes);

        assertThat(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
    }

    @Test
    public void deveCalcularValorLocacao() throws Exception {
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);

        assertThat(valor, is(4.0));
    }

}
