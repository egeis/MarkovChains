/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package marrkovchains;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Richard Coan
 */
public class FileHelper {
    protected static FileHelper instance = null;
    
    protected FileHelper() {
    }
    
    /** Get the Instance of the class.
     * @return FileHelper
     */
    public static FileHelper getInstance() {
        if(instance == null) {
            instance = new FileHelper();
        }
        
        return instance;
    }
    
    /** Load a file into a string
     * @param _path
     * @return s
     */
    public String Load(String _path) {        
        String s = "";
        File file = null;
        FileInputStream fis = null;
        byte[] data = new byte[1];
        
        try {
            System.out.println("Loading: "+new File(_path).getAbsolutePath());
            file = new File(_path);
            fis = new FileInputStream(file);
            data = new byte[(int)file.length()];
        } catch (java.io.FileNotFoundException err) {
            System.out.println(err);
            return s;
        }
                
        try {
            fis.read(data);
            fis.close();
            s = new String(data, "UTF-8");
        } catch (java.io.IOException err) {
            System.out.println(err);
            return s;
        }
        
        return s;
    }
}