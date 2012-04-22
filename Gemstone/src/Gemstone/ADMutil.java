/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

/**
 *
 * @author jusjoken
 */

import sagex.UIContext;
import java.util.Properties;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class ADMutil {

    public static String Version = "0.502";
    public static final String ListToken = ":&&:";
    public static final String PropertyComment = "---ADM MenuItem Properties - Do Not Manually Edit---";
    public static final String PropertyBackupFile = "ADMbackup.properties";
    public static final String SageADMBasePropertyLocation = "ADM/";
    public static final String SagePropertyLocation = "ADM/menuitem/";
    public static final String SageFocusPropertyLocation = "ADM/focus/";
    public static final String SageBackgroundsPropertyLocation = "ADM/backgrounds/";
    public static final String SageADMSettingsPropertyLocation = "ADM/settings";
    public static final String AdvancedModePropertyLocation = "ADM/settings/advanced_mode";
    public static final String UseADMPropertyLocation = "ADM/settings/use_adm";
    public static final String UseQLMPropertyLocation = "ADM/settings/use_qlm";
    public static final String ADMCopyModePropertyLocation = "ADM/settings/admcopymode";
    public static final String TopMenu = "xTopMenu";
//    public static final String ADMLocation = sagex.api.Utility.GetWorkingDirectory(new UIContext(sagex.api.Global.GetUIContextName())) + File.separator + "userdata" + File.separator + "ADM";
//    public static final String ADMDefaultsLocation = sagex.api.Utility.GetWorkingDirectory(new UIContext(sagex.api.Global.GetUIContextName())) + File.separator + "STVs" + File.separator + "ADM" + File.separator + "defaults";
    private static final String SageBGVariablesListFile = "ADMSageBGVariables.properties";
    private static final String SageSubMenusLevel1ListFile = "ADMSageSubMenus1.properties";
    private static final String SageSubMenusLevel2ListFile = "ADMSageSubMenus2.properties";
    public static final String ListNone = "<None>";
    public static final String OptionNotFound = "Option not Found";
    public static final String ActionTypeDefault = "DoNothing";
    public static final String ButtonTextDefault = "<Not defined>";
    public static final String SortStyleDefault = "xNaturalOrder";
    public static Boolean ADMInitComplete = false;
    public static Properties SageBGVariablesProps = new Properties();
    public static Collection<String> SageBGVariablesKeys = new LinkedHashSet<String>();
    public static Collection<String> SageSubMenusKeys = new LinkedHashSet<String>();
    public static Properties SageSubMenusLevel1Props = new Properties();
    public static Collection<String> SageSubMenusLevel1Keys = new LinkedHashSet<String>();
    public static Properties SageSubMenusLevel2Props = new Properties();
    public static Collection<String> SageSubMenusLevel2Keys = new LinkedHashSet<String>();
    public static final char[] symbols = new char[36];
    private static final Random random = new Random();
    public static List<String> SageBackgrounds = new LinkedList<String>(); 
    private static final String MainMenuWidgetSymbol = "OPUS4A-202264"; //"BASE-44343";
    public static enum TriState{YES,NO,OTHER};

    public static String GetListNone(){ return ListNone; }

    public static Boolean GetDefaultsWorkingMode() {
        return GetPropertyAsBoolean(SageADMSettingsPropertyLocation + "/DefaultsWorkingMode", Boolean.FALSE);
    }
    public static void SetDefaultsWorkingMode() {
        Boolean tValue = !GetDefaultsWorkingMode(); 
        SetProperty(SageADMSettingsPropertyLocation + "/DefaultsWorkingMode", tValue.toString());
        System.out.println("ADM: uDefaultsWorkingMode - set to '" + tValue.toString() + "'");
    }
    
    //ADM Hidden Features are toggled in ADM by typing 5309 on the Close button on the Options Menu in ADM Manager
    public static Boolean GetADMHiddenFeaturesMode() {
        return GetPropertyAsBoolean(SageADMSettingsPropertyLocation + "/ADMHiddenFeaturesMode", Boolean.FALSE);
    }
    public static void SetADMHiddenFeaturesMode() {
        Boolean tValue = !GetADMHiddenFeaturesMode(); 
        SetProperty(SageADMSettingsPropertyLocation + "/ADMHiddenFeaturesMode", tValue.toString());
        System.out.println("ADM: uADMHiddenFeaturesMode - set to '" + tValue.toString() + "'");
    }
    
    public static void InitADM(){
        
        if (!ADMInitComplete) {
            //initiate one time load items
            
            //Init Actions
            ADMAction.Init();
            
            //ensure the ADM file location exists
            try{
                boolean success = (new File(ADMLocation())).mkdirs();
                if (success) {
                    System.out.println("ADM: uInitADM - Directories created for '" + ADMLocation() + "'");
                   }

                }catch (Exception ex){//Catch exception if any
                    System.out.println("ADM: uInitADM - error creating '" + ADMLocation() + "'" + ex.getMessage());
                }
            
            //also load the BGVariables for BG Images on Top Level Menus
            LoadSageBGVariablesList();
            SageBGVariablesKeys.add(ListNone);
            LoadSageBGList();
            //also load SubMenu lists for levels 1 and 2 and Diamond
            LoadSubMenuListLevel1();
            LoadSubMenuListLevel2();
            //Add in a -None- option to the list
            SageSubMenusLevel1Keys.add(ListNone);
            SageSubMenusLevel2Keys.add(ListNone);

            //clean up existing focus items
            ClearFocusStorage();
        
            //generate symbols to be used for new MenuItem names
            for (int idx = 0; idx < 10; ++idx)
                symbols[idx] = (char) ('0' + idx);
            for (int idx = 10; idx < 36; ++idx)
                symbols[idx] = (char) ('a' + idx - 10);
            
            ADMInitComplete = true;

            System.out.println("ADM: uInitADM - One Time initialization complete.");

        }
        //initiate items that may differ per UIContext - the UI needs to ensure this only gets loaded once
        ADMMenuNode.LoadMenuItemsFromSage();
        System.out.println("ADM: uInitADM - UI level initialization complete.");

    }
    
    public static void ClearFocusStorage(){
            //clean up existing focus items
            RemovePropertyAndChildren(SageFocusPropertyLocation);
    }
    
    public static void ClearAll(){

        //backup existing MenuItems before clearing settings and menus
        if (ADMMenuNode.MenuNodeList().size()>0){
            ADMMenuNode.ExportMenuItems(PropertyBackupFile);
        }
        
        //clear all the Sage property settings for ADM
        System.out.println("ADM: uClearAll: clear Sage Properties");
        RemovePropertyAndChildren(SageADMBasePropertyLocation);
        System.out.println("ADM: uClearAll: clear Sage Server Properties");
        RemoveServerPropertyAndChildren(SageADMBasePropertyLocation);
        ADMInitComplete = Boolean.FALSE;
        System.out.println("ADM: uClearAll: load default menus");
        ADMMenuNode.LoadMenuItemDefaults();
        System.out.println("ADM: uClearAll: initialize settings");
        InitADM();
        System.out.println("ADM: uClearAll: complete - settings restored to defaults");
        
    }

    public static void ReloadADMSettings(){

        //clear all the Sage property settings for ADM
        System.out.println("ADM: uReloadADMSettings: reload ADM settings");
        ADMInitComplete = Boolean.FALSE;
        InitADM();
        System.out.println("ADM: uReloadADMSettings: complete");
        
    }

    public static String GetElement(Collection<String> List, Integer element){
        System.out.println("ADM: uGetElement: looking for element " + element + " in:" + List);
        Integer counter = 0;
        for (String CurElement:List){
            counter++;
            System.out.println("ADM: uGetElement: checking element '" + counter + "' = '" + CurElement + "'");
            if (counter.equals(element)){
                System.out.println("ADM: uGetElement: found '" + CurElement + "'");
                return CurElement;
            }
        }
        System.out.println("ADM: uGetElement: not found.");
        return null;
    }
    
    //Create/Edit MenuItems Dialog Options
    public static Collection<String> GetEditOptionsList(String Name){
        Collection<String> EditOptions = new LinkedHashSet<String>();
        //Determine valid Edit Options for the passed in MenuItem Name
        EditOptions.add("admEditMenuItem"); //Edit current Menu Item
        EditOptions.add("admAddMenuItem"); //Add current Menu Item below
        if (ADMMenuNode.GetMenuItemLevel(Name)<3){
            //do not allow a child to be added to a Dynamic list item
            if (!ADMMenuNode.GetMenuItemActionType(Name).equals(ADMAction.DynamicList)){
                EditOptions.add("admAddSubMenuItem"); //Add SubMenu to current Menu Item
            }
        }
        EditOptions.add("admDeleteMenuItem"); //Delete current Menu Item
        EditOptions.add("admCloseEdit"); //Close the Edit Menu
        System.out.println("ADM: uGetEditOptionsList - Loaded list for '" + Name + "' :" + EditOptions);
        return EditOptions;
    }
    
    public static String GetEditOptionButtonText(String Option, String Name){
        String ButtonText = OptionNotFound;
        if("admEditMenuItem".equals(Option)){
            ButtonText = "Edit";
        }else if("admAddMenuItem".equals(Option)){
            ButtonText = "Add Menu Item below '" + ADMMenuNode.GetMenuItemButtonText(Name) + "'";
        }else if("admAddSubMenuItem".equals(Option)){
            ButtonText = "Add Child Menu to '" + ADMMenuNode.GetMenuItemButtonText(Name) + "'";
        }else if("admDeleteMenuItem".equals(Option)){
            ButtonText = "Delete";
        }else if("admCloseEdit".equals(Option)){
            ButtonText = "Close";
        }
        //System.out.println("ADM: uGetEditOptionButtonText returned '" + ButtonText + "' for '" + Option + "'");
        return ButtonText;
    }
    
    public static void LoadSubMenuListLevel1(){
        String SubMenuPropsPath = ADMDefaultsLocation() + File.separator + SageSubMenusLevel1ListFile;
        SageSubMenusLevel1Keys.clear();
        SageSubMenusKeys.clear();
        
        //read the properties from the properties file
        try {
            FileInputStream in = new FileInputStream(SubMenuPropsPath);
            try {
                SageSubMenusLevel1Props.load(in);
                in.close();
            } catch (IOException ex) {
                System.out.println("ADM: uLoadSubMenuListLevel1: IO exception loading standard actions " + ADMutil.class.getName() + ex);
                return;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("ADM: uLoadSubMenuListLevel1: file not found loading standard actions " + ADMutil.class.getName() + ex);
            return;
        }

        //sort the keys into value order
        SortedMap<String,String> SageSubMenusList = new TreeMap<String,String>();

        //Add all the Values to a sorted list
        for (String SageSubMenusItem : SageSubMenusLevel1Props.stringPropertyNames()){
            SageSubMenusList.put(SageSubMenusLevel1Props.getProperty(SageSubMenusItem),SageSubMenusItem);
        }

        //build a list of keys in the order of the values
        for (String SageSubMenusValue : SageSubMenusList.keySet()){
            SageSubMenusLevel1Keys.add(SageSubMenusList.get(SageSubMenusValue));
            SageSubMenusKeys.add(SageSubMenusList.get(SageSubMenusValue));
        }
        
        //Add in a -None- option to the list
        SageSubMenusLevel1Props.put(ListNone,ListNone);
        
        System.out.println("ADM: uLoadSubMenuListLevel1: completed for '" + SubMenuPropsPath + "'");
        return;
    }

    public static void LoadSubMenuListLevel2(){
        String SubMenuPropsPath = ADMDefaultsLocation() + File.separator + SageSubMenusLevel2ListFile;
        SageSubMenusLevel2Keys.clear();
        
        //read the properties from the properties file
        try {
            FileInputStream in = new FileInputStream(SubMenuPropsPath);
            try {
                SageSubMenusLevel2Props.load(in);
                in.close();
            } catch (IOException ex) {
                System.out.println("ADM: uLoadSubMenuListLevel2: IO exception loading standard actions " + ADMutil.class.getName() + ex);
                return;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("ADM: uLoadSubMenuListLevel2: file not found loading standard actions " + ADMutil.class.getName() + ex);
            return;
        }

        //sort the keys into value order
        SortedMap<String,String> SageSubMenusList = new TreeMap<String,String>();

        //Add all the Values to a sorted list
        for (String SageSubMenusItem : SageSubMenusLevel2Props.stringPropertyNames()){
            SageSubMenusList.put(SageSubMenusLevel2Props.getProperty(SageSubMenusItem),SageSubMenusItem);
        }

        //build a list of keys in the order of the values
        for (String SageSubMenusValue : SageSubMenusList.keySet()){
            SageSubMenusLevel2Keys.add(SageSubMenusList.get(SageSubMenusValue));
            SageSubMenusKeys.add(SageSubMenusList.get(SageSubMenusValue));
        }
        
        //Add in a -None- option to the list
        SageSubMenusLevel2Props.put(ListNone,ListNone);
        
        System.out.println("ADM: uLoadSubMenuListLevel2: completed for '" + SubMenuPropsPath + "'");
        return;
    }

    public static void LoadSageBGVariablesList(){
        String StandardActionPropsPath = ADMDefaultsLocation() + File.separator + SageBGVariablesListFile;
        SageBGVariablesKeys.clear();
        
        //read the properties from the properties file
        try {
            FileInputStream in = new FileInputStream(StandardActionPropsPath);
            try {
                SageBGVariablesProps.load(in);
                in.close();
            } catch (IOException ex) {
                System.out.println("ADM: uLoadSageBGVariablesList: IO exception loading SageBGVariables " + ADMutil.class.getName() + ex);
                return;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("ADM: uLoadSageBGVariablesList: file not found loading SageBGVariables " + ADMutil.class.getName() + ex);
            return;
        }

        //sort the keys into value order
        SortedMap<String,String> ActionValuesList = new TreeMap<String,String>();

        //Add all the Values to a sorted list
        for (String ActionItem : SageBGVariablesProps.stringPropertyNames()){
            ActionValuesList.put(SageBGVariablesProps.getProperty(ActionItem),ActionItem);
        }

        //build a list of keys in the order of the values
        for (String ActionValue : ActionValuesList.keySet()){
            SageBGVariablesKeys.add(ActionValuesList.get(ActionValue));
        }
        
        System.out.println("ADM: uLoadSageBGVariablesList: completed for '" + StandardActionPropsPath + "'");
        return;
    }

    public static String GetSageBGVariablesButtonText(String Option){
        if (Option==null || Option.equals(ListNone)){
            return ListNone;
        }
        //determine if using Advanced options
        if (IsAdvancedMode()){
            return SageBGVariablesProps.getProperty(Option, ListNone) + " (" + Option + ")";
        }else{
            return SageBGVariablesProps.getProperty(Option, ListNone);
        }
    }

    public static Collection<String> GetSageBGVariablesList(){
        return SageBGVariablesKeys;
    }

    public static void LoadSageBGList(){
        SageBackgrounds.clear();
        //add all the Backgrounds from the available Theme variables
        SageBackgrounds.addAll(SageBGVariablesKeys);
        //remove none as it may not be where we want it in the list
        SageBackgrounds.remove(ListNone);
        System.out.println("ADM: uLoadSageBGList: Loaded BGVariables");
        //add None to the start of the list
        SageBackgrounds.add(0,ListNone);

        //find all Backgrounds from the SageTV properties file
        String[] tBackgrounds = sagex.api.Configuration.GetServerSubpropertiesThatAreLeaves(new UIContext(sagex.api.Global.GetUIContextName()),SageBackgroundsPropertyLocation);
        System.out.println("ADM: uLoadSageBGList: Getting '" + tBackgrounds.length + "' backgrounds + UI = '" + sagex.api.Global.GetUIContextName() + "' tBackgrounds = '" + tBackgrounds + "'");
        if (tBackgrounds.length>0){
            for (String BGKey: tBackgrounds){
                //only add valid backgrounds
                String PropLocation = ADMutil.SageBackgroundsPropertyLocation + BGKey;
                String tPath = GetServerProperty(PropLocation, OptionNotFound);
                if (!tPath.equals(OptionNotFound)){
                    SageBackgrounds.add(BGKey);
                }else{
                    //remove any invalid backgrounds
                    RemoveServerProperty(PropLocation);
                }
                
            }
            System.out.println("ADM: uLoadSageBGList: Loading Backgrounds");
        }
    }

    public static List<String> GetSageBGList(){
        return SageBackgrounds;
    }

    public static Integer GetSageBGListCount(){
        return SageBackgrounds.size();
    }
    
    public static Collection<String> GetSageBGMenuItemList(){
        return ADMMenuNode.GetMenuItemSortedList(Boolean.TRUE);
        //return ADMMenuNode.GetMenuItemParentList(2);
    }

    public static String GetSageBGMenuItemButtonText(String Name){
        return ADMMenuNode.GetMenuItemButtonTextFormatted(Name,null) + " = {" + GetSageBGButtonText(ADMMenuNode.GetMenuItemBGImageFile(Name)) + "}";
    }
    
    public static Integer GetSageBGListItem(String Option){
        Integer tItem = SageBackgrounds.indexOf(Option);
        if (tItem.equals(-1)){
            return 0;
        }else{
            return tItem;
        }
    }

    public static String GetSageBGListElement(Integer Option){
        return SageBackgrounds.get(Option);
    }

    public static String GetSageBGButtonText(String Option){
        if (Option==null || Option.equals(ListNone)){
            return ListNone;
        }

        if (Option.startsWith("adm")){
            //a custom file is being referenced so look up the path from the Sage Properties file
            String PropLocation = ADMutil.SageBackgroundsPropertyLocation + Option;
            String tPath = GetServerProperty(PropLocation, OptionNotFound);
            if (!tPath.equals(OptionNotFound)){
                File tBackground = sagex.api.Utility.CreateFilePath(tPath, "");
                String tBackgroundName = sagex.api.Utility.GetFileNameFromPath(tBackground);
                System.out.println("ADM: uGetSageBGButtonText for '" + Option + "' = '" + tBackgroundName + "'");
                return tBackgroundName;
            }else{
                //remove the Not Found key if it was created as part of the Get
                RemoveServerProperty(PropLocation);
                System.out.println("ADM: uGetSageBGButtonText for '" + Option + "' Invalid request passed in");
                return ListNone;
            }
        }else{
        //determine if using Advanced options
            if (IsAdvancedMode()){
                return SageBGVariablesProps.getProperty(Option, ListNone) + " (" + Option + ")";
            }else{
                return SageBGVariablesProps.getProperty(Option, ListNone);
            }
        }
    }

    public static String GetSageBGFile(String Option){
        //see if using a GlobalVariable from a Theme or a path to an image file
        if (Option==null || Option.equals("") || Option.equals(ListNone)){
            //System.out.println("ADM: uSetBGImageFileandPath for '" + bBGImageFile + "' - null found");
            //System.out.println("ADM: uGetSageBGFile for '" + Option + "' Invalid request passed in");
            return null;
        }
        if (Option.startsWith("adm")){
            //a custom file is being referenced so look up the path from the Sage Properties file
            String PropLocation = ADMutil.SageBackgroundsPropertyLocation + Option;
            String tPath = GetServerProperty(PropLocation, OptionNotFound);
            if (!tPath.equals(OptionNotFound)){
                //System.out.println("ADM: uGetSageBGFile for '" + Option + "' = '" + tPath + "'");
                return tPath;
            }else{
                //remove the Not Found key if it was created as part of the Get
                RemoveServerProperty(PropLocation);
                //System.out.println("ADM: uGetSageBGFile for '" + Option + "' Invalid request passed in");
                return null;
            }
        }else{
            //expect a Global Variable from the theme
            //System.out.println("ADM: uSetBGImageFileandPath for '" + bBGImageFile + "' - variable found");
            String BGImageFilePath = "";
            BGImageFilePath = EvaluateAttribute(Option);
            if (BGImageFilePath.equals(OptionNotFound)){
                //System.out.println("ADM: uGetSageBGFile for '" + Option + "' Evaluate Failed");
                return null;
            }else{
                //System.out.println("ADM: uGetSageBGFile for '" + Option + "' = '" + BGImageFilePath + "'");
                return BGImageFilePath;
            }
        }
    }

    public static void SaveSageBackground(String BackgroundFile){
        if (BackgroundFile==null || BackgroundFile.equals("") || BackgroundFile.equals(ListNone)){
            //do nothing
            System.out.println("ADM: uSaveSageBackground for '" + BackgroundFile + "' NOTHING FOUND");
        }else{
            File tBackground = sagex.api.Utility.CreateFilePath(BackgroundFile, "");
            String tBackgroundPath = tBackground.toString();
            String tBackgroundKey = GetNewBackgroundKey();
            SetServerProperty(SageBackgroundsPropertyLocation + tBackgroundKey, tBackgroundPath);
            System.out.println("ADM: uSaveSageBackground completed for '" + BackgroundFile + "'");
            SageBackgrounds.add(tBackgroundKey);
        }
    }
    
    public static Boolean CustomSageBackgroundExits(String Option){
        //find all Backgrounds from the SageTV properties file and check them against the passed in path
        String[] tBackgrounds = sagex.api.Configuration.GetServerSubpropertiesThatAreLeaves(new UIContext(sagex.api.Global.GetUIContextName()),SageBackgroundsPropertyLocation);
        if (tBackgrounds.length>0){
            for (String BGKey: tBackgrounds){
                //find each background and compare it
                String PropLocation = ADMutil.SageBackgroundsPropertyLocation + BGKey;
                String tPath = GetServerProperty(PropLocation, OptionNotFound);
                if (tPath.equals(Option)){
                    System.out.println("ADM: uCustomSageBackgroundExits: Background found - '" + Option + "'");
                    return Boolean.TRUE;
                }
            }
        }
        System.out.println("ADM: uCustomSageBackgroundExits: Background not found - '" + Option + "'");
        return Boolean.FALSE;
    }
    
    public static Boolean IsCustomSageBackground(String Option){
        //can only remove custom Backgrounds
        if (Option.startsWith("adm")){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static Boolean IsCustomSageBackground(Integer Option){
        //can only remove custom Backgrounds
        return IsCustomSageBackground(GetSageBGListElement(Option));
    }
    
    public static void RemoveAllSageBackgrounds(){
        //find all Backgrounds from the SageTV properties file
        String[] tBackgrounds = sagex.api.Configuration.GetServerSubpropertiesThatAreLeaves(new UIContext(sagex.api.Global.GetUIContextName()),SageBackgroundsPropertyLocation);
        if (tBackgrounds.length>0){
            for (String BGKey: tBackgrounds){
                //remove each background
                RemoveSageBackground(BGKey);
            }
            System.out.println("ADM: uLoadSageBGList: Loading Backgrounds");
        }
        System.out.println("ADM: uRemoveAllSageBackgrounds: Removed '" + tBackgrounds.length + "' backgrounds");
    }
    
    public static void RemoveSageBackground(String Option){
        //can only remove custom Backgrounds
        if (Option.startsWith("adm")){
            RemoveServerProperty(SageBackgroundsPropertyLocation + Option);
            SageBackgrounds.remove(Option);
            //need to find all MenuNodes using this background and reset them
            for (String MenuItem: ADMMenuNode.MenuNodeList().keySet()){
                if(ADMMenuNode.GetMenuItemBGImageFile(MenuItem).equals(Option)){
                    ADMMenuNode.SetMenuItemBGImageFile(MenuItem,ListNone);
                    System.out.println("ADM: uRemoveSageBackground: Active background removed from '" + MenuItem + "'");
                }
            }
            System.out.println("ADM: uRemoveSageBackground completed for '" + Option + "'");
        }
    }
    
    public static Boolean IsSageSubMenu(String SubMenu){
        return !ADMMenuNode.MenuNodeList().containsKey(SubMenu);
    }

    public static Boolean IsAdvancedMode(){
        if (GetPropertyAsBoolean(AdvancedModePropertyLocation, Boolean.FALSE)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static void ChangeAdvancedMode(){
        Boolean NewValue = !GetPropertyAsBoolean(AdvancedModePropertyLocation, Boolean.FALSE);
        SetProperty(AdvancedModePropertyLocation, NewValue.toString());
    }
    
    public static Boolean UseADM(){
        if (GetPropertyAsBoolean(UseADMPropertyLocation, Boolean.TRUE)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static void ChangeUseADM(){
        Boolean NewValue = !GetPropertyAsBoolean(UseADMPropertyLocation, Boolean.FALSE);
        SetProperty(UseADMPropertyLocation, NewValue.toString());
    }
    
    public static Boolean UseQLM(){
        if (GetPropertyAsBoolean(UseQLMPropertyLocation, Boolean.FALSE)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static void ChangeUseQLM(){
        Boolean NewValue = !GetPropertyAsBoolean(UseQLMPropertyLocation, Boolean.FALSE);
        SetProperty(UseQLMPropertyLocation, NewValue.toString());
    }
    
    public static Boolean ADMCopyMode(){
        if (GetPropertyAsBoolean(ADMCopyModePropertyLocation, Boolean.FALSE)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static void ChangeADMCopyMode(){
        Boolean NewValue = !GetPropertyAsBoolean(ADMCopyModePropertyLocation, Boolean.FALSE);
        SetProperty(ADMCopyModePropertyLocation, NewValue.toString());
    }
    
    public static String GetSubMenuListButtonText(String Option, Integer Level){
        return GetSubMenuListButtonText(Option, Level, Boolean.FALSE);
    }
    
    public static String GetSubMenuListButtonText(String Option, Integer Level, Boolean SkipAdvanced){
        //System.out.println("ADM: uGetSubMenuListButtonText: Option '" + Option + "' for Level = '" + Level + "'");
        if (Option==null || Option.equals(ListNone)){
            return ListNone;
        }
        String RetVal = "";
        if (Level==1){
            RetVal = SageSubMenusLevel1Props.getProperty(Option, ListNone);
        }else{
            RetVal = SageSubMenusLevel2Props.getProperty(Option, ListNone);
        }

        //determine if using Advanced options
        if (IsAdvancedMode() && !SkipAdvanced){
            return RetVal + " (" + Option + ")";
        }else{
            return RetVal;
        }
    }

    public static Collection<String> GetSubMenuList(Integer Level){
        if (Level==1){
            return SageSubMenusLevel1Keys;
        }else{
            return SageSubMenusLevel2Keys;
        }
    }
            
    public static String GetVersion() {
        return Version;
    }
    
    public static String GetOptionNotFound(){
        return ADMutil.OptionNotFound;
    }
    
    public static Float[] GetMenuInsets(){
        Float[] Insets = new Float[]{0f,0f,0f,0f};
        Float[] DiamondInsets = new Float[]{0f,0f,0.015f,0f};
        if (ADMDiamond.IsDiamond()){
            System.out.println("ADM: uGetMenuInsets: Diamond");
            return DiamondInsets;
        }else{
            System.out.println("ADM: uGetMenuInsets:");
            return Insets;
        }
    }

    public static String EvaluateAttribute(String Attribute){
        //System.out.println("ADM: uEvaluateAttribute: Attribute = '" + Attribute + "'");
        Object[] passvalue = new Object[1];
        passvalue[0] = sagex.api.WidgetAPI.EvaluateExpression(new UIContext(sagex.api.Global.GetUIContextName()), Attribute);
        if (passvalue[0]==null){
            System.out.println("ADM: uEvaluateAttribute for Attribute = '" + Attribute + "' not evaluated.");
            return OptionNotFound;
        }else{
            System.out.println("ADM: uEvaluateAttribute for Attribute = '" + Attribute + "' = '" + passvalue[0].toString() + "'");
            return passvalue[0].toString();
        }
        
    }

    //Save the current item that is focused for later retrieval
    public static void SetLastFocusForSubMenu(String SubMenu, String FocusItem){
        System.out.println("ADM: uSetLastFocusForSubMenu: SubMenu '" + SubMenu + "' to '" + FocusItem + "'");
        SetProperty(SageFocusPropertyLocation + SubMenu, FocusItem);
    }

    public static String GetLastFocusForSubMenuQLM(String SubMenu){
        return GetLastFocusForSubMenu(SubMenu, Boolean.TRUE);
    }
    
    public static String GetLastFocusForSubMenu(String SubMenu){
        return GetLastFocusForSubMenu(SubMenu, Boolean.FALSE);
    }
    
    public static String GetLastFocusForSubMenu(String SubMenu, Boolean QLMCheck){
        String LastFocus = GetProperty(SageFocusPropertyLocation + SubMenu,OptionNotFound);
        if (LastFocus.equals(OptionNotFound)){
            //return the DefaultMenuItem for this SubMenu
            System.out.println("ADM: uGetLastFocusForSubMenu: SubMenu '" + SubMenu + "' not found - returning DEFAULT");
            return ADMMenuNode.GetSubMenuDefault(SubMenu);
        }else{
            //check that the focus item stored in Sage is still valid
            if (ADMMenuNode.IsSubMenuItem(SubMenu, LastFocus, QLMCheck)){
                return LastFocus;
            }else{
                System.out.println("ADM: uGetLastFocusForSubMenu: SubMenu '" + SubMenu + "' not valid - returning DEFAULT");
                return ADMMenuNode.GetSubMenuDefault(SubMenu);
            }
        }
    }

    public static String repeat(String str, int times){
       StringBuilder ret = new StringBuilder();
       for(int i = 0;i < times;i++) ret.append(str);
       return ret.toString();
    }

    //use this to TEST Objects by seeing their member values
    public static void ListObjectMembers(Object obj){
        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true); // if you want to modify private fields
                System.out.println("ADM: uListObjectMembers: " + field.getName() + " - " + field.getType() + " - " + field.get(obj));
            } catch (IllegalArgumentException ex) {
                System.out.println("ADM: uListObjectMembers: ERROR: " + ADMutil.class.getName() + ex);
            } catch (IllegalAccessException ex) {
                System.out.println("ADM: uListObjectMembers: ERROR: " + ADMutil.class.getName() + ex);
            }
        }
    }

    public static String GetNewBackgroundKey(){
        Boolean UniqueName = Boolean.FALSE;
        String NewName = null;
        while (!UniqueName){
            NewName = GenerateRandomadmName();
            //check to see that the name is unique from other existing MenuItemNames
            UniqueName = !SageBackgrounds.contains(NewName);
        }
        return NewName;
    }

    public static String GenerateRandomadmName(){
        char[] buf = new char[10];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return "adm" + new String(buf);
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
            RemovePropertyAndChildren(Property);
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

    public static void RemoveServerPropertyAndChildren(String Property){
        sagex.api.Configuration.RemoveServerPropertyAndChildren(new UIContext(sagex.api.Global.GetUIContextName()),Property);
    }

    public static String ConvertListtoString(List<String> ListValue){
        String Value = "";
        if (ListValue.size()>0){
            Boolean tFirstItem = Boolean.TRUE;
            for (String ListItem : ListValue){
                if (tFirstItem){
                    Value = ListItem;
                    tFirstItem = Boolean.FALSE;
                }else{
                    Value = Value + ListToken + ListItem;
                }
            }
        }
        return Value;
    }

    public static List<String> ConvertStringtoList(String tValue){
        if (tValue.equals(OptionNotFound) || tValue.equals("") || tValue==null){
            return new LinkedList<String>();
        }else{
//            List<String> tList = new LinkedList<String>();
//            StringTokenizer st = new StringTokenizer(tValue, ListToken); 
//            while(st.hasMoreTokens()) { 
//                tList.add(st.nextToken());
//            } 
//            return tList;
            return Arrays.asList(tValue.split(ListToken));
        }
    }
    
    public static Integer GetMaxMenuItemsQLM(){
        Integer DefaultMax = 8;
        Integer LevelMax = GetPropertyAsInteger(SageADMSettingsPropertyLocation + "/MaxMenuItems/0", DefaultMax);
        return LevelMax;
    }

    public static Integer GetMaxMenuItems(Integer Level){
        Integer DefaultMax = 8;
        if (Level > 1){
            DefaultMax = 10;
        }
        if (Level==1){
            Integer Level1Max = GetPropertyAsInteger(SageADMSettingsPropertyLocation + "/MaxMenuItems/" + Level.toString(), DefaultMax);
            Integer TopMenuCount = ADMMenuNode.GetMenuItemCount(1);
            if (Level1Max > TopMenuCount){
                return TopMenuCount;
            }else{
                return Level1Max;
            }
        }else{
            return GetPropertyAsInteger(SageADMSettingsPropertyLocation + "/MaxMenuItems/" + Level.toString(), DefaultMax);
        }
    }

    public static Integer GetPropertyMaxMenuItems(Integer Level){
        Integer DefaultMax = 8;
        if (Level > 1){
            DefaultMax = 10;
        }
        return GetPropertyAsInteger(SageADMSettingsPropertyLocation + "/MaxMenuItems/" + Level.toString(), DefaultMax);
    }
    public static void SetPropertyMaxMenuItems(Integer Level, Integer Value){
        //ensure the value is within a reasonable range
        Integer dMin = 3;
        Integer dMax = 12;
        if (Value > dMax){
            Value = dMax;
        }else if (Value < dMin){
            Value = dMin;
        }
        SetProperty(SageADMSettingsPropertyLocation + "/MaxMenuItems/" + Level.toString(), Value.toString());
    }

    public static enum QLMCloseType{HOME_MM_LEFT_CLOSE,HOME_CLOSE_LEFT_MM,HOME_CLOSE_LEFT_CLOSE};
    public static final String SageADMSettingsQLMCloseState = SageADMSettingsPropertyLocation + "/qlm_close_state";

    public static String GetQLMCloseState(){
        //determine the current state and return a button text string to display
        String cState = GetProperty(SageADMSettingsQLMCloseState, QLMCloseType.HOME_CLOSE_LEFT_CLOSE.toString());
        if (cState.equals(QLMCloseType.HOME_MM_LEFT_CLOSE.toString())){
            return "Home: Main Menu - Left: Close QLM";
        }else if (cState.equals(QLMCloseType.HOME_CLOSE_LEFT_MM.toString())){
            return "Home: Close QLM - Left: Main Menu";
        }else{
            //default to HOME_CLOSE_LEFT_CLOSE
            return "Home: Close QLM - Left: Close QLM";
        }
    }
    
    public static void SetQLMCloseState(Integer Delta){
        //determine the current state and change to the next/previous state
        String cState = GetProperty(SageADMSettingsQLMCloseState, QLMCloseType.HOME_CLOSE_LEFT_CLOSE.toString());
        QLMCloseType nextState = QLMCloseType.HOME_CLOSE_LEFT_CLOSE;
        Integer i = 0;
        for (QLMCloseType value : QLMCloseType.values()){
            if (value.toString().equals(cState)){
                break;
            }
            i++;
        }
        //i now has the current state
        i = i + Delta;
        //i now has the next state based on Delta
        if (i>=QLMCloseType.values().length){
            nextState = QLMCloseType.values()[0];
        }else if(i<0){
            nextState = QLMCloseType.values()[QLMCloseType.values().length-1];
        }else{
            nextState = QLMCloseType.values()[i];
        }
        //save the nextState
        SetProperty(SageADMSettingsQLMCloseState, nextState.toString());
    }

    public static void ExecuteQLMCloseStateLeft(){
        //determine the current state and execute based on that state
        String cState = GetProperty(SageADMSettingsQLMCloseState, QLMCloseType.HOME_CLOSE_LEFT_CLOSE.toString());
        if (cState.equals(QLMCloseType.HOME_MM_LEFT_CLOSE.toString())){
            CloseOptionsMenu();
        }else if (cState.equals(QLMCloseType.HOME_CLOSE_LEFT_MM.toString())){
            CloseOptionsMenu();
            ADMAction.ExecuteWidget(MainMenuWidgetSymbol);
        }else{
            //default to HOME_CLOSE_LEFT_CLOSE
            CloseOptionsMenu();
        }
    }

    public static void ExecuteQLMCloseStateHome(){
        //determine the current state and execute based on that state
        String cState = GetProperty(SageADMSettingsQLMCloseState, QLMCloseType.HOME_CLOSE_LEFT_CLOSE.toString());
        if (cState.equals(QLMCloseType.HOME_MM_LEFT_CLOSE.toString())){
            CloseOptionsMenu();
            ADMAction.ExecuteWidget(MainMenuWidgetSymbol);
        }else if (cState.equals(QLMCloseType.HOME_CLOSE_LEFT_MM.toString())){
            CloseOptionsMenu();
        }else{
            //default to HOME_CLOSE_LEFT_CLOSE
            CloseOptionsMenu();
        }
    }

    private static void CloseOptionsMenu(){
        sagex.api.Global.CloseOptionsMenu(new UIContext(sagex.api.Global.GetUIContextName()));
    }

    public static String ADMLocation(){
        //return sagex.api.Utility.GetWorkingDirectory(new UIContext(sagex.api.Global.GetUIContextName())) + File.separator + "userdata" + File.separator + "ADM";
        return getSageTVRootDir() + File.separator + "userdata" + File.separator + "ADM";
    }
    public static String ADMDefaultsLocation(){
        //return  sagex.api.Utility.GetWorkingDirectory(new UIContext(sagex.api.Global.GetUIContextName())) + File.separator + "STVs" + File.separator + "ADM" + File.separator + "defaults";
        return  getSageTVRootDir() + File.separator + "STVs" + File.separator + "ADM" + File.separator + "defaults";
    }
    public static String GetADMLocation() {
        return ADMLocation();
    }
    public static String GetADMDefaultsLocation(){
        return ADMDefaultsLocation();
    }

    public static String getSageTVRootDir() {
        return new File(System.getProperty(SageADMBasePropertyLocation + "/sagetvHomeDir", ".")).toString();
    }    
   
//    public static String[] GetSubpropertiesThatAreBranchesUI(String property)
//	throws InvocationTargetException 
//	{
//            return (String[])ApiUI(sagex.api.Global.GetUIContextName(),"GetSubpropertiesThatAreBranches", new Object[]{property});
//	}
//    public static String GetPropertyUI(String property, String defval)
//	throws InvocationTargetException 
//	{
//		return StringApi("GetProperty", new Object[]{property,defval});
//	}
//    public static String StringApi(String function, Object[] args)
//	throws InvocationTargetException, ClassCastException
//	{
//		return (String)ApiUI(sagex.api.Global.GetUIContextName(),function,args);
//	}
//    public static Object ApiUI(String context, String function, Object[] args)
//    throws InvocationTargetException {
//        try { 
//            try {
//                return sage.SageTV.apiUI(context,function,args);
//            } catch ( NoSuchMethodError e ) {
//                if ( context.equals("SAGETV_PROCESS_LOCAL_UI"))
//                    // fallback to 2.2 API
//                    return sage.SageTV.api(function,args);
//                return null;
//            }
//        } catch (InvocationTargetException e) {
//            if ( args != null)
//                throw new InvocationTargetException(e,"Exception while executing SageApi: \""+function+"\" numargs="+Integer.toString(args.length));
//            else
//                throw new InvocationTargetException(e,"Exception while executing SageApi: \""+function+"\" numargs=0");
//        }
//    }    

//    public static void TestConfig(){
//        Configuration config = new PropertiesConfiguration("usergui.properties");
//        //http://commons.apache.org/configuration/
//        
//
//    }
}
