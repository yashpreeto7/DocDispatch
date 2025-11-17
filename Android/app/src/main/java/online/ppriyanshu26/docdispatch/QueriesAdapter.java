package online.ppriyanshu26.docdispatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QueriesAdapter extends RecyclerView.Adapter<QueriesAdapter.ViewHolder> {

    ArrayList<QueryModel> list;

    public QueriesAdapter(ArrayList<QueryModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_query, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QueryModel q = list.get(position);

        holder.txtName.setText("Name: " + q.name);
        holder.txtQid.setText("Query ID: " + q.qid);
        holder.txtAttended.setText("Attended: " + (q.attended ? "Yes" : "No"));

        if (!q.attended) {
            holder.btnDetails.setEnabled(false);
            holder.btnDetails.setAlpha(0.5f);
        } else {
            holder.btnDetails.setEnabled(true);
            holder.btnDetails.setAlpha(1f);
        }

        holder.btnDetails.setOnClickListener(v -> {
            if (!q.attended) {
                Toast.makeText(v.getContext(), "Not attended yet", Toast.LENGTH_SHORT).show();
                return;
            }

            // Inflate dialog layout
            View dialogView = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.dialog_details, null);

            ((TextView) dialogView.findViewById(R.id.txtDoctor))
                    .setText("Doctor: " + q.doctor);

            ((TextView) dialogView.findViewById(R.id.txtTreatment))
                    .setText("Treatment: " + q.treatment);

            ((TextView) dialogView.findViewById(R.id.txtRemarks))
                    .setText("Remarks: " + q.remarks);

            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Query Details")
                    .setView(dialogView)
                    .setPositiveButton("Close", null)
                    .show();
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtQid, txtAttended;
        Button btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtQid = itemView.findViewById(R.id.txtQid);
            txtAttended = itemView.findViewById(R.id.txtAttended);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }

    }
}


