package letshangllc.uncrecattendence.adapter;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import letshangllc.uncrecattendence.R;
import letshangllc.uncrecattendence.objects.FacilityTime;

/**
 * Created by cvburnha on 11/3/2015.
 */
public class FacilityTimesAdapter extends RecyclerView.Adapter<FacilityTimesAdapter.ViewHolder> {
    public ArrayList<FacilityTime> items;

    private Context context;

    /* TODO: Change color based on if its currently open */

    // Provide a suitable constructor (depends on the kind of dataset)
    public FacilityTimesAdapter(ArrayList<FacilityTime> items, Context context) {
        this.items = items;
        this.context = context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public FacilityTimesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_time_item,  null);

            // create ViewHolder

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
            }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        final FacilityTime item = items.get(position);


        viewHolder.tvName.setText(item.getName());
        viewHolder.tvOpenTime.setText(context.getString(R.string.open) +" "+ item.getOpenTime());
        viewHolder.tvCloseTime.setText(context.getString(R.string.close) +" "+ item.getCloseTime());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return  items.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvOpenTime;
        public TextView tvCloseTime;

        public ViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvFacilityName);
            tvOpenTime = (TextView) view.findViewById(R.id.tvOpenTime);
            tvCloseTime = (TextView) view.findViewById(R.id.tvCloseTime);
        }
    }

}
