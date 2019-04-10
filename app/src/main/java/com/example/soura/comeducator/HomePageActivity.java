package com.example.soura.comeducator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = "Check";
    // declare the variable needed in activity

    MyExpandableAdapter expandableadapter;
    ExpandableListView expandableListView;
    List<String> headersList;
    private FirebaseAuth mAuth;
    HashMap<String, List<String>> itemsMap;

    //Firebase storage references
    private FirebaseDatabase mFirebasedatabase;
    //Task database references:
    private DatabaseReference mTasksDatabaseReference;
    //My Implimentation
    List<String> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Initialize Firebase Components
        mAuth = FirebaseAuth.getInstance();
        mFirebasedatabase = FirebaseDatabase.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTasksDatabaseReference = mFirebasedatabase.getReference().child("Topics");
        itemsMap = new HashMap<String, List<String>>();
        headersList = new ArrayList<>();
        tasks = new ArrayList<>();
        getDataFromFirebase();

        expandableListView = (ExpandableListView) findViewById(R.id.expandlist);
        expandableadapter = new MyExpandableAdapter(this, headersList, itemsMap);

        expandableListView.setAdapter(expandableadapter);
        expandableadapter.notifyDataSetChanged();

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, final View v, int groupPosition, int childPosition, long id)
            {
                mTasksDatabaseReference.child(headersList.get(groupPosition)).child(itemsMap.get(headersList.get(groupPosition)).get(childPosition)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String url = dataSnapshot.child("url").getValue().toString();
                        if(!url.equals("nil")) {
                            Intent video = new Intent(HomePageActivity.this, VideoViewActivity.class);
                            video.putExtra("url", url);
                            startActivity(video);
                        }
                        else
                        {
                            Snackbar.make(v, "NO Video Avialable! We will add it soon!", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return false;
            }
        });
        //get the expand of headersList
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition)
            {}
        });
        //get the collapse of headersList
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener()
        {
            @Override
            public void onGroupCollapse(int groupPosition)
            {}
        });
    }

    void getDataFromFirebase()
    {
        //Firebase Read Listener
        mTasksDatabaseReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                //Running Foreach loop
                for(DataSnapshot d : dataSnapshot.getChildren())
                {
                    tasks.add(d.getKey());
                }

                //Putting key & tasks(ArrayList) in HashMap
                itemsMap.put(dataSnapshot.getKey(),tasks);
                headersList.add(dataSnapshot.getKey());
                tasks=new ArrayList<>();

                Log.d(TAG, "onChildAdded: dataSnapshot.getChildren: "+dataSnapshot.getChildren());
                Log.d(TAG, "onChildAdded: KEY"+dataSnapshot.getKey()+" value "+dataSnapshot.getValue().toString());
                expandableadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Log.d(TAG, "onChildChanged: "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {}

            @Override
            public void onCancelled(DatabaseError databaseError)
            {}
        });

    }

    private void updateUI()
    {
        Intent account = new Intent(HomePageActivity.this,LoginActivity.class);
        startActivity(account);
        finish();
    }

    private static final int TIME_INTERVAL = 1000;
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            mAuth.signOut();
            updateUI();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share)
        {
            //temporary link
            Intent video = new Intent(HomePageActivity.this,VideoViewActivity.class);
            startActivity(video);
        }
        else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
