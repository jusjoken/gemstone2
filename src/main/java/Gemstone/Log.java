/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.*;
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

    public static void Init(){
        Properties tProps = GetLogProps();
        Log4jConfigurator.configureQuietly("Gemstone", Log.class.getClassLoader());
        LOG.info("Init: log settings loaded.");
    }

    public static void StoreProps(Properties tProps){
        File configFile = new File(GetLogFileNameFull());
        try {
            OutputStream out = new FileOutputStream(configFile);
            tProps.store(out,"Created default log4j properties file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("StoreProps: log settings stored to:" + configFile.toString());
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
        String tLevel = props.getProperty("log4j.logger.Gemstone", "INFO, Gemstone");
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
            props.setProperty("log4j.logger.Gemstone", Level + ", Gemstone");
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
            Log4jConfigurator.reconfigure("Gemstone", props);
            originalLevel = GetLevel();
            LOG.info("SaveSettings: log settings saved.");
        }
    }
    
    public void LoadDefaults(){
        props.clear();
        props = GetPropDefaults();

        Log4jConfigurator.reconfigure("Gemstone", props);
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
        return util.GetSageTVRootDir() + File.separator + Const.LogFileName;
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
                LOG.info("GetLogProps: from file failed so loading from internal defaults");
                props = GetPropDefaults();
                StoreProps(props);
            }
        }else{
            //as the file was not found create it with the defaults
            LOG.info("GetLogProps: from internal defaults");
            props = GetPropDefaults();
            StoreProps(props);
        }
        //temp output to test this
        LOG.info("GetLogProps: level = '" + props.getProperty("log4j.logger.Gemstone", "NOT FOUND"));
        return props;
    }

    public static Properties GetPropDefaults(){
        //make sure these match the gemstone.log4j.properties file imbeded in the JAR
        Properties sProps = new Properties();
        sProps.setProperty("log4j.appender.Gemstone", "org.apache.log4j.RollingFileAppender");
        sProps.setProperty("log4j.appender.Gemstone.MaxFileSize", "10000KB");
        sProps.setProperty("log4j.appender.Gemstone.MaxBackupIndex", "2");
        sProps.setProperty("log4j.appender.Gemstone.File", "logs/Gemstone.log");
        sProps.setProperty("log4j.appender.Gemstone.Append", "false");
        sProps.setProperty("log4j.appender.Gemstone.layout", "org.apache.log4j.PatternLayout");
        sProps.setProperty("log4j.appender.Gemstone.layout.ConversionPattern", "%d [%t] %-5p %c - %m%n");
        sProps.setProperty("log4j.logger.Gemstone", "INFO, Gemstone");
        sProps.setProperty("log4j.additivity.Gemstone", "false");
        return sProps;
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
