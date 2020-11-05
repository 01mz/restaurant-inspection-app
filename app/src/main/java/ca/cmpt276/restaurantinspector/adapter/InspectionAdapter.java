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

import java.util.List;

import ca.cmpt276.restaurantinspector.R;
import ca.cmpt276.restaurantinspector.model.Inspection;
import ca.cmpt276.restaurantinspector.model.Restaurant;
import ca.cmpt276.restaurantinspector.ui.RestaurantInfo;


import static androidx.core.content.ContextCompat.startActivity;


public class InspectionAdapter extends RecyclerView.Adapter<InspectionAdapter.ViewHolder>{
    Context context;
    List<Restaurant> restaurantList;
    List<Inspection> inspectionList;


    public InspectionAdapter(List<Inspection> inspectionList, Context context) {
        this.inspectionList = inspectionList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.inspection_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        Restaurant restaurant = restaurantList.get(position);
//        restaurant.getInspectionList();

        Inspection inspection = inspectionList.get(position);




            //set TextView # of critical issues
            int numCritIssues = inspection.getNUM_CRITICAL();
            holder.textViewCritIssues.setText(Integer.toString(numCritIssues));

            //set TextView # of non-critical issues
            int numNonCritIssues = inspection.getNUM_NONCRITICAL();
            holder.textViewNonCritIssues.setText(context.getString(R.string.num_non_crit_issues, numNonCritIssues));

            // set hazard level icon
            switch (inspection.getHAZARD_RATING().toUpperCase()) {
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



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i= RestaurantInfo.makeLaunch(context);

               // startActivity(context,i,null);
                Toast.makeText(context, inspection.getNUM_CRITICAL(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return inspectionList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewDate;
        ImageView rating;
        TextView textViewCritIssues;
        TextView textViewNonCritIssues;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.InspectionDate);
            rating= itemView.findViewById(R.id.hazard_level);
            textViewCritIssues = itemView.findViewById(R.id.critIssues);
            textViewNonCritIssues = itemView.findViewById(R.id.nonCritIssues);

        }
    }

}




