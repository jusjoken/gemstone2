/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone2;

import java.io.File;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import sagex.UIContext;

/**
 *
 * @author jusjoken
 */
public class Theme {
    
    static private final Logger LOG = Logger.getLogger(Theme.class);

    public static Collection<String> GetHighlightSets(){
        SortedSet<String> tList = new TreeSet<String>();
        File HLSetLoc = new File(new File(util.ThemeLocation()), "Highlights");
        File[] files = HLSetLoc.listFiles();
        for (File file : files){
            if (file.isDirectory()){
                tList.add(file.getName());
            }
        }
        //LOG.debug("GetHighlightSets: found '" + tList + "'");
        return tList;
    }
    
    //stored setting for transparency will be as a percent between 0 and 100
    public static Integer GetTransparencyPercent(String ThemedVariable,String ThemedVariableDefault){
        String tProp = Const.BaseProp + Const.PropDivider + Const.ThemeProp + Const.PropDivider + Const.ThemeTrans + Const.PropDivider + ThemedVariable;
        //determine the default value from the themed variable
        Float fDefault = 1.0f;
        String transAsString = util.EvaluateAttribute(ThemedVariableDefault);
        if (!transAsString.equals(util.OptionNotFound)){
            fDefault = Float.valueOf(transAsString);
        }
        Integer iDefault = Math.round(fDefault * 100);
        Integer value = util.GetPropertyAsInteger(tProp, iDefault);
        LOG.debug("GetTransparencyPercent: for '" + ThemedVariable + " Prop '" + tProp + "' value = '" + value + "'");
        return value;
    }
    public static Float GetTransparency(String ThemedVariable,String ThemedVariableDefault){
        return (float) GetTransparencyPercent(ThemedVariable, ThemedVariableDefault) /100;
    }
    public static void SetTransparencyPercent(String ThemedVariable,Integer Value){
        String tProp = Const.BaseProp + Const.PropDivider + Const.ThemeProp + Const.PropDivider + Const.ThemeTrans + Const.PropDivider + ThemedVariable;
        util.SetProperty(tProp, Value + "");
        Float fValue = (float) Value/100;
        LOG.debug("SetTransparencyPercent: for '" + ThemedVariable + " Prop '" + tProp + "' value = '" + fValue + "'");
        sagex.api.Global.AddGlobalContext(new UIContext(sagex.api.Global.GetUIContextName()), ThemedVariable, fValue);
    }
    public static void ResetTransparency(String ThemedVariable){
        //set the transparency to the default by just removing the property
        String tProp = Const.BaseProp + Const.PropDivider + Const.ThemeProp + Const.PropDivider + Const.ThemeTrans + Const.PropDivider + ThemedVariable;
        util.RemoveProperty(tProp);
    }
}
