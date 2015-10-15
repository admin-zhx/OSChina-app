package com.bill.mygitosc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bill.mygitosc.R;
import com.bill.mygitosc.bean.Commits;
import com.bill.mygitosc.bean.Event;
import com.bill.mygitosc.common.BitmapCache;
import com.bill.mygitosc.utils.EventUtils;
import com.bill.mygitosc.utils.TimeUtils;
import com.bill.mygitosc.utils.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liaobb on 2015/7/27.
 */
public class EventAdapter extends BaseStateRecyclerAdapter<Event> {
    private ImageLoader mImageLoader;
    private boolean noPictureMode;
    //private ImageLoader.ImageListener listener;

   /* private View.OnClickListener eventClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Event event = (Event) v.getTag();
            if(event.getEvents().getIssue()==null){
                Intent intent = new Intent(context, ViewProjectInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ViewProjectInfoActivity.VIEW_PROJECT_INFO, event.getProject());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        }
    };*/

    public EventAdapter(Context context) {
        super(context);
        RequestQueue mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        noPictureMode = Utils.checkNoPicMode(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BaseStateRecyclerAdapter.TYPE_ITEM) {
            return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.recycleview_event_item, parent, false));
        } else {
            return new FootViewHolder(LayoutInflater.from(context).inflate(R.layout.foot_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventViewHolder) {
            EventViewHolder eventHolder = (EventViewHolder) holder;
            Event event = mData.get(position);
            String title = EventUtils.parseEventTitle(context, event
                    .getAuthor().getName(), event.getProject().getOwner().getName()
                    + "/" + event.getProject().getName(), event);
            eventHolder.ev_title.setText(title);

            if (!TextUtils.isEmpty(event.getUpdated_at())) {
                String updateTime = TimeUtils.friendlyFormat(context, event.getUpdated_at());
                if (!TextUtils.isEmpty(updateTime)) {
                    eventHolder.ev_data.setText(updateTime);
                }
            }

            eventHolder.ev_all_commits_list.setVisibility(View.GONE);
            eventHolder.ev_all_commits_list.removeAllViews();
            if (event.getData() != null) {
                List<Commits> commits = event.getData().getCommits();
                if (commits != null && commits.size() > 0) {
                    showCommitInfo(eventHolder.ev_all_commits_list, commits);
                    eventHolder.ev_all_commits_list.setVisibility(View.VISIBLE);
                }
            }

            if (!noPictureMode) {
                String portraitURL = event.getAuthor().getNew_portrait();
                if (portraitURL.endsWith("portrait.gif")) {
                    eventHolder.ev_portrait.setImageResource(R.drawable.mini_avatar);
                } else {
                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(eventHolder.ev_portrait,
                            R.drawable.mini_avatar, R.drawable.mini_avatar);
                    mImageLoader.get(portraitURL, listener);
                }
            }

//            eventHolder.itemView.setTag(event);
//            eventHolder.itemView.setOnClickListener(eventClickListener);

        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.foot_hint.setText(getStateDescription());
            footViewHolder.foot_progressBar.setVisibility(getProgressBarVisiable());
        }
    }

    private void showCommitInfo(LinearLayout layout, List<Commits> commits) {
        if (commits.size() >= 2) {
            addCommitItem(layout, commits.get(0));
            addCommitItem(layout, commits.get(1));
        } else {
            for (Commits commit : commits) {
                addCommitItem(layout, commit);
            }
        }
    }

    /**
     * ÃÌº”commitœÓ
     */
    private void addCommitItem(LinearLayout layout, Commits commit) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_event_commits, null);
        ((TextView) v.findViewById(R.id.event_commits_listitem_commitid))
                .setText(commit.getId());
        ((TextView) v.findViewById(R.id.event_commits_listitem_username))
                .setText(commit.getAuthor().getName());
        ((TextView) v.findViewById(R.id.event_commits_listitem_message))
                .setText(commit.getMessage());
        layout.addView(v);
    }


    class EventViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ev_portrait;

        TextView ev_title;
        TextView ev_description;
        LinearLayout ev_all_commits_list;
        TextView ev_data;

        public EventViewHolder(View itemView) {
            super(itemView);
            ev_portrait = (CircleImageView) itemView.findViewById(R.id.event_portrait);
            ev_title = (TextView) itemView.findViewById(R.id.event_title);
            ev_description = (TextView) itemView.findViewById(R.id.event_description);
            ev_all_commits_list = (LinearLayout) itemView.findViewById(R.id.event_all_commits_list);
            ev_data = (TextView) itemView.findViewById(R.id.event_date);
        }
    }
}
