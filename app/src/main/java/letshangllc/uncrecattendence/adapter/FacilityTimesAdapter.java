package letshangllc.uncrecattendence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import letshangllc.uncrecattendence.R;
import letshangllc.uncrecattendence.objects.FacilityTime;

/**
 * Created by cvburnha on 11/3/2015.
 */
public class FacilityTimesAdapter extends RecyclerView.Adapter<FacilityTimesAdapter.ViewHolder> {
    public ArrayList<FacilityTime> items;
    private Context context;

    public FacilityTimesAdapter(ArrayList<FacilityTime> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public FacilityTimesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.facility_time_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final FacilityTime item = items.get(position);
        viewHolder.tvName.setText(item.getName());
        viewHolder.tvOpenTime.setText(context.getString(R.string.open) + " " + item.getOpenTime());
        viewHolder.tvCloseTime.setText(context.getString(R.string.close) + " " + item.getCloseTime());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvOpenTime;
        public TextView tvCloseTime;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvFacilityName);
            tvOpenTime = view.findViewById(R.id.tvOpenTime);
            tvCloseTime = view.findViewById(R.id.tvCloseTime);
        }
    }
}
