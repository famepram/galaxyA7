package me.fmy.galaxy_a7.helpers;

/**
 * Created by Femmy on 7/24/2016.
 */
public class DBConst {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "galaxyA7";

    public static final String TABLE_USER = "user";

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_EMAIL = "email";

    public static final String COL_IG = "instagram";

    public static final String COL_SONG = "song";

    public static final String COL_VIDEO = "video";



    public static final String SPREF_NAME = "FMY.GLXY.SPREF";

    public static final String SPREF_KEY_ID     = "ID";

    public static final String SPREF_KEY_NAME   = "NAME";

    public static final String SPREF_KEY_EMAIL  = "EMAIL";

    public static final String SPREF_KEY_IG     = "INSTAGRAM";

    public static final String SPREF_KEY_SONG   = "SONG";

    public static final String SPREF_KEY_VIDEO  = "VIDEO";


    public static final String BT_UUID = "00001101-0000-1000-8000-00805f9b34fb";



    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final int VID_DURATION = 30000;
    public static final String VID_STORAGE_PATH = "/sdcard/GalaxyExp/";
    public static final String VID_NAME_PREFIX = "VID_GLXY_EXP_";
    public static final String VID_EXT = ".mp4";
    public static final String VID_UPLOAD_URL = "http://project.mineral.co.id/galaxya7/home/upload/";
    public static final String VID_UPLOAD_SOURCE_KEY = "video_src";
    public static final String VID_UPLOAD_DIR = "uploads/video/";
    public static final String VID_USER_INSERT_URL = "http://project.mineral.co.id/galaxya7/home/insert/";

    public static final int VID_MAX_DURATION    = 600000;
    public static final int VID_MAX_SIZE        = 50000000;

    public static final String BT_DEVICE_NAME = "HC-05";

    public static final String BT_MSG_CHECK = "check\n";

    public static final String BT_MSG_SONG_1 = "ar\n";
    public static final String BT_MSG_SONG_2 = "cn\n";
    public static final String BT_MSG_SONG_3 = "kpr\n";
    public static final String BT_MSG_SONG_4 = "kim\n";
    public static final String BT_MSG_SONG_5 = "nl\n";

    public static final String BT_MSG_SONG_STOP = "stop\n";

    public static final String SONG_TITLE_1 = "FIN";
    public static final String SONG_TITLE_2 = "A";
    public static final String SONG_TITLE_3 = "Tanda Tanya";
    public static final String SONG_TITLE_4 = "Embrace";
    public static final String SONG_TITLE_5 = "Night Love";

    public static final String ARTIST_1 = "ARRIO";
    public static final String ARTIST_2 = "CNTROL";
    public static final String ARTIST_3 = "Kelompok Penerbang Roket";
    public static final String ARTIST_4 = "Kimokal";
    public static final String ARTIST_5 = "Artificial";

}
