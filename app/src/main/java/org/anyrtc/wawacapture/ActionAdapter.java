package org.anyrtc.wawacapture;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by liuxiaozhong on 2017/11/29.
 */

public class ActionAdapter extends BaseQuickAdapter<ActionBean,BaseViewHolder> {
    public ActionAdapter() {
        super(R.layout.item_action);
    }


    @Override
    protected void convert(BaseViewHolder helper, ActionBean item) {
        helper.setText(R.id.tv_time,item.time);
        helper.setText(R.id.tv_action,item.action);
    }


}
