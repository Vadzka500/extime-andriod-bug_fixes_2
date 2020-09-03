package ai.extime.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.widget.ImageView;


import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.snatik.storage.Storage;


public class SvgSoftwareLayerSetter<T> implements RequestListener<T, PictureDrawable>  {


    @Override
    public boolean onException(Exception e, T model, Target<PictureDrawable> target, boolean isFirstResource) {
        ImageView view = ((ImageViewTarget<?>) target).getView();
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            view.setLayerType(ImageView.LAYER_TYPE_NONE, null);
        }
        return false;
    }

    @Override
    public boolean onResourceReady(PictureDrawable resource, T model, Target<PictureDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        ImageView view = ((ImageViewTarget<?>) target).getView();
        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
            view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
        }

        Bitmap bm = Bitmap.createBitmap(resource.getIntrinsicWidth(), resource.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawPicture(resource.getPicture());



        return false;
    }
}
