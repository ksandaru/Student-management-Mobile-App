package com.vta.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import com.vta.app.fragment.StudentsFragment;
import com.vta.app.model.Student;
import com.vta.app.R;
import com.vta.app.utils.ListItemAnimation;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsRecyclerviewAdapter extends RecyclerView.Adapter<StudentsRecyclerviewAdapter.UsersRecyclerviewHolder> {

    Context context;
    List<Student> dataList;
    List<Student> filteredDataList;

    public StudentsRecyclerviewAdapter(Context context, List<Student> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.filteredDataList = dataList;
    }

    @NonNull
    @Override
    public UsersRecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.students_recyclerview_item, parent, false);
        return new UsersRecyclerviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersRecyclerviewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tv_name.setText(filteredDataList.get(position).getFullName());
        holder.tv_cource.setText(filteredDataList.get(position).getCource());
        holder.tv_nic_no.setText(filteredDataList.get(position).getNIC());
        Picasso.get().load(R.drawable.default_avatar).into(holder.userImage);

        ListItemAnimation.animateFadeIn(holder.itemView, position);

        // TODO: Item onClick
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Item onlick
            }
        });

        holder.btnActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentsFragment.studentsListItemOnClick(filteredDataList.get(position).getEmail());
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredDataList.size();
    }

    public static final class UsersRecyclerviewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView tv_name, tv_cource, tv_nic_no;
        MaterialButton btnActions;

        public UsersRecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_cource = itemView.findViewById(R.id.tv_cource);
            tv_nic_no = itemView.findViewById(R.id.tv_nic_no);
            btnActions = itemView.findViewById(R.id.btnActions);
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

                    List<Student> lstFiltered = new ArrayList<>();
                    for (Student row : dataList) {
                        if (row.getFullName().toLowerCase().contains(Key.toLowerCase()) || row.getNIC().toLowerCase().contains(Key.toLowerCase()) || row.getCource().toLowerCase().contains(Key.toLowerCase())) {
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
                filteredDataList = (List<Student>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

}
