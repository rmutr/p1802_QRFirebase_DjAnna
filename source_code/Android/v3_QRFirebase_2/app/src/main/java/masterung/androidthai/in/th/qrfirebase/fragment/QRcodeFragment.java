package masterung.androidthai.in.th.qrfirebase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import masterung.androidthai.in.th.qrfirebase.R;
import masterung.androidthai.in.th.qrfirebase.utility.ProductModel;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Slump on 03/09/2018.
 */

public class QRcodeFragment extends Fragment {

//    Explicit
    private ZXingScannerView zXingScannerView;



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        QR Controller
        QRController();

    }   // Main Method

    private void QRController() {
        Button button = getView().findViewById(R.id.btnQRScan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                QR Scan
                zXingScannerView = new ZXingScannerView(getActivity());
                getActivity().setContentView(zXingScannerView);
                zXingScannerView.startCamera();

                zXingScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
                    @Override
                    public void handleResult(Result result) {

                        String resultString = result.getText().toString();

                        if (resultString.length() != 0) {

                            Log.d("9MarchV1", "Result ==> " + resultString);

//                            Add result to realtime service Firebase
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference();
                            Map<String, Object> stringObjectMap = new HashMap<>();
                            stringObjectMap.put("NewsFeed", resultString);
                            databaseReference.updateChildren(stringObjectMap);

//                            Update DateTime and Result to Firebase
                            Calendar calendar = Calendar.getInstance();
                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String dateTimeString = dateFormat.format(calendar.getTime());

                            Log.d("9MarchV1", "DateTime ==> " + dateTimeString);

//                            Setter Model
                            ProductModel productModel = new ProductModel(dateTimeString, resultString);

//                            Getter Model and Update value to Firebase
                            databaseReference.child("Product").child("Px" + dateTimeString).setValue(productModel);

                            zXingScannerView.removeAllViews();
                            zXingScannerView.stopCameraPreview();
                            zXingScannerView.stopCamera();

                            Intent intent = getActivity().getIntent();
                            getActivity().finish();
                            startActivity(intent);

                        }

                    }
                });
            }


        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_qrcode, container, false);
        return view;
    }

}   // Main Class
