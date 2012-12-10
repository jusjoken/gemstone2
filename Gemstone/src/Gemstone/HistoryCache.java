/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import sagex.UIContext;

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
    public void Add(String Key, Object Item){
        history.put(Key, new HistoryItem(Item));
        logHistory(Key);
    }
    public void Add(String Key, Object Item, Integer Index){
        history.put(Key, new HistoryItem(Item,Index));
        logHistory(Key);
    }
    public void Add(String Key, Object Item, Integer Index, String TableCell){
        //when the TableCell is included then combine with Key for a more specific key
        history.put(TableCellKey(Key,TableCell), new HistoryItem(Item,Index,TableCell));
        logHistory(TableCellKey(Key,TableCell));
    }
    private void logHistory(String Key){
        LOG.debug("HISTORY Added '" + Key + "'");
        for (String tKey:history.keySet()){
            LOG.debug("HISTORY item '" + tKey + "' Item '" + Item(tKey) + "'");
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

    public boolean SetFocus(String Key, Object DefaultItem, Integer DefaultIndex){
        if (Contains(Key)){
            //determine the index to use
            Integer tIndex = DefaultIndex;
            if (Index(Key)==null || Index(Key).equals(-1)){
                //leave as the default passed in
            }else{
                tIndex = Index(Key);
            }
            //try to set the focus for the saved history item
            sagex.api.Global.EnsureVisibilityForVariable(tUI,TableCell(Key),Item(Key),tIndex);
            boolean set = sagex.api.Global.EnsureVisibilityForVariable(tUI,TableCell(Key),Item(Key),tIndex);
            if (set){
                sagex.api.Global.SetFocusForVariable(tUI, TableCell(Key), Item(Key));
                LOG.debug("SetFocus: Key '" + Key + "' TableCell '" + TableCell(Key) + "' Index '" + tIndex + "' Item '" + Item(Key) + "'");
                return true;
            }else{
                sagex.api.Global.EnsureVisibilityForVariable(tUI,TableCell(Key),DefaultItem,tIndex);
                sagex.api.Global.SetFocusForVariable(tUI, TableCell(Key), DefaultItem);
                LOG.debug("SetFocus: Key '" + Key + "'  TableCell '" + TableCell(Key) + "' Index '" + tIndex + "' Item '" + DefaultItem + "'");
                return false;
            }
        }else{
            sagex.api.Global.EnsureVisibilityForVariable(tUI,TableCell(Key),DefaultItem,DefaultIndex);
            sagex.api.Global.SetFocusForVariable(tUI, TableCell(Key), DefaultItem);
            LOG.debug("SetFocus: Key '" + Key + "'  TableCell '" + TableCell(Key) + "' Index '" + DefaultIndex + "' Item '" + DefaultItem + "'");
            return false;
        }
    }
    public boolean SetFocus(String Key, String TableCell, Object DefaultItem, Integer DefaultIndex){
        return SetFocus(TableCellKey(Key, TableCell),DefaultItem,DefaultIndex);
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
    }
}
