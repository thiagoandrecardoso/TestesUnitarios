package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;

public class LocacaoService {

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

        Locacao locacao = new Locacao();
        locacao.setListaFilme(listaFilme);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(new Date());

        // adicionar preço da locação
        double valorLocacaoAux = 0;

        for (int i = 0; i < listaFilme.size(); i++) {
            Filme filme = listaFilme.get(i);
            Double valorFilme = filme.getPrecoLocacao();
            if (i == 2) {
                valorFilme *= 0.75;
            }
            valorLocacaoAux += valorFilme;
        }
        locacao.setValor(valorLocacaoAux);

        //Entrega no dia seguinte
        Date dataEntrega = new Date();
        dataEntrega = adicionarDias(dataEntrega, 1);
        locacao.setDataRetorno(dataEntrega);

        //Salvando a locacao...
        //TODO adicionar método para salvar

        return locacao;
    }
}