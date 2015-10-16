package net.oschina.gitapp.adapter;

import android.content.Context;
import android.widget.TextView;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.util.TypefaceUtils;

/**
 * 项目代码树列表适配器
 * @created 2014-05-26 下午17：25
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class ProjectCodeTreeAdapter extends CommonAdapter<CodeTree> {
	
	public ProjectCodeTreeAdapter(Context context, int resource) {
		super(context, resource);
	}

    @Override
    public void convert(ViewHolder vh, CodeTree code) {
        // 1.显示相关的信息
        String type = code.getType();
        int tagRes = R.string.sem_folder;
        TextView name = vh.getView(R.id.tv_name);
        TextView flag = vh.getView(R.id.tv_flag);
        flag.setTextSize(20);
        if (type.equalsIgnoreCase(CodeTree.TYPE_BLOB)) {
            tagRes = R.string.sem_file_text;
            flag.setTextSize(24);
        }
        vh.setText(R.id.tv_flag, tagRes);
        TypefaceUtils.setSemantic((TextView) vh.getView(R.id.tv_flag));
        name.setText(code.getName());
    }
}
