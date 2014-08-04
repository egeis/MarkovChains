package marrkovchains;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Richard Coan
 */
public class Markov {
    protected static Markov instance = null;
    protected FileHelper fh;
    
    protected int[][] _occurances = new int[676][26];
    protected int[] _sums = new int[676]; 
    protected double[][] _probability = new double[676][26];
    
    protected String VOWELS = "aeiou";
    protected Random r = new Random();
        
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
                    data = instance.generateWords(process);
                    try {
                        CSVWriter writer = new CSVWriter(new FileWriter("output-words.csv"));
                        writer.writeAll(data);
                        writer.close();
                    } catch(java.io.IOException err) {
                        System.out.println("Output File Could not be written:");
                        System.out.println("----"+err);
                        System.exit(0);
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
    
    protected ArrayList<String[]> generateWords(String[] process) {
        ArrayList<String[]> data = new ArrayList<String[]>();
        if(process.length % 2 == 0) {
            for(int x = 0; x < process.length; x+=2) {
                int target = Integer.parseInt(process[x+1]);
                StringBuilder sb = new StringBuilder();
                sb.append(process[x]);
                String stopped = " ";
                
                for(int y = 2; y < target; y++) {
                    int a = (int) (sb.charAt(y-2) - 'a');
                    int b = (int) (sb.charAt(y-1) - 'a');
                    int occ = this._sums[a*26+b];
                    
                    if(occ == 0) {
                        stopped = "stopped early!";
                        //sb.append(this.VOWELS.charAt(this.r.nextInt( this.VOWELS.length() )));
                        break;
                    } else {
                        double sel = this.r.nextDouble();
                        double total = 0;
                        
                        for(int z = 0; z < this._probability[a*26+b].length; z++) {
                            total += this._probability[a*26+b][z];
                            if(sel <= total) {
                                sb.append( (char) (97 + z) );
                                break;
                            }
                        }
                    }
                }
                
                data.add(new String[] {process[x],process[x+1],sb.toString(),stopped});
            }
        } else {
           System.out.println("Invalid Generation: Each character selection MUST include a word length.");
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
                    this._sums[a*26+b] += 1;
                }             
            }
            
            //Probabilities
            for (int x = 0; x < this._occurances.length; x++) {
                if(this._sums[x] != 0) {
                    for(int y = 0; y < this._occurances[x].length; y++) {
                        this._probability[x][y] = (double) this._occurances[x][y] / this._sums[x];
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