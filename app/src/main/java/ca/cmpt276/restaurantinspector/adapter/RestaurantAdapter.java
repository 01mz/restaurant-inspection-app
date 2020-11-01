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

import java.util.ArrayList;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Restaurant;
import ca.cmpt276.restaurantinspector.ui.MainActivity;
import ca.cmpt276.restaurantinspector.ui.RestaurantInfo;

import static androidx.core.content.ContextCompat.startActivity;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>{

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
        holder.TrackingNumber.setText(myDataList.getADDRESS());
        holder.InspectionDate.setText(myDataList.getTRACKING_NUMBER());
        holder.RestaurantImage.setImageResource(R.drawable.ic_launcher_background);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,myDataList.getNAME(),Toast.LENGTH_LONG).show();
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


}
