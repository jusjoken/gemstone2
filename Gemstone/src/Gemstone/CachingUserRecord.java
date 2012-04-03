/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author SBANTA
 */
public class CachingUserRecord {

    private static String SName = sagex.api.Global.IsClient() ? "SageDiamondTeam" + sagex.api.Global.GetUIContextName() : "SageDiamondTeam";

    public static void main(String[] args) {
        Object[] stores = sagex.api.UserRecordAPI.GetAllUserRecords(SName);
        for (Object curr : stores) {
            String[] store = sagex.api.UserRecordAPI.GetUserRecordNames(curr);
            for (String currs : store) {
                System.out.println("CurrentStore=" + currs);
                System.out.println("Value=" + sagex.api.UserRecordAPI.GetUserRecordData(curr, currs));
            }
        }
    }

    public static void DeleteStoredLocations() {
        sagex.api.UserRecordAPI.DeleteAllUserRecords(SName);
    }

    public static Boolean HasStoredLocation(String ID, String Type) {
        Object Record = sagex.api.UserRecordAPI.GetUserRecord(SName, ID);
        if (Record == null) {
            return false;
        }

        String Curr = sagex.api.UserRecordAPI.GetUserRecordData(Record, Type);
        return Curr != null && !Curr.equals("");

    }

    public static Boolean HasStoredLocation(String ID) {
        Object Record = sagex.api.UserRecordAPI.GetUserRecord(SName, ID);
        System.out.println("Recordforfanaat=" + Record);
        return Record != null;


    }

    public static String[] GetAllStoresForID(String ID) {
        Object Record = sagex.api.UserRecordAPI.GetUserRecord(SName, ID);
        String[] Stores = sagex.api.UserRecordAPI.GetUserRecordNames(Record);
        return Stores;
    }

    public static void DeleteStoresForID(String ID) {
        Object Record = sagex.api.UserRecordAPI.GetUserRecord(SName, ID);
        sagex.api.UserRecordAPI.DeleteUserRecord(Record);

    }

    public static ArrayList<File> GetAllCacheLocationsForID(String ID) {
        ArrayList<File> Cached = new ArrayList<File>();
        Object Record = sagex.api.UserRecordAPI.GetUserRecord(SName, ID);
        String[] Stores = sagex.api.UserRecordAPI.GetUserRecordNames(Record);
        for (String curr : Stores) {
            Cached.add(new File(sagex.api.UserRecordAPI.GetUserRecordData(Record, curr)));
        }
        return Cached;
    }

    public static String GetStoredLocation(String ID, String Type) {
        Object Record = sagex.api.UserRecordAPI.GetUserRecord(SName, ID);
        return sagex.api.UserRecordAPI.GetUserRecordData(Record, Type);

    }

    public static void setStoredLocation(String ID, String Type, String Location) {
        sagex.api.UserRecordAPI.AddUserRecord(SName, ID);
        Object Record = sagex.api.UserRecordAPI.GetUserRecord(SName, ID);
        sagex.api.UserRecordAPI.SetUserRecordData(Record, Type, Location);

    }

    public static void deleteStoredLocation(String ID, String Type, String Location) {
        Object Record = sagex.api.UserRecordAPI.GetUserRecord(SName, ID);
        sagex.api.UserRecordAPI.DeleteUserRecord(Record);

    }

    public static String[] GetStoredFanart(String ID) {
        Object Record = sagex.api.UserRecordAPI.GetUserRecord(SName, ID);
        return sagex.api.UserRecordAPI.GetUserRecordNames(Record);

    }
}
