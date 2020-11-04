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
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Inspection;
import ca.cmpt276.restaurantinspector.model.InspectionDate;
import ca.cmpt276.restaurantinspector.model.Restaurant;
import ca.cmpt276.restaurantinspector.ui.MainActivity;
import ca.cmpt276.restaurantinspector.ui.RestaurantInfo;

import static androidx.core.content.ContextCompat.startActivity;
import static java.util.Date.parse;

import java.util.Calendar;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    Data RestaurantData=Data.getInstance();
    List<Restaurant> restaurants;
    Context context;






    public RestaurantAdapter(List<Restaurant> restaurants,MainActivity activity) {
        this.restaurants = restaurants;
        this.context = activity;
    }
    private InspectionDate LatestInspection(int position) {
        Inspection inspection = restaurants.get(position).getMostRecentInspection();
        InspectionDate id = null;
        if (restaurants.get(position).hasInspection() == true) {
            id = inspection.getINSPECTION_DATE();
        }
        return id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.restaurant_item_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Restaurant RestaurantList = restaurants.get(position);
        holder.textViewName.setText(RestaurantList.getNAME());

        holder.trackingNumber.setText("# of Issues: 0");
        holder.textViewDate.setText("No Inspection Performed Yet");


        if(RestaurantList.hasInspection()) {
            LocalDate localDate = LocalDate.now();
            int year = localDate.getYear();

            if (RestaurantList.getMostRecentInspection().getINSPECTION_DATE().isWithinThirtyDays()) { // within 30 days textview setter

                int temp = RestaurantList.getMostRecentInspection().getINSPECTION_DATE().getDaysAgo();
                String count = Integer.toString(temp);

                holder.textViewDate.setText("Inspected On: " + LatestInspection(position).toString() + " " + count + " days ago");
            }
            if (year == RestaurantList.getMostRecentInspection().getINSPECTION_DATE().getYear()) {
                String str=RestaurantList.getMostRecentInspection().getINSPECTION_DATE().getMonth()+" "+RestaurantList.getMostRecentInspection().getINSPECTION_DATE().getDay();
                holder.textViewDate.setText("Inspected On: " +str);
            }
            else{
                holder.textViewDate.setText("Inspected On: "+RestaurantList.getMostRecentInspection().getINSPECTION_DATE().getMonth()+" "+RestaurantList.getMostRecentInspection().getINSPECTION_DATE().getYear());
            }
        }
        holder.rating.setImageResource(R.drawable.no_inspection);
        if(restaurants.get(position).hasInspection()==true) {
            if (restaurants.get(position).getMostRecentInspection().getHAZARD_RATING().equals("Low")) {
                holder.rating.setImageResource(R.drawable.hazardlow);
            }
            if (restaurants.get(position).getMostRecentInspection().getHAZARD_RATING().equals("Moderate")) {
                holder.rating.setImageResource(R.drawable.hazardmoderate);
            }
            if(restaurants.get(position).getMostRecentInspection().getHAZARD_RATING().equals("High")) {
                holder.rating.setImageResource(R.drawable.hazardhigh);
            }
            int total=restaurants.get(position).getMostRecentInspection().getTotalIssues();
            String issues= Integer.toString(total);
            holder.trackingNumber.setText("# of Issues: "+issues);

        }
        holder.RestaurantImage.setImageResource(R.drawable.generic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= RestaurantInfo.makeLaunch(context); /// Add the restaurants description Intent here.....

                Toast.makeText(context, RestaurantList.getADDRESS(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return restaurants.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView RestaurantImage;
        TextView textViewName;
        TextView textViewDate;
        TextView trackingNumber;
        ImageView rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            RestaurantImage = itemView.findViewById(R.id.imageview);
            textViewName = itemView.findViewById(R.id.RestaurantName);
            textViewDate = itemView.findViewById(R.id.InspectionDate);
            trackingNumber=itemView.findViewById(R.id.TrackingNumber);
            rating= itemView.findViewById(R.id.rating);

        }
    }

}
