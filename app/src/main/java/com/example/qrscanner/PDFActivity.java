package com.example.qrscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class PDFActivity extends AppCompatActivity {

    private static final String TAG = "PdfCreatorActivity";
    private EditText mContentEditText;
    private Button mCreateButton;
    private File pdfFile;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        mContentEditText = (EditText)findViewById(R.id.edit_text_content);
        mCreateButton = (Button)findViewById(R.id.button_create);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContentEditText.getText().toString().isEmpty()){
                    mContentEditText.setError("Isi Tulisan Terlebih Dahulu!");
                    mContentEditText.requestFocus();
                    return;
                }
                try{
                    createPdfWrapper();
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }catch (DocumentException e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void createPdfWrapper()throws FileNotFoundException,DocumentException{
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)){
                    showMessegeOKCancel("Yoo need allow your storage permession",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        }
                    });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }else {
            createPdf();
        }
    }
   // @Override
    public void onRequestPermissionResult(int req,String[]permission,int[]grantResult){
        switch (req){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
                default:
                    super.onRequestPermissionsResult(req,permission,grantResult);
        }
    }
    private void showMessegeOKCancel(String message,DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Ok",okListener)
                .setNegativeButton("Keluar",null)
                .create()
                .show();
    }
    private void createPdf() throws FileNotFoundException,DocumentException{
        File docsFolder = new File(Environment.getExternalStorageDirectory()+"/Documents");
        if (!docsFolder.exists()){
            docsFolder.mkdir();
            Log.i(TAG,"Buat Folder PDF Baru");
        }
        pdfFile = new File(docsFolder.getAbsolutePath(),"PDFgenerate.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph(mContentEditText.getText().toString()));
        document.close();
        previewPdf();
    }
    private void previewPdf() {
        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri photoURI = FileProvider.getUriForFile(this,"com.example.qrscanner.fileprovider",pdfFile);
            intent.setDataAndType(photoURI, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }else{
            Toast.makeText(this,"Download aplikasi pdf viewer untuk melihat hasil generate",Toast.LENGTH_SHORT).show();
        }
    }
}
