/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.Map;
import org.apache.log4j.Logger;
import sage.SageTVPluginRegistry;
import sagex.plugin.AbstractPlugin;
import sagex.plugin.SageEvent;
import sagex.plugin.SageEvents;
import sagex.util.Log4jConfigurator;

/**
 *
 * @author jusjoken
 */

//saved for later - not called nor needed at this time 05-16-2012 jusjoken
public class GemstonePlugin extends AbstractPlugin {

    static private final Logger LOG = Logger.getLogger(GemstonePlugin.class);
    
    public GemstonePlugin(SageTVPluginRegistry registry) { 
        super(registry); 
        LOG.debug("Gemstone Plugin Loaded");
    }    

    @SageEvent(value = SageEvents.AllPluginsLoaded, background = true)
    public void onPluginsLoaded() {  
        LOG.debug("onPluginsLoaded: All Plugins Loaded");
        //util.HandleNonCompatiblePlugins();
    }  
    
    @SageEvent(value=SageEvents.ClientConnected, background=true)
    public void onClientConnected(Map args) {
        LOG.debug("onClientConnected: '" + sagex.api.Global.GetUIContextName() + "' args '" + args + "'");
    }

    @SageEvent(value=SageEvents.ClientDisconnected, background=true)
    public void onClientDisconnected(Map args) {
        LOG.debug("onClientDisconnected: '" + sagex.api.Global.GetUIContextName() + "' args '" + args + "'");
    }    

//    @Override
//    public void sageEvent(String eventName, Map eventVars) {
//        super.sageEvent(eventName, eventVars);
//        if (eventName.equals(SageEvents.ClientConnected)){
//            LOG.debug("sageEvent: ClientConnected '" + sagex.api.Global.GetUIContextName() + "' eventVars '" + eventVars + "'");
//        }
//        if (eventName.equals(SageEvents.ClientDisconnected)){
//            LOG.debug("sageEvent: ClientDisconnected '" + sagex.api.Global.GetUIContextName() + "' eventVars '" + eventVars + "'");
//        }
//    }
    
    @Override
    public void start() {      
        LOG.debug("start: called for GemstonePlugin");
        super.start(); 
        api.InitLogger();
    }
    
}
