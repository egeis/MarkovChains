package marrkovchains;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Richard Coan
 */
public class Markov {
    protected static Markov instance = null;
    protected FileHelper fh;
    
    protected int[][] _occurances = new int[676][26]; 
    protected double[][] _probability = new double[676][26];
        
    protected Markov() {
        fh = FileHelper.getInstance();
    }
    
    /** Get the Instance of the class.
     * @return FileHelper
     */
    public static Markov getInstance() {
        if(instance == null) {
            instance = new Markov();
        }
        
        return instance;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        instance = getInstance(); 
        
        //int a = (int) ('z' - 97);
        //System.out.println(a);
        
        if(args.length > 0) {
            boolean parsed = instance.parseText(args[0]);
                        
            if(parsed) {
                ArrayList<String[]> data = instance.builtTable();
                try {
                    CSVWriter writer = new CSVWriter(new FileWriter("output.csv"));
                    writer.writeAll(data);
                    writer.close();
                } catch(java.io.IOException err) {
                    System.out.println("Output File Could not be written:");
                    System.out.println("----"+err);
                    System.exit(0);
                }
               
               if(args.length > 1) {
                   String process[] = args[1].split("\\s");
                   if(process.length % 2 == 0) {
                        
                   }
               }         
            }
        } else {
            System.out.println("Requires Arguments: path-to-text.txt [optional] Generate world lenth # starting with two letters ex.\"aa 4 ab 3\"");
        }
    }
   
    protected ArrayList<String[]> builtTable() {
        ArrayList<String[]> data = new ArrayList<String[]>();
        data.add(new String[] {" ","a","b","c","d","e","f","g","h","i","j"
                ,"k","l","m","n","o","p","q","r","s","t","u","v","w","x",
                "y","z"});
        
        for (int x = 0; x < this._occurances.length; x++) {
            String[] temp = new String[27];
            
            int a = (int) Math.floor(x / 26);
            int b = (int) x % 26;
            
            char c = (char) (a + 97);
            char d = (char) (b + 97);
            
            temp[0] = c + "" + d;
            
            for(int y = 0; y < this._occurances[x].length; y++) {
                temp[y+1] = Double.toString(this._probability[x][y]);
            }
            
            data.add(temp);
        }
        
        return data;
    }
    
    protected boolean parseText(String _path) {
        String s = fh.Load(_path);
        
        for(double[] row : this._probability) {
            Arrays.fill(row, 0.0);
        }
        
        if(s != null) {
            s = s.replaceAll("[^a-zA-Z\\s]+", "");
            s = s.toLowerCase();
            
            //Occurances
            String[] parts = s.split("\\s");
            for( String part : parts) {
                //part = part.replaceAll("[^a-zA-Z]", "");
                if(part.length() <= 2) continue;
                
                char[] chars = part.toCharArray();
                for( int x = 0; x < chars.length - 2; x++ ) {
                    int a = (int) (chars[x] - 'a');
                    int b = (int) (chars[x+1] - 'a');
                    int c = (int) (chars[x+2] - 'a');
                    
                    this._occurances[a*26+b][c] += 1;
                }             
            }
            
            //Probabilities
            for (int x = 0; x < this._occurances.length; x++) {
                int total = 0;
                for(int y = 0; y < this._occurances[x].length; y++) {
                    total += this._occurances[x][y];
                }
                
                if(total != 0) {
                    for(int y = 0; y < this._occurances[x].length; y++) {
                        this._probability[x][y] = (double) this._occurances[x][y] / total;
                    }
                } else {
                    Arrays.fill(this._probability[x], 0.0);
                }
            }
            
            return true;
        }
        
        return false;
    }
    
}