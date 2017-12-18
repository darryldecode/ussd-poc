package com.vizsion.vizsioncardreader;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.handleActivity(this,savedInstanceState);
    }

    private void handleActivity(Context c, Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            VirtualCardReaderFragment fragment = new VirtualCardReaderFragment();
            transaction.replace(R.id.cardReaderFragment,fragment);
            transaction.commit();
        }
    }
}
