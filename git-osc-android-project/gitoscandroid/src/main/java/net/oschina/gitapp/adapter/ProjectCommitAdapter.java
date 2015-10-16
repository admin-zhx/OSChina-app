package net.oschina.gitapp.adapter;

import android.content.Context;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.common.StringUtils;

/**
 * 项目Commit列表适配器
 * @created 2014-05-26 下午14:43
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class ProjectCommitAdapter extends CommonAdapter<Commit> {
	
	public ProjectCommitAdapter(Context context, int resource) {
		super(context, resource);
	}

    @Override
    public void convert(ViewHolder vh, Commit commit) {
        // 1.加载头像

        String portraitURL = commit.getAuthor() == null ? "" : commit.getAuthor().getNew_portrait();
        if (portraitURL.endsWith("portrait.gif") || StringUtils.isEmpty(portraitURL)) {
            vh.setImageResource(R.id.iv_portrait, R.drawable.mini_avatar);
        } else {
            vh.setImageForUrl(R.id.iv_portrait, portraitURL);
        }

        // 2.显示相关信息
        String name = commit.getAuthor() == null ? commit.getAuthor_name() : commit.getAuthor().getName();
        vh.setText(R.id.tv_name, name);
        vh.setText(R.id.tv_content, commit.getTitle());
        vh.setTextWithSemantic(R.id.tv_date, StringUtils.friendly_time(commit.getCreatedAt()), R.string.sem_wait    );
    }
}
