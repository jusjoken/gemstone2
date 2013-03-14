/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.apache.log4j.Logger;
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

    public static Logger LOG=Logger.getLogger(api.class);

    public static String Version = "1.0202" + "";
    private static boolean STVAppStarted = false;
    private static boolean LoadCompleted = false;
    
    public static boolean IsSTVAppStarted(){
        return STVAppStarted;
    }
    public static void SetSTVAppStarted(boolean value){
        STVAppStarted = value;
    }

    public static void main(String[] args){

        Load();
    }


    //load any Gemstone settings that need to load at application start
    //should be called from GemstonePlugin on the start event
    public static void Load(){
        if (LoadCompleted){
            LOG.debug("Load: api load called again - already loaded: " + util.LogInfo());
        }else{
            //get the gemstone instance which will also initiate logging
            Gemstone.getInstance();
            LOG.debug("Load: api load started: " + util.LogInfo());

            //initialize the ADM settings
            //these are now called directly by the client start
            //ADMutil.LoadADM();

            //Init the common Weather interface
            Weather.Init();

            //generate symbols to be used for new names
            util.InitNameGen();

            //ensure the gemstone file location exists
            util.InitLocations();

            //prepare the image cache
            ImageCache.Init();

            LoadCompleted = true;
            LOG.debug("Load: api load completed: " + util.LogInfo());
        }
        
   }

    public static void ClientStart(){
        //client specific settings
        util.InitLocations(); //call just to ensure this is completed
        ADMutil.ClientStart();
        util.LogConnectedClients();
        util.LogPlugins();
        util.gc(2);
        
    }

    public static void ClientExit(String UIContext){
        //remove client specific settings for Menus
        ADMutil.ClientExit(UIContext);
        util.LogConnectedClients();
        util.gc(2);
        
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
