package masterung.androidthai.in.th.qrfirebase;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import masterung.androidthai.in.th.qrfirebase.fragment.GetMultiDataFragment;
import masterung.androidthai.in.th.qrfirebase.fragment.PostMultiDataFragment;
import masterung.androidthai.in.th.qrfirebase.fragment.QRcodeFragment;
import masterung.androidthai.in.th.qrfirebase.fragment.ReadAllCodeFragment;
import masterung.androidthai.in.th.qrfirebase.fragment.ReadRealTimeFragment;
import masterung.androidthai.in.th.qrfirebase.fragment.WriteRealTimeFragment;

public class ServiceActivity extends AppCompatActivity {

    //    Explicit
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String displayString;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

//        Setup Object
        setupObject();

//        Add Fragment
        addFragment(savedInstanceState);

//        Check Current User
        checkCurrentUser();

//        ReadRealTime Controller
        readRealTimeController();

        writeRealTimeController();

        postMultiDataController();

        getMultiDataController();

//        QR Code Service
        QRCodeService();

//        Read All Code
        readAllCode();

    }   // Main Method

    private void readAllCode() {
        TextView textView = findViewById(R.id.txtReadAllcode);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentServiceFragment, new ReadAllCodeFragment()).commit();
                drawerLayout.closeDrawers();

            }
        });
    }

    private void QRCodeService() {
        TextView textView = findViewById(R.id.txtQRcode);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentServiceFragment, new QRcodeFragment()).commit();
                drawerLayout.closeDrawers();

            }
        });
    }

    private void getMultiDataController() {
        TextView textView = findViewById(R.id.txtGetMultiData);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentServiceFragment, new GetMultiDataFragment())
                        .commit();
                drawerLayout.closeDrawers();

            }
        });
    }

    private void postMultiDataController() {
        TextView textView = findViewById(R.id.txtPostMultidata);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentServiceFragment, new PostMultiDataFragment())
                        .commit();
                drawerLayout.closeDrawers();

            }
        });
    }

    private void readRealTimeController() {
        TextView textView = findViewById(R.id.txtReadRealTime);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentServiceFragment, new ReadRealTimeFragment())
                        .commit();
                drawerLayout.closeDrawers();

            }
        });
    }

    private void writeRealTimeController() {
        TextView textView = findViewById(R.id.txtWriteRealTime);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentServiceFragment, new WriteRealTimeFragment())
                        .commit();
                drawerLayout.closeDrawers();

            }
        });
    }

    private void checkCurrentUser() {
        if (firebaseUser == null) {
//            Log Out

        } else {
            displayString = firebaseUser.getDisplayName();
            createToolbar();

        }
    }

    private void addFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentServiceFragment, new ReadRealTimeFragment())
                    .commit();
        }
    }

    private void setupObject() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private void createToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbarService);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Service");
        getSupportActionBar().setSubtitle(displayString + " login");

        drawerLayout = findViewById(R.id.layoutDrawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(ServiceActivity.this,
                drawerLayout, R.string.open, R.string.close);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}   // Main Class
