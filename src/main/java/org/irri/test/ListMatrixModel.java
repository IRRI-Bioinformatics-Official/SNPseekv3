package org.irri.test;

import org.zkoss.zkmax.zul.MatrixModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.ext.SelectionControl;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.ListDataEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.LinkedHashSet;

public class ListMatrixModel<E> implements MatrixModel<E, E, E, E>, ListModel<E>, Selectable<E> {
    
    private List<E> data;
    private List<ListDataListener> listeners = new ArrayList<>();
    private Set<E> selection = new LinkedHashSet<>();
    private SelectionControl selectionControl;
    
    public ListMatrixModel(List<E> data) {
        this.data = data != null ? data : new ArrayList<>();
    }
    
    // ==================== MatrixModel Methods ====================
    
    @Override
    public E getCellAt(E rowData, int col) {
        return rowData;
    }
    
    @Override
    public E getHeadAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            return data.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public int getHeadSize() {
        return data.size();
    }
    
    @Override
    public E getHeaderAt(E rowData, int col) {
        return null;
    }
    
    @Override
    public int getColumnSize() {
        return 1;
    }
    
    // ==================== ListModel Methods ====================
    
    @Override
    public void addListDataListener(ListDataListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    @Override
    public E getElementAt(int index) {
        if (index >= 0 && index < data.size()) {
            return data.get(index);
        }
        return null;
    }
    
    @Override
    public int getSize() {
        return data.size();
    }
    
    @Override
    public void removeListDataListener(ListDataListener listener) {
        listeners.remove(listener);
    }
    
    // ==================== Selectable Methods ====================
    
    @Override
    public Set<E> getSelection() {
        return new LinkedHashSet<>(selection);
    }
    
    @Override
    public void setSelection(Collection<? extends E> selection) {
        this.selection.clear();
        if (selection != null) {
            this.selection.addAll(selection);
        }
    }
    
    @Override
    public boolean isSelected(Object obj) {
        return selection.contains(obj);
    }
    
    @Override
    public boolean addToSelection(E obj) {
        if (obj != null && !selection.contains(obj)) {
            selection.add(obj);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean removeFromSelection(Object obj) {
        return selection.remove(obj);
    }
    
    @Override
    public void clearSelection() {
        selection.clear();
    }
    
    @Override
    public boolean isSelectionEmpty() {
        return selection.isEmpty();
    }
    
    @Override
    public boolean isMultiple() {
        return true;
    }
    
    @Override
    public void setMultiple(boolean multiple) {
    }
    
    @Override
    public SelectionControl getSelectionControl() {
        return selectionControl;
    }
    
    @Override
    public void setSelectionControl(SelectionControl selectionControl) {
        this.selectionControl = selectionControl;
    }
    
    // ==================== Utility Methods ====================
    
    public void clear() {
        int oldSize = data.size();
        data.clear();
        selection.clear();
        if (oldSize > 0) {
            fireEvent(ListDataEvent.CONTENTS_CHANGED, 0, Math.max(0, oldSize - 1));
        }
    }
    
    public void addAll(List<E> items) {
        if (items != null && !items.isEmpty()) {
            int startIndex = data.size();
            data.addAll(items);
            fireEvent(ListDataEvent.INTERVAL_ADDED, startIndex, data.size() - 1);
        }
    }
    
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    public int size() {
        return data.size();
    }
    
    public List<E> getData() {
        return data;
    }
    
    private void fireEvent(int type, int index0, int index1) {
        if (listeners.isEmpty()) {
            return;
        }
        
        ListDataEvent event = new ListDataEvent(this, type, index0, index1);
        for (ListDataListener listener : new ArrayList<>(listeners)) {
            listener.onChange(event);
        }
    }
}