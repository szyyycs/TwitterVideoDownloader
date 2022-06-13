package com.ycs.servicetest.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

/**
 * 剪切板读写工具
 */
public class ClipBoardUtil {
    Context mcontext;
    ClipboardManager manager;
    public ClipBoardUtil(Context context) {
        this.mcontext = context;
        manager = (ClipboardManager) mcontext.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * 获取剪切板内容
     * @return
     */
    public String paste(){
        if (manager != null) {
            if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();
                String addedTextString = String.valueOf(addedText);
                if (!TextUtils.isEmpty(addedTextString)) {
                    return addedTextString;
                }
            }
        }
        return "";
    }

    /**
     * 清空剪切板
     */
    public void clear(){
        if (manager != null) {
            try {
                manager.setPrimaryClip(manager.getPrimaryClip());
                manager.setPrimaryClip(ClipData.newPlainText("",""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

