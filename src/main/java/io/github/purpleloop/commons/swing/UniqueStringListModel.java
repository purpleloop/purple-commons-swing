package io.github.purpleloop.commons.swing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;

/** A list model based on a list of strings. 
 * Entries of this model are unique and can be sorted.
 */
public class UniqueStringListModel extends AbstractListModel<String> {

    /** Serial tag. */
    private static final long serialVersionUID = 5628744345761664799L;

    /** The list of strings. */
    private List<String> list;

    /** A comparator on strings. */
    private transient Comparator<String> comparator;

    /**
     * Constructs a StringListModel from a collection of strings.
     * 
     * @param stringCollection collection of strings
     */
    public UniqueStringListModel(Collection<String> stringCollection) {
        super();
        this.list = new ArrayList<>(stringCollection.size());
        this.list.addAll(stringCollection);
    }

    /**
     * Constructs a StringListModel from a collection of strings with a provided
     * comparator.
     * 
     * @param stringCollection collection of strings
     * @param comparator the comparator to use
     */
    public UniqueStringListModel(Collection<String> stringCollection, Comparator<String> comparator) {
        super();
        this.list = new ArrayList<>(stringCollection.size());
        this.list.addAll(stringCollection);
        setComparator(comparator);
        sort();
    }

    /**
     * Constructs an empty StringListModel with a provided comparator.
     * 
     * @param comparator the comparator to use
     */
    public UniqueStringListModel(Comparator<String> comparator) {
        this.list = new ArrayList<>();
        setComparator(comparator);
    }

    /**
     * @param comparator the comparator to use
     */
    public void setComparator(Comparator<String> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int getSize() {
        return list.size();
    }

    /**
     * Add the given string to the model.
     * 
     * @param entry string entry to add
     */
    public void add(String entry) {
        if (!list.contains(entry)) {
            list.add(entry);
            sort();
            fireIntervalAdded(this, 0, list.size() - 1);
        }
    }

    /**
     * Add all the provided strings in the list model.
     * 
     * @param stringCollection a collection of strings to add
     */
    public void addAll(Collection<String> stringCollection) {
        list.addAll(stringCollection);
        sort();
        fireContentsChanged(this, 0, list.size() - 1);
    }

    @Override
    public String getElementAt(int index) {
        return list.get(index);
    }

    /**
     * Remove the given string from the model.
     * 
     * @param entry string entry to remove
     */
    public void remove(String entry) {
        int i = list.indexOf(entry);
        if (i != -1) {
            list.remove(i);
            fireIntervalRemoved(this, i, i);
        }
    }

    /**
     * Drop all contained strings of this model in the provided target.
     * 
     * @param targetModel a StringListModel where to drop all strings
     */
    public void dropAllTo(UniqueStringListModel targetModel) {
        targetModel.addAll(list);
        list.clear();
        fireContentsChanged(this, 0, 0);
    }

    /** Sort the strings. */
    private void sort() {
        if (comparator != null) {
            Collections.sort(list, comparator);
        } else {
            Collections.sort(list);
        }
    }

    /** @return the list of strings */
    public List<String> getList() {
        return list;
    }

    /** Clears the strings list of this model. */
    public void clear() {
        list.clear();
        fireContentsChanged(this, 0, 0);
    }

}
