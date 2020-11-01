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

import java.util.Date;
import java.util.List;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Data;
import ca.cmpt276.restaurantinspector.model.Inspection;
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
        if(RestaurantList==RestaurantData.getRestaurantByTrackingNumber(RestaurantList.getTRACKING_NUMBER())){

        }
        holder.trackingNumber.setText(RestaurantList.getTRACKING_NUMBER());
        holder.RestaurantImage.setImageResource(R.drawable.a_and_w);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            RestaurantImage = itemView.findViewById(R.id.imageview);
            textViewName = itemView.findViewById(R.id.RestaurantName);
            textViewDate = itemView.findViewById(R.id.InspectionDate);
            trackingNumber=itemView.findViewById(R.id.TrackingNumber);

        }
    }
    private void IntelligentInpectionDate(String InspectionDate){

        Date currentTime = Calendar.getInstance().getTime();
        Date.parse(InspectionDate);
    }

}
/*public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>{

    ArrayList<Restaurant> myData;
    Context context;
    public RestaurantAdapter(ArrayList<Restaurant> myData, MainActivity activity){
        this.myData=myData;
        this.context=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.restaurant_item_list,parent,false);
        ViewHolder viewHolder =new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Restaurant myDataList=myData.get(position);
        holder.RestaurantName.setText(myDataList.getNAME());
        holder.TrackingNumber.setText(myDataList.getTRACKING_NUMBER());

        holder.InspectionDate.setText(myDataList.getADDRESS());
        holder.RestaurantImage.setImageResource(R.drawable.zugba);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,myDataList.getTRACKING_NUMBER(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView RestaurantImage;
        TextView  InspectionDate;
        TextView  RestaurantName;
        TextView  TrackingNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            RestaurantImage=itemView.findViewById(R.id.imageview);
            RestaurantName=itemView.findViewById(R.id.RestaurantName);
            InspectionDate=itemView.findViewById(R.id.InspectionDate);
            TrackingNumber=itemView.findViewById(R.id.TrackingNumber);
        }
    }
}*/
