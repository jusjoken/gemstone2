package Gemstone;

import org.apache.log4j.Logger;
import Gemstone.util;
import sagex.phoenix.menu.IMenuItem;
import sagex.phoenix.menu.Menu;
import sagex.phoenix.menu.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static Gemstone.util.*;

/**
 * Created by jusjoken on 11/21/2016.
 */
public class PMenuUtil {
    static private final Logger LOG = Logger.getLogger(PMenuUtil.class);
    public static final String MenuManagerBaseProp = Const.BaseProp + Const.PropDivider + Const.MenuManagerProp + Const.PropDivider;
    public static final String SageFocusPropertyLocation = MenuManagerBaseProp + "focus/";

    public static Integer GetMaxMenuItems(Object MenuItems, Integer Level){
        Integer DefaultMax = 8;
        if (MenuItems==null){
            return DefaultMax;
        }
        if (Level > 1){
            DefaultMax = 10;
        }
        if (Level==1){
            Integer Level1Max = util.GetPropertyAsInteger(MenuManagerBaseProp + "MaxMenuItems/" + Level.toString(), DefaultMax);
            Integer TopMenuCount = phoenix.menu.GetVisibleItems( sagex.api.Utility.GetElement(MenuItems,0) ).size();
            if (Level1Max > TopMenuCount){
                return TopMenuCount;
            }else{
                return Level1Max;
            }
        }else{
            return util.GetPropertyAsInteger(MenuManagerBaseProp + "MaxMenuItems/" + Level.toString(), DefaultMax);
        }
    }

    public static Integer GetMenuItemCount(Object MenuItems){
        if (MenuItems==null){
            return 0;
        }
        return phoenix.menu.GetVisibleItems( MenuItems ).size();
    }

    //Save the current item that is focused for later retrieval
    public static void SetLastFocusedMenu(String MenuName, String FocusItem){
        if (MenuName==null){
            LOG.debug("SetLastFocusedMenu: MenuName passed was null - FocusItem not saved");
            return;
        }
        LOG.debug("SetLastFocusedMenu: MenuName '" + MenuName + "' for FocusItem '" + FocusItem + "'");
        SetProperty(SageFocusPropertyLocation + MenuName, FocusItem);
    }

    //Save the current item that is focused for later retrieval
    public static void SetLastFocusedMenu(String MenuName, Integer FocusItemIndex){
        if (MenuName==null){
            LOG.debug("SetLastFocusedMenu: MenuName passed was null - FocusItem not saved");
            return;
        }
        //find the menu
        Menu menu = phoenix.menu.GetMenu(MenuName);
        if (menu==null){
            LOG.debug("SetLastFocusedMenu: menu not found for MenuName '" + MenuName +"'");
            return;
        }
        //find the name at the passed in index
        IMenuItem item = phoenix.menu.GetVisibleItems(menu).get(FocusItemIndex);
        if (item==null){
            LOG.debug("SetLastFocusedMenu: index '" + FocusItemIndex + "' not found in MenuName '" + MenuName +"'");
        }
        LOG.debug("SetLastFocusedMenu: MenuName '" + MenuName + "' for Index '" + FocusItemIndex + "' FocusItem '" + phoenix.menu.GetName(item) + "' saved");
        SetProperty(SageFocusPropertyLocation + MenuName, phoenix.menu.GetName(item));
    }

    public static String GetLastFocusedMenu(String MenuName){
        if (MenuName==null){
            LOG.debug("GetLastFocusedMenu: MenuName passed was null - returning empty string");
            return "";
        }
        String LastFocus = GetProperty(SageFocusPropertyLocation + MenuName,OptionNotFound);
        if (LastFocus.equals(OptionNotFound)){
            //return the DefaultMenuItem for this MenuName
            return phoenix.menu.GetDefaultItem(MenuName).getName();
        }else{
            return LastFocus;
        }
    }

    public static Integer GetLastFocusedMenuIndex(String MenuName, List<IMenuItem> menuList){
        if (MenuName==null || menuList==null){
            LOG.debug("GetLastFocusedMenuIndex: MenuName or menuList passed was null - returning 0");
            return 0;
        }
        String LastFocus = GetProperty(SageFocusPropertyLocation + MenuName,OptionNotFound);
        if (LastFocus.equals(OptionNotFound)){
            LOG.debug("GetLastFocusedMenuIndex: MenuName '" + MenuName + "' not found - returning 0");
            return 0;
        }
        Menu menu = phoenix.menu.GetMenu(MenuName);
        if (menu==null){
            LOG.debug("GetLastFocusedMenuIndex: menu not found for MenuName '" + MenuName +"' returning 0");
            return 0;
        }
        IMenuItem item = phoenix.menu.GetItemByName(menu,LastFocus);
        if (item==null){
            LOG.debug("GetLastFocusedMenuIndex: LastFocus '" + LastFocus + "' not found for menu '" + MenuName +"' returning 0");
            return 0;
        }
        //search for the item in the list
        Integer lastIndex = -1;
        Integer counter = 0;
        for (IMenuItem tItem : menuList) {
            if (tItem.getName().equals(item.getName())){
                lastIndex = counter;
                break;
            }
            counter++;
        }
        if (lastIndex==-1){
            LOG.debug("GetLastFocusedMenuIndex: LastFocus '" + LastFocus + "' not found for MenuName '" + MenuName +"' returning 0");
            return 0;
        }
        return lastIndex;
    }

    public static void ClearFocusStorage(){
        //clean up existing focus items
        RemovePropertyAndChildren(SageFocusPropertyLocation);
    }

    //retrieves the menu item index within the menu
    public static Integer GetItemIndex(Object menu, IMenuItem item ){
        if (menu==null){
            return 0;
        }
        if (menu instanceof Menu) {
            return ((Menu) menu).getVisibleItems().indexOf(item);
        }
        return 0;
    }

    //used for 360 menu to pad before the first item as well as after the focused item
    public static List<IMenuItem> GetTopMenu360(Object menu, Integer FocusedIndex, Integer PadBlanks ){
        List<IMenuItem> retList = new ArrayList<IMenuItem>();
        List<IMenuItem> topList = phoenix.menu.GetVisibleItems(menu);
        if (topList==null){
            return retList;
        }
        int OddOffset = 0;
        if ((topList.size() & 1)==0){//even
            OddOffset = 0;
        }else{//odd
            OddOffset = 1;
        }
        //calculate FocusPos
        Integer FocusPos = ((topList.size() + OddOffset) / 2) -1;
        LOG.debug("GetTopMenu360 called with FocusPos '" + FocusPos + "' FocusedIndex '" + FocusedIndex + "' OddOffset '" + OddOffset + "'");
        //fill the List with Blanks
        for (int i = 0; i < topList.size() + PadBlanks + OddOffset; i++){
            retList.add(phoenix.menu.CreateMenuItem(null,"BlankItem"+i,"BlankItem"+i));
        }
        //replace BlankItems with valid menu items at the right locations
        int itemCounter = FocusPos - FocusedIndex;
        if (itemCounter < OddOffset) itemCounter = itemCounter + retList.size() - OddOffset;
        //if (itemCounter==0) itemCounter = OddOffset;
        //LOG.info("**** GetTopMenuList starting itemCounter '" + itemCounter + "'");
        for (IMenuItem Item : topList){
            if (itemCounter >= retList.size()){
                itemCounter = OddOffset;
                //LOG.info("**** GetTopMenuList itemCounter reset to '" + OddOffset + "'");
            }
            //just replace the item
            retList.set(itemCounter, Item);
            //LOG.info("**** GetTopMenuList setting itemCounter '" + itemCounter + "' to '" + Item + "'");
            if (itemCounter==FocusPos){
                //skip by PadBlanks
                itemCounter = itemCounter + PadBlanks;
                //LOG.info("**** GetTopMenuList skipping PadBlanks. itemCounter set to '" + itemCounter);
            }
            itemCounter++;
            //LOG.info("**** GetTopMenuList itemCounter increased to '" + itemCounter + "'");
        }
        //LOG.info("**** GetTopMenuList returning new list '" + retList + "'");
        return retList;
    }

    //adjusts the menu style for 360
    public static boolean StyleMenu360(Menu menu, Integer BlanksBefore, Integer BlanksAfter ){
        if (menu==null){
            LOG.debug("StyleMenu360: null menu passed in returning false");
            return false;
        }
        //iterate through the menu to adjust the style
        List<IMenuItem> TopList = menu.getVisibleItems();
        for (IMenuItem item:TopList){
            LOG.debug("StyleMenu360: processing '" + item.label() + "'");
            Boolean hasActions = false;
            Boolean isView = false;
            if (phoenix.menu.IsViewMenu(item)){
                isView = true;
            } else if (phoenix.menu.HasActions(item)){
                hasActions = true;
            }
            if (phoenix.menu.IsMenu(item)){
                StyleSubMenu360((Menu) item,BlanksBefore,BlanksAfter,hasActions);
            }else{
                //not a menu so if there is an Action - add it
                if (hasActions){
                    Menu newMenu = phoenix.menu.ConvertMenuItemToMenu(item);
                    IMenuItem firstMenuItem = newMenu.getVisibleItems().get(0);
                    int counter = 0;
                    for (int i = 0; i < BlanksBefore; i++){
                        counter++;
                        phoenix.menu.InsertBefore(newMenu,phoenix.menu.CreateMenuItem(null,"BlankItem"+counter,"BlankItem"+counter),firstMenuItem );
                    }
                    for (int i = 0; i < BlanksAfter; i++){
                        counter++;
                        phoenix.menu.InsertAfter(newMenu,phoenix.menu.CreateMenuItem(null,"BlankItem"+counter,"BlankItem"+counter),firstMenuItem );
                    }
                }
            }
        }
        return true;
    }

    public static boolean StyleSubMenu360(Menu item, Integer BlanksBefore, Integer BlanksAfter, boolean hasActions ){
        List<IMenuItem> tList = phoenix.menu.GetVisibleItems(item);
        LOG.debug("StyleSubMenu360: tList = " + tList);
        for (IMenuItem subItem:tList){
            if (phoenix.menu.IsMenu(subItem)){
                StyleSubMenu360((Menu) subItem,BlanksBefore,BlanksAfter,false);
            }
        }
        //get the first and last MenuItems for reference to insert blanks
        IMenuItem firstMenuItem = tList.get(0);
        //LOG.debug("StyleSubMenu360: firstMenuItem = " + firstMenuItem);
        IMenuItem lastMenuItem = tList.get(tList.size()-1);
        //LOG.debug("StyleSubMenu360: lastMenuItem = " + lastMenuItem);
        //insert blanks at the start of the list of items
        int counter = 0;
        for (int i = 0; i < BlanksBefore; i++){
            counter++;
            phoenix.menu.InsertBefore((Menu) item,phoenix.menu.CreateMenuItem(null,"BlankItem"+counter,"BlankItem"+counter),firstMenuItem );
        }
        //add the action of the passed menu if it has one
        if (hasActions){
            counter++;
            //get the parent of the first item to use as the parent for the copy
            Menu parent = tList.get(0).getParent();
            phoenix.menu.InsertBefore((Menu) item,phoenix.menu.CopyMenuItem(parent, item),firstMenuItem );
        }
        //insert blanks at the end of the list of items
        for (int i = 0; i < BlanksAfter; i++){
            counter++;
            phoenix.menu.InsertAfter((Menu) item,phoenix.menu.CreateMenuItem(null,"BlankItem"+counter,"BlankItem"+counter),lastMenuItem );
        }
        return true;
    }


        public static Boolean IsBlank(IMenuItem Item){
        return Item.label().toString().startsWith("BlankItem");
    }

    //return a path delimited by "/"
    public static String GetBreadCrumb(IMenuItem topItem, IMenuItem item){
        if (item==null || topItem==null){
            return "";
        }
        String BreadCrumb = "";
        IMenuItem tParent = item;
        while(tParent!=null && tParent!=topItem){
            if (BreadCrumb.equals("")){
                BreadCrumb = phoenix.menu.GetLabel(tParent);
            }else{
                BreadCrumb = phoenix.menu.GetLabel(tParent) + " / " + BreadCrumb;
            }
            tParent = tParent.getParent();
        }
        return BreadCrumb;
    }

}
