package ai.extime.Enums;

import android.graphics.drawable.Drawable;

import com.extime.R;

public enum  FileEnums {
    JPG(R.drawable.jpg_icon),
    PNG(R.drawable.png_icon),
    PDF(R.drawable.pdf_icon),
    DOC(R.drawable.doc_icon),
    XLS(R.drawable.xls_icon),
    PPT(R.drawable.ppt_icon),
    ZIP(R.drawable.zip_icon),
    GIF(R.drawable.gif_icon),
    PHP(R.drawable.php_icon),
    HTML(R.drawable.html_icon),
    JS(R.drawable.js_icon),
    MP3(R.drawable.mp3_icon),
    CSS(R.drawable.css_icon),
    BMP(R.drawable.bmp_icon),
    CAD(R.drawable.cad_icon),
    AVI(R.drawable.avi_icon),
    MOV(R.drawable.mov_icon),
    TIF(R.drawable.tif_icon),
    CDR(R.drawable.cdr_icon),
    XML(R.drawable.xml_icon),
    TXT(R.drawable.txt_icon),
    SQL(R.drawable.sql_icon),
    PSD(R.drawable.psd_icon),
    AI(R.drawable.ai_icon),
    SVG(R.drawable.svg_icon),
    EPS(R.drawable.eps_icon),
    ISO(R.drawable.iso_icon),
    RAW(R.drawable.raw_icon),
    MP4(R.drawable.mov_icon);


    private int id;
    FileEnums(int  id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
