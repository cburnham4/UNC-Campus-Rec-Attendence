package letshangllc.uncrecattendence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import letshangllc.uncrecattendence.R;
import letshangllc.uncrecattendence.objects.FacilitySelectItem;

/**
 * Created by cvburnha on 11/3/2015.
 */
public class FacilitySelectorAdapter extends RecyclerView.Adapter<FacilitySelectorAdapter.ViewHolder> {
    public ArrayList<FacilitySelectItem> items;
    private ArrayList<FacilitySelectItem> checkedItems;
    private Context context;

    public FacilitySelectorAdapter(ArrayList<FacilitySelectItem> items, Context context,
                                   ArrayList<FacilitySelectItem> checkedItems) {
        this.items = items;
        this.context = context;
        this.checkedItems = checkedItems;
    }

    @NonNull
    @Override
    public FacilitySelectorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.facility_name_item, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final FacilitySelectItem item = items.get(position);

        viewHolder.bxSelectFacility.setOnClickListener(v -> {
            CheckBox cb = (CheckBox) v;
            FacilitySelectItem selected = (FacilitySelectItem) cb.getTag(R.string.tag_facility_name);
            selected.setShowResult(cb.isChecked());
            if (selected.isShowResult()) {
                checkedItems.add(selected);
                selected.setShowResult(true);
            } else {
                checkedItems.remove(selected);
                selected.setShowResult(false);
            }
        });

        viewHolder.tvFacilityName.setText(item.getName());
        viewHolder.bxSelectFacility.setChecked(item.isShowResult());
        viewHolder.bxSelectFacility.setTag(R.string.tag_facility_name, item);
        viewHolder.bxSelectFacility.setTag(R.string.tag_tv_name, viewHolder.tvFacilityName);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFacilityName;
        public CheckBox bxSelectFacility;

        public ViewHolder(View view) {
            super(view);
            tvFacilityName = view.findViewById(R.id.tvFacilityName);
            bxSelectFacility = view.findViewById(R.id.bxSelectFacility);
        }
    }
}
