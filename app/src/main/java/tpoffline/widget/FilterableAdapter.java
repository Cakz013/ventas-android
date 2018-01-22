package tpoffline.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Cesar on 6/30/2017.
 */
public abstract class FilterableAdapter<ObjectType, ConstraintType> extends BaseAdapter implements Filterable {
    private static final String TAG = "FilterableAdapter";
    private List<ObjectType> mObjects;
    private List<ObjectType> mDisplayedObjects;
    private int mResourceId;
    private int mTextResourceId;
    private Boolean mOutOfSync;
    private boolean mNotifyOnChange;
    private Filter mFilter;
    private CharSequence mLastFilter;
    private Context mContext;
    private LayoutInflater mInflater;
    private final Object mFilterLock;

    public FilterableAdapter(Context context) {
        this(context, 0, 0, (List)null);
    }

    public FilterableAdapter(Context context, int resourceId) {
        this(context, resourceId, 0, (List)null);
    }

    public FilterableAdapter(Context context, List<ObjectType> objects) {
        this(context, 0, 0, objects);
    }

    public FilterableAdapter(Context context, int resourceId, List<ObjectType> objects) {
        this(context, resourceId, 0, objects);
    }

    public FilterableAdapter(Context context, int resourceId, int textResourceId, List<ObjectType> objects) {
        this.mTextResourceId = 0;
        this.mOutOfSync = Boolean.valueOf(false);
        this.mNotifyOnChange = false;
        this.mFilterLock = new Object();
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mResourceId = resourceId;
        this.mTextResourceId = textResourceId;
        this.mObjects = (List)(objects != null?objects:new ArrayList());
        this.mDisplayedObjects = new ArrayList(this.mObjects);
    }

    protected int getmTextResourceId() {
        return this.mTextResourceId;
    }

    protected int getmResourceId() {
        return this.mResourceId;
    }

    protected LayoutInflater getmInflater() {
        return this.mInflater;
    }

    public Context getContext() {
        return this.mContext;
    }

    protected List<ObjectType> getObjects() {
        return this.mObjects;
    }

    protected Object getFilterLock() {
        return this.mFilterLock;
    }

    protected boolean isOutOfSync() {
        return this.mOutOfSync.booleanValue();
    }

    protected void setOutOfSync(boolean outOfSync) {
        this.mOutOfSync = Boolean.valueOf(outOfSync);
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        this.mNotifyOnChange = notifyOnChange;
    }

    public void add(ObjectType object) {
        List var2 = this.mObjects;
        synchronized(this.mObjects) {
            this.mObjects.add(object);
        }

        if(this.mNotifyOnChange) {
            this.notifyDataSetChanged();
        }

    }

    public void addAll(Collection<? extends ObjectType> collection) {
        List var2 = this.mObjects;
        synchronized(this.mObjects) {
            this.mObjects.addAll(collection);
        }

        if(this.mNotifyOnChange) {
            this.notifyDataSetChanged();
        }

    }

    public void addAll(ObjectType... objects) {
        List var2 = this.mObjects;
        synchronized(this.mObjects) {
            Object[] var6 = objects;
            int var5 = objects.length;
            int var4 = 0;

            while(true) {
                if(var4 >= var5) {
                    break;
                }

                Object object = var6[var4];
                this.mObjects.add((ObjectType) object);
                ++var4;
            }
        }

        if(this.mNotifyOnChange) {
            this.notifyDataSetChanged();
        }

    }

    public void insert(ObjectType object, int index) {
        List var3 = this.mObjects;
        synchronized(this.mObjects) {
            this.mObjects.add(index, object);
        }

        if(this.mNotifyOnChange) {
            this.notifyDataSetChanged();
        }

    }

    public void remove(ObjectType object) {
        List var2 = this.mObjects;
        synchronized(this.mObjects) {
            this.mObjects.remove(object);
        }

        if(this.mNotifyOnChange) {
            this.notifyDataSetChanged();
        }

    }

    public void clear() {
        List var1 = this.mObjects;
        synchronized(this.mObjects) {
            this.mObjects.clear();
        }

        if(this.mNotifyOnChange) {
            this.notifyDataSetChanged();
        }

    }

    public void sort(Comparator<? super ObjectType> comparator) {
        List var2 = this.mObjects;
        synchronized(this.mObjects) {
            Collections.sort(this.mObjects, comparator);
        }

        if(this.mNotifyOnChange) {
            this.notifyDataSetChanged();
        }

    }

    public void notifyDataSetChanged() {
        Object var2 = this.mFilterLock;
        boolean reapplyFilter;
        synchronized(this.mFilterLock) {
            reapplyFilter = (this.mOutOfSync = Boolean.valueOf(this.mLastFilter != null)).booleanValue();
        }

        if(reapplyFilter) {
            this.getFilter().filter(this.mLastFilter);
        } else {
            List var5 = this.mObjects;
            synchronized(this.mObjects) {
                this.mDisplayedObjects = new ArrayList(this.mObjects);
            }
        }

        this.doNotifyDataSetChanged();
    }

    protected void doNotifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public int getCount() {
        return this.mDisplayedObjects.size();
    }

    public ObjectType getItem(int position) {
        return this.mDisplayedObjects.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null) {
            if(this.mResourceId == 0) {
                throw new IllegalStateException("No view specified for this Adapter. Construct with resource ID, or override getView()");
            }

            view = this.mInflater.inflate(this.mResourceId, parent, false);
        } else {
            view = convertView;
        }

        TextView textView;
        try {
            if(this.mTextResourceId == 0) {
                textView = (TextView)view;
            } else {
                textView = (TextView)view.findViewById(this.mTextResourceId);
            }
        } catch (ClassCastException var7) {
            throw new IllegalStateException("This Adapter needs a text view. Pass proper resource IDs on construction, or override getView()");
        }

        Object item = this.getItem(position);
        textView.setText(item.toString());
        return view;
    }

    public Filter getFilter() {
        if(this.mFilter == null) {
            this.mFilter = new FilterableAdapter.ExtensibleFilter();
        }

        return this.mFilter;
    }

    protected abstract ConstraintType prepareFilter(CharSequence var1);

    protected abstract boolean passesFilter(Object var1, Object var2);

    protected class ExtensibleFilter extends Filter {
        protected ExtensibleFilter() {
        }

        protected FilterResults performFiltering(CharSequence constraintSeq) {
            synchronized(FilterableAdapter.this.mFilterLock) {
                if(!FilterableAdapter.this.mOutOfSync.booleanValue() && FilterableAdapter.this.mLastFilter != null && FilterableAdapter.this.mLastFilter.equals(constraintSeq)) {
                    return null;
                }

                FilterableAdapter.this.mOutOfSync = Boolean.valueOf(false);
                FilterableAdapter.this.mLastFilter = constraintSeq;
            }

            ArrayList filteredObjects;
            synchronized(FilterableAdapter.this.mObjects) {
                filteredObjects = new ArrayList(FilterableAdapter.this.mObjects);
            }

            if(constraintSeq == null) {
                return this.resultsFromList(filteredObjects);
            } else {
                Object constraint = FilterableAdapter.this.prepareFilter(constraintSeq);
                ListIterator it = filteredObjects.listIterator();

                while(it.hasNext()) {
                    Object item = it.next();
                    if(!FilterableAdapter.this.passesFilter(item, constraint)) {
                        it.remove();
                    }
                }

                return this.resultsFromList(filteredObjects);
            }
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results != null) {
                FilterableAdapter.this.mDisplayedObjects = (List)results.values;
            }

            FilterableAdapter.this.doNotifyDataSetChanged();
        }

        protected FilterResults resultsFromList(List<ObjectType> list) {
            FilterResults fr = new FilterResults();
            fr.values = list;
            fr.count = list.size();
            return fr;
        }
    }
}
