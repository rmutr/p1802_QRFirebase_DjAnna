package slump.com.qrfirebase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import slump.com.qrfirebase.R;
import slump.com.qrfirebase.utility.MainAlert;

/**
 * Created by Slump on 03/04/2018.
 */

public class WriteRealTimeFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Upload Controller
        uploadController();

    }   // Main Method

    private void uploadController() {
        Button button = getView().findViewById(R.id.btnUpload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = getView().findViewById(R.id.edtFillin);
                String fillInString = editText.getText().toString().trim();
                MainAlert mainAlert = new MainAlert(getActivity());

                if (fillInString.isEmpty() == true) {
                    mainAlert.normalDialog(getString(R.string.title_have_space),
                            getString(R.string.message_have_space));
                } else {

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference();

                    Map<String, Object> map = new HashMap();
                    map.put("NewsFeed", fillInString);

                    databaseReference.updateChildren(map);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contentServiceFragment, new ReadRealTimeFragment())
                            .commit();

                }

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_realtime, container, false);
        return view;
    }

}   // Main Class
