package com.oceansoftwares.store.activities.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.oceansoftwares.store.R;

import java.util.ArrayList;

public class CountryAdapter extends BaseAdapter implements Filterable {

    Context context;

    LayoutInflater inflter;
    ArrayList<String> countryList;
    public ArrayList<String> orig;

    public CountryAdapter(Context applicationContext, ArrayList<String> countryList) {
        this.context = context;
        this.countryList = countryList;

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public Object getItem(int i) {
        return countryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.simplelist, null);
        TextView country = (TextView) view.findViewById(R.id.textview);

        country.setText(countryList.get(i));

        return view;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<String> results = new ArrayList<String>();
                if (orig == null)
                    orig = countryList;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final String g : orig) {
                            if (g.toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                countryList = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
