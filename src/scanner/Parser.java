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

        if (this.token.getSimbolo().equals(Simbolos.ESP_ABRE_COLCHETES)) {
            this.pegarProximoToken();
            while (this.declaracao) {
                this.declararVariavel();
            }
            while (this.declararComando()) {
                if (Simbolos.verificarTipo(this.token.getSimbolo())) {
                    throw new CompiladorException("ERRO. Não pode ter declaração de variavel depois de um comando." + this.token.getSimbolo().toString());
                }
            }
            if (!this.token.getSimbolo().equals(Simbolos.ESP_FECHA_COLCHETES)) {
                if(this.token.getSimbolo().equals(Simbolos.FIM_DE_ARQUIVO)){
                      throw new CompiladorException("ERRO. É preciso fechar o bloco com fecha colchetes. Encontrou um: " + this.token.getSimbolo().toString()+ " STRING: "+ this.token.getLexema()) ; 
                }else{
                     throw new CompiladorException("ERRO. Token não faz parte de uma DECLARAÇÃO ou COMANDO. Encontrou um: " + this.token.getSimbolo().toString()+ " STRING: "+ this.token.getLexema()) ;  
                }
             
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

    public Boolean declararComando() throws CompiladorException, IOException {
        if (this.token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
            this.atribuicao();
            return true;
        } else if (this.token.getSimbolo().equals(Simbolos.ESP_ABRE_COLCHETES)) {
            this.analisarBloco();
            return true;
        } else if (this.token.getSimbolo().equals(Simbolos.PR_IF)) {
            this.comandoIfElse();
            return true;
        } else if (this.token.getSimbolo().equals(Simbolos.PR_WHILE)) {
            this.comandoWhile();
            return true;
        }else if (this.token.getSimbolo().equals(Simbolos.PR_DO)) {
            this.comandoDoWhile();
            return true;
        }else {
            return false;
        }
    }

    public void comandoDoWhile() throws CompiladorException, IOException {
        this.pegarProximoToken();
        if (this.declararComando()) {
            if (this.token.getSimbolo().equals(Simbolos.PR_WHILE)) {
                this.pegarProximoToken();
                if (this.token.getSimbolo().equals(Simbolos.ESP_ABRE_PARENTESES)) {
                    this.pegarProximoToken();
                    this.expressaoRelacional();
                    if (this.token.getSimbolo().equals(Simbolos.ESP_FECHA_PARENTESES)) {
                        this.pegarProximoToken();
                        if (!this.token.getSimbolo().equals(Simbolos.ESP_PONTO_E_VIRGULA)) {
                            throw new CompiladorException("ERRO. Esperava um PONTO E VIRGULA. Encontrou um " + this.token.getSimbolo().toString());
                        }
                        this.pegarProximoToken();
                    } else {
                        throw new CompiladorException("ERRO. O token esperado deve ser um FECHA PARENTESE. Encontrou um " + this.token.getSimbolo().toString());
                    }
                } else {
                    throw new CompiladorException("ERRO. O token esperado deve ser um ABRE PARENTESE. Encontrou um " + this.token.getSimbolo().toString());
                }
            }else{
                throw new CompiladorException("ERRO. Esperava um WHILE. Encontrou um " + this.token.getSimbolo().toString()); 
            }
        } else {
            throw new CompiladorException("ERRO. Esperava um COMANDO. Encontrou um " + this.token.getSimbolo().toString());
        }
    }

    public void comandoWhile() throws CompiladorException, IOException {
        this.pegarProximoToken();
        if (this.token.getSimbolo().equals(Simbolos.ESP_ABRE_PARENTESES)) {
            this.pegarProximoToken();
            this.expressaoRelacional();
            if (this.token.getSimbolo().equals(Simbolos.ESP_FECHA_PARENTESES)) {
                this.pegarProximoToken();
                if (!this.declararComando()) {
                    throw new CompiladorException("ERRO. Esperava um COMANDO. Encontrou um " + this.token.getSimbolo().toString());
                }
            } else {
                throw new CompiladorException("ERRO. O token esperado deve ser um FECHA PARENTESE. Encontrou um " + this.token.getSimbolo().toString());
            }
        } else {
            throw new CompiladorException("ERRO. O token esperado deve ser um ABRE PARENTESE. Encontrou um " + this.token.getSimbolo().toString());
        }
    }

    public void comandoIfElse() throws CompiladorException, IOException {
        this.pegarProximoToken();
        if (this.token.getSimbolo().equals(Simbolos.ESP_ABRE_PARENTESES)) {
            this.pegarProximoToken();
            this.expressaoRelacional();
            if (this.token.getSimbolo().equals(Simbolos.ESP_FECHA_PARENTESES)) {
                this.pegarProximoToken();
                if (this.declararComando()) {
                    if (this.token.getSimbolo().equals(Simbolos.PR_ELSE)) {
                        this.pegarProximoToken();
                        if (!this.declararComando()) {
                            throw new CompiladorException("ERRO. Precisa existir um comando apos o ELSE. Encontrou um " + this.token.getSimbolo().toString());
                        }
                    }
                } else {
                    throw new CompiladorException("ERRO. Precisa existir um comando apos a Expressao Relacional. Encontrou um " + this.token.getSimbolo().toString());
                }
            } else {
                throw new CompiladorException("ERRO. O token esperado deve ser um FECHA PARENTESE. Encontrou um " + this.token.getSimbolo().toString());
            }
        } else {
            throw new CompiladorException("ERRO. O token esperado deve ser um ABRE PARENTESE. Encontrou um " + this.token.getSimbolo().toString());
        }

    }

    public void expressaoRelacional() throws CompiladorException, IOException {
        this.expressaoAritmetica();
        this.operadorRelacional();
        this.pegarProximoToken();
        this.expressaoAritmetica();
    }

    public void operadorRelacional() throws CompiladorException {
        if (!Simbolos.verificarRelacional(this.token.getSimbolo())) {
            throw new CompiladorException("ERRO. O token esperado deve ser um Operador Relacional. Encontrou um " + this.token.getSimbolo().toString());
        }
    }

    public void atribuicao() throws CompiladorException, IOException {
        if (this.token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
            this.pegarProximoToken();
            if (this.token.getSimbolo().equals(Simbolos.OP_ARITMETICO_IGUAL)) {
                this.pegarProximoToken();
                this.expressaoAritmetica();
                if (!this.token.getSimbolo().equals(Simbolos.ESP_PONTO_E_VIRGULA)) {
                    throw new CompiladorException("ERRO. O token esperado deve ser um PONTO E VIRGULA ou um OPERADOR ARITMETICO. Encontrou um " + this.token.getSimbolo().toString());
                }
                this.pegarProximoToken();
            } else {
                throw new CompiladorException("ERRO. O token esperado deve ser um IGUAL. Encontrou um " + this.token.getSimbolo().toString());
            }
        }
    }

    public void expressaoAritmetica() throws CompiladorException, IOException {
        this.verificaTermo();
        this.expressaoAritmeticaLinha();
    }

    public void expressaoAritmeticaLinha() throws CompiladorException, IOException {
        if (Simbolos.verificarAddSub(this.token.getSimbolo())) {
            this.pegarProximoToken();
            this.verificaTermo();
            this.expressaoAritmeticaLinha();
        }
    }

    public void verificaTermo() throws CompiladorException, IOException {
        this.verificarFator();
        while (Simbolos.verificarMultDiv(this.token.getSimbolo())) {
            this.pegarProximoToken();
            this.verificarFator();
        }
    }

    public void verificarFator() throws CompiladorException, IOException {
        if (Simbolos.verificarFator(this.token.getSimbolo())) {
            if (this.token.getSimbolo().equals(Simbolos.ESP_ABRE_PARENTESES)) {
                this.pegarProximoToken();
                this.expressaoAritmetica();
                if (!this.token.getSimbolo().equals(Simbolos.ESP_FECHA_PARENTESES)) {
                    throw new CompiladorException("ERRO. O token esperado deve ser um FECHA PARENTESES. Encontrou um " + this.token.getSimbolo().toString());
                }
                this.pegarProximoToken();
            } else {
                this.pegarProximoToken();
            }
        } else {
            throw new CompiladorException("ERRO. O token esperado deve ser um FATOR. Encontrou um " + this.token.getSimbolo().toString());
        }
    }

}
