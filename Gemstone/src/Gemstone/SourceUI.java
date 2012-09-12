/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import sagex.phoenix.factory.IConfigurable;
import sagex.phoenix.vfs.views.ViewFactory;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */
public class SourceUI {
    static private final Logger LOG = Logger.getLogger(SourceUI.class);
    private String thisFlowName = "";
    private String PropLocation = "";
    private Integer thisLevels = 0;
    private SortedMap<Integer,PresentationUI> thisUIList = new TreeMap<Integer,PresentationUI>();
    private SortedMap<String,ConfigOption> ConfigOptionsList = new TreeMap<String,ConfigOption>();
    public static enum OrganizerType{GROUP,SORT};
    public static final String OptionNotSet = "NotSet";
    
    public SourceUI(String FlowName){
        if (FlowName!=null){
            this.thisFlowName = FlowName;
            PropLocation = GetPropertyLocation(FlowName);
            //based on the FlowName load the settings into this class from the properties file
            IConfigurable tOrganizer = new ViewFactory();
            for (String tOpt: tOrganizer.getOptionNames()){
                //skip some options types that we don't want the user to set
                String OptionsToSkip[] = { "name", "label", "description", "visible" };
                ArrayList OptionsToSkipList = new ArrayList();
                OptionsToSkipList.addAll(Arrays.asList(OptionsToSkip));
                ConfigOption tConfig = new ConfigOption(PropLocation, tOrganizer.getOption(tOpt));
                if (!OptionsToSkipList.contains(tConfig.getName())){
                    ConfigOptionsList.put(tConfig.getLabel(), tConfig);
                }
            }
            Refresh();
        }
    }
    
    public void Refresh(){
        thisUIList.clear();
        Integer counter = 0;
        PresentationUI tUI = null;
        do {
            tUI = new PresentationUI(thisFlowName,counter);
            counter++;
            if (tUI.HasContent()){
                thisUIList.put(counter, tUI);
            }
        } while (tUI.HasContent());
        thisLevels = thisUIList.size();
        LOG.debug(LogMessage());
    }
    
    public String Source(){
        return Flow.GetFlowSource(thisFlowName);
    }
    public String Name(){
        return "source:" + thisFlowName;
    }
    public String Label(){
        return Flow.GetFlowName(thisFlowName);
    }
    public Boolean HasConfigOptions(){
        return !ConfigOptionsList.isEmpty();
    }
    public HashSet<ConfigOption> ConfigOptions(){
        LinkedHashSet<ConfigOption> tList = new LinkedHashSet<ConfigOption>();
        for (ConfigOption tConfig:ConfigOptionsList.values()){
            tList.add(tConfig);
        }
        return tList;
    }
    
    //Presentation specific settings
    public Boolean HasPresentation(){
        if (HasUI()){
            return Boolean.TRUE;
        }else if (HasConfigOptionsSet()){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    public Boolean HasUI(){
        if (!thisUIList.isEmpty()){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    public Boolean HasConfigOptionsSet(){
        for (ConfigOption tConfig: ConfigOptionsList.values()){
            if (tConfig.IsSet()){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    public void ClearPresentation(){
        String tProp = Flow.GetFlowBaseProp(thisFlowName) + Const.PropDivider + Const.FlowSourceUI;
        util.RemovePropertyAndChildren(tProp);
        Refresh();
    }
    public Integer Levels(){
        return thisLevels;
    }
    public HashSet<PresentationUI> UIList(){
        LinkedHashSet<PresentationUI> tList = new LinkedHashSet<PresentationUI>();
        for (PresentationUI tUI:thisUIList.values()){
            tList.add(tUI);
        }
        
        return tList;
    }
    public HashSet<PresentationUI> UIListPlusOne(){
        LinkedHashSet<PresentationUI> tList = new LinkedHashSet<PresentationUI>();
        for (PresentationUI tUI:thisUIList.values()){
            tList.add(tUI);
        }
        //Add one more to be used if needed for adding a Level
        PresentationUI tUI = null;
        tUI = new PresentationUI(thisFlowName,thisLevels);
        tList.add(tUI);
        
        return tList;
    }
    public String LogMessage(){
        String tMess = Label() + "-'" + Source() + "'Levels'" + thisLevels + "'-";
        for (ConfigOption tConfig: ConfigOptionsList.values()){
            tMess = tMess + ":" + tConfig.getName() + "=" + tConfig.GetValue() + "(" + tConfig.GetValueLabel() + ")";
        }
        for (PresentationUI tUI: UIList()){
            tMess = tMess + "[" + tUI.LogMessage() + "]";
        }
        return tMess;
    }
    
    public static String GetPresentationProp(Integer Level){
        return Const.FlowSourceUI + Const.PropDivider + Const.FlowPresentation + Const.PropDivider + Level.toString() + Const.PropDivider;
    }
    public static String GetPropertyLocation(String FlowName){
        String tProp = Const.FlowSourceUI;
        return Const.FlowProp + Const.PropDivider + FlowName + Const.PropDivider +  tProp;
    }
    public static String GetPropertyLocation(String FlowName, String OrgType, Integer Level){
        String tProp = GetPresentationProp(Level) + OrgType;
        return Const.FlowProp + Const.PropDivider + FlowName + Const.PropDivider +  tProp;
    }
    public static String GetOrgValue(String FlowName, String OrgType, Integer Level, String Option){
        String tProp = GetPresentationProp(Level) + OrgType + Const.PropDivider + Option;
        return Flow.GetOptionName(FlowName, tProp, OptionNotSet);
    }
    public static void SetOrgValue(String FlowName, String OrgType, Integer Level, String Option, String NewValue){
        String tProp = GetPresentationProp(Level) + OrgType + Const.PropDivider + Option;
        Flow.SetOption(FlowName, tProp, NewValue);
    }
    public static Boolean IsSet(String Value){
        if (Value.equals(OptionNotSet)){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    //helper function to clear any list of ConfigOption objects
    public static void ClearConfigOptions(HashSet<ConfigOption> ConfigList){
        for (ConfigOption tConfig:ConfigList){
            tConfig.Clear();
        }
    }
    public static Boolean IsDefaultConfigOption(HashSet<ConfigOption> ConfigList, ConfigOption CheckItem){
        for (ConfigOption tConfig:ConfigList){
            if (tConfig.equals(CheckItem)){
                return Boolean.TRUE;
            }else{
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }

}
