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
import sagex.UIContext;
import sagex.util.Log4jConfigurator;

/**
 *
 * @author jusjoken
 */
public class Log {
    static private final Logger LOG = Logger.getLogger(util.class);
    private Properties props = null;
    public static enum LogLevels2{FATAL,ERROR,WARN,DEBUG,INFO};
    private static List<String> LogLevels = Arrays.asList("DEBUG","INFO","WARN","ERROR"); 
    private String DefaultLevel = "INFO";
    private String originalLevel = "";
    
    public Log() {
        props = GetLogProps();
        originalLevel = GetLevel();
    }

    public List<String> GetLevels(){
        return LogLevels;
    }
    
    public boolean IsCurrentLevel(String Level){
        if (Level.equals(GetLevel())){
            return true;
        }
        return false;
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
    
    public void LoadDefaults(){
        props.clear();
        //make sure these match the gemstone.log4j.properties file imbeded in the JAR
        props.setProperty("log4j.appender.GEMSTONE", "org.apache.log4j.RollingFileAppender");
        props.setProperty("log4j.appender.GEMSTONE.MaxFileSize", "10000KB");
        props.setProperty("log4j.appender.GEMSTONE.MaxBackupIndex", "2");
        props.setProperty("log4j.appender.GEMSTONE.File", "logs/gemstone.log");
        props.setProperty("log4j.appender.GEMSTONE.Append", "false");
        props.setProperty("log4j.appender.GEMSTONE.layout", "org.apache.log4j.PatternLayout");
        props.setProperty("log4j.appender.GEMSTONE.layout.ConversionPattern", "%d [%t] %-5p %c - %m%n");
        props.setProperty("log4j.logger.Gemstone", "INFO, GEMSTONE");
        props.setProperty("log4j.additivity.Gemstone", "false");
        Log4jConfigurator.reconfigure("gemstone", props);
        originalLevel = GetLevel();
        //set the SageTV logging to it's default
        if (util.IsClient()){
            setSageLogSettingServer(Boolean.FALSE);
            setSageLogSettingClient(Boolean.FALSE);
        }else{
            setSageLogSettingServer(Boolean.FALSE);
        }
        LOG.info("LoadDefaults: log settings set to defaults.");
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
            props = getPropsfromJAR();
        }
        //temp output to test this
        LOG.info("GetLogProps: level = '" + props.getProperty("log4j.logger.Gemstone", "NOT FOUND"));
        return props;
    }
    
    private static Properties getPropsfromJAR(){
        File configFile = new File(GetLogFileNameFull());
        LOG.info("getPropsfromJAR: from JAR");
        ClassLoader loader = Log4jConfigurator.class.getClassLoader();
        return getPropsAndCloseStream(Const.LogFileName, configFile, loader.getResourceAsStream(Const.LogFileName));
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
            Properties sProps = new Properties();
            sProps.load(is);
            return sProps;
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
    
    public static String GetSageLogSettingLabel(){
        if (util.IsClient()){
            return "Server/Client Debug Log";
        }else{
            return "SageTV Debug Log";
        }
    }
    
    public static String GetSageLogSetting(){
        String retVal = "";
        if (util.IsClient()){
            //client needs to return the server and the client value combined
            if (getSageLogSettingServer()){
                retVal = "On/";
            }else{
                retVal = "Off/";
            }
            if (getSageLogSettingClient()){
                retVal = retVal + "On";
            }else{
                retVal = retVal + "Off";
            }
        }else{
            //just need the server value
            if (getSageLogSettingServer()){
                retVal = "On";
            }else{
                retVal = "Off";
            }
        }
        return retVal;
    }
    
    public static void SetSageLogSettingNext(){
        if (util.IsClient()){
            //client: need to rotate through server and client logging settings
            //values can be false/false, false/true, true/false, true,true
            if (!getSageLogSettingServer() && !getSageLogSettingClient()){
                setSageLogSettingServer(Boolean.FALSE);
                setSageLogSettingClient(Boolean.TRUE);
            }else if (!getSageLogSettingServer() && getSageLogSettingClient()){
                setSageLogSettingServer(Boolean.TRUE);
                setSageLogSettingClient(Boolean.FALSE);
            }else if (getSageLogSettingServer() && !getSageLogSettingClient()){
                setSageLogSettingServer(Boolean.TRUE);
                setSageLogSettingClient(Boolean.TRUE);
            }else{
                setSageLogSettingServer(Boolean.FALSE);
                setSageLogSettingClient(Boolean.FALSE);
            }
        }else{
            //just need to toggle the server value
            if (getSageLogSettingServer()){
                setSageLogSettingServer(Boolean.FALSE);
            }else{
                setSageLogSettingServer(Boolean.TRUE);
            }
        }
    }
    
    private static boolean getSageLogSettingClient(){
        return util.GetPropertyAsBoolean("debug_logging", false);
    }
    private static boolean getSageLogSettingServer(){
        return util.GetServerPropertyAsBoolean("debug_logging", false);
    }
    private static void setSageLogSettingClient(Boolean setting){
        util.SetProperty("debug_logging", setting.toString());
    }
    private static void setSageLogSettingServer(Boolean setting){
        util.SetServerProperty("debug_logging", setting.toString());
    }
    
}
