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
import ca.cmpt276.restaurantinspector.model.InspectionDate;
import ca.cmpt276.restaurantinspector.model.Restaurant;
import ca.cmpt276.restaurantinspector.ui.RestaurantInfo;
import ca.cmpt276.restaurantinspector.ui.SingleInspection;


import static androidx.core.content.ContextCompat.startActivity;


public class InspectionAdapter extends RecyclerView.Adapter<InspectionAdapter.ViewHolder>{
    private final int restaurantPosition;
    Context context;
    List<Restaurant> restaurantList;
    List<Inspection> inspectionList;


    public InspectionAdapter(List<Inspection> inspectionList, Context context, int restaurantPosition) {
        this.inspectionList = inspectionList;
        this.context = context;
        this.restaurantPosition = restaurantPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.inspection_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        Restaurant restaurant = restaurantList.get(position);
//        restaurant.getInspectionList();

        Inspection inspection = inspectionList.get(position);


            //set TextView Date of Inspection
            String date = getIntelligentInspectionDate(inspection.getINSPECTION_DATE());
            holder.textViewDate.setText(date);

            //set TextView # of critical issues
            int numCritIssues = inspection.getNUM_CRITICAL();
            holder.textViewCritIssues.setText(context.getString(R.string.num_crit_issues, numCritIssues));

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



        holder.itemView.setOnClickListener(v -> {
            Intent i= SingleInspection.makeLaunch(context);
            i.putExtra("criticalIssues",inspection.getNUM_CRITICAL());
            i.putExtra("nonCriticalIssues", inspection.getNUM_NONCRITICAL());
            String date1 = getIntelligentInspectionDate(inspection.getINSPECTION_DATE());
            i.putExtra("date", date1);
            i.putExtra("inspectionPosition", position);
            i.putExtra("restaurantPosition", restaurantPosition);
            startActivity(context,i,null);
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




