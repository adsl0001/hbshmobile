package com.hse.mobile.oa.business;

import org.json.JSONObject;

/*
 * ����
 * ��ʽ�汾�õ�41������
 * ���԰汾�õ�44������
 */
public class Constant {
	public static final int YES = 1;
	public static final int NO = 0;
	//������ʧ����ʾ
	public static String LOGIN_PORTAL_RESULT="��¼����,������������!";
	public static void setLOGIN_PORTAL_RESULT(String lOGIN_PORTAL_RESULT) {
		LOGIN_PORTAL_RESULT = lOGIN_PORTAL_RESULT;
	}
	//���յ��İ汾��
	public static String APP_VERSION_RECIVE="1.0.2";
	//���ص�������sd����·��
	public static String SDCARD_PATH="/sdcard/";
	public static void setAPP_VERSION_RECIVE(String aPP_VERSION_RECIVE) {
		APP_VERSION_RECIVE = aPP_VERSION_RECIVE;
	}

	public static final String  TICKET_ILLEGAL = "illegal";
	
	public static final String  TICKET_POSITIVE = "positive";
	
	public static final String  NEWS_TYPE_PHOTONEWS = "PhotoNews";
	//�л�������ַ
//	public static final String  URL_ROOT = "http://114.251.186.41:81";  
//	public static final String  URL_ROOTB = "http://114.251.186.41:81";
	
	public static final String  URL_ROOT = "http://114.251.186.42:81";  
	public static final String  URL_ROOTB = "http://114.251.186.42:81";
	
	//���԰���apk�ĵ�ַ ����ʽ����apk�ĵ�ַ
//	public static final String URL_APK_DOWNLOAD=URL_ROOT+"/publish/hse_mobile_oa_test.apk";
	public static final String URL_APK_DOWNLOAD=URL_ROOT+"/publish/hse_mobile_oa.apk";
	
	//�µ�¼ʱ�İ�����״̬
	public static final String URL_AppLoginCheck=URL_ROOT+"/web-s/servlet/AppLoginCheck";
	
	//��֤�������
	public static final String URL_GetBindStatus=URL_ROOT+"/web-s/GetBindStatus";
	//��֤������󶨵ķ��ؽ��
	public static String  GetBindStatus_2="";
	public static String GetBindStatus_3="";
	public static void setGetBindStatus_2(String getBindStatus_2) {
		GetBindStatus_2 = getBindStatus_2;
	}
	public static void setGetBindStatus_3(String getBindStatus_3) {
		GetBindStatus_3 = getBindStatus_3;
	}
	//�󶨻�����
	public static final String URL_OnGetBindStatus=URL_ROOT+"/web-s/ClientBindServlet";
	
	public static final String  URL_NEWSLIST = URL_ROOTB + "/web-s/cms/sumlist/";
	
	public static final String  URL_NEWS_DETAIL = URL_ROOTB + "/web-s/cms/content/";
	
	public static final String  URL_LOGIN = URL_ROOT + "/r1/login";
	
	public static String updatepoint="";
	public static String getUpdatepoint() {
		return updatepoint;
	}
	public static void setUpdatepoint(String updatepoint) {
		Constant.updatepoint = updatepoint;
	}
	//��������¼��֤
	public static final String URL_LOGIN_PORTAL=URL_ROOT+"/web-s/LoginLogger";
	//���µ�¼״̬�ĵ�ַ
	public static final String URL_UpdateLoginStatus=URL_ROOT+"/web-s/UpdateLoginStatus";
	public static final String URL_UseServlet=URL_ROOT+"/web-s/AppClientUseServlet";
	//�����û��� ��mac��imei
	//�����û��� ��mac��imei
		public static  String User_name="";
		public static  String User_mac="";
		public static  String User_imei="";
		public static String getUser_name() {
			return User_name;
		}
		public static String getUser_mac() {
			return User_mac;
		}
		public static String getUser_imei() {
			return User_imei;
		}
		public static void setUser_name(String user_name) {
			User_name = user_name;
		}
		
		public static void setUser_mac(String user_mac) {
			User_mac = user_mac;
		}
		
		public static void setUser_imei(String user_imei) {
			User_imei = user_imei;
		}
	public static JSONObject readhistroy=null;
	public static JSONObject getReadhistroy() {
		return readhistroy;
	}
	public static void setReadhistroy(JSONObject readhistroy) {
		Constant.readhistroy = readhistroy;
	}
	/*
	 * ��˾Ҫ����newsnumber
	 * ��˾֪ͨ��noticenumber
	 */
	public static final String URL_SETBADGENUMBER=URL_ROOT+"/web-s/JpushMsgService"; 
	public static final String URL_HanldeMyTask=URL_ROOT+"/web-s/servlet/myTaskCheck";
	public static String newsnumber;
	public static String noticenumber;
	public static String getNewsnumber() {
		return newsnumber;
	}
	public static void setNewsnumber(String newsnumber) {
		Constant.newsnumber = newsnumber;
	}
	public static String getNoticenumber() {
		return noticenumber;
	}
	public static void setNoticenumber(String noticenumber) {
		Constant.noticenumber = noticenumber;
	}
	public static String MytaskNumber;
	
	public static String getMytaskNumber() {
		return MytaskNumber;
	}
	public static void setMytaskNumber(String mytaskNumber) {
		MytaskNumber = mytaskNumber;
	}
	public static Boolean backfromMyTask;
	
	public static Boolean getBackfromMyTask() {
		return backfromMyTask;
	}
	public static void setBackfromMyTask(Boolean backfromMyTask) {
		Constant.backfromMyTask = backfromMyTask;
	}
	//�豸��Ϣ
	public static final String URL_Equipment=URL_ROOT+"/web-s/servlet/getOfficeDetail"
;	//�����޸�
	public static final String URL_PASSWORD=URL_ROOT+"/dataCenter/welcome";
	//Ա������
	public static final String URL_PERSON=URL_ROOT+"/hr/person_contact";
	//����ҳ��
	public static final String URL_HELP=URL_ROOT+"/help/help.html";
	//�汾����ҳ��
	public static final String URL_VERSION_DESCRIPTION=URL_ROOT+"/help/version.html";
	
	public static final String  URL_PHOTONEWS = URL_ROOTB + "/web-s/cms/sumlist/PhotoNews";

	public static final String  URL_PORTAL_DATA = URL_ROOT + "/web-s/PortalIcon";
	//�ղص�ͼ��
	public static final String URL_PRRTAL_COLLECTION=URL_ROOT+"/web-s/getMyFavorite";
	//��������ĵ�ַ
	public static final String URL_SUGGESTION=URL_ROOT+"/web-s/UserAdvice";
	//������Ϣ�б��ַ
	public static final String URL_MyMessage=URL_ROOT+"/web-s/MyMessageServlet";
	//������Ϣ��ϸ��ַ
	public static final String URl_MyMessageDetail=URL_ROOT+"/web-s/GetMyMessageById";
	public static final String  URL_PORTAL_PROCESS = URL_ROOTB + "/web-s/processList.jsp";
	
	public static final String  URL_EASYTICKETS_LIST = URL_ROOTB + "/petrochemical/easytickets/tickets/";

	public static final String  URL_TICKET_DETAIL = URL_ROOTB + "/petrochemical/easytickets/tickets/detail/";
	
	public static final String  URL_TICKET_SUBMIT = URL_EASYTICKETS_LIST;
	
	public static final String  URL_TICKET_DELETE = URL_EASYTICKETS_LIST + "delete/";
	
	public static final String  URL_DEPART_LIST = URL_ROOTB + "/petrochemical/easytickets/unittree/";
	
	public static final String  URL_IMAGE = URL_ROOTB + "/petrochemical/easytickets/images/";
	
	public static final String  URL_IMAGE_DELETE = URL_ROOTB + "/petrochemical/easytickets/images/delete/";
	
	public static final String  URL_SAFETYHAZARD = URL_ROOT + "/SecurityManager/safehazard/input";

	public static final String  URL_TODOTASK = URL_ROOT + "/ISBPM/workFlowStepFromView/initStepPage?";

	public static final String  URL_TASKLIST = URL_ROOT + "/ISBPM/myTask";
	
//	public static final String  URL_REPORT = URL_ROOT + "/www/report/reportweb.html";
	//��report�ֿ�
	public static final String  URL_REPORT = URL_ROOT + "/web-s/PortalIcon?moduletype=report"; 
	
	public static final String  URL_NEXTPERSON = URL_ROOT + "/petrochemical/easytickets/tickets/nextPersons";
	
	public static final String  URL_AUTHOR = URL_ROOT + "/petrochemical/easytickets/tickets/authority/";
	
}
