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

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Packages> list;
    private AdapterListener listener;
    private int ty;


    public ProductAdapter(Context context, AdapterListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Packages product = list.get(position);
        itemViewHolder.textName.setText(product.getPackageName());
        itemViewHolder.textStock.setText("Stok : "+product.getStock());
        itemViewHolder.textPrice.setText(Cons.currencyId(product.getPackagePrice()));
        itemViewHolder.textCat.setVisibility(View.GONE);
        Glide.with(mContext)
                .load(product.getImageUrl())
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
            itemViewHolder.act_more.setVisibility(View.GONE);
        } else {
            itemViewHolder.act_more.setVisibility(View.VISIBLE);
        }

        if (product.isArzip()){
            itemViewHolder.arsip.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.arsip.setVisibility(View.GONE);
        }



        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(product);
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
        void onClick(Packages entity);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, act_more;
        TextView textPrice, textName, textStock, arsip, textCat;
        ProgressBar progressBar;
        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            act_more = itemView.findViewById(R.id.act_more);
            textPrice = itemView.findViewById(R.id.textPrice);
            textName = itemView.findViewById(R.id.textName);
            textStock = itemView.findViewById(R.id.textStock);
            progressBar = itemView.findViewById(R.id.progressBar);
            arsip = itemView.findViewById(R.id.arsip);
            textCat = itemView.findViewById(R.id.textCat);
        }
    }
}
