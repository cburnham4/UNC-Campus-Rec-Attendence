package letshangllc.uncrecattendence.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

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



    // Provide a suitable constructor (depends on the kind of dataset)
    public FacilitySelectorAdapter(ArrayList<FacilitySelectItem> items, Context context, ArrayList<FacilitySelectItem> checkedItems) {
        this.items = items;
        this.context = context;
        this.checkedItems = checkedItems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FacilitySelectorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_name_item, null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
            }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        final FacilitySelectItem item = items.get(position);



        viewHolder.bxSelectFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;

                FacilitySelectItem item = (FacilitySelectItem) cb.getTag(R.string.tag_facility_name);
                TextView tv = (TextView) cb.getTag(R.string.tag_tv_name);
                item.setShowResult(cb.isChecked());
                if (item.isShowResult()) {
                    checkedItems.add(item);
                    item.setShowResult(true);
                } else {
                    checkedItems.remove(item);
                    item.setShowResult(false);
                }
            }
        });

        viewHolder.tvFacilityName.setText(item.getName());

        viewHolder.bxSelectFacility.setChecked(item.isShowResult());
        viewHolder.bxSelectFacility.setTag(R.string.tag_facility_name, item);
        viewHolder.bxSelectFacility.setTag(R.string.tag_tv_name, viewHolder.tvFacilityName);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return  items.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFacilityName;
        public CheckBox bxSelectFacility;

        public ViewHolder(View view) {
            super(view);
            tvFacilityName = (TextView) view.findViewById(R.id.tvFacilityName);
            bxSelectFacility = (CheckBox) view.findViewById(R.id.bxSelectFacility);
        }
    }

}
