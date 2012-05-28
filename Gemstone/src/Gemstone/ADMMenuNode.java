/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.log4j.Logger;
import sagex.UIContext;
import sagex.phoenix.image.ImageUtil;

/**
 *
 * @author jusjoken
 */
public class ADMMenuNode {
    //public static DefaultMutableTreeNode Testing;

    static private final Logger LOG = Logger.getLogger(ADMMenuNode.class);
    private String Parent = "";
    public String Name = "";
    private String ButtonText = "";
    private String SubMenu = "";
    private String ActionAttribute = "";
    private Object ActionObject = null;
    private String ActionType = "";
    private String BGImageFile = "";
    private String BGImageFilePath = "";
    private Boolean IsDefault = false;
    private ADMutil.TriState IsActive = ADMutil.TriState.YES;
    private List<String> BlockedSageUsersList = new LinkedList<String>();
    private Integer SortKey = 0;
    private DefaultMutableTreeNode NodeItem;
    private String ShowIF = ADMutil.OptionNotFound;
    //IsCreatedNotLoaded is used to optionally avoid exporting created menu items if rebuilding a default menu
    // only used in Hidden Features mode when creating Default Menus from an export
    private Boolean IsCreatedNotLoaded = Boolean.FALSE;
    //IsTemp is used for Menu Items that are created as part of Dynamic Lists so they can be deleted
    private Boolean IsTemp = Boolean.FALSE;
    private ADMAction.ExternalAction ActionExternal = null;
    public static Integer SortKeyCounter = 0;
    //public static Map<String,MenuNode> InternalMenuNodeList = new LinkedHashMap<String,MenuNode>();
    public static Map<String,LinkedHashMap> UIMenuNodeList = new LinkedHashMap<String,LinkedHashMap>();
    public static Map<String,DefaultMutableTreeNode> UIroot = new LinkedHashMap<String,DefaultMutableTreeNode>();
    public static DefaultMutableTreeNode Internalroot = new DefaultMutableTreeNode(ADMutil.OptionNotFound);
    public static final String SageUserAdministrator = "Administrator";
    public static Map<String,Collection<String>> UIMenuListLevel1 = new LinkedHashMap<String,Collection<String>>();
    public static Map<String,Collection<String>> UIMenuListLevel2 = new LinkedHashMap<String,Collection<String>>();
    public static Map<String,Collection<String>> UIMenuListLevel3 = new LinkedHashMap<String,Collection<String>>();
    public static Map<String,Collection<String>> UIMenuListQLM = new LinkedHashMap<String,Collection<String>>();
    private static SoftHashMap BGCache = new SoftHashMap(5);

    public ADMMenuNode(String bName){
        //create a MenuItem with just default values
        this(ADMutil.TopMenu,bName,0,ADMutil.ButtonTextDefault,null,ADMutil.ActionTypeDefault,null,null,Boolean.FALSE,ADMutil.TriState.YES);
    }
    
    public ADMMenuNode(String bParent, String bName, Integer bSortKey, String bButtonText, String bSubMenu, String bActionType, String bAction, String bBGImageFile, Boolean bIsDefault, ADMutil.TriState bIsActive){
        Parent = bParent;
        Name = bName;
        ButtonText = bButtonText;
        SubMenu = bSubMenu;
        ActionType = bActionType;
        ActionAttribute = bAction;
        SetBGImageFileandPath(bBGImageFile);
        IsDefault = bIsDefault;
        IsActive = bIsActive;
        SortKey = bSortKey;
        ActionExternal = new ADMAction.ExternalAction(Name);
        MenuNodeList().put(Name, this);
    }
    
    @Override
    public String toString(){
        //TODO: may want to change this to ButtonText
        return Name;
        //return ButtonText;
    }

    public static String GetMenuItemAction(String Name){
        //LOG.debug("GetMenuItemAction for '" + Name + "' = '" + MenuNodeList().get(Name).ActionAttribute + "'");
        try {
            if (MenuNodeList().get(Name).ActionType.equals(ADMAction.LaunchExternalApplication)){
                return MenuNodeList().get(Name).ActionExternal.GetApplication();
            }else{
                return MenuNodeList().get(Name).ActionAttribute;
            }
        } catch (Exception e) {
            LOG.debug("GetMenuItemAction ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemAction(String Name, String Setting){
        Save(Name, "Action", Setting);
    }

    public static Object GetMenuItemActionObject(String Name){
        //LOG.debug("GetMenuItemActionObject for '" + Name + "' = '" + MenuNodeList().get(Name).ActionObject + "'");
        try {
            return MenuNodeList().get(Name).ActionObject;
        } catch (Exception e) {
            LOG.debug("GetMenuItemActionObject ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemActionObject(String Name, Object Setting){
        //this is saved in memory only and not written to the properties file
        MenuNodeList().get(Name).ActionObject = Setting;
    }

    public static String GetMenuItemActionType(String Name){
        try {
            return MenuNodeList().get(Name).ActionType;
        } catch (Exception e) {
            LOG.debug("GetMenuItemActionType ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemActionType(String Name, String Setting){
        Save(Name, "ActionType", Setting);
    }

    public static ADMAction.ExternalAction GetMenuItemActionExternal(String Name){
        try {
            return MenuNodeList().get(Name).ActionExternal;
        } catch (Exception e) {
            LOG.debug("GetMenuItemActionExternal ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }
    public static String GetMenuItemActionExternalApplication(String Name){
        try {
            return MenuNodeList().get(Name).ActionExternal.GetApplication();
        } catch (Exception e) {
            LOG.debug("GetMenuItemActionExternalApplication ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }
    public static String GetMenuItemActionExternalArguments(String Name){
        try {
            return MenuNodeList().get(Name).ActionExternal.GetArguments();
        } catch (Exception e) {
            LOG.debug("GetMenuItemActionExternalArguments ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }
    public static String GetMenuItemActionExternalWindowType(String Name){
        try {
            return MenuNodeList().get(Name).ActionExternal.GetWindowType();
        } catch (Exception e) {
            LOG.debug("GetMenuItemActionExternalWindowType ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }
    public static String GetMenuItemActionExternalWaitForExit(String Name){
        try {
            return MenuNodeList().get(Name).ActionExternal.GetWaitForExit();
        } catch (Exception e) {
            LOG.debug("GetMenuItemActionExternalWaitForExit ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }
    public static String GetMenuItemActionExternalSageStatus(String Name){
        try {
            return MenuNodeList().get(Name).ActionExternal.GetSageStatus();
        } catch (Exception e) {
            LOG.debug("GetMenuItemActionExternalSageStatus ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemActionExternalApplication(String Name, String Setting){
        //change the setting to the next +1, previous -1, or dont change it and then save
        MenuNodeList().get(Name).ActionExternal.SetApplication(Setting);
    }
    public static void SetMenuItemActionExternalArguments(String Name, String Setting){
        //change the setting to the next +1, previous -1, or dont change it and then save
        MenuNodeList().get(Name).ActionExternal.SetArguments(Setting);
    }
    public static void SetMenuItemActionExternalWindowType(String Name, Integer Delta){
        //change the setting to the next +1, previous -1, or dont change it and then save
        MenuNodeList().get(Name).ActionExternal.ChangeWindowType(Delta);
    }
    public static void SetMenuItemActionExternalWaitForExit(String Name){
        //change the setting to the next +1, previous -1, or dont change it and then save
        MenuNodeList().get(Name).ActionExternal.ChangeWaitForExit();
    }
    public static void SetMenuItemActionExternalSageStatus(String Name, Integer Delta){
        //change the setting to the next +1, previous -1, or dont change it and then save
        MenuNodeList().get(Name).ActionExternal.ChangeSageStatus(Delta);
    }
    
    private void SetBGImageFileandPath(String bBGImageFile){
        //see if using a GlobalVariable from a Theme or a path to an image file
        BGImageFile = bBGImageFile;
        BGImageFilePath = ADMutil.GetSageBGFile(bBGImageFile);
        //LOG.debug("SetBGImageFileandPath for '" + Name + "' BGImageFile = '" + BGImageFile + "' BGImageFilePath = '" + BGImageFilePath + "'");
    }
    
    public static String GetMenuItemBGImageFileButtonText(String Name){
        return ADMutil.GetSageBGButtonText(MenuNodeList().get(Name).BGImageFile);
    }
    
    public static String GetMenuItemBGImageFile(String Name){
        if(MenuNodeList().get(Name).BGImageFile==null){
            return ADMutil.ListNone;
        }else{
            try {
                return MenuNodeList().get(Name).BGImageFile;
            } catch (Exception e) {
                LOG.debug("GetMenuItemBGImageFile ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
                return null;
            }
        }
    }

    public static void BGImageCacheClear(){
        //clear the memory cache
        BGCache.clear();
        //clear the file system cache
        ClearBGFileSystemCache();
    }
    
    public static void ClearBGFileSystemCache(){
        File CacheLoc = ImageUtil.getImageCacheDir();
        File GemCache = new File(CacheLoc, Const.CreateBGImageTag);
        File[] files = GemCache.listFiles();
        Integer counterYes = 0;
        Integer counterNo = 0;
        for (File file : files){
            if (!file.delete()){
                LOG.debug("ClearBGFileSystemCache: failed to delete '" + file + "'");
                counterNo++;
            }else{
                counterYes++;
            }
        }
        LOG.debug("ClearBGFileSystemCache: deleted " + counterYes + " of " + files.length + " files from: '" + GemCache.getPath());
        if (counterNo>0){
            LOG.debug("ClearBGFileSystemCache: FAILED to delete " + counterNo + " of " + files.length + " files from: '" + GemCache.getPath());
        }
    }
    
    public static Object GetMenuItemBGImage(String Name){
        String BGKey = GetMenuItemBGImageFilePath(Name);
        Object BGImage = null;
        if (BGKey==null){
            LOG.debug("GetMenuItemBGImage: null Key returned from GetMenuItemBGImageFilePath for '" + Name + "'");
            return null;
        }else{
            BGImage = BGCache.get(BGKey);
            if (BGImage!=null){
                //LOG.debug("GetMenuItemBGImage: BGImage found in Cache for Key '" + BGKey + "'");
                return BGImage;
            }else{
                //add it to the Cache and then return it
                BGImage = CreateBGImage(BGKey);
                if (BGImage==null){
                    LOG.debug("GetMenuItemBGImage: null image returned from CreateBGImage '" + BGKey + "'");
                }else{
                    BGCache.put(BGKey, BGImage);
                    LOG.debug("GetMenuItemBGImage: adding to BGCache '" + BGKey + "'");
                }
                return BGImage;
            }
        }
    }
    
    public static Object CreateBGImage(String Key){
        if (Key==null){
            LOG.debug("CreateBGImage: called with null Key");
            return null;
        }
        Object ThisImage = null;
        //See if the image is already cached in the filesystem by a previous CreateBGImage call
        ThisImage = phoenix.image.GetImage(Key, Const.CreateBGImageTag);
        if (ThisImage!=null){
            LOG.debug("CreateBGImage: Filesystem cached item found for Tag '" + Const.CreateBGImageTag + "' ID '" + Key + "' ThisImage = '" + ThisImage + "'");
            return ThisImage;
        }
        
        //if we got this far then the Image was not in the FileSystem Cache
        UIContext UIc = new UIContext(sagex.api.Global.GetUIContextName());
        Integer UIWidth = sagex.api.Global.GetFullUIWidth(UIc);
        Double scalewidth = 1.0;
        Double finalscalewidth = scalewidth * UIWidth;
        try {
            //ThisImage = phoenix.image.CreateImage(Key, CreateImageTag, Key, "{name: scale, width: " + finalscalewidth + ", height: -1}", true);
            ThisImage = phoenix.image.CreateImage(Key, Const.CreateBGImageTag, Key, "{name: dummy}", true);
            LOG.debug("CreateBGImage: Image = '" + ThisImage + "' for Key '" + Key + "'");
        } catch (Exception e) {
            LOG.debug("CreateBGImage: phoenix.image.CreateImage FAILED - finalscalewidth = '" + finalscalewidth + "' for Image = '" + Key + "' Error: '" + e + "'");
            return null;
        }
        if (!sagex.api.Utility.IsImageLoaded(UIc, ThisImage)){
            LOG.debug("CreateBGImage: Loaded using LoagImage(loadImage)) - finalscalewidth = '" + finalscalewidth + "' for Image = '" + Key + "'");
            sagex.api.Utility.LoadImage(UIc, sagex.api.Utility.LoadImage(UIc, ThisImage));
        }else{
            sagex.api.Utility.UnloadImage(UIc, ThisImage.toString());
            sagex.api.Utility.LoadImage(UIc, sagex.api.Utility.LoadImage(UIc, ThisImage));
            LOG.debug("CreateBGImage: already Loaded - finalscalewidth = '" + finalscalewidth + "' for Image = '" + Key + "'");
        }
        return ThisImage;
    }
    
    //TODO: Create New function that uses this as a Key to lookup the background in a SoftHashMap Cache
    public static String GetMenuItemBGImageFilePath(String Name){
        //LOG.debug("GetMenuItemBGImageFilePath for '" + Name + "' returning '" + MenuNodeList().get(Name).BGImageFilePath + "'");
        try {
            if (MenuNodeList().get(Name).BGImageFilePath==null){
                if (MenuNodeList().get(Name).Parent.equals(ADMutil.TopMenu)){
                    return null;
                }else{
                    return GetMenuItemBGImageFilePath(MenuNodeList().get(Name).Parent);
                }
            }else{
                //changed to ensure if a variable it get re-evaluated if a new theme is loaded
                return ADMutil.GetSageBGFile(MenuNodeList().get(Name).BGImageFile);
                //return MenuNodeList().get(Name).BGImageFilePath;
            }
        } catch (Exception e) {
            LOG.debug("GetMenuItemBGImageFilePath ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemBGImageFile(String Name, String Setting){
        if (Setting.equals(ADMutil.ListNone) || Setting==null){
            Save(Name, "BGImageFile", ADMutil.ListNone);
        }else{
            Save(Name, "BGImageFile", Setting);
        }
        MenuNodeList().get(Name).SetBGImageFileandPath(Setting);
    }

    public static String GetMenuItemButtonText(String Name){
        //LOG.debug("GetMenuItemButtonText: Name '" + Name + "' NodeListCount = '" + (MenuNode) UIMenuNodeList.get("SAGETV_PROCESS_LOCAL_UI").get(Name) + "' root = '" + UIroot.get("SAGETV_PROCESS_LOCAL_UI").getChildCount() + "'");
        if (Name.equals(ADMutil.TopMenu)){
            return "Top Level";
        }else{
            try {
                return MenuNodeList().get(Name).ButtonText;
            } catch (Exception e) {
                LOG.debug("GetMenuItemButtonText ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
                return null;
            }
        }
    }

    public static String GetMenuItemButtonTextNewTest(String Name){
        if (MenuNodeList().get(Name).ButtonText.equals(ADMutil.ButtonTextDefault)){
            return "";
        }else{
            return GetMenuItemButtonText(Name);
        }
    }

    public static void SetMenuItemButtonText(String Name, String Setting){
        Save(Name, "ButtonText", Setting);
    }

    public static Boolean GetMenuItemHasSubMenu(String Name){
        try {
            return !MenuNodeList().get(Name).NodeItem.isLeaf();
        } catch (Exception e) {
            LOG.debug("GetMenuItemHasSubMenu ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static ADMutil.TriState GetMenuItemIsActive(String Name){
        try {
            return MenuNodeList().get(Name).IsActive;
        } catch (Exception e) {
            LOG.debug("GetMenuItemIsActive ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static String GetMenuItemIsActiveIncludingParentFormatted(String Name){
        try {
            if (MenuNodeList().get(Name).IsActive.equals(ADMutil.TriState.NO)){
                return "No";
            }else if (MenuNodeList().get(Name).IsActive.equals(ADMutil.TriState.OTHER)){
                return "User Based";
            }else if (GetMenuItemIsActiveIncludingParent(Name).equals(ADMutil.TriState.YES)){
                return "Yes";
            }else if (GetMenuItemIsActiveIncludingParent(Name).equals(ADMutil.TriState.OTHER)){
                return "Yes (Parent: User Based)";
            }
            // in this case this item is active BUT the parent is not
            return "Yes (Parent: No)";
        } catch (Exception e) {
            LOG.debug("GetMenuItemIsActiveIncludingParentFormatted ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }
    
    public static ADMutil.TriState GetMenuItemIsActiveIncludingParent(String Name){
        try {
            if (MenuNodeList().get(Name).IsActive.equals(ADMutil.TriState.NO)){
                return ADMutil.TriState.NO;
            }else if (MenuNodeList().get(Name).IsActive.equals(ADMutil.TriState.OTHER)){
                return ADMutil.TriState.OTHER;
            }
            TreeNode[] path = MenuNodeList().get(Name).NodeItem.getPath();
            for (TreeNode pathnode : path){
                DefaultMutableTreeNode pathnodea = (DefaultMutableTreeNode)pathnode;
                ADMMenuNode tMenu = (ADMMenuNode)pathnodea.getUserObject();
                //LOG.debug("GetMenuItemIsActiveIncludingParent for '" + Name + "' for item = '" + tMenu.ButtonText + "'");
                if (tMenu.IsActive.equals(ADMutil.TriState.NO)){
                    //LOG.debug("GetMenuItemIsActiveIncludingParent for '" + Name + "' for item = '" + tMenu.ButtonText + "' = NO");
                    return ADMutil.TriState.NO;
                }else if(tMenu.IsActive.equals(ADMutil.TriState.OTHER)){
                    //LOG.debug("GetMenuItemIsActiveIncludingParent for '" + Name + "' for item = '" + tMenu.ButtonText + "' = OTHER");
                    return ADMutil.TriState.OTHER;
                }
            }
            //LOG.debug("GetMenuItemIsActiveIncludingParent for '" + Name + "' YES");
            return ADMutil.TriState.YES;
        } catch (Exception e) {
            LOG.debug("GetMenuItemIsActiveIncludingParent ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemIsActive(String Name, ADMutil.TriState Setting){
        Save(Name, "IsActive", Setting.toString());
    }

    public static void ChangeMenuItemIsActive(String Name){
        try {
            if(MenuNodeList().get(Name).IsActive.equals(ADMutil.TriState.YES)){
                Save(Name, "IsActive", ADMutil.TriState.NO.toString());
            }else if(MenuNodeList().get(Name).IsActive.equals(ADMutil.TriState.NO)){
                Save(Name, "IsActive", ADMutil.TriState.OTHER.toString());
            }else{
                Save(Name, "IsActive", ADMutil.TriState.YES.toString());
            }
        } catch (Exception e) {
        }
    }

    public static List<String> GetMenuItemBlockedSageUsersListAsList(String Name){
        try {
            return MenuNodeList().get(Name).BlockedSageUsersList;
        } catch (Exception e) {
            return new LinkedList<String>();
        }
    }
    
    public static String GetMenuItemBlockedSageUsersList(String Name){
        try {
            return ADMutil.ConvertListtoString(MenuNodeList().get(Name).BlockedSageUsersList);
        } catch (Exception e) {
            return "";
        }
    }
    
    public static List<String> GetSageUsersList(){
        //return a list of SageUsers in sorted order with the Administrator removed
        List<String> ProfileList = new LinkedList<String>();
        ProfileList.addAll(Arrays.asList(sagex.api.Security.GetSecurityProfiles(new UIContext(sagex.api.Global.GetUIContextName()))));
        ProfileList.remove(SageUserAdministrator);
        Collections.sort(ProfileList);
        return ProfileList;
    }
    
    public static Integer GetSageUsersListCount(){
        //return a count of SageUsers with the Administrator removed
        return sagex.api.Security.GetSecurityProfiles(new UIContext(sagex.api.Global.GetUIContextName())).length - 1;
    }
    
    public static Integer GetSageUsersBlockedListCount(String Name){
        Integer tInt = 0;
        for (String Item : sagex.api.Security.GetSecurityProfiles(new UIContext(sagex.api.Global.GetUIContextName()))){
            try {
                if (MenuNodeList().get(Name).BlockedSageUsersList.contains(Item)){
                    tInt++;
                }
            } catch (Exception e) {
                //do nothing
            }
        }
        return tInt;
    }
    
    //use a Tokenized String to list all the users that are NOT allowed to use this menu item
    //if a user is not in the String List then it is assumed they are ALLOWED to use the menu item
    //therefore - new Users created in Sage will have access to all menu items until removed specifically
    public static Boolean IsSageUserAllowed(String Name, String SageUser){
        try {
            if (SageUser.equals(SageUserAdministrator)){
                return Boolean.TRUE;
            }else{
                //if the SageUser is NOT in the list then the user is ALLOWED
                return !MenuNodeList().get(Name).BlockedSageUsersList.contains(SageUser);
            }
        } catch (Exception e) {
            LOG.debug("IsSageUserAllowed ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }
    
    public static void ChangeSageUserAllowed(String Name, String SageUser){
        if (!SageUser.equals(SageUserAdministrator)){
            try {
                //if the user is in the blocked list then remove it from the list otherwise add it
                if (MenuNodeList().get(Name).BlockedSageUsersList.contains(SageUser)){
                    MenuNodeList().get(Name).BlockedSageUsersList.remove(SageUser);
                }else{
                    MenuNodeList().get(Name).BlockedSageUsersList.add(SageUser);
                }
                Save(Name, "BlockedSageUsersList", ADMutil.ConvertListtoString(MenuNodeList().get(Name).BlockedSageUsersList));
            } catch (Exception e) {
                LOG.debug("ChangeSageUserAllowed ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            }
        }
    }
    
    public static Boolean GetMenuItemIsCreatedNotLoaded(String Name){
        try {
            return MenuNodeList().get(Name).IsCreatedNotLoaded;
        } catch (Exception e) {
            LOG.debug("GetMenuItemIsCreatedNotLoaded ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemIsCreatedNotLoaded(String Name, Boolean Setting){
        MenuNodeList().get(Name).IsCreatedNotLoaded = Setting;
    }
    
    public static Boolean GetMenuItemIsTemp(String Name){
        try {
            return MenuNodeList().get(Name).IsTemp;
        } catch (Exception e) {
            LOG.debug("SetMenuItemIsCreatedNotLoaded ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemIsTemp(String Name, Boolean Setting){
        Save(Name, "IsTemp", Setting.toString());
    }
    
    public static Boolean GetMenuItemIsDefault(String Name){
        try {
            return MenuNodeList().get(Name).IsDefault;
        } catch (Exception e) {
            LOG.debug("SetMenuItemIsTemp ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemIsDefault(String Name, Boolean Setting){
        //LOG.debug("SetMenuItemIsDefault: Name '" + Name + "' Setting '" + Setting + "'");
        if (Setting==Boolean.TRUE){
            //first clear existing Default settings for Menu Items with the same parent 
            ClearSubMenuDefaults(MenuNodeList().get(Name).Parent);
            Save(Name, "IsDefault", Setting.toString());
            LOG.debug("SetMenuItemIsDefault: true Name '" + Name + "' Setting '" + Setting + "'");
        }else{
            //LOG.debug("SetMenuItemIsDefault: false Name '" + Name + "' Setting '" + Setting + "'");
            Save(Name, "IsDefault", Setting.toString());
            //ensure at least 1 item remaining is a default
            ValidateSubMenuDefault(MenuNodeList().get(Name).Parent);
        }
    }

    @SuppressWarnings("unchecked")
    public static void ValidateSubMenuDefault(String bParent){
        //ensure that 1 and only 1 item is set as the default
        Boolean FoundDefault = Boolean.FALSE;
        
        if (MenuNodeList().get(bParent).NodeItem.getChildCount()>0){

            Enumeration<DefaultMutableTreeNode> en = MenuNodeList().get(bParent).NodeItem.children();
            while (en.hasMoreElements())   {
                DefaultMutableTreeNode child = en.nextElement();
                ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
                if (tMenu.IsDefault){
                    if (FoundDefault){
                    //Save setting
                        Save(tMenu.Name, "IsDefault", Boolean.FALSE.toString());
                    }else{
                        FoundDefault = Boolean.TRUE;
                    }
                }
            }         
            if (!FoundDefault){
                //no default found so set the first item as the default
                DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode)MenuNodeList().get(bParent).NodeItem.getFirstChild();
                ADMMenuNode tMenu = (ADMMenuNode)firstChild.getUserObject();
                LOG.debug("ValidateSubMenuDefault for '" + bParent + "' : no Default found so setting first = '" + tMenu.Name + "'");
                Save(tMenu.Name, "IsDefault", Boolean.TRUE.toString());
            }else {
                LOG.debug("ValidateSubMenuDefault for '" + bParent + "' : Default already set");
            }
            //As there are submenu items the submenu setting should be null
            SetMenuItemSubMenu(bParent, ADMutil.ListNone);
        }else{
            //no subMenu items so make sure this parent's SubMenu settings are correct
            SetMenuItemSubMenu(bParent, ADMutil.ListNone);
            LOG.debug("ValidateSubMenuDefault for '" + bParent + "' : no SubMenu items found");
        }
    }
        
    //clear all defaults for a submenu - used prior to setting a new default to ensure there is not more than one
    @SuppressWarnings("unchecked")
    public static void ClearSubMenuDefaults(String bParent){
        if (MenuNodeList().get(bParent).NodeItem.getChildCount()>0){
            Enumeration<DefaultMutableTreeNode> en = MenuNodeList().get(bParent).NodeItem.children();
            while (en.hasMoreElements())   {
                DefaultMutableTreeNode child = en.nextElement();
                ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
                if (tMenu.IsDefault){
                    //Save setting
                    Save(tMenu.Name, "IsDefault", Boolean.FALSE.toString());
                }
            }         
        }
        LOG.debug("ClearSubMenuDefaults for '" + bParent + "' '" + MenuNodeList().get(bParent).NodeItem.getChildCount() + "' cleared");
    }
    
    @SuppressWarnings("unchecked")
    public static String GetSubMenuDefault(String bParent){
        String FirstChildName = ADMutil.OptionNotFound;
        if (MenuNodeList().get(bParent).NodeItem.getChildCount()>0){
            Enumeration<DefaultMutableTreeNode> en = MenuNodeList().get(bParent).NodeItem.children();
            while (en.hasMoreElements())   {
                DefaultMutableTreeNode child = en.nextElement();
                ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
                if (FirstChildName.equals(ADMutil.OptionNotFound)){
                    FirstChildName = tMenu.Name;
                }
                //for Dynamic Lists skip the item as you don't want to consider the Dynamic list item as the default as it does not display
                if (tMenu.ActionType.equals(ADMAction.DynamicList)){
                    //skip
                }else{
                    if (tMenu.IsDefault){
                        LOG.debug("GetSubMenuDefault for '" + bParent + "' Default = '" + tMenu.Name + "'");
                        return tMenu.Name;
                    }
                }
            }         
        }
        //if you get here then NOT FOUND so find the first item and return it 
        if (FirstChildName.equals(ADMutil.OptionNotFound)){
            LOG.debug("GetSubMenuDefault for '" + bParent + "' - none found");
            return "";
        }else{
            if (GetMenuItemActionType(FirstChildName).equals(ADMAction.DynamicList)){
                LOG.debug("GetSubMenuDefault for '" + bParent + "' - not found so returning Dynamic Lists FirstChild '" + FirstChildName + "'");
                return GetSubMenuFirstChild(FirstChildName);
            }else{
                LOG.debug("GetSubMenuDefault for '" + bParent + "' - not found so returning FirstChild '" + FirstChildName + "'");
                return FirstChildName;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static String GetSubMenuFirstChild(String bParent){
        String FirstChildName = ADMutil.OptionNotFound;
        if (MenuNodeList().get(bParent).NodeItem.getChildCount()>0){
            Enumeration<DefaultMutableTreeNode> en = MenuNodeList().get(bParent).NodeItem.children();
            while (en.hasMoreElements())   {
                DefaultMutableTreeNode child = en.nextElement();
                ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
                FirstChildName = tMenu.Name;
                break;
            }         
        }
        if (FirstChildName.equals(ADMutil.OptionNotFound)){
            LOG.debug("GetSubMenuFirstChild for '" + bParent + "' - none found");
            return "";
        }else{
            LOG.debug("GetSubMenuFirstChild for '" + bParent + "' - FirstChild '" + FirstChildName + "'");
            return FirstChildName;
        }
    }

    public static Integer GetMenuItemLevel(String Name){
        try {
            return MenuNodeList().get(Name).NodeItem.getLevel();
        } catch (Exception e) {
            LOG.debug("GetMenuItemLevel ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemName(String Name){
        Save(Name, "Name", Name);
    }

    public static String GetMenuItemParent(String Name){
        //get the parent from the Tree structure
        if (Name.equals(ADMutil.TopMenu)){
            //LOG.debug("GetMenuItemParent for '" + Name + "' returning null");
            return null;
        }else{
            //LOG.debug("GetMenuItemParent for '" + Name + "' = '" + MenuNodeList().get(Name).NodeItem.getParent().toString() + "'");
            try {
                return MenuNodeList().get(Name).NodeItem.getParent().toString();
            } catch (Exception e) {
                LOG.debug("GetMenuItemParent ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
                return null;
            }
        }
    }

    //moves the MenuNode to another parent if valid
    public static void SetMenuItemParent(String Name, String NewParent){
        //make sure the parent is not the MenuItem itself
        if(Name.equals(NewParent) || NewParent.equals(MenuNodeList().get(Name).Parent) || Name.equals(ADMutil.TopMenu)){
            //do nothing as changing the parent here is invalid
        }else{
            String OldParent = MenuNodeList().get(Name).NodeItem.getParent().toString();
            
            MenuNodeList().get(OldParent).NodeItem.remove(MenuNodeList().get(Name).NodeItem);
            MenuNodeList().get(NewParent).NodeItem.add(MenuNodeList().get(Name).NodeItem);
            Save(Name, "Parent", NewParent);

            //update the sort keys for the old and new parents
            SortKeyUpdate(MenuNodeList().get(OldParent).NodeItem);
            SortKeyUpdate(MenuNodeList().get(NewParent).NodeItem);

            //check the new parent and set it's SubMenu properly
            if (!NewParent.equals(ADMutil.TopMenu)){
                SetMenuItemSubMenu(NewParent,ADMutil.ListNone);
            }
            
            //make sure the old and new SubMenus have a single default item
            ValidateSubMenuDefault(OldParent);
            ValidateSubMenuDefault(NewParent);
            LOG.debug("SetMenuItemParent: Parent changed for '" + Name + "' to = '" + NewParent + "'");
        }
    }
    
    public static String GetMenuItemShowIF(String Name){
        if (Name.equals(ADMutil.TopMenu)){
            return "";
        }else{
            try {
                return MenuNodeList().get(Name).ShowIF;
            } catch (Exception e) {
                LOG.debug("GetMenuItemShowIF ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
                return null;
            }
        }
    }

    public static Integer GetMenuItemSortKey(String Name){
        try {
            return MenuNodeList().get(Name).SortKey;
        } catch (Exception e) {
            LOG.debug("GetMenuItemSortKey ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public void setSortKey(String SortKey) {
        Integer tSortKey = 0;
        try {
            tSortKey = Integer.valueOf(SortKey);
        } catch (NumberFormatException ex) {
            LOG.debug("setSortKey: error converting '" + SortKey + "' " + ADMutil.class.getName() + ex);
            tSortKey = SortKeyCounter++;
        }
        this.SortKey = tSortKey;
    }
    
    public static void ChangeSortOrder(String Name, Integer aDelta){
        if (moveNode(MenuNodeList().get(Name).NodeItem, aDelta)){
            SortKeyUpdate((DefaultMutableTreeNode)MenuNodeList().get(Name).NodeItem.getParent());
            LOG.debug("ChangeSortOrder: moving '" + Name + "' by '" + aDelta.toString() + "'");
        }else{
            LOG.debug("ChangeSortOrder: NOT ABLE to move '" + Name + "' by '" + aDelta.toString() + "'");
        }
    }
    
    public static Boolean moveNode( DefaultMutableTreeNode Node, int aDelta ){
        if ( null == Node ) return Boolean.FALSE;                         // No node selected   
        DefaultMutableTreeNode lParent = (DefaultMutableTreeNode)Node.getParent();   
        if ( null == lParent ) return Boolean.FALSE;                      // Cannot move the Root!   
        int lOldIndex = lParent.getIndex( Node );   
        int lNewIndex = lOldIndex + aDelta;   
        if ( lNewIndex < 0 ) return Boolean.FALSE;                        // Cannot move first child up   
        if ( lNewIndex >= lParent.getChildCount() ) return Boolean.FALSE; // Cannot move last child down   
        lParent.remove(Node);   
        lParent.insert( Node, lNewIndex );  
        return  Boolean.TRUE;
    }  
    
    public static void SortKeyUpdate(){
        SortKeyUpdate(root());
    }

    public static void SortKeyUpdate(String bParent){
        SortKeyUpdate(MenuNodeList().get(bParent).NodeItem);
    }
    
    @SuppressWarnings("unchecked")
    public static void SortKeyUpdate(DefaultMutableTreeNode aParent){
        //update all the sortkey values to the index starting from the aParent
        Enumeration<DefaultMutableTreeNode> en;
        if (aParent.equals(root())){
            en = aParent.preorderEnumeration();
        }else{
            en = aParent.children();
        }
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
            if (!tMenu.Name.equals(ADMutil.TopMenu)){
                tMenu.SortKey = child.getParent().getIndex(child);
                Save(tMenu.Name, "SortKey", tMenu.SortKey.toString());
                //LOG.debug("SortKeyUpdate: Child = '" + child + "' SortKey = '" + tMenu.SortKey + "' Parent = '" + child.getParent() + "'"  );
            }
        }         
        LOG.debug("SortKeyUpdate: completed for Parent = '" + aParent + "'"  );
    }
    
    //the SubMenu field is only filled in if using a built in Sage SubMenu
    // otherwise, the Name of the MenuItem is returned if the MenuItem has a SubMenu
    public String getSubMenu() {
        if (SubMenu==null){
            if (!NodeItem.isLeaf()){
                return Name;
            }else{
                return SubMenu;
            }
        }else{
            return SubMenu;
        }
    }

    public String getSubMenuExcludingSageMenus() {
        if (!NodeItem.isLeaf()){
            return Name;
        }else{
            return null;
        }
    }

    //special case so SageSubMenus do not get displayed
    public static String GetMenuItemSubMenuQLM(String Name){
        try {
            return MenuNodeList().get(Name).getSubMenuExcludingSageMenus();
        } catch (Exception e) {
            LOG.debug("GetMenuItemSubMenuQLM ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static String GetMenuItemSubMenu(String Name){
        try {
            return MenuNodeList().get(Name).getSubMenu();
        } catch (Exception e) {
            LOG.debug("GetMenuItemSubMenu ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static String GetMenuItemSubMenuButtonText(String Name){
        try {
            return ADMutil.GetSubMenuListButtonText(MenuNodeList().get(Name).getSubMenu(), GetMenuItemLevel(Name));
        } catch (Exception e) {
            LOG.debug("GetMenuItemSubMenuButtonText ERROR: Value not available for '" + Name + "' Exception = '" + e + "'");
            return null;
        }
    }

    public static void SetMenuItemSubMenu(String Name, String Setting){
        //LOG.debug("SetMenuItemSubMenu for '" + Name + "' Setting = '" + Setting + "'");
        if (Setting.equals(ADMutil.ListNone) || Setting==null){
            //set the SubMenu field
            Save(Name, "SubMenu", null);
        }else{
            //set the SubMenu field
            Save(Name, "SubMenu", Setting);
        }
    }

    public static Boolean IsSubMenuItem(String bParent, String Item){
        return IsSubMenuItem(bParent, Item, Boolean.FALSE);
    }
    
    public static Boolean IsSubMenuItem(String bParent, String Item, Boolean QLMCheck){
        //check if Item is a child of bParent
        if (bParent==null || Item==null || !MenuNodeList().containsKey(Item)){
            //LOG.debug("IsSubMenuItem for Parent = '" + bParent + "' Item '" + Item + "' NOT found or null");
            return Boolean.FALSE;
        }
        try {
            if (MenuNodeList().get(Item).IsTemp){
                //as Temp items are not directly related to the Parent passed in then return TRUE as it is in the list as above
                return Boolean.TRUE;
            }else if (MenuNodeList().get(Item).NodeItem.getParent().toString().equals(bParent)){
                if (QLMCheck){
                    //make sure the item is to show in QLM - NO SageSubMenus
                    if (MenuNodeList().get(Item).SubMenu==null || MenuNodeList().get(Item).SubMenu.equals(MenuNodeList().get(Item).Name)){
                        return Boolean.TRUE;
                    }else{
                        return Boolean.FALSE;
                    }
                }else{
                    //LOG.debug("IsSubMenuItem for Parent = '" + bParent + "' Item '" + Item + "' found");
                    return Boolean.TRUE;
                }
            }else{
                //LOG.debug("IsSubMenuItem for Parent = '" + bParent + "' Item '" + Item + "' NOT found");
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            LOG.debug("IsSubMenuItem ERROR: Value not available for '" + bParent + "' Exception = '" + e + "'");
            return null;
        }
    }

    //returns the full list of ALL menu items regardless of parent
    public static Collection<String> GetMenuItemNameList(){
        try {
            return MenuNodeList().keySet();
        } catch (Exception e) {
            LOG.debug("GetMenuItemNameList ERROR: Value not available. Exception = '" + e + "'");
            return null;
        }
    }
    
    //returns only menu items for a specific parent that are active and have no SageSubMenu items
    public static Collection<String> GetMenuItemNameListQLM(String Parent){
        return GetMenuItemNameList(Parent, Boolean.FALSE, Boolean.TRUE);
    }

    //returns only menu items for a specific parent that are active
    public static Collection<String> GetMenuItemNameList(String Parent){
        return GetMenuItemNameList(Parent, Boolean.FALSE);
    }

    public static Collection<String> GetMenuItemNameList(String Parent, Boolean IncludeInactive){
        return GetMenuItemNameList(Parent, Boolean.FALSE, Boolean.FALSE);
    }
    
    //returns menu items for a specific parent
    @SuppressWarnings("unchecked")
    public static Collection<String> GetMenuItemNameList(String Parent, Boolean IncludeInactive, Boolean QLMCheck){
        Collection<String> bNames = new LinkedHashSet<String>();
        String tActiveUser = sagex.api.Security.GetActiveSecurityProfile(new UIContext(sagex.api.Global.GetUIContextName()));
        if (MenuNodeList().containsKey(Parent) && MenuNodeList().get(Parent).NodeItem!=null){
            Enumeration<DefaultMutableTreeNode> en = MenuNodeList().get(Parent).NodeItem.children();
            while (en.hasMoreElements())   {
                DefaultMutableTreeNode child = en.nextElement();
                ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
                if (IncludeInactive==true){
                    AddMenuItemtoList(bNames, tMenu);
                }else if(tMenu.IsActive.equals(ADMutil.TriState.YES)){
                    if (QLMCheck && QLMInvalidSubmenu(tMenu)){
                        //do not add this item
                    }else{
                        AddMenuItemtoList(bNames, tMenu);
                    }
                }else if(tMenu.IsActive.equals(ADMutil.TriState.OTHER)){
                    //only add if the active user is allowed to see this Menu Item
                    if (!tMenu.BlockedSageUsersList.contains(tActiveUser)){
                        if (QLMCheck && QLMInvalidSubmenu(tMenu)){
                            //do not add this item
                        }else{
                            AddMenuItemtoList(bNames, tMenu);
                        }
                    }
                }
                //otherwise do not add the Menu Item
            }         
        }
        LOG.debug("GetMenuItemNameList for '" + Parent + "' : IncludeInactive = '" + IncludeInactive.toString() + "' " + bNames);
        return bNames;
    }
    
    private static void AddMenuItemtoList(Collection<String> bNames, ADMMenuNode tMenu ){
        if (tMenu.ActionType.equals(ADMAction.DynamicList)){
            //for Dynamic Lists get the list and add an item for each
            for (String tItem : ADMAction.GetDynamicListItems(tMenu.Name, tMenu.ActionAttribute)){
                bNames.add(tItem);
            }
        }else{
            bNames.add(tMenu.Name);
        }
    }

    public static Collection<String> GetMenuList(Integer Level){
        String UIContext = sagex.api.Global.GetUIContextName();
        if (Level==1){
            if (!UIMenuListLevel1.containsKey(UIContext)){
                return new LinkedList<String>();
            }else{
                return UIMenuListLevel1.get(UIContext);
            }
        }else if (Level==2){
            if (!UIMenuListLevel2.containsKey(UIContext)){
                return new LinkedList<String>();
            }else{
                return UIMenuListLevel2.get(UIContext);
            }
        }else if (Level==3){
            if (!UIMenuListLevel3.containsKey(UIContext)){
                return new LinkedList<String>();
            }else{
                return UIMenuListLevel3.get(UIContext);
            }
        }
        return new LinkedList<String>();
    }
    
    public static Collection<String> GetMenuListQLM(){
        String UIContext = sagex.api.Global.GetUIContextName();
        if (!UIMenuListQLM.containsKey(UIContext)){
            return new LinkedList<String>();
        }else{
            return UIMenuListQLM.get(UIContext);
        }
    }
    
    public static void MenuBeforeOpen(Integer Level, String MenuName){
        LOG.debug("MenuBeforeOpen: Level '" + Level + "' MenuName '" + MenuName + "'");
        //store the Menu for this Level for later retrieval while the menu is open
        //cleanup previous Temp Menu Items if any
        //DeleteAllTempMenuItems();
        String UIContext = sagex.api.Global.GetUIContextName();
        if (Level==1){
            if (!UIMenuListLevel1.containsKey(UIContext)){
                UIMenuListLevel1.put(UIContext, new LinkedHashSet<String>());
            }
            DeleteAllTempMenuItems(1);
            UIMenuListLevel1.get(UIContext).clear();
            UIMenuListLevel1.get(UIContext).addAll(GetMenuItemNameList(MenuName));
        }else if (Level==2){
            if (!UIMenuListLevel2.containsKey(UIContext)){
                UIMenuListLevel2.put(UIContext, new LinkedHashSet<String>());
            }
            DeleteAllTempMenuItems(2);
            UIMenuListLevel2.get(UIContext).clear();
            UIMenuListLevel2.get(UIContext).addAll(GetMenuItemNameList(MenuName));
        }else if (Level==3){
            if (!UIMenuListLevel3.containsKey(UIContext)){
                UIMenuListLevel3.put(UIContext, new LinkedHashSet<String>());
            }
            DeleteAllTempMenuItems(3);
            UIMenuListLevel3.get(UIContext).clear();
            UIMenuListLevel3.get(UIContext).addAll(GetMenuItemNameList(MenuName));
        }
    }

    public static void MenuAfterClose(Integer Level){
        LOG.debug("MenuAfterClose: Level '" + Level + "'");
        //clear the Menus for this Level and delete any TEMP Menu items
        
    }
    public static void MenuBeforeOpenQLM(String MenuName){
        LOG.debug("MenuBeforeOpenQLM: MenuName '" + MenuName + "'");
        //cleanup previous Temp Menu Items if any
        DeleteAllTempMenuItems();
        String UIContext = sagex.api.Global.GetUIContextName();
        //store the QLM Menu for later retrieval while the menu is open
        if (!UIMenuListQLM.containsKey(UIContext)){
            UIMenuListQLM.put(UIContext, new LinkedHashSet<String>());
        }
        UIMenuListQLM.get(UIContext).clear();
        UIMenuListQLM.get(UIContext).addAll(GetMenuItemNameListQLM(MenuName));
    }
    public static void MenuAfterCloseQLM(){
        LOG.debug("MenuAfterCloseQLM:");
        //clear the QLM Menu and delete any TEMP Menu items
        
    }
    
    //TODO: EXTERNAL MENU - Delete Temp Menu Items
    private static void DeleteAllTempMenuItems(){
        List<String> TempItems = new LinkedList<String>();
        //Get Temp Items for deletion
        for (ADMMenuNode tMenu : MenuNodeList().values()){
            if (tMenu.IsTemp){
                TempItems.add(tMenu.Name);
                tMenu.NodeItem.removeFromParent();
                //LOG.debug("DeleteAllTempMenuItems for '" + tMenu.ButtonText + "' : Name = '" + tMenu.Name + "'");
            }
        }
        String PropLocation = "";
        for (String TempItem : TempItems){
            MenuNodeList().remove(TempItem);
            //remove them from the SageTV Properties
            PropLocation = ADMutil.SagePropertyLocation + TempItem;
            ADMutil.RemovePropertyAndChildren(PropLocation);
        }
        LOG.debug("DeleteAllTempMenuItems : Deleted '" + TempItems.size() + "' items");
    }
    //TODO: EXTERNAL MENU - Delete Temp Menu Items
    private static void DeleteAllTempMenuItems(Integer Level){
        List<String> TempItems = new LinkedList<String>();
        //Get Temp Items for deletion
        for (ADMMenuNode tMenu : MenuNodeList().values()){
            if (tMenu.IsTemp){
                if (tMenu.NodeItem.getLevel()==Level+1){
                    TempItems.add(tMenu.Name);
                    tMenu.NodeItem.removeFromParent();
                    //LOG.debug("DeleteAllTempMenuItems for Level '" + Level + "' '" + tMenu.ButtonText + "' : Name = '" + tMenu.Name + "'");
                }else{
                    //LOG.debug("DeleteAllTempMenuItems for Level '" + Level + "' Skipping as different Level '" + tMenu.ButtonText + "' : Name = '" + tMenu.Name + "' Level = '" + (GetMenuItemLevel(tMenu.Name)-1) + "'");
                }
            }
        }
        String PropLocation = "";
        for (String TempItem : TempItems){
            MenuNodeList().remove(TempItem);
            //remove them from the SageTV Properties
            PropLocation = ADMutil.SagePropertyLocation + TempItem;
            ADMutil.RemovePropertyAndChildren(PropLocation);
        }
        LOG.debug("DeleteAllTempMenuItems for Level " + Level + " : Deleted '" + TempItems.size() + "' items");
    }
    
    private static Boolean QLMInvalidSubmenu(ADMMenuNode tMenu){
        if (tMenu.SubMenu==null || tMenu.SubMenu.equals(tMenu.Name)){
            return Boolean.FALSE;
        }else if(!tMenu.ActionType.equals(ADMAction.ActionTypeDefault)){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static Boolean QLMInvalidSubmenu(String SubMenu, String Name){
        if (SubMenu==null || SubMenu.equals(Name)){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    //returns the count of ALL MenuNode ITems
    public static int GetMenuItemCount(){
        try {
            return MenuNodeList().size();
        } catch (Exception e) {
            LOG.debug("GetMenuItemCount ERROR: Value not available. Exception = '" + e + "'");
            return 0;
        }
    }
    
    //Get the count of MenuItems for a parent that are active and do not include SageSubMenus
    public static int GetMenuItemCountQLM(){
        String UIContext = sagex.api.Global.GetUIContextName();
        if (!UIMenuListQLM.containsKey(UIContext)){
            return 0;
        }
        return UIMenuListQLM.get(UIContext).size();
    }

    //Get the count of MenuItems for a parent that are active
    public static int GetMenuItemCount(Integer Level){
        String UIContext = sagex.api.Global.GetUIContextName();
        if (Level==1){
            if (!UIMenuListLevel1.containsKey(UIContext)){
                return 0;
            }
            return UIMenuListLevel1.get(UIContext).size();
        }else if (Level==2){
            if (!UIMenuListLevel2.containsKey(UIContext)){
                return 0;
            }
            return UIMenuListLevel2.get(UIContext).size();
        }else if (Level==3){
            if (!UIMenuListLevel3.containsKey(UIContext)){
                return 0;
            }
            return UIMenuListLevel3.get(UIContext).size();
        }
        return 0;
    }

    //returns only menu items for a specific parent that are active
    @SuppressWarnings("unchecked")
    public static Collection<String> GetMenuItemSortedList(Boolean Grouped){
        Collection<String> FinalList = new LinkedHashSet<String>();
        
        Enumeration<DefaultMutableTreeNode> en;
        if (Grouped){
            //Menu Items in Level 1, then Level 2 etc
            en = root().breadthFirstEnumeration();
        }else{
            //Menu Items in Tree Order
            en = root().preorderEnumeration();
        }
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
            //add all items except the Top Level menu
            if (!tMenu.Name.equals(ADMutil.TopMenu)){
                //do not add any temp items as they should not be available in ADM Manager
                if (!tMenu.IsTemp){
                    FinalList.add(tMenu.Name);
                }
            }
        }         
        LOG.debug("GetMenuItemSortedList: Grouped = '" + Grouped.toString() + "' :" + FinalList);
        return FinalList;
    }
    
    public static Collection<String> GetParentListforMenuItem(String Name){
        if (Name.equals(GetMenuItemSubMenu(Name)) || GetMenuItemSubMenu(Name)==null){
            return GetMenuItemParentList();
        }else{
            return GetMenuItemParentList(GetMenuItemLevel(Name));
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<String> GetMenuItemParentList(){
        Collection<String> ValidParentList = new LinkedHashSet<String>();
        Enumeration<DefaultMutableTreeNode> en = root().preorderEnumeration();
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
            //only add level 1 and 2 menu items as valid parents
            if (child.getLevel()<3){
                //do not add DynamicList items as valid parents
                if (!tMenu.ActionType.equals(ADMAction.DynamicList)){
                    ValidParentList.add(tMenu.Name);
                }
            }
        }         
        LOG.debug("GetMenuItemParentList: '" + ValidParentList + "'");
        return ValidParentList;
    }
    
    //get valid parent list for only 1 specific level
    @SuppressWarnings("unchecked")
    public static Collection<String> GetMenuItemParentList(Integer SpecificLevel){
        Collection<String> ValidParentList = new LinkedHashSet<String>();
        Enumeration<DefaultMutableTreeNode> en = root().preorderEnumeration();
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
            if (child.getLevel()==SpecificLevel-1){
                //do not add DynamicList items as valid parents
                if (!tMenu.ActionType.equals(ADMAction.DynamicList)){
                    ValidParentList.add(tMenu.Name);
                }
            }
        }         
        LOG.debug("GetMenuItemParentList: for Level = '" + SpecificLevel + "' List = '" + ValidParentList + "'");
        return ValidParentList;
    }
    
    //get the specific format based on the Sort style
    public static String GetMenuItemButtonTextbyStyle(String Name, Boolean SortGrouped){
        String SubMenuText = GetMenuItemSubMenu(Name);
        if (SubMenuText!=null){
            if (SubMenuText.equals(Name)){
                SubMenuText = "";
            }else{
                SubMenuText = " <" + ADMutil.GetSubMenuListButtonText(SubMenuText, GetMenuItemLevel(Name),Boolean.TRUE) + ">";
            }
        }else{
            SubMenuText = "";
        }
        String DefaultIndicator = "";
        if (MenuNodeList().get(Name).IsDefault){
            DefaultIndicator = "* ";
        }
        if (SortGrouped){
            //return a / delimited path
            return GetMenuItemButtonTextFormatted(Name,null) + DefaultIndicator + SubMenuText;
        }else{
            //return a prefix padded string
            return GetMenuItemButtonTextFormatted(Name,"     ") + DefaultIndicator + SubMenuText;
        }
    }

    //return a path delimited by "/" or the name with prefix padding
    public static String GetMenuItemButtonTextFormatted(String Name, String PrefixPadding){
        if (PrefixPadding==null){
            if (Name.equals(ADMutil.TopMenu)){
                return MenuNodeList().get(Name).ButtonText;
            }else{
                return GetPath(MenuNodeList().get(Name).NodeItem);
            }
        }else{
            String tPadded = "" + ADMutil.repeat(PrefixPadding,MenuNodeList().get(Name).NodeItem.getLevel()-1 );
            return tPadded + MenuNodeList().get(Name).ButtonText;
        }
    }

    //return a path delimited by "/"
    public static String GetBreadCrumb(String Name){
        String BreadCrumb = GetPath(MenuNodeList().get(Name).NodeItem);
        if (BreadCrumb==null){
            return "";
        }else{
            return BreadCrumb;
        }
    }

    public static void Execute(String Name){
        ADMAction.Execute(Name);
    }
    
    public static String GetActionAttributeButtonText(String Name){
        if (GetMenuItemAction(Name)==null){
            return "";
        }else{
            return ADMAction.GetAttributeButtonText(GetMenuItemActionType(Name), GetMenuItemAction(Name));
        }
    }

    //prepare the environment for a new load or a delete
    public static void CleanMenuNodeListandTree(){
        String UIContext = sagex.api.Global.GetUIContextName();
        //ListAllNodes(UIContext + "-before");
        //create and store the top menu node
        if (UIroot.containsKey(UIContext)){
            LOG.debug("CleanMenuNodeListandTree: clearing root for '" + UIContext + "'");
            UIroot.get(UIContext).removeAllChildren();
            UIroot.remove(UIContext);
        }

        //clear the existing MenuItems from the list
        if (UIMenuNodeList.containsKey(UIContext)){
            LOG.debug("CleanMenuNodeListandTree: clearing MenuNodeList for '" + UIContext + "'");
            UIMenuNodeList.remove(UIContext);
        }
        MenuNodeList().clear();
        root().removeAllChildren();
        //ListAllNodes(UIContext + "-after");
        
    }
    
    //TODO: EXTERNAL MENU - GetMenuItemsList
    public static Collection<String> GetMenuItemsList(Properties MenuProps){
        Collection<String> MenuList = new LinkedHashSet<String>();
        //TODO: EXTERNAL MENU - write a new GetSubpropertiesThatAreBranches based on a Properties

        //validate that each Menu Item is a fully valid menu item (has a Name property) and not just a remnant Sage property
        
        return MenuList;
    }
    public static Collection<String> GetMenuItemsList(){
        Collection<String> MenuList = new LinkedHashSet<String>();
        String rUI = sagex.api.Global.GetUIContextName();
        UIContext cUI = new UIContext(rUI);
        //get the list of menu items from Sage
        String[] MenuItemNames = sagex.api.Configuration.GetSubpropertiesThatAreBranches(cUI,ADMutil.SagePropertyLocation);
        if (MenuItemNames.length>0){
            //validate that each Menu Item is a fully valid menu item and not just a remnant Sage property
            //for RemoteUI's use a modified process for validation as the GetProperty for a RemoteUI will 
            //  misleadingly return the Servers property if the client property is not set
            if (sagex.api.Global.IsRemoteUI(cUI)){
                //process the RemoteUI's property file directly
                LOG.debug("GetMenuItemsList: Remote UI '" + rUI + "'");
                //make sure the properties file has been saved first
                sagex.api.Configuration.SaveProperties(cUI);
                //get the RemoteUI properties file
                String pFile = sagex.api.Utility.GetWorkingDirectory(cUI) + File.separator + "clients" + File.separator + rUI + ".properties";

                Properties MenuItemProps = new Properties();

                //read the properties from the properties file
                try {
                    FileInputStream in = new FileInputStream(pFile);
                    try {
                        MenuItemProps.load(in);
                        in.close();
                    } catch (IOException ex) {
                        LOG.debug("GetMenuItemsList: IO exception loading properties " + ADMutil.class.getName() + ex);
                        return MenuList;
                    }
                } catch (FileNotFoundException ex) {
                    LOG.debug("GetMenuItemsList: file not found loading properties " + ADMutil.class.getName() + ex);
                    return MenuList;
                }
                
                for (String tMenuItemName : MenuItemNames){
                    if (MenuItemProps.getProperty(ADMutil.SagePropertyLocation + tMenuItemName + "/Name",ADMutil.OptionNotFound).equals(tMenuItemName)){
                        MenuList.add(tMenuItemName);
                    }else{
                        LOG.debug("GetMenuItemsList: skipping invalid menuitem '" + tMenuItemName + "'");
                    }
                }
            }else{
                //process the clients's properties using Sage functions
                LOG.debug("GetMenuItemsList: Client UI '" + rUI + "'");
                for (String tMenuItemName : MenuItemNames){
                    if (ADMutil.HasProperty(ADMutil.SagePropertyLocation + tMenuItemName + "/Name")){
                        MenuList.add(tMenuItemName);
                    }else{
                        LOG.debug("GetMenuItemsList: skipping invalid menuitem '" + tMenuItemName + "'");
                    }
                }
            }
        }
        return MenuList;
    }
    
    //TODO: EXTERNAL MENU - LoadMenuItemsFromSage
    public static void LoadMenuItemsFromSage(){
        String PropLocation = "";

        //cleanup the Nodes and the Tree prior to loading
        CleanMenuNodeListandTree();
        
        //find all MenuItem Name entries from the SageTV properties file
        Collection<String> MenuItemNames = GetMenuItemsList();

        if (MenuItemNames.size()>0){
            
            //load MenuItems
            for (String tMenuItemName : MenuItemNames){
                //make sure you do not load a TopMenu item - it should never be saved but this is just an extra check
                if (!tMenuItemName.equals(ADMutil.TopMenu)){
                    PropLocation = ADMutil.SagePropertyLocation + tMenuItemName;
                    //check the hidden ShowIF property and skip if it is FALSE
                    if (ADMutil.GetPropertyEvalAsBoolean(PropLocation + "/ShowIF", Boolean.TRUE) || ADMutil.GetDefaultsWorkingMode()){
                        ADMMenuNode NewMenuItem = new ADMMenuNode(tMenuItemName);
                        NewMenuItem.ActionAttribute = ADMutil.GetProperty(PropLocation + "/Action", null);
                        NewMenuItem.ActionType = ADMutil.GetProperty(PropLocation + "/ActionType", ADMutil.ActionTypeDefault);
                        NewMenuItem.SetBGImageFileandPath(ADMutil.GetProperty(PropLocation + "/BGImageFile", null));
                        NewMenuItem.ButtonText = ADMutil.GetProperty(PropLocation + "/ButtonText", ADMutil.ButtonTextDefault);
                        NewMenuItem.Name = ADMutil.GetProperty(PropLocation + "/Name", tMenuItemName);
                        NewMenuItem.Parent = ADMutil.GetProperty(PropLocation + "/Parent", "xTopMenu");
                        NewMenuItem.setSortKey(ADMutil.GetProperty(PropLocation + "/SortKey", "0")); 
                        NewMenuItem.SubMenu = ADMutil.GetProperty(PropLocation + "/SubMenu", null);
                        NewMenuItem.IsDefault = Boolean.parseBoolean(ADMutil.GetProperty(PropLocation + "/IsDefault", "false"));
                        NewMenuItem.IsTemp = Boolean.parseBoolean(ADMutil.GetProperty(PropLocation + "/IsTemp", "false"));
                        NewMenuItem.IsActive = ADMutil.GetPropertyAsTriState(PropLocation + "/IsActive", ADMutil.TriState.YES);
                        NewMenuItem.BlockedSageUsersList = ADMutil.GetPropertyAsList(PropLocation + "/BlockedSageUsersList");
                        if (ADMutil.GetDefaultsWorkingMode()){
                            NewMenuItem.ShowIF = ADMutil.GetProperty(PropLocation + "/ShowIF", ADMutil.OptionNotFound);
                        }
                        NewMenuItem.ActionExternal.Load();
                        LOG.debug("LoadMenuItemsFromSage: loaded - '" + tMenuItemName + "' = '" + NewMenuItem.ButtonText + "'");
                    }else{
                        LOG.debug("LoadMenuItemsFromSage: skipped - '" + tMenuItemName + "' due to ShowIF ");
                    }
                }else{
                    LOG.debug("LoadMenuItemsFromSage: skipping - '" + tMenuItemName + "' - should not load a TopMenu item");
                }
            }
            if (MenuNodeList().size()>0){
                //create the tree nodes
                for (ADMMenuNode Node : MenuNodeList().values()){
                    //check if the current node exists yet
                    AddNode(Node);
                }
                //now update the sortkeys from the Tree structure
                SortKeyUpdate();
            }
            
        }else{
            //load a default Menu here.  Load a Diamond Menu if Diamond if active
            LOG.debug("LoadMenuItemsFromSage: no MenuItems found - loading default menu.");
            LoadMenuItemDefaults();
        }
        LOG.debug("LoadMenuItemsFromSage: loaded " + MenuNodeList().size() + " MenuItems = '" + MenuNodeList() + "'");
        
        return;
    }
    
    //TODO: EXTERNAL MENU - SaveMenuItemsToSage
    //saves all MenuItems to Sage properties
    @SuppressWarnings("unchecked")
    public static void SaveMenuItemsToSage(){
        
        //clean up existing MenuItems from the SageTV properties file before writing the new ones
        ADMutil.RemovePropertyAndChildren(ADMutil.SagePropertyLocation);
        //clear the MenuNodeList and rebuild it while saving
        MenuNodeList().clear();
        
        //iterate through all the MenuItems and save to SageTV properties
        Enumeration<DefaultMutableTreeNode> en = root().preorderEnumeration();
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
            if (!tMenu.Name.equals(ADMutil.TopMenu)){
                SaveMenuItemToSage(tMenu);
            }
            //add the item into the MenuNodeList
            MenuNodeList().put(tMenu.Name, tMenu);
        }         
        LOG.debug("SaveMenuItemsToSage: saved " + MenuNodeList().size() + " MenuItems");
        
        return;
    }
 
    //TODO: EXTERNAL MENU - SaveMenuItemToSage
    public static void SaveMenuItemToSage(ADMMenuNode tMenu){
        if (!tMenu.Name.equals(ADMutil.TopMenu)){
            String PropLocation = "";
            PropLocation = ADMutil.SagePropertyLocation + tMenu.Name;
            ADMutil.SetProperty(PropLocation + "/Action", tMenu.ActionAttribute);
            ADMutil.SetProperty(PropLocation + "/ActionType", tMenu.ActionType);
            ADMutil.SetProperty(PropLocation + "/BGImageFile", tMenu.BGImageFile);
            ADMutil.SetProperty(PropLocation + "/ButtonText", tMenu.ButtonText);
            ADMutil.SetProperty(PropLocation + "/Name", tMenu.Name);
            ADMutil.SetProperty(PropLocation + "/Parent", tMenu.Parent);
            ADMutil.SetProperty(PropLocation + "/SortKey", tMenu.SortKey.toString());
            ADMutil.SetProperty(PropLocation + "/SubMenu", tMenu.SubMenu);
            ADMutil.SetProperty(PropLocation + "/IsDefault", tMenu.IsDefault.toString());
            ADMutil.SetProperty(PropLocation + "/IsActive", tMenu.IsActive.toString());
            ADMutil.SetPropertyAsList(PropLocation + "/BlockedSageUsersList", tMenu.BlockedSageUsersList);
        }
    }
    
    public static void DeleteMenuItem(String Name){
        //store the parent for later cleanup
        String OldParent = GetMenuItemParent(Name);
        //do all the deletes first
        MenuNodeList().get(Name).NodeItem.removeAllChildren();
        MenuNodeList().get(Name).NodeItem.removeFromParent();
        //Make sure there is still one default Menu Item
        ValidateSubMenuDefault(OldParent);
        //rebuild any lists
        SaveMenuItemsToSage();
        LOG.debug("DeleteMenuItem: deleted '" + Name + "'");
    }
    
    //TODO: EXTERNAL MENU - DeleteAllMenuItems
    public static void DeleteAllMenuItems(){

        //backup existing MenuItems before deleting
        if (MenuNodeList().size()>0){
            Export tExport = new Export(ADMutil.PropertyBackupFile, util.ExportType.MENUS);
        }
        //clean up existing MenuItems from the SageTV properties file
        ADMutil.RemovePropertyAndChildren(ADMutil.SagePropertyLocation);
        //clean the environment
        CleanMenuNodeListandTree();
        //Create 1 new MenuItem at the TopMenu level
        NewMenuItem(ADMutil.TopMenu, 1) ;

        LOG.debug("DeleteAllMenuItems: completed");
    }
    
    public static String NewMenuItem(String Parent, Integer SortKey){
        String tMenuItemName = GetNewMenuItemName();

        //Create a new MenuItem with defaults
        ADMMenuNode NewMenuItem = new ADMMenuNode(Parent,tMenuItemName,SortKey,ADMutil.ButtonTextDefault,null,ADMutil.ActionTypeDefault,null,null,Boolean.FALSE,ADMutil.TriState.YES);
        SaveMenuItemToSage(NewMenuItem);
        //add the Node to the Tree
        InsertNode(MenuNodeList().get(Parent).NodeItem, NewMenuItem, Boolean.TRUE);
        //ensure there is 1 default item
        ValidateSubMenuDefault(Parent);
        LOG.debug("NewMenuItem: Parent '" + Parent + "' Name '" + tMenuItemName + "' SortKey = '" + SortKey + "'");
        //ADMutil.ListObjectMembers(NewMenuItem);
        return tMenuItemName;
    }
 
    public static void LoadMenuItemDefaults(){
        //load default MenuItems from one or more default .properties file
        String DefaultPropFile = "ADMDefault.properties";
        String DefaultsFullPath = util.DefaultsLocation() + File.separator + DefaultPropFile;
        
        //backup existing MenuItems before processing the import if any exist
        if (MenuNodeList().size()>0){
            LOG.debug("LoadMenuItemDefaults: called Export");
            Export tExport = new Export(ADMutil.PropertyBackupFile, util.ExportType.MENUS);
            LOG.debug("LoadMenuItemDefaults: Export returned to Load Defaults");
        }
        //do an Import for MENUS only
        Import tImport = new Import(DefaultsFullPath, util.ExportType.MENUS);
        LOG.debug("LoadMenuItemDefaults: Import returned to Load Defaults");
        
        ADMutil.ClearFocusStorage();
        
        //now build any dynamic submenus
        LOG.debug("LoadMenuItemDefaults: building any dynamic submenus");
        Integer Counter = 0;
        
        //build the TV Recordings submenu
        String sSubMenu = "admRecordings";
        //determine the max number of TV Recording Views to add
        Integer ViewCount = ADMutil.GetPropertyAsInteger("sagetv_recordings/" + "view_count", 4);
        String NewMenuItemName = "";
        String FirstItem = ADMutil.OptionNotFound;
        for (String vName: ADMAction.SageTVRecordingViews.keySet()){
            NewMenuItemName = CreateDynamicMenuItem(vName, sSubMenu, ADMAction.TVRecordingView, Counter);
            if (Counter==0){
                FirstItem = NewMenuItemName;
            }
            Counter++;
            if (Counter>=ViewCount){
                break;
            }
        }
        //ensure there is 1 default item
        if (!FirstItem.equals(ADMutil.OptionNotFound)){
            SetMenuItemIsDefault(FirstItem, Boolean.TRUE);
            SortKeyUpdate(sSubMenu);
        }
        
        //buld Diamond Videos Menu
        String SageTVMenuVideos = "admSageTVVideos";
        SetMenuItemButtonText(SageTVMenuVideos, "Videos");
        ValidateSubMenuDefault(SageTVMenuVideos);
        SortKeyUpdate(SageTVMenuVideos);

        LOG.debug("LoadMenuItemDefaults: loading default menu items from '" + DefaultsFullPath + "'");
    }
    
    public static String CreateDynamicMenuItem(String dKey, String dParent, String dActionType, Integer dSortKey){
        String tMenuItemName = GetNewMenuItemName();
        //Create a new MenuItem with defaults
        ADMMenuNode NewMenuItem = new ADMMenuNode(dParent,tMenuItemName,dSortKey,ADMutil.ButtonTextDefault,null,ADMutil.ActionTypeDefault,null,null,Boolean.FALSE,ADMutil.TriState.YES);
        SaveMenuItemToSage(NewMenuItem);
        //add the Node to the Tree
        InsertNode(MenuNodeList().get(dParent).NodeItem, NewMenuItem, dSortKey);

        //keep track that this is a dynamically created menu item so in some cases we do not export it when in DefaultsWorkingMode
        ADMMenuNode.SetMenuItemIsCreatedNotLoaded(tMenuItemName, Boolean.TRUE);

        ADMMenuNode.SetMenuItemActionType(tMenuItemName,dActionType);
        ADMMenuNode.SetMenuItemAction(tMenuItemName,dKey);
        ADMMenuNode.SetMenuItemBGImageFile(tMenuItemName,ADMutil.ListNone);
        ADMMenuNode.SetMenuItemButtonText(tMenuItemName,ADMAction.GetAttributeButtonText(dActionType, dKey, Boolean.TRUE));
        ADMMenuNode.SetMenuItemName(tMenuItemName);
        ADMMenuNode.SetMenuItemSubMenu(tMenuItemName,ADMutil.ListNone);
        ADMMenuNode.SetMenuItemIsActive(tMenuItemName,ADMutil.TriState.YES);
        
        return tMenuItemName;
    }

    //used for temp menu items created during the display of Dynamic Lists
    public static void CreateTempMenuItem(String tMenuItemName, String dParent, String dActionType, String dActionAttribute, String dButtonText, Integer dSortKey){
        //see if this parent already has a menu item with this ActionType and ActionAttribute
        //String tMenuItemName = FindMatchingAction(MenuNodeList().get(dParent).NodeItem, dActionType, dActionAttribute);
            //Create a new MenuItem with defaults
        ADMMenuNode NewMenuItem = new ADMMenuNode(dParent,tMenuItemName,dSortKey,ADMutil.ButtonTextDefault,null,ADMutil.ActionTypeDefault,null,null,Boolean.FALSE,ADMutil.TriState.YES);
        SaveMenuItemToSage(NewMenuItem);
        //add the Node to the Tree
        InsertNode(MenuNodeList().get(dParent).NodeItem, NewMenuItem, dSortKey);

        //keep track that this is a temp menu item so we can easily delete it
        ADMMenuNode.SetMenuItemIsTemp(tMenuItemName, Boolean.TRUE);

        ADMMenuNode.SetMenuItemActionType(tMenuItemName,dActionType);
        ADMMenuNode.SetMenuItemAction(tMenuItemName,dActionAttribute);
        ADMMenuNode.SetMenuItemBGImageFile(tMenuItemName,ADMutil.ListNone);
        ADMMenuNode.SetMenuItemButtonText(tMenuItemName,dButtonText);
        ADMMenuNode.SetMenuItemName(tMenuItemName);
        ADMMenuNode.SetMenuItemSubMenu(tMenuItemName,ADMutil.ListNone);
        ADMMenuNode.SetMenuItemIsActive(tMenuItemName,ADMutil.TriState.YES);
    }

    public static Boolean IsEditAllowed(String Name){
        //check if this MenuItem should be allowed to be edited in the ADM Manager interface
        //Temp items are created and then deleted so they should not be allowed to be edited
        if (GetMenuItemIsTemp(Name)){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    
    public static Boolean IsSageSubmenuAllowed(String Name){
        //check if this MenuItem should be allowed to have a SageSubmenu
        if (GetMenuItemLevel(Name)<3 && !GetMenuItemHasSubMenu(Name) && !GetMenuItemActionType(Name).equals(ADMAction.DynamicList)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    //TODO: EXTERNAL MENU - needs updating to find a shared or client based menu file
    private static String CleanPathChars(String InPath){
        InPath = InPath.replaceAll("/", "");
        InPath = InPath.replaceAll(File.separator, "");
        InPath = InPath.replaceAll(":", "");
        InPath = InPath.replaceAll("\\.", "");
        return InPath.toLowerCase();
    }
    public static String GetMenuID(){
        String tID = sagex.api.Global.GetUIContextName();
        Boolean Override = util.GetTrueFalseOption(Const.MenuManagerProp, "MenuLocationOverride", Boolean.FALSE);
        if (Override){
            String rID = util.GetOptionName(Const.MenuManagerProp, "MenuLocationFullPath", tID);
            if (!rID.equals(tID)){
                rID = CleanPathChars(rID);
            }
            LOG.debug("GetMenuID: returning '" + rID + "'");
            return rID;
        }else{
            LOG.debug("GetMenuID: returning '" + tID + "'");
            return tID;
        }
    }
    public static String GetDefaultMenuLocation(){
        String tLocation = util.MenusLocation() + File.separator + GetMenuID() + ".properties";
        Boolean Override = util.GetTrueFalseOption(Const.MenuManagerProp, "MenuLocationOverride", Boolean.FALSE);
        if (Override){
            String rLocation = util.GetOptionName(Const.MenuManagerProp, "MenuLocationFullPath", tLocation);
            LOG.debug("GetDefaultMenuLocation: returning '" + rLocation + "'");
            return rLocation;
        }else{
            LOG.debug("GetDefaultMenuLocation: returning '" + tLocation + "'");
            return tLocation;
        }
    }
    public static String GetDefaultMenuLocationPath(){
        String rLocation = util.GetOptionName(Const.MenuManagerProp, "MenuLocationFullPath", util.OptionNotFound);
        if (rLocation.equals(util.OptionNotFound)){
            LOG.debug("GetDefaultMenuLocationPath: returning '" + util.UserDataLocation() + "'");
            return util.UserDataLocation();
        }else{
            String tLocation = (new File(rLocation)).getParent();
            LOG.debug("GetDefaultMenuLocationPath: returning '" + tLocation + "'");
            return tLocation;
        }
    }
    public static void SetDefaultMenuLocation(String Location){
        //first see if this is a change
        String rLocation = util.GetOptionName(Const.MenuManagerProp, "MenuLocationFullPath", util.OptionNotFound);
        if (!rLocation.equals(Location)){
            Boolean FileExists = (new File(Location)).exists();
            if (FileExists){
                util.SetOption(Const.MenuManagerProp, "MenuLocationFullPath", Location);
                //TODO: EXTERNAL MENU - load new menus here
                LOG.debug("SetDefaultMenuLocation: setting to '" + Location + "' and loading new Menus");
            }else{
                util.SetTrueFalseOption(Const.MenuManagerProp, "MenuLocationOverride", Boolean.FALSE);
                util.SetOption(Const.MenuManagerProp, "MenuLocationFullPath", GetDefaultMenuLocation());
                LOG.debug("SetDefaultMenuLocation: '" + Location + "' not found so using default client menu");
            }
        }
    }

    public static void PropertyLoad(Properties MenuItemProps){
        String PropLocation = "";
        for (String tName : MenuNodeList().keySet()){
            if (!tName.equals(ADMutil.TopMenu)){
                if (GetMenuItemIsCreatedNotLoaded(tName) && ADMutil.GetDefaultsWorkingMode()){
                    //skip exporting this item as we are in DefaultsWorkingMode and this is a Created item so it should not be exported
                }else if (GetMenuItemIsTemp(tName)){
                    //skip exporting this item as it is a TEMP Menu Item and should not be exported
                }else{
                    PropLocation = ADMutil.SagePropertyLocation + tName;
                    PropertyAdd(MenuItemProps,PropLocation + "/Action",GetMenuItemAction(tName));
                    PropertyAdd(MenuItemProps,PropLocation + "/ActionType", GetMenuItemActionType(tName));
                    PropertyAdd(MenuItemProps,PropLocation + "/BGImageFile", GetMenuItemBGImageFile(tName));
                    PropertyAdd(MenuItemProps,PropLocation + "/ButtonText", GetMenuItemButtonText(tName));
                    PropertyAdd(MenuItemProps,PropLocation + "/Name", tName);
                    PropertyAdd(MenuItemProps,PropLocation + "/Parent", GetMenuItemParent(tName));
                    PropertyAdd(MenuItemProps,PropLocation + "/SortKey", GetMenuItemSortKey(tName).toString());
                    if (GetMenuItemSubMenu(tName)==null){
                        //do nothing for null
                    }else if (!GetMenuItemSubMenu(tName).equals(tName)){
                        PropertyAdd(MenuItemProps,PropLocation + "/SubMenu", GetMenuItemSubMenu(tName));
                    }
                    PropertyAdd(MenuItemProps,PropLocation + "/IsDefault", GetMenuItemIsDefault(tName).toString());
                    PropertyAdd(MenuItemProps,PropLocation + "/IsActive", GetMenuItemIsActive(tName).toString());
                    if (GetMenuItemBlockedSageUsersListAsList(tName).size()>0){
                        PropertyAdd(MenuItemProps,PropLocation + "/BlockedSageUsersList", GetMenuItemBlockedSageUsersList(tName));
                    }
                    if (ADMutil.GetDefaultsWorkingMode() && !GetMenuItemShowIF(tName).equals(ADMutil.OptionNotFound)){
                        //in this mode the ShowIF property get's exported so it's available to build a new defaults file
                        PropertyAdd(MenuItemProps,PropLocation + "/ShowIF", GetMenuItemShowIF(tName));
                    }
                    //if this is an external action then save out the external action properties
                    if (GetMenuItemActionType(tName).equals(ADMAction.LaunchExternalApplication)){
                        GetMenuItemActionExternal(tName).AddProperties(MenuItemProps);
                    }
                    //LOG.debug("ExportMenuItems: exported - '" + entry.getValue().getName() + "'");
                }
            }
        }
        
    }
    
    private static void PropertyAdd(Properties inProp, String Location, String Setting){
        if (Setting!=null){
            inProp.setProperty(Location, Setting);
        }
    }
    
    private static void AddNode(ADMMenuNode aNode){
        if (!NodeExists(root(), aNode.Name)){
            //check if the current nodes parent exists yet
            if (aNode.Parent.equals(ADMutil.TopMenu)){
                //root.add(new DefaultMutableTreeNode(aNode));
                InsertNode(root(), aNode, Boolean.FALSE);
                //LOG.debug("AddNode: node '" + aNode.ButtonText + "' not found so adding to ROOT");
            }else{
                AddNode(MenuNodeList().get(aNode.Parent));
                DefaultMutableTreeNode tParent = FindNode(root(), aNode.Parent);
                //tParent.add(new DefaultMutableTreeNode(aNode));
                InsertNode(tParent, aNode, Boolean.FALSE);
                //LOG.debug("AddNode: node '" + aNode.ButtonText + "' not found so adding");
            }
        }else{
            //LOG.debug("AddNode: node '" + aNode.ButtonText + "' already exists");
        }
    }
    
    private static void InsertNode(DefaultMutableTreeNode iParent, ADMMenuNode iNode, Boolean FixSort){
        //insert the node according to the SortKey value
        if ( iParent.getChildCount() == 0 || iNode.SortKey < 0 ) {
            //no children or forced to bottom (by -1 SortKey) so just do an add
            InsertNode(iParent,iNode,-1);
        }else{
            
            DefaultMutableTreeNode tlastChild = (DefaultMutableTreeNode)iParent.getFirstChild() ;
            ADMMenuNode lastChild = (ADMMenuNode)tlastChild.getUserObject() ;
            //MenuNode newChildA = (MenuNode) iNode ;
            if ( iNode.SortKey < lastChild.SortKey ) {
                // Its at the top of the list
                InsertNode(iParent,iNode,0);
            }
            else if ( iParent.getChildCount() == 1 ) {
                // There is only one element and since it ain't less than then well put it after
                InsertNode(iParent,iNode,1);
            } else { 
                // we gotta go look for the right spot to insert it
                Boolean done = Boolean.FALSE ;
                for ( int i = 1 ; i < iParent.getChildCount() && !done ; i++ ) {

                    DefaultMutableTreeNode tnextChild = (DefaultMutableTreeNode)iParent.getChildAt(i) ;
                    ADMMenuNode nextChild = (ADMMenuNode)tnextChild.getUserObject() ;
                    if (( iNode.SortKey >= lastChild.SortKey ) && ( iNode.SortKey < nextChild.SortKey ) ){
                        // Ok it needs to go between these two
                        InsertNode(iParent,iNode,i);
                        done = Boolean.TRUE;
                    }
                }
                if ( !done ) { // didn't find a place to insert the node must be the last one
                    InsertNode(iParent,iNode,iParent.getChildCount());
                }
            }            
        }
        if (FixSort){
            //fix the sortkeys when a single Insert calls this function
            SortKeyUpdate(iParent);
        }
    }
    
    private static void InsertNode(DefaultMutableTreeNode iParent, ADMMenuNode iNode, Integer iLocation){
        DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(iNode);
        iNode.NodeItem = tNode;
        if (iLocation>=0){
            //do an insert
            iParent.insert(tNode, iLocation);
        }else{
            //do an add for -1
            iParent.add(tNode);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static DefaultMutableTreeNode FindNode(DefaultMutableTreeNode Root, DefaultMutableTreeNode Node){
        Enumeration<DefaultMutableTreeNode> en = Root.preorderEnumeration();
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            if(child.equals(Node)) 
            { 
                //tree node with string found 
                //LOG.debug("FindNode: '" + Node + "' found = '" + child + "' childcount = '" + child.getChildCount() + "' Parent = '" + child.getParent() + "' Level = '" + child.getLevel() + "' Leaf = '" + child.isLeaf() + "'"  );
                return child;                          
            } 
        }         
        //LOG.debug("FindNode: '" + Node + "' not found.");
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static DefaultMutableTreeNode FindNode(DefaultMutableTreeNode Root, String NodeKey){
        Enumeration<DefaultMutableTreeNode> en = Root.preorderEnumeration();
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            if(NodeKey.equals(child.getUserObject().toString())) 
            { 
                //tree node with string found 
                //LOG.debug("FindNode: '" + NodeKey + "' found = '" + child + "' childcount = '" + child.getChildCount() + "' Parent = '" + child.getParent() + "' Level = '" + child.getLevel() + "' Leaf = '" + child.isLeaf() + "'"  );
                return child;                          
            } 
        }         
        //LOG.debug("FindNode: '" + NodeKey + "' not found.");
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static String FindMatchingAction(DefaultMutableTreeNode Root, String ActionType, String ActionAttribute){
        Enumeration<DefaultMutableTreeNode> en = Root.preorderEnumeration();
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
            if(tMenu.ActionType.equals(ActionType) && tMenu.ActionAttribute.equals(ActionAttribute)){
                return tMenu.Name;
            }
        }         
        //LOG.debug("FindNode: '" + NodeKey + "' not found.");
        return ADMutil.OptionNotFound;
    }
    
    @SuppressWarnings("unchecked")
    public static Boolean NodeExists(DefaultMutableTreeNode Root, String NodeKey){
        Enumeration<DefaultMutableTreeNode> en = Root.preorderEnumeration();
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            if(NodeKey.equals(child.getUserObject().toString())) 
            { 
                //tree node with string found 
                //LOG.debug("NodeExists: '" + NodeKey + "' found = '" + child + "' childcount = '" + child.getChildCount() + "' Parent = '" + child.getParent() + "' Level = '" + child.getLevel() + "' Leaf = '" + child.isLeaf() + "'"  );
                return Boolean.TRUE;                          
            } 
        }         
        //LOG.debug("NodeExists: '" + NodeKey + "' not found.");
        return Boolean.FALSE;
    }
    
    @SuppressWarnings("unchecked")
    public static void ListAllNodes(String LogText){
        Enumeration<DefaultMutableTreeNode> en = root().preorderEnumeration();
        while (en.hasMoreElements())   {
            DefaultMutableTreeNode child = en.nextElement();
            ADMMenuNode tMenu = (ADMMenuNode)child.getUserObject();
            LOG.debug("ListAllNodes(" + LogText + "}: '" + tMenu.Name + "' ButtonText = '" + tMenu.ButtonText + "' childcount = '" + child.getChildCount() + "' Parent = '" + child.getParent() + "' Level = '" + child.getLevel() + "' Leaf = '" + child.isLeaf() + "'"  );
        }         
    }
    
    public static String GetPath(DefaultMutableTreeNode Node){
        String OutPath = null;
        TreeNode[] path = Node.getPath();
        for (TreeNode pathnode : path){
            DefaultMutableTreeNode pathnodea = (DefaultMutableTreeNode)pathnode;
            ADMMenuNode tMenu = (ADMMenuNode)pathnodea.getUserObject();
            if (!tMenu.Name.equals(ADMutil.TopMenu)){
                if (OutPath == null){
                    OutPath = tMenu.ButtonText;
                }else{
                    OutPath = OutPath + " / " + tMenu.ButtonText;
                }
            }
        }
        return OutPath;
    }
    
    public static void setParent( DefaultMutableTreeNode Node, DefaultMutableTreeNode newParent ){
        if ( null == Node ) return;                         // No node selected   
        if ( null == newParent ) return;                    // No Parent provided    
        DefaultMutableTreeNode oldParent = (DefaultMutableTreeNode)Node.getParent();   
        oldParent.remove(Node);   
        newParent.add(Node);  
        LOG.debug("setParent: node = '" + Node + "' oldParent = '" + oldParent +"' newParent = '" + newParent + "'");
    }
    
    //TODO: EXTERNAL MENU - Save
    public static void Save(String Name, String PropType, String Setting){
        if (!Name.equals(ADMutil.TopMenu)){
            String PropLocation = ADMutil.SagePropertyLocation + Name;
            ADMutil.SetProperty(PropLocation + "/" + PropType, Setting);
            //now save the specifc node field change
            if (PropType.equals("Action")){
                MenuNodeList().get(Name).ActionAttribute = Setting;
            }else if (PropType.equals("ActionType")){
                MenuNodeList().get(Name).ActionType = Setting;
            }else if (PropType.equals("BGImageFile")){
                MenuNodeList().get(Name).BGImageFile = Setting;
            }else if (PropType.equals("ButtonText")){
                MenuNodeList().get(Name).ButtonText = Setting;
            }else if (PropType.equals("IsActive")){
                try {
                    MenuNodeList().get(Name).IsActive = ADMutil.TriState.valueOf(Setting);
                } catch (Exception e) {
                    MenuNodeList().get(Name).IsActive = ADMutil.TriState.YES;
                }
            }else if (PropType.equals("IsTemp")){
                MenuNodeList().get(Name).IsTemp = Boolean.parseBoolean(Setting);
            }else if (PropType.equals("IsDefault")){
                MenuNodeList().get(Name).IsDefault = Boolean.parseBoolean(Setting);
            }else if (PropType.equals("SortKey")){
                //included sortKey only so it does not raise an error when called
            }else if (PropType.equals("SubMenu")){
                MenuNodeList().get(Name).SubMenu = Setting;
            }else if (PropType.equals("Name")){
                //included Name only so it does not raise an error when called
            }else if (PropType.equals("Parent")){
                MenuNodeList().get(Name).Parent = Setting;
            }else if (PropType.equals("BlockedSageUsersList")){
                //assume that the list has already been modified by the calling routine
            }else{
                LOG.debug("Save - invalid option passed for '" + PropType + "' '" + Name + "' = '" + Setting + "'");
            }
            //LOG.debug("Save completed for '" + PropType + "' '" + Name + "' = '" + Setting + "'");
        }
    }
    
    public static String GetNewMenuItemName(){
        Boolean UniqueName = Boolean.FALSE;
        String NewName = null;
        while (!UniqueName){
            NewName = ADMutil.GenerateRandomadmName();
            //check to see that the name is unique from other existing MenuItemNames
            UniqueName = !MenuNodeList().containsKey(NewName);
        }
        return NewName;
    }

    @SuppressWarnings("unchecked")
    public static Map<String,ADMMenuNode> MenuNodeList(){
        String UIContext = sagex.api.Global.GetUIContextName();
        if (!UIMenuNodeList.containsKey(UIContext)){
            //create the MenuNodeList for this UIContext
            LOG.debug("MenuNodeList: creating MenuNodeList for '" + UIContext + "'");
            UIMenuNodeList.put(UIContext, new LinkedHashMap<String,ADMMenuNode>());
        }
        //LOG.debug("MenuNodeList: '" + UIContext + "'");
        Map<String,ADMMenuNode> tMenuNodeList = null;
        try {
            tMenuNodeList = UIMenuNodeList.get(UIContext);
        } catch (Exception e) {
            //LOG.debug("MenuNodeList ERROR: '" + UIContext + "' = '" + e + "'");
        }
        return tMenuNodeList;
    }

    public static DefaultMutableTreeNode root(){
        String UIContext = sagex.api.Global.GetUIContextName();
        if (!UIroot.containsKey(UIContext)){
            LOG.debug("root: creating root for '" + UIContext + "'");
            ADMMenuNode rootNode = new ADMMenuNode(ADMutil.TopMenu);
            UIroot.put(UIContext,new DefaultMutableTreeNode(rootNode));
            rootNode.NodeItem = UIroot.get(UIContext);
            rootNode.ButtonText = "Top Level";
        }
        //LOG.debug("root: '" + UIContext + "'");
        return UIroot.get(UIContext);
    }

    public static DefaultMutableTreeNode root(String UIContext){
        if (!UIroot.containsKey(UIContext)){
            LOG.debug("root: creating root for '" + UIContext + "'");
            ADMMenuNode rootNode = new ADMMenuNode(ADMutil.TopMenu);
            UIroot.put(UIContext,new DefaultMutableTreeNode(rootNode));
            rootNode.NodeItem = UIroot.get(UIContext);
            rootNode.ButtonText = "Top Level";
        }
        //LOG.debug("root: '" + UIContext + "'");
        return UIroot.get(UIContext);
    }
    
}
