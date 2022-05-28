package com.vta.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vta.app.R;
import com.vta.app.fragment.CourceListingsFragment;
import com.vta.app.model.Cource;
import com.vta.app.model.CourceWithKey;
import com.vta.app.utils.ListItemAnimation;

import java.util.ArrayList;
import java.util.List;

public class CourcesRecyclerviewAdapter extends RecyclerView.Adapter<CourcesRecyclerviewAdapter.UsersRecyclerviewHolder> {

    Context context;
    List<CourceWithKey> dataList;
    List<CourceWithKey> filteredDataList;

    public CourcesRecyclerviewAdapter(Context context, List<CourceWithKey> userList) {
        this.context = context;
        this.dataList = userList;
        this.filteredDataList = userList;
    }

    @NonNull
    @Override
    public UsersRecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.cources_recyclerview_item, parent, false);
        return new UsersRecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersRecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.title.setText(filteredDataList.get(position).getName());
        holder.secondaryText.setText(filteredDataList.get(position).getNvq_level());
        holder.bottomLText.setText(filteredDataList.get(position).getLanguage());
        holder.bottomRText.setText("available");
        holder.txtLevel.setText("-");
        Picasso.get().load(filteredDataList.get(position).getImageUrl()).into(holder.cardimage);

        ListItemAnimation.animateFadeIn(holder.itemView, position);

        // TODO: Item onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 CourceListingsFragment.listItemOnClick(String.valueOf(filteredDataList.get(position).getKey()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public static final class UsersRecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView title,secondaryText,bottomLText,bottomRText,txtLevel;
        ImageView cardimage;

        //        CircleImageView imageDisaster;
        public UsersRecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cardTitle);
            secondaryText = itemView.findViewById(R.id.cardSecondText);
            bottomLText = itemView.findViewById(R.id.bottomLText);
            bottomRText = itemView.findViewById(R.id.bottomRText);
            txtLevel = itemView.findViewById(R.id.txtLevel);
            cardimage = itemView.findViewById(R.id.item_image);
        }
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String Key = charSequence.toString();
                if (Key.isEmpty()) {
                    filteredDataList = dataList;
                } else {

                    List<CourceWithKey> lstFiltered = new ArrayList<>();
                    for (CourceWithKey row : dataList) {
                        if (row.getName().toLowerCase().contains(Key.toLowerCase()) || row.getNvq_level().toLowerCase().contains(Key.toLowerCase()) ) {
                            lstFiltered.add(row);
                        }
                    }

                    filteredDataList = lstFiltered;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredDataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredDataList = (List<CourceWithKey>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

}
