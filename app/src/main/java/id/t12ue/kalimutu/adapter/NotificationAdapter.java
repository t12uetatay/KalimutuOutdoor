package id.t12ue.kalimutu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.t12ue.kalimutu.R;
import id.t12ue.kalimutu.helpers.DateConverter;
import id.t12ue.kalimutu.model.NotificationEntity;
import id.t12ue.kalimutu.model.Orders;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<NotificationEntity> list;
    private AdapterListener listener;
    private int ty;


    public NotificationAdapter(Context context, AdapterListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        NotificationEntity entity = list.get(position);
        itemViewHolder.textMsg.setText(entity.getMessage());
        itemViewHolder.tanggal.setText(DateConverter.getTanggal(entity.getNotificationTime()));

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(entity);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setDataList(List<NotificationEntity> dataList, int ty) {
        this.list = dataList;
        this.ty=ty;
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onClick(NotificationEntity entity);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textMsg, tanggal;
        RelativeLayout rootLayout;
        public ItemViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            textMsg = itemView.findViewById(R.id.textMsg);
            tanggal = itemView.findViewById(R.id.tanggal);
        }
    }
}
