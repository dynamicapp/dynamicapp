package jp.zyyx.dynamicapp.plugins;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.provider.ContactsContract;
import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.util.Base64;

public class AddressBook extends DynamicAppPlugin {
	private static final String TAG = "AddressBook";

	private static AddressBook instance = null;

	private static final String METHOD_SHOW = "show";
	//private static final String JSON_KEY_CONTACT_INFO = "contactInfo";
	private static final String JSON_KEY_CONTACT_ID = "id";
	
	private static final String JSON_KEY_CONTACT_NAME = "name";
	private static final String JSON_KEY_CONTACT_DISPLAY_NAME = "displayName";
	private static final String JSON_KEY_CONTACT_NICKNAME = "nickName";
	private static final String JSON_KEY_CONTACT_FORMATTED_NAME = "formatted";
	private static final String JSON_KEY_CONTACT_FAMILY_NAME = "familyName";
	private static final String JSON_KEY_CONTACT_GIVEN_NAME = "givenName";
	private static final String JSON_KEY_CONTACT_MIDDLE_NAME = "middleName";
	private static final String JSON_KEY_CONTACT_PREFIX = "prefix";
	private static final String JSON_KEY_CONTACT_SUFFIX = "suffix";

	private static final String JSON_KEY_CONTACT_PHONE_NUMBER = "phoneNumbers";
	private static final String JSON_KEY_PHONE_NUMBER_TYPE = "type";
	private static final String JSON_KEY_PHONE_NUMBER = "number";
	
	private static final String JSON_KEY_CONTACT_EMAIL = "emails";
	private static final String JSON_KEY_EMAIL_TYPE = "type";
	private static final String JSON_KEY_EMAIL = "address";
	
	
	private static final String JSON_KEY_CONTACT_ADDRESS = "addresses";
	private static final String JSON_KEY_CONTACT_FORMATTED_ADDRESS = "formatted";
	private static final String JSON_KEY_ADDRESS_TYPE = "type";
	private static final String JSON_KEY_ADDRESS_STREET = "streetAddress";
	private static final String JSON_KEY_ADDRESS_LOCALITY = "locality";
	private static final String JSON_KEY_ADDRESS_REGION = "region";
	private static final String JSON_KEY_ADDRESS_POSTCODE = "postalCode";
	private static final String JSON_KEY_ADDRESS_COUNTRY = "country";
	
	private static final String JSON_KEY_CONTACT_IM = "ims";
	private static final String JSON_KEY_IM_TYPE = "type";
	private static final String JSON_KEY_IM = "address";
	
	private static final String JSON_KEY_CONTACT_ORG = "organizations";
	private static final String JSON_KEY_ORG_TYPE = "type";
	private static final String JSON_KEY_ORG_DEPT = "department";
	private static final String JSON_KEY_ORG_TITLE = "title";
	private static final String JSON_KEY_ORG = "name";
	
	private static final String JSON_KEY_CONTACT_BDAY = "birthday";
	private static final String JSON_KEY_CONTACT_NOTE = "note";
	private static final String JSON_KEY_CONTACT_PHOTO = "photos";
	private static final String JSON_KEY_CONTACT_CATEGORY = "categories";
	private static final String JSON_KEY_CATEGORY = "category";
	
	private static final String JSON_KEY_CONTACT_URL = "urls";
	private static final String JSON_KEY_URL = "url";
	
	private static final String DEFAULT_STRING_TYPE = "HOME";
	private static final int DEFAULT_INT_TYPE = 1;

	private ContentResolver cr = dynamicApp.getContentResolver();

	private boolean isSelect = false;
	private static final int ERROR_NO_DATA = 1;
	private AddressBook() {
	}

	public static synchronized AddressBook getInstance() {
		if (instance == null) {
			instance = new AddressBook();
		}

		return instance;
	}

	@Override
	public void execute() {
		DebugLog.i(TAG, "method " + methodName + " is called.");
		DebugLog.i(TAG, "parameters are: " + params);

		isSelect = param.get("isSelect", false);
		
		dynamicApp.callJsEvent(PROCESSING_FALSE);
		if (methodName.equalsIgnoreCase(METHOD_SHOW)){
			this.showOrSelectContacts();	
		}

	}

	private void showOrSelectContacts() {
		boolean ok = true;
		
		try {
			int requestCode = (isSelect)? ACTIVITY_REQUEST_CD_PICK_CONTACT : ACTIVITY_REQUEST_CD_SHOW_CONTACTS;			
			Intent intent = (isSelect)?  getSelectIntent() : getShowIntent();
			dynamicApp.startActivityForResult(intent, requestCode);
		} catch(Exception e) {
			e.printStackTrace();
			ok = false;
		} finally {
			if(!ok) {
				DebugLog.i(TAG, "result: = " + -1);
				AddressBook.onError("" + ERROR_NO_DATA, callbackId);
			}	
		}
	}
	
	private Intent getShowIntent() {
		Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
		intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		intent.putExtra(ContactsContract.Intents.Insert.EMAIL, "");
		return intent;
	}
	
	private Intent getSelectIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		return intent;
	}

	private String getStringData(Cursor cur, String columnName, String value) {
		int columnIndex = cur.getColumnIndex(columnName);
		String data = (columnIndex > -1) ? ((cur.getString(columnIndex)!= null) ? cur.getString(columnIndex) : value) : value;
		return data;
	}

	private int getIntData(Cursor cur, String columnName, int value) {
		int columnIndex = cur.getColumnIndex(columnName);
		int data = (columnIndex > -1) ? cur.getInt(columnIndex) : value;
		return data;
	}

	private void processMethodShowResult(final int resultCode) {
		//final int OK = Activity.RESULT_OK;
		AddressBook.onSuccess(new JSONObject(), callbackId, false);
	}

	private JSONObject populateContactInfo(String id, String displayName,
			String nickName, JSONObject name, JSONArray number) {
		try {
			message = new JSONObject();
			//JSONObject contactInfo = new JSONObject();
			message.put(JSON_KEY_CONTACT_ID, id);
			message.put(JSON_KEY_CONTACT_DISPLAY_NAME, displayName);
			message.put(JSON_KEY_CONTACT_NAME, name);
			message.put(JSON_KEY_CONTACT_NICKNAME, nickName);
			if (number != null)
				message.put(JSON_KEY_CONTACT_PHONE_NUMBER, number);
			if (id.length() > 0) {
				message.put(JSON_KEY_CONTACT_EMAIL, getEmailAddress(id));
				message.put(JSON_KEY_CONTACT_ADDRESS, getAddress(id));
				message.put(JSON_KEY_CONTACT_IM, getIM(id));
				message.put(JSON_KEY_CONTACT_ORG, getOrganizations(id));
				message
						.put(JSON_KEY_CONTACT_BDAY, getContactBirthday(id));
				message.put(JSON_KEY_CONTACT_NOTE, getNote(id));
				message.put(JSON_KEY_CONTACT_PHOTO, getContactPhotoBase64(cr, id));
				message.put(JSON_KEY_CONTACT_CATEGORY, getCategories(id));
				message.put(JSON_KEY_CONTACT_URL, getURLs(id));
			}
			//message.put(JSON_KEY_CONTACT_INFO, contactInfo);
		} catch (JSONException e) {
			AddressBook.onError(-1 + "", callbackId);
		}

		return message;
	}

	protected String getContactPhotoUri(String id) {
	    try {
	        Cursor cur = cr.query(
	                ContactsContract.Data.CONTENT_URI,
	                null,
	                ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
	                        + ContactsContract.Data.MIMETYPE + "='"
	                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
	                null);
	        if (cur != null) {
	            if (!cur.moveToFirst()) {
	                return "";
	            }
	        } else {
	            return ""; 
	        }
	    } catch (Exception e) {
	        return "";
	    }
	    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
	            .parseLong(id));
	    String imageUri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY).toString();
	    
		return imageUri;
	}
	
	protected String getContactPhotoBase64(ContentResolver cr, String  id) {
		ByteArrayOutputStream bos = null;
		Bitmap bMap = null;
		
	    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
	            .parseLong(id));
	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
	    if (input == null) {
	        return "";
	    }
        
        bMap = DynamicAppUtils.decodeBitmap(cr, uri, 100, 100);
        bos = new ByteArrayOutputStream();
	    bMap.compress(CompressFormat.JPEG, 100, bos);
		byte[] _bArray = bos.toByteArray();
		String contents = Base64.encodeToString(_bArray, Base64.DEFAULT);
		contents = "data:image/jpeg;base64," + contents;
		
		if (bMap != null) {
        	bMap.recycle();
        	bMap = null;
        }
        
        if (bos != null) {
        	bos = null;
        }
        System.gc();
	    return contents;
	}

	
	protected String getGroupNameFor(int groupId){
	    Uri uri = ContactsContract.Groups.CONTENT_URI;
	    String where = String.format("%s = ?", ContactsContract.Groups._ID);
	    String[] whereParams = new String[]{Integer.toString(groupId)};
	    String[] selectColumns = {ContactsContract.Groups.TITLE};
	    Cursor c = cr.query(
	            uri, 
	            selectColumns,
	            where, 
	            whereParams, 
	            null);

	    try{
	        if (c.moveToFirst()){
	            return c.getString(0);  
	        }
	        return null;
	    }finally{
	        c.close();
	    }
	}
	
	protected JSONArray getCategories(String id) {
		JSONArray cats = new JSONArray();
		JSONObject cat = null;
		
		Uri uri = ContactsContract.Data.CONTENT_URI;
	    String where = String.format(
	            "%s = ? AND %s = ?",
	            ContactsContract.Data.MIMETYPE,
	            ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID);

	    String[] whereParams = new String[] {
	    		ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE,
	               id,
	    };

	    String[] selectColumns = new String[]{
	    		ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
	    };


	    Cursor groupIdCursor = cr.query(
	            uri, 
	            selectColumns, 
	            where, 
	            whereParams, 
	            null);
	    
        while (groupIdCursor.moveToNext()) {
        	int grouID = groupIdCursor.getInt(0);
        	String category = getGroupNameFor(grouID);
        	try {
				cat = new JSONObject();
				cat.put(JSON_KEY_CATEGORY, category);
				cats.put(cat);
			} catch (JSONException e) {
				e.printStackTrace();
			}
            
        }

		return cats;
	}
	
	protected String getTypeNameFor(int typeID){
	    String type = "";
		switch(typeID) {
			case Website.TYPE_HOMEPAGE :
				type = "HOMEPAGE";
				break;
			case Website.TYPE_BLOG :
				type = "BLOG";
				break;
			case Website.TYPE_PROFILE :
				type = "PROFILE";
				break;
			case Website.TYPE_HOME :
				type = "HOME";
				break;
			case Website.TYPE_WORK :
				type = "WORK";
				break;
			case Website.TYPE_FTP :
				type = "FTP";
				break;
			default:
				type = "OTHER";
				break;
	    }
		
		return type;
	}
	
	protected JSONArray getURLs(String id) {
		JSONArray urls = new JSONArray();
		JSONObject url = null;
		String urlWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
				+ ContactsContract.Data.MIMETYPE + " = ?";
		String[] urlWhereParams = new String[] { id,
				ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE };
		Cursor urlCur = cr.query(ContactsContract.Data.CONTENT_URI, null,
				urlWhere, urlWhereParams, null);
		while (urlCur.moveToNext()) {
			String urlName = this.getStringData(urlCur,
					ContactsContract.CommonDataKinds.Website.URL, "");
			String type = this.getStringData(urlCur,
					ContactsContract.CommonDataKinds.Website.TYPE, "");
			type = (type == null) ? "7" : type;
			String typeName = "OTHER";
			try {
				typeName = getTypeNameFor(Integer.valueOf(type));
			} catch (Exception e){
				
			}
			try {
				url = new JSONObject();
				url.put(JSON_KEY_URL, urlName);
				url.put("type", typeName);
				urls.put(url);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		urlCur.close();

		return urls;
	}
	
	protected JSONObject getName(String id) {
		JSONObject name = new JSONObject();
		String whereName = ContactsContract.Data.CONTACT_ID + " = ? AND "
				+ ContactsContract.Data.MIMETYPE + " = ?";
	    String[] whereNameParams = new String[] { id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
	    Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, 
	    		ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
	    while (nameCur.moveToNext()) {
	    	String familyName = this
					.getStringData(
							nameCur,
							ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
							"");
			String givenName = this.getStringData(nameCur,
					ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "");
			String middleName = this
					.getStringData(
							nameCur,
							ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
							"");
			String prefix = this.getStringData(nameCur,
					ContactsContract.CommonDataKinds.StructuredName.PREFIX, "");
			String suffix = this.getStringData(nameCur,
					ContactsContract.CommonDataKinds.StructuredName.SUFFIX, "");
			
			String formatted = prefix + " " + givenName + " " + middleName + " " + familyName + " " + suffix;
			
			DebugLog.i(TAG, "given name length:" + givenName.length());

			try {
				name.put(JSON_KEY_CONTACT_FORMATTED_NAME, formatted);
				name.put(JSON_KEY_CONTACT_FAMILY_NAME, familyName);
				name.put(JSON_KEY_CONTACT_GIVEN_NAME, givenName);
				name.put(JSON_KEY_CONTACT_MIDDLE_NAME, middleName);
				name.put(JSON_KEY_CONTACT_PREFIX, prefix);
				name.put(JSON_KEY_CONTACT_SUFFIX, suffix);
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    }
	    nameCur.close();

		
		return name;
	}
	
	private String getNickName(String id){
		Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, new String[] {ContactsContract.Data.DATA1}, ContactsContract.Data.CONTACT_ID + " = " + id, null, null);
        String nickname = "";

        if (cur.moveToFirst()) {
            nickname = this.getStringData(cur, ContactsContract.Data.DATA1, "");
        }
        return nickname;
	}
	
	private String getContactBirthday(String id) {
		String bDay = "";
		Uri uri = ContactsContract.Data.CONTENT_URI;

		String whereName = ContactsContract.Data.CONTACT_ID + " = ? AND "
				+ ContactsContract.Data.MIMETYPE + " = ? AND "
				+ ContactsContract.CommonDataKinds.Event.TYPE + "="
				+ ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
	    String[] whereNameParams = new String[] { id, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
	    Cursor curBday = cr.query(uri, null, whereName, whereNameParams,
				null);
		
		while (curBday.moveToNext()) {
			bDay = this.getStringData(curBday,
					ContactsContract.CommonDataKinds.Event.START_DATE, "");
		}

		return bDay;
	}
	
	protected JSONArray getPhoneNumber(Cursor cur, String id) {
		JSONArray phoneNumbers = new JSONArray();
		JSONObject phoneNumber = null;

		if (Integer.parseInt(cur.getString(cur
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
			Cursor pCur = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[] { id }, null);

			while (pCur.moveToNext()) {
				String number = this.getStringData(pCur,
						ContactsContract.CommonDataKinds.Phone.DATA, "");

				int type = this.getIntData(pCur,
						ContactsContract.CommonDataKinds.Phone.TYPE, DEFAULT_INT_TYPE);
				String customLabel = this.getStringData(pCur,
						ContactsContract.CommonDataKinds.Phone.LABEL, DEFAULT_STRING_TYPE);
				CharSequence numberType = ContactsContract.CommonDataKinds.Phone
						.getTypeLabel(dynamicApp.getResources(), type,
								customLabel);

				try {
					phoneNumber = new JSONObject();
					phoneNumber.put(JSON_KEY_PHONE_NUMBER_TYPE, numberType);
					phoneNumber.put(JSON_KEY_PHONE_NUMBER, number);
					phoneNumbers.put(phoneNumber);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			pCur.close();
		}

		return phoneNumbers;
	}
	
	protected JSONArray getEmailAddress(String id) {
		JSONArray emails = new JSONArray();
		JSONObject email = null;
		Cursor emailCur = cr.query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
				new String[] { id }, null);
		while (emailCur.moveToNext()) {
			String emailAdd = this.getStringData(emailCur,
					ContactsContract.CommonDataKinds.Email.DATA, "");
			int type = this.getIntData(emailCur,
					ContactsContract.CommonDataKinds.Email.TYPE, DEFAULT_INT_TYPE);
			String customLabel = this.getStringData(emailCur,
					ContactsContract.CommonDataKinds.Email.LABEL, DEFAULT_STRING_TYPE);
			CharSequence emailType = ContactsContract.CommonDataKinds.Email
					.getTypeLabel(dynamicApp.getResources(), type, customLabel);

			try {
				email = new JSONObject();
				email.put(JSON_KEY_EMAIL_TYPE, emailType);
				email.put(JSON_KEY_EMAIL, emailAdd);
				emails.put(email);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		emailCur.close();

		return emails;
	}
	
	protected String getNote(String id) {
		String note = null;
		String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
				+ ContactsContract.Data.MIMETYPE + " = ?";
		String[] noteWhereParams = new String[] { id,
				ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE };
		Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null,
				noteWhere, noteWhereParams, null);
		if (noteCur.moveToFirst()) {
			note = this.getStringData(noteCur,
					ContactsContract.CommonDataKinds.Note.NOTE, "");
		}
		noteCur.close();

		return note;
	}

	protected JSONArray getAddress(String id) {
		JSONArray addresses = new JSONArray();
		JSONObject address = null;
		String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
				+ ContactsContract.Data.MIMETYPE + " = ?";
		String[] addrWhereParams = new String[] {
				id,
				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };
		Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI, null,
				addrWhere, addrWhereParams, null);
		while (addrCur.moveToNext()) {
			int type = this.getIntData(addrCur,
					ContactsContract.CommonDataKinds.StructuredPostal.TYPE, 1);
			String customLabel = this.getStringData(addrCur,
					ContactsContract.CommonDataKinds.StructuredPostal.LABEL,
					"HOME");
			CharSequence addtype = ContactsContract.CommonDataKinds.StructuredPostal
					.getTypeLabel(dynamicApp.getResources(), type, customLabel);

			// String poBox = addrCur.getString(
			// addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
			String street = this.getStringData(addrCur,
					ContactsContract.CommonDataKinds.StructuredPostal.STREET,
					"");

			String city = this.getStringData(addrCur,
					ContactsContract.CommonDataKinds.StructuredPostal.CITY, "");
			String region = this.getStringData(addrCur,
					ContactsContract.CommonDataKinds.StructuredPostal.REGION,
					"");
			String postalCode = this.getStringData(addrCur,
					ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
					"");
			String country = this.getStringData(addrCur,
					ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
					"");
			
			String formatted = this.getStringData(addrCur,
					ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
					"");

			try {
				address = new JSONObject();
				address.put(JSON_KEY_CONTACT_FORMATTED_ADDRESS, formatted);
				address.put(JSON_KEY_ADDRESS_TYPE, addtype);
				address.put(JSON_KEY_ADDRESS_STREET, street);
				address.put(JSON_KEY_ADDRESS_LOCALITY, city);
				address.put(JSON_KEY_ADDRESS_REGION, region);
				address.put(JSON_KEY_ADDRESS_POSTCODE, postalCode);
				address.put(JSON_KEY_ADDRESS_COUNTRY, country);
				// address.put("poBox", poBox);
				addresses.put(address);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		addrCur.close();

		return addresses;
	}

	protected JSONArray getIM(String id) {
		JSONArray ims = new JSONArray();
		JSONObject im = null;
		String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
				+ ContactsContract.Data.MIMETYPE + " = ?";
		String[] imWhereParams = new String[] { id,
				ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE };
		Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI, null,
				imWhere, imWhereParams, null);
		while (imCur.moveToNext()) {
			int type = this.getIntData(imCur,
					ContactsContract.CommonDataKinds.Im.TYPE, 1);
			String customLabel = this.getStringData(imCur,
					ContactsContract.CommonDataKinds.Im.LABEL, "HOME");
			CharSequence imType = ContactsContract.CommonDataKinds.Im
					.getTypeLabel(dynamicApp.getResources(), type, customLabel);
			String imName = this.getStringData(imCur,
					ContactsContract.CommonDataKinds.Im.DATA, "");

			try {
				im = new JSONObject();
				im.put(JSON_KEY_IM_TYPE, imType);
				im.put(JSON_KEY_IM, imName);
				ims.put(im);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		imCur.close();

		return ims;
	}

	protected JSONArray getOrganizations(String id) {
		JSONArray orgs = new JSONArray();
		JSONObject org = null;
		String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
				+ ContactsContract.Data.MIMETYPE + " = ?";
		String[] orgWhereParams = new String[] { id,
				ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE };
		Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, null,
				orgWhere, orgWhereParams, null);
		while (orgCur.moveToNext()) {
			int type = this.getIntData(orgCur,
					ContactsContract.CommonDataKinds.Organization.TYPE, 1);
			String customLabel = this
					.getStringData(
							orgCur,
							ContactsContract.CommonDataKinds.Organization.LABEL,
							"HOME");
			CharSequence orgType = ContactsContract.CommonDataKinds.Organization
					.getTypeLabel(dynamicApp.getResources(), type, customLabel);

			String orgName = this.getStringData(orgCur,
					ContactsContract.CommonDataKinds.Organization.COMPANY, "");
			String department = this.getStringData(orgCur,
					ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
					"");
			String title = this.getStringData(orgCur,
					ContactsContract.CommonDataKinds.Organization.TITLE, "");

			try {
				org = new JSONObject();
				org.put(JSON_KEY_ORG_TYPE, orgType);
				org.put(JSON_KEY_ORG, orgName);
				org.put(JSON_KEY_ORG_DEPT, department);
				org.put(JSON_KEY_ORG_TITLE, title);
				orgs.put(org);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		orgCur.close();

		return orgs;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		final int OK = Activity.RESULT_OK;

		if (requestCode == ACTIVITY_REQUEST_CD_SHOW_CONTACTS) {
			this.processMethodShowResult(resultCode);
		} else if (requestCode == ACTIVITY_REQUEST_CD_PICK_CONTACT) {
			if (resultCode == OK) {
				Uri contactData = intent.getData();
				Cursor cur = cr.query(contactData, null, null, null, null);
				String id = "";
				String displayName = "";
				String nickName = "";
				JSONObject name = null;
				JSONArray number = null;

				if (cur.getCount() > 0) {
					while (cur.moveToNext()) {
						id = this.getStringData(cur,
								ContactsContract.Contacts._ID, "");
						displayName = this.getStringData(cur,
								ContactsContract.Contacts.DISPLAY_NAME, "");
						if (id.length() > 0)
							number = getPhoneNumber(cur, id);
					}
				}
				if (id.length() > 0) {
					name = getName(id);
					nickName = getNickName(id);
				}
				JSONObject retObject = populateContactInfo(id, displayName,
						nickName, name, number);
				AddressBook.onSuccess(retObject, callbackId, false);
			} else {
				DebugLog.i(TAG, "result: = " + -1);
				AddressBook.onError("" + ERROR_NO_DATA, callbackId);
			}
		}
	}

	@Override
	public void onBackKeyDown() {
		DynamicAppUtils.currentCommandRef = null;
		instance = null;
	}
}

