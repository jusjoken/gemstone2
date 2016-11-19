/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import sagex.UIContext;
import sagex.phoenix.vfs.IMediaResource;

/**
 *
 * @author jusjoken
 */
public class HistoryCache {
    static private final Logger LOG = Logger.getLogger(HistoryCache.class);
    private Map<String,HistoryItem> history = new HashMap<String,HistoryItem>();
    private UIContext tUI = new UIContext(sagex.api.Global.GetUIContextName());
    public HistoryCache() {
    }
//    public void Add(String Key, Object Item){
//        history.put(Key, new HistoryItem(Item));
//        logHistory(Key);
//    }
//    public void Add(String Key, Object Item, Integer Index){
//        history.put(Key, new HistoryItem(Item,Index));
//        logHistory(Key);
//    }
//    public void Add(String Key, Object Item, Integer Index, String TableCell){
//        //when the TableCell is included then combine with Key for a more specific key
//        history.put(TableCellKey(Key,TableCell), new HistoryItem(Item,Index,TableCell));
//        logHistory(TableCellKey(Key,TableCell));
//    }
    public void Add(Object Item){
        IMediaResource imr = Source.ConvertToIMR(Item);
        if (imr!=null){
            String Key = phoenix.media.GetPath(phoenix.umb.GetParent(imr));
            history.put(Key, new HistoryItem(Item));
            logHistory(Key);
        }
    }
    public void Add(Object Item, Integer Index){
        IMediaResource imr = Source.ConvertToIMR(Item);
        if (imr!=null){
            String Key = phoenix.media.GetPath(phoenix.umb.GetParent(imr));
            history.put(Key, new HistoryItem(Item,Index));
            logHistory(Key);
        }
    }
    public void Add(Object Item, String TableCell){
        IMediaResource imr = Source.ConvertToIMR(Item);
        if (imr!=null){
            String Key = phoenix.media.GetPath(phoenix.umb.GetParent(imr));
            history.put(Key, new HistoryItem(Item,TableCell));
            logHistory(Key);
        }
    }
    public void Add(Object Item, Integer Index, String TableCell){
        IMediaResource imr = Source.ConvertToIMR(Item);
        if (imr!=null){
            String Key = phoenix.media.GetPath(phoenix.umb.GetParent(imr));
            history.put(Key, new HistoryItem(Item,Index,TableCell));
            logHistory(Key);
        }
    }
    private void logHistory(String Key){
        LOG.debug("HISTORY Added '" + Key + "'");
        for (String tKey:history.keySet()){
            LOG.debug("HISTORY item '" + tKey + "' Index/Table '" + Index(tKey)+ "/" + TableCell(tKey) + "' Item '" + Item(tKey) + "'");
        }
    }
    private String TableCellKey(String Key, String TableCell){
        return Key + util.ListToken + TableCell;
    }
    public boolean Contains(String Key){
        return history.containsKey(Key);
    }
    public boolean Contains(String Key, String TableCell){
        return Contains(TableCellKey(Key, TableCell));
    }
    public Object Item(String Key){
        if (Contains(Key)){
            return history.get(Key).Item;
        }else{
            return null;
        }
    }
    public Object Item(String Key, String TableCell){
        return Item(TableCellKey(Key, TableCell));
    }
    public Integer Index(String Key){
        if (Contains(Key)){
            return history.get(Key).Index;
        }else{
            return null;
        }
    }
    public Integer Index(String Key, String TableCell){
        return Index(TableCellKey(Key, TableCell));
    }
    public String TableCell(String Key){
        if (Contains(Key)){
            return history.get(Key).TableCell;
        }else{
            return null;
        }
    }
    public String TableCell(String Key, String TableCell){
        return TableCell(TableCellKey(Key, TableCell));
    }
    public void Clear(){
        history.clear();
    }
    public boolean Remove(String Key){
        if (Contains(Key)){
            history.remove(Key);
            return true;
        }
        return false;
    }
    public boolean Remove(String Key, String TableCell){
        return Remove(TableCellKey(Key, TableCell));
    }

    //pass in the current focuseditem and this function will set the focus based on history to the next or previous item
    public boolean SetFocus(Object Item, Object DefaultItem, Integer DefaultIndex, Boolean Next){
        return SetFocus(Item, DefaultItem, DefaultIndex, "VideoCell", Next);
    }
    public boolean SetFocus(Object Item, Object DefaultItem, Integer DefaultIndex, String TableCell, Boolean Next){
        LOG.debug("SetFocus: *** Next '" + Next + "' DefaultIndex '" + DefaultIndex + "' TableCell '" + TableCell + "' DefaultItem '" + DefaultItem + "' Item '" + Item + "'");
        IMediaResource imr = Source.ConvertToIMR(Item);
        if (imr==null){
            LOG.debug("SetFocus: null Item passed in - so using Defaults");
            return SetFocus(util.OptionNotFound, DefaultItem, DefaultIndex, TableCell);
        }
        String Key = "";
        if (Next==null){
            Key = phoenix.media.GetPath(phoenix.umb.GetParent(imr));
        }else if (Next){
            //determine the Key for the Next item
            Key = phoenix.media.GetPath(imr);
        }else{
            //determine the Key for the Previous item
            Key = phoenix.media.GetPath(phoenix.umb.GetParent(phoenix.umb.GetParent(imr)));
        }
        if (TableCell==null){
            TableCell = TableCell(Key);
        }
        return SetFocus(Key, DefaultItem, DefaultIndex, TableCell);
    }
    public boolean SetFocus(String Key, Object DefaultItem, Integer DefaultIndex){
        return SetFocus(Key, DefaultItem, DefaultIndex, TableCell(Key));
    }
    public boolean SetFocus(String Key, Object DefaultItem, Integer DefaultIndex, String TableCell){
        LOG.debug("SetFocus: *** looking for '" + Key + "' DefaultItem '" + DefaultItem + "' DefaultIndex '" + DefaultIndex + "' TableCell '" + TableCell + "'");
        if (Contains(Key)){
            //determine the index to use
            Integer tIndex = DefaultIndex;
            if (Index(Key)==null || Index(Key).equals(-1)){
                //leave as the default passed in
            }else{
                tIndex = Index(Key);
            }
            //try to set the focus for the saved history item
            sagex.api.Global.EnsureVisibilityForVariable(tUI,TableCell,Item(Key),tIndex);
            boolean set = sagex.api.Global.EnsureVisibilityForVariable(tUI,TableCell,Item(Key),tIndex);
            if (set){
                sagex.api.Global.SetFocusForVariable(tUI, TableCell, Item(Key));
                LOG.debug("SetFocus: found Key '" + Key + "' TableCell '" + TableCell + "' Index '" + tIndex + "' Item '" + Item(Key) + "'");
                return true;
            }else{
                sagex.api.Global.EnsureVisibilityForVariable(tUI,TableCell,DefaultItem,tIndex);
                sagex.api.Global.SetFocusForVariable(tUI, TableCell, DefaultItem);
                LOG.debug("SetFocus: not found in table '" + Key + "'  TableCell '" + TableCell + "' Index '" + tIndex + "' Item '" + DefaultItem + "'");
                return false;
            }
        }else{
            sagex.api.Global.EnsureVisibilityForVariable(tUI,TableCell,DefaultItem,DefaultIndex);
            sagex.api.Global.SetFocusForVariable(tUI, TableCell, DefaultItem);
            LOG.debug("SetFocus: using default as Key (" + Key + ") not found - TableCell '" + TableCell + "' Index '" + DefaultIndex + "' Item '" + DefaultItem + "'");
            return false;
        }
    }
    
    private class HistoryItem {
        private Integer Index = -1;
        private Object Item = null;
        private String TableCell = "VideoCell";
        public HistoryItem(Object Item) {
            this.Item = Item;
        }
        public HistoryItem(Object Item, Integer Index) {
            this.Item = Item;
            this.Index = Index;
        }
        public HistoryItem(Object Item, Integer Index, String TableCell) {
            this.Item = Item;
            this.Index = Index;
            this.TableCell = TableCell;
        }
        public HistoryItem(Object Item, String TableCell) {
            this.Item = Item;
            this.TableCell = TableCell;
        }
    }
}
