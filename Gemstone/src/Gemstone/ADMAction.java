/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import sagex.UIContext;

/**
 *
 * @author jusjoken
 */
public class ADMAction {

    static private final Logger LOG = Logger.getLogger(ADMAction.class);
    private static final String Blank = "admActionBlank";
    private static final String VarNull = "VarNull";
    private static final String SageADMCustomActionsPropertyLocation = "ADM/custom_actions";
    private static final String ActionCategoryFilterPropertyLocation = "ADM/action_category/filter";
    private static final String ActionCategoryFilterStickyPropertyLocation = "ADM/action_category/sticky";
    private static final String UseAttributeValue = "UseAttributeValue";
    private static final String UseAttributeObjectValue = "UseAttributeObjectValue";
    private static final String StandardActionListFile = "ADMStandardActions.properties";
    public static Map<String,CustomAction> SageMenuActions = new LinkedHashMap<String,CustomAction>();
    public static Map<String,String>  SageTVRecordingViews = new LinkedHashMap<String,String>();
    public static Map<String,String> DynamicLists = new LinkedHashMap<String,String>();
    public static final String SageTVRecordingViewsTitlePropertyLocation = "sagetv_recordings/view_title/";
    private String Type = "";
    private String ButtonText = "";
    private String Attribute = Blank;
    private String WidgetSymbol = Blank;
    private String FieldTitle = "";
    private static final ActionVariable BlankActionVariable = new ActionVariable(Blank, Blank, Blank);
    private ActionVariable EvalExpression = BlankActionVariable;
    private List<ActionVariable> ActionVariables = new LinkedList<ActionVariable>();
    private List<String> ActionCategories = new LinkedList<String>();
    private static final String VarTypeGlobal = "VarTypeGlobal";
    private static final String VarTypeStatic = "VarTypeStatic";
    private static final String VarTypeSetProp = "VarTypeSetProp";
    private Boolean AdvancedOnly = Boolean.FALSE;
    private Boolean InternalOnly = Boolean.FALSE;
    private static Map<String,ADMAction> ActionList = new LinkedHashMap<String,ADMAction>();
    public static final String WidgetbySymbol = "ExecuteWidget";
    public static final String BrowseVideoFolder = "ExecuteBrowseVideoFolder";
    public static final String StandardMenuAction = "ExecuteStandardMenuAction";
    public static final String TVRecordingView = "ExecuteTVRecordingView";
    public static final String GemstoneFlow = "ExecuteGemstoneFlow";
    public static final String BrowseFileFolderLocal = "ExecuteBrowseFileFolderLocal";
    public static final String BrowseFileFolderServer = "ExecuteBrowseFileFolderServer";
    public static final String BrowseFileFolderImports = "ExecuteBrowseFileFolderImports";
    public static final String BrowseFileFolderRecDir = "ExecuteBrowseFileFolderRecDir";
    public static final String BrowseFileFolderNetwork = "ExecuteBrowseFileFolderNetwork";
    public static final String LaunchExternalApplication = "LaunchExternalApplication";
    public static final String LaunchPlayList = "LaunchPlayList";
    public static final String DynamicList = "AddDynamicList";
    public static final String ActionTypeDefault = "DoNothing";
    public static final String DynamicTVRecordingsList = "admDynamicTVRecordingsList";
    public static final String DynamicVideoPlaylist = "admDynamicVideoPlaylist";
    public static final String DynamicMusicPlaylist = "admDynamicMusicPlaylist";
    public static final String DynamicGemstoneFlows = "admDynamicGemstoneFlows";
    public static final String ActionCategoryShowAll = "admActionCategoryShowAll";
    public static final String ActionCategoryOther = "Other (no category)";
    

    public ADMAction(String Type, Boolean AdvancedOnly, String ButtonText){
        this(Type,AdvancedOnly,ButtonText,"Action",Blank);
    }

    public ADMAction(String Type, Boolean AdvancedOnly, String ButtonText, String FieldTitle){
        this(Type,AdvancedOnly,ButtonText,FieldTitle,Blank);
    }

    public ADMAction(String Type, Boolean AdvancedOnly, String ButtonText, String FieldTitle, String WidgetSymbol){
        this.Type = Type;
        this.AdvancedOnly = AdvancedOnly;
        this.ButtonText = ButtonText;
        this.FieldTitle = FieldTitle;
        this.WidgetSymbol = WidgetSymbol;
    }
    
    public static void Init(){
        //Clear existing Actions if any
        ActionList.clear();
        //Create the Actions for ADM to use
        ActionList.put(ActionTypeDefault, new ADMAction(ActionTypeDefault,Boolean.FALSE,"None"));

        ActionList.put(WidgetbySymbol, new ADMAction(WidgetbySymbol,Boolean.TRUE,"Execute Widget by Symbol", "Action"));

        ActionList.put(BrowseVideoFolder, new ADMAction(BrowseVideoFolder,Boolean.FALSE,"Video Browser with specific Folder","Video Browser Folder","OPUS4A-174637"));
        ActionList.get(BrowseVideoFolder).ActionVariables.add(new ActionVariable(VarTypeGlobal,"gCurrentVideoBrowserFolder", UseAttributeValue));
        ActionList.get(BrowseVideoFolder).ActionCategories.add("Video");


        ActionList.put(StandardMenuAction, new ADMAction(StandardMenuAction,Boolean.FALSE,"Execute Standard Sage Menu Action", "Standard Action"));

        ActionList.put(TVRecordingView, new ADMAction(TVRecordingView,Boolean.FALSE,"Launch Specific TV Recordings View", "TV Recordings View","OPUS4A-174116"));
        ActionList.get(TVRecordingView).ActionVariables.add(new ActionVariable(VarTypeGlobal,"ViewFilter", UseAttributeValue));
        ActionList.get(TVRecordingView).ActionCategories.add("TV");

        ActionList.put(DynamicList, new ADMAction(DynamicList,Boolean.FALSE,"Dynamic List Item", "Dynamic List Type"));

        ActionList.put(DynamicTVRecordingsList, new ADMAction(DynamicTVRecordingsList,Boolean.FALSE,"DynamicTVRecordingsList", "DynamicTVRecordingsList"));
        ActionList.get(DynamicTVRecordingsList).InternalOnly = Boolean.TRUE;

        ActionList.put(DynamicVideoPlaylist, new ADMAction(DynamicVideoPlaylist,Boolean.FALSE,"DynamicVideoPlaylist", "DynamicVideoPlaylist"));
        ActionList.get(DynamicVideoPlaylist).InternalOnly = Boolean.TRUE;

        ActionList.put(DynamicMusicPlaylist, new ADMAction(DynamicMusicPlaylist,Boolean.FALSE,"DynamicMusicPlaylist", "DynamicMusicPlaylist"));
        ActionList.get(DynamicMusicPlaylist).InternalOnly = Boolean.TRUE;

        ActionList.put(DynamicGemstoneFlows, new ADMAction(DynamicGemstoneFlows,Boolean.FALSE,"DynamicGemstoneFlows", "DynamicGemstoneFlows"));
        ActionList.get(DynamicGemstoneFlows).InternalOnly = Boolean.TRUE;
        
        ActionList.put(LaunchPlayList, new ADMAction(LaunchPlayList,Boolean.FALSE,"LaunchPlayList", "LaunchPlayList","OPUS4A-183733"));
        ActionList.get(LaunchPlayList).InternalOnly = Boolean.TRUE;
        ActionList.get(LaunchPlayList).ActionVariables.add(new ActionVariable(VarTypeGlobal,"PlaylistItem", UseAttributeObjectValue));
        ActionList.get(LaunchPlayList).ActionVariables.add(new ActionVariable(VarTypeGlobal,"BasePlaylistUnit", UseAttributeValue));

        ActionList.put(GemstoneFlow, new ADMAction(GemstoneFlow,Boolean.FALSE,"Gemstone Flow", "Gemstone Flow","AOSCS-679216"));
        ActionList.get(GemstoneFlow).ActionVariables.add(new ActionVariable(VarTypeGlobal,"ViewCell", UseAttributeValue));
        ActionList.get(GemstoneFlow).ActionCategories.add("Video");
        ActionList.get(GemstoneFlow).ActionCategories.add("TV");
        ActionList.get(GemstoneFlow).ActionCategories.add("Gemstone");

        ActionList.put(BrowseFileFolderLocal, new ADMAction(BrowseFileFolderLocal,Boolean.FALSE,"File Browser: Local","Local File Path","BASE-51703"));
        ActionList.get(BrowseFileFolderLocal).ActionVariables.add(new ActionVariable(VarTypeGlobal,"ForceReload", "true"));
        ActionList.get(BrowseFileFolderLocal).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_style", "xLocal"));
        ActionList.get(BrowseFileFolderLocal).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_folder/local", UseAttributeValue));
        ActionList.get(BrowseFileFolderLocal).ActionCategories.add("File Systems");

        ActionList.put(BrowseFileFolderServer, new ADMAction(BrowseFileFolderServer,Boolean.FALSE,"File Browser: Server","Server File Path","BASE-51703"));
        ActionList.get(BrowseFileFolderServer).ActionVariables.add(new ActionVariable(VarTypeGlobal,"ForceReload", "true"));
        ActionList.get(BrowseFileFolderServer).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_style", "xServer"));
        ActionList.get(BrowseFileFolderServer).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_folder/server", UseAttributeValue));
        ActionList.get(BrowseFileFolderServer).ActionCategories.add("File Systems");

        ActionList.put(BrowseFileFolderImports, new ADMAction(BrowseFileFolderImports,Boolean.FALSE,"File Browser: Imports","Imports File Path","BASE-51703"));
        ActionList.get(BrowseFileFolderImports).ActionVariables.add(new ActionVariable(VarTypeGlobal,"ForceReload", "true"));
        ActionList.get(BrowseFileFolderImports).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_style", "xImports"));
        ActionList.get(BrowseFileFolderImports).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_folder/imports", UseAttributeValue));
        ActionList.get(BrowseFileFolderImports).ActionCategories.add("File Systems");

        ActionList.put(BrowseFileFolderRecDir, new ADMAction(BrowseFileFolderRecDir,Boolean.FALSE,"File Browser: Recordings","Recording File Path","BASE-51703"));
        ActionList.get(BrowseFileFolderRecDir).ActionVariables.add(new ActionVariable(VarTypeGlobal,"ForceReload", "true"));
        ActionList.get(BrowseFileFolderRecDir).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_style", "xRecDirs"));
        ActionList.get(BrowseFileFolderRecDir).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_folder/rec_dirs", UseAttributeValue));
        ActionList.get(BrowseFileFolderRecDir).ActionCategories.add("File Systems");

        ActionList.put(BrowseFileFolderNetwork, new ADMAction(BrowseFileFolderNetwork,Boolean.FALSE,"File Browser: Network","Network File Path","BASE-51703"));
        ActionList.get(BrowseFileFolderNetwork).ActionVariables.add(new ActionVariable(VarTypeGlobal,"ForceReload", "true"));
        ActionList.get(BrowseFileFolderNetwork).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_style", "xNetwork"));
        ActionList.get(BrowseFileFolderNetwork).ActionVariables.add(new ActionVariable(VarTypeSetProp,"file_browser/last_folder/network", UseAttributeValue));
        ActionList.get(BrowseFileFolderNetwork).ActionCategories.add("File Systems");

        ActionList.put(LaunchExternalApplication, new ADMAction(LaunchExternalApplication,Boolean.FALSE,"Launch External Application", "Application Settings"));

        //also load the actions lists - only needs loaded at startup
        //clear the lists
        SageMenuActions.clear();
        CustomAction.ActionListSorted.clear();
        CustomAction.CopyModeUniqueIDs.clear();
        CustomAction.WidgetSymbols.clear();
        CustomAction.AllActionCategories.clear();
        CustomAction.AllActionCategories.add(ActionCategoryOther);
        CustomAction.AllActionCategories.add("Gemstone");

        LoadStandardActionList();
        LoadDynamicLists();

        LoadSageTVRecordingViews();
    }
    
    public static String GetWidgetbySymbol(){ return WidgetbySymbol; }
    public static String GetBrowseVideoFolder(){ return BrowseVideoFolder; }
    public static String GetStandardMenuAction(){ return StandardMenuAction; }
    public static String GetTVRecordingView(){ return TVRecordingView; }
    public static String GetGemstoneFlow(){ return GemstoneFlow; }
    public static String GetBrowseFileFolderLocal(){ return BrowseFileFolderLocal; }
    public static String GetBrowseFileFolderServer(){ return BrowseFileFolderServer; }
    public static String GetBrowseFileFolderImports(){ return BrowseFileFolderImports; }
    public static String GetBrowseFileFolderRecDir(){ return BrowseFileFolderRecDir; }
    public static String GetBrowseFileFolderNetwork(){ return BrowseFileFolderNetwork; }
    public static String GetLaunchExternalApplication(){ return LaunchExternalApplication; }
    public static String GetLaunchPlayList(){ return LaunchPlayList; }
    public static String GetDynamicList(){ return DynamicList; }
    public static String GetActionTypeDefault(){ return ActionTypeDefault; }
    
        
    public static String GetButtonText(String Type){
        return ActionList.get(Type).ButtonText;
    }
    
    public static String GetFieldTitle(String Type){
        return ActionList.get(Type).FieldTitle;
    }
    
    public static String GetWidgetSymbol(String Type){
        return ActionList.get(Type).WidgetSymbol;
    }
    
    public static Boolean GetAdvancedOnly(String Type){
        return ActionList.get(Type).AdvancedOnly;
    }
    
    public static Boolean GetInternalOnly(String Type){
        return ActionList.get(Type).InternalOnly;
    }
    
    public static List<ActionVariable> GetActionVariables(String Type){
        return ActionList.get(Type).ActionVariables;
    }
    
    public static Collection<String> GetTypes(){
        Collection<String> tempList = new LinkedHashSet<String>();
        for (String Item : ActionList.keySet()){
            if (GetAdvancedOnly(Item)){
                if (ADMutil.IsAdvancedMode()){
                    tempList.add(Item);
                }
            }else if (GetInternalOnly(Item)){
                //do not add these types of items as they should not show on any lists
            }else{
                tempList.add(Item);
            }
        }
        return tempList;
    }

    public static Boolean IsValidAction(String Type){
        if (Type.equals(ActionTypeDefault)){
            LOG.debug("IsValidAction - FALSE for = '" + Type + "'");
            return Boolean.FALSE;
        }else{
            LOG.debug("IsValidAction - Lookup for = '" + Type + "' = '" + ActionList.containsKey(Type) + "'");
            return ActionList.containsKey(Type);
        }
    }
    
    public static String GetAllActionsAttributeButtonText(String Type, String Attribute){
        return GetAllActionsAttributeButtonText(GetAllActionsKey(Type, Attribute), Boolean.FALSE);
    }
    public static String GetAllActionsAttributeButtonText(String AAType){
        return GetAllActionsAttributeButtonText(AAType, Boolean.FALSE);
    }
    //splits the All Actions Type (Key) using the ListToken to get the Button Text
    public static String GetAllActionsAttributeButtonText(String AAType, Boolean IgnoreAdvanced){
        //the AAType is made up of the ActionType + ListToken + Attribute (Key)
        String tAttribute = GetAllActionsAttribute(AAType);
        String tType = GetAllActionsType(AAType);
        //LOG.debug("GetAllActionsAttributeButtonText - AAType '" + AAType + "' Type/Attribute '" + tType + "' - '" + tAttribute + "'");
        if (tAttribute.equals(ADMutil.OptionNotFound)){
            if(tType.equals(ADMutil.OptionNotFound)){
                //bad conversion
                return ADMutil.OptionNotFound;
            }else{
                return GetButtonText(tType);
            }
        }else{
            if(tType.equals(TVRecordingView)){
                return GetSageTVRecordingViewsActionButtonText(tAttribute);
            }else if(tType.equals(GemstoneFlow)){
                return GetButtonText(tType) + " - " + GetAttributeButtonText(tType, tAttribute, IgnoreAdvanced);
            }else{
                return GetAttributeButtonText(tType, tAttribute, IgnoreAdvanced);
            }
        }
    }
    
    public static String GetAttributeButtonText(String Type, String Attribute){
        return GetAttributeButtonText(Type, Attribute, Boolean.FALSE);
    }
    
    public static String GetAttributeButtonText(String Type, String Attribute, Boolean IgnoreAdvanced){
        if (Type.equals(StandardMenuAction)){
            //determine if using Advanced options
            if (ADMutil.IsAdvancedMode() && !IgnoreAdvanced){
                return SageMenuActions.get(Attribute).ButtonText + " \n  (" + Attribute + ")";
            }else{
                return SageMenuActions.get(Attribute).ButtonText;
            }
        }else if(Type.equals(WidgetbySymbol)){
            return Attribute + " - " + GetWidgetName(Attribute);
        }else if(Type.equals(BrowseVideoFolder)){
            if (Attribute==null){
                return "Root";
            }else{
                return Attribute;
            }
        }else if(Type.equals(TVRecordingView)){
            return GetSageTVRecordingViewsButtonText(Attribute);
        }else if(Type.equals(DynamicList)){
            return DynamicLists.get(Attribute);
        }else if(Type.equals(LaunchPlayList)){
            //should not be used as this is an internal only item and should not be displayed
            return "Invalid use of this Internal PlayList item";
        }else if(Type.equals(GemstoneFlow)){
            return Flow.GetFlowName(Attribute);
        }else if(Type.equals(LaunchExternalApplication)){
            if (Attribute.isEmpty()){
                return "Configure";
            }else{
                return "Configure (" + Attribute + ")";
            }
        }else if(IsFileBrowserType(Type)){
            if (Attribute==null){
                return "Choose";
            }else{
                return Attribute;
            }
        }else{
            return ADMutil.OptionNotFound;
        }
    }
    
    //execute the Action based on the Menu Item ActionType value
    public static void Execute(String MenuItemName){
        String tActionType = ADMMenuNode.GetMenuItemActionType(MenuItemName);
        String tActionAttribute = ADMMenuNode.GetMenuItemAction(MenuItemName);
        LOG.debug("Execute - ActionType = '" + tActionType + "' Action = '" + tActionAttribute + "'");
        if (!tActionType.equals(ActionTypeDefault)){
            if (tActionType.equals(StandardMenuAction)){
                SageMenuActions.get(tActionAttribute).Execute(tActionAttribute);
            }else{
                //see if there are any ActionVariables that need to be evaluated
                for (ActionVariable tActionVar : GetActionVariables(tActionType)){
                    if (tActionVar.Val.equals(UseAttributeObjectValue)){
                        tActionVar.EvaluateVariable(ADMMenuNode.GetMenuItemActionObject(MenuItemName));
                    }else{
                        tActionVar.EvaluateVariable(tActionAttribute);
                    }
                }
                //determine what to execute
                if (tActionType.equals(LaunchExternalApplication)){
                    //launch external application
                    ExternalAction tExtApp = ADMMenuNode.GetMenuItemActionExternal(MenuItemName);
                    tExtApp.Execute();
                }else{
                    //either execute the default widget symbol or the one for the Menu Item passed in
                    if (GetWidgetSymbol(tActionType).equals(Blank)){
                        ExecuteWidget(tActionAttribute);
                    }else{
                        ExecuteWidget(GetWidgetSymbol(tActionType));
                    }
                }
            }
        }
        //else do nothing
    }
    
    public static Boolean ExecuteWidget(String WidgetSymbol){
        Object[] passvalue = new Object[1];
        passvalue[0] = sagex.api.WidgetAPI.FindWidgetBySymbol(new UIContext(sagex.api.Global.GetUIContextName()), WidgetSymbol);
        if (passvalue[0]==null){
            LOG.debug("ExecuteWidget - FindWidgetSymbol failed for WidgetSymbol = '" + WidgetSymbol + "'");
            return Boolean.FALSE;
        }else{
            LOG.debug("ExecuteWidget - ExecuteWidgetChain called with WidgetSymbol = '" + WidgetSymbol + "'");

            try {
                sage.SageTV.apiUI(new UIContext(sagex.api.Global.GetUIContextName()).toString(), "ExecuteWidgetChainInCurrentMenuContext", passvalue);
            } catch (InvocationTargetException ex) {
                LOG.debug("ExecuteWidget: error executing widget" + ADMutil.class.getName() + ex);
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
            //            sagex.api.WidgetAPI.ExecuteWidgetChain(MyUIContext, WidgetSymbol);
            
        }
               
    }

    public static Boolean IsWidgetValid(String WidgetSymbol){
        Object[] passvalue = new Object[1];
        passvalue[0] = sagex.api.WidgetAPI.FindWidgetBySymbol(new UIContext(sagex.api.Global.GetUIContextName()), WidgetSymbol);
        if (passvalue[0]==null){
            LOG.debug("IsWidgetValid - FindWidgetSymbol failed for WidgetSymbol = '" + WidgetSymbol + "'");
            return Boolean.FALSE;
        }else{
            LOG.debug("IsWidgetValid - FindWidgetSymbol passed for WidgetSymbol = '" + WidgetSymbol + "'");
            return Boolean.TRUE;
        }
               
    }
    
    public static String GetWidgetName(String WidgetSymbol){
        Object[] passvalue = new Object[1];
        passvalue[0] = sagex.api.WidgetAPI.FindWidgetBySymbol(new UIContext(sagex.api.Global.GetUIContextName()), WidgetSymbol);
        if (passvalue[0]==null){
            LOG.debug("GetWidgetName - FindWidgetSymbol failed for WidgetSymbol = '" + WidgetSymbol + "'");
            return ADMutil.OptionNotFound;
        }else{
            String WidgetName = sagex.api.WidgetAPI.GetWidgetName(new UIContext(sagex.api.Global.GetUIContextName()), WidgetSymbol);
            LOG.debug("GetWidgetName for Symbol = '" + WidgetSymbol + "' = '" + WidgetName + "'");
            return WidgetName;
        }
               
    }
    
    public static void LoadDynamicLists(){
        //Dynamic Lists are single menu items that expand themselves into a list of items of a specified type
        DynamicLists.clear();
        DynamicLists.put(DynamicTVRecordingsList, "TV Recordings List");
        ActionList.get(DynamicTVRecordingsList).ActionCategories.add("TV");
        
        DynamicLists.put(DynamicVideoPlaylist, "Video Playlist");
        ActionList.get(DynamicVideoPlaylist).ActionCategories.add("Video");

        DynamicLists.put(DynamicMusicPlaylist, "Music Playlist");
        ActionList.get(DynamicMusicPlaylist).ActionCategories.add("Music");

        DynamicLists.put(DynamicGemstoneFlows, "Gemstone Flows");
        ActionList.get(DynamicGemstoneFlows).ActionCategories.add("Gemstone");
        ActionList.get(DynamicGemstoneFlows).ActionCategories.add("Video");
        ActionList.get(DynamicGemstoneFlows).ActionCategories.add("TV");
    }
    
    public static Collection<String> GetDynamicListItems(String dParent, String Attribute){
        Collection<String> TempMenuItems = new LinkedHashSet<String>();
        Integer Counter = 0;
        String FirstNameforDefault = Blank;
        String ItemName = Blank;
        if (Attribute.equals(DynamicTVRecordingsList)){
            Integer ViewCount = ADMutil.GetPropertyAsInteger("sagetv_recordings/" + "view_count", 4);
            for (String ItemKey : SageTVRecordingViews.keySet()){
                //create a temp menu item for each item
                //Use a consistent name made up of the Parent + the Counter
                ItemName = dParent + Counter.toString();
                ADMMenuNode.CreateTempMenuItem(ItemName, dParent, TVRecordingView, ItemKey, GetSageTVRecordingViewsButtonText(ItemKey), Counter);
                TempMenuItems.add(ItemName);
                if (FirstNameforDefault.equals(Blank)){
                    FirstNameforDefault = ItemName;
                }
                Counter++;
                if (Counter>=ViewCount){
                    break;
                }
            }
            if (FirstNameforDefault.equals(Blank)){
                ADMMenuNode.ValidateSubMenuDefault(dParent);
            }else{
                ADMMenuNode.SetMenuItemIsDefault(FirstNameforDefault, Boolean.TRUE);
            }
            LOG.debug("GetDynamicListItems: Parent '" + dParent + "' Attribute '" + Attribute + "' Items '" + TempMenuItems + "'");
            return TempMenuItems;
        }else if(Attribute.equals(DynamicVideoPlaylist)){
            TempMenuItems = GetPlayList(Boolean.TRUE, dParent);
            LOG.debug("GetDynamicListItems: Parent '" + dParent + "' Attribute '" + Attribute + "' Items '" + TempMenuItems + "'");
            return TempMenuItems;
        }else if(Attribute.equals(DynamicMusicPlaylist)){
            TempMenuItems = GetPlayList(Boolean.FALSE, dParent);
            LOG.debug("GetDynamicListItems: Parent '" + dParent + "' Attribute '" + Attribute + "' Items '" + TempMenuItems + "'");
            return TempMenuItems;
        }else if(Attribute.equals(DynamicGemstoneFlows)){
            Counter = 0;
            for (String vFlow: Flow.GetFlows()){
                ItemName = dParent + Counter.toString();
                ADMMenuNode.CreateTempMenuItem(ItemName, dParent, GemstoneFlow, vFlow, GetAttributeButtonText(GemstoneFlow, vFlow, Boolean.TRUE), Counter);
                TempMenuItems.add(ItemName);
                Counter++;
            }
            return TempMenuItems;
        }else{
            LOG.debug("GetDynamicListItems: Parent '" + dParent + "' Attribute '" + Attribute + "' Items '" + TempMenuItems + "'");
            return TempMenuItems;
        }
    }

    private static Collection<String> GetPlayList(Boolean IsVideo, String dParent){
        //based on IsVideo this will create MenuItems for 
        // true = Vidoes
        // false = Music
        String ItemName = Blank;
        Collection<String> TempMenuItems = new LinkedHashSet<String>();
        String PlayListItemType = "";
        if (IsVideo){
            PlayListItemType = "xVideo";
        }else{
            PlayListItemType = "xSong";
        }
        Object[] AllPlayLists = sagex.api.PlaylistAPI.GetPlaylists();
        //Create a menu item for each of the playlists
        Integer Counter = 0;
        for (Object Playlist : AllPlayLists){
            if (sagex.api.PlaylistAPI.DoesPlaylistHaveVideo(Playlist)==IsVideo){
                //skip specific Playlists
                if (sagex.api.PlaylistAPI.GetName(Playlist).equals("DVD BURN PLAYLIST") || sagex.api.PlaylistAPI.GetName(Playlist).equals("Now Playing")){
                    //skip these playlists
                }else{
                    //now creage a Menu Item for this Playlist
                    ItemName = dParent + Counter.toString();
                    ADMMenuNode.CreateTempMenuItem(ItemName, dParent, LaunchPlayList, PlayListItemType, GetPlayListButtonText(Playlist), Counter);
                    ADMMenuNode.SetMenuItemActionObject(ItemName, Playlist);
                    TempMenuItems.add(ItemName);
                    Counter++;
                }
            }
        }
        
        return TempMenuItems;
    }
    
    private static String GetPlayListButtonText(Object Playlist){
        String PLName = sagex.api.PlaylistAPI.GetName(new UIContext(sagex.api.Global.GetUIContextName()),Playlist);
        //Get name after last /, if the string has any.
        Integer SlashPos = PLName.lastIndexOf("/");
        if (SlashPos!=-1){
            PLName = ".." + PLName.substring( SlashPos+1, -1 );
        }
        return PLName;
    }
    
    //returns a sorted list of ALL the actions
    private static Integer AllActionsListCount = 0;
    public static Collection<String> GetAllActionsList(){
        SortedMap<String,String> AllActionsSorted = new TreeMap<String,String>();
        for (String aType : GetTypes()){
            //do not add Do Nothing to the list
            if (!aType.equals(ActionTypeDefault)){
                if (HasActionList(aType)){
                    for (String aAttribute : GetActionList(aType)){
                        AllActionsListAdd(AllActionsSorted, GetAllActionsAttributeButtonText(aType,aAttribute), aType, aAttribute);
                    }
                }else{
                    AllActionsListAdd(AllActionsSorted, GetButtonText(aType), aType, Blank);
                }
            }
        }
        //LOG.debug("GetAllActionsList: complete List '" + AllActionsSorted.keySet() + "' Values '" + AllActionsSorted.values() + "'");
        AllActionsListCount = AllActionsSorted.size();
        return AllActionsSorted.values();
    }

    private static void AllActionsListAdd(SortedMap<String,String> AllActionsSorted, String bButtonText, String bType, String bAttribute){
        //filter the list based on the selected Category Filter
        if (GetActionCategoryFilter().equals(ActionCategoryShowAll)){
            //LOG.debug("AllActionsListAdd: No Filter - adding '" + bButtonText + "' Type/Attribute '" + bType + "' - '" + bAttribute + "'");
            AllActionsSorted.put(bButtonText, GetAllActionsKey(bType, bAttribute));
        }else{
            String tFilter = GetActionCategoryFilter();
            //determine if this is a CustomAction
            if (SageMenuActions.containsKey(bAttribute)){
                if (tFilter.equals(ActionCategoryOther)){
                    if (SageMenuActions.get(bAttribute).ActionCategories.isEmpty()){
                        //LOG.debug("AllActionsListAdd: Filter '" + tFilter + "' CustomAction for '" + bAttribute + "' Adding as No Categories and Other");
                        AllActionsSorted.put(bButtonText, GetAllActionsKey(bType, bAttribute));
                    }
                }else{
                    if (SageMenuActions.get(bAttribute).HasCategory(tFilter)){
                        //LOG.debug("AllActionsListAdd: Filter '" + tFilter + "' CustomAction for '" + bType + "' - '" + bAttribute + "' Adding");
                        AllActionsSorted.put(bButtonText, GetAllActionsKey(bType, bAttribute));
                    }
                }
            }else{
                //Other Action
                if (ActionList.containsKey(bType)){
                    String CheckType = bType;
                    if (bType.equals(DynamicList)){
                        //LOG.debug("AllActionsListAdd: Dynamic List item - Attribute '" + bAttribute + "'");
                        CheckType = bAttribute;
                    }
                    //LOG.debug("AllActionsListAdd: Filter '" + tFilter + "' checking Other Action for '" + bType + "' Attribute '" + bAttribute + "'");
                    if (tFilter.equals(ActionCategoryOther)){
                        if (ActionList.get(CheckType).ActionCategories.isEmpty()){
                            //LOG.debug("AllActionsListAdd: Filter '" + tFilter + "' Other Action for '" + bType + "' Adding as No Categories and Other");
                            AllActionsSorted.put(bButtonText, GetAllActionsKey(bType, bAttribute));
                        }
                    }else{
                        if (ActionList.get(CheckType).ActionCategories.contains(tFilter)){
                            //LOG.debug("AllActionsListAdd: Filter '" + tFilter + "' Found Match for Type '" + bType + "' Adding");
                            AllActionsSorted.put(bButtonText, GetAllActionsKey(bType, bAttribute));
                        }
                    }
                }else{
                    //do an Add so you don't miss anything that didn't match either of the above checks - should be nothing
                    LOG.debug("AllActionsListAdd: Filter '" + tFilter + "' NO MATCH SO ADDING ANYWAY '" + bType + "' ButtonText '" + bButtonText + "'");
                    AllActionsSorted.put(bButtonText, GetAllActionsKey(bType, bAttribute));
                }
            }
        }
    }

    public static Integer GetAllActionsListCount(){
        return AllActionsListCount;
    }
    
    public static String GetAllActionsInitialFocus(String Name){
        Collection<String> AllActionsList = new LinkedList<String>();
        AllActionsList = GetAllActionsList();
        //if the current Action is in the list return it as focus otherwise return the first item
        String tKey = GetAllActionsKey(ADMMenuNode.GetMenuItemActionType(Name), ADMMenuNode.GetMenuItemAction(Name));
        if (AllActionsList.contains(tKey)){
            return tKey;
        }else{
            String FirstItem = "";
            for (String Item : AllActionsList){
                FirstItem = Item;
                break;
            }
            return FirstItem;
        }
    }
    
    public static String GetAllActionsKey(String aType, String aAttribute){
        if (HasActionList(aType)){
            return aType + ADMutil.ListToken + aAttribute;
        }else{
            return aType;
        }
    }
    
    public static String GetAllActionsAttribute(String AAType){
        //the AAType is made up of the ActionType + ListToken + Attribute (Key)
        List<String> tList = ADMutil.ConvertStringtoList(AAType);
        LOG.debug("GetAllActionsAttribute: AAType '" + AAType + "' List '" + tList + "'");
        if (tList.size()==2){
            return tList.get(1);
        }else{
            //bad conversion or no attribute
            return ADMutil.OptionNotFound;
        }
    }
    
    public static String GetAllActionsType(String AAType){
        //the AAType is made up of the ActionType + ListToken + Attribute (Key)
        List<String> tList = ADMutil.ConvertStringtoList(AAType);
        if (tList.size()>=1){
            return tList.get(0);
        }else{
            //bad conversion
            return ADMutil.OptionNotFound;
        }
    }
    
    public static Collection<String> GetActionList(String Type){
        //TODO: build a AllActions list to provide a search or full list to select from.
        if (Type.equals(StandardMenuAction)){
            return CustomAction.ActionListSorted.values();
        }else if(Type.equals(TVRecordingView)){
            return SageTVRecordingViews.keySet();
        }else if(Type.equals(DynamicList)){
            return DynamicLists.keySet();
        }else if(Type.equals(GemstoneFlow)){
            return Flow.GetFlows();
        }else{
            return Collections.emptyList();
        }
    }

    public static Boolean HasActionList(String Type){
        if (Type.equals(StandardMenuAction)){
            return Boolean.TRUE;
        }else if(Type.equals(TVRecordingView)){
            return Boolean.TRUE;
        }else if(Type.equals(DynamicList)){
            return Boolean.TRUE;
        }else if(Type.equals(GemstoneFlow)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

    public static Boolean UseGenericEditBox(String Type){
        if (Type.equals(BrowseVideoFolder)){
            return Boolean.TRUE;
        }else if(IsFileBrowserType(Type)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

    public static Boolean IsFileBrowserType(String Type){
        if (Type.equals(BrowseFileFolderLocal)){
            return Boolean.TRUE;
        }else if(Type.equals(BrowseFileFolderServer)){
            return Boolean.TRUE;
        }else if(Type.equals(BrowseFileFolderImports)){
            return Boolean.TRUE;
        }else if(Type.equals(BrowseFileFolderNetwork)){
            return Boolean.TRUE;
        }else if(Type.equals(BrowseFileFolderRecDir)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }

    public static String GetFileBrowserType(String Style){
        if (Style.equals("xLocal")){
            return BrowseFileFolderLocal;
        }else if(Style.equals("xServer")){
            return BrowseFileFolderServer;
        }else if(Style.equals("xImports")){
            return BrowseFileFolderImports;
        }else if(Style.equals("xRecDirs")){
            return BrowseFileFolderRecDir;
        }else if(Style.equals("xNetwork")){
            return BrowseFileFolderNetwork;
        }else{
            return ADMutil.OptionNotFound;
        }
    }

    public static String GetGenericEditBoxMessage(String Type){
        if (Type.equals(BrowseVideoFolder)){
            return "Enter a Folder Name/Path:\nHint: use the same text as displayed\nin the 'Video by Folder' view next to 'Folder (case sensitive):'";
        }else if(IsFileBrowserType(Type)){
            return "Enter a Folder Name/Path:\nHint: use the same text as displayed\nin the File Browser";
        }else if(Type.equals(LaunchExternalApplication)){
            return "Enter a command";
        }else{
            return "";
        }
    }

    public static void LoadStandardActionList(){
        Properties CustomActionProps = new Properties();
        String CustomActionPropsPath = ADMutil.ADMDefaultsLocation() + File.separator + StandardActionListFile;
        //read the properties from the properties file
        try {
            FileInputStream in = new FileInputStream(CustomActionPropsPath);
            try {
                CustomActionProps.load(in);
                in.close();
            } catch (IOException ex) {
                LOG.debug("LoadStandardActionList: IO exception loading actions " + ADMutil.class.getName() + ex);
                return;
            }
        } catch (FileNotFoundException ex) {
            LOG.debug("LoadStandardActionList: file not found loading actions " + ADMutil.class.getName() + ex);
            return;
        }

        //write all the custom actions to Sage properties as it is easier to parse them that way - delete them when done
        if (CustomActionProps.size()>0){
            //clean up existing Custom Actions from the SageTV properties file before writing the new ones
            ADMutil.RemovePropertyAndChildren(SageADMCustomActionsPropertyLocation);
            
            for (String tPropertyKey : CustomActionProps.stringPropertyNames()){
                ADMutil.SetProperty(tPropertyKey, CustomActionProps.getProperty(tPropertyKey));
            }
        }
        
        //load custom menu actions from Sage Properties
        //find all Custom Action Name entries from the SageTV properties file
        String[] CustomActionNames = sagex.api.Configuration.GetSubpropertiesThatAreBranches(new UIContext(sagex.api.Global.GetUIContextName()),SageADMCustomActionsPropertyLocation);
        
        if (CustomActionNames.length>0){
            String PropLocation = "";
            for (String tCustomActionName : CustomActionNames){
                LOG.debug("LoadStandardActionList: loading '" + tCustomActionName + "' Custom Menu Action");
                PropLocation = SageADMCustomActionsPropertyLocation + "/" + tCustomActionName;
                String tButtonText = ADMutil.GetProperty(PropLocation + "/ButtonText", ADMutil.ButtonTextDefault);
                String tWidgetSymbol = ADMutil.GetProperty(PropLocation + "/WidgetSymbol", "");
                String tCopyModeAttributeVar = ADMutil.GetProperty(PropLocation + "/CopyModeAttributeVar", Blank);
                CustomAction tAction = new CustomAction(tCustomActionName,tButtonText, tWidgetSymbol, tCopyModeAttributeVar);
                SageMenuActions.put(tCustomActionName,tAction);
                
                //load any action variables
                Integer Counter = 0;
                Boolean Found = Boolean.TRUE;
                do {
                    Counter++;
                    //first test if the current action variable is available
                    String AVPropLocation = PropLocation + "/ActionVariables/" + Counter;
                    if (ADMutil.HasProperty(AVPropLocation + "/VarType")){
                        ActionVariable tVar = new ActionVariable();
                        tVar.VarType = ADMutil.GetProperty(AVPropLocation + "/VarType", VarTypeGlobal);
                        tVar.Var = ADMutil.GetProperty(AVPropLocation + "/Var", "");
                        tVar.Val = ADMutil.GetProperty(AVPropLocation + "/Val", "");
                        SageMenuActions.get(tCustomActionName).ActionVariables.add(tVar);
                        LOG.debug("LoadStandardActionList: Loading Vars from '" + AVPropLocation + "' VarType='" + tVar.VarType + "' Var='" + tVar.Var + "' Val ='" + tVar.Val + "'");
                    }else{
                        Found = Boolean.FALSE;
                    }
                } while (Found);

                //load any action categories
                Counter = 0;
                Found = Boolean.TRUE;
                String tCategory = "";
                do {
                    Counter++;
                    //first test if the current action category is available
                    String AVPropLocation = PropLocation + "/ActionCategory/" + Counter;
                    if (ADMutil.HasProperty(AVPropLocation)){
                        tCategory = ADMutil.GetProperty(AVPropLocation, Blank);
                        SageMenuActions.get(tCustomActionName).AddCategory(tCategory);
                        LOG.debug("LoadStandardActionList: Loading Category from '" + AVPropLocation + "' Category='" + tCategory + "' Categories '" + CustomAction.AllActionCategories + "'");
                    }else{
                        Found = Boolean.FALSE;
                    }
                } while (Found);
            }
        }
        
        //clean up existing Custom Actions from the SageTV properties file as they are no longer needed
        ADMutil.RemovePropertyAndChildren(SageADMCustomActionsPropertyLocation);
        LOG.debug("LoadStandardActionList: completed loading '" + SageMenuActions.size() + "' Custom Menu Actions");
    }

    private static void LoadSageTVRecordingViews(){
        SageTVRecordingViews.clear();
        //put the ViewType and Default View Name into a list
        SageTVRecordingViews.put("xAll","All Recordings");
        SageTVRecordingViews.put("xRecordings","Current Recordings");
        SageTVRecordingViews.put("xArchives","Archived Recordings");
        SageTVRecordingViews.put("xMovies","Recorded Movies");
        //SageTVRecordingViews.put("xPartials","");
        SageTVRecordingViews.put("xView5","Recording View5");
        SageTVRecordingViews.put("xView6","Recording View6");
        SageTVRecordingViews.put("xView7","Recording View7");
        SageTVRecordingViews.put("xView8","Recording View8");
    }

    public static String GetSageTVRecordingViewsButtonText(String Name){
        //return the stored name from Sage or the Default Name if nothing is stored
        return ADMutil.GetProperty(SageTVRecordingViewsTitlePropertyLocation + Name, SageTVRecordingViews.get(Name));
    }
    
    public static String GetSageTVRecordingViewsActionButtonText(String Name){
        //return the stored name from Sage or the Default Name if nothing is stored
        return "Recordings - " + ADMutil.GetProperty(SageTVRecordingViewsTitlePropertyLocation + Name, SageTVRecordingViews.get(Name));
    }
    
    public static void SetSageTVRecordingViewsButtonText(String ViewType, String Name){
        //rename the specified TV Recording View 
        ADMutil.SetProperty(SageTVRecordingViewsTitlePropertyLocation + ViewType, Name);
    }

    public static Collection<String> GetAllActionCategories(){
        return CustomAction.AllActionCategories;
    }
        
    public static String GetAllActionCategoriesFooter(String Name){
        String Prefix = "Current Action: ";
        String tActionType = GetButtonText(ADMMenuNode.GetMenuItemActionType(Name));
        String tActionAttribute = ADMMenuNode.GetActionAttributeButtonText(Name);
        String ReturnValue = Prefix;
        if (tActionType.equals(ActionTypeDefault)){
            return "";
        }else{
            ReturnValue = ReturnValue + tActionType;
            if (!tActionAttribute.equals(ADMutil.OptionNotFound)){
                ReturnValue = ReturnValue + "\n " + tActionAttribute;
            }
        }
        return ReturnValue;
    }
    
    public static void ActionCategoryFilterReset(){
        if (GetActionCategoryFilterSticky().equals(ADMutil.TriState.OTHER)){
            LOG.debug("ActionCategoryFilterReset: sticky found so no change");
            //leave it as is as it is supposed to be sticky
        }else{
            //reset the Filter to the ShowAll filter
            LOG.debug("ActionCategoryFilterReset: reseting to Show All");
            ADMutil.SetPropertyAsTriState(ActionCategoryFilterStickyPropertyLocation, ADMutil.TriState.NO);
            ADMutil.SetProperty(ActionCategoryFilterPropertyLocation, ActionCategoryShowAll);
        }
    }
    
    public static String GetActionCategoryFilter(){
        return ADMutil.GetProperty(ActionCategoryFilterPropertyLocation, ActionCategoryShowAll);
    }
    
    public static String GetActionCategoryFilterButtonText(){
        String tFilter = ADMutil.GetProperty(ActionCategoryFilterPropertyLocation, ActionCategoryShowAll);
        if (tFilter.equals(ActionCategoryShowAll)){
            return "-Not Filtered-";
        }else{
            return " ";
        }
    }
    
    public static ADMutil.TriState GetActionCategoryFilterSticky(){
        return ADMutil.GetPropertyAsTriState(ActionCategoryFilterStickyPropertyLocation, ADMutil.TriState.NO);
    }
    
    public static Boolean GetActionCategoryFilterStickyIsChecked(String Category){
        String tCategory = ADMutil.GetProperty(ActionCategoryFilterPropertyLocation, Blank);
        if (tCategory.equals(Category)){
            ADMutil.TriState tSticky = ADMutil.GetPropertyAsTriState(ActionCategoryFilterStickyPropertyLocation, ADMutil.TriState.NO);
            if (tSticky.equals(ADMutil.TriState.OTHER) || tSticky.equals(ADMutil.TriState.YES)){
                return Boolean.TRUE;
            }else{
                return Boolean.FALSE;
            }
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static Boolean GetActionCategoryFilterStickyIsSticky(String Category){
        String tCategory = ADMutil.GetProperty(ActionCategoryFilterPropertyLocation, Blank);
        if (tCategory.equals(Category)){
            ADMutil.TriState tSticky = ADMutil.GetPropertyAsTriState(ActionCategoryFilterStickyPropertyLocation, ADMutil.TriState.NO);
            if (tSticky.equals(ADMutil.TriState.OTHER)){
                return Boolean.TRUE;
            }else{
                return Boolean.FALSE;
            }
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static void ChangeActionCategoryFilter(String Category){
        String prevCategory = ADMutil.GetProperty(ActionCategoryFilterPropertyLocation, Blank);
        ADMutil.TriState prevSticky = ADMutil.GetPropertyAsTriState(ActionCategoryFilterStickyPropertyLocation, ADMutil.TriState.NO);
        ADMutil.TriState newSticky = ADMutil.TriState.YES;
        String newCategory = Category;
        if (prevCategory.equals(Category)){
            if (prevSticky.equals(ADMutil.TriState.YES)){
                newSticky = ADMutil.TriState.OTHER;
            }else if (prevSticky.equals(ADMutil.TriState.OTHER)){
                newSticky = ADMutil.TriState.NO;
                newCategory = ActionCategoryShowAll;
            }else{
                newSticky = ADMutil.TriState.YES;
            }
        }
        //store the newCategory
        ADMutil.SetPropertyAsTriState(ActionCategoryFilterStickyPropertyLocation, newSticky);
        ADMutil.SetProperty(ActionCategoryFilterPropertyLocation, newCategory);
    }
    
    public static class ActionVariable{
        private String Var = "";
        private String Val = "";
        private String VarType = VarTypeGlobal;

        public ActionVariable(){
        }
        
        public ActionVariable(String VarType, String Var, String Val ){
            this.VarType = VarType;
            this.Var = Var;
            this.Val = Val;
        }
        
        public String getVarType(){
            return this.VarType;
        }
        public String getVar(){
            return this.Var;
        }
        public String getVal(){
            return this.Val;
        }
        //using an Object rather than a string as for PlayLists you need to pass an Object into a Global Variable
        public void EvaluateVariable(Object Attribute){
            Object tVal = this.Val;
            if (this.Val.equals(UseAttributeValue) || this.Val.equals(UseAttributeObjectValue)){
                tVal = Attribute;
            }else if (this.Val.equals(VarNull)){
                tVal = null;
            }
            if (this.VarType.equals(VarTypeGlobal)){
                sagex.api.Global.AddGlobalContext(new UIContext(sagex.api.Global.GetUIContextName()), this.Var, tVal);
            }else if (this.VarType.equals(VarTypeStatic)){
                sagex.api.Global.AddStaticContext(new UIContext(sagex.api.Global.GetUIContextName()), this.Var, tVal);
            }else if (this.VarType.equals(VarTypeSetProp)){
                ADMutil.SetProperty(this.Var, tVal.toString());
            }
            LOG.debug("EvaluateVariable - Type '" + this.VarType + "' setting '" + this.Var + "' to '" + tVal + "' for Attribute '" + Attribute + "' original Val ='" + this.Val + "'");
        }
    }

    public static class CustomAction{
        private String Name = "";
        private String ButtonText = "";
        private String WidgetSymbol = "";
        private String CopyModeAttributeVar = Blank;
        private List<ActionVariable> ActionVariables = new LinkedList<ActionVariable>();
        private List<String> ActionCategories = new LinkedList<String>();
        public static Collection<String> WidgetSymbols = new LinkedHashSet<String>();
        //the combination of the Name and CopymodeAttributeVar fields make the CustomAction unique for the CopyMode
        public static Collection<String> CopyModeUniqueIDs = new LinkedHashSet<String>();
        //need a list of keys that are sorted by the ButtonText
        public static SortedMap<String,String> ActionListSorted = new TreeMap<String,String>();
        //unique set of All Categories
        public static SortedSet<String> AllActionCategories = new TreeSet<String>();

        
//        public CustomAction(String Name, String ButtonText){
//            this(Name,ButtonText,"",Blank);
//        }
//
        public CustomAction(String Name, String ButtonText, String WidgetSymbol){
            this(Name,ButtonText,WidgetSymbol,Blank);
        }

        public CustomAction(String Name, String ButtonText, String WidgetSymbol, String CopyModeAttributeVar){
            this.Name = Name;
            this.ButtonText = ButtonText;
            this.WidgetSymbol = WidgetSymbol;
            this.CopyModeAttributeVar = CopyModeAttributeVar;
            //WidgetSymbols list is used for the copymode function to further refine the item to be copied
            //therefore it is not populated for items that do not have a CopyModeAttributeVar
            if (!CopyModeAttributeVar.equals(Blank)){
                WidgetSymbols.add(WidgetSymbol);
            }
            CopyModeUniqueIDs.add(UniqueID(CopyModeAttributeVar,Name));
            ActionListSorted.put(this.ButtonText, Name);
        }
        
        public void AddCategory(String aCategory){
            ActionCategories.add(aCategory);
            AllActionCategories.add(aCategory);
        }
        
        public Boolean HasCategory(String aCategory){
            return ActionCategories.contains(aCategory);
        }
        public void Execute(String Attribute){
            //evaluate all the ActionVariables first
            for (ActionVariable ActionVar : ActionVariables){
                ActionVar.EvaluateVariable(Attribute);
            }
            
            //Execute the WidgetSymbol
            ADMAction.ExecuteWidget(WidgetSymbol);
        }
        
        public static String UniqueID(String Name, String Var){
            return Name + ADMutil.ListToken + Var;
        }
        
    }
    
    public static class ExternalAction{
        //portions borrowed from NielM ExtCommand.java code
        private Integer windowType; // (maximised | minimised | hidden | normal | console )
        private String command;
        private String arguments;
        private Boolean waitForExit;
        private Integer sageStatus;
        private String MenuItemName;

        static public final String[] windowTypeStrings = { 
            "Normal",
            "Maximised",
            "Minimised",
            "Hidden"
        };
        static public final int WINDOW_NORMAL =0;
        static public final int WINDOW_MAXIMISED=1;
        static public final int WINDOW_MINIMISED=2;
        static public final int WINDOW_HIDDEN=3;

        static public final String[] sageStatusStrings = { 
            "Do nothing with Sage",
            "Put Sage in Sleep Mode",
            "Exit Sage after application launch"
        };
        static public final Integer SageStatusNothing = 0;
        static public final Integer SageStatusSleep = 1;
        static public final Integer SageStatusExit = 2;
        
        public ExternalAction(String MenuItemName){
            this.MenuItemName = MenuItemName;
            this.command="";
            this.windowType=0;
            this.arguments="";
            this.waitForExit=Boolean.TRUE;
            this.sageStatus = SageStatusNothing;
        }
        
        public ExternalAction(String MenuItemName, String command, Integer windowType, String arguments, Boolean waitForExit, Integer sageStatus ){
            this.MenuItemName = MenuItemName;
            this.command=command;
            
            //validate windowType
            if (windowType<0 || windowType>windowTypeStrings.length){
                this.windowType=0;  //default to Normal
            }else{
                this.windowType=windowType;
            }
            this.arguments=arguments;
            this.waitForExit=waitForExit;
            if (sageStatus<0 || sageStatus>sageStatusStrings.length){
                this.sageStatus = SageStatusNothing;
            }else{
                this.sageStatus = sageStatus;
            }
        }
        
        public String GetApplication(){
            return this.command;
        }
        public void SetApplication(String bApplication){
            this.command = bApplication;
            this.Save();
        }
        public String GetArguments(){
            return this.arguments;
        }
        public void SetArguments(String bArguments){
            this.arguments = bArguments;
            this.Save();
        }
        public String GetWindowType(){
            return windowTypeStrings[windowType];
        }
        public void ChangeWindowType(Integer Delta){
            this.windowType = this.windowType + Delta;
            if (windowType>=windowTypeStrings.length){
                this.windowType = 0;
            }else if(windowType<0){
                this.windowType = windowTypeStrings.length-1;
            }
            this.Save();
        }
        
        public String GetWaitForExit(){
            if (waitForExit){
                return "Wait until Application Exits";
            }else{
                return "Do not wait";
            }
        }
        public void ChangeWaitForExit(){
            this.waitForExit = !this.waitForExit;
            this.Save();
        }

        public String GetSageStatus(){
            return sageStatusStrings[sageStatus];
        }
        public void ChangeSageStatus(Integer Delta){
            this.sageStatus = this.sageStatus + Delta;
            if (sageStatus>=sageStatusStrings.length){
                this.sageStatus = 0;
            }else if(sageStatus<0){
                this.sageStatus = sageStatusStrings.length-1;
            }
            this.Save();
        }
        
        public void Save(){
            //save all variables to the sage Properties
            String PropLocation = ADMutil.SagePropertyLocation + MenuItemName + "/ExternalAction/";
            ADMutil.SetProperty(PropLocation + "Application", command);
            ADMutil.SetProperty(PropLocation + "Arguments", arguments);
            ADMutil.SetProperty(PropLocation + "WindowType", windowType.toString());
            ADMutil.SetProperty(PropLocation + "WaitForExit", waitForExit.toString());
            ADMutil.SetProperty(PropLocation + "SageStatus", sageStatus.toString());
        }
        
        public void Load(){
            //save all variables to the sage Properties
            String PropLocation = ADMutil.SagePropertyLocation + MenuItemName + "/ExternalAction/";
            this.command = ADMutil.GetProperty(PropLocation + "Application", "");
            this.arguments = ADMutil.GetProperty(PropLocation + "Arguments", "");
            this.windowType = ADMutil.GetPropertyAsInteger(PropLocation + "WindowType", 0);
            this.waitForExit = ADMutil.GetPropertyAsBoolean(PropLocation + "WaitForExit", Boolean.TRUE);
            this.sageStatus = ADMutil.GetPropertyAsInteger(PropLocation + "SageStatus", 0);
        }

        public void AddProperties(Properties inProp){
            String PropLocation = ADMutil.SagePropertyLocation + MenuItemName + "/ExternalAction/";
            inProp.setProperty(PropLocation + "Application", command);
            inProp.setProperty(PropLocation + "Arguments", arguments);
            inProp.setProperty(PropLocation + "WindowType", windowType.toString());
            inProp.setProperty(PropLocation + "WaitForExit", waitForExit.toString());
            inProp.setProperty(PropLocation + "SageStatus", sageStatus.toString());
        }
        
        public void Execute(){
            UIContext MyUIContext = new UIContext(sagex.api.Global.GetUIContextName());
            try{            
                String osName = System.getProperty("os.name");
                String[] cmd = null;

                if ( osName.toLowerCase().startsWith("windows")) {
                    cmd = new String[3];
                    if( osName.equals( "Windows 95" ) || osName.equals( "Windows 98" ) ){
                        cmd[0] = "command.com" ;
                        cmd[1] = "/C" ;
                    } else if( osName.toLowerCase().contains("win")){//any newer Windows OS
                        cmd[0] = "cmd.exe" ;
                        cmd[1] = "/C" ;
                    }else {
                        LOG.debug("ExternalAction.Execute: unknown OS:"+osName+" assuming newer Windows OS");
                        cmd[0] = "cmd.exe" ;
                        cmd[1] = "/C" ;
                    }
                    cmd[2] = "start \"console\" ";
                    if ( waitForExit ){
                        cmd[2]=cmd[2] + "/wait ";
                    }
                    //Window Types
                    if ( windowType == WINDOW_MAXIMISED ){
                        cmd[2] = cmd[2] +"/max ";
                    }else if ( windowType == WINDOW_MINIMISED ){
                        cmd[2] = cmd[2] +"/min ";
                    }else if ( windowType == WINDOW_HIDDEN ){
                        cmd[2] = cmd[2] +"/b ";
                    }

                    // append command -- first for window title, second for exe name
                    cmd[2]=cmd[2]+"\""+command+"\" " + arguments;
                    LOG.debug("ExternalAction.Execute: Command = '" + cmd[0] + " " + cmd[1] + " " + cmd[2] + "'");
                } else {
                    cmd = new String[2];
                    cmd[0]=command;
                    cmd[1]=arguments;
                    LOG.debug("ExternalAction.Execute: for unknown OS '" + osName + "' Command = '" + cmd[0] + " " + cmd[1] + "'" );

                }
                //determine what to do with Sage - before
                Boolean tFullScreen = sagex.api.Global.IsFullScreen(MyUIContext);
                if (sageStatus.equals(SageStatusSleep)){
                    if (tFullScreen){
                        sagex.api.Global.SetFullScreen(MyUIContext, Boolean.FALSE);
                    }
                    sagex.api.Global.SageCommand(MyUIContext,"Power Off");
                }else if (sageStatus.equals(SageStatusExit)){
                    //the exit command would only occur AFTER the External Application is executed - see below
                    //sagex.api.Global.Exit(new UIContext(sagex.api.Global.GetUIContextName()));
                }else{
                    //do thing assumed
                }
                
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec(cmd);
                // any error message?
                StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");            

                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");

                // kick them off
                errorGobbler.start();
                outputGobbler.start();

                // any error???
                int exitVal = proc.waitFor();
                LOG.debug("ExternalAction.Execute: ExitValue: '" + exitVal + "'"); 

                //determine what to do with Sage - after
                if (sageStatus.equals(SageStatusSleep)){
                    sagex.api.Global.SageCommand(MyUIContext,"Power On");
                    if (tFullScreen){
                        sagex.api.Global.SetFullScreen(MyUIContext, Boolean.TRUE);
                    }
                }else if (sageStatus.equals(SageStatusExit)){
                    //exit Sage after starting the External Application
                    sagex.api.Global.Exit(new UIContext(sagex.api.Global.GetUIContextName()));
                }else{
                    //do thing assumed
                }
                
                
            } catch (Throwable t)
            {
                LOG.debug("ExternalAction.Execute: ERROR - Exception = '" + t + "'"); 
                //t.printStackTrace();
            }
            
        }
        
        //StreamGobbler class from
        //http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html

        public static class StreamGobbler extends Thread
        {
            InputStream is;
            String type;

            StreamGobbler(InputStream is, String type)
            {
                this.is = is;
                this.type = type;
            }

            @Override
            public void run()
            {
                try
                {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line=null;
                    while ( (line = br.readLine()) != null)
                        System.out.println(type + ">" + line);
                } catch (IOException ioe)
                {
                    LOG.debug("ExternalAction.StreamGobbler: ERROR - Exception = '" + ioe + "'"); 
                    //ioe.printStackTrace();
                }
            }
        }

    }
    
    
}
