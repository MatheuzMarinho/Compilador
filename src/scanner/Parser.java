/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

/**
 *
 * @author mathe
 */
public class Parser {

    private final Scanner scanner;
    private final PushbackReader arquivo;
    private Boolean declaracao;
    private Boolean comando;
    private Token token;

    public Parser(InputStreamReader entradaFormatada) {
        this.scanner = new Scanner();
        this.arquivo = new PushbackReader(entradaFormatada);
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void analisar() throws CompiladorException, IOException {
        this.pegarProximoToken();
        this.analisarPrograma();

    }

    public void pegarProximoToken() throws CompiladorException, IOException {
        this.token = this.scanner.gerarTokens(this.arquivo);
        //System.out.println("Token: " + token.getLexema() + " / " + token.getSimbolo());
    }

    public void analisarPrograma() throws CompiladorException, IOException {
        if (this.token.getSimbolo().equals(Simbolos.PR_INT)) {
            this.pegarProximoToken();
            if (this.token.getSimbolo().equals(Simbolos.PR_MAIN)) {
                this.pegarProximoToken();
                if (this.token.getSimbolo().equals(Simbolos.ESP_ABRE_PARENTESES)) {
                    this.pegarProximoToken();
                    if (this.token.getSimbolo().equals(Simbolos.ESP_FECHA_PARENTESES)) {
                        this.pegarProximoToken();
                        this.analisarBloco();
                        if (!this.token.getSimbolo().equals(Simbolos.FIM_DE_ARQUIVO)) {
                            throw new CompiladorException("ERRO. Não pode existir tokens após o fim do bloco principal." + this.token.getSimbolo().toString());
                        }
                    } else {
                        throw new CompiladorException("ERRO. O token esperado deve ser ). Encontrou um " + this.token.getSimbolo().toString());
                    }
                } else {
                    throw new CompiladorException("ERRO. O token esperado deve ser (. Encontrou um " + this.token.getSimbolo().toString());
                }
            } else {
                throw new CompiladorException("ERRO. O token esperado deve ser main.Encontrou um " + this.token.getSimbolo().toString());
            }

        } else {
            throw new CompiladorException("ERRO. O Programa deve ser iniciado com int.Encontrou um " + this.token.getSimbolo().toString());
        }
    }

    public void analisarBloco() throws CompiladorException, IOException {
        this.declaracao = true;
        this.comando = true;

        if (this.token.getSimbolo().equals(Simbolos.ESP_ABRE_COLCHETES)) {
            this.pegarProximoToken();
            while (this.declaracao) {
                this.declararVariavel();
            }
            while (this.comando) {
                this.declararComando();
            }

            if (Simbolos.verificarTipo(this.token.getSimbolo())) {
                throw new CompiladorException("ERRO. Não pode ter declaração de variavel depois de um comando." + this.token.getSimbolo().toString());
            }
            if (!this.token.getSimbolo().equals(Simbolos.ESP_FECHA_COLCHETES)) {
                throw new CompiladorException("ERRO. É preciso fechar o bloco com fecha colchetes. Encontrou um: " + this.token.getSimbolo().toString());
            }
            this.pegarProximoToken();
        } else {
            throw new CompiladorException("ERRO. O token esperado deve ser {. Encontrou um " + this.token.getSimbolo().toString());
        }

    }

    public void declararVariavel() throws CompiladorException, IOException {

        if (Simbolos.verificarTipo(this.token.getSimbolo())) {
            this.pegarProximoToken();
            if (this.token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
                while (this.token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
                    this.pegarProximoToken();
                    switch (this.token.getSimbolo()) {
                        case ESP_VIRGULA:
                            this.pegarProximoToken();
                            break;
                        case ESP_PONTO_E_VIRGULA:
                            this.pegarProximoToken();
                            return;
                        default:
                            throw new CompiladorException("ERRO. O token esperado deve ser VIRGULA OU PONTO E VIRGULA. Encontrou um " + this.token.getSimbolo().toString());
                    }
                }
                throw new CompiladorException("ERRO. O token esperado deve ser IDENTIFICADOR. Encontrou um " + this.token.getSimbolo().toString());
            } else {
                throw new CompiladorException("ERRO. O token esperado deve ser IDENTIFICADOR. Encontrou um " + this.token.getSimbolo().toString());
            }
        } else {
            this.declaracao = false;
        }
    }

    public void declararComando() throws CompiladorException, IOException {
        if (this.token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
            this.atribuicao();
        }else if(this.token.getSimbolo().equals(Simbolos.ESP_ABRE_COLCHETES)){
            this.analisarBloco();
        }else if(this.token.getSimbolo().equals(Simbolos.PR_IF)){
            this.comandoIfElse();
        }else {
            this.comando = false;
        }
    }
    
    public void comandoIfElse() throws CompiladorException, IOException{
        this.pegarProximoToken();
        if(this.token.getSimbolo().equals(Simbolos.ESP_ABRE_PARENTESES)){
            this.pegarProximoToken();
            this.expressaoRelacional();
            if(this.token.getSimbolo().equals(Simbolos.ESP_FECHA_PARENTESES)){
                this.pegarProximoToken();
                this.declararComando();
            }else{
               throw new CompiladorException("ERRO. O token esperado deve ser um FECHA PARENTESE. Encontrou um " + this.token.getSimbolo().toString());   
            }
        }else{
            throw new CompiladorException("ERRO. O token esperado deve ser um ABRE PARENTESE. Encontrou um " + this.token.getSimbolo().toString()); 
        }
        
    }
    
    public void expressaoRelacional() throws CompiladorException, IOException{
        this.expressaoAritmetrica();
        this.operadorRelacional();
        this.pegarProximoToken();
        this.expressaoAritmetrica();      
    }
    
    public void operadorRelacional() throws CompiladorException{
        if(!Simbolos.verificarRelacional(this.token.getSimbolo())){
            throw new CompiladorException("ERRO. O token esperado deve ser um Operador Relacional. Encontrou um " + this.token.getSimbolo().toString()); 
        }
    }
    
    public void atribuicao() throws CompiladorException, IOException {
        if (this.token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
            this.pegarProximoToken();
            if (this.token.getSimbolo().equals(Simbolos.OP_ARITMETICO_IGUAL)) {
                this.pegarProximoToken();
                this.expressaoAritmetrica();
                if(!this.token.getSimbolo().equals(Simbolos.ESP_PONTO_E_VIRGULA)){
                    throw new CompiladorException("ERRO. O token esperado deve ser um PONTO E VIRGULA. Encontrou um " + this.token.getSimbolo().toString());
                }
                this.pegarProximoToken();
            } else {
                throw new CompiladorException("ERRO. O token esperado deve ser um IGUAL. Encontrou um " + this.token.getSimbolo().toString());
            }
        }
    }

    public void expressaoAritmetrica() throws CompiladorException, IOException {
        this.verificaTermo();
        this.expressaoAritmetricaLinha();
    }

    public void expressaoAritmetricaLinha() throws CompiladorException, IOException {
        if (Simbolos.verificarAddSub(this.token.getSimbolo())) {
            this.pegarProximoToken();
            this.verificaTermo();
            this.expressaoAritmetricaLinha();
        }
    }

    public void verificaTermo() throws CompiladorException, IOException {
            this.verificarFator();
            this.pegarProximoToken();
            while (Simbolos.verificarMultDiv(this.token.getSimbolo())) {
                this.pegarProximoToken();
                this.verificarFator();
                 this.pegarProximoToken();
            }
    }
    
    public void verificarFator() throws CompiladorException, IOException{
        if(Simbolos.verificarFator(this.token.getSimbolo())){
        } else {
            throw new CompiladorException("ERRO. O token esperado deve ser um FATOR. Encontrou um " + this.token.getSimbolo().toString());
        }
    }

}
