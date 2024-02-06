package com.kahr.siddiqiamosque;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Events> {

    Context context;
    List<Events> arrayListEvents;


    public MyAdapter(@NonNull Context context, List<Events> arrayListEvents) {
        super(context, R.layout.custom_list_item,arrayListEvents);

        this.context = context;
        this.arrayListEvents = arrayListEvents;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_item,null,true);

        TextView tvName= view.findViewById(R.id.eventName);
        TextView tvDescription = view.findViewById(R.id.eventDescription);
        TextView tveventDate = view.findViewById(R.id.eventDate);


        tvName.setText(arrayListEvents.get(position).getEventName());
        tvDescription.setText(arrayListEvents.get(position).getDescription());
        tveventDate.setText(arrayListEvents.get(position).getEventDate());

        return view;
    }
}
