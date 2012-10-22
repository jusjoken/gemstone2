/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import sagex.UIContext;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.views.ViewFactory;
import sagex.phoenix.vfs.views.ViewFolder;


/**
 *
 * @author jusjoken
 * - 10/10/2011 - class for all Flow related calls - for 4.x this will be replaced with a Flow class to encapsulate Flow as an object
 * - 04/04/2012 - updated for Gemstone
 * 
 */
public class Flow {
    
    static private final Logger LOG = Logger.getLogger(Flow.class);
    public static HashMap<String,String> InternalFlowTypes = new HashMap<String,String>();
    
    public static String GetFlowsBaseProp(){
        return Const.BaseProp + Const.PropDivider + Const.FlowProp + Const.PropDivider;
    }
    public static String GetFlowBaseProp(String FlowName){
        return GetFlowsBaseProp() + FlowName;
    }
    
    public static Boolean GetTrueFalseOption(String PropSection, String PropName, Boolean DefaultValue){
        return util.GetTrueFalseOptionBase(Boolean.TRUE, PropSection, PropName, DefaultValue);
    }
    
    public static String GetTrueFalseOptionName(String PropSection, String PropName, String TrueValue, String FalseValue){
        return util.GetTrueFalseOptionNameBase(Boolean.TRUE, PropSection, PropName, TrueValue, FalseValue, Boolean.FALSE);
    }
    public static String GetTrueFalseOptionName(String PropSection, String PropName, String TrueValue, String FalseValue, Boolean DefaultValue){
        return util.GetTrueFalseOptionNameBase(Boolean.TRUE, PropSection, PropName, TrueValue, FalseValue, DefaultValue);
    }

    public static void SetTrueFalseOptionNext(String PropSection, String PropName){
        util.SetTrueFalseOptionNextBase(Boolean.TRUE, PropSection, PropName, Boolean.FALSE);
    }
    public static void SetTrueFalseOptionNext(String PropSection, String PropName, Boolean DefaultValue){
        util.SetTrueFalseOptionNextBase(Boolean.TRUE, PropSection, PropName, DefaultValue);
    }
    public static void SetTrueFalseOption(String PropSection, String PropName, Boolean NewValue){
        util.SetTrueFalseOptionBase(Boolean.TRUE, PropSection, PropName, NewValue);
    }
    
    public static String GetOptionName(String PropSection, String PropName, String DefaultValue){
        return util.GetOptionNameBase(Boolean.TRUE, PropSection, PropName, DefaultValue);
    }
    
    public static void SetOption(String PropSection, String PropName, String NewValue){
        util.SetOptionBase(Boolean.TRUE, PropSection, PropName, NewValue);
    }
    
    public static String GetListOptionName(String PropSection, String PropName, String OptionList, String DefaultValue){
        return util.GetListOptionNameBase(Boolean.TRUE, PropSection, PropName, OptionList, DefaultValue);
    }
    
    public static void SetListOptionNext(String PropSection, String PropName, String OptionList){
        util.SetListOptionNextBase(Boolean.TRUE, PropSection, PropName, OptionList);
    }

    public static String PropertyListasString(String PropSection, String PropName){
        return util.PropertyListasStringBase(Boolean.TRUE, PropSection, PropName);
    }
    public static List<String> PropertyList(String PropSection, String PropName){
        return util.PropertyListBase(Boolean.TRUE, PropSection, PropName);
    }
    public static void PropertyListAdd( String PropSection, String PropName, String NewValue){
        util.PropertyListAddBase(Boolean.TRUE, PropSection, PropName, NewValue);
    }
    public static void PropertyListRemove(String PropSection, String PropName, String NewValue){
        util.PropertyListRemoveBase(Boolean.TRUE, PropSection, PropName, NewValue);
    }
    public static void PropertyListRemoveAll(String PropSection, String PropName){
        util.PropertyListRemoveAllBase(Boolean.TRUE, PropSection, PropName);
    }
    public static Boolean PropertyListContains(String PropSection, String PropName, String NewValue){
        return util.PropertyListContainsBase(Boolean.TRUE, PropSection, PropName, NewValue);
    }
    public static Boolean PropertyListContains(String PropSection, String PropName, ViewFolder Folder){
        return util.PropertyListContainsBase(Boolean.TRUE, PropSection, PropName, Folder);
    }
    public static Integer PropertyListCount(String PropSection, String PropName){
        return util.PropertyListCountBase(Boolean.TRUE, PropSection, PropName);
    }
    
    public static ArrayList<String> GetFlows(){
        String[] FlowItems = sagex.api.Configuration.GetSubpropertiesThatAreBranches(new UIContext(sagex.api.Global.GetUIContextName()),GetFlowsBaseProp());
        if (FlowItems.length>0){
            //Add the flows in Sort Order
            TreeMap<Integer,String> tSortedList = new TreeMap<Integer,String>();
            Integer counter = 0;
            for (String tFlow:FlowItems){
                //make sure this is a real Flow entry with a name property
                if (!GetFlowName(tFlow).equals(Const.FlowNameNotFound)){
                    counter++;
                    Integer thisSort = GetFlowSort(tFlow);
                    if (thisSort==0){
                        thisSort = counter;
                    }
                    while(tSortedList.containsKey(thisSort)){
                        counter++;
                        thisSort = counter;
                    }
                    tSortedList.put(thisSort, tFlow);
                    //LOG.debug("GetFlows: '" + tFlow + "' added at '" + thisSort + "'");
                }
            }
            return new ArrayList<String>(tSortedList.values()); 
        }else{
            return new ArrayList<String>();
        }
    }

    public static String GetNextPrevFlow(String Element, Integer Delta){
        ArrayList<String> tflows = GetFlows();
        Integer currLocation = tflows.indexOf(Element);
        if (currLocation==-1){
            return tflows.get(0);
        }
        Integer NextPrev = currLocation + Delta;
        if (NextPrev<0){
            return tflows.get(tflows.size()-1);
        }else if(NextPrev>=tflows.size()){
            return tflows.get(0);
        }else{
            return tflows.get(NextPrev);
        }
    }
    
    public static Boolean IsValidFlow(String Element){
        if (Element==null){
            return Boolean.FALSE;
        }else{
            if (GetFlowName(Element).equals(Const.FlowNameNotFound)){
                return Boolean.FALSE;
            }else{
                return Boolean.TRUE;
            }
        }
    }
    
    //old method did not always return a valid result
    public static Boolean IsValidFlowOld(String Element){
        String[] FlowItems = sagex.api.Configuration.GetSubpropertiesThatAreBranches(new UIContext(sagex.api.Global.GetUIContextName()),GetFlowsBaseProp());
        if (FlowItems.length>0){
            if (Arrays.asList(FlowItems).contains(Element)){
                return Boolean.TRUE;
            }else{
                return Boolean.FALSE;
            }
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static Integer GetFlowSort(String Element){
        return util.GetPropertyAsInteger(GetFlowBaseProp(Element) + Const.PropDivider + Const.FlowSort, 0);
    }
    
    public static void SetFlowSort(String Element, Integer iSort){
        util.SetProperty(GetFlowBaseProp(Element) + Const.PropDivider + Const.FlowSort, iSort.toString());
    }
    
    public static void SaveFlowOrder(ArrayList<String> inFlows){
        Integer counter = 0;
        for(String thisflow:inFlows){
            counter++;
            SetFlowSort(thisflow, counter);
        }
    }
    
    public static ArrayList<String> MoveFlowinList(ArrayList<String> inFlows, String thisFlow, Integer Delta){
        Integer CurrentFlowLocation = inFlows.indexOf(thisFlow);
        //Boolean Moved = Boolean.FALSE;
        if (CurrentFlowLocation==-1){
            LOG.debug("MoveFlowinList: '" + thisFlow + "' not found in list");
            return inFlows;
        }else{
            if (Delta>0){ //move down in list
                if (CurrentFlowLocation+Delta<inFlows.size()){
                    inFlows.remove(thisFlow);
                    inFlows.add(CurrentFlowLocation+Delta, thisFlow);
                    //Moved = Boolean.TRUE;
                }
            }else{ //move up in list
                if (CurrentFlowLocation+Delta>=0){
                    inFlows.remove(thisFlow);
                    inFlows.add(CurrentFlowLocation+Delta, thisFlow);
                    //Moved = Boolean.TRUE;
                }
            }
        }
        return inFlows;
    }
    
    public static String CreateNewFlow() {
        return CreateNewFlow(Const.FlowTypeDefault, Const.FlowTypeDefault);
    }
    public static String CreateNewFlow(String ViewName, String ViewType) {
        if (ViewName==null){
            LOG.debug("CreateNewFlow: request for null name returned 0");
            return "0";
        }
        String Element = util.GenerateRandomName();
        System.out.print("CreateNewFlow: Element '" + Element + "'");
        Integer NewSort = GetFlows().size()+1;  //sort numbers are base of 1 - 0 is invalid
        //Save the Name and Type for the Flow
        String FlowNameProp = GetFlowBaseProp(Element) + Const.PropDivider + Const.FlowName;
        System.out.print("CreateNewFlow: FlowNameProp '" + FlowNameProp + "' ViewName '" + ViewName + "'");
        util.SetProperty(FlowNameProp, ViewName);
        String FlowTypeProp = GetFlowBaseProp(Element) + Const.PropDivider + Const.FlowType;
        System.out.print("CreateNewFlow: FlowTypeProp '" + FlowTypeProp + "' ViewType '" + ViewType + "'");
        util.SetProperty(FlowTypeProp, ViewType);
        //Save the sort order
        SetFlowSort(Element, NewSort);
        return Element;
    }
  
    public static String RemoveFlow(String Element) {
        if (Element==null){
            LOG.debug("RemoveFlow: request for null name returned 0");
            return "0";
        }
        String OldCVPropName = GetFlowBaseProp(Element);
        util.RemovePropertyAndChildren(OldCVPropName);
        return "1";
    }
    
    public static void RemoveAllFlows(){
        for (String tFlow: GetFlows()){
            RemoveFlow(tFlow);
        }
    }

    public static String RenameFlow(String Element, String NewViewName) {
        String FlowNameProp = Flow.GetFlowBaseProp(Element) + Const.PropDivider + Const.FlowName;
        util.SetProperty(FlowNameProp, NewViewName);
        return "1";
    }

    public static String GetFlowName(String name){
        if (name==null){
            //LOG.debug("GetFlowName: request for null name returned NotFound");
            return util.OptionNotFound;
        }
        String FlowNameProp = Flow.GetFlowBaseProp(name) + Const.PropDivider + Const.FlowName;
        return util.GetProperty(FlowNameProp, Const.FlowNameNotFound);
    }
    
    public static void AddFlowType(String FlowType, String ShortName){
        InternalFlowTypes.put(FlowType, ShortName);
    }
   
    public static String GetFlowType(String name){
        if (name==null){
            //LOG.debug("GetFlowType: request for null name returned NotFound");
            return util.OptionNotFound;
        }
        String FlowTypeProp = Flow.GetFlowBaseProp(name) + Const.PropDivider + Const.FlowType;
        if (IsValidFlow(name)){
            return util.GetProperty(FlowTypeProp, Const.FlowTypeDefault);
        }else{
            return Const.FlowTypeNotFound;
        }
    }
    
    public static String GetFlowTypeShortName(String name){
        if (name==null){
            LOG.debug("GetFlowType: request for null name returned NotFound");
            return util.OptionNotFound;
        }
        String FlowTypeProp = Flow.GetFlowBaseProp(name) + Const.PropDivider + Const.FlowType;
        String tFlow = util.GetProperty(FlowTypeProp, Const.FlowTypeDefault);
        if (InternalFlowTypes.containsKey(tFlow)){
            return InternalFlowTypes.get(tFlow);
        }else{
            return tFlow.replaceAll("Flow", "").trim();
        }
    }

    public static Boolean IsValidFlowType(String name){
        if (name==null){
            LOG.debug("GetFlowType: request for null name returned NotFound");
            return Boolean.FALSE;
        }
        String FlowTypeProp = Flow.GetFlowBaseProp(name) + Const.PropDivider + Const.FlowType;
        String tFlow = util.GetProperty(FlowTypeProp, Const.FlowTypeDefault);
        if (InternalFlowTypes.containsKey(tFlow)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

    public static String ChangeFlowType(String Element, String NewViewType) {
        String FlowTypeProp = Flow.GetFlowBaseProp(Element) + Const.PropDivider + Const.FlowType;
        String OldType = GetFlowType(Element);
        String OldName = GetFlowName(Element);
        util.SetProperty(FlowTypeProp, NewViewType);
        if (OldType.equals(OldName)){
            RenameFlow(Element, NewViewType);
        }
        return "1";
    }
    
    public static void SetFlowType(String name, String FlowType){
        if (name==null){
            LOG.debug("SetFlowType: request for null name returned NotFound");
        }
        String FlowTypeProp = Flow.GetFlowBaseProp(name) + Const.PropDivider + Const.FlowType;
        util.SetProperty(FlowTypeProp, FlowType);
    }

    public static Collection<String> FlowTypes(){
        return InternalFlowTypes.keySet();
    }
    
    public static void CreateDefaultFlows(){
        for (String tFlow: FlowTypes()){
            if (!IsDuplicate(tFlow)){
                CreateNewFlow(tFlow, tFlow);
            }
        }
    }
    
    public static boolean IsDuplicate(String FlowName){
        for (String flow:GetFlows()){
            if (GetFlowName(flow).equals(FlowName)){
                return true;
            }
        }
        return false;
    }

    public static String GetFlowSource(String name){
        if (name==null){
            //LOG.debug("GetFlowSource: request for null name returned NotFound");
            return util.OptionNotFound;
        }
        String FlowSourceProp = Flow.GetFlowBaseProp(name) + Const.PropDivider + Const.FlowSource;
        return util.GetProperty(FlowSourceProp, util.OptionNotFound);
    }
    public static String GetSourceName(String tSource){
        return GetSourceName(tSource, Boolean.FALSE);
    }
    public static String GetSourceName(String tSource, Boolean AllowNongemstoneSources){
        //LOG.debug("GetSourceName: getting source name for '" + tSource + "'");
        if (tSource==null){
            LOG.debug("GetSourceName: request for null name returned NotFound");
            return Const.FlowSourceDefaultName;
        }
        if (tSource.equals(util.OptionNotFound)){
            LOG.debug("GetSourceName: Not Found for '" + tSource + "'");
            return Const.FlowSourceDefaultName;
        }else{
            String tSourceName = Const.FlowSourceDefaultName;
            for (sagex.phoenix.vfs.views.ViewFactory factory: SourceList(AllowNongemstoneSources)){
                if (factory.getName().equals(tSource)){
                    tSourceName = factory.getLabel();
                    break;
                }
            }
            //LOG.debug("GetSourceName: returning '" + tSourceName + "' for '" + tSource + "'");
            return tSourceName;
        }
    }
    public static String GetFlowSourceName(String name){
        return GetFlowSourceName(name, Boolean.FALSE);
    }
    public static String GetFlowSourceName(String name, Boolean AllowNongemstoneSources){
        if (name==null){
            LOG.debug("GetFlowSourceName: request for null name returned NotFound");
            return Const.FlowSourceDefaultName;
        }
        String tSource = GetFlowSource(name);
        return GetSourceName(tSource,AllowNongemstoneSources);
    }
    //
    public static void SetFlowSource(String name, String FlowSource){
        if (name==null){
            LOG.debug("SetFlowSource: request for null name returned NotFound");
        }
        String FlowSourceProp = Flow.GetFlowBaseProp(name) + Const.PropDivider + Const.FlowSource;
        util.SetProperty(FlowSourceProp, FlowSource);
    }
    
    public static Boolean IsValidSource(String tSource){
        return IsValidSource(tSource, Boolean.FALSE);
    }
    public static Boolean IsValidSource(String tSource, Boolean AllowNongemstoneSources){
        if (tSource==null){
            LOG.debug("IsValidSource: request for null name returned false");
            return Boolean.FALSE;
        }
        if (tSource.equals(util.OptionNotFound)){
            LOG.debug("IsValidSource: Not Found for '" + tSource + "'");
            return Boolean.FALSE;
        }else{
            for (sagex.phoenix.vfs.views.ViewFactory factory: SourceList(AllowNongemstoneSources)){
                if (factory.getName().equals(tSource)){
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Set<ViewFactory> SourceList(Boolean AllowNongemstoneSources){
        Set<ViewFactory> Sources = null;
        if (AllowNongemstoneSources){
            Sources = new HashSet(phoenix.umb.GetViewFactories());
        }else{
            Sources = phoenix.umb.GetViewFactories("gemsource");
        }
        return Sources;
    }
    
    public static void Test(){
        for (sagex.phoenix.vfs.views.ViewFactory factory: phoenix.umb.GetVisibleViews()){
            LOG.debug("Factory = '" + factory.getLabel() + "' Name = '" + factory.getName() + "'");
        }
        for (sagex.phoenix.vfs.views.ViewFactory factory: phoenix.umb.GetViewFactories()){
            LOG.debug("Factory = '" + factory.getLabel() + "' Name = '" + factory.getName() + "'");
        }
        
    }
    public static void main(String[] args){
        
        for (sagex.phoenix.vfs.views.ViewFactory factory: phoenix.umb.GetVisibleViews()){
            LOG.debug("Factory = '" + factory.getLabel() + "' Name = '" + factory.getName() + "'");
        }

    }

    public static String DisplaySeasonEpisode(String Element, Object MediaObject) {
        IMediaResource IMR = Source.ConvertToIMR(MediaObject);
        String SEFormat = Flow.GetOptionName(Element,"SEFormat","S1E01");
        //LOG.debug("DisplaySeasonEpisodeVFS: Object '" + MediaObject.getClass() + " Media '" + MediaObject + "'" );
        return MetadataCalls.DisplaySeasonEpisode(phoenix.media.GetMediaObject(IMR), SEFormat);
    }
    
//    public static String DisplaySeasonEpisode(String Element, Object MediaObject) {
//        String SEFormat = Flow.GetOptionName(Element,"SEFormat","S1E01");
//        //LOG.debug("DisplaySeasonEpisode: SEFormat = '" + SEFormat + "' for '" + Element + "' Object '" + MediaObject.getClass() + " Media '" + MediaObject + "'" );
//        return MetadataCalls.DisplaySeasonEpisode(MediaObject, SEFormat);
//    }
    
    public static void ClearOverrideFlows(String FlowName, int MaxLevel){
        for (int l=2;l<=MaxLevel;l++){
            if (HasOverrideFlow(FlowName, l)){
                String Prop = GetFlowBaseProp(FlowName) + Const.PropDivider + Const.FlowOverride + Const.PropDivider + l;
                util.RemoveProperty(Prop);
            }
        }
    }
    
    public static boolean HasOverrideFlows(String FlowName, int MaxLevel){
        for (int l=2;l<=MaxLevel;l++){
            if (HasOverrideFlow(FlowName, l)){
                return true;
            }
        }
        return false;
    }
    
    public static boolean HasOverrideFlow(String FlowName, int Level){
        String tOverride = GetOverrideFlow(FlowName, Level);
        if (tOverride.equals(FlowName) || tOverride.equals("0")){
            return false;
        }else{
            return true;
        }
    }
    
    public static String GetOverrideFlow(String FlowName, int Level){
        if (FlowName==null){
            LOG.debug("GetOverrideFlow: request for null name returned 0");
            return "0";
        }
        if (Level<2){
            //can only override level 2 and higher
            //LOG.debug("GetOverrideFlow: override for invalid level '" + Level + "' returning original FlowName '" + FlowName + "'");
            return FlowName;
        }
        String tOverride = getOverrideFlow(FlowName, Level);
        if (tOverride.equals(util.OptionNotFound)){
            //check lower levels for previous overrides
            for (int l=Level-1;l>1;l--){
                tOverride = getOverrideFlow(FlowName, l);
                if (!tOverride.equals(util.OptionNotFound)){
                    //LOG.debug("GetOverrideFlow: override found for previous level '" + l + "' for Level '" + Level + "' override = '" + tOverride + "'");
                    return tOverride;
                }
            }
        }else{
            //LOG.debug("GetOverrideFlow: override found for level '" + Level + "' override = '" + tOverride + "'");
            return tOverride;
        }
        //LOG.debug("GetOverrideFlow: no override found for level '" + Level + "' returning original FlowName '" + FlowName + "'");
        return FlowName;
    }

    private static String getOverrideFlow(String FlowName, int Level){
        String Prop = GetFlowBaseProp(FlowName) + Const.PropDivider + Const.FlowOverride + Const.PropDivider + Level;
        return util.GetProperty(Prop, util.OptionNotFound);
    }

    public static void SetOverrideFlow(String FlowName, int Level, String Override){
        String Prop = GetFlowBaseProp(FlowName) + Const.PropDivider + Const.FlowOverride + Const.PropDivider + Level;
        if (Override.equals(FlowName)){
            //clear the property as the Override is the same as the base flow
            util.RemoveProperty(Prop);
        }else{
            util.SetProperty(Prop, Override);
        }
    }
    
}
