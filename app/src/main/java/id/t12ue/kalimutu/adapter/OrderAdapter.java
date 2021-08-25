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
import id.t12ue.kalimutu.helpers.DateConverter;
import id.t12ue.kalimutu.model.Orders;
import id.t12ue.kalimutu.model.Packages;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Orders> list;
    private AdapterListener listener;
    private int ty;


    public OrderAdapter(Context context, AdapterListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_orders, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        TypedArray typedArray  = mContext.getResources().obtainTypedArray(R.array.pay_methode);
        Orders orders = list.get(position);
        itemViewHolder.textName.setText(orders.getUserName());
        itemViewHolder.textId.setText("KLM"+orders.getTransactionId());
        itemViewHolder.tanggal.setText(DateConverter.getTanggal(orders.getTransactionTime()));
        itemViewHolder.textStatus.setText(Cons.status(orders.getStatus()));
        itemViewHolder.textPay.setText(typedArray.getString(orders.getPayMethode()));

        if (ty==0){
            itemViewHolder.textName.setVisibility(View.GONE);
        } else {
            itemViewHolder.textName.setVisibility(View.VISIBLE);
        }


        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(orders);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setDataList(List<Orders> dataList, int ty) {
        this.list = dataList;
        this.ty=ty;
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onClick(Orders orders);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textStatus, textId, tanggal, textPay;
        public ItemViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textStatus = itemView.findViewById(R.id.textStatus);
            textId = itemView.findViewById(R.id.textId);
            tanggal = itemView.findViewById(R.id.tanggal);
            textPay = itemView.findViewById(R.id.textPay);
        }
    }
}
