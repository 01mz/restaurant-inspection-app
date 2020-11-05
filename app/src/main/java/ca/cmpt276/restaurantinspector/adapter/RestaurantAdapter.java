package ca.cmpt276.restaurantinspector.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Inspection;
import ca.cmpt276.restaurantinspector.model.InspectionDate;
import ca.cmpt276.restaurantinspector.model.Restaurant;
import ca.cmpt276.restaurantinspector.ui.RestaurantInfo;

import static androidx.core.content.ContextCompat.startActivity;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    List<Restaurant> restaurantList;
    Context context;

    public RestaurantAdapter(List<Restaurant> restaurantList, Context context) {
        this.restaurantList = restaurantList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.restaurant_item_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Restaurant restaurant = restaurantList.get(position);

        // set restaurant ImageView and name TextView
        holder.imageViewRestaurantLogo.setImageResource(R.drawable.generic);
        holder.textViewName.setText(restaurant.getNAME());

        if(restaurant.hasInspection()) {
            Inspection recentInspection = restaurant.getMostRecentInspection();

            // set TextView # of issues
            int numTotalIssues = recentInspection.getTotalIssues();
            holder.textViewNumTotalIssues.setText(context.getString(R.string.num_issues, numTotalIssues));

            // set TextView intelligent inspection date
            String inspectionDateString = getIntelligentInspectionDate(recentInspection.getINSPECTION_DATE());
            holder.textViewDate.setText(inspectionDateString);

            //set TextView # of critical issues
//            int numCritIssues = recentInspection.getNUM_CRITICAL();
//            if (this.context instanceof  RestaurantInfo){
//                holder.textViewCritIssues.setText(Integer.toString(numCritIssues));}

            //set TextView # of non-critical issues
//            int numNonCritIssues = recentInspection.getNUM_NONCRITICAL();
//            if (this.context instanceof RestaurantInfo){
//            holder.textViewNonCritIssues.setText(context.getString(R.string.num_non_crit_issues, numNonCritIssues));}

            // set hazard level icon
            switch (recentInspection.getHAZARD_RATING().toUpperCase()) {
                case "LOW":
                    holder.rating.setImageResource(R.drawable.hazard_low);
                    break;
                case "MODERATE":
                    holder.rating.setImageResource(R.drawable.hazard_moderate);
                    break;
                case "HIGH":
                    holder.rating.setImageResource(R.drawable.hazard_high);
                    break;
            }
        } else {
            // No recent inspections
            holder.textViewDate.setText(R.string.no_inspections);
            holder.rating.setImageResource(R.drawable.hazard_no_inspection);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= RestaurantInfo.makeLaunch(context); /// Add the restaurants description Intent here.....
                i.putExtra("position", position);
                i.putExtra("name", restaurant.getNAME());
                i.putExtra("address", restaurant.getADDRESS());
                i.putExtra("latitude", restaurant.getLATITUDE());
                i.putExtra("longitude", restaurant.getLONGITUDE());
                startActivity(context,i,null);

                Toast.makeText(context, restaurant.getADDRESS(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getIntelligentInspectionDate(InspectionDate inspectionDate) {
        String inspectionDateString;
        if (inspectionDate.isWithinThirtyDays()) { // within 30 days TextView setter
            inspectionDateString = context.getString(R.string.inspected_days_ago,
                    inspectionDate.getDaysAgo());
        }
        else if (inspectionDate.isWithinLastYear()) {
            inspectionDateString = context.getString(R.string.inspection_on_month_day,
                    inspectionDate.getMonth(), inspectionDate.getDay());
        }
        else {
            inspectionDateString = context.getString(R.string.inspection_on_month_year,
                    inspectionDate.getMonth(), inspectionDate.getYear());
        }
        return inspectionDateString;
    }


    @Override
    public int getItemCount() {
        return restaurantList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewRestaurantLogo;
        TextView textViewName;
        TextView textViewDate;
        TextView textViewNumTotalIssues;
        ImageView rating;
//        TextView textViewCritIssues;
//        TextView textViewNonCritIssues;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRestaurantLogo = itemView.findViewById(R.id.restaurant_logo);
            textViewName = itemView.findViewById(R.id.RestaurantName);
            textViewDate = itemView.findViewById(R.id.InspectionDate);
            textViewNumTotalIssues =itemView.findViewById(R.id.sumNumIssues);
            rating= itemView.findViewById(R.id.hazard_level);
//            textViewCritIssues = itemView.findViewById(R.id.critIssues);
//            textViewNonCritIssues = itemView.findViewById(R.id.nonCritIssues);

        }
    }

}
