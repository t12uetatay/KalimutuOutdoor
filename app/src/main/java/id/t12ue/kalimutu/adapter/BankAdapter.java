package id.t12ue.kalimutu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.model.BankEntity;

public class BankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<BankEntity> list;
    private AdapterListener listener;


    public BankAdapter(Context context, AdapterListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_bank, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        BankEntity bankEntity = list.get(position);
        itemViewHolder.bank.setText(bankEntity.getBankName());
        itemViewHolder.nama.setText(bankEntity.getRekeningName());
        itemViewHolder.nomor.setText(bankEntity.getRekeningNumber());
        if (bankEntity.isArsip()){
            itemViewHolder.arsip.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.arsip.setVisibility(View.GONE);
        }
        itemViewHolder.act_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onActionMoreClick(bankEntity, itemViewHolder.act_more);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setDataList(List<BankEntity> dataList) {
        this.list = dataList;
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onActionMoreClick(BankEntity entity, ImageView imageView);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView act_more;
        TextView bank, nama, nomor, arsip;
        public ItemViewHolder(View itemView) {
            super(itemView);
            act_more = itemView.findViewById(R.id.act_menu);
            bank = itemView.findViewById(R.id.bank);
            nama = itemView.findViewById(R.id.nama);
            nomor = itemView.findViewById(R.id.nomor);
            arsip = itemView.findViewById(R.id.arsip);
        }
    }
}
