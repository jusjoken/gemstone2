/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TreeSet;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import sagex.UIContext;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.views.ViewFolder;

/**
 *
 * @author SBANTA
 * @author JUSJOKEN
 * - 10/09/2011 - added LOG4J setup and Main method for testing, and StringNumberFormat
 * - 10/10/2011 - added some generic functions
 * - 04/04/2012 - updated for Gemstone
 */
public class util {

    static private final Logger LOG = Logger.getLogger(util.class);
    public static final char[] symbols = new char[36];
    private static final Random random = new Random();
    public static final String OptionNotFound = "Option not Found";
    public static enum TriState{YES,NO,OTHER};
    public static enum ExportType{ALL,WIDGETS,FLOWS,FLOW,GENERIC};
    public static final String ListToken = ":&&:";
    
    public static void main(String[] args){

        //String test = StringNumberFormat("27.96903", 0, 2);
        //String test = StringNumberFormat("27.1", 0, 2);
        //LOG.debug(test);
        api.InitLogger();
        
    }
    
    private static void print(Map map) {
        LOG.debug("One=" + map.get("One"));
        LOG.debug("Two=" + map.get("Two"));
        LOG.debug("Three=" + map.get("Three"));
        LOG.debug("Four=" + map.get("Four"));
        LOG.debug("Five=" + map.get("Five"));
    }
    private static void print2(Map map, Integer items) {
        for(int i=1; i<=items; i++){
            LOG.debug("TEST " + map.getClass() + " ITEMS '" + items + "' - " + TempName(i) + "=" + map.get(TempName(i)) + " Mem = '" + FreeMem() + "'");
        }
    }
    private static void testMap(Map map, Integer Multiplier) {
        LOG.debug("Testing " + map.getClass());
        for(int i=1; i<=Multiplier; i++){
            map.put(TempName(i), new byte[10*1024*1024]);
            print2(map,i);
        }
//        LOG.debug("Adding 10MB * " + Multiplier);
////        for(int i=1; i<=Multiplier; i++){
//        byte[] block = new byte[10*1024*1024*Multiplier]; // 10 MB
////        }
//        print(map);
    }
    public static String FreeMem() {
        Long total = Runtime.getRuntime().totalMemory();
        Long free = Runtime.getRuntime().freeMemory();
        String InfoText = Math.round((total-free)/1000000.0) + "MB/";
        return InfoText;
    }
    public static void LogFreeMem(String Message) {
        Long total = Runtime.getRuntime().totalMemory();
        Long free = Runtime.getRuntime().freeMemory();
        String InfoText = Math.round((total-free)/1000000.0) + "MB";
        LOG.debug(Message + " FreeMem '" + InfoText + "'");
    }
    private static String TempName(Integer i) {
        return "Item_" + i;
    }
    
//    private static void testMap(Map map, Integer Multiplier) {
//        LOG.debug("Testing " + map.getClass());
//        map.put("One", new Integer(1));
//        map.put("Two", new Integer(2));
//        map.put("Three", new Integer(3));
//        map.put("Four", new Integer(4));
//        map.put("Five", new Integer(5));
//        print(map);
//        LOG.debug("Adding 10MB * " + Multiplier);
////        for(int i=1; i<=Multiplier; i++){
//        byte[] block = new byte[10*1024*1024*Multiplier]; // 10 MB
////        }
//        print(map);
//    }
    public static void test1(Integer Min, Integer Multiplier) {
        testMap(new SoftHashMap(Min), Multiplier);
        //testMap(new HashMap(), Multiplier);
    }    

    //pass in a String that contains a number and this will format it to a specific number of decimal places
    public static String StringNumberFormat(String Input, Integer DecimalPlaces){
        return StringNumberFormat(Input, DecimalPlaces, DecimalPlaces);
    }
    public static String StringNumberFormat(String Input, Integer MinDecimalPlaces, Integer MaxDecimalPlaces){
        float a = 0;
        try {
            a = Float.parseFloat(Input);
        } catch (NumberFormatException nfe) {
            LOG.error("StringNumberFormat - NumberFormatException for '" + Input + "'");
            return Input;
        }
        NumberFormat df = DecimalFormat.getInstance();
        df.setMinimumFractionDigits(MinDecimalPlaces);
        df.setMaximumFractionDigits(MaxDecimalPlaces);
        df.setRoundingMode(RoundingMode.DOWN);   
        String retString = df.format(a);
        return retString;
    }
    

    public static Object CheckSeasonSize(Map<String, Object> Files, int sizeneeded) {
        LinkedHashMap<String, Object> WithBlanks = new LinkedHashMap<String, Object>();
        for(int index=0;index<sizeneeded;index++){
        WithBlanks.put("blankelement1"+index, null);}
        
        WithBlanks.putAll(Files);
        for(int index=sizeneeded;index<sizeneeded+sizeneeded;index++){
        WithBlanks.put("blankelement"+index, null);}

        return WithBlanks;

    }

    public static Object CheckCategorySize(Map<String, Object> Files) {
        LinkedHashMap<String, Object> WithBlanks = new LinkedHashMap<String, Object>();
        WithBlanks.put("blankelement1", null);
        WithBlanks.putAll(Files);
        WithBlanks.put("blankelement4", null);


        return WithBlanks;

    }

    public static Object CheckSimpleSize(Object[] Files,int sizeneeded) {
        if (!Files.toString().contains("blankelement")) {

            List<Object> WithBlanks = new ArrayList<Object>();
            for(int index=0;index<sizeneeded;index++){
            WithBlanks.add("blankelement"+index);}


            for (Object curr : Files) {
                WithBlanks.add(curr);
            }
            for(int index=sizeneeded;index<sizeneeded+sizeneeded;index++){
            WithBlanks.add("blankelement"+index);}
            
            return WithBlanks;
        }
        return Files;


    }

    public static Object CheckFileSize(List<Object> files, String diamondprop) {
        String viewtype = Flow.GetFlowType(diamondprop);
        ArrayList<Object> NewList = new ArrayList<Object>();
        if (viewtype == "Wall Flow" && files.size() < 5) {
        } else if (viewtype == "Cover Flow" && files.size() < 7) {
            NewList.add(null);
            NewList.add(null);
            NewList.add(null);
            NewList.addAll(files);
            NewList.add(null);
            NewList.add(null);
            return NewList;
        }


        return files;
    }

    public static LinkedHashMap<String, Integer> GetLetterLocations(Object[] MediaFiles) {
        String CurrentLocation = "845948921";
        Boolean ScrapTitle = Boolean.parseBoolean(sagex.api.Configuration.GetProperty("ui/ignore_the_when_sorting", "false"));
        LinkedHashMap<String, Integer> Values = new LinkedHashMap<String, Integer>();
        String Title = "";
        int i = 0;

        for (Object curr : MediaFiles) {

            if (ScrapTitle) {
                Title = MetadataCalls.GetSortTitle(curr);
            } else {
                Title = MetadataCalls.GetMediaTitle(curr);
            }
            if (!Title.startsWith(CurrentLocation)) {

                CurrentLocation = Title.substring(0, 1);
                Values.put(CurrentLocation.toLowerCase(), i);
            }
            i++;


        }
        return Values;
    }

//    public static HashMap<Object,Object> GetHeaders(Object MediaFiles,String Method){
//    Object[] Files = FanartCaching.toArray(MediaFiles);
//    HashMap<Object,Object> Headers = new HashMap<Object,Object>();
//    Object CurrGroup=null;
//    if(Method.contains("AirDate")){
//    for(Object curr:Files){
//    Object thisvalue=GetTimeAdded(curr);
//    if(thisvalue!=CurrGroup){
//    Headers.put(curr,thisvalue);
//    CurrGroup=thisvalue;}}
//
//    }
//    else if (Method.contains("Title")){
//    for(Object curr:Files){
//    String thisvalues=ClassFromString.GetSortDividerClass(Method,curr).toString();
//    Object thisvalue=thisvalues.substring(0,1);
//    if(!thisvalue.equals(CurrGroup)){
//    Headers.put(curr,thisvalue);
//    CurrGroup=thisvalue;}
//    }
//
//    }
//    return Headers;}
    public static boolean IsHeader(Map headers, Object Key) {
        return headers.containsKey(Key);
    }

    public static String GenerateRandomName(){
        char[] buf = new char[10];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return "sd" + new String(buf);
    }

    public static Boolean HasProperty(String Property){
        String tValue = sagex.api.Configuration.GetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    
    public static String GetProperty(String Property, String DefaultValue){
        String tValue = sagex.api.Configuration.GetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return DefaultValue;
        }else{
            return tValue;
        }
    }
    
    public static Boolean GetPropertyAsBoolean(String Property, Boolean DefaultValue){
        String tValue = sagex.api.Configuration.GetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return DefaultValue;
        }else{
            return Boolean.parseBoolean(tValue);
        }
    }
    
    //Evaluates the property and returns it's value - must be true or false - returns true otherwise
    public static Boolean GetPropertyEvalAsBoolean(String Property, Boolean DefaultValue){
        String tValue = sagex.api.Configuration.GetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return DefaultValue;
        }else{
            return Boolean.parseBoolean(EvaluateAttribute(tValue));
        }
    }
    
    public static TriState GetPropertyAsTriState(String Property, TriState DefaultValue){
        String tValue = sagex.api.Configuration.GetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return DefaultValue;
        }else if(tValue.equals("YES")){
            return TriState.YES;
        }else if(tValue.equals("NO")){
            return TriState.NO;
        }else if(tValue.equals("OTHER")){
            return TriState.OTHER;
        }else if(Boolean.parseBoolean(tValue)){
            return TriState.YES;
        }else if(!Boolean.parseBoolean(tValue)){
            return TriState.NO;
        }else{
            return TriState.YES;
        }
    }
    
    public static List<String> GetPropertyAsList(String Property){
        String tValue = sagex.api.Configuration.GetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return new LinkedList<String>();
        }else{
            return ConvertStringtoList(tValue);
        }
    }
    
    public static Integer GetPropertyAsInteger(String Property, Integer DefaultValue){
        //read in the Sage Property and force convert it to an Integer
        Integer tInteger = DefaultValue;
        String tValue = sagex.api.Configuration.GetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return DefaultValue;
        }
        try {
            tInteger = Integer.valueOf(tValue);
        } catch (NumberFormatException ex) {
            //use DefaultValue
            return DefaultValue;
        }
        return tInteger;
    }
    
    public static Double GetPropertyAsDouble(String Property, Double DefaultValue){
        //read in the Sage Property and force convert it to an Integer
        Double tDouble = DefaultValue;
        String tValue = sagex.api.Configuration.GetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return DefaultValue;
        }
        try {
            tDouble = Double.valueOf(tValue);
        } catch (NumberFormatException ex) {
            //use DefaultValue
            return DefaultValue;
        }
        return tDouble;
    }
    

    public static String GetServerProperty(String Property, String DefaultValue){
        String tValue = sagex.api.Configuration.GetServerProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return DefaultValue;
        }else{
            return tValue;
        }
    }

    public static void SetProperty(String Property, String Value){
        sagex.api.Configuration.SetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, Value);
    }

    public static void SetPropertyAsTriState(String Property, TriState Value){
        sagex.api.Configuration.SetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, Value.toString());
    }

    public static void SetPropertyAsList(String Property, List<String> ListValue){
        String Value = ConvertListtoString(ListValue);
        if (ListValue.size()>0){
            sagex.api.Configuration.SetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, Value);
        }else{
            RemoveProperty(Property);
        }
    }

    public static void SetServerProperty(String Property, String Value){
        sagex.api.Configuration.SetServerProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, Value);
    }

    public static void RemoveServerProperty(String Property){
        sagex.api.Configuration.RemoveServerProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property);
    }

    public static void RemovePropertyAndChildren(String Property){
        sagex.api.Configuration.RemovePropertyAndChildren(new UIContext(sagex.api.Global.GetUIContextName()),Property);
    }

    public static void RemoveProperty(String Property){
        sagex.api.Configuration.RemoveProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property);
    }

    public static void RemoveServerPropertyAndChildren(String Property){
        sagex.api.Configuration.RemoveServerPropertyAndChildren(new UIContext(sagex.api.Global.GetUIContextName()),Property);
    }

    public static String ConvertListtoString(List<String> ListValue){
        return ConvertListtoString(ListValue, ListToken);
    }
    public static String ConvertListtoString(List<String> ListValue, String Separator){
        String Value = "";
        if (ListValue.size()>0){
            Boolean tFirstItem = Boolean.TRUE;
            for (String ListItem : ListValue){
                if (tFirstItem){
                    Value = ListItem;
                    tFirstItem = Boolean.FALSE;
                }else{
                    Value = Value + Separator + ListItem;
                }
            }
        }
        return Value;
    }

    public static List<String> ConvertStringtoList(String tValue){
        if (tValue.equals(OptionNotFound) || tValue.equals("") || tValue==null){
            return new LinkedList<String>();
        }else{
            return Arrays.asList(tValue.split(ListToken));
        }
    }
    
    public static String EvaluateAttribute(String Attribute){
        //LOG.debug("EvaluateAttribute: Attribute = '" + Attribute + "'");
        Object[] passvalue = new Object[1];
        passvalue[0] = sagex.api.WidgetAPI.EvaluateExpression(new UIContext(sagex.api.Global.GetUIContextName()), Attribute);
        if (passvalue[0]==null){
            LOG.debug("EvaluateAttribute for Attribute = '" + Attribute + "' not evaluated.");
            return OptionNotFound;
        }else{
            //LOG.debug("EvaluateAttribute for Attribute = '" + Attribute + "' = '" + passvalue[0].toString() + "'");
            return passvalue[0].toString();
        }
        
    }

    public static void OptionsClear(){
        //clear all the Options settings used only while the Options menu is open
        String tProp = Const.BaseProp + Const.PropDivider + Const.OptionsFocused;
        RemovePropertyAndChildren(tProp);
        tProp = Const.BaseProp + Const.PropDivider + Const.OptionsType;
        RemovePropertyAndChildren(tProp);
        tProp = Const.BaseProp + Const.PropDivider + Const.OptionsTitle;
        RemovePropertyAndChildren(tProp);
    }
    
    public static void OptionsLastFocusedSet(Integer CurrentLevel, String FocusedItem){
        String tProp = Const.BaseProp + Const.PropDivider + Const.OptionsFocused + Const.PropDivider + CurrentLevel.toString() + Const.PropDivider + Const.OptionsFocusedItem;
        //LOG.debug("OptionsLastFocusedSet: CurrentLevel = '" + CurrentLevel + "' FocusedItem = '" + FocusedItem + "'");
        SetProperty(tProp, FocusedItem);
    }

    public static String OptionsLastFocusedGet(Integer CurrentLevel){
        String tProp = Const.BaseProp + Const.PropDivider + Const.OptionsFocused + Const.PropDivider + CurrentLevel.toString() + Const.PropDivider + Const.OptionsFocusedItem;
        String FocusedItem = GetProperty(tProp, OptionNotFound);
        //LOG.debug("OptionsLastFocusedGet: CurrentLevel = '" + CurrentLevel + "' FocusedItem = '" + FocusedItem + "'");
        return FocusedItem;
    }

    public static String OptionsTypeGet(Integer CurrentLevel){
        String tProp = Const.BaseProp + Const.PropDivider + Const.OptionsType + Const.PropDivider + CurrentLevel.toString() + Const.PropDivider + Const.OptionsTypeName;
        String OptionsType = GetProperty(tProp, OptionNotFound);
        //LOG.debug("OptionsTypeGet: CurrentLevel = '" + CurrentLevel + "' OptionsType = '" + OptionsType + "'");
        return OptionsType;
    }

    public static Integer OptionsTypeSet(Integer CurrentLevel, String OptionsType){
        String tProp = Const.BaseProp + Const.PropDivider + Const.OptionsType + Const.PropDivider + CurrentLevel.toString() + Const.PropDivider + Const.OptionsTypeName;
        //LOG.debug("OptionsTypeSet: CurrentLevel = '" + CurrentLevel + "' OptionsType = '" + OptionsType + "'");
        SetProperty(tProp, OptionsType);
        return CurrentLevel;
    }

    public static void OptionsTitleSet(Integer CurrentLevel, String Title){
        String tProp = Const.BaseProp + Const.PropDivider + Const.OptionsTitle + Const.PropDivider + CurrentLevel.toString() + Const.PropDivider + Const.OptionsTitleName;
        //LOG.debug("OptionsTitleSet: CurrentLevel = '" + CurrentLevel + "' Title = '" + Title + "'");
        SetProperty(tProp, Title);
    }

    public static String OptionsTitleGet(Integer CurrentLevel){
        String tProp = Const.BaseProp + Const.PropDivider + Const.OptionsTitle + Const.PropDivider + CurrentLevel.toString() + Const.PropDivider + Const.OptionsTitleName;
        String Title = GetProperty(tProp, OptionNotFound);
        //LOG.debug("OptionsTitleGet: CurrentLevel = '" + CurrentLevel + "' Title = '" + Title + "'");
        return Title;
    }

    //Set of functions for Get/Set of generic True/False values with passed in test names to display
    public static Boolean GetTrueFalseOption(String PropSection, String PropName, Boolean DefaultValue){
        return GetTrueFalseOptionBase(Boolean.FALSE, PropSection, PropName, DefaultValue);
    }
    public static Boolean GetTrueFalseOptionBase(Boolean bFlow, String PropSection, String PropName, Boolean DefaultValue){
        String tProp = "";
        if (PropName.equals("")){  //expect the full property string in the PropSection
            tProp = PropSection;
        }else{
            if (bFlow){
                tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
            }else{
                tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
            }
        }
        return util.GetPropertyAsBoolean(tProp, DefaultValue);
    }
    public static String GetTrueFalseOptionName(String PropSection, String TrueValue, String FalseValue, Boolean DefaultValue){
        return GetTrueFalseOptionNameBase(Boolean.FALSE, PropSection, "", TrueValue, FalseValue, DefaultValue);
    }
    public static String GetTrueFalseOptionName(String PropSection, String TrueValue, String FalseValue){
        return GetTrueFalseOptionNameBase(Boolean.FALSE, PropSection, "", TrueValue, FalseValue, Boolean.FALSE);
    }
    public static String GetTrueFalseOptionName(String PropSection, String PropName, String TrueValue, String FalseValue){
        return GetTrueFalseOptionNameBase(Boolean.FALSE, PropSection, PropName, TrueValue, FalseValue, Boolean.FALSE);
    }
    public static String GetTrueFalseOptionName(String PropSection, String PropName, String TrueValue, String FalseValue, Boolean DefaultValue){
        return GetTrueFalseOptionNameBase(Boolean.FALSE, PropSection, PropName, TrueValue, FalseValue, DefaultValue);
    }
    public static String GetTrueFalseOptionNameBase(Boolean bFlow, String PropSection, String PropName, String TrueValue, String FalseValue, Boolean DefaultValue){
        String tProp = "";
        if (PropName.equals("")){  //expect the full property string in the PropSection
            tProp = PropSection;
        }else{
            if (bFlow){
                tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
            }else{
                tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
            }
        }
        Boolean CurrentValue = util.GetPropertyAsBoolean(tProp, DefaultValue);
        if (CurrentValue){
            return TrueValue;
        }else{
            return FalseValue;
        }
    }

    //option for full Property string passed in
    public static void SetTrueFalseOptionNext(String PropSection, Boolean DefaultValue){
        SetTrueFalseOptionNextBase(Boolean.FALSE, PropSection, "", DefaultValue);
    }
    //option for assuming a FALSE default value and full Property string passed in
    public static void SetTrueFalseOptionNext(String PropSection){
        SetTrueFalseOptionNextBase(Boolean.FALSE, PropSection, "", Boolean.FALSE);
    }
    //option for assuming a FALSE default value
    public static void SetTrueFalseOptionNext(String PropSection, String PropName){
        SetTrueFalseOptionNextBase(Boolean.FALSE, PropSection, PropName, Boolean.FALSE);
    }
    public static void SetTrueFalseOptionNext(String PropSection, String PropName, Boolean DefaultValue){
        SetTrueFalseOptionNextBase(Boolean.FALSE, PropSection, PropName, DefaultValue);
    }
    public static void SetTrueFalseOptionNextBase(Boolean bFlow, String PropSection, String PropName, Boolean DefaultValue){
        String tProp = "";
        if (PropName.equals("")){  //expect the full property string in the PropSection
            tProp = PropSection;
        }else{
            if (bFlow){
                tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
            }else{
                tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
            }
        }
        Boolean NewValue = !util.GetPropertyAsBoolean(tProp, DefaultValue);
        util.SetProperty(tProp, NewValue.toString());
    }
    
    //option for full Property string passed in
    public static void SetTrueFalseOption(String PropSection, Boolean NewValue){
        SetTrueFalseOptionBase(Boolean.FALSE, PropSection, "", NewValue);
    }
    public static void SetTrueFalseOption(String PropSection, String PropName, Boolean NewValue){
        SetTrueFalseOptionBase(Boolean.FALSE, PropSection, PropName, NewValue);
    }
    public static void SetTrueFalseOptionBase(Boolean bFlow, String PropSection, String PropName, Boolean NewValue){
        String tProp = "";
        if (PropName.equals("")){  //expect the full property string in the PropSection
            tProp = PropSection;
        }else{
            if (bFlow){
                tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
            }else{
                tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
            }
        }
        util.SetProperty(tProp, NewValue.toString());
    }
    
    //Set of functions for Get/Set of generic passed in List
    //List items must be separated by ListToken
    public static String GetListOptionName(String PropSection, String PropName, String OptionList, String DefaultValue){
        return GetListOptionNameBase(Boolean.FALSE, PropSection, PropName, OptionList, DefaultValue);
    }
    public static String GetListOptionNameBase(Boolean bFlow, String PropSection, String PropName, String OptionList, String DefaultValue){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        String CurrentValue = util.GetProperty(tProp, DefaultValue);
        if (ConvertStringtoList(OptionList).contains(CurrentValue)){
            return CurrentValue;
        }else{
            return DefaultValue;
        }
    }
    public static void SetListOptionNext(String PropSection, String PropName, String OptionList){
        SetListOptionNextBase(Boolean.FALSE, PropSection, PropName, OptionList);
    }
    public static void SetListOptionNextBase(Boolean bFlow, String PropSection, String PropName, String OptionList){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        String CurrentValue = util.GetProperty(tProp, OptionNotFound);
        //LOG.debug("SetListOptionNextBase: currentvalue '" + CurrentValue + "' for '" + tProp + "'");
        List<String> FullList = ConvertStringtoList(OptionList);
        if (CurrentValue.equals(OptionNotFound)){
            util.SetProperty(tProp, FullList.get(1));  //default to the 2nd item
        }else{
            Integer pos = FullList.indexOf(CurrentValue);
            if (pos==-1){ //not found
                util.SetProperty(tProp, FullList.get(0));
            }else if(pos==FullList.size()-1){ //last item
                util.SetProperty(tProp, FullList.get(0));
            }else{ //get next item
                util.SetProperty(tProp, FullList.get(pos+1));
            }
        }
    }
    
    //set of functions for generic String based properties
    public static String GetOptionName(String PropSection, String PropName, String DefaultValue){
        return GetOptionNameBase(Boolean.FALSE, PropSection, PropName, DefaultValue);
    }
    public static String GetOptionNameBase(Boolean bFlow, String PropSection, String PropName, String DefaultValue){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        //LOG.debug("GetOptionNameBase: property '" + tProp + "'");
        return util.GetProperty(tProp, DefaultValue);
    }
    public static void SetOption(String PropSection, String PropName, String NewValue){
        SetOptionBase(Boolean.FALSE, PropSection, PropName, NewValue);
    }
    public static void SetOptionBase(Boolean bFlow, String PropSection, String PropName, String NewValue){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        util.SetProperty(tProp, NewValue);
    }


    public static String PropertyListasString(String PropSection, String PropName){
        return PropertyListasStringBase(Boolean.FALSE, PropSection, PropName);
    }
    public static String PropertyListasStringBase(Boolean bFlow, String PropSection, String PropName){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        return ConvertListtoString(GetPropertyAsList(tProp),",");
    }
    
    public static List<String> PropertyList(String PropSection, String PropName){
        return PropertyListBase(Boolean.FALSE, PropSection, PropName);
    }
    public static List<String> PropertyListBase(Boolean bFlow, String PropSection, String PropName){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        TreeSet<String> tList = new TreeSet<String>();
        tList.addAll(GetPropertyAsList(tProp));
        return new ArrayList<String>(tList);
    }
    
    public static void PropertyListAdd( String PropSection, String PropName, String NewValue){
        PropertyListAddBase(Boolean.FALSE, PropSection, PropName, NewValue);
    }
    public static void PropertyListAddBase(Boolean bFlow, String PropSection, String PropName, String NewValue){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        ArrayList<String> tList = new ArrayList<String>(GetPropertyAsList(tProp));
        tList.add(NewValue);
        SetPropertyAsList(tProp, tList);
    }
    
    public static void PropertyListRemove(String PropSection, String PropName, String NewValue){
        PropertyListRemoveBase(Boolean.FALSE, PropSection, PropName, NewValue);
    }
    public static void PropertyListRemoveBase(Boolean bFlow, String PropSection, String PropName, String NewValue){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        ArrayList<String> tList = new ArrayList<String>(GetPropertyAsList(tProp));
        tList.remove(NewValue);
        SetPropertyAsList(tProp, tList);
    }
    
    public static void PropertyListRemoveAll(String PropSection, String PropName){
        PropertyListRemoveAllBase(Boolean.FALSE, PropSection, PropName);
    }
    public static void PropertyListRemoveAllBase(Boolean bFlow, String PropSection, String PropName){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        RemoveProperty(tProp);
    }
    
    public static Boolean PropertyListContains(String PropSection, String PropName, String NewValue){
        return PropertyListContainsBase(Boolean.FALSE, PropSection, PropName, NewValue);
    }
    public static Boolean PropertyListContainsBase(Boolean bFlow, String PropSection, String PropName, String NewValue){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        List<String> tList = GetPropertyAsList(tProp);
        if (tList.contains(NewValue)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    public static Boolean PropertyListContains(String PropSection, String PropName, ViewFolder Folder){
        return PropertyListContainsBase(Boolean.FALSE, PropSection, PropName, Folder);
    }
    public static Boolean PropertyListContainsBase(Boolean bFlow, String PropSection, String PropName, ViewFolder Folder){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        List<String> tList = GetPropertyAsList(tProp);
        if (tList.size()>0){
            for (IMediaResource Child: Folder.getChildren()){
                String NewValue = Child.getTitle();
                if (tList.contains(NewValue)){
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }else{
            return Boolean.FALSE;
        }
    }

    public static Integer PropertyListCount(String PropSection, String PropName){
        return PropertyListCountBase(Boolean.FALSE, PropSection, PropName);
    }
    public static Integer PropertyListCountBase(Boolean bFlow, String PropSection, String PropName){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        List<String> tList = GetPropertyAsList(tProp);
        return tList.size();
    }

    public static String NotFound(){
        return OptionNotFound;
    }

    public static String UnknownName(){
        return Const.UnknownName;
    }

    public static Object PadMediaFiles(Integer PadBefore, Object MediaFiles, Integer PadAfter){
        Object tMediaFiles = MediaFiles;
        for (int index=0;index<PadBefore;index++){
            tMediaFiles = sagex.api.Database.DataUnion(sagex.api.Utility.CreateArray("BlankItem" + (index+1)), tMediaFiles);
        }
        for (int index=0;index<PadAfter;index++){
            tMediaFiles = sagex.api.Database.DataUnion(tMediaFiles, sagex.api.Utility.CreateArray("BlankItem"+(index+100)));
        }
        return tMediaFiles;
    }
    public static Object BlankArrayItems(Integer ItemCount, String UniqueID){
        Object tArray = null;
        for (int index=0;index<ItemCount;index++){
            tArray = sagex.api.Database.DataUnion(sagex.api.Utility.CreateArray("BlankItem" + UniqueID + (index)), tArray);
        }
        return tArray;
    }
    
    //VFS utility functions
    
    //Get the menu title based on the flow and the current folder
    public static String GetMenuTitle(String FlowKey, Object thisFolder){
        String tFlowName = Flow.GetFlowName(FlowKey);
        String FolderName = phoenix.media.GetTitle(thisFolder);
        String ParentName = phoenix.media.GetTitle(phoenix.umb.GetParent((sagex.phoenix.vfs.IMediaResource) thisFolder));
        if (ParentName==null){
            return tFlowName + " : " + FolderName;
        }else{
            return tFlowName + " : " + ParentName + "/" + FolderName;
        }
    }
    
    public static ArrayList<String> GetFirstListItems(ArrayList<String> InList, Integer Items){
        //filter out null values and return at most Items items
        ArrayList<String> OutList = new ArrayList<String>();
        Integer counter = 0;
        for (String Item: InList){
            if (Item==null){
                //do nothing
            }else{
                OutList.add(Item);
                counter++;
                if (counter>=Items){
                    break;
                }
            }
        }
        return OutList;
    }
    
    public static void ExportAll(String FileName){
        Export(FileName, Const.BaseProp, ExportType.ALL);
    }
    
    //Export/Import functions
//    public static void Export(String ExportFile, String PropLocation){
//        Export(ExportFile, PropLocation, ExportType.GENERIC);
//    }
    public static void Export(String ExportFile, String PropLocation, ExportType eType){
        Export(ExportFile, PropLocation, eType, "");
    }
    public static void Export(String ExportFile, String PropLocation, ExportType eType, String Name){
        //Gemstone/Flow/sd5osqk1vijs
        String ExportFilePath = UserDataLocation() + File.separator + ExportFile + ".properties";
        LOG.info("Export: Full Path = '" + ExportFilePath + "' for Properties '" + PropLocation + "'");
        
        //iterate through all the Properties and Children and save to a Property Collection
        Properties ExportProps = new Properties();
        //Add the Export Type to aid in the Import
        ExportProps.put(Const.ExportTypeKey, eType.toString());
        ExportProps.put(Const.ExportPropKey, PropLocation);
        if (!Name.equals("")){
            ExportProps.put(Const.ExportPropName, Name);
        }

        //Get all base properties first
        LoadProperties(PropLocation, ExportProps);
        
        //Now get all subproperties
        LoadSubProperties(PropLocation, ExportProps);

        //write the properties to the properties file
        try {
            FileOutputStream out = new FileOutputStream(ExportFilePath);
            try {
                ExportProps.store(out, Const.PropertyComment);
                out.close();
            } catch (IOException ex) {
                LOG.debug("Export: error exporting properties " + util.class.getName() + ex);
            }
        } catch (FileNotFoundException ex) {
            LOG.debug("Export: error exporting properties " + util.class.getName() + ex);
        }
        
    }
    
    public static void LoadProperties(String PropLocation, Properties PropContainer){
        String[] PropNames = sagex.api.Configuration.GetSubpropertiesThatAreLeaves(new UIContext(sagex.api.Global.GetUIContextName()),PropLocation);
        for (String PropItem: PropNames){
            String tProp = PropLocation + Const.PropDivider + PropItem;
            String tValue = GetProperty(tProp, OptionNotFound);
            PropContainer.put(tProp, tValue);
            //LOG.debug("LoadProperties: '" + tProp + "' = '" + tValue + "'");
        }
    }
    public static void LoadSubProperties(String PropLocation, Properties PropContainer){
        String[] PropNames = sagex.api.Configuration.GetSubpropertiesThatAreBranches(new UIContext(sagex.api.Global.GetUIContextName()),PropLocation);
        for (String PropItem: PropNames){
            String tProp = PropLocation + Const.PropDivider + PropItem;
            LoadProperties(tProp, PropContainer);
            LoadSubProperties(tProp, PropContainer);
        }
    }
    
    public static String PrintDateSortable(){  
        DateFormat df = new SimpleDateFormat("yyyyMMdd-hhmm");  
        return df.format(new Date());  
    }      

    public static String UserDataLocation(){
        return GetSageTVRootDir() + File.separator + "userdata" + File.separator + "Gemstone";
    }

    public static String DefaultsLocation(){
        return GetSageTVRootDir() + File.separator + "STVs" + File.separator + "Gemstone" + File.separator + "defaults";
    }
    
    public static String GetSageTVRootDir(){
        return sagex.phoenix.Phoenix.getInstance().getSageTVRootDir().toString();
    }

    public static Boolean IsADM(){
        String ADMPluginID = "jusjokenADM";
        String ADMWidgetSymbol = "JUSJOKEN-469564";
        // check to see if the ADM Plugin is installed
        Object[] FoundWidget = new Object[1];
        FoundWidget[0] = sagex.api.WidgetAPI.FindWidgetBySymbol(new UIContext(sagex.api.Global.GetUIContextName()), ADMWidgetSymbol);
        if (sagex.api.PluginAPI.IsPluginEnabled(sagex.api.PluginAPI.GetAvailablePluginForID(ADMPluginID)) || FoundWidget[0]!=null){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static String repeat(String str, int times){
       StringBuilder ret = new StringBuilder();
       for(int i = 0;i < times;i++) ret.append(str);
       return ret.toString();
    }

    public static String intToString(Integer num, Integer digits) {
        if (digits<0){
            return num.toString();
        }
        // create variable length array of zeros
        char[] zeros = new char[digits];
        Arrays.fill(zeros, '0');
        // format number as String
        DecimalFormat df = new DecimalFormat(String.valueOf(zeros));
        return df.format(num);
    }

    public static String MD5(String md5) {
        try {
            byte[] bytesOfMessage;  
            try {
                bytesOfMessage = md5.getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] array = md.digest(bytesOfMessage); 
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < array.length; ++i) {
                    sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
                }
                return sb.toString();

            } catch (UnsupportedEncodingException ex) {
                java.util.logging.Logger.getLogger(util.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }   
    
    public static void HandleNonCompatiblePlugins(){
        Boolean DisableForConflict = GetTrueFalseOption("Utility", "PluginConflictMode", Boolean.FALSE);
        UIContext tUI = new UIContext(sagex.api.Global.GetUIContextName());
        //check for CVF
        if (sagex.api.PluginAPI.IsPluginEnabled(tUI, "jusjokenCVF")){
            if(DisableForConflict){
                sagex.api.PluginAPI.DisablePlugin(tUI, "jusjokenCVF");
                String tMessage = "CVF Plugin is NOT compatible with Gemstone as Gemstone now contains similar functions.\n \n* CVF Plugin has been disabled.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelInfo, tMessage);
                LOG.debug("HandleNonCompatiblePlugins: CVF found and disabled");
            }else{
                String tMessage = "CVF Plugin is NOT compatible with Gemstone as Gemstone now contains similar functions.\n \n* Please disable the CVF Plugin and reload the UI.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelError, tMessage);
                LOG.debug("HandleNonCompatiblePlugins: CVF found and System Error message created");
            }
        }
        //check for ADM
        if (sagex.api.PluginAPI.IsPluginEnabled(tUI, "jusjokenADM")){
            if(DisableForConflict){
                sagex.api.PluginAPI.DisablePlugin(tUI, "jusjokenADM");
                String tMessage = "ADM Plugin is NOT compatible with Gemstone as Gemstone now contains similar functions.\n \n* ADM Plugin has been disabled.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelInfo, tMessage);
                LOG.debug("HandleNonCompatiblePlugins: ADM found and disabled");
            }else{
                String tMessage = "ADM Plugin is NOT compatible with Gemstone as Gemstone now contains similar functions.\n \n* Please disable the ADM Plugin and reload the UI.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelError, tMessage);
                LOG.debug("HandleNonCompatiblePlugins: ADM found and System Error message created");
            }
        }
    }
    
    public static void PostSytemMessage(Integer Code, String MessageType, Integer AlertLevel, String Message){
        Properties MessageProp = new Properties();
        MessageProp.setProperty("code", Code.toString());
        MessageProp.setProperty("typename", MessageType);
        sagex.api.SystemMessageAPI.PostSystemMessage(new UIContext(sagex.api.Global.GetUIContextName()), Code, AlertLevel, Message, MessageProp);
    }
    
    public static String CleanProperty(String PropLocation, String Default){
        String tValue = GetProperty(PropLocation, Default);
        //see if the value has Diamond_ or sagediamond_ in it and if so then return the default
        if (tValue.startsWith("Diamond_") || tValue.startsWith("sagediamond_")){
            //save the default so the old invalid property is overwritten
            SetProperty(PropLocation, Default);
            return Default;
        }
        return tValue;
    }
}

