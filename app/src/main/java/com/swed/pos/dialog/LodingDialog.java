package com.swed.pos.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.swed.pos.myapplication.R;
import com.swed.pos.util.TextUtil;

public class LodingDialog
        extends Dialog {
    private int colorResource = 17170445;
    private TextView msgTV;

    public LodingDialog(Context paramContext, String paramString, boolean paramBoolean) {
        super(paramContext);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(this.colorResource);
        setCanceledOnTouchOutside(false);
        setCancelable(paramBoolean);
        setContentView(R.layout.layout_loadingprogress);
        this.msgTV = ((TextView) findViewById(R.id.progress_msg));
        if (TextUtil.isEmpty(paramString)) {
            this.msgTV.setText(".....");
            return;
        }
        this.msgTV.setText(paramString);
    }

    public void setCancelable(boolean paramBoolean) {
        super.setCancelable(paramBoolean);
    }

    public void setMsg(String paramString) {
        if (TextUtil.isEmpty(paramString)) {
            this.msgTV.setText(".....");
            return;
        }
        this.msgTV.setText(paramString);
    }
}
