package ai.extime.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snatik.storage.Storage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ai.extime.Activity.MainActivity;
import ai.extime.Adapters.ContactAdapter;
import ai.extime.Interfaces.FileBarInter;
import ai.extime.Interfaces.PopupsInter;
import ai.extime.Adapters.FileAdapter;
import ai.extime.Events.CopyFile;
import ai.extime.Interfaces.Postman;
import ai.extime.Models.FileCall2me;
import com.extime.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by patal on 21.09.2017.
 */

public class FilesFragment extends Fragment implements PopupsInter, FileBarInter {

    private View mainView;

    private RecyclerView containerFiles;

    private LinearLayoutManager mLinearLayoutManager;

    private ArrayList<FileCall2me> listOfFileCall2mes;

    private static final int FILE_SELECT_CODE = 228;

    private ArrayList<FileCall2me> listOfFiles;

    private ArrayList<View> openedViews;

    private FileAdapter fileAdapter;

    public FrameLayout lastOpenPopup = null;

    public FrameLayout filesPopup = null;

    public FrameLayout filesDrivePopup = null;

    public FrameLayout filesLinkPopup = null;

    public FrameLayout filesHashPopup = null;

    public boolean checkAllTypes = true;
    public List<Boolean> listCheckTypes;

    public boolean sortDateFiles = false;
    public boolean sortAZFiles = false;
    CheckBox all,pdf,xls,doc,photo;

    private Toolbar toolbarC;

    private ArrayList<FileCall2me> getData(){
        listOfFiles = new ArrayList<>();
        int countpdf = 0;
        int countXls = 0;
        int countDoc = 0;
        int countPhoto = 0;
        java.io.File file = new java.io.File (Environment.getExternalStorageDirectory().getPath()+"//Extime/ExtimeFiles");
        if(file.isDirectory()){
            java.io.File[] fileList = file.listFiles();
            for(int i = fileList.length-1; i>=0;i--){

                String type = fileList[i].getName().substring(fileList[i].getName().lastIndexOf(".")+1);
                String path = fileList[i].getAbsolutePath();
                String name = "";
                try {
                    name = fileList[i].getName().substring(0, fileList[i].getName().indexOf("."));
                }catch (Exception e){

                }
                Date date = new Date(fileList[i].lastModified());

                //     listOfFiles.add(new File(type, path, name, date.toString()));


                if(type.equals("pdf"))
                    countpdf++;
                else if(type.equals("xls"))
                    countXls++;
                else if(type.equals("doc") || type.equals("docx"))
                    countDoc++;
                else if(type.toLowerCase().equals("jpg") || type.toLowerCase().equals("jpeg") || type.toLowerCase().equals("png") || type.toLowerCase().equals("bmp"))
                    countPhoto++;


                if(type.equals("pdf")){
                    if(listCheckTypes.get(0) == true){
                        listOfFiles.add(new FileCall2me(type, path, name, date.toString()));
                    }
                }else if(type.equals("xls")){
                    if(listCheckTypes.get(1) == true){
                        listOfFiles.add(new FileCall2me(type, path, name, date.toString()));
                    }
                }else if(type.equals("doc") || type.equals("docx")){
                    if(listCheckTypes.get(2) == true){
                        listOfFiles.add(new FileCall2me(type, path, name, date.toString()));
                    }
                }else if(type.toLowerCase().equals("jpg") || type.toLowerCase().equals("jpeg") || type.toLowerCase().equals("png") || type.toLowerCase().equals("bmp")){
                    if(listCheckTypes.get(3) == true){
                        listOfFiles.add(new FileCall2me(type, path, name, date.toString()));
                    }
                }else{
                    if(checkAllTypes){
                        //  listOfFiles.add(new File(type, path, name, date.toString()));
                    }
                }

            }

            TextView countAll_t = (TextView) getActivity().findViewById(R.id.popupFilesAllCount);
            TextView countPdf_t = (TextView) getActivity().findViewById(R.id.popupFilesSamsungC);
            TextView countEx_t = (TextView) getActivity().findViewById(R.id.popupFilesSheet);
            TextView countDoc_t = (TextView) getActivity().findViewById(R.id.popupFilesDoc);
            TextView countPhoto_t = (TextView) getActivity().findViewById(R.id.popupFilesPhoto);
            countAll_t.setText("("+String.valueOf(countpdf + countXls + countDoc + countPhoto)+")");
            countPdf_t.setText("("+String.valueOf(countpdf)+")");
            countEx_t.setText("("+String.valueOf(countXls)+")");
            countDoc_t.setText("("+String.valueOf(countDoc)+")");
            countPhoto_t.setText("("+String.valueOf(countPhoto)+")");
        }
        return listOfFiles;
    }

    private void initRecycler(){
        fileAdapter = new FileAdapter(getActivity(),getData(),this);
        containerFiles.setAdapter(fileAdapter);
        FirstsortAZ();
        containerFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOtherPopups();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Storage storage = new Storage(mainView.getContext());
                        Uri currFileURI = data.getData();



                    }catch (Exception e){e.printStackTrace();}
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }



    private void initFragment(){
        containerFiles = (RecyclerView) mainView.findViewById(R.id.filesContainer);
        mLinearLayoutManager = new LinearLayoutManager(mainView.getContext());
        containerFiles.setLayoutManager(mLinearLayoutManager);
        openedViews = new ArrayList<>();
        getActivity().findViewById(R.id.create_item).setOnClickListener(v -> {
            ((MainActivity)getActivity()).chooseDialog();
            //showFileChooser();
        });

        listCheckTypes = new ArrayList<>();
        listCheckTypes.add(true);
        listCheckTypes.add(true);
        listCheckTypes.add(true);
        listCheckTypes.add(true);
        all = (CheckBox) getActivity().findViewById(R.id.allFilesCheck);
        pdf = (CheckBox) getActivity().findViewById(R.id.PDFCheck);
        xls = (CheckBox) getActivity().findViewById(R.id.SheetCheck);
        doc = (CheckBox) getActivity().findViewById(R.id.docFilesCheck);
        photo = (CheckBox) getActivity().findViewById(R.id.PhotoCheck);

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");


        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a FileCall2me to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(mainView.getContext(), "Please install a FileCall2me Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        //mainView = inflater.inflate(R.layout.files_layout_content, viewGroup, false);

        mainView = inflater.inflate(R.layout.layout_files_under_construct, viewGroup, false);

        toolbarC = ((Postman) getActivity()).getToolbar();

        setHasOptionsMenu(true);

        //initFragment();
        //initRecycler();

        /*mainView.findViewById(R.id.closer2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeOtherPopups();
            }
        });


        getActivity().findViewById(R.id.allTypesFilesLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkedAllTypes(true);
            }
        });

        CheckBox checkBoxAll = (CheckBox) getActivity().findViewById(R.id.allFilesCheck);
        checkBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkedAllTypes(false);
            }
        });



        getActivity().findViewById(R.id.PdfFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkedPDFFiles(true);
            }
        });

        CheckBox checkBoxPDF = (CheckBox) getActivity().findViewById(R.id.PDFCheck);
        checkBoxPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkedPDFFiles(false);
            }
        });


        getActivity().findViewById(R.id.SheetsFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SheetCheckFiles(true);
            }
        });

        CheckBox checkBoxCheets = (CheckBox) getActivity().findViewById(R.id.SheetCheck);
        checkBoxCheets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SheetCheckFiles(false);
            }
        });


        getActivity().findViewById(R.id.docFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                docFilesCheched(true);
                );

            }
        });

        CheckBox checkBox = (CheckBox) getActivity().findViewById(R.id.docFilesCheck);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                docFilesCheched(false);
            }
        });

        getActivity().findViewById(R.id.photoFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoFilesChecked(true);

            }
        });

        CheckBox PhotoCheckChBox = (CheckBox) getActivity().findViewById(R.id.PhotoCheck);
        PhotoCheckChBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoFilesChecked(false);
            }
        });

        getActivity().findViewById(R.id.timeSortFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortTime();
            }
        });

        getActivity().findViewById(R.id.    sortElementsFiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortAZ();
            }
        });*/


        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!ContactAdapter.checkMerge)
        menu.getItem(0).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        ((MainActivity) mainView.getContext()).setContactsToContent();

        ((TextView) toolbarC.findViewById(R.id.toolbar_title)).setText("Files");

        if(!ContactAdapter.checkMerge)
        ((TextView) toolbarC.findViewById(R.id.cancel_toolbar)).setVisibility(View.GONE);

        toolbarC.findViewById(R.id.toolbar_kanban_clicker).setVisibility(View.GONE);

        super.onPrepareOptionsMenu(menu);
    }


    public void sortTime(){
        FilesFragment filesFragment = this;
        listOfFiles = new ArrayList<>();

        listOfFiles = getfilesFromFolder();

        if(!sortDateFiles){
            Collections.sort(listOfFiles, new Comparator<FileCall2me>() {
                @Override
                public int compare(FileCall2me file, FileCall2me t1) {
                    return t1.getDate().compareTo(file.getDate());
                }
            });
            sortDateFiles = true;
        }else{
            Collections.sort(listOfFiles, new Comparator<FileCall2me>() {
                @Override
                public int compare(FileCall2me file, FileCall2me t1) {
                    return file.getDate().compareTo(t1.getDate());
                }
            });
            sortDateFiles = false;
        }

        FileAdapter fileAd = new FileAdapter(getActivity(),listOfFiles,filesFragment);
        containerFiles.setAdapter(fileAd);
        //--------------------
        ((TextView) getActivity().findViewById(R.id.sortTextFiles)).setTextColor(getResources().getColor(R.color.gray));
        ((ImageView) getActivity().findViewById(R.id.sortFilesFrom)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) getActivity().findViewById(R.id.sortFilesFromTime)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
    }


    public void sortAfterAddFileByTime(){
        FilesFragment filesFragment = this;
        listOfFiles = new ArrayList<>();

        listOfFiles = getfilesFromFolder();


        Collections.sort(listOfFiles, new Comparator<FileCall2me>() {
            @Override
            public int compare(FileCall2me file, FileCall2me t1) {
                return t1.getDate().compareTo(file.getDate());
            }
        });
        sortDateFiles = true;


        FileAdapter fileAd = new FileAdapter(getActivity(),listOfFiles,filesFragment);
        containerFiles.setAdapter(fileAd);
        //--------------------
        ((TextView) getActivity().findViewById(R.id.sortTextFiles)).setTextColor(getResources().getColor(R.color.gray));
        ((ImageView) getActivity().findViewById(R.id.sortFilesFrom)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) getActivity().findViewById(R.id.sortFilesFromTime)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.primary));
    }

    public void sortAZ(){
        FilesFragment filesFragment = this;
        listOfFiles = new ArrayList<>();

        listOfFiles = getfilesFromFolder();
        TextView TextAZ = (TextView) getActivity().findViewById(R.id.sortTextFiles);
        if(!sortAZFiles){
            Collections.sort(listOfFiles, new Comparator<FileCall2me>() {
                @Override
                public int compare(FileCall2me file, FileCall2me t1) {
                    return file.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                }
            });
            sortAZFiles = true;
            TextAZ.setText("A-Z");
        }else{
            Collections.sort(listOfFiles, new Comparator<FileCall2me>() {
                @Override
                public int compare(FileCall2me file, FileCall2me t1) {
                    return t1.getName().toLowerCase().compareTo(file.getName().toLowerCase());
                }

            });
            TextAZ.setText("Z-A");
            sortAZFiles = false;
        }
        FileAdapter fileAd = new FileAdapter(getActivity(),listOfFiles,filesFragment);
        containerFiles.setAdapter(fileAd);
        //---------------------
        ((TextView) getActivity().findViewById(R.id.sortTextFiles)).setTextColor(getResources().getColor(R.color.primary));
        ((ImageView) getActivity().findViewById(R.id.sortFilesFrom)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) getActivity().findViewById(R.id.sortFilesFromTime)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));

    }


    public void FirstsortAZ(){
        FilesFragment filesFragment = this;
        listOfFiles = new ArrayList<>();

        listOfFiles = getfilesFromFolder();
        TextView TextAZ = (TextView) getActivity().findViewById(R.id.sortTextFiles);
        TextAZ.setText("A-Z");

        Collections.sort(listOfFiles, new Comparator<FileCall2me>() {
            @Override
            public int compare(FileCall2me file, FileCall2me t1) {
                return file.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
            }
        });
        sortAZFiles = true;

        FileAdapter fileAd = new FileAdapter(getActivity(),listOfFiles,filesFragment);
        containerFiles.setAdapter(fileAd);
        //---------------------
        ((TextView) getActivity().findViewById(R.id.sortTextFiles)).setTextColor(getResources().getColor(R.color.primary));
        ((ImageView) getActivity().findViewById(R.id.sortFilesFrom)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));
        ((ImageView) getActivity().findViewById(R.id.sortFilesFromTime)).setColorFilter(ContextCompat.getColor(mainView.getContext(), R.color.gray));

    }

    public ArrayList getfilesFromFolder(){
        ArrayList<FileCall2me> listOfFile = new ArrayList<>();
        java.io.File file = new java.io.File (Environment.getExternalStorageDirectory().getPath()+"//Call2me/Call2MeFiles");
        if(file.isDirectory()){
            java.io.File[] fileList = file.listFiles();
            for(int i = fileList.length-1; i>=0;i--) {

                try {

                    String type = fileList[i].getName().substring(fileList[i].getName().lastIndexOf(".") + 1);
                    String path = fileList[i].getAbsolutePath();
                    String name = fileList[i].getName().substring(0, fileList[i].getName().indexOf("."));

                    Date date = new Date(fileList[i].lastModified());

                    if (type.equals("pdf")) {
                        if (listCheckTypes.get(0) == true) {
                            listOfFile.add(new FileCall2me(type, path, name, date.toString()));
                        }
                    } else if (type.equals("xls")) {
                        if (listCheckTypes.get(1) == true) {
                            listOfFile.add(new FileCall2me(type, path, name, date.toString()));
                        }
                    } else if (type.equals("doc") || type.equals("docx")) {
                        if (listCheckTypes.get(2) == true) {
                            listOfFile.add(new FileCall2me(type, path, name, date.toString()));
                        }
                    } else if (type.toLowerCase().equals("jpg") || type.toLowerCase().equals("jpeg") || type.toLowerCase().equals("png") || type.toLowerCase().equals("bmp")) {
                        if (listCheckTypes.get(3) == true) {
                            listOfFile.add(new FileCall2me(type, path, name, date.toString()));
                        }
                    } else {
                        if (checkAllTypes) {
                            //       listOfFile.add(new File(type, path, name, date.toString()));
                        }
                    }

                    //listOfFile.add(new File(type, path, name, date.toString()));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return listOfFile;
    }

    public void checkAllChecked(){
        boolean c = true;
        for(int i = 0;i<4;i++){
            if(!listCheckTypes.get(i))
                c = false;
        }
        if(c) all.setChecked(true);
        else all.setChecked(false);
    }

    public void checkedAllTypes(boolean check){
        CheckBox allFiles = (CheckBox) getActivity().findViewById(R.id.allFilesCheck);
        if(allFiles.isChecked() && check){
            listCheckTypes.set(3,false);
            listCheckTypes.set(2,false);
            listCheckTypes.set(1,false);
            listCheckTypes.set(0,false);
            checkAllTypes = false;

            all.setChecked(false);
            pdf.setChecked(false);
            xls.setChecked(false);
            doc.setChecked(false);
            photo.setChecked(false);

            updateListFiles();
        }else if (!allFiles.isChecked() && check){
            listCheckTypes.set(3,true);
            listCheckTypes.set(2,true);
            listCheckTypes.set(1,true);
            listCheckTypes.set(0,true);
            checkAllTypes = true;

            all.setChecked(true);
            pdf.setChecked(true);
            xls.setChecked(true);
            doc.setChecked(true);
            photo.setChecked(true);

            updateListFiles();
        }else if(allFiles.isChecked() && !check){
            listCheckTypes.set(3,true);
            listCheckTypes.set(2,true);
            listCheckTypes.set(1,true);
            listCheckTypes.set(0,true);
            checkAllTypes = true;

            // all.setChecked(true);
            pdf.setChecked(true);
            xls.setChecked(true);
            doc.setChecked(true);
            photo.setChecked(true);

            updateListFiles();
        }else{
            listCheckTypes.set(3,false);
            listCheckTypes.set(2,false);
            listCheckTypes.set(1,false);
            listCheckTypes.set(0,false);
            checkAllTypes = false;

            //    all.setChecked(false);
            pdf.setChecked(false);
            xls.setChecked(false);
            doc.setChecked(false);
            photo.setChecked(false);

            updateListFiles();
        }

    }

    public void checkedPDFFiles(boolean check){
        CheckBox allFiles = (CheckBox) getActivity().findViewById(R.id.PDFCheck);
        if(allFiles.isChecked() && check){
            allFiles.setChecked(true);
            checkAllTypes = false;
            listCheckTypes.set(3,false);
            listCheckTypes.set(2,false);
            listCheckTypes.set(1,false);
            listCheckTypes.set(0,true);
            all.setChecked(false);
            xls.setChecked(false);
            doc.setChecked(false);
            photo.setChecked(false);
            updateListFiles();
        }else if (!allFiles.isChecked() && check){
            allFiles.setChecked(true);
            checkAllTypes = false;
            listCheckTypes.set(3,false);
            listCheckTypes.set(2,false);
            listCheckTypes.set(1,false);
            listCheckTypes.set(0,true);
            all.setChecked(false);
            xls.setChecked(false);
            doc.setChecked(false);
            photo.setChecked(false);
            updateListFiles();
        }else if(allFiles.isChecked() && !check){
            listCheckTypes.set(0,true);
            checkAllChecked();
            updateListFiles();
        }else{
            all.setChecked(false);
            listCheckTypes.set(0,false);
            updateListFiles();
        }
    }

    public void SheetCheckFiles(boolean check){
        CheckBox allFiles = (CheckBox) getActivity().findViewById(R.id.SheetCheck);
        if(allFiles.isChecked() && check){
            allFiles.setChecked(true);
            //    fileAdapter = new FileAdapter(context ,list_empty ,fg);
            //    containerFiles.setAdapter(fileAdapter);
            checkAllTypes = false;
            listCheckTypes.set(3,false);
            listCheckTypes.set(2,false);
            listCheckTypes.set(1,true);
            listCheckTypes.set(0,false);

            all.setChecked(false);
            pdf.setChecked(false);
            doc.setChecked(false);
            photo.setChecked(false);
            updateListFiles();
        }else if (!allFiles.isChecked() && check){
            allFiles.setChecked(true);
            //     checkAllTypes = true;
            checkAllTypes = false;
            listCheckTypes.set(3,false);
            listCheckTypes.set(2,false);
            listCheckTypes.set(1,true);
            listCheckTypes.set(0,false);

            all.setChecked(false);
            pdf.setChecked(false);
            doc.setChecked(false);
            photo.setChecked(false);
            updateListFiles();
        }else if(allFiles.isChecked() && !check){
            listCheckTypes.set(1,true);
            checkAllChecked();
            updateListFiles();
        }else{
            all.setChecked(false);
            listCheckTypes.set(1,false);
            updateListFiles();
        }
    }


    public void docFilesCheched(boolean check){
        CheckBox allFiles = (CheckBox) getActivity().findViewById(R.id.docFilesCheck);
        if(allFiles.isChecked() && check){
            allFiles.setChecked(true);
            checkAllTypes = false;
            listCheckTypes.set(3,false);
            listCheckTypes.set(2,true);
            listCheckTypes.set(1,false);
            listCheckTypes.set(0,false);

            all.setChecked(false);
            pdf.setChecked(false);
            xls.setChecked(false);
            photo.setChecked(false);
            updateListFiles();
        }else if (!allFiles.isChecked() && check){

            allFiles.setChecked(true);
            //     checkAllTypes = true;
            checkAllTypes = false;
            listCheckTypes.set(3,false);
            listCheckTypes.set(2,true);
            listCheckTypes.set(1,false);
            listCheckTypes.set(0,false);

            all.setChecked(false);
            pdf.setChecked(false);
            xls.setChecked(false);
            photo.setChecked(false);
            updateListFiles();
        }else if(allFiles.isChecked() && !check){
            listCheckTypes.set(2,true);
            checkAllChecked();
            updateListFiles();
        }else{
            all.setChecked(false);
            listCheckTypes.set(2,false);
            updateListFiles();
        }
    }


    public void photoFilesChecked(boolean check){
        CheckBox allFiles = (CheckBox) getActivity().findViewById(R.id.PhotoCheck);
        if(allFiles.isChecked() && check){
            allFiles.setChecked(true);
            checkAllTypes = false;
            listCheckTypes.set(3,true);
            listCheckTypes.set(2,false);
            listCheckTypes.set(1,false);
            listCheckTypes.set(0,false);

            all.setChecked(false);
            pdf.setChecked(false);
            xls.setChecked(false);
            doc.setChecked(false);

            updateListFiles();
        }else if (!allFiles.isChecked() && check){
            allFiles.setChecked(true);
            //     checkAllTypes = true;
            checkAllTypes = false;
            listCheckTypes.set(3,true);
            listCheckTypes.set(2,false);
            listCheckTypes.set(1,false);
            listCheckTypes.set(0,false);

            all.setChecked(false);
            pdf.setChecked(false);
            xls.setChecked(false);
            doc.setChecked(false);

            updateListFiles();
        }else if(allFiles.isChecked() && !check){
            listCheckTypes.set(3,true);
            checkAllChecked();
            updateListFiles();
        }else{
            all.setChecked(false);
            listCheckTypes.set(3,false);
            updateListFiles();
        }
    }


    public void updateListFiles(){
        listOfFiles = new ArrayList<>();
        java.io.File file = new java.io.File (Environment.getExternalStorageDirectory().getPath()+"//Call2me/Call2MeFiles");
        if(file.isDirectory()){
            java.io.File[] fileList = file.listFiles();
            for(int i = fileList.length-1; i>=0;i--){

                String type = fileList[i].getName().substring(fileList[i].getName().lastIndexOf(".")+1);
                String path = fileList[i].getAbsolutePath();
                String name = fileList[i].getName().substring(0,fileList[i].getName().indexOf("."));
                Date date = new Date(fileList[i].lastModified());

                if(type.equals("pdf")){
                    if(listCheckTypes.get(0) == true){
                        listOfFiles.add(new FileCall2me(type, path, name, date.toString()));
                    }
                }else if(type.equals("xls")){
                    if(listCheckTypes.get(1) == true){
                        listOfFiles.add(new FileCall2me(type, path, name, date.toString()));
                    }
                }else if(type.equals("doc") || type.equals("docx")){
                    if(listCheckTypes.get(2) == true){
                        listOfFiles.add(new FileCall2me(type, path, name, date.toString()));
                    }
                }else if(type.toLowerCase().equals("jpg") || type.toLowerCase().equals("jpeg") || type.toLowerCase().equals("png") || type.toLowerCase().equals("bmp")){
                    if(listCheckTypes.get(3) == true){
                        listOfFiles.add(new FileCall2me(type, path, name, date.toString()));
                    }
                }else{
                    if(checkAllTypes){
                        //  listOfFiles.add(new File(type, path, name, date.toString()));
                    }
                }

            }


            FileAdapter fileAd = new FileAdapter(getActivity(),listOfFiles,this);
            containerFiles.setAdapter(fileAd);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    public void showAllFilesPopup(){
        filesPopup = (FrameLayout) mainView.getRootView().findViewById(R.id.popupFilesTypes);

        if(filesPopup.getVisibility() == View.VISIBLE){
            getActivity().findViewById(R.id.folderFilesLineActive).setVisibility(View.GONE);
            filesPopup.setVisibility(View.GONE);
            return;
        }
        closeOtherPopups();
        //   getActivity().findViewById(R.id.folderFilesLineActive).setBackgroundColor(0);
        getActivity().findViewById(R.id.folderFilesLineActive).setVisibility(View.VISIBLE);
        filesPopup.setVisibility(View.VISIBLE);
        lastOpenPopup = filesPopup;


    }

    public void showAllCloud(){
        filesDrivePopup = (FrameLayout) mainView.getRootView().findViewById(R.id.popupFilesClouds);

        if(filesDrivePopup.getVisibility() == View.VISIBLE){
            getActivity().findViewById(R.id.driveFileLineActive).setVisibility(View.GONE);
            filesDrivePopup.setVisibility(View.GONE);
            return;
        }
        closeOtherPopups();
        //  getActivity().findViewById(R.id.driveFileLineActive).setBackgroundColor(0);
        getActivity().findViewById(R.id.driveFileLineActive).setVisibility(View.VISIBLE);
        filesDrivePopup.setVisibility(View.VISIBLE);
        lastOpenPopup = filesDrivePopup;
    }

    public void showAllLink(){
        filesLinkPopup = (FrameLayout) mainView.getRootView().findViewById(R.id.popupFilesLink);

        if(filesLinkPopup.getVisibility() == View.VISIBLE){
            getActivity().findViewById(R.id.linkFileLineActive).setVisibility(View.GONE);
            filesLinkPopup.setVisibility(View.GONE);
            return;
        }
        closeOtherPopups();
        //   getActivity().findViewById(R.id.linkFileLineActive).setBackgroundColor(0);
        getActivity().findViewById(R.id.linkFileLineActive).setVisibility(View.VISIBLE);
        filesLinkPopup.setVisibility(View.VISIBLE);
        lastOpenPopup = filesLinkPopup;

    }

    public void showAllHash(){
        filesHashPopup = (FrameLayout) mainView.getRootView().findViewById(R.id.popupFilesHash);

        if(filesHashPopup.getVisibility() == View.VISIBLE){
            getActivity().findViewById(R.id.LineActiveHash).setVisibility(View.GONE);
            filesHashPopup.setVisibility(View.GONE);
            return;
        }
        closeOtherPopups();
        //  getActivity().findViewById(R.id.LineActiveHash).setBackgroundColor(0);
        getActivity().findViewById(R.id.LineActiveHash).setVisibility(View.VISIBLE);

        filesHashPopup.setVisibility(View.VISIBLE);
        lastOpenPopup = filesHashPopup;
    }

    public void closeOtherPopups(){
        if(lastOpenPopup != null){
            lastOpenPopup.setVisibility(View.GONE);
            getActivity().findViewById(R.id.folderFilesLineActive).setVisibility(View.GONE);
            getActivity().findViewById(R.id.driveFileLineActive).setVisibility(View.GONE);
            getActivity().findViewById(R.id.linkFileLineActive).setVisibility(View.GONE);
            getActivity().findViewById(R.id.LineActiveHash).setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        //((MainActivity)mainView.getContext()).setBarToFiles();



        //((MainActivity)mainView.getContext()).selectedType = FragmentTypeEnum.FILES;
        //((MainActivity)mainView.getContext()).applyBottomAndTopBar();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CopyFile event) {
        File file = event.getFile();
        String type = file.getName().substring(file.getName().lastIndexOf(".")+1);
        String path = file.getAbsolutePath();
        String name = file.getName().substring(0,file.getName().indexOf("."));
        Date date = new Date(file.lastModified());
        //   FileCall2me f = new FileCall2me(type, path, name, date);
        fileAdapter.addNewFile(new FileCall2me(type, path, name, date.toString()));
    };



    @Override
    public void closeOtherPopup() {
        if(lastOpenPopup != null){
            lastOpenPopup.setVisibility(View.GONE);
            getActivity().findViewById(R.id.folderFilesLineActive).setVisibility(View.GONE);
            getActivity().findViewById(R.id.driveFileLineActive).setVisibility(View.GONE);
            getActivity().findViewById(R.id.linkFileLineActive).setVisibility(View.GONE);
            getActivity().findViewById(R.id.LineActiveHash).setVisibility(View.GONE);
        }
    }


}
