package com.vizsion.vizsioncardreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class VirtualCardReaderFragment extends Fragment implements VirtualCardReader.AccountCallback {

    public static final String TAG = "CardReaderFragment";
    // Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
    // activity is interested in NFC-A devices (including other Android devices), and that the
    // system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
    public static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public VirtualCardReader virtualCardReader;
    private EditText cardData;
    private WebView webView;

    /** Called when sample is created. Displays generic UI with welcome text. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_virtual_card_reader, container, false);

        if (v != null) {

            // show spinner
            final ProgressDialog progress = new ProgressDialog(getContext());
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();

            // load web view
            webView = (WebView)v.findViewById(R.id.loginWebView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl("http://ampdev.seospeed.host/wp-login.php");

            // store class first
            final VirtualCardReaderFragment _this = this;

            webView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    progress.dismiss();

                    virtualCardReader = new VirtualCardReader(_this);

                    // Disable Android Beam and register our card reader callback
                    enableReaderMode();
                }
            });
        }

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        disableReaderMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableReaderMode();
    }

    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.enableReaderMode(activity, virtualCardReader, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.disableReaderMode(activity);
        }
    }

    @Override
    public void onAccountReceived(final String account) {
        // This callback is run on a background thread, but updates to UI elements must be performed
        // on the UI thread.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final String js = "javascript:document.getElementById('user_login').value = '" + account + "';";

                if (Build.VERSION.SDK_INT >= 19) {
                    webView.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                } else {
                    webView.loadUrl(js);
                }
            }
        });
    }
}