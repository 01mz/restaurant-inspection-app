package ca.cmpt276.restaurantinspector.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Inspection;
import ca.cmpt276.restaurantinspector.model.InspectionDate;
import ca.cmpt276.restaurantinspector.model.Restaurant;

/**
 * Adapter class for FavoritesUpdatedActivity RecyclerView. Populates the RecyclerView with restaurant_list_items.
 */
public class FavoritesUpdatedAdapter extends RecyclerView.Adapter<FavoritesUpdatedAdapter.ViewHolder>{


    List<Restaurant> restaurantList;
    Context context;
    private final Data data = Data.getInstance();

    public FavoritesUpdatedAdapter(List<Restaurant> restaurantList, Context context) {
        this.restaurantList = new ArrayList<>(restaurantList);
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.restaurant_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        if(data.isFavorite(restaurant)) {
            holder.itemView.setBackgroundColor(Color.argb(70, 255, 255, 0)); // yellow
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        if(restaurant.getNAME().contains("A & W")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.a_and_w);
        }else if (restaurant.getNAME().contains("A&W")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.a_and_w);
        } else if (restaurant.getNAME().contains("Blenz")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.blenz);
        } else if (restaurant.getNAME().contains("Boston")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.boston);
        } else if (restaurant.getNAME().contains("Eleven")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.seven_eleven);
        } else if (restaurant.getNAME().contains("Panago")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.panago);
        } else if (restaurant.getNAME().contains("McDonald")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.mcdonalds);
        } else if (restaurant.getNAME().contains("Lee Yuen")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.seafood);
        } else if (restaurant.getNAME().contains("Subway")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.subway);
        } else if (restaurant.getNAME().contains("Tim Hortons")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.timhortons);
        }else if (restaurant.getNAME().contains("Starbucks")){
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.starbucks);
        }else{
            // set restaurant ImageView and name TextView
            holder.imageViewRestaurantLogo.setImageResource(R.drawable.generic);

        }


        // Trim long names to max length of 30
        holder.textViewName.setText(StringUtils.abbreviate(restaurant.getNAME(), 30));

        if(restaurant.hasInspection()) {
            Inspection recentInspection = restaurant.getMostRecentInspection();

            // set TextView # of issues
            int numTotalIssues = recentInspection.getTotalIssues();
            holder.textViewNumTotalIssues.setText(context.getString(R.string.num_issues, numTotalIssues));

            // set TextView intelligent inspection date
            String inspectionDateString = getIntelligentInspectionDate(recentInspection.getINSPECTION_DATE());
            holder.textViewDate.setText(inspectionDateString);

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


    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewRestaurantLogo;
        TextView textViewName;
        TextView textViewDate;
        TextView textViewNumTotalIssues;
        ImageView rating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRestaurantLogo = itemView.findViewById(R.id.restaurant_logo);
            textViewName = itemView.findViewById(R.id.RestaurantName);
            textViewDate = itemView.findViewById(R.id.InspectionDate);
            textViewNumTotalIssues =itemView.findViewById(R.id.sumNumIssues);
            rating= itemView.findViewById(R.id.hazard_level);
        }

    }

}
