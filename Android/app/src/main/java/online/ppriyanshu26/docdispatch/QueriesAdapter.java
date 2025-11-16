package online.ppriyanshu26.docdispatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtQid, txtAttended;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtQid = itemView.findViewById(R.id.txtQid);
            txtAttended = itemView.findViewById(R.id.txtAttended);
        }
    }
}


