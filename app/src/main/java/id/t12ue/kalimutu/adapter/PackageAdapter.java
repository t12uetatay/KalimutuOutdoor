package id.t12ue.kalimutu.adapter;

import android.content.Context;
import android.content.res.TypedArray;
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

public class PackageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Packages> list;
    private AdapterListener listener;
    private int ty;
    private String ket="";


    public PackageAdapter(Context context, AdapterListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        TypedArray typedArray  = mContext.getResources().obtainTypedArray(R.array.category);
        Packages packages = list.get(position);
        if (packages.getCategory()==0){
            ket="Stok: ";
        } else {
            ket="Kouta: ";
        }
        itemViewHolder.textName.setText(packages.getPackageName());
        itemViewHolder.textStock.setText(ket+packages.getStock());
        itemViewHolder.textPrice.setText(Cons.currencyId(packages.getPackagePrice()));
        itemViewHolder.textCat.setText(typedArray.getString(packages.getCategory()));

        Glide.with(mContext)
                .load(packages.getImageUrl())
                //.fitXy()
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

        if (packages.isArzip()){
            itemViewHolder.arsip.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.arsip.setVisibility(View.GONE);
        }

        itemViewHolder.act_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onActionMoreClick(packages, itemViewHolder.act_more);
            }
        });


        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(packages);
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
