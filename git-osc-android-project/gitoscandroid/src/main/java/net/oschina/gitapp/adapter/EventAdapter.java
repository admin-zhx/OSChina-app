package net.oschina.gitapp.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.HtmlRegexpUtils;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;

import java.util.List;

/**
 * 个人动态列表适配器
 * 
 * @created 2014-05-20 下午15：28
 * @author 火蚁（http://my.oschina.net/LittleDY）
 */
public class EventAdapter extends CommonAdapter<Event> {

    public EventAdapter(Context context,
                        int resource) {
        super(context, resource);
    }

    @Override
    public void convert(ViewHolder vh, final Event event) {
        displayContent(vh, event);
    }

    private void displayContent(ViewHolder vh, final Event event) {

        // 1.加载头像
        vh.setImageForUrl(R.id.iv_portrait, event.getAuthor().getNew_portrait());
        vh.getView(R.id.iv_portrait).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = event.getAuthor();
                if (user == null) {
                    return;
                }
                UIHelper.showUserInfoDetail(mContext, user, null);
            }
        });

        // 2.显示相关信息
        SpannableString title = UIHelper.parseEventTitle(event
                .getAuthor().getName(), event.getProject().getOwner().getName()
                + " / " + event.getProject().getName(), event);
        vh.setText(R.id.tv_name, title);

        // commits信息的显示
        LinearLayout commitLists = vh.getView(R.id.ll_commits_list);
        commitLists.setVisibility(View.GONE);
        commitLists.removeAllViews();
        if (event.getData() != null) {
            List<Commit> commits = event.getData().getCommits();
            if (commits != null && commits.size() > 0) {
                showCommitInfo(commitLists, commits);
                commitLists.setVisibility(View.VISIBLE);
            }
        }

        TextView content = vh.getView(R.id.tv_content);
        content.setVisibility(View.GONE);
        // 评论的内容
        if (event.getEvents().getNote() != null
                && event.getEvents().getNote().getNote() != null) {
            content.setText(HtmlRegexpUtils.filterHtml(event
                    .getEvents().getNote().getNote()));
            content.setVisibility(View.VISIBLE);
        }

        // issue的title
        if (event.getEvents().getIssue() != null
                && event.getEvents().getNote() == null) {
            content.setText(event.getEvents().getIssue()
                    .getTitle());
            content.setVisibility(View.VISIBLE);
        }

        // pr的title
        if (event.getEvents().getPull_request() != null
                && event.getEvents().getNote() == null) {
            content.setText(event.getEvents().getPull_request()
                    .getTitle());
            content.setVisibility(View.VISIBLE);
        }
        vh.setText(R.id.tv_date, StringUtils.friendly_time(event
                .getUpdated_at()));
    }

    private void showCommitInfo(LinearLayout layout, List<Commit> commits) {
        if (commits.size() >= 2) {
            addCommitItem(layout, commits.get(0));
            addCommitItem(layout, commits.get(1));
        } else {
            for (Commit commit : commits) {
                addCommitItem(layout, commit);
            }
        }
    }

    /**
     * 添加commit项
     * 
     * @param layout
     * @param commit
     */
    private void addCommitItem(LinearLayout layout, Commit commit) {
        View v = mInflater.inflate(R.layout.list_item_event_commits, null);
        ((TextView) v.findViewById(R.id.event_commits_listitem_commitid))
                .setText(commit.getId());
        ((TextView) v.findViewById(R.id.event_commits_listitem_username))
                .setText(commit.getAuthor().getName());
        ((TextView) v.findViewById(R.id.event_commits_listitem_message))
                .setText(commit.getMessage());
        layout.addView(v);
    }
}
