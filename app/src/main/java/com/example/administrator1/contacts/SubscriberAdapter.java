package com.example.administrator1.contacts;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class SubscriberAdapter extends BaseAdapter {
    private List<Subscriber> subscribers;
    private Context c;

    public SubscriberAdapter(List<Subscriber> subscribers, Context c) {
        this.subscribers = subscribers;
        this.c = c;
    }

    @Override
    public int getCount() {
        return subscribers.size();
    }

    @Override
    public Object getItem(int i) {
        return subscribers.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View friendView = convertView;
        if (friendView == null) {
            friendView = LayoutInflater.from(c).inflate(R.layout.change, null);
        }

        Subscriber p = subscribers.get(position);

        ((TextView) friendView.findViewById(R.id.tvEmail)).setText(p.getEmail());
        ((TextView) friendView.findViewById(R.id.tvGroup)).setText(p.getGroup());
        ((TextView) friendView.findViewById(R.id.tvHomeNumber)).setText(p.getHomeNumber());
        ((TextView) friendView.findViewById(R.id.tvNumber)).setText(p.getNumber());
        ((TextView) friendView.findViewById(R.id.tvName)).setText(p.getName());

        return friendView;
    }
}












