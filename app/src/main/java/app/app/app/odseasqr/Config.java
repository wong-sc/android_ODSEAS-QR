package app.app.app.odseasqr;

public class Config {

    /*every static variable will be stored here to ease the modification*/
    public static final String BASE_URL = "http://192.168.137.1/";
    public static final String GET_SUBJECT_DATA = "ODSEAS-QR/student/getSubjectData.php";
    public static final String GET_DETAILS_DATA = "ODSEAS-QR/student/getDetailsData.php";
    public static final String GET_ATTENDED_DATA = "ODSEAS-QR/student/getAttendedData.php";
    public static final String GET_ANS_BOOKLETS = "ODSEAS-QR/student/getAnswerBooklet.php";
    public static final String CHECK_ALREADY_SCAN = "ODSEAS-QR/student/checkAlreadyScan.php";
    public static final String GET_STUDENT_SUBJECT = "ODSEAS-QR/student/getStudentSubject.php";
    public static final String GET_STUDENT_DATA = "ODSEAS-QR/student/getStudentData.php";
    public static final String UPDATE_ATTENDANCE_DATA = "ODSEAS-QR/gcm_test/v1/updateAttendanceRecord";
    public static final String LOGIN = "ODSEAS-QR/staff/login.php";
    public static final String REGISTER = "ODSEAS-QR/staff/register.php";
    public static final String GET_ALL_DATA = "ODSEAS-QR/student/getAllData.php";
    public static final String GET_ATTENDEES_DATA = "ODSEAS-QR/student/getAttendeesData.php";
    public static final String GET_ABSENTEES_DATA = "ODSEAS-QR/student/getAbsenteesData.php";
    public static final String GET_SUBMITTED_DATA = "ODSEAS-QR/student/getSubmittedData.php";
    public static final String GET_INEXAMINATION_DATA = "ODSEAS-QR/student/getInExaminationData.php";
    public static final String GET_OFFLINE_DATA = "ODSEAS-QR/student/getOfflineData.php";
    public static final String SYNC = "ODSEAS-QR/gcm_test/v1/sync";
    public static final String STOP_COURSE = "ODSEAS-QR/staff/CloseSubject.php";

    public static final String WIFI_STATUS = "Wifi_Status";
    public static final String CHIEF = "CHIEF";
    public static final String NOT_CONNECTED = "Not connected to Internet";
    public static final int FROM_SERVER = 1;
    public static final int FROM_DB = 0;
    public static final String COURSE_ID = "course_id";
    public static final String AVAILABLE = "available";
    public static final String UNAVAILABLE = "unavailable";
}
