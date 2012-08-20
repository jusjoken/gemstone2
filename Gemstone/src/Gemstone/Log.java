/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import sagex.util.Log4jConfigurator;

/**
 *
 * @author jusjoken
 */
public class Log {
    static private final Logger LOG = Logger.getLogger(util.class);
    private Properties props = null;
    public static enum LogLevels2{FATAL,ERROR,WARN,DEBUG,INFO};
    public static List<String> LogLevels = Arrays.asList("INFO","DEBUG","WARN","ERROR","FATAL"); 
    private String DefaultLevel = "INFO";
    private String originalLevel = "";
    
    public Log() {
        props = GetLogProps();
        originalLevel = GetLevel();
    }
    
    public String GetLevel(){
        String tLevel = props.getProperty("log4j.logger.Gemstone", "INFO, GEMSTONE");
        String parts[] = tLevel.split(",");
        if (LogLevels.contains(parts[0])){
            return parts[0];          
        }else{
            return DefaultLevel;
        }
    }
    
    public void SetLevel(String Level){
        //check if this is a valid level
        if (LogLevels.contains(Level)){
            props.setProperty("log4j.logger.Gemstone", Level + ", GEMSTONE");
        }
    }
    
    public void SetLevelNext(){
        String tLevel = GetLevel();
        int levelIndex = LogLevels.indexOf(tLevel);
        if (levelIndex==-1){
            SetLevel(LogLevels.get(0));
        }else{
            levelIndex++;
            if (levelIndex>=LogLevels.size()){
                SetLevel(LogLevels.get(0));
            }else{
                SetLevel(LogLevels.get(levelIndex));
            }
        }
    }
    
    public boolean IsDirty(){
        if (!originalLevel.equals(GetLevel())){
            return true;
        }
        return false;
    }
    
    public void SaveSettings(){
        if (IsDirty()){
            Log4jConfigurator.reconfigure("gemstone", props);
            originalLevel = GetLevel();
            LOG.info("SaveSettings: log settings saved.");
        }
    }
    
    public static String GetLogFileNameFull(){
        return util.GetLocalWorkingDir() + File.separator + Const.LogFileName;
    }
    
    public static Properties GetLogProps(){
        //get the properties for the gemstone log either from the file if it exists or from the JAR
        LOG.info("GetLogProps: starting");
        File configFile = new File(GetLogFileNameFull());
        Properties props = new Properties();
        if (configFile.exists()){
            LOG.info("GetLogProps: from File");
            try {
                props = getPropsAndCloseStream(Const.LogFileName, configFile, new FileInputStream(configFile));
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            LOG.info("GetLogProps: from JAR");
            ClassLoader loader = Log4jConfigurator.class.getClassLoader();
            props = getPropsAndCloseStream(Const.LogFileName, configFile, loader.getResourceAsStream(Const.LogFileName));
        }
        //temp output to test this
        LOG.info("GetLogProps: level = '" + props.getProperty("log4j.logger.Gemstone", "NOT FOUND"));
        return props;
    }
    
    private static Properties getPropsAndCloseStream(String id, File file, InputStream is) {
        try {
                return getPropsforStream(id, file, is);
        } finally {
                closeStream(is);
        }
    }

    private static Properties getPropsforStream(String id, File file, InputStream is) {
        if (is == null){
            return new Properties();
        }

        // configure default logging
        try {
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (Exception e) {
            LOG.debug("Failed to load props for: " + id + " using file: " + file);
        }
        return new Properties();
    }

    private static void closeStream(InputStream fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                LOG.debug("closeStream: Failed to close InputStream");
            }
        }
    }

}
