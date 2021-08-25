package id.t12ue.kalimutu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.model.ImageEntity;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<ImageEntity> imageList;
    private final static int IMAGE_LIST = 0;
    private final static int IMAGE_PICKER = 1;
    private final AdapterListener listener;

    public ImageAdapter(Context context, AdapterListener adapterListener) {
        this.context = context;
        this.listener = adapterListener;
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_image, parent, false);
        return new ImageListViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position < 2 ? IMAGE_PICKER : IMAGE_LIST;
    }

    @Override
    public  void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ImageListViewHolder viewHolder = (ImageListViewHolder) holder;
        Glide.with(context)
                .load(imageList.get(position).getImageUrl())
                .error(R.drawable.picture)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(viewHolder.image);
        //viewHolder.textName.setText(imageList.get(position).getImageUrl());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(imageList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    public class ImageListViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView textName;

        public ImageListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            textName = itemView.findViewById(R.id.textName);
        }
    }


    public void setDataList(List<ImageEntity> dataList) {
        this.imageList = dataList;
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onClick(ImageEntity enty);
    }

}