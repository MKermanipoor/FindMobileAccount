package ir.mkp.findmobileaccount;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        textView = findViewById(R.id.text_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkPermission = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (MainActivity.this.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                10);

                    checkPermission = MainActivity.this.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
                }

                if (checkPermission){
                    runAsyncTask();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void runAsyncTask(){
        new AsyncTask<Void, Void , String[]>() {
            @Override
            protected String[] doInBackground(Void... voids) {
                Set<String> accounts = new HashSet<>();

                ContentResolver contentResolver = MainActivity.this.getContentResolver();
                Cursor cursor = contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Contactables.HAS_PHONE_NUMBER + " = ?",
                        new String[]{"1"},
                        ContactsContract.CommonDataKinds.Contactables.LOOKUP_KEY
                );


                if (cursor != null && cursor.moveToFirst()){
                        int accountTypeIndex = cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE);
                    do{
                        String account = cursor.getString(accountTypeIndex);
                        accounts.add(account);
                    }while (cursor.moveToNext());
                }
                String[] accountsString = new String[accounts.size()];
                accounts.toArray(accountsString);
                return accountsString;
            }

            @Override
            protected void onPostExecute(String[] strings) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : strings)
                    stringBuilder.append(s).append("\n");

                textView.setText(stringBuilder.toString());
            }
        }.execute();


    }
}
