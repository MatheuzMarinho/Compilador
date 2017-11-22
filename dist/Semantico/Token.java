/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

/**
 *
 * @author mathe
 */
public class Token {

    private Simbolos simbolo;
    private String lexema;

    public Token(Simbolos simbolo, String lexema) {
        this.simbolo = simbolo;
        this.lexema = lexema;
    }

    public Token(Simbolos simbolo) {
        this.simbolo = simbolo;
    }

    public Simbolos getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(Simbolos simbolo) {
        this.simbolo = simbolo;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

}
