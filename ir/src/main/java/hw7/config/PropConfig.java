package hw7.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Sushant on 8/10/2017.
 */
public class PropConfig {

    private Properties properties;

    public PropConfig(String propFile) {
        this(new File(propFile));
    }

    public PropConfig(File propFile) {
        this.properties = new Properties();
        try(FileInputStream finps = new FileInputStream(propFile)){
             properties.load(finps);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key){
        if(!containsKey(key)){
            throw new IllegalArgumentException("Properties file does not contain the key");
        }

        return this.properties.getProperty(key);
    }

    public boolean containsKey(String key){
        return this.properties.containsKey(key);
    }

}
