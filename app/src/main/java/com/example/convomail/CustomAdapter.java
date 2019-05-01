package com.example.convomail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Datamodel> implements View.OnClickListener {
    private ArrayList<Datamodel> dataSet;
    Context mContext;

    @Override
    public void onClick(View view) {
        int position=(Integer) view.getTag();
        Object object= getItem(position);
        Datamodel dataModel=(Datamodel)object;
        switch (view.getId()){
            case R.id.remove_attachment2:
                dataSet.remove(position);
                notifyDataSetChanged();
        }
    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Datamodel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.attachmentlayout, parent, false);
            viewHolder.attachment_name = (TextView) convertView.findViewById(R.id.attachment_name2);
            viewHolder.remove = convertView.findViewById(R.id.remove_attachment2);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.attachment_name.setText(dataModel.getFileName());

        viewHolder.remove.setOnClickListener(this);
        viewHolder.remove.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
    private static class ViewHolder{
        TextView attachment_name;
        ImageButton remove;
    }
    public CustomAdapter(ArrayList<Datamodel> data, Context c){
        super(c, R.layout.attachmentlayout, data);
        this.dataSet = data;
        this.mContext = c;
    }

}
