package net.oschina.gitapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Notification;
import net.oschina.gitapp.bean.ProjectNotification;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;

import java.util.List;

/**
 * 通知列表适配器
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *         <p/>
 *         最后更新：2014-07-08
 *         更新者：火蚁
 * @created 2014-07-07
 * @reason 改用分类的适配器
 */
public class NotificationAdapter extends BaseExpandableListAdapter {

    private Context mContext;

    private List<List<Notification>> mData;

    private List<ProjectNotification> mGroups;

    private LayoutInflater mInflater;

    private class GroupViewHolder {
        public ImageView mGroupFace;
        public TextView mGroupUserName;
        public TextView mGroupName;
        public TextView mGroupCount;
    }

    private class ChildViewHolder {
        public ImageView face;
        public TextView user_name;
        public TextView title;
        public TextView date;//日期
    }

    public NotificationAdapter(Context context, List<List<Notification>> data, List<ProjectNotification> mGroups) {
        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(mContext);
        this.mGroups = mGroups;
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).size();
    }

    @Override
    public List<Notification> getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public Notification getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // 返回分组的view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_group_item_notification, null);

            holder = new GroupViewHolder();

            holder.mGroupFace = (ImageView) convertView.findViewById(R.id.group_face);
            holder.mGroupUserName = (TextView) convertView.findViewById(R.id.group_username);
            holder.mGroupCount = (TextView) convertView.findViewById(R.id.group_count);
            holder.mGroupName = (TextView) convertView.findViewById(R.id.group_name);

            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        ProjectNotification pn = mGroups.get(groupPosition);

        // 加载头像
        String portrait = pn.getOwner().getPortrait() == null ? "" : pn.getOwner().getPortrait();
        if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
            holder.mGroupFace.setImageResource(R.drawable.mini_avatar);
        } else {
            String portraitURL = pn.getOwner().getPortrait();
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.mini_avatar)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader.getInstance().displayImage(portraitURL, holder.mGroupFace, options);
        }

        holder.mGroupUserName.setText(pn.getOwner().getName());

        holder.mGroupName.setText(pn.getName());

        holder.mGroupCount.setText(mData.get(groupPosition).size() + "");

        // 更换状态图标
        ImageView parentImageViw = (ImageView) convertView.findViewById(R.id.group_arrow);
        //判断isExpanded就可以控制是按下还是关闭，同时更换图片
        if (isExpanded) {
            parentImageViw.setBackgroundResource(R.drawable.notice_group_arrow_down);
        } else {
            parentImageViw.setBackgroundResource(R.drawable.notice_group_arrow_up);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_notification, null);

            holder = new ChildViewHolder();

            holder.face = (ImageView) convertView.findViewById(R.id.notification_listitem_userface);
            holder.user_name = (TextView) convertView.findViewById(R.id.notification_listitem_name);
            holder.title = (TextView) convertView.findViewById(R.id.notification_listitem_title);
            holder.date = (TextView) convertView.findViewById(R.id.notification_listitem_date);

            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        final Notification notification = getChild(groupPosition, childPosition);

        // 1.加载头像
        String portrait = notification.getUserinfo().getPortrait() == null ? "" : notification.getUserinfo().getPortrait();
        if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
            holder.face.setImageResource(R.drawable.mini_avatar);
        } else {
            String portraitURL = notification.getUserinfo().getPortrait();
            // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.mini_avatar)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader.getInstance().displayImage(portraitURL, holder.face, options);
        }

        holder.face.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = notification.getUserinfo();
                if (user == null) {
                    return;
                }
                UIHelper.showUserInfoDetail(mContext, user, null);
            }
        });

        holder.user_name.setText(notification.getUserinfo().getName());

        holder.title.setText(notification.getTitle());

        holder.date.setText(StringUtils.friendly_time(notification.getCreated_at()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
