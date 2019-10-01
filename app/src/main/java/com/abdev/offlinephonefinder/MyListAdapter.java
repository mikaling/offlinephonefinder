package com.abdev.offlinephonefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> ID;
    ArrayList<String> Feature;
    ArrayList<String> Code;


    public MyListAdapter(
            Context context2,
            ArrayList<String> id,
            ArrayList<String> feature,
            ArrayList<String> code
    )
    {

        this.context = context2;
        this.ID = id;
        this.Feature = feature;
        this.Code = code;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return ID.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View child, ViewGroup parent) {

        Holder holder;

        LayoutInflater layoutInflater;

        if (child == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            child = layoutInflater.inflate(R.layout.items, null);

            holder = new Holder();

            holder.ID_TextView = (TextView) child.findViewById(R.id.textViewID);
            holder.Feature_TextView = (TextView) child.findViewById(R.id.textViewFEATURE);
            holder.CodeTextView = (TextView) child.findViewById(R.id.textViewCODE);

            child.setTag(holder);

        } else {

            holder = (Holder) child.getTag();
        }
        holder.ID_TextView.setText(ID.get(position));
        holder.Feature_TextView.setText(Feature.get(position));
        holder.CodeTextView.setText(Code.get(position));

        return child;
    }

    public class Holder {

        TextView ID_TextView;
        TextView Feature_TextView;
        TextView CodeTextView;
    }

}
