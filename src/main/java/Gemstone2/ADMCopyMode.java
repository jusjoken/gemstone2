package Gemstone2;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import sagex.UIContext;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jusjoken
 */
public class ADMCopyMode {

    static private final Logger LOG = Logger.getLogger(ADMCopyMode.class);
    public static final String SageCurrentMenuItemPropertyLocation = "ADM/currmenuitem/";

    //save the current Folder item details to sage properties to assist the copy function
    public static void SaveFileFolderDetails(String CurFolderStyle, String BrowserFileCell){
        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "Type", "FileFolder");

        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "CurFolderStyle", CurFolderStyle);
        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "BrowserFileCell", BrowserFileCell);
        LOG.debug("SaveFileFolderDetails: CurFolderStyle '" + CurFolderStyle + "' BrowserFileCell '" + BrowserFileCell + "'");
    }
    
    public static String GetFileFolderDetails(){
        //determine if Combined mode is on as the path is created differently
        String BrowserFileCell = ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "BrowserFileCell", ADMutil.OptionNotFound);
        return BrowserFileCell;
    }
    
    public static Boolean IsFileFolderStyleValid(){
        //determine if Combined mode is on as the path is created differently
        String CurFolderStyle = ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "CurFolderStyle", ADMutil.OptionNotFound);
        if (ADMAction.GetFileBrowserType(CurFolderStyle).equals(ADMutil.OptionNotFound)){
            LOG.debug("IsFileFolderStyleValid: invalid Style = '" + CurFolderStyle + "'");
            return Boolean.FALSE;
        }else{
            LOG.debug("IsFileFolderStyleValid: valid Style = '" + CurFolderStyle + "'");
            return Boolean.TRUE;
        }
    }
    
    public static String GetFileFolderDetailsButtonText(){
        String ButtonText = GetFileFolderDetails();
        ButtonText = ButtonText.replace("/", " ").trim();
        ButtonText = ButtonText.replace("\\", " ").trim();
        if (ButtonText.isEmpty()){
            ButtonText = "Root";
        }
        return ButtonText;
    }
    
    public static Collection<String> GetFileFolderDetailsParentList(){
        return ADMMenuNode.GetMenuItemParentList();
    }
    
    //create a new Menu Item from the current Video Folder Menu Item details
    public static String CreateMenuItemfromFileFolderCopyDetails(String Parent){
        //Create a new MenuItem with defaults
        String CurFolderStyle = ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "CurFolderStyle", ADMutil.OptionNotFound);
        if (!ADMAction.GetFileBrowserType(CurFolderStyle).equals(ADMutil.OptionNotFound)){
            String tMenuItemName = ADMMenuNode.NewMenuItem(Parent, 0);

            //set all the copy details
            ADMMenuNode.SetMenuItemAction(tMenuItemName,GetFileFolderDetails());
            ADMMenuNode.SetMenuItemActionType(tMenuItemName,ADMAction.GetFileBrowserType(CurFolderStyle));

            ADMMenuNode.SetMenuItemBGImageFile(tMenuItemName,ADMutil.ListNone);
            ADMMenuNode.SetMenuItemButtonText(tMenuItemName,GetFileFolderDetailsButtonText());
            ADMMenuNode.SetMenuItemName(tMenuItemName);
            ADMMenuNode.SetMenuItemSubMenu(tMenuItemName,ADMutil.ListNone);
            ADMMenuNode.SetMenuItemIsActive(tMenuItemName,ADMutil.TriState.YES);

            LOG.debug("CreateMenuItemfromFileFolderCopyDetails: created '" + tMenuItemName + "' for Parent = '" + Parent + "'");
            return tMenuItemName;
            
        }else{
            LOG.debug("CreateMenuItemfromFileFolderCopyDetails: invalid CurFolderStyle '" + CurFolderStyle + "'");
            return ADMutil.OptionNotFound;
        }
    }
    
    //save the current Folder item details to sage properties to assist the copy function
    public static void SaveVideoFolderDetails(String CurFolder, String VideoItem){
        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "Type", "Folder");

        if (CurFolder==null || CurFolder.equals("null")){
            ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "CurFolder", null);
            ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "VideoItem", VideoItem);
        }else{
            ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "CurFolder", CurFolder);
            ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "VideoItem", VideoItem);
        }
        LOG.debug("SaveVideoFolderDetails: CurFolder '" + CurFolder + "' VideoItem '" + VideoItem + "'");
    }
    
    public static String GetVideoFolderDetails(){
        //determine if Combined mode is on as the path is created differently
        String CurFolder = ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "CurFolder", ADMutil.OptionNotFound);
        String VideoItem = ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "VideoItem", ADMutil.OptionNotFound);
        
        if (ADMutil.GetProperty("video_lib/folder_style", "xCombined").equals("xCombined")){
            if(CurFolder==null || CurFolder.equals(ADMutil.OptionNotFound)){
                return VideoItem + "/";
            }else{
                return CurFolder + VideoItem + "/";
            }
        }else{
            if(CurFolder==null || CurFolder.equals(ADMutil.OptionNotFound)){
                return VideoItem;
            }else{
                return sagex.api.Utility.CreateFilePath( CurFolder, VideoItem ).toString();
            }
        }
    }
    
    public static String GetVideoFolderDetailsButtonText(){
        String ButtonText = GetVideoFolderDetails();
        ButtonText = ButtonText.replace("/", " ").trim();
        ButtonText = ButtonText.replace("\\", " ").trim();
        if (ButtonText.isEmpty()){
            ButtonText = "Root";
        }
        return ButtonText;
    }
    
    public static Collection<String> GetVideoFolderDetailsParentList(){
        return ADMMenuNode.GetMenuItemParentList();
    }
    
    //create a new Menu Item from the current Video Folder Menu Item details
    public static String CreateMenuItemfromVideoFolderCopyDetails(String Parent){
        //Create a new MenuItem with defaults
        String tMenuItemName = ADMMenuNode.NewMenuItem(Parent, 0);

        //set all the copy details
        ADMMenuNode.SetMenuItemAction(tMenuItemName,GetVideoFolderDetails());
        ADMMenuNode.SetMenuItemActionType(tMenuItemName,ADMAction.BrowseVideoFolder);

        ADMMenuNode.SetMenuItemBGImageFile(tMenuItemName,ADMutil.ListNone);
        ADMMenuNode.SetMenuItemButtonText(tMenuItemName,GetVideoFolderDetailsButtonText());
        ADMMenuNode.SetMenuItemName(tMenuItemName);
        ADMMenuNode.SetMenuItemSubMenu(tMenuItemName,ADMutil.ListNone);
        ADMMenuNode.SetMenuItemIsActive(tMenuItemName,ADMutil.TriState.YES);
        
        LOG.debug("CreateMenuItemfromVideoFolderCopyDetails: created '" + tMenuItemName + "' for Parent = '" + Parent + "'");
        return tMenuItemName;
        
    }
    
    //save the current item details to sage properties to assist the copy function
    public static void SaveMenuItemDetails(String ButtonText, String SubMenu, String CurrentWidgetSymbol, Integer Level, String UniqueID){
        //clear previously stored Menu Item Details
        ADMutil.RemovePropertyAndChildren(SageCurrentMenuItemPropertyLocation);
        //save the current details
        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "Type", "MenuItem");
        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "WidgetSymbol", CurrentWidgetSymbol);
        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "ButtonText", ButtonText);
        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "SubMenu", SubMenu);
        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "Level", Level.toString());
        ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "UniqueID", UniqueID);
        
        //determine if there is an Action Widget for this Menu Item
        String ActionWidget = null;
        UIContext tUIContext = new UIContext(sagex.api.Global.GetUIContextName());
        Object[] Children = sagex.api.WidgetAPI.GetWidgetChildren(tUIContext, CurrentWidgetSymbol);
        for (Object Child : Children){
            //LOG.debug("SaveCurrentMenuItemDetails: WidgetName = '" + sagex.api.WidgetAPI.GetWidgetName(tUIContext,Child) + "' WidgetType '" + sagex.api.WidgetAPI.GetWidgetType(tUIContext,Child) + "'");
            List<String> validActions = new LinkedList<String>();
            validActions.add("Action");
            validActions.add("Menu");
            validActions.add("OptionsMenu");
            validActions.add("Conditional");
            if (validActions.contains(sagex.api.WidgetAPI.GetWidgetType(tUIContext,Child))){
                //found an action so save it and leave
                ActionWidget = sagex.api.WidgetAPI.GetWidgetSymbol(tUIContext,Child);
                break;
            }
        }
        String FinalAction = ActionWidget;
        String FinalType = ADMAction.WidgetbySymbol;
        if (ActionWidget!=null){
            //test for special Action Widget Symbols
            if (ActionWidget.equals(ADMAction.GetWidgetSymbol(ADMAction.TVRecordingView))){
                //TV RecordingsView found so save the view Type
                //LOG.debug("SaveCurrentMenuItemDetails: TVRecordingView for ActionWidget '" + ActionWidget + "'");
                FinalType = ADMAction.TVRecordingView;
                String tViewFilter = ADMutil.OptionNotFound;
                String tViewTitlePostfixText = ADMutil.OptionNotFound;
                for (Object Child : Children){
                    if ("Attribute".equals(sagex.api.WidgetAPI.GetWidgetType(new UIContext(sagex.api.Global.GetUIContextName()),Child))){
                        if ("ViewFilter".equals(sagex.api.WidgetAPI.GetWidgetName(new UIContext(sagex.api.Global.GetUIContextName()),Child))){
                            tViewFilter = sagex.api.WidgetAPI.GetWidgetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Child,"Value");
                            //get rid of the imbedded " (quotes) in the returned string
                            tViewFilter = tViewFilter.replace("\"", "");
                        }else if ("ViewTitlePostfixText".equals(sagex.api.WidgetAPI.GetWidgetName(new UIContext(sagex.api.Global.GetUIContextName()),Child))){
                            tViewTitlePostfixText = sagex.api.WidgetAPI.GetWidgetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Child,"Value");
                            //get rid of the imbedded " (quotes) in the returned string
                            tViewFilter = "xView" + tViewTitlePostfixText.replace("\"", "");
                            break;
                        }
                    }
                }
                //determine if a standard TV Recording view was found or one of the extra views (5,6,7,or 8)
                if (ADMAction.GetActionList(ADMAction.TVRecordingView).contains(tViewFilter)){
                    FinalAction = tViewFilter;
                }else{
                    FinalAction = "xAll";
                }
            }else if (ActionWidget.equals(ADMAction.GetWidgetSymbol(ADMAction.GemstoneFlow))){
                if (UniqueID!=null){
                    FinalAction = UniqueID;
                    FinalType = ADMAction.GemstoneFlow;
                }
                //LOG.debug("SaveCurrentMenuItemDetails: GemstoneFlow for ActionWidget '" + ActionWidget + "' FinalAction '" + FinalAction + "'");
            }else if (ADMAction.CustomAction.WidgetSymbols.contains(ActionWidget)){
                //CustomAction found so determine which one 
                //LOG.debug("SaveCurrentMenuItemDetails: CustomAction for ActionWidget '" + ActionWidget + "'");
                Boolean tFound = Boolean.FALSE;
                for (Object Child : Children){
                    //check only Attribute Widgets
                    if ("Attribute".equals(sagex.api.WidgetAPI.GetWidgetType(new UIContext(sagex.api.Global.GetUIContextName()),Child))){
                        //append the widget Value to the widget Name to lookup a unique CopyMode name
                        String tName = sagex.api.WidgetAPI.GetWidgetName(new UIContext(sagex.api.Global.GetUIContextName()),Child);
                        String tVar = sagex.api.WidgetAPI.GetWidgetProperty(new UIContext(sagex.api.Global.GetUIContextName()),Child,"Value");
                        tVar = tVar.replace("\"", "");
                        String tCheckString = ADMAction.CustomAction.UniqueID(tName,tVar);
                        if (ADMAction.CustomAction.CopyModeUniqueIDs.contains(tCheckString)){
                            FinalAction = tVar;
                            tFound = Boolean.TRUE;
                            break;
                        }
                    }
                }
                if (tFound){
                    FinalType = ADMAction.StandardMenuAction;
                }
            }else if (ADMAction.GetActionList(ADMAction.StandardMenuAction).contains(ActionWidget)){
                //LOG.debug("SaveCurrentMenuItemDetails: StandardMenuAction for ActionWidget '" + ActionWidget + "'");
                FinalType = ADMAction.StandardMenuAction;
            }else{
                //LOG.debug("SaveCurrentMenuItemDetails: NOTHING for ActionWidget '" + ActionWidget + "' List = [" + ADMAction.CustomAction.WidgetSymbols + "]");
            }
            ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "Type", FinalType);
            ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "Action", FinalAction);
        }else{
            //no action found so set to DoNothing
            //LOG.debug("SaveCurrentMenuItemDetails: NO ACTION '" + ActionWidget + "'");
            ADMutil.SetProperty(SageCurrentMenuItemPropertyLocation + "Type", ADMAction.ActionTypeDefault);
        }
        LOG.debug("SaveCurrentMenuItemDetails: ButtonText '" + ButtonText + "' SubMenu '" + SubMenu + "' WidgetSymbol '" + CurrentWidgetSymbol + "' Level '" + Level + "' Type ='" + FinalType + "' Action = '" + FinalAction + "'");
    }
    
    //create a new Menu Item from the current Menu Item details
    public static String CreateMenuItemfromCopyDetails(String Parent){
        //Create a new MenuItem with defaults
        String tMenuItemName = ADMMenuNode.NewMenuItem(Parent, 0);

        //set all the copy details
        if (ADMAction.IsValidAction(GetMenuItemDetailsType())){
            ADMMenuNode.SetMenuItemAction(tMenuItemName,GetMenuItemDetailsAction());
            ADMMenuNode.SetMenuItemActionType(tMenuItemName,GetMenuItemDetailsType());
        }
        ADMMenuNode.SetMenuItemBGImageFile(tMenuItemName,ADMutil.ListNone);
        ADMMenuNode.SetMenuItemButtonText(tMenuItemName,GetMenuItemDetailsButtonText());
        ADMMenuNode.SetMenuItemName(tMenuItemName);
        if (GetMenuItemDetailsSubMenu().equals(ADMutil.OptionNotFound)){
            ADMMenuNode.SetMenuItemSubMenu(tMenuItemName,ADMutil.ListNone);
        }else{
            ADMMenuNode.SetMenuItemSubMenu(tMenuItemName,GetMenuItemDetailsSubMenu());
        }
        ADMMenuNode.SetMenuItemIsActive(tMenuItemName,ADMutil.TriState.YES);

        LOG.debug("CreateMenuItemfromCopyDetails: created '" + tMenuItemName + "' for Parent = '" + Parent + "'");
        return tMenuItemName;
        
    }
    
    public static String CreateMenuItemfromCopyDetails(){
        //default the Parent to TopMenu
        return CreateMenuItemfromCopyDetails(ADMutil.TopMenu);
    }
    
    public static Collection<String> GetMenuItemDetailsParentList(){
        if (GetMenuItemDetailsSubMenu().equals(ADMutil.OptionNotFound)){
            return ADMMenuNode.GetMenuItemParentList();
        }else{
            return ADMMenuNode.GetMenuItemParentList(GetMenuItemDetailsLevel());
        }
    }
    
    public static String GetMenuItemDetailsButtonText(){
        return ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "ButtonText", ADMutil.OptionNotFound);
    }
    
    public static String GetMenuItemDetailsAction(){
        return ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "Action", ADMutil.OptionNotFound);
    }
    
    public static String GetMenuItemDetailsType(){
        return ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "Type", ADMutil.OptionNotFound);
    }
    
    public static Integer GetMenuItemDetailsLevel(){
        Integer tLevel = 0;
        try {
            tLevel = Integer.valueOf(ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "Level", "0"));
        } catch (NumberFormatException ex) {
            LOG.debug("GetCurrentMenuItemDetailsLevel: error loading level: " + ADMutil.class.getName() + ex);
            tLevel = 0;
        }
        //LOG.debug("GetCurrentMenuItemDetailsLevel: returning level = '" + tLevel + "'");
        return tLevel;
    }
    
    public static String GetMenuItemDetailsWidgetSymbol(){
        return ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "WidgetSymbol", ADMutil.OptionNotFound);
    }
    
    public static String GetMenuItemDetailsSubMenu(){
        return ADMutil.GetProperty(SageCurrentMenuItemPropertyLocation + "SubMenu", ADMutil.OptionNotFound);
    }
    
    

}
    
