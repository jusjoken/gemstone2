/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import sagex.UIContext;

/**
 *
 * @author SBANTA
 * @author JUSJOKEN
 * - 9/27/2011 - added LOG4J setup and Load method
 * - 10/1/2011 - implemented Load and InitLogger methods
 * - 10/28/2011 - Conversion to Diamond API for 4.x
 * - 04/02/2012 - Conversion to Gemstone API for 1.x
 */
public class api {

    public static Logger LOG=null;

    public static String Version = "1.005" + "";

    public static void main(String[] args){

        Load();
    }


    //load any Gemstone settings that need to load at application start
    public static void Load(){
        //initialize the Logging 
        InitLogger();
        //initialize the ADM settings
        util.AlertNonCompatiblePlugins();
        ADMutil.LoadADM();
        
        //generate symbols to be used for new names
        for (int idx = 0; idx < 10; ++idx)
            util.symbols[idx] = (char) ('0' + idx);
        for (int idx = 10; idx < 36; ++idx)
            util.symbols[idx] = (char) ('a' + idx - 10);
        
        //ensure the gemstone file location exists
        try{
            boolean success = (new File(util.UserDataLocation())).mkdirs();
            if (success) {
                LOG.debug("Load: Directories created for '" + util.UserDataLocation() + "'");
               }

            }catch (Exception ex){//Catch exception if any
                LOG.debug("Load: - error creating '" + util.UserDataLocation() + "'" + ex.getMessage());
            }
        ImageCache.Init();
        
   }

    public static void InitLogger(){
        //initialize the Logging 
        System.out.println("InitLogger: setting up logger");
        LOG = Logger.getLogger(api.class);
        String log4jfile = "STVs" + File.separator + "Gemstone" + File.separator + "Configuration" + File.separator + "Gemstone.log4j.properties";
        String log4jfullpath = sagex.api.Utility.GetWorkingDirectory(new UIContext(sagex.api.Global.GetUIContextName())) + File.separator + log4jfile;
        //check if the log4j property file exists and use defaults if it does not
        Boolean FileExists = (new File(log4jfullpath)).exists();
        if (FileExists){
            System.out.println("InitLogger: using '" + log4jfullpath + "' for log properties");
            PropertyConfigurator.configure(log4jfullpath);
        }else{
            //configure manually
            System.out.println("InitLogger: using internal defaults for log properties. Properties file not found '" + log4jfullpath + "'");
            Properties log4jProps = new Properties();
            log4jProps.put("log4j.rootCategory", "debug, Log");
            log4jProps.put("log4j.additivity.Gemstone", "false");
            log4jProps.put("log4j.appender.Gemstone", "org.apache.log4j.RollingFileAppender");
            log4jProps.put("log4j.appender.Gemstone.File", "logs/Gemstone.log");
            log4jProps.put("log4j.appender.Gemstone.layout", "org.apache.log4j.PatternLayout");
            log4jProps.put("log4j.appender.Gemstone.layout.ConversionPattern", "%d{EEE M/d HH:mm:ss.SSS} [%t] %-5p %c - %m%n");
            log4jProps.put("log4j.appender.Gemstone.MaxBackupIndex", "5");
            log4jProps.put("log4j.appender.Gemstone.MaxFileSize", "10000KB");
            log4jProps.put("log4j.appender.Gemstone.Threshold", "debug");
            log4jProps.put("log4j.additivity.Sage", "false");
            log4jProps.put("log4j.appender.Sage", "org.apache.log4j.ConsoleAppender");
            log4jProps.put("log4j.appender.Sage.layout", "org.apache.log4j.PatternLayout");
            log4jProps.put("log4j.appender.Sage.layout.ConversionPattern", "%d{EEE M/d HH:mm:ss.SSS} [%t] %-5p %c - %m%n");
            log4jProps.put("log4j.appender.Sage.Threshold", "debug");
            //log4jProps.put("log4j.logger.SDGroup", "debug,Gemstone,Sage");
            log4jProps.put("log4j.logger.Gemstone", "debug,Gemstone,Sage");
            PropertyConfigurator.configure(log4jProps);
        }
        LOG.info("Logger for Gemstone created successfully!");
//        LOG.debug("Test Log Message - debug");
//        LOG.info("Test Log Message - info");
//        LOG.warn("Test Log Message - warn");
//        LOG.error("Test Log Message - error");
//        LOG.fatal("Test Log Message - fatal");
   }

    public static int GetSeasonEpisodeNumber(Object MediaObject) {
        return MetadataCalls.GetSeasonEpisodeNumber(MediaObject);
    }

    public static void AddStaticContext(String Context, Object Value) {
        sagex.api.Global.AddStaticContext(new UIContext(sagex.api.Global.GetUIContextName()), Context, Value);

    }

    public static void ExecuteWidgeChain(String UID) {
        String UIContext = sagex.api.Global.GetUIContextName();
        System.out.println("Getting Ready to execute widget chain for " + UIContext);
        System.out.println("Actual context " + sagex.api.Global.GetUIContextName());
        Object[] passvalue = new Object[1];
        passvalue[0] = sagex.api.WidgetAPI.FindWidgetBySymbol(new UIContext(UIContext), UID);
        try {
            sage.SageTV.apiUI(UIContext, "ExecuteWidgetChainInCurrentMenuContext", passvalue);
        } catch (InvocationTargetException ex) {
            System.out.println("error executing widget" + api.class.getName() + ex);
        }
//        sagex.api.WidgetAPI.ExecuteWidgetChain(new UIContext(UIContext),sagex.api.WidgetAPI.FindWidgetBySymbol(new UIContext(UIContext),UID));
        }

    public static String GetVersion() {
        return Version;
    }
}
