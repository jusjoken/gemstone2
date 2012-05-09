/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */
public class Widget {
    static private final Logger LOG = Logger.getLogger(Widget.class);
    public static final String WidgetProps = Const.BaseProp + Const.PropDivider + Const.WidgetProp + Const.PropDivider;
    public static enum WidgetSize{XL,L,M,S};
    public static List<String> InternalWidgetList = new ArrayList<String>();
    public static Map<String,Integer> InternalWidgetListSections = new HashMap<String,Integer>();
    public static Map<String,Integer> InternalWidgetListDefaultListSize = new HashMap<String,Integer>();
    public static Map<String,ArrayList> InternalWidgetLists = new HashMap<String,ArrayList>();
    
    public static void SetWidgetList(String WidgetType, ArrayList List){
        InternalWidgetLists.put(WidgetType, List);
    }
    public static ArrayList GetWidgetList(String WidgetType){
        if (InternalWidgetLists.containsKey(WidgetType)){
            return InternalWidgetLists.get(WidgetType);
        }
        return new ArrayList();
    }
    public static Boolean WidgetListIsValid(String WidgetType){
        if (InternalWidgetLists.containsKey(WidgetType)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    public static Boolean WidgetListIsEmpty(String WidgetType){
        if (InternalWidgetLists.containsKey(WidgetType)){
            return InternalWidgetLists.get(WidgetType).isEmpty();
        }
        return Boolean.TRUE;
    }
    
    public static void AddWidgetType(String WidgetType, Integer Sections){
        AddWidgetType(WidgetType, Sections, 1);
    }
    public static void AddWidgetType(String WidgetType, Integer Sections, Integer DefaultListSize){
        if (!InternalWidgetList.contains(WidgetType)){
            InternalWidgetList.add(WidgetType);
            InternalWidgetListSections.put(WidgetType, Sections);
            InternalWidgetListDefaultListSize.put(WidgetType, DefaultListSize);
        }
    }

    public static ArrayList<String> GetWidgetListBase(){
        return new ArrayList<String>(InternalWidgetList);
    }

    public static ArrayList<String> GetWidgetList(){
        return GetWidgetList(Boolean.FALSE);
    }
    public static ArrayList<String> GetWidgetList(Boolean IncludeOff){
        
        if (InternalWidgetList.size()>0){
            //Add the widgets in Sort Order
            TreeMap<Integer,String> tSortedList = new TreeMap<Integer,String>();
            Boolean SortNeedsSaving = Boolean.FALSE;
            Integer counter = 0;
            for (String tWidget:InternalWidgetList){
                //make sure this is a real Flow entry with a name property
                if ((IncludeOff) || IncludeOff==Boolean.FALSE && ShowWidget(tWidget)){
                    counter++;
                    Integer thisSort = GetWidgetSort(tWidget);
                    if (thisSort==0){
                        thisSort = counter;
                        SortNeedsSaving = Boolean.TRUE;
                    }
                    while(tSortedList.containsKey(thisSort)){
                        counter++;
                        thisSort = counter;
                        SortNeedsSaving = Boolean.TRUE;
                    }
                    tSortedList.put(thisSort, tWidget);
                    //LOG.debug("GetWidgetList: '" + tWidget + "' added at '" + thisSort + "'");
                }
            }
            if (SortNeedsSaving){
                for (Integer tWidgetIndex:tSortedList.keySet()){
                    SetWidgetSort(tSortedList.get(tWidgetIndex),tWidgetIndex);
                    //LOG.debug("GetWidgetList: Saving sort for '" + tSortedList.get(tWidgetIndex) + "' at '" + tWidgetIndex + "'");
                }
            }
            return new ArrayList<String>(tSortedList.values()); 
        }else{
            return new ArrayList<String>();
        }
    }
    
//    public static String GetUseWidgetsName(){
//        return util.GetTrueFalseOptionNameBase(Boolean.FALSE, WidgetProps + "WidgetsUse","", "On", "Off", Boolean.FALSE);
//    }
    public static Boolean GetUseWidgets(){
        if (util.GetProperty(WidgetProps + "WidgetsUse", "Off").equals("Off")){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    public static Boolean AllowWidgetToggle(){
        if (util.GetProperty(WidgetProps + "WidgetsUse", "Off").equals("Off")){
            return Boolean.FALSE;
        }else if (util.GetProperty(WidgetProps + "WidgetsUse", "Off").equals("Shown")){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
//    public static void SetUseWidgets(Boolean Value){
//        util.SetProperty(WidgetProps + "WidgetsUse", Value.toString());
//    }

//    public static String GetLiveWidgetsName(){
//        return util.GetTrueFalseOptionNameBase(Boolean.FALSE, WidgetProps + "WidgetsLive","", "On", "Off", Boolean.FALSE);
//    }
    public static Boolean GetLiveWidgets(){
        if (util.GetPropertyAsBoolean(WidgetProps + "WidgetsLive", Boolean.FALSE)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
//    public static void SetLiveWidgets(Boolean Value){
//        util.SetProperty(WidgetProps + "WidgetsLive", Value.toString());
//    }

    public static Boolean ShowWidgets(){
        //see if at least one of the 4 Widget Panels is not Off
        if (GetWidgetList().size()>0){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static Boolean UseTabStyle(){
        if (util.GetPropertyAsBoolean(WidgetProps + "UseTabStyle", Boolean.FALSE)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

    public static Integer GetWidgetsWidth(){
        return util.GetPropertyAsInteger(WidgetProps + "WidgetWidth", 7);
    }
    public static Integer GetWidgetSort(String WidgetType){
        return util.GetPropertyAsInteger(WidgetProps + WidgetType + Const.PropDivider + "Sort", 0);
    }
    public static void SetWidgetSort(String WidgetType, Integer iSort){
        util.SetProperty(WidgetProps + WidgetType + Const.PropDivider + "Sort", iSort.toString());
    }
    
    public static void MoveWidget(String WidgetType, Integer Delta){
        ArrayList<String> Widgets = GetWidgetList();
        Integer currentPos = Widgets.indexOf(WidgetType);
        Integer newPos = currentPos + Delta;
        //LOG.debug("MoveWidget: WidgetType '" + WidgetType + "' newPos '" + newPos + " currentPos '" + currentPos + " Size = '" + Widgets.size() + " Delta '" + Delta + "'");
        if (newPos>Widgets.size() || newPos<0){
            //do not move the widget out of bounds
        }else{
            String newWidget = Widgets.get(newPos);
            //now resave all the sorts with the change in place
            for (Integer i=0;i<Widgets.size();i++){
                if (i==currentPos){
                    SetWidgetSort(WidgetType, newPos+1);
                    //LOG.debug("MoveWidget: WidgetType original '" + WidgetType + "' to: '" + (newPos+1) + " Delta '" + Delta + "'");
                }else if (i==newPos){
                    SetWidgetSort(newWidget, currentPos+1);
                    //LOG.debug("MoveWidget: WidgetType replaced '" + newWidget + "' to: '" + (currentPos+1) + " Delta '" + Delta + "'");
                }else{
                    SetWidgetSort(Widgets.get(i), i+1);
                    //LOG.debug("MoveWidget: WidgetType saving   '" + Widgets.get(i) + "' to: '" + (i+1) + " Delta '" + Delta + "'");
                }
            }
        }
    }
    
    public static Boolean ShowWidget(String WidgetType){
        return util.GetPropertyAsBoolean(WidgetProps + WidgetType + Const.PropDivider + "Show",false);
    }
    public static String GetSize(String WidgetType){
        return util.GetProperty(WidgetProps + WidgetType + Const.PropDivider + "Size","Off");
    }
    public static void SetSize(String WidgetType, String tSize){
        util.SetProperty(WidgetProps + WidgetType + Const.PropDivider + "Size",tSize);
    }
    
    public static Double GetForecastHeight(String WidgetType,Integer ForecastDay){
        String tSize = GetSize(WidgetType);
        if (tSize.equals(WidgetSize.XL.toString())){
            return 1.0/4;
        }else if (tSize.equals(WidgetSize.L.toString())){
            if (ForecastDay>3){
                return 0.0;
            }else{
                return 1.0/3;
            }
        }else if (tSize.equals(WidgetSize.M.toString())){
            if (ForecastDay>2){
                return 0.0;
            }else{
                return 1.0/2;
            }
        }else{
            if (ForecastDay>1){
                return 0.0;
            }else{
                return 1.0;
            }
        }
    }
    
//    public static String GetType(Integer WidgetNumber){
//        return util.GetProperty(WidgetProps + "WidgetPanel" + WidgetNumber,"Off");
//    }
//   
    public static Double GetHeightbyType(String WidgetType){
        String tWidgetSize = GetSize(WidgetType);
        Double tRetVal = GetHeight(tWidgetSize);
        //LOG.debug("GetHeightbyType: for Widget '" + WidgetType + "' height = '" + tRetVal + "'");
        return tRetVal;
    }
    public static Double GetHeight(String tWidgetSize){
        Double tRetVal = StringtoDouble(util.GetProperty(WidgetProps + "WidgetHeight" + tWidgetSize,"0.00"));
        //LOG.debug("GetHeight: for WidgetSize '" + tWidgetSize + "' height = '" + tRetVal + "'");
        return tRetVal;
    }
    
    public static void SetHeight(String tWidgetSize, Double Height){
        //LOG.debug("SetHeight: for Widget Size = '" + tWidgetSize + "' height = '" + Height + "'");
        util.SetProperty(WidgetProps + "WidgetHeight" + tWidgetSize, Height.toString());
    }
    
    public static Double GetTitleHeightbyType(String WidgetType){
        String tWidgetSize = GetSize(WidgetType);
        return GetTitleHeight(tWidgetSize);
    }
    public static Double GetTitleHeight(String tWidgetSize){
        Double tRetVal = StringtoDouble(util.GetProperty(WidgetProps + "WidgetTitleHeight" + tWidgetSize,"0.00"));
        //LOG.debug("GetTitleHeight: for Widget Size = '" + tWidgetSize + "' height = '" + tRetVal + "'");
        return tRetVal;
    }
    
    public static void SetTitleHeight(String tWidgetSize, Double Height){
        util.SetProperty(WidgetProps + "WidgetTitleHeight" + tWidgetSize, Height.toString());
    }
    
    private static Double StringtoDouble(String s){
        try
        {
          Double d = Double.valueOf(s.trim()).doubleValue();
          return d;
        }
        catch (NumberFormatException nfe)
        {
            LOG.debug("StringtoDouble: Error converting '" + s + "' ERROR: '" + nfe + "'");
            return 0.00;
        }
    }
    
    public static Integer GetMaxListItems(String tWidgetSize){
        return util.GetPropertyAsInteger(WidgetProps + "WidgetMaxListItems" + tWidgetSize, 0);
    }
    public static void SetMaxListItems(String tWidgetSize, Integer MaxItems){
        util.SetProperty(WidgetProps + "WidgetMaxListItems" + tWidgetSize, MaxItems.toString());
    }
    
    
    
    
    public static String GetSectionEnabledName(String WidgetType, Integer Section, String TrueValue, String FalseValue){
        if (GetSectionEnabled(WidgetType, Section)){
            return TrueValue;
        }else{
            return FalseValue;
        }
    }
    public static Boolean GetSectionEnabled(String WidgetType, Integer Section){
        String tProp = WidgetProps + WidgetType + Const.PropDivider + Section.toString() + Const.PropDivider + "Enabled";
        return util.GetPropertyAsBoolean(tProp,Boolean.TRUE);
    }
    public static void SetSectionEnabledNext(String WidgetType, Integer Section){
        String tProp = WidgetProps + WidgetType + Const.PropDivider + Section.toString() + Const.PropDivider + "Enabled";
        Boolean NewValue = !util.GetPropertyAsBoolean(tProp, Boolean.TRUE);
        util.SetProperty(tProp, NewValue.toString());
    }

    public static Integer GetDefaultListSize(String WidgetType){
        if (InternalWidgetListDefaultListSize.containsKey(WidgetType)){
            return InternalWidgetListDefaultListSize.get(WidgetType);
        }
        return 1;
    }
    public static Integer GetListSize(String WidgetType){
        String tProp = WidgetProps + WidgetType + Const.PropDivider + "ListSize";
        Integer tRetVal = util.GetPropertyAsInteger(tProp,InternalWidgetListDefaultListSize.get(WidgetType));
        return tRetVal;
    }
    public static void SetListSize(String WidgetType, Integer tSize){
        String tProp = WidgetProps + WidgetType + Const.PropDivider + "ListSize";
        util.SetProperty(tProp,tSize.toString());
    }

    //Section 1 is always the Title
    //Section size is how many "spaces" this Widget section will consume
    public static Double GetSectionSize(String WidgetType, Integer Section){
        Double tRetVal = StringtoDouble(util.GetProperty(WidgetProps + WidgetType + Const.PropDivider + Section.toString() + Const.PropDivider + "Size","0.00"));
        return tRetVal;
    }
    public static void SetSectionSize(String WidgetType, Integer Section, Double tSize){
        util.SetProperty(WidgetProps + WidgetType + Const.PropDivider + Section.toString() + Const.PropDivider + "Size",tSize.toString());
    }

    //Space size to use as a multiplier with Section size to determine the Height for the Section
    public static Double GetSpaceSize(){
        Double tRetVal = StringtoDouble(util.GetProperty(WidgetProps + "SpaceSize","0.00"));
        return tRetVal;
    }
    public static void SetSpaceSize(Double tSize){
        util.SetProperty(WidgetProps + "SpaceSize",tSize.toString());
    }

    public static Integer GetMaxSections(String WidgetType){
        return InternalWidgetListSections.get(WidgetType);
    }
    
    public static Double GetAllHeights(){
        Double tHeight = 0.00;
        for (String tWidget:GetWidgetList()){
            tHeight = tHeight + GetWidgetHeight(tWidget);
        }
        //LOG.debug("GetAllHeights: returning ='" + tHeight + "'");
        return tHeight;
    }

    public static Double GetWidgetHeightP(String WidgetType){
        return GetWidgetHeight(WidgetType)/GetAllHeights();
    }
    public static Double GetWidgetHeight(String WidgetType){
        Double tHeight = 0.00;
        for (Integer i=1;i<InternalWidgetListSections.get(WidgetType)+1;i++){
            tHeight = tHeight + GetWidgetSectionHeight(WidgetType, i);
        }
        //LOG.debug("GetWidgetHeight: returning ='" + tHeight + "'");
        return tHeight;
    }
    public static Double GetWidgetSectionHeightP(String WidgetType, Integer Section){
        return GetWidgetSectionHeight(WidgetType, Section)/GetWidgetHeight(WidgetType);
    }
    public static Double GetWidgetSectionHeight(String WidgetType, Integer Section){
        Double tHeight = 0.00;
        if (GetSectionEnabled(WidgetType, Section)){
            Integer ListItems = 1;
            Integer ListExtraInfo = 1;
            if (Section>1){
                ListItems = GetListSize(WidgetType);
                String tProp = WidgetProps + WidgetType + Const.PropDivider + "ExtraInfoinList";
                if (util.GetPropertyAsBoolean(tProp, Boolean.FALSE)){
                   ListExtraInfo = 2;
                }
            }
            Double thisHeight = (GetSpaceSize()*GetSectionSize(WidgetType, Section)*ListItems*ListExtraInfo);
            tHeight = tHeight + thisHeight;
            //LOG.debug("GetWidgetHeight: for Widget ='" + WidgetType + "' Section = '" + Section + "' Height = '" + thisHeight + "' totalHeight = '" + tHeight + "' ListItems = '" + ListItems + "'");
        }
        return tHeight;
    }
    
    public static void Export(String FileName){
        util.Export(FileName, Const.BaseProp + Const.PropDivider + Const.WidgetProp, util.ExportType.WIDGETS);
    }
}
