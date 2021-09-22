package eu.faircode.email;

/*

*/

import android.content.Context;

import java.util.Comparator;

public abstract class EntityOrder {
    public Integer order;

    abstract Long getSortId();

    abstract String[] getSortTitle(Context context);

    abstract Comparator getComparator(Context context);
}
