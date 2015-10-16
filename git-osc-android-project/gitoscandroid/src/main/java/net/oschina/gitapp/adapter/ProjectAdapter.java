package net.oschina.gitapp.adapter;

import android.content.Context;
import android.view.View;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.UIHelper;

/**
 *
 * Created by 火蚁 on 15/4/9.
 */
public class ProjectAdapter extends CommonAdapter<Project> {

    public ProjectAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public void convert(ViewHolder vh, final Project project) {
        // 2.显示相关信息
        vh.setText(R.id.tv_title, project.getOwner().getName() + " / " + project.getName());

        // 判断是否有项目的介绍
        vh.setText(R.id.tv_description, project.getDescription(), R.string.msg_project_empty_description);
        // 显示项目的star、fork、language信息
        vh.setTextWithSemantic(R.id.tv_watch, project.getWatches_count().toString(), R.string.sem_watch);
        vh.setTextWithSemantic(R.id.tv_star, project.getStars_count().toString(), R.string.sem_star);
        vh.setTextWithSemantic(R.id.tv_fork, project.getForks_count().toString(), R.string.sem_fork);

        String language = project.getLanguage() != null ? project.getLanguage() : "";
        if (project.getLanguage() != null) {
            vh.setTextWithSemantic(R.id.tv_lanuage, language, R.string.sem_tag);
        } else {
            vh.getView(R.id.tv_lanuage).setVisibility(View.GONE);
        }

        // 1.加载头像
        String portraitURL = project.getOwner().getNew_portrait();
        if (portraitURL.endsWith("portrait.gif")) {
            vh.setImageResource(R.id.iv_portrait, R.drawable.mini_avatar);
        } else {
            vh.setImageForUrl(R.id.iv_portrait, portraitURL);
        }
        vh.getView(R.id.iv_portrait).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = project.getOwner();
                if (user == null) {
                    return;
                }
                UIHelper.showUserInfoDetail(mContext, user, null);
            }
        });
    }
}
