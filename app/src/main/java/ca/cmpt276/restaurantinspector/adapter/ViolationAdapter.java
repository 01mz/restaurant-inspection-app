package ca.cmpt276.restaurantinspector.adapter;

import android.content.Context;
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
import ca.cmpt276.restaurantinspector.model.Restaurant;
import ca.cmpt276.restaurantinspector.model.Violation;


public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.ViewHolder>{
    Context context;
    List<Restaurant> restaurantList;
    List<Violation> violationList;


    public ViolationAdapter(List<Violation> violationList, Context context) {
        this.violationList = violationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.violation_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        Restaurant restaurant = restaurantList.get(position);
//        restaurant.getInspectionList();

        Violation violation = violationList.get(position);



        holder.textViewDescription.setText(violation.getDESCRIPTION());


        switch(violation.getTYPE().toUpperCase()){
            case "FOOD":
                holder.imageViewViolationTypeIcon.setImageResource(R.drawable.violation_food);
                break;
            case "PEST":
                holder.imageViewViolationTypeIcon.setImageResource(R.drawable.violation_pest);
                break;
            case "EQUIPMENT":
                holder.imageViewViolationTypeIcon.setImageResource(R.drawable.violation_equipment);
                break;
            default:
                holder.imageViewViolationTypeIcon.setImageResource(R.drawable.violation_other);
        }

        if(violation.isCRITICAL()){
            holder.imageViewViolationSeverityIcon.setImageResource(R.drawable.is_critical_icon);
        } else{
            holder.imageViewViolationSeverityIcon.setImageResource(R.drawable.is_not_critical_icon);
        }




        holder.itemView.setOnClickListener(v -> Toast.makeText(context, violation.getDESCRIPTION(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return violationList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewDescription;
        ImageView imageViewViolationTypeIcon;
        ImageView imageViewViolationSeverityIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDescription = itemView.findViewById(R.id.textViewViolationDescription);
            imageViewViolationTypeIcon = itemView.findViewById(R.id.imageViewViolationIcon);
            imageViewViolationSeverityIcon = itemView.findViewById(R.id.imageViewViolationSeverity);

        }
    }

}




