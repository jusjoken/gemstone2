/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
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
import java.util.Timer;
import java.util.TimerTask;
import sagex.api.PluginAPI;


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
    private static final char[] symbols = new char[36];
    private static boolean symbolsInit = false;
    private static boolean locationsInit = false;
    private static final Random random = new Random();
    public static final String OptionNotFound = "Option not Found";
    public static enum TriState{YES,NO,OTHER};
    public static enum ExportType{ALL,WIDGETS,FLOWS,FLOW,MENUS,GENERAL};
    public static final String ListToken = ":&&:";
    public static enum ClientType {None, Local, MVP, HD100, HD200, HD300, Placeshifter};
    
    public static void main(String[] args){

        //String test = StringNumberFormat("27.96903", 0, 2);
        //String test = StringNumberFormat("27.1", 0, 2);
        //LOG.debug(test);
        //api.InitLogger();
        //test1();
        
    }
    
//    public static void test1(){
//        PropertiesExt Props = new PropertiesExt();
//        String FilePath = util.UserDataLocation() + File.separator + "menutest.properties";
//        Boolean KeepProcessing = Boolean.TRUE;
//        //read the properties from the properties file
//        try {
//            FileInputStream in = new FileInputStream(FilePath);
//            try {
//                Props.load(in);
//                in.close();
//            } catch (IOException ex) {
//                LOG.debug("test1: IO exception inporting properties " + util.class.getName() + ex);
//                KeepProcessing = Boolean.FALSE;
//            }
//        } catch (FileNotFoundException ex) {
//            LOG.debug("test1: file not found inporting properties " + util.class.getName() + ex);
//            KeepProcessing = Boolean.FALSE;
//        }
//        if (KeepProcessing){
//            LOG.debug("test1: start of BRANCHES");
//            for (String Key:Props.GetSubpropertiesThatAreBranches("Gemstone/Widgets")){
//                LOG.debug("TEST item '" + Key + "'");
//            }
//            LOG.debug("test1: start of LEAVES");
//            for (String Key:Props.GetSubpropertiesThatAreLeaves("Gemstone/Widgets")){
//                LOG.debug("TEST item '" + Key + "'");
//            }
//        }
//        
//    }
    
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
    public static void gc(final int repeat){
        final String Before = FreeMem();
        for (int i = 0;i<repeat;i++){
            java.lang.System.gc();
        }
        LOG.debug("gc: run " + repeat + " times. Before/After: " + Before + FreeMem());
    }
    private static Timer gcTimer;
    public static void gcback(final int repeat){
        LOG.debug("gc: Starting the background gc " + repeat + " times.");
        final String Before = FreeMem();
        //run the gc in a thread
        gcTimer = new Timer();

        TimerTask task = new TimerTask() {
            public void run() {
                for (int i = 0;i<repeat;i++){
                    java.lang.System.gc();
                }
                gcTimer.cancel();
                LOG.debug("gc: run " + repeat + " times. Before/After: " + Before + FreeMem());
            }
        };
        //wait 1 second and then run the gc
        gcTimer.schedule(task, 1000);
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

    //TODO: check out this function as it likely always returns the full array
    public static Object CheckSimpleSize(Object[] Files,int sizeneeded) {
        if (!Arrays.asList(Files).contains("blankelement")) {

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

    public static void InitNameGen(){
        for (int idx = 0; idx < 10; ++idx)
            util.symbols[idx] = (char) ('0' + idx);
        for (int idx = 10; idx < 36; ++idx)
            util.symbols[idx] = (char) ('a' + idx - 10);
        symbolsInit = true;
    }
    
    public static String GenerateRandomName(){
        if (!symbolsInit){
            InitNameGen();
        }
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
    
    public static Boolean GetServerPropertyAsBoolean(String Property, Boolean DefaultValue){
        String tValue = sagex.api.Configuration.GetServerProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
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
        String tValue = sagex.api.Configuration.GetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Property, null);
        if (tValue==null || tValue.equals(OptionNotFound)){
            return DefaultValue;
        }
        return GetInteger(tValue, DefaultValue);
    }

    public static Integer GetInteger(Object Value, Integer DefaultValue){
        //force a string to an integer or return the default
        if (Value==null){
            return DefaultValue;
        }
        Integer tInteger = DefaultValue;
        try {
            tInteger = Integer.valueOf(Value.toString());
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
    public static Boolean GetTrueFalseOption(String PropSection, String PropName){
        return GetTrueFalseOptionBase(Boolean.FALSE, PropSection, PropName, Boolean.FALSE);
    }
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
            //LOG.debug("SetListOptionNextBase: Not Found so setting to 2nd item '" + FullList.get(1) + "' for '" + tProp + "'");
            util.SetProperty(tProp, FullList.get(1));  //default to the 2nd item
        }else{
            Integer pos = FullList.indexOf(CurrentValue);
            //LOG.debug("SetListOptionNextBase: Found - pos = " + pos + "' for '" + tProp + "'");
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

    //remove an option property
    public static void RemoveOption(String PropSection, String PropName){
        RemoveOptionBase(Boolean.FALSE, PropSection, PropName);
    }
    public static void RemoveOptionBase(Boolean bFlow, String PropSection, String PropName){
        String tProp = "";
        if (bFlow){
            tProp = Flow.GetFlowBaseProp(PropSection) + Const.PropDivider + PropName;
        }else{
            tProp = Const.BaseProp + Const.PropDivider + PropSection + Const.PropDivider + PropName;
        }
        RemoveProperty(tProp);
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
    
    public static String PrintDateSortable(){  
        DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");  
        return df.format(new Date());  
    }      

    public static String PrintDateTime(){  
        DateFormat df = new SimpleDateFormat("HHmm MMM dd yyyy");  
        return df.format(new Date());  
    }      

    public static String PrintDateSortable(Date myDate){  
        DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");  
        return df.format(myDate);  
    }      

    public static String PrintDateTime(Date myDate){  
        DateFormat df = new SimpleDateFormat("HHmm MMM dd yyyy");  
        return df.format(myDate);  
    }      
    
    public static String UserDataLocation(){
        return GetSageTVRootDir() + File.separator + "userdata" + File.separator + "Gemstone";
    }
    public static String UserDataLocationTemp(){
        return GetSageTVRootDir() + File.separator + "userdata" + File.separator + "Gemstone" + File.separator + "temp";
    }
    public static String UserDataLocationServer(){
        return sagex.api.Utility.GetWorkingDirectory() + File.separator + "userdata" + File.separator + "Gemstone";
    }

    public static String DefaultsLocation(){
        return GetSageTVRootDir() + File.separator + "STVs" + File.separator + "Gemstone" + File.separator + "defaults";
    }
    
    public static String MenusLocation(){
        return GetSageTVRootDir() + File.separator + "STVs" + File.separator + "Gemstone" + File.separator + "menus";
    }
    
    public static String WeatherLocation(){
        return GetSageTVRootDir() + File.separator + "STVs" + File.separator + "Gemstone" + File.separator + "Weather";
    }

    public static String GemstoneSTVFile(){
        return GetSageTVRootDir() + File.separator + "STVs" + File.separator + "SageTV7" + File.separator + "Gemstone.xml";
    }
    
    public static String ThemeLocation(){
        return GetSageTVRootDir() + File.separator + "STVs" + File.separator + "SageTV7" + File.separator + "Themes" + File.separator + "Gemstone";
    }

    public static String GetSageTVRootDir(){
        return sagex.phoenix.Phoenix.getInstance().getSageTVRootDir().toString();
    }
    
    public static String GetLocalWorkingDir(){
        String tSTV = sagex.api.WidgetAPI.GetCurrentSTVFile(new UIContext(sagex.api.Global.GetUIContextName()));
        File tFileSTV = new File(tSTV).getParentFile().getParentFile().getParentFile();
        return tFileSTV.toString();
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
    
    public static boolean DisableGemstonePlugin(){
        UIContext tUI = new UIContext(sagex.api.Global.GetUIContextName());
        Object thisPlugin = sagex.api.PluginAPI.GetAvailablePluginForID(tUI,"DiamondSTVi");
        if (sagex.api.PluginAPI.IsPluginEnabled(tUI,thisPlugin)){
            boolean ret = false;
            ret = sagex.api.PluginAPI.DisablePlugin(tUI, thisPlugin);
            if (ret){
                LOG.debug("DisableGemstonePlugin: Gemstone plugin found and disabled");
            }else{
                LOG.debug("DisableGemstonePlugin: Gemstone plugin found but could not be disabled");
            }
            return ret;
        }else{
            LOG.debug("DisableGemstonePlugin: Gemstone plugin was not enabled");
            return false;
        }
        
    }

    public static void LogPlugins(){
        if (LOG.isDebugEnabled()){
            UIContext tUI = new UIContext(sagex.api.Global.GetUIContextName());
            //List system plugins
            Object[] plugins = PluginAPI.GetInstalledPlugins(tUI);
            LOG.debug("LogPlugins: checking system plugins: " + LogInfo());
            if (plugins != null && plugins.length > 0) {
                String enabledText;
                for (Object plugin : plugins) {
                    if (PluginAPI.IsPluginEnabled(tUI,plugin)){
                        enabledText = "Y";
                    }else{
                        enabledText = "N";
                    }
                    LOG.debug("    : System Plugin: Enabled '" + enabledText + "' ID '" + PluginAPI.GetPluginIdentifier(tUI,plugin)+ "' Name: '" + PluginAPI.GetPluginName(tUI, plugin) + "'");
                }
            }else{
                LOG.debug("    : None found");
            }
            //List client plugins
            plugins = PluginAPI.GetInstalledClientPlugins(tUI);
            LOG.debug("LogPlugins: checking client plugins: " + LogInfo());
            if (plugins != null && plugins.length > 0) {
                String enabledText;
                for (Object plugin : plugins) {
                    if (PluginAPI.IsPluginEnabled(tUI,plugin)){
                        enabledText = "Y";
                    }else{
                        enabledText = "N";
                    }
                    LOG.debug("    : Client Plugin: Enabled '" + enabledText + "' ID '" + PluginAPI.GetPluginIdentifier(tUI,plugin)+ "' Name: '" + PluginAPI.GetPluginName(tUI, plugin) + "'");
                }
            }else{
                LOG.debug("    : None found");
            }
        }
    }
    
    public static void LogConnectedClients(){
        if (LOG.isDebugEnabled()){
            UIContext tUI = new UIContext(sagex.api.Global.GetUIContextName());
            LOG.debug("LogConnectedClients: checking remote connected clients: " + LogInfo());
            for (String client:GemstonePlugin.getNonPCClients()){
                LOG.debug("    : Internal List: '" + client + "'");
            }
        }
    }
    
    public static Boolean HandleNonCompatiblePlugins(){
        LOG.debug("HandleNonCompatiblePlugins: start checking for non-compatible plugins: " + LogInfo());
        Boolean DisableForConflict = Boolean.TRUE;
        Boolean ReloadUI = Boolean.FALSE;
        //Boolean DisableForConflict = GetTrueFalseOption("Utility", "PluginConflictMode", Boolean.FALSE);
        UIContext tUI = new UIContext(sagex.api.Global.GetUIContextName());

//        //List plugins
//        Object[] plugins = PluginAPI.GetAllAvailablePlugins();
//        if (plugins != null && plugins.length > 0) {
//            for (Object plugin : plugins) {
//                LOG.debug("HandleNonCompatiblePlugins: Plugin '" + PluginAPI.GetPluginIdentifier(plugin)+ "' Installed '" + PluginAPI.IsPluginInstalled(plugin) + "' Enabled '" + PluginAPI.IsPluginEnabled(plugin) + "' C Installed '" + PluginAPI.IsClientPluginInstalled(plugin) + "'");
//            }
//        }
//        
        //check for CVF
        //LOG.debug("HandleNonCompatiblePlugins: checking for CVF: " + LogInfo());
        Object thisPlugin = sagex.api.PluginAPI.GetAvailablePluginForID(tUI,"jusjokencvf");
        if (sagex.api.PluginAPI.IsPluginEnabled(tUI,thisPlugin)){
            if(DisableForConflict){
                sagex.api.PluginAPI.DisablePlugin(tUI, thisPlugin);
                String tMessage = "CVF Plugin is NOT compatible with Gemstone as Gemstone now contains similar functions.\n \n* CVF Plugin has been disabled.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelInfo, tMessage);
                ReloadUI = Boolean.TRUE;
                LOG.debug("HandleNonCompatiblePlugins: CVF found and disabled");
            }else{
                String tMessage = "CVF Plugin is NOT compatible with Gemstone as Gemstone now contains similar functions.\n \n* Please disable the CVF Plugin and reload the UI.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelError, tMessage);
                LOG.debug("HandleNonCompatiblePlugins: CVF found and System Error message created");
            }
        }else{
            LOG.debug("HandleNonCompatiblePlugins: checking for CVF - not found");
        }
        //check for ADM
        //LOG.debug("HandleNonCompatiblePlugins: checking for ADM: " + LogInfo());
        thisPlugin = sagex.api.PluginAPI.GetAvailablePluginForID(tUI,"jusjokenadm");
        if (sagex.api.PluginAPI.IsPluginEnabled(tUI, thisPlugin)){
            if(DisableForConflict){
                sagex.api.PluginAPI.DisablePlugin(tUI, thisPlugin);
                String tMessage = "ADM Plugin is NOT compatible with Gemstone as Gemstone now contains similar functions.\n \n* ADM Plugin has been disabled.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelInfo, tMessage);
                ReloadUI = Boolean.TRUE;
                LOG.debug("HandleNonCompatiblePlugins: ADM found and disabled");
            }else{
                String tMessage = "ADM Plugin is NOT compatible with Gemstone as Gemstone now contains similar functions.\n \n* Please disable the ADM Plugin and reload the UI.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelError, tMessage);
                LOG.debug("HandleNonCompatiblePlugins: ADM found and System Error message created");
            }
        }else{
            LOG.debug("HandleNonCompatiblePlugins: checking for ADM - not found");
        }
        //check for Diamond Legacy
        //LOG.debug("HandleNonCompatiblePlugins: checking for DiamondLegacy: " + LogInfo());
        thisPlugin = sagex.api.PluginAPI.GetAvailablePluginForID(tUI,"DiamondLegacySTVi");
        if (sagex.api.PluginAPI.IsPluginEnabled(tUI, thisPlugin)){
            if(DisableForConflict){
                sagex.api.PluginAPI.DisablePlugin(tUI, thisPlugin);
                String tMessage = "Diamond Legacy Plugin is NOT compatible with Gemstone as Gemstone now replaces Diamond.\n \n* Diamond Legacy Plugin has been disabled.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelInfo, tMessage);
                ReloadUI = Boolean.TRUE;
                LOG.debug("HandleNonCompatiblePlugins: Diamond Legacy found and disabled");
            }else{
                String tMessage = "Diamond Legacy Plugin is NOT compatible with Gemstone as Gemstone now replaces Diamond.\n \n* Please disable the Diamond Legacy Plugin and reload the UI.";
                PostSytemMessage(Const.SystemMessagePluginConflictCode, Const.SystemMessagePluginConflictName, Const.SystemMessageAlertLevelError, tMessage);
                LOG.debug("HandleNonCompatiblePlugins: Diamond Legacy found and System Error message created");
            }
        }else{
            LOG.debug("HandleNonCompatiblePlugins: checking for Diamond Legacy - not found");
        }
        LOG.debug("HandleNonCompatiblePlugins: complete - checking for non-compatible plugins: " + LogInfo());
        return ReloadUI;
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
    
    public static String SidewaysText(String text){
        //reverse the text first
        String t = new StringBuffer(text).reverse().toString();
        //add newline characters between each letter
        String o = "";
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < t.length(); i++) {
            if (i==0){
                buf.append(t.charAt(i));
            }else{
                buf.append("\n" + t.charAt(i));
            }
        } 
        o = buf.toString();
        o = o.toLowerCase();
        LOG.debug("SidewaysText: text in '" + text + "' text out '" + o + "'");
        return o;
    }

    //TODO: remove as only used for temp conversion for Scott's Menus for Playon
//    public static void BuildActions(){
//        UIContext tUI = new UIContext(sagex.api.Global.GetUIContextName());
//        Properties Props = new Properties();
//        Object[] Menus = sagex.api.WidgetAPI.GetWidgetsByType(tUI,"Menu");
//        for (Object item: Menus){
//            String tName = sagex.api.WidgetAPI.GetWidgetName(item);
//            //LOG.debug("BuildActions: Menu '" + tName + "'");
//            if (tName.startsWith("PlayOn::")){
//                String tTitle = tName.substring(8);
//                if (tTitle.startsWith("Custom")){
//                    continue;
//                }
//                String ActionName = tTitle.replaceAll("::", "_");
//                String ActionVal = tTitle.replaceAll("::", " ");
//                ActionName = "xItemPlayOn_" + ActionName.replaceAll(" ", "_");
//                String ActionTitle = tTitle.replaceAll("::", " - ");
//                LOG.debug("BuildActions: Item '" + ActionName + "' Title '" + ActionTitle + "'");
//                String Start = "ADM/custom_actions/";
//                Props.put(Start + ActionName + "/ActionCategory/1", "Online");
//                Props.put(Start + ActionName + "/ActionCategory/2", "PlayOn");
//                Props.put(Start + ActionName + "/ButtonText", ActionTitle);
//                Props.put(Start + ActionName + "/WidgetSymbol", "KMWIY-932161");
//                Props.put(Start + ActionName + "/ActionVariables/1/Val", ActionVal);
//                Props.put(Start + ActionName + "/ActionVariables/1/Var", "PlayOnMenuItem");
//                Props.put(Start + ActionName + "/ActionVariables/1/VarType", "VarTypeGlobal");
//                Props.put(Start + ActionName + "/CopyModeAttributeVar", "ThisItem");
//            }
//        }
//        String FilePath = util.UserDataLocation() +  File.separator + "ActionTemp.properties";
//        if (Props.size()>0){
//            try {
//                FileOutputStream out = new FileOutputStream(FilePath);
//                try {
//                    Props.store(out, Const.PropertyComment);
//                    out.close();
//                } catch (IOException ex) {
//                    LOG.debug("Execute: error exporting properties " + util.class.getName() + ex);
//                }
//            } catch (FileNotFoundException ex) {
//                LOG.debug("Execute: error exporting properties " + util.class.getName() + ex);
//            }
//            
//        }
//    }

    public static boolean IsClient(){
        UIContext uic = new UIContext(sagex.api.Global.GetUIContextName());
        return sagex.api.Global.IsClient(uic) || (!sagex.api.Global.IsClient(uic) && !sagex.api.Global.IsRemoteUI(uic) && (GetProperty("client","")=="true"));
    }
    
    public static ArrayList<Integer> GetNumberList(int Start, int Max){
        if (Start>Max){
            return new ArrayList<Integer>();
        }
        ArrayList<Integer> tList = new ArrayList<Integer>();
        for (int i=Start;i<=Max;i++){
            tList.add(i);
        }
        return tList;
    }
    
    public static ArrayList<Object> GetSageMediaFiles(List MediaFiles){
        if (MediaFiles==null){
            return new ArrayList<Object>();
        }
        ArrayList<Object> outMediaFiles = new ArrayList<Object>();
        int counter = 0;
        for (Object inMediaFile:MediaFiles){
            outMediaFiles.add(phoenix.media.GetSageMediaFile(inMediaFile));
            counter++;
        }
        LOG.debug("GetSageMediaFiles: converted '" + MediaFiles.size() + "' MediaFiles to SageMediaFiles");
        return outMediaFiles;
    }

    public static String GetClientType(){
        UIContext uic = new UIContext(sagex.api.Global.GetUIContextName());
        ClientType clientType = ClientType.None;
        if (sagex.api.Global.GetRemoteUIType(uic).equalsIgnoreCase(sagex.api.Utility.LocalizeString("Local"))){
            clientType = ClientType.Local;
        }
        else if (sagex.api.Global.GetRemoteUIType(uic).equalsIgnoreCase(sagex.api.Utility.LocalizeString("Placeshifter"))){
            clientType = ClientType.Placeshifter;
        }
        else if (sagex.api.Global.GetRemoteUIType(uic).equalsIgnoreCase(sagex.api.Utility.LocalizeString("SD Media Extender"))){
            clientType = ClientType.MVP;
        }
        else if (sagex.api.Global.GetRemoteUIType(uic).equalsIgnoreCase(sagex.api.Utility.LocalizeString("HD Media Extender"))){
            clientType = ClientType.HD100;
        }
        else if (sagex.api.Global.GetRemoteUIType(uic).equalsIgnoreCase(sagex.api.Utility.LocalizeString("HD Media Player"))){
            clientType = ClientType.HD300;
            for (String option : sagex.api.Configuration.GetAudioOutputOptions(uic)){
		if (option.equalsIgnoreCase("HDMIHBR")){
                    clientType = ClientType.HD200;
                    break;
                }
            }
        }
        return clientType.toString();
    }

    public static String LogInfo(){
        return GetClientType() + ":" + FreeMem();
    }

    public static void DebugLog(String message){
        DebugLog(message, false);
    }
    public static void DebugLog(String message, boolean IncludeInfo){
        String tMessage = message;
        if (IncludeInfo){
            tMessage = tMessage + ": " + LogInfo();
        }
        LOG.debug(tMessage);
    }

    public static void InfoLog(String message){
        InfoLog(message, false);
    }
    public static void InfoLog(String message, boolean IncludeInfo){
        String tMessage = message;
        if (IncludeInfo){
            tMessage = tMessage + ": " + LogInfo();
        }
        LOG.info(tMessage);
    }

    public static void InitLocations(){
        if (!locationsInit){
            InitLocation(util.UserDataLocation());
            InitLocation(util.UserDataLocationTemp());
            locationsInit = true;
        }else{
            LOG.debug("InitLocations: called but previously run so nothing completed this time");
        }
    }

    private static void InitLocation(String LocationPath){
        try{
            boolean success = (new File(LocationPath)).mkdirs();
            if (success) {
                LOG.debug("InitLocation: Directories created for '" + LocationPath + "'");
            }else{
                LOG.debug("InitLocation: mkdirs returned false for '" + LocationPath + "'");
            }

        }catch (Exception ex){//Catch exception if any
            LOG.debug("InitLocation: - error creating '" + LocationPath + "'" + ex.getMessage());
        }
    }

    public static String LastofString(String text, int maxSize){
        String tStr = text.substring(Math.max(0, text.length() - maxSize));
        if (tStr.length()==maxSize){
            return "..." + tStr;
        }else{
            return tStr;
        }
    }
    
}

