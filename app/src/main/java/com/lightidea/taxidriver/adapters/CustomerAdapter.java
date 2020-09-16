package com.lightidea.taxidriver.adapters;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lightidea.taxidriver.R;
import com.lightidea.taxidriver.models.Customer;
import com.lightidea.taxidriver.utils.GPSTracker;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> implements Filterable {
    GPSTracker gps;
    private Context context;
    private List<Customer> customerList;
    private List<Customer> customerListFiltered;
    private CustomerAdapterListener listener;
    private LocationManager locationManager;

    public CustomerAdapter(Context context, List<Customer> customerList, CustomerAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.customerList = customerList;
        this.customerListFiltered = customerList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

 /*   public String getCurrentLocation( Customer customer ) {

        float realDistance = 0;
        // Check if GPS enabled
        if (gps.canGetLocation()) {
            Location driverLocation = new Location("");
            driverLocation.setLatitude(gps.getLatitude());// current latitude
            driverLocation.setLongitude(gps.getLongitude());//current  Longitude
            Toast.makeText(context,gps.getLatitude()+"--"+gps.getLongitude(),Toast.LENGTH_LONG).show();
            Location customerLocation = new Location("");

            String location = customer.getLatLog();
            String[] afterSplitLoc = location.split(",");
            double customer_latitude = Double.parseDouble(afterSplitLoc[0]);
            double customer_longitude = Double.parseDouble(afterSplitLoc[1]);
            customerLocation.setLatitude(customer_latitude);
            customerLocation.setLongitude(customer_longitude);
            realDistance = driverLocation.distanceTo(customerLocation);

        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
          //  gps.showSettingsAlert();
        }

        return String.format("%.2f", realDistance) + " km";
    }*/

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Customer customer = customerListFiltered.get(position);
        gps = new GPSTracker(context);
        holder.name.setText(customer.getName());
        holder.phone.setText(customer.getPhone());


        float realDistance = 0;

            Location driverLocation = new Location("");
            driverLocation.setLatitude(gps.getLatitude());// current latitude
            driverLocation.setLongitude(gps.getLongitude());//current  Longitude

            Toast.makeText(context,gps.getLatitude()+"--"+gps.getLongitude(),Toast.LENGTH_LONG).show();
            Location customerLocation = new Location("");

            String location = customer.getLatLog();
            String[] afterSplitLoc = location.split(",");
            double customer_latitude = Double.parseDouble(afterSplitLoc[0]);
            double customer_longitude = Double.parseDouble(afterSplitLoc[1]);
            customerLocation.setLatitude(customer_latitude);
            customerLocation.setLongitude(customer_longitude);
            realDistance = driverLocation.distanceTo(customerLocation)/1000;

        holder.distance.setText(String.format("%.2f", realDistance) + " km");
        Glide.with(context)
                .load(customer.getPhotoURL())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return customerListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    customerListFiltered = customerList;
                } else {
                    List<Customer> filteredList = new ArrayList<>();
                    for (Customer row : customerList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    customerListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = customerListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                customerListFiltered = (ArrayList<Customer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CustomerAdapterListener {
        void onCustomerSelected(Customer customer);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone, distance;
        public ImageView thumbnail;
        Context context;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.user_name);
            phone = view.findViewById(R.id.phone);
            distance = view.findViewById(R.id.distance);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onCustomerSelected(customerListFiltered.get(getAdapterPosition()));
                }
            });
        }

    }

}


