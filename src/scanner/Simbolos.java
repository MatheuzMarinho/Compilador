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
public enum Simbolos {

    //Tipo de dados
    FIM_DE_ARQUIVO(0),
    TIPO_INTEIRO(1),
    TIPO_FLOAT(2),
    TIPO_CHAR(3),
    IDENTIFICADOR(4),
    PR_INT(5),
    PR_FLOAT(6),
    PR_CHAR(7),
    PR_MAIN(8),
    PR_IF(9),
    PR_ELSE(10),
    PR_WHILE(11),
    PR_DO(12),
    PR_FOR(13),
    OP_ARITMETICO_MAIS(14),
    OP_ARITMETICO_MENOS(15),
    OP_ARITMETICO_MULTIPLICACAO(16),
    OP_ARITMETICO_BARRA(17),
    OP_ARITMETICO_IGUAL(18),
    ESP_ABRE_PARENTESES(19),
    ESP_FECHA_PARENTESES(20),
    ESP_ABRE_COLCHETES(21),
    ESP_FECHA_COLCHETES(22),
    ESP_VIRGULA(23),
    ESP_PONTO_E_VIRGULA(24),
    OP_RELACIONAL_MENOR(25),
    OP_RELACIONAL_MAIOR(26),
    OP_RELACIONAL_MENOR_IGUAL(27),
    OP_RELACIONAL_MAIOR_IGUAL(28),
    OP_RELACIONAL_COMPARACAO(29),
    OP_RELACIONAL_DIFERENTE(30);

    private final int simboloCodigo;

    private Simbolos(int simboloCodigo) {
        this.simboloCodigo = simboloCodigo;
    }

    public int getSimboloCodigo() {
        return this.simboloCodigo;
    }

    public static Simbolos verificarPalavraReservada(String lexema) {
        switch (lexema) {
            case "int":
                return Simbolos.PR_INT;
            case "float":
                return Simbolos.PR_FLOAT;
            case "char":
                return Simbolos.PR_CHAR;
            case "main":
                return Simbolos.PR_MAIN;
            case "if":
                return Simbolos.PR_IF;
            case "else":
                return Simbolos.PR_ELSE;
            case "while":
                return Simbolos.PR_WHILE;
            case "do":
                return Simbolos.PR_DO;
            case "for":
                return Simbolos.PR_FOR;
            default:
                return Simbolos.IDENTIFICADOR;
        }
    }

    public static Simbolos verificarOperadores(char c) {
        String lexema = "" + c;
        switch (lexema) {
            case "+":
                return Simbolos.OP_ARITMETICO_MAIS;
            case "-":
                return Simbolos.OP_ARITMETICO_MENOS;
            case "*":
                return Simbolos.OP_ARITMETICO_MULTIPLICACAO;
            case "/":
                return Simbolos.OP_ARITMETICO_BARRA;
            case "=":
                return Simbolos.OP_ARITMETICO_IGUAL;
        }
        return null;
    }

    public static String converteOperadores(Simbolos s) {
        switch (s) {
            case OP_ARITMETICO_MAIS:
                return "+";
            case OP_ARITMETICO_MENOS:
                return "-";
            case OP_ARITMETICO_BARRA:
                return "/";
            default:
                return "*";
        }
    }

    public static String converteRelacionais(Simbolos s) {
        switch (s) {
            case OP_RELACIONAL_COMPARACAO:
                return "==";
            case OP_RELACIONAL_DIFERENTE:
                return "!=";
            case OP_RELACIONAL_MAIOR:
                return ">";
            case OP_RELACIONAL_MAIOR_IGUAL:
                return ">=";
            case OP_RELACIONAL_MENOR:
                return "<";
            case OP_RELACIONAL_MENOR_IGUAL:
                return "<=";
            default:
                return "";
        }
    }

    public static Simbolos verificarEspeciais(char c) {
        String lexema = "" + c;
        switch (lexema) {
            case "(":
                return Simbolos.ESP_ABRE_PARENTESES;
            case ")":
                return Simbolos.ESP_FECHA_PARENTESES;
            case "{":
                return Simbolos.ESP_ABRE_COLCHETES;
            case "}":
                return Simbolos.ESP_FECHA_COLCHETES;
            case ",":
                return Simbolos.ESP_VIRGULA;
            case ";":
                return Simbolos.ESP_PONTO_E_VIRGULA;
        }
        return null;
    }

    public static Simbolos verificarRelacionais(char c) {
        String lexema = "" + c;
        switch (lexema) {
            case ">":
                return Simbolos.OP_RELACIONAL_MAIOR;
            case "<":
                return Simbolos.OP_RELACIONAL_MENOR;
            case ">=":
                return Simbolos.OP_RELACIONAL_MAIOR_IGUAL;
            case "<=":
                return Simbolos.OP_RELACIONAL_MENOR_IGUAL;
            case "!=":
                return Simbolos.OP_RELACIONAL_DIFERENTE;
            case "==":
                return Simbolos.OP_RELACIONAL_COMPARACAO;
        }
        return null;
    }

    public static Boolean verificarTipo(Simbolos s) {

        return s.equals(Simbolos.PR_FLOAT) || s.equals(Simbolos.PR_INT) || s.equals(Simbolos.PR_CHAR);

    }

    public static Boolean verificarFator(Simbolos s) {

        return s.equals(Simbolos.TIPO_CHAR) || s.equals(Simbolos.TIPO_INTEIRO) || s.equals(Simbolos.TIPO_FLOAT) || s.equals(Simbolos.IDENTIFICADOR) || s.equals(Simbolos.ESP_ABRE_PARENTESES);

    }

    public static Boolean verificarMultDiv(Simbolos s) {

        return s.equals(Simbolos.OP_ARITMETICO_BARRA) || s.equals(Simbolos.OP_ARITMETICO_MULTIPLICACAO);

    }

    public static Boolean verificarAddSub(Simbolos s) {

        return s.equals(Simbolos.OP_ARITMETICO_MAIS) || s.equals(Simbolos.OP_ARITMETICO_MENOS);

    }

    public static Boolean verificarRelacional(Simbolos s) {
        return s.equals(Simbolos.OP_RELACIONAL_COMPARACAO) || s.equals(Simbolos.OP_RELACIONAL_DIFERENTE) || s.equals(Simbolos.OP_RELACIONAL_MAIOR) || s.equals(Simbolos.OP_RELACIONAL_MAIOR_IGUAL) || s.equals(Simbolos.OP_RELACIONAL_MENOR) || s.equals(Simbolos.OP_RELACIONAL_MENOR_IGUAL);
    }

}
