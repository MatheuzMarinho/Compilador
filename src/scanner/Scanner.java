/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

import java.io.IOException;
import java.io.PushbackReader;
import util.ControladorArquivo;

/**
 *
 * @author mathe
 */
public class Scanner {

    private ControladorArquivo controladorArquivo;
    private int la;

    public Token gerarTokens(PushbackReader arquivo) throws CompiladorException, IOException {

        while (true) {
            lerArquivo(arquivo);
            //Comentário
            if ((char) this.la == '/') {
                if (!comentarios(arquivo)) {
                     voltarArquivo(arquivo);
                    return new Token(Simbolos.OP_ARITMETICO_BARRA);
                }
                voltarArquivo(arquivo);
            } else //FIM DE ARQUIVO
            if (la == 0 || la == -1) {
                return new Token(Simbolos.FIM_DE_ARQUIVO);
            } // OPERADORES ARITMETICOS
            else if (Simbolos.verificarOperadores((char) la) != null) {
                voltarArquivo(arquivo);
                return gerarOperadoresAritmeticos(arquivo);
            } // DIGITO
            else if (Character.isDigit((char) la)) {
                voltarArquivo(arquivo);
                return gerarTokenNumerico(arquivo);
            } // FLOAT
            else if (la == 46) {
                return gerarTokenDecimal(arquivo, "0");
            } // LETRAS
            else if (Character.isLetter((char) la) || (char) this.la == '_') {
                voltarArquivo(arquivo);
                return gerarTokenLetras(arquivo);
            } // OPERADORES ESPECIAIS
            else if (Simbolos.verificarEspeciais((char) la) != null) {
                return new Token(Simbolos.verificarEspeciais((char) la));
            }// OPERADORES RELACIONAIS
            else if (Simbolos.verificarRelacionais((char) la) != null || (char) this.la == '!') {
                voltarArquivo(arquivo);
                return gerarOperadoresRelacionais(arquivo);
            } else if ((char) this.la == '\'') {
                return gerarChar(arquivo);
            } else {
                if (!caracterInvalido()) {
                    throw new CompiladorException("ERRO. Caracter inválido.");
                }
            }

        }
    }

    private Boolean caracterInvalido() {
        if (Character.isWhitespace(la)) {
            return true;
        } else {
            return false;
        }
    }

    private Token gerarChar(PushbackReader arquivo) throws IOException, CompiladorException {
        lerArquivo(arquivo);
        String lexema = "" + (char) this.la;
        lerArquivo(arquivo);
        if ((char) this.la == '\'') {
            return new Token(Simbolos.TIPO_CHAR, lexema);
        } else {
            throw new CompiladorException("ERRO. Char Mal Formado. Esperava um fecha aspas. Encontrou um: " + (char) this.la);
        }
    }

    private Boolean comentarios(PushbackReader arquivo) throws IOException, CompiladorException {
        String lexema = "";
        lerArquivo(arquivo);
        switch ((char) this.la) {
            case '/':
                while (this.la != 10) {
                    lerArquivo(arquivo);
                }
                //pegar o enter
                lerArquivo(arquivo);
                return true;
            case '*':
                while (true) {
                    lerArquivo(arquivo);
                    if (this.la == -1) {
                        throw new CompiladorException("ERRO. Fim de Arquivo no Meio do Comentário. É preciso fechar o comentário multilinhas.");
                    } else if ((char) this.la == '*') {
                        lerArquivo(arquivo);
                        if (this.la == -1) {
                            throw new CompiladorException("ERRO. Fim de Arquivo no Meio do Comentário. É preciso fechar o comentário multilinhas.");
                        } else if ((char) this.la == '/') {
                            lerArquivo(arquivo);
                            return true;
                        }
                        voltarArquivo(arquivo);
                    }
                }
            default:
                return false;
        }
    }

    private Token gerarOperadoresRelacionais(PushbackReader arquivo) throws IOException, CompiladorException {
        String lexema = "";
        lerArquivo(arquivo);

        if ((char) this.la == '!') {
            lerArquivo(arquivo);
            if ((char) this.la == '=') {
                return new Token(Simbolos.OP_RELACIONAL_DIFERENTE);
            } else {
                throw new CompiladorException("ERRO. Exclamação Sozinha. Esperava-se um operador de =.");
            }
        } else {
            Simbolos simbolo = Simbolos.verificarRelacionais((char) la);
            if (simbolo.equals(Simbolos.OP_RELACIONAL_MAIOR)) {
                lexema += (char) this.la;
                lerArquivo(arquivo);
                if ((char) this.la == '=') {
                    return new Token(Simbolos.OP_RELACIONAL_MAIOR_IGUAL);
                } else {
                    voltarArquivo(arquivo);
                    return new Token(simbolo);
                }
            } else {
                lexema += (char) this.la;
                lerArquivo(arquivo);
                if ((char) this.la == '=') {
                    return new Token(Simbolos.OP_RELACIONAL_MENOR_IGUAL);
                } else {
                    voltarArquivo(arquivo);
                    return new Token(simbolo);
                }
            }
        }

    }

    private Token gerarOperadoresAritmeticos(PushbackReader arquivo) throws IOException {
        String lexema = "";
        lerArquivo(arquivo);
        Simbolos simbolo = Simbolos.verificarOperadores((char) la);

        if (!simbolo.equals(Simbolos.OP_ARITMETICO_BARRA) && !simbolo.equals(Simbolos.OP_ARITMETICO_IGUAL)) {
            return new Token(simbolo);
        } else if (simbolo.equals(Simbolos.OP_ARITMETICO_IGUAL)) {
            lexema += (char) this.la;
            lerArquivo(arquivo);
            if ((char) this.la == '=') {
                return new Token(Simbolos.OP_RELACIONAL_COMPARACAO);
            } else {
                voltarArquivo(arquivo);
                return new Token(Simbolos.OP_ARITMETICO_IGUAL);
            }
        } else {
            voltarArquivo(arquivo);
            return new Token(Simbolos.OP_ARITMETICO_BARRA);
        }
    }

    private Token gerarTokenLetras(PushbackReader arquivo) throws IOException, CompiladorException {
        String lexema = "";
        lerArquivo(arquivo);
        while (Character.isLetterOrDigit((char) this.la) || (char) this.la == '_') {
            lexema += (char) this.la;
            lerArquivo(arquivo);
        }
        voltarArquivo(arquivo);
        Simbolos simbolo = Simbolos.verificarPalavraReservada(lexema);
        if (simbolo.equals(Simbolos.IDENTIFICADOR)) {
            return new Token(simbolo, lexema);
        } else {
            return new Token(simbolo);
        }

    }

    private Token gerarTokenDecimal(PushbackReader arquivo, String parteInteira) throws IOException, CompiladorException {
        Token t = null;
        if (this.la == 46) {
            parteInteira += (char) this.la;
            lerArquivo(arquivo);
            if (!Character.isDigit((char) this.la)) {
                // Mudança de Linha, Fim de Arquivo ou Enter
                if (this.la == 10 || this.la == 13) {
                    throw new CompiladorException("FLOAT MAL FORMADO,QUEBRA DE LINHA DEPOIS DO PONTO");
                } else if (Character.isWhitespace((char) this.la)) {
                    if (this.la == 9) {
                        controladorArquivo.setColuna(controladorArquivo.getColuna() - 3);
                    }
                    throw new CompiladorException("FLOAT MAL FORMADO,ESPAÇO EM BRANCO DEPOIS DO PONTO");
                } else if (this.la == -1) {
                    throw new CompiladorException("FLOAT MAL FORMADO,FIM DE ARQUIVO DEPOIS DO PONTO");
                } else {
                    throw new CompiladorException("FLOAT MAL FORMADO. ESPERAVA UM DIGITO ENCONTROU UM: " + (char) this.la);
                }

            } else {
                parteInteira += (char) this.la;
                lerArquivo(arquivo);
                while (Character.isDigit((char) this.la)) {
                    parteInteira += (char) this.la;
                    lerArquivo(arquivo);
                }
                voltarArquivo(arquivo);
                return new Token(Simbolos.TIPO_FLOAT, parteInteira);
            }
        }
        return t;
    }

    private Token gerarTokenNumerico(PushbackReader arquivo) throws CompiladorException, IOException {
        String lexema = "";

        do {
            lerArquivo(arquivo);
            if (Character.isDigit((char) this.la)) {
                lexema += (char) this.la;
            } else if (this.la == 46) {
                return gerarTokenDecimal(arquivo, lexema);
            }
        } while (Character.isDigit((char) this.la));
        voltarArquivo(arquivo);
        return new Token(Simbolos.TIPO_INTEIRO, lexema);
    }

    private void lerArquivo(PushbackReader arquivo) throws IOException {
        this.la = arquivo.read();
        switch (this.la) {
            case 9:
                controladorArquivo.setColuna(controladorArquivo.getColuna() + 4);
                break;
            case 10:
                controladorArquivo.setColuna(0);
                controladorArquivo.setLinha(controladorArquivo.getLinha() + 1);
                break;
            default:
                controladorArquivo.setColuna(controladorArquivo.getColuna() + 1);
                break;
        }

    }

    private void voltarArquivo(PushbackReader arquivo) throws IOException {
        controladorArquivo.setColuna(controladorArquivo.getColuna() - 1);
        if (this.la == -1) {
            arquivo.unread(0);
        } else {
            arquivo.unread(this.la);
        }
    }

    public ControladorArquivo getControladorArquivo() {
        return controladorArquivo;
    }

    public void setControladorArquivo(ControladorArquivo controladorArquivo) {
        this.controladorArquivo = controladorArquivo;
    }

    public Scanner() {
        this.controladorArquivo = new ControladorArquivo();
    }

}