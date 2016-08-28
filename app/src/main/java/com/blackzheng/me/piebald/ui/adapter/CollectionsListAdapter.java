package com.blackzheng.me.piebald.ui.adapter;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.blackzheng.me.piebald.R;
import com.blackzheng.me.piebald.data.ImageCacheManager;
import com.blackzheng.me.piebald.model.Collection;
import com.blackzheng.me.piebald.util.Decoder;
import com.blackzheng.me.piebald.util.DrawableUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BlackZheng on 2016/8/27.
 */
public class CollectionsListAdapter extends BaseAbstractRecycleCursorAdapter<CollectionsListAdapter.ViewHolder> {

    private static final int[] COLORS = {R.color.holo_blue_light, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_purple_light, R.color.holo_red_light};
    private Resources mResource;
    private Drawable mDefaultImageDrawable;
    private OnItemClickLitener mOnItemClickLitener;
    private int mWidth;

    public CollectionsListAdapter(Context context, Cursor c) {
        super(context, c);
        mWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth() / 2;
        mResource = context.getResources();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collections_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        if (holder.coverPhotoRequest != null) {
            holder.coverPhotoRequest.cancelRequest();
        }
        if (holder.profileRequest != null) {
            holder.profileRequest.cancelRequest();
        }
        final Collection collection = Collection.fromCursor(cursor);
        holder.title.setText(collection.title);
        holder.curator.setText(Decoder.decodeStr(collection.user.name));

        if(collection.cover_photo.color != null){
            mDefaultImageDrawable = new ColorDrawable(Color.parseColor(collection.cover_photo.color));
        }else{
            mDefaultImageDrawable = new ColorDrawable(mResource.getColor(COLORS[cursor.getPosition() % COLORS.length]));
        }

        holder.coverPhotoRequest = ImageCacheManager.loadImage(
                Decoder.decodeURL(collection.cover_photo.urls.thumb),
                ImageCacheManager.getProfileListener(holder.cover_photo, DrawableUtil.toSuitableDrawable(mDefaultImageDrawable, mWidth, mWidth*collection.cover_photo.height/collection.cover_photo.width),
                mDefaultImageDrawable), 0, 0);
        holder.profileRequest = ImageCacheManager.loadImage(
                Decoder.decodeURL(collection.user.profile_image.medium),
                ImageCacheManager.getProfileListener(holder.profile,
                mDefaultImageDrawable, mDefaultImageDrawable), 0, 0);

        holder.cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickLitener != null){
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(v, collection, pos);
                }
            }
        });
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView cover_photo, profile;
        public TextView title;
        public TextView curator;
        public ImageLoader.ImageContainer profileRequest;
        public ImageLoader.ImageContainer coverPhotoRequest;

        public ViewHolder(View itemView) {
            super(itemView);
            cover_photo = (ImageView) itemView.findViewById(R.id.cover_photo);
            profile = (ImageView) itemView.findViewById(R.id.profile);
            title = (TextView) itemView.findViewById(R.id.title);
            curator = (TextView) itemView.findViewById(R.id.curator);
        }
    }

    public interface OnItemClickLitener
    {
        void onItemClick(View view, Collection collection, int position);
        void onItemLongClick(View view , Collection collection, int position);
    }
}