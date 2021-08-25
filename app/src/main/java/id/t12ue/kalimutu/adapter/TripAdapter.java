package id.t12ue.kalimutu.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.helpers.Cons;
import id.t12ue.kalimutu.model.Packages;

public class TripAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Packages> list;
    private AdapterListener listener;
    private int ty;


    public TripAdapter(Context context, AdapterListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_trip, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Packages trip = list.get(position);
        itemViewHolder.textName.setText(trip.getPackageName());
        itemViewHolder.textStock.setText("Kuota : "+trip.getStock());
        itemViewHolder.textPrice.setText("IDR : "+Cons.currencyId(trip.getPackagePrice()));

        Glide.with(mContext)
                .load(trip.getImageUrl())
                .centerCrop()
                .error(R.drawable.picture)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        itemViewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        itemViewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(itemViewHolder.imageView);

        if (ty==0){
            itemViewHolder.act_next.setImageResource(R.drawable.ic_baseline_arrow_forward_24);
        } else {
            itemViewHolder.act_next.setImageResource(R.drawable.ic_baseline_more_horiz_24);
        }

        if (trip.isArzip()){
            itemViewHolder.arsip.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.arsip.setVisibility(View.GONE);
        }


        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onActionMoreClick(trip, itemViewHolder.act_next);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setDataList(List<Packages> dataList, int ty) {
        this.list = dataList;
        this.ty=ty;
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onActionMoreClick(Packages entity, ImageView imageView);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, act_next;
        TextView textPrice, textName, textDesc, textStock, arsip;
        ProgressBar progressBar;
        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            act_next = itemView.findViewById(R.id.act_next);
            textPrice = itemView.findViewById(R.id.textPrice);
            textName = itemView.findViewById(R.id.textName);
            textDesc = itemView.findViewById(R.id.textDesc);
            textStock = itemView.findViewById(R.id.textStock);
            progressBar = itemView.findViewById(R.id.progressBar);
            arsip = itemView.findViewById(R.id.arsip);
        }
    }
}
