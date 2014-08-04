package marrkovchains;

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
        boolean parsed = instance.parseText(args[0]);
        
        if(parsed) {
        
        }
        
    }
    
    public boolean parseText(String _path) {
        String s = fh.Load(_path);
       
        if(s != null) {
            s = s.replaceAll("[^a-zA-Z\\s]+", "");
            s = s.toLowerCase();
            
            //Occurances
            String[] parts = s.split("\\s");
            for( String part : parts) {
                part = part.replaceAll("[^a-zA-Z]", "");
                if(part.length() <= 2) continue;
                
                char[] chars = part.toCharArray();
                for( int x = 0; x < chars.length - 2; x++ ) {
                    int a = chars[x] - 'a';
                    int b = chars[x+1] - 'a';
                    int c = chars[x+2] - 'a';
                    
                    this._occurances[a*26+b][c] += 1;
                }             
            }
            
            //Probabilities
            for (int x = 0; x < this._occurances.length - 1; x++) {
                int total = 0;
                for(int y = 0; y < this._occurances[x].length; y++) {
                    total += this._occurances[x][y];
                }
                
                for(int y = 0; y < this._occurances[x].length; y++) {
                    this._probability[x][y] = (double) this._occurances[x][y] / total;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
}
