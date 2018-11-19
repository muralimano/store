package com.oceansoftwares.store.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.oceansoftwares.store.R;
import com.oceansoftwares.store.activities.MainActivity;
import com.oceansoftwares.store.app.App;
import com.oceansoftwares.store.app.MyAppPrefsManager;
import com.oceansoftwares.store.customs.DialogLoader;
import com.oceansoftwares.store.models.Comment;
import com.oceansoftwares.store.models.device_model.AppSettingsDetails;
import com.oceansoftwares.store.network.APIClient;

import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;

public class Comment_Fragment extends Fragment implements View.OnClickListener {

    DialogLoader dialogLoader;

    Button btn_attach,btn_submit;
    EditText ed_comment;
    ImageView img;

    Uri URI = null;
    private static final int PICK_FROM_GALLERY = 101;
    String email, subject, message, attachmentFile;
    int columnIndex;

    CoordinatorLayout coordinator_container;
    private AppSettingsDetails appSettings;
    MyAppPrefsManager myAppPrefsManager;

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.comment, container, false);

        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("FEEDBACK");

        // Get AppSettingsDetails from ApplicationContext
        appSettings = ((App) getContext().getApplicationContext()).getAppSettingsDetails();

        myAppPrefsManager = new MyAppPrefsManager(getContext());

        btn_attach=(Button)rootView.findViewById(R.id.btn_attach);

        btn_submit=(Button)rootView.findViewById(R.id.btn_submit);
        ed_comment = (EditText) rootView.findViewById(R.id.ed_comment);

        img=(ImageView)rootView.findViewById(R.id.imgView);



        dialogLoader = new DialogLoader(getContext());

        btn_attach.setOnClickListener(this);

       btn_submit.setOnClickListener(this);

        return rootView;
    }

    public void submit_comment(String comment){
        dialogLoader.showProgressDialog();

        Call<Comment> call = APIClient.getInstance()
                .addComment(comment);

        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, retrofit2.Response<Comment> response) {

                dialogLoader.hideProgressDialog();

                Log.e("COmment Message:",response.body().getMessage());

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();

                    }
                    else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            /**
             * Get Path
             */
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            img.setImageURI(selectedImage);
           // ((ImageView) rootView.findViewById(R.id.imgView)).setImageURI(selectedImage);

            //String[] filePathColumn = {MediaStore.};


            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            attachmentFile = cursor.getString(columnIndex);
            Log.e("Attachment Path:", attachmentFile);
            URI = Uri.parse("file://" + attachmentFile);
            cursor.close();


        }

    }

    @Override
    public void onClick(View v) {
        if (v == btn_attach) {
            openGallery();
            btn_submit.setVisibility(View.VISIBLE);
        }
        if (v == btn_submit) {
            try {
                email = "govarthanansakthi@gmail.com";
                subject = "Feedback";
                message = "Query";

                String customerEmail=myAppPrefsManager.getCustomerEmail();

                String finalEmail=email+","+customerEmail;


                String contactemail=appSettings.getContactUsEmail();
                //Log.e("Contact Email",finalEmail);

                final Intent emailIntent = new Intent(
                        android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[]{contactemail,customerEmail});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        subject);
                if (URI != null) {
                    emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
                }
                emailIntent
                        .putExtra(android.content.Intent.EXTRA_TEXT, message);
                this.startActivity(Intent.createChooser(emailIntent,
                        "Sending email..."));

            } catch (Throwable t) {
                Toast.makeText(getContext(),
                        "Request failed try again: " + t.toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void openGallery () {
        Intent intent = new Intent();
        intent.setType("image/*");
       // intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra("return-data", true);
        startActivityForResult(
                Intent.createChooser(intent, "Complete action using"),
                PICK_FROM_GALLERY);

    }
}
