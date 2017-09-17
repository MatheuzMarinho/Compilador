package scanner;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;


/**
 *
 * @author mathe
 */
public class Main {
    
     public static void main(String[] args) throws FileNotFoundException, IOException, CompiladorException{
        //FileInputStream entrada = new FileInputStream(args[0]);
        FileInputStream entrada = new FileInputStream("file.txt");
        InputStreamReader entradaFormatada = new InputStreamReader(entrada);
        Parser parser = new Parser(entradaFormatada);
          try {
              
              parser.analisar();
              
            
        }catch(CompiladorException exception){
            System.out.println(exception.getMessage());
            System.out.println("LINHA: "+parser.getScanner().getControladorArquivo().getLinha()+" COLUNA: "+parser.getScanner().getControladorArquivo().getColuna());
        }
     }
    
}
