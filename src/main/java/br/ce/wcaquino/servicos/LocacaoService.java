package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

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

        if (spcService.possuiNegativacao(usuario)){
            throw new LocadoraException("Usuário Negativado");
        }

        Locacao locacao = new Locacao();
        locacao.setListaFilme(listaFilme);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(new Date());

        // adicionar preço da locação
        double valorLocacaoAux = 0;

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
        locacao.setValor(valorLocacaoAux);

        //Entrega no dia seguinte
        Date dataEntrega = new Date();

        if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SATURDAY)){
            dataEntrega = adicionarDias(dataEntrega, 2);
        }else{
            dataEntrega = adicionarDias(dataEntrega, 1);
        }
        locacao.setDataRetorno(dataEntrega);
        //Salvando a locacao...
        dao.salvar(locacao);

        return locacao;
    }

    public void notificarAtrasos(){
        List<Locacao> locacoes = dao.obterLocacoesPendentes();
        for (Locacao locacao : locacoes){
            if (locacao.getDataRetorno().before(new Date())){
                emailServices.notificarAtraso(locacao.getUsuario());
            }
        }
    }
}