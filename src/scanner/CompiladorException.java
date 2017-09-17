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
public class CompiladorException extends Exception {
    private final String mensagem;
    
    public CompiladorException(String mensagem){
        super(mensagem);
        this.mensagem = mensagem;
    }
    
}
