package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

public class LocacaoService {

    private LocacaoDAO dao;
    private SPCService spcService;
    private EmailServices emailServices;

    public Locacao alugarFilme(Usuario usuario, List<Filme> listaFilme) throws FilmeSemEstoqueException, LocadoraException {
        if (usuario == null) {
            throw new LocadoraException("Usuário vazio");
        }
        if (listaFilme == null || listaFilme.isEmpty()) {
            throw new LocadoraException("Filme vazio");
        }
        for (Filme lista : listaFilme) {
            if (lista.getEstoque() == 0) {
                throw new FilmeSemEstoqueException(lista.getNome());
            }
        }

        boolean ehNegativado;
        try {
            ehNegativado = spcService.possuiNegativacao(usuario);
        } catch (Exception e) {
            throw new LocadoraException("Problemas com SPC, tente novamente");
        }
        if (ehNegativado) {
            throw new LocadoraException("Usuário Negativado");
        }

        Locacao locacao = new Locacao();
        locacao.setListaFilme(listaFilme);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(ObterData());

        locacao.setValor(calcularValorLocacao(listaFilme));

        Date dataEntrega = ObterData();

        if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SATURDAY)) {
            dataEntrega = adicionarDias(dataEntrega, 2);
        } else {
            dataEntrega = adicionarDias(dataEntrega, 1);
        }
        locacao.setDataRetorno(dataEntrega);

        dao.salvar(locacao);

        return locacao;
    }

    @NotNull
    protected Date ObterData() {
        return new Date();
    }

    private double calcularValorLocacao(List<Filme> listaFilme) {
        double valorLocacaoAux = 0d;
        for (int i = 0; i < listaFilme.size(); i++) {
            Filme filme = listaFilme.get(i);
            Double valorFilme = filme.getPrecoLocacao();
            switch (i) {
                case 2:
                    valorFilme *= 0.75;
                    break;
                case 3:
                    valorFilme *= 0.5;
                    break;
                case 4:
                    valorFilme *= 0.25;
                    break;
                case 5:
                    valorFilme *= 0.0;
                    break;
            }
            valorLocacaoAux += valorFilme;
        }
        return valorLocacaoAux;
    }

    public void notificarAtrasos() {
        List<Locacao> locacoes = dao.obterLocacoesPendentes();
        for (Locacao locacao : locacoes) {
            if (locacao.getDataRetorno().before(ObterData())) {
                emailServices.notificarAtraso(locacao.getUsuario());
            }
        }
    }

    public void prorrogarLocacao(@NotNull Locacao locacao, int dias) {
        Locacao novaLocacao = new Locacao();
        novaLocacao.setUsuario(locacao.getUsuario());
        novaLocacao.setListaFilme(locacao.getListaFilme());
        novaLocacao.setDataLocacao(ObterData());
        novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
        novaLocacao.setValor(locacao.getValor() * dias);
        dao.salvar(novaLocacao);
    }

}