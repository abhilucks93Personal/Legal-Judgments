package com.legaljudgements.admin.judgements.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.legaljudgements.R;
import com.legaljudgements.Utils.Constants;
import com.legaljudgements.Utils.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
    private String path;
    private ArrayList<Integer> number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_judgement_description);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Judgment");
        findViewById();

        Utility.setWaterMark(ViewJudgement.this, numWaterMark);


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
        tv_desc = findViewById(R.id.view_judgement_tv_desc);
        tv_desc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        tv_desc.setLongClickable(false);
        tv_desc.setHapticFeedbackEnabled(false);
        numWaterMark = findViewById(R.id.num_water_mark);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_print:
                String strPrintDoc = htmlDocument;
               /* if (strPrintDoc.contains("style=\"background-color: rgb(255, 255, 0);\""))
                    strPrintDoc = strPrintDoc.replaceAll("style=\"background-color: rgb(255, 255, 0);\"", "");
*/
                if (strPrintDoc.contains("background-color: rgb(255, 255, 0);"))
                    strPrintDoc = strPrintDoc.replace("background-color: rgb(255, 255, 0);", "");

                strPrintDoc = strPrintDoc.replaceAll("<b>", "");
                strPrintDoc = strPrintDoc.replaceAll("</b>", "");
                strPrintDoc = strPrintDoc.replaceAll("<i>", "");
                strPrintDoc = strPrintDoc.replaceAll("</i>", "");

                doWebViewPrint(strPrintDoc);
                return true;

            case R.id.action_email:

                String strPdfDoc = htmlDocument;
                if (strPdfDoc.contains("style=\"background-color: rgb(255, 255, 0);\""))
                    strPdfDoc = strPdfDoc.replace("style=\"background-color: rgb(255, 255, 0);\"", "");
                strPdfDoc = strPdfDoc.replace("<b>", "");
                strPdfDoc = strPdfDoc.replace("</b>", "");
                strPdfDoc = strPdfDoc.replace("<i>", "");
                strPdfDoc = strPdfDoc.replace("</i>", "");
                createPdf(strPdfDoc);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


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

    public void createPdf(String text) {

        try {

            File mFolder = new File(getExternalCacheDir() + "/LJ");
            File pdfFile = new File(mFolder.getAbsolutePath() + System.currentTimeMillis() + ".pdf");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            if (!pdfFile.exists()) {
                try {
                    pdfFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            document.open();

            HTMLWorker htmlWorker = new HTMLWorker(document);
            htmlWorker.parse(new StringReader(text));

            document.close();

            mailPdf(pdfFile);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void mailPdf(File file) {


        Uri path = Uri.fromFile(file);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("image/*");
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches) {
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) {
                best = info;
                break;
            }
        }
        if (best != null) {
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        }
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_STREAM, path);

        startActivity(intent);

    }


}