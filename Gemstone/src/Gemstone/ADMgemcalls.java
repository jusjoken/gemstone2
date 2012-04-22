/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.ArrayList;
import sagex.UIContext;
import Gemstone.Flow;
import Gemstone.Widget;

/**
 *
 * @author jusjoken
 */
public class ADMgemcalls {
    
    public static Boolean Isgemstone(){
        String gemstoneWidgetSymbol = "JUSJOKEN-3084835";
        // check to see if the gemstone Plugin is installed
        Object[] FoundWidget = new Object[1];
        FoundWidget[0] = sagex.api.WidgetAPI.FindWidgetBySymbol(new UIContext(sagex.api.Global.GetUIContextName()), gemstoneWidgetSymbol);
        if (FoundWidget[0]!=null){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static ArrayList<String> GetFlows(){
        ArrayList<String> tReturn = new ArrayList<String>();
        try {
            tReturn = Flow.GetFlows();
        } catch (NoClassDefFoundError e) {
            System.out.println("ADM gemcalls : gemstone class not found '" + e + "'");
        }
        return tReturn;
    }
    
    public static String GetFlowName(String name){
        String tReturn = "";
        try {
            tReturn = Flow.GetFlowName(name);
        } catch (NoClassDefFoundError e) {
            System.out.println("ADM gemcalls : gemstone class not found '" + e + "'");
        }
        return tReturn;
    }
    
    public static Boolean ShowWidgetswithQLM(){
        Boolean tReturn = Boolean.FALSE;
        try {
            tReturn = Widget.GetUseWidgets();
        } catch (NoClassDefFoundError e) {
            System.out.println("ADM gemcalls : gemstone class not found '" + e + "'");
        }
        if (tReturn){
            try {
                tReturn = Widget.ShowWidgets();
            } catch (NoClassDefFoundError e) {
                System.out.println("ADM gemcalls : gemstone class not found '" + e + "'");
            }
        }
        return tReturn;
    }

    public static Integer GetWidgetsWidth(){
        Integer tReturn = 7;
        try {
            tReturn = Widget.GetWidgetsWidth();
        } catch (NoClassDefFoundError e) {
            System.out.println("ADM gemcalls : gemstone class not found '" + e + "'");
        }
        return tReturn;
    }

    public static Boolean WidgetsUseTabStyle(){
        Boolean tReturn = Boolean.FALSE;
        try {
            tReturn = Widget.UseTabStyle();
        } catch (NoClassDefFoundError e) {
            System.out.println("ADM gemcalls : gemstone class not found '" + e + "'");
        }
        return tReturn;
    }
}
