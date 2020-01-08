package br.ce.wcaquino.exception;

/**
 *
 */
public class FilmeSemEstoqueException extends Exception {
    public FilmeSemEstoqueException(String message) {
        super("O filme = " + message + " não tem em estoque!");
    }
}
