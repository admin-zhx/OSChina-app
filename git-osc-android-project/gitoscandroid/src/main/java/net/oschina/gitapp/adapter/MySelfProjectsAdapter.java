package net.oschina.gitapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.util.TypefaceUtils;

import java.util.Date;

/**
 * 个人项目列表适配器
 * @created 2014-05-12
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class MySelfProjectsAdapter extends CommonAdapter<Project> {
	
	public MySelfProjectsAdapter(Context context, int resource) {
		super(context, resource);
	}

    @Override
    public void convert(ViewHolder vh, final Project project) {
        // 1.加载头像
        String portraitURL = project.getOwner().getNew_portrait();
        if (portraitURL.endsWith("portrait.gif")) {
            vh.setImageResource(R.id.iv_portrait, R.drawable.mini_avatar);
        } else {
            vh.setImageForUrl(R.id.iv_portrait, portraitURL);
        }

        vh.getView(R.id.iv_portrait).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = project.getOwner();
                if (user == null) {
                    return;
                }
                UIHelper.showUserInfoDetail(mContext, user, null);
            }
        });

        vh.setText(R.id.tv_title, project.getOwner().getName() + " / " + project.getName());

        Date last_push_at = project.getLast_push_at() != null ? project.getLast_push_at() : project.getCreatedAt();
        vh.setText(R.id.tv_date, "更新于: " + StringUtils.friendly_time(last_push_at));

        // 判断是否有项目的介绍
        vh.setText(R.id.tv_description, project.getDescription(), R.string.msg_project_empty_description);
        // 显示项目的star、fork、language信息
        vh.setTextWithSemantic(R.id.tv_watch, project.getWatches_count().toString(), R.string.sem_watch);
        vh.setTextWithSemantic(R.id.tv_star, project.getStars_count().toString(), R.string.sem_star);
        vh.setTextWithSemantic(R.id.tv_fork, project.getForks_count().toString(), R.string.sem_fork);

        TextView flag = vh.getView(R.id.tv_flag);
        int flagRes = R.string.oct_lock;
        if (project.getParent_id() != null) {
            flagRes = R.string.oct_fork;
        } else if (project.isPublic()) {
            flagRes = R.string.oct_repo;
        } else {
            flagRes = R.string.oct_lock;
        }
        flag.setText(flagRes);
        TypefaceUtils.setOcticons(flag);
    }
}
