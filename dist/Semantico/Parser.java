/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mathe
 */
public class Parser {

    private final Scanner scanner;
    private final PushbackReader arquivo;
    private Boolean declaracao;
    private Token token;
    private int escopo;
    private final List<ItemTabela> tabela;

    public Parser(InputStreamReader entradaFormatada) {
        this.scanner = new Scanner();
        this.arquivo = new PushbackReader(entradaFormatada);
        this.tabela = new ArrayList<>();
        this.escopo = 0;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void adicionarEscopo() {
        this.escopo++;
    }

    public void retirarEscopo() {
        SemanticoService.removerEscopo(tabela, escopo);
        this.escopo--;
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
            this.adicionarEscopo();

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
                if (this.token.getSimbolo().equals(Simbolos.FIM_DE_ARQUIVO)) {
                    throw new CompiladorException("ERRO. É preciso fechar o bloco com fecha colchetes. Encontrou um: " + this.token.getSimbolo().toString() + " STRING: " + this.token.getLexema());
                } else {
                    throw new CompiladorException("ERRO. Token não faz parte de uma DECLARAÇÃO ou COMANDO. Encontrou um: " + this.token.getSimbolo().toString() + " STRING: " + this.token.getLexema());
                }

            }
            this.retirarEscopo();
            this.pegarProximoToken();
        } else {
            throw new CompiladorException("ERRO. O token esperado deve ser {. Encontrou um " + this.token.getSimbolo().toString());
        }

    }
    
    public Simbolos converteSimboloDeclaracao(Simbolos simboloDeclaracao){
        switch (simboloDeclaracao) {
            case PR_CHAR:
                return Simbolos.TIPO_CHAR;
            case PR_FLOAT:
                return Simbolos.TIPO_FLOAT;
            default:
                return Simbolos.TIPO_INTEIRO;
        }
    }

    public void declararVariavel() throws CompiladorException, IOException {
        Simbolos simbolo;
        if (Simbolos.verificarTipo(this.token.getSimbolo())) {
            simbolo = this.converteSimboloDeclaracao(this.token.getSimbolo());
            this.pegarProximoToken();
            if (this.token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
                while (this.token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
                    if (SemanticoService.validarEscopo(tabela, this.token.getLexema(), escopo)) {
                        this.tabela.add(new ItemTabela(simbolo, escopo, this.token.getLexema()));
                    }
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
        } else if (this.token.getSimbolo().equals(Simbolos.PR_DO)) {
            this.comandoDoWhile();
            return true;
        } else {
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
            } else {
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
        Simbolos tipoIdUm, tipoIdDois;
        tipoIdUm = this.expressaoAritmetica();
        this.operadorRelacional();
        this.pegarProximoToken();
        tipoIdDois = this.expressaoAritmetica();
        SemanticoService.verificaExpressaoRelacional(tipoIdUm, tipoIdDois);
    }

    public void operadorRelacional() throws CompiladorException {
        if (!Simbolos.verificarRelacional(this.token.getSimbolo())) {
            throw new CompiladorException("ERRO. O token esperado deve ser um Operador Relacional. Encontrou um " + this.token.getSimbolo().toString());
        }
    }

    public void atribuicao() throws CompiladorException, IOException {
        Simbolos tipoIdUm, tipoIdDois;
        if (this.token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
            tipoIdUm = SemanticoService.verificaSeDeclada(tabela, this.token);
            this.pegarProximoToken();
            if (this.token.getSimbolo().equals(Simbolos.OP_ARITMETICO_IGUAL)) {
                this.pegarProximoToken();
                tipoIdDois = this.expressaoAritmetica();
                if (tipoIdUm.equals(tipoIdDois)) {

                } else {
                    if (tipoIdUm.equals(Simbolos.TIPO_CHAR) || tipoIdDois.equals(Simbolos.TIPO_CHAR)) {
                        throw new CompiladorException("Tipo dos identificadores diferentes. CHAR recebe ou atribui somente com CHAR");
                    }else if(tipoIdUm.equals(Simbolos.TIPO_INTEIRO)){
                        throw new CompiladorException("Tipo dos identificadores diferentes. INT não recebe FLOAT");
                    }          
                }
                if (!this.token.getSimbolo().equals(Simbolos.ESP_PONTO_E_VIRGULA)) {
                    throw new CompiladorException("ERRO. O token esperado deve ser um PONTO E VIRGULA ou um OPERADOR ARITMETICO. Encontrou um " + this.token.getSimbolo().toString());
                }
                this.pegarProximoToken();
            } else {
                throw new CompiladorException("ERRO. O token esperado deve ser um IGUAL. Encontrou um " + this.token.getSimbolo().toString());
            }
        }
    }

    public Simbolos expressaoAritmetica() throws CompiladorException, IOException {
        Simbolos tipoIdUm;
        tipoIdUm = this.verificaTermo();
        tipoIdUm = this.expressaoAritmeticaLinha(tipoIdUm);
        return tipoIdUm;
    }

    public Simbolos expressaoAritmeticaLinha(Simbolos tipoIdUm) throws CompiladorException, IOException {
        Simbolos tipoIdDois, operacao;
        if (Simbolos.verificarAddSub(this.token.getSimbolo())) {
            operacao = this.token.getSimbolo();
            this.pegarProximoToken();
            tipoIdDois = this.verificaTermo();
            tipoIdUm = this.verificaTiposComOperacao(tipoIdUm, tipoIdDois, operacao);
            tipoIdUm = this.expressaoAritmeticaLinha(tipoIdUm);
        }
        return tipoIdUm;
    }

    public Simbolos verificaTermo() throws CompiladorException, IOException {
        Simbolos tipoIdUm, tipoIdDois, operacao;

        tipoIdUm = this.verificarFator();
        while (Simbolos.verificarMultDiv(this.token.getSimbolo())) {
            operacao = this.token.getSimbolo();
            this.pegarProximoToken();
            tipoIdDois = this.verificarFator();
            tipoIdUm = this.verificaTiposComOperacao(tipoIdUm, tipoIdDois, operacao);
        }

        return tipoIdUm;
    }

    public Simbolos verificaTiposComOperacao(Simbolos tipoIdUm, Simbolos tipoIdDois, Simbolos operacao) throws CompiladorException {
        if (tipoIdUm.equals(tipoIdDois)) {
            if (tipoIdUm.equals(Simbolos.TIPO_CHAR)) {
                return Simbolos.TIPO_CHAR;
            } else if (tipoIdUm.equals(Simbolos.TIPO_FLOAT)) {
                return Simbolos.TIPO_FLOAT;
            } else if (tipoIdUm.equals(Simbolos.TIPO_INTEIRO) && operacao.equals(Simbolos.OP_ARITMETICO_BARRA)) {
                return Simbolos.TIPO_FLOAT;
            } else {
                return Simbolos.TIPO_INTEIRO;
            }
        } else {
            if (tipoIdUm.equals(Simbolos.TIPO_CHAR) || tipoIdDois.equals(Simbolos.TIPO_CHAR)) {
                throw new CompiladorException("Tipo dos identificadores diferentes. CHAR somente realiza operacoes com CHAR");
            } else {
                return Simbolos.TIPO_FLOAT;
            }
        }
    }

    public Simbolos verificarFator() throws CompiladorException, IOException {
        if (Simbolos.verificarFator(this.token.getSimbolo())) {
            if (this.token.getSimbolo().equals(Simbolos.ESP_ABRE_PARENTESES)) {
                this.pegarProximoToken();
                this.expressaoAritmetica();
                if (!this.token.getSimbolo().equals(Simbolos.ESP_FECHA_PARENTESES)) {
                    throw new CompiladorException("ERRO. O token esperado deve ser um FECHA PARENTESES. Encontrou um " + this.token.getSimbolo().toString());
                }
                Simbolos s = SemanticoService.verificaSeDeclada(tabela, this.token);
                this.pegarProximoToken();
                return s;
            } else {
                Simbolos s = SemanticoService.verificaSeDeclada(tabela, this.token);
                this.pegarProximoToken();
                return s;
            }
        } else {
            throw new CompiladorException("ERRO. O token esperado deve ser um FATOR. Encontrou um " + this.token.getSimbolo().toString());
        }
    }

}
