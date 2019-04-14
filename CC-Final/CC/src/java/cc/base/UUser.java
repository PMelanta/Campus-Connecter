package cc.base;


import org.apache.log4j.Logger;

public class UUser {

    static Logger logger = Logger.getLogger(UUser.class);

    private int user_rid = 0;
    private String user_name;
    private String user_title="";
    private String user_password = "";
    private int passwordStatus = 0;

    public UUser(int uRID, String uName, String uTitle, String uPassword) {

	user_rid = uRID;
	user_name = uName;
	user_title = uTitle;
	user_password = uPassword;
    }
    
    public UUser(int uRID, String uName, String uTitle, String uPassword,int passStatus) {

	user_rid = uRID;
	user_name = uName;
	user_title = uTitle;
	user_password = uPassword;
        passwordStatus = passStatus;
    }

    public int getUserRID() {
	return user_rid;
    }

    public String getUserPassword() {
	return user_password;
    }
    
    public int getPasswordStatus() {
	return passwordStatus;
    }


    public String getUserName() {
	return user_name;
    }

    public String getUserTitle() {
	return user_title;
    }


}
