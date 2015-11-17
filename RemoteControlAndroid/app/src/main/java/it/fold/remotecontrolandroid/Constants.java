package it.fold.remotecontrolandroid;

public class Constants {
    public static       int CUR_IMG_WIDTH        = 1184; // defaults only...
    public static       int CUR_IMG_HEIGHT       =  768;
    public static       int REAL_IMG_WIDTH       = 1184;
    public static       int REAL_IMG_HEIGHT      =  768;
    public static       String IP_ADDRESS        = "10.0.2.2";
    public static       String KEY               = "";

    public static final int PORT                 = 1230;



    // general constants
    public static final int  TILE_SIZE           = 16;
    public static final char MAGIC               = 88;
    public static final char VERSION             =  3;
    public static final int  KEY_LENGTH          =  5;

    // client events
    public static final int CL_FIRST_MSG_SIZE    = 12;
    public static final int CL_MSG_SIZE          =  7;

    public static final char CLEV_REFRESH        =  1;
    public static final char CLEV_TERMINATE      =  2;
    public static final char CLEV_SCROLL_DOWN    =  3;
    public static final char CLEV_SCROLL_UP      =  4;

    public static final char CLEV_MODKEY_DOWN    =  5;
    public static final char CLEV_MODKEY_UP      =  6;
    public static final char CLEV_CHAR           =  7;

    public static final char CLEV_MOUSE_DOWN     =  8;
    public static final char CLEV_MOUSE_UP       =  9;
    public static final char CLEV_MOUSE_MOVE     = 10;

    public static final char CLEV_AUX_PTR_DOWN   = 11;
    public static final char CLEV_AUX_PTR_UP     = 12;
    public static final char CLEV_AUX_PTR_MOVE   = 13;

    // server events
    public static final int SE_MSG_HDR           =  4;

    public static final char SEEV_FLUSH          =  1;
    public static final char SEEV_TERMINATE      =  2;

    public static final char SEEV_TILE           =  3;
    public static final char SEEV_SOLID_TILE     =  4;
    public static final char SEEV_RLE24_TILE     =  5;
    public static final char SEEV_RLE16_TILE     =  6;
    public static final char SEEV_RLE8_TILE      =  7;
}
