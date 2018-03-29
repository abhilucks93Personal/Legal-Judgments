package com.legaljudgements.admin.judgements.view;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;

import jp.wasabeef.richeditor.RichEditor;

/**
 * Created by ng on 2/27/2017.
 */
public class ViewJudgement extends AppCompatActivity {

    private RichEditor tv_desc;
    private WebView mWebView;
    private String str_desc;
    String img = "<div style=text-align:center><img src=http://api.legaljudgments.co.in/img/NewLogo.png alt=Legal Judgments height=92 width=152></div>";
    private String htmlDocument;
    private TextView numWaterMark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_judgement_description);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Judgment");
        findViewById();

        Utility.setWaterMark(ViewJudgement.this,numWaterMark);


        str_desc = getIntent().getStringExtra("desc");
        if (str_desc == null)
            str_desc = Constants.tempDetailedDescription;

        if (str_desc.length() == 0)
            str_desc = Constants.tempDetailedDescription;

        htmlDocument = "<html><body>" + "<p>" + str_desc + "</p></body></html>";
        tv_desc.loadData(htmlDocument, "text/html; charset=UTF-8", null);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.print, menu);

        return true;
    }


    private void findViewById() {
        tv_desc = (RichEditor) findViewById(R.id.view_judgement_tv_desc);
        numWaterMark= (TextView) findViewById(R.id.num_water_mark);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_print:

                String strPrintDoc = htmlDocument;

                if (strPrintDoc.contains("style=\"background-color: rgb(255, 255, 0);\""))
                    strPrintDoc = strPrintDoc.replace("style=\"background-color: rgb(255, 255, 0);\"", "");
                strPrintDoc = strPrintDoc.replace("<b>", "");
                strPrintDoc = strPrintDoc.replace("</b>", "");
                strPrintDoc = strPrintDoc.replace("<i>", "");
                strPrintDoc = strPrintDoc.replace("</i>", "");

                doWebViewPrint(strPrintDoc);


                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getPrintFormattedString(String strPrintDoc) {


    }


   /* case R.id.action_email:
            try {

        Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        // shareIntent.setType("text/html");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(htmlDocument));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Legal Judgments");
        startActivity(shareIntent);

    } catch (ActivityNotFoundException e) {
        Utility.showToast(getApplicationContext(), "No application found for this action\n" + e.getMessage());
    }


                return true;*/


    private void doWebViewPrint(String strPrintDoc) {

        // Create a WebView object specifically for printing
        WebView webView = new WebView(ViewJudgement.this);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("", "page finished loading " + url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    createWebPrintJob(view);
                }
                mWebView = null;
            }
        });

        // Generate an HTML document on the fly:


        webView.loadDataWithBaseURL("file:///android_asset/", strPrintDoc, "text/HTML", "UTF-8", null);

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

        // Save the job object for later status checking
        // mPrintJobs.add(printJob);
    }


}