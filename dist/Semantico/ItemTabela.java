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
public class ItemTabela {
    
    private Simbolos simbolo;
    private int escopo;
    private String lexema;

    public ItemTabela() {
    }

    public ItemTabela(Simbolos simbolo, int escopo, String lexema) {
        this.simbolo = simbolo;
        this.escopo = escopo;
        this.lexema = lexema;
    }
    
    public Simbolos getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(Simbolos simbolo) {
        this.simbolo = simbolo;
    }

    public int getEscopo() {
        return escopo;
    }

    public void setEscopo(int escopo) {
        this.escopo = escopo;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }
    

    
    
    
}
