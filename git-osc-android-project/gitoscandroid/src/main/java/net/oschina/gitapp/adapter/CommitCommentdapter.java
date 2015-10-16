package net.oschina.gitapp.adapter;

import android.content.Context;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Comment;
import net.oschina.gitapp.common.HtmlRegexpUtils;

/**
 * issue的评论列表适配器
 * @created 2014-06-16
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class CommitCommentdapter extends CommonAdapter<Comment> {
	
	public CommitCommentdapter(Context context, int resource) {
		super(context, resource);
	}

    @Override
    public void convert(ViewHolder vh, Comment comment) {
        // 1.加载头像
        String portraitURL = comment.getAuthor().getNew_portrait();
        if (portraitURL.endsWith("portrait.gif")) {
            vh.setImageResource(R.id.commit_comment_listitem_userface, R.drawable.mini_avatar);
        } else {
            vh.setImageForUrl(R.id.commit_comment_listitem_userface, portraitURL);
        }

        // 2.显示相关信息
        vh.setText(R.id.commit_comment_listitem_username, comment.getAuthor().getName());
        vh.setText(R.id.commit_comment_listitem_body, HtmlRegexpUtils.filterHtml(comment.getNote()));
        vh.setText(R.id.commit_comment_listitem_data, comment.getAuthor().getName());
    }
}
