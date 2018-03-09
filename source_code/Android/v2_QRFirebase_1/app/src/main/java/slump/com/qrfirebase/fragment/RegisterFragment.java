package slump.com.qrfirebase.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import slump.com.qrfirebase.MainActivity;
import slump.com.qrfirebase.R;
import slump.com.qrfirebase.utility.MainAlert;

/**
 * Created by Slump on 03/03/2018.
 */

public class RegisterFragment extends Fragment{

//    Explicit
    private String nameString, emailString, passwordString;
    private ProgressDialog progressDialog;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Create Toolbar
        createToolbar();

        setHasOptionsMenu(true);

    }   // Main Method

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.itemSave) {
            saveController();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveController() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Save value to Firebase");
        progressDialog.setMessage("Please wait few minus...");
        progressDialog.show();

//        Get Value from EditText
        EditText nameEditText     = getView().findViewById(R.id.edtName);
        EditText emailEditText    = getView().findViewById(R.id.edtEmail);
        EditText passwordEditText = getView().findViewById(R.id.edtPassword);

        this.nameString     = nameEditText.getText().toString().trim();
        this.emailString    = emailEditText.getText().toString().trim();
        this.passwordString = passwordEditText.getText().toString().trim();

//        Check Space
        if ((nameString.isEmpty() == true) || (emailString.isEmpty() == true) || (passwordString.isEmpty() == true)) {

            MainAlert mainAlert = new MainAlert(getActivity());
            mainAlert.normalDialog(getString(R.string.title_have_space)
                    , getString(R.string.message_have_space));
            progressDialog.dismiss();
        } else {

            uploadToFirebase();

        }
    }

    private void uploadToFirebase() {

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() == true) {
//                            Register successed

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest
                                    .Builder().setDisplayName(nameString).build();

                            firebaseUser.updateProfile(userProfileChangeRequest)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("4MarchV1", "Complete Display Name");
                                        }
                                    });


                            Toast.makeText(getActivity(), "Welcome register success",
                                    Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();

                        } else {
//                            Register false
                            String resultString = task.getException().getMessage();
                            MainAlert mainAlert = new MainAlert(getActivity());
                            mainAlert.normalDialog("Register False", resultString);

                        }

                        progressDialog.dismiss();

                    }   // onComplete
                });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_register, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void createToolbar() {
        Toolbar toolbar = getView().findViewById(R.id.toobarRegister);

        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.th_register));
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle(getString(R.string.sub_register));

        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

}   // Main Class
