package id.t12ue.kalimutu.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import id.t12ue.kalimutu.model.Cart;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Cart> list;
    private AdapterListener listener;


    public CartAdapter(Context context, AdapterListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cart, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Cart product = list.get(position);
        itemViewHolder.textName.setText(product.getPackageName());
        itemViewHolder.textStock.setText("Tersedia: "+product.getStock());
        itemViewHolder.textPrice.setText(Cons.currencyId(product.getPackagePrice()));
        itemViewHolder.textViewQty.setText(String.valueOf(product.getQuantity())+"");
        itemViewHolder.textTotal.setText(Cons.currencyId(product.getPackagePrice()*product.getQuantity()));

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

        itemViewHolder.bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.plusOnClick(product, itemViewHolder.textViewQty);
            }
        });


        itemViewHolder.bMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.minOnClick(product, itemViewHolder.textViewQty);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setDataList(List<Cart> dataList) {
        this.list = dataList;
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void plusOnClick(Cart entity, TextView textView);
        void minOnClick(Cart entity , TextView textView);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, act_more;
        TextView textPrice, textName, textStock, textViewQty, textTotal;
        ProgressBar progressBar;
        ImageButton bMin, bPlus;
        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            act_more = itemView.findViewById(R.id.act_more);
            textPrice = itemView.findViewById(R.id.textPrice);
            textName = itemView.findViewById(R.id.textName);
            textStock = itemView.findViewById(R.id.textStock);
            textViewQty = itemView.findViewById(R.id.textViewQty);
            textTotal = itemView.findViewById(R.id.textTotal);
            progressBar = itemView.findViewById(R.id.progressBar);
            bMin = itemView.findViewById(R.id.bMin);
            bPlus = itemView.findViewById(R.id.bPlus);
        }
    }
}
