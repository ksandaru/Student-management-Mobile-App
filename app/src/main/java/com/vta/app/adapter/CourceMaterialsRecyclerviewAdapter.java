package com.vta.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import com.vta.app.R;
import com.vta.app.fragment.CourceMaterialsFragment;
import com.vta.app.fragment.InstructorsFragment;
import com.vta.app.model.CourceMaterial;
import com.vta.app.model.Instructor;
import com.vta.app.utils.ListItemAnimation;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourceMaterialsRecyclerviewAdapter extends RecyclerView.Adapter<CourceMaterialsRecyclerviewAdapter.UsersRecyclerviewHolder> {

    Context context;
    List<CourceMaterial> dataList;
    List<CourceMaterial> filteredDataList;

    public CourceMaterialsRecyclerviewAdapter(Context context, List<CourceMaterial> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.filteredDataList = dataList;
    }

    @NonNull
    @Override
    public CourceMaterialsRecyclerviewAdapter.UsersRecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.cource_material_recyclerview_item, parent, false);
        return new CourceMaterialsRecyclerviewAdapter.UsersRecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourceMaterialsRecyclerviewAdapter.UsersRecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tv_title.setText(filteredDataList.get(position).getTitle());
        holder.tv_description.setText(filteredDataList.get(position).getDescription());

        ListItemAnimation.animateFadeIn(holder.itemView, position);

        // TODO: Item onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Item onlick
            }
        });

        holder.btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourceMaterialsFragment.DownlaodBtnClick(filteredDataList.get(position).getDocumentUrl());
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public static final class UsersRecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_description;
        Button btn_download;

        public UsersRecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            btn_download = itemView.findViewById(R.id.btn_download);
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

                    List<CourceMaterial> lstFiltered = new ArrayList<>();
                    for (CourceMaterial row : dataList) {
                        if (row.getTitle().toLowerCase().contains(Key.toLowerCase()) || row.getDescription().toLowerCase().contains(Key.toLowerCase())) {
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
                filteredDataList = (List<CourceMaterial>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

}
