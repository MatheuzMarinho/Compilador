/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author mathe
 */
public class SemanticoService {

    private final List<ItemTabela> tabela;

    public SemanticoService(List<ItemTabela> tabela) {
        this.tabela = tabela;
    }

    public static boolean validarEscopo(List<ItemTabela> tabela, String lexema, int escopo) throws CompiladorException {
        for (ItemTabela i : tabela) {
            if (i.getEscopo() == escopo) {
                if (i.getLexema().equals(lexema)) {
                    throw new CompiladorException("Variavel está sendo utilizada neste escopo!");
                }
            }
        }
        return true;
    }

    public static Token verificaSeDeclada(List<ItemTabela> tabela, Token token) throws CompiladorException {
        if (token.getSimbolo().equals(Simbolos.IDENTIFICADOR)) {
            List<ItemTabela> t = new ArrayList<ItemTabela>(tabela);
            Collections.reverse(t);
            for (ItemTabela i : t) {
                if (i.getLexema().equals(token.getLexema())) {
                    token.setSimbolo(i.getSimbolo());
                    return token;
                }
            }
        } else {
            if (isInt(token.getLexema())) {
                return token;
            }
            if (isFloat(token.getLexema())) {
                return token;
            }
            if (token.getSimbolo().equals(Simbolos.TIPO_CHAR)) {
                return token;
            }
        }

        throw new CompiladorException("Variavel precisa ser declarada antes de ser utilizada!");
    }

    public static boolean isInt(String input) {
        boolean parsable = true;
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            parsable = false;
        }
        return parsable;
    }

    public static boolean isFloat(String input) {
        boolean parsable = true;
        try {
            Float.parseFloat(input);
        } catch (NumberFormatException e) {
            parsable = false;
        }
        return parsable;
    }

    public static void removerEscopo(List<ItemTabela> tabela, int escopo) {
        tabela.removeIf((ItemTabela i) -> {
            return i.getEscopo() == escopo;
        });
    }

    public static void verificaExpressaoRelacional(Token opUm, Token opDois) throws CompiladorException, CompiladorException {

        if (opUm.getSimbolo().equals(opDois.getSimbolo())) {

        } else {
            if (opUm.getSimbolo().equals(Simbolos.TIPO_CHAR) || opDois.getSimbolo().equals(Simbolos.TIPO_CHAR)) {
                throw new CompiladorException("Tipo dos identificadores diferentes. CHAR só faz operação com CHAR");
            }
        }

    }

}
