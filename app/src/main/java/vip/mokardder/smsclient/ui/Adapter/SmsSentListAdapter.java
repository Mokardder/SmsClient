package vip.mokardder.smsclient.ui.Adapter;




import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import vip.mokardder.smsclient.R;
import vip.mokardder.smsclient.models.sms_list_payload;


public class SmsSentListAdapter extends RecyclerView.Adapter<SmsSentListAdapter.ViewHolder> {
    private List<sms_list_payload> updateItemList; // Change this to List<UpdateOfflineData>
    private Context context;
    sms_list_payload sms_list_payload;


//    xrManagerDBHelper db;


    public SmsSentListAdapter(Context context, List<sms_list_payload> updateItemList) {
        this.context = context;
        this.updateItemList = updateItemList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sms_sent_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SmsSentListAdapter.ViewHolder holder, int position) {
        sms_list_payload item = updateItemList.get(position); // Get the item at the current position
//        db = new xrManagerDBHelper(context);

        // Get the list of buttons for this specific item


        // Set the nested RecyclerView adapter






        //when you want horizontal





        holder.sent_to.setText(item.getSent_to());
//        holder.userName.setText(item.getUser_name());
        holder.sms_content.setText(item.getSms_content());
        holder.date_time.setText(item.getTime());




    }

    @Override
    public int getItemCount() {
        return updateItemList.size(); // Return the size of the list
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, sent_to, sms_content, date_time;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            sent_to = itemView.findViewById(R.id.sent_to);
            sms_content = itemView.findViewById(R.id.sms_content);
            date_time = itemView.findViewById(R.id.date_time);




        }
    }
}
