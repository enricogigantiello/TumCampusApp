package de.tum.in.tumcampus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.tum.in.tumcampus.R;
import de.tum.in.tumcampus.auxiliary.DateUtils;
import de.tum.in.tumcampus.auxiliary.Utils;
import de.tum.in.tumcampus.models.managers.ChatRoomManager;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * This class handles the view output of the results for finding lectures via
 * TUMOnline used in {@link de.tum.in.tumcampus.activities.LecturesPersonalActivity}
 * and {@link de.tum.in.tumcampus.activities.ChatRoomsActivity}. It
 * implements {@link se.emilsjolander.stickylistheaders.StickyListHeadersAdapter} to
 * show semester info as sticky header.
 */

public class ChatRoomListAdapter extends CursorAdapter implements StickyListHeadersAdapter {

    // the layout of the list
    static class ViewHolder {
        TextView tvDozent;
        TextView tvLectureName;
        TextView tvMembers;
        TextView tvLastmsg;
        LinearLayout llAdditionalInfo;
    }

    private final ArrayList<String> filters;
    private final LayoutInflater mInflater;
    private final boolean showDateAndNumber;

    // constructor
    public ChatRoomListAdapter(Context context, Cursor results, int mode) {
        super(context, results, false);

        this.mInflater = LayoutInflater.from(context);
        this.filters = new ArrayList<>();
        this.showDateAndNumber = (mode == 1);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View convertView = mInflater.inflate(R.layout.activity_lectures_listview, viewGroup, false);
        ViewHolder holder = new ViewHolder();

        // set UI elements
        holder.tvLectureName = (TextView) convertView.findViewById(R.id.tvLectureName);
        holder.tvDozent = (TextView) convertView.findViewById(R.id.tvDozent);
        holder.tvMembers = (TextView) convertView.findViewById(R.id.tvMembers);
        holder.tvLastmsg = (TextView) convertView.findViewById(R.id.tvLastmsg);
        holder.llAdditionalInfo = (LinearLayout) convertView.findViewById(R.id.llAdditionalInfo);
        convertView.findViewById(R.id.tvTypeSWSSemester).setVisibility(View.GONE);

        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvLectureName.setText(cursor.getString(ChatRoomManager.COL_NAME));
        holder.tvDozent.setText(cursor.getString(9));

        if (showDateAndNumber) {
            holder.tvMembers.setText(cursor.getString(ChatRoomManager.COL_MEMBERS));
            holder.tvLastmsg.setText(DateUtils.getTimeOrDay(cursor.getString(8), context));
            holder.llAdditionalInfo.setVisibility(View.VISIBLE);
        }else{
            holder.tvDozent.setText(cursor.getString(ChatRoomManager.COL_CONTRIBUTOR));
        }

        Utils.logv("members " + cursor.getString(ChatRoomManager.COL_MEMBERS) + " " + cursor.getString(8));
    }

    // Generate header view
    @Override
    public View getHeaderView(int pos, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.lecture_header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        Cursor item = (Cursor) getItem(pos);
        String semester = item.getString(ChatRoomManager.COL_SEMESTER);
        if (semester.isEmpty())
            semester = mContext.getString(R.string.my_chat_rooms);
        holder.text.setText(semester);
        return convertView;
    }

    @Override
    public long getHeaderId(int i) {
        Cursor item = (Cursor) getItem(i);
        String semester = item.getString(ChatRoomManager.COL_SEMESTER);
        if (!filters.contains(semester)) {
            filters.add(semester);
        }
        return filters.indexOf(semester);
    }

    static class HeaderViewHolder {
        TextView text;
    }
}
