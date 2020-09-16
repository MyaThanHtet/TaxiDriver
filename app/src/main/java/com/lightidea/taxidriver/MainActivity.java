package com.lightidea.taxidriver;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lightidea.taxidriver.activities.MapActivity;
import com.lightidea.taxidriver.activities.RegisterActivity;
import com.lightidea.taxidriver.adapters.CustomerAdapter;
import com.lightidea.taxidriver.models.Customer;
import com.lightidea.taxidriver.utils.GPSTracker;
import com.lightidea.taxidriver.utils.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomerAdapter.CustomerAdapterListener {

    public static final String filename = "TaxiDriverData";
    private static final String TAG = "MainActivity";
    public static String Key_for_imageURL = "imageURL";
    public static String Key_for_name = "username";
    public static String Key_for_email = "useremail";

    public DrawerLayout drawerLayout;
    public NavController navController;
    public NavigationView navigationView;
    SearchView searchView;
    Toolbar toolbar;
    SharedPreferences sp;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    CustomerAdapter adapter;
    GPSTracker gps;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView recyclerView;
    private List<Customer> customerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigation();
        gps = new GPSTracker(MainActivity.this);

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));

        customerList = new ArrayList<>();
        adapter = new CustomerAdapter(this, customerList, this);
        fetchData();

    }




    public void fetchData() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String image_url = dataSnapshot1.child("imageURL").getValue(String.class);
                        String location = dataSnapshot1.child("latlog").getValue(String.class);
                        String name = dataSnapshot1.child("name").getValue(String.class);
                        String phone = dataSnapshot1.child("phone").getValue(String.class);
                        Customer customer = new Customer(name, phone, location, image_url);
                        customerList.add(customer);
                    }


                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter.notifyDataSetChanged();
    }

    public void setupNavigation() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigationView);
        View headerview = navigationView.getHeaderView(0);

        TextView textViewName = headerview.findViewById(R.id.textViewName);
        TextView textViewEmail = headerview.findViewById(R.id.textViewEmail);
        final ImageView userImage = headerview.findViewById(R.id.userImageView);

        sp = getApplicationContext().getSharedPreferences(filename, Context.MODE_PRIVATE);
        if (!sp.getString(Key_for_name, "").isEmpty()) {

            Glide.with(this)
                    .load(sp.getString(Key_for_imageURL, ""))
                    .apply(RequestOptions.circleCropTransform())
                    .into(userImage);

            textViewName.setText(sp.getString(Key_for_name, ""));
            textViewEmail.setText(sp.getString(Key_for_email, ""));
        }


        //  navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.toolbar_title, R.string.toolbar_title) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.signout:
                        signOut();
                        break;

                }
                return false;
            }
        });
    }

    private void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Signed out of google");
                    }
                });

        sp = getApplicationContext().getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_action)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }


        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.search_action:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();

    }


    @Override
    public void onCustomerSelected(Customer customer) {
        String location = customer.getLatLog();
        String[] afterSplitLoc = location.split(",");
        double customer_latitude = Double.parseDouble(afterSplitLoc[0]);
        double customer_logitude = Double.parseDouble(afterSplitLoc[1]);
        Intent i = new Intent(MainActivity.this, MapActivity.class);
        i.putExtra("latitude", gps.getLatitude());
        i.putExtra("longitude", gps.getLongitude());
        i.putExtra("customerLat", customer_latitude);
        i.putExtra("customerLong", customer_logitude);
        i.putExtra("customerName", customer.getName());
        startActivity(i);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();

    }
}
