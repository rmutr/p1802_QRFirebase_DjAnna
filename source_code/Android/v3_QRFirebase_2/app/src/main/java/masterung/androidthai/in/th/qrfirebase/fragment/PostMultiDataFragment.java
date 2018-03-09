package masterung.androidthai.in.th.qrfirebase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import masterung.androidthai.in.th.qrfirebase.R;
import masterung.androidthai.in.th.qrfirebase.utility.MainAlert;
import masterung.androidthai.in.th.qrfirebase.utility.WeightModel;

/**
 * Created by masterung on 4/3/2018 AD.
 */

public class PostMultiDataFragment extends Fragment{

    private String dateTimeString, weightString, uidString;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Show Date
        showDate();

//        Save Controller
        saveController();


    }   // Main Method

    private void saveController() {
        Button button = getView().findViewById(R.id.btnSaveOnFirebase);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDate();

//                Find uid
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                uidString = firebaseUser.getUid();
                Log.d("4MarchV1", "uidString ==> " + uidString);

                EditText editText = getView().findViewById(R.id.edtWeight);
                weightString = editText.getText().toString().trim();
                if (weightString.isEmpty()) {
                    MainAlert mainAlert = new MainAlert(getActivity());
                    mainAlert.normalDialog(getString(R.string.title_have_space),
                            getString(R.string.message_have_space));
                } else {

//                    Setter to Model
                    WeightModel weightModel = new WeightModel(
                            uidString,
                            dateTimeString,
                            weightString);

//                   Model Getter to Firebase
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference();
                    databaseReference.child("Weight").child(dateTimeString).setValue(weightModel);
                    editText.setText("");



                }   // if




            }
        });
    }

    private void showDate() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        dateTimeString = dateFormat.format(calendar.getTime());
        TextView textView = getView().findViewById(R.id.txtShowDate);
        textView.setText(dateTimeString);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_data, container, false);
        return view;
    }
}
