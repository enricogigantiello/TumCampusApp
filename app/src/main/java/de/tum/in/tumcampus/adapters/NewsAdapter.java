package de.tum.in.tumcampus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.tum.in.tumcampus.R;
import de.tum.in.tumcampus.auxiliary.NetUtils;
import de.tum.in.tumcampus.auxiliary.Utils;

public class NewsAdapter extends CursorAdapter {
    private final LayoutInflater mInflater;
    private final NetUtils net;

    public NewsAdapter(Context context, Cursor c) {
        super(context, c, false);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        net = new NetUtils(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor c = (Cursor) getItem(position);
        return c.getString(1).equals("2") ? 0 : 1;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return newNewsView(mInflater, cursor, viewGroup);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        bindNewsView(net, view, cursor);
    }

    public static View newNewsView(LayoutInflater inflater, Cursor cursor, ViewGroup parent) {
        View card;
        if (cursor.getInt(1) == 2) {
            card = inflater.inflate(R.layout.card_news_film_item, parent, false);
        } else {
            card = inflater.inflate(R.layout.card_news_item, parent, false);
        }
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) card.findViewById(R.id.news_title);
        holder.img = (ImageView) card.findViewById(R.id.news_img);
        holder.src_date = (TextView) card.findViewById(R.id.news_src_date);
        holder.src_icon = (ImageView) card.findViewById(R.id.news_src_icon);
        holder.src_title = (TextView) card.findViewById(R.id.news_src_title);
        card.setTag(holder);
        return card;
    }

    public static void bindNewsView(NetUtils net, View view, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // Set image
        String imgUrl = cursor.getString(4);
        if (imgUrl == null || imgUrl.isEmpty() || imgUrl.equals("null")) {
            holder.img.setVisibility(View.GONE);
        } else {
            holder.img.setVisibility(View.VISIBLE);
            net.loadAndSetImage(imgUrl, holder.img);
        }

        String title = cursor.getString(2);
        if (cursor.getInt(1) == 2) {
            title = title.replaceAll("^[0-9]+\\. [0-9]+\\. [0-9]+:[ ]*","");
        }
        holder.title.setText(title);

        // Adds date
        String date = cursor.getString(5);
        Date d = Utils.getISODateTime(date);
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        holder.src_date.setText(sdf.format(d));

        holder.src_title.setText(cursor.getString(8));
        String icon = cursor.getString(7);
        if (icon.isEmpty() || icon.equals("null")) {
            holder.src_icon.setImageResource(R.drawable.ic_comment);
        } else {
            net.loadAndSetImage(icon, holder.src_icon);
        }
    }

    public static class ViewHolder {
        ImageView img;
        TextView title;
        TextView src_date;
        TextView src_title;
        ImageView src_icon;
    }
}
