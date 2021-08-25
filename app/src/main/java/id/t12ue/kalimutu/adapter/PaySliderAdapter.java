package id.t12ue.kalimutu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.model.BankEntity;

public class PaySliderAdapter extends RecyclerView.Adapter<PaySliderAdapter.ViewHolder> {
    List<BankEntity>list;
    Context context;
    private AdapterListener listener;

    public PaySliderAdapter(Context context, List<BankEntity> list, AdapterListener listener) {
        this.list = list;
        this.context=context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pay,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BankEntity entity= list.get(position);
        holder.tvaccount.setText(entity.getBankName());
        holder.tvname.setText(entity.getRekeningName());
        holder.tvnumber.setText(entity.getRekeningNumber());

        holder.act_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onActionCopy(entity);
            }
        });
//
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface AdapterListener {
        void onActionCopy(BankEntity entity);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvnumber, tvname, tvaccount;
        ImageView act_copy;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvnumber = itemView.findViewById(R.id.nomor);
            tvname = itemView.findViewById(R.id.nama);
            tvaccount = itemView.findViewById(R.id.bank);
            act_copy = itemView.findViewById(R.id.act_copy);

        }
    }
}
