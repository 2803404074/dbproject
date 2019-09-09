package com.dabangvr.common.weight;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apache.commons.lang.StringUtils;

public class HeadSimple extends SimpleDraweeView {
    public HeadSimple(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
    }

    @Override
    public void setImageURI(String uriString) {
        if (!StringUtils.isEmpty(uriString)){
            super.setImageURI(uriString);
        }
    }
}
