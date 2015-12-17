/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Gemstone2;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author SBANTA
 * @author JUSJOKEN
 * - 9/29/2011 - minor code flow updating, log message changes for testing
 * - 04/04/2012 - updated for Gemstone
 */
public class Dividers {

    static private final Logger LOG = Logger.getLogger(Dividers.class);
    public static Class SageClass = null;

    public static List<Object> AddDividers(Object[] current,String SortMethod){

        List<Object> withdividers= new ArrayList<Object>();
        LOG.debug("GettingDividerCall from sort method="+SortMethod);
        String CurrentClassCall = GetCurrentClassCall(SortMethod);
        LOG.debug("DividerCall="+CurrentClassCall);
        Object CurrDivider=0;

        for(Object curr:current){
            LOG.debug("Get current Divider");
            Object ThisDivider =ClassFromString.GetDateClass(CurrentClassCall, curr);
            LOG.debug("Checking CurrentGroup="+ThisDivider+" : Against"+CurrDivider);
            if(!CurrDivider.equals(ThisDivider)){
                LOG.debug("NewGroupFor="+ThisDivider);
                CurrDivider=ThisDivider;
                withdividers.add(CurrDivider);
            }
            withdividers.add(curr);
        }
        return withdividers;
    }

    public static boolean IsDivider(Object cell){
        return !cell.getClass().equals(SageClass);
    }

    public static String GetCurrentClassCall(String current){
        if(current.equals(SortMethods.Seasons)){
            return "GetSeasonNumberDivider";
        }
        else if(current.equals(SortMethods.EpisodeTitle)){
            return "GetEpisodeTitleDivider";
        }
        else{
            return "GetTimeAdded";
        }
     }
}
