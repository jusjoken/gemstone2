/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import sagex.UIContext;

/**
 *
 * @author jusjoken
 */
public class ADMDiamond {
    
    //class functions copied from diamond 3.30 release to support diamond Menu Customization

    public static final String PropName="JOrton/CustomViews";
    public static Map<String,ADMDiamond.DefaultFlow> DiamondDefaultFlows = new LinkedHashMap<String,ADMDiamond.DefaultFlow>();

    //Diamond Legacy call
    public static Collection<String> GetCustomViews(){
        String views=util.GetProperty(PropName,"");
        if(views.contains(";")){	
            System.out.println("ADM Diamond : GetCustomViews (split) = '" + views.split(";") + "'");
            return Arrays.asList(views.split(";"));
        }
        System.out.println("ADM Diamond : GetCustomViews (single) = '" + views + "'");
        return Arrays.asList(views);
    }

    //Diamond Legacy call
    public static String GetViewName(String name){
        String[] SplitString = name.split("&&");
        if (SplitString.length == 2) {
            System.out.println("ADM Diamond : GetViewName("+name+") = '" + SplitString[0] + "'");
            return SplitString[0];
        } else {
            System.out.println("ADM Diamond : Not Found: GetViewName("+name+")");
            return util.OptionNotFound;
        }
    }

    //Diamond Legacy call
    public static void RenameFlow(String OldName, String NewName){
        //sagediamond.CustomViews.RenameView();
    }
    
    public static Boolean IsDiamond(){
        if (IsDiamondLegacy() || ADMgemcalls.Isgemstone()){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    //will only return true if this is the legacy version of Diamond (pre-gemstone)
    public static Boolean IsDiamondLegacy(){
        String DiamondWidgetSymbol = "AOSCS-65"; 
        // check to see if the Diamond Plugin is installed
        Object[] FoundWidget = new Object[1];
        FoundWidget[0] = sagex.api.WidgetAPI.FindWidgetBySymbol(new UIContext(sagex.api.Global.GetUIContextName()), DiamondWidgetSymbol);
        if (FoundWidget[0]!=null){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    //Diamond Legacy call
    public static Boolean UseDiamondMovies(){
        if (IsDiamondLegacy()){
            String DiamondVideoMenuCheckProp = "JOrton/MainMenu/ShowDiamondMoviesTab";
            if (util.GetPropertyAsBoolean(DiamondVideoMenuCheckProp, Boolean.FALSE)){
                return Boolean.TRUE;
            }else{
                return Boolean.FALSE;
            }
        }else{
            return Boolean.FALSE;
        }
    }
    
    //gemstone OR Diamond Legacy
    public static Boolean ShowWidgetswithQLM(){
        //ensure Diamond is installed and enabled
        if (!IsDiamond()){
            return Boolean.FALSE;
        }
        //ensure at minimum that the option is enabled in QLM
        Boolean OptionOn = util.GetPropertyAsBoolean(ADMutil.SageADMSettingsPropertyLocation + "/qlm_show_diamond_widgets", Boolean.FALSE);
        if (OptionOn){
            if (ADMgemcalls.Isgemstone()){  //check for new gemstone version of Diamond
                return ADMgemcalls.ShowWidgetswithQLM();
            }else{  //legacy Diamond
                //now ensure that the Diamond Widget options are turned on and should be showing
                String WidgetPanel = "JOrton/MainMenu/WidgetPanel";
                String Off = "Off";
                if (!util.GetProperty(WidgetPanel + "1", Off).equals(Off) || !util.GetProperty(WidgetPanel + "2", Off).equals(Off) || !util.GetProperty(WidgetPanel + "3", Off).equals(Off) || !util.GetProperty(WidgetPanel + "4", Off).equals(Off)){
                    //as at least one panel is On then Show the Widget Panel
                    return Boolean.TRUE;
                }else{
                    return Boolean.FALSE;
                }
            }
        }else{
            return Boolean.FALSE;
        }
    }
    
    //gemstone OR Diamond Legacy
    public static void LoadDiamondWidgetswithQLM(){
        DiamondWidgetswithQLM(Boolean.TRUE);
    }
    
    //gemstone OR Diamond Legacy
    public static void UnloadDiamondWidgetswithQLM(){
        DiamondWidgetswithQLM(Boolean.FALSE);
    }
    
    //gemstone OR Diamond Legacy
    private static void DiamondWidgetswithQLM(Boolean Load){
        //show the Diamond Widgets
        String DiamondWidgetsPanelSymbol = "AOSCS-679196";
        String QLMWidgetsParentPanelSymbol = "JUSJOKEN-1236101";

        String DiamondRecordingDataSymbol = "PLUCKYHD-437950";
        String QLMRecordingDataParentPanelSymbol = "JUSJOKEN-2168359";
        
        //connect/disconnect the Widget panel from it's parent
        if (Load){
            sagex.api.WidgetAPI.InsertWidgetChild(new UIContext(sagex.api.Global.GetUIContextName()), QLMWidgetsParentPanelSymbol, DiamondWidgetsPanelSymbol,0);
            sagex.api.WidgetAPI.InsertWidgetChild(new UIContext(sagex.api.Global.GetUIContextName()), QLMRecordingDataParentPanelSymbol, DiamondRecordingDataSymbol,0);
        }else{
            sagex.api.WidgetAPI.RemoveWidgetChild(new UIContext(sagex.api.Global.GetUIContextName()), QLMWidgetsParentPanelSymbol, DiamondWidgetsPanelSymbol);
            sagex.api.WidgetAPI.RemoveWidgetChild(new UIContext(sagex.api.Global.GetUIContextName()), QLMRecordingDataParentPanelSymbol, DiamondRecordingDataSymbol);
        }
        
    }

    //gemstone OR Diamond Legacy
    //Use the diamond widget width property and return it for the panel width
    public static Double DiamondWidgetsPanelWidth(){
        if (IsDiamondLegacy()){
            return util.GetPropertyAsInteger("JOrton/MainMenu/MenuWidgetWidth", 6)*0.038;
        }else{
            return ADMgemcalls.GetWidgetsWidth()*0.038;
        }
    }

    //gemstone OR Diamond Legacy
    //see if the Widget panel is using the Tab Style
    public static Boolean UseDiamondWidgetsPanelTabStyle(){
        if (IsDiamondLegacy()){
            return util.GetPropertyAsBoolean("JOrton/MainMenu/MenuWidgetTabStyle", Boolean.FALSE);
        }else{
            return ADMgemcalls.WidgetsUseTabStyle();
        }
    }

    //Diamond Legacy call
    public static void LoadDiamondDefaultFlows(){
        DiamondDefaultFlows.clear();
        DiamondDefaultFlows.put("LCKOF-346154", new DefaultFlow("Wall Flow", "LCKOF-346154", 0,Boolean.TRUE));
        DiamondDefaultFlows.put("LCKOF-346153", new DefaultFlow("Cover Flow", "LCKOF-346153", 1));
        DiamondDefaultFlows.put("LCKOF-346152", new DefaultFlow("List Flow", "LCKOF-346152", 2));
        DiamondDefaultFlows.put("PLUCKYHD-1486084", new DefaultFlow("SideWays Flow", "PLUCKYHD-1486084", 3));
        DiamondDefaultFlows.put("AOSCS-186340", new DefaultFlow("Category Flow", "AOSCS-186340", 4));
        DiamondDefaultFlows.put("LCKOF-392395", new DefaultFlow("360 Flow", "LCKOF-392395", 5));
    }
    
    //Diamond Legacy call
    public static class DefaultFlow{
        public String ButtonText = "";
        public String WidgetSymbol = "";
        public Integer SortOrder = 0;
        public Boolean Default = Boolean.FALSE;
        public static SortedMap<String,String> ListSorted = new TreeMap<String,String>();
        
        public DefaultFlow(String ButtonText, String WidgetSymbol, Integer SortOrder){
            this(ButtonText, WidgetSymbol, SortOrder, Boolean.FALSE);
        }
        public DefaultFlow(String ButtonText, String WidgetSymbol, Integer SortOrder, Boolean Default){
            this.ButtonText = ButtonText;
            this.WidgetSymbol = WidgetSymbol;
            this.SortOrder = SortOrder;
            this.Default = Default;
            ListSorted.put(this.ButtonText, this.WidgetSymbol);
        }
    }
    
}
