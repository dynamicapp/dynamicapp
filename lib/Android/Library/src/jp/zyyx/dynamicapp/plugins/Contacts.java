package jp.zyyx.dynamicapp.plugins;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import jp.zyyx.dynamicapp.JSONObjectWrapper;
import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;

public class Contacts extends DynamicAppPlugin {
	private static final String TAG = "Contacts";

	private static Contacts instance = null;

	private static final String METHOD_SEARCH = "search";
	private static final String METHOD_SAVE = "save";
	private static final String METHOD_REMOVE = "remove";
	private static final String JSON_KEY_CONTACT = "contact";
	private static final String JSON_KEY_CONTACT_ID = "id";
	private static final String JSON_KEY_RAW_CONTACT_ID = "rawId";
	private static final String JSON_KEY_CONTACT_NAME = "name";
	private static final String JSON_KEY_CONTACT_FORMATTED_NAME = "formatted";
	private static final String JSON_KEY_CONTACT_DISPLAY_NAME = "displayName";
	private static final String JSON_KEY_CONTACT_NICKNAME = "nickName";
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
	private static final String JSON_KEY_CONTACT_PHOTO = "photo";
	private static final String JSON_KEY_CONTACT_CATEGORY = "categories";
	private static final String JSON_KEY_CATEGORY = "category";

	private static final String JSON_KEY_CONTACT_URL = "urls";
	private static final String JSON_KEY_URL = "url";

	private static final String JSON_KEY_HOME = "home";
	private static final String JSON_KEY_MOBILE = "mobile";
	private static final String JSON_KEY_WORK = "work";
	private static final String JSON_KEY_WORK_FAX = "workFax";
	private static final String JSON_KEY_HOME_FAX = "homeFax";
	private static final String JSON_KEY_PAGER = "pager";
	private static final String JSON_KEY_OTHER = "other";
	private static final String JSON_KEY_CUSTOM = "custom";
	private static final String JSON_KEY_CALLBACK = "callback";
	private static final String JSON_KEY_ASSISTANT = "assistant";
	private static final String JSON_KEY_CAR = "car";
	private static final String JSON_KEY_COMPANY_MAIN = "companyMain";
	private static final String JSON_KEY_ISDN = "isdn";
	private static final String JSON_KEY_MAIN = "main";
	private static final String JSON_KEY_MMS = "mms";
	private static final String JSON_KEY_OTHER_FAX = "otherFax";
	private static final String JSON_KEY_RADIO = "radio";
	private static final String JSON_KEY_TELEX = "telex";
	private static final String JSON_KEY_TTY_TDD = "ttyTdd";
	private static final String JSON_KEY_WORK_MOBILE = "workMobile";
	private static final String JSON_KEY_WORK_PAGER = "workPager";
	private static final String JSON_KEY_BLOG = "blog";
	private static final String JSON_KEY_FTP = "ftp";
	private static final String JSON_KEY_HOMEPAGE = "homepage";
	private static final String JSON_KEY_PROFILE = "profile";

	private static final String JSON_KEY_PROTOCOL_AIM = "aim";
	private static final String JSON_KEY_PROTOCOL_GTALK = "googleTalk";
	private static final String JSON_KEY_PROTOCOL_YAHOO = "yahoo";
	private static final String JSON_KEY_PROTOCOL_SKYPE = "skype";
	private static final String JSON_KEY_PROTOCOL_QQ = "qq";
	private static final String JSON_KEY_PROTOCOL_NET_MEETING = "netMeeting";
	private static final String JSON_KEY_PROTOCOL_MSN = "msn";
	private static final String JSON_KEY_PROTOCOL_JABBER = "jabber";
	private static final String JSON_KEY_PROTOCOL_ICQ = "icq";
	private static final String JSON_KEY_PROTOCOL_CUSTOM = "custom";

	private static final long MAX_PHOTO_SIZE = 1048576;
	private static final String EMPTY = "";
	private static final String QUERY_CONDITIONS = "conditions";
	private static final String QUERY_SORT = "sort";
	private static final int ERROR_NO_DATA = 1;

	private ArrayList<ContentProviderOperation> ops = null;
	private String id = null;

	private Contacts() {
	}

	public static synchronized Contacts getInstance() {
		if (instance == null) {
			instance = new Contacts();
		}

		return instance;
	}

	@Override
	public void execute() {
		DebugLog.i(TAG, "Method " + methodName + " is called.");
		DebugLog.i(TAG, "Parameters are " + params);

		if (methodName.equalsIgnoreCase(METHOD_SEARCH)) {
			this.search();
		} else if (methodName.equalsIgnoreCase(METHOD_SAVE)) {
			this.save();
		} else if (methodName.equalsIgnoreCase(METHOD_REMOVE)) {
			this.remove();
		}
	}

	private void applyBatch(ArrayList<ContentProviderOperation> ops)
			throws Exception {
		dynamicApp.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
				ops);
	}

	public void search() {
		boolean ok = true;
		JSONArray conditions = param.get(QUERY_CONDITIONS, new JSONArray());
		String sortOrder = param.get(QUERY_SORT, EMPTY);
		WhereClause whereClause = null;
		JSONArray contactsArr = null;
		Set<String> idSet = null;

		try {
			switch (Integer.parseInt(sortOrder)) {
			case 0:
				sortOrder = " DESC";
				break;
			case 1:
				sortOrder = " ASC";
				break;
			default:
				sortOrder = " ASC";
				break;
			}

			whereClause = populateWhereClause(conditions);

			Cursor cur = dynamicApp.getContentResolver().query(
					Data.CONTENT_URI, null, whereClause.getWhere(),
					whereClause.getWhereParams(), Data.CONTACT_ID + sortOrder);

			idSet = populateContactIdSet(cur);

			whereClause = populateWhereIdClause(idSet);

			cur = dynamicApp.getContentResolver().query(Data.CONTENT_URI, null,
					whereClause.getWhere(), whereClause.getWhereParams(),
					Data.CONTACT_ID + sortOrder);

			contactsArr = getContacts(cur);
			cur.close();
		} catch (Exception e) {
			ok = false;
			e.printStackTrace();
		} finally {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			if (ok) {
				Contacts.onSuccess(contactsArr, callbackId, false);
			} else {
				DebugLog.i(TAG, "result: = " + -1);
				Contacts.onError("" + ERROR_NO_DATA, callbackId);
			}
		}
	}

	public void save() {
		JSONObjectWrapper contact = null;
		String contactStr = param.get(JSON_KEY_CONTACT, EMPTY);
		boolean ok = true;

		try {
			contact = new JSONObjectWrapper(contactStr);
			id = contact.get(JSON_KEY_CONTACT_ID, EMPTY);

			if (null != id && !EMPTY.equals(id)) {
				this.update(id, contactStr);
			} else {
				this.insert(contactStr);
			}
		} catch (Exception e) {
			ok = false;
			e.printStackTrace();
		} finally {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			if (ok) {
				this.onSuccess();
			} else {
				this.onError();
			}
		}
	}

	public void remove() {
		ops = new ArrayList<ContentProviderOperation>();
		JSONObjectWrapper contact = null;
		String contactStr = param.get(JSON_KEY_CONTACT, EMPTY);
		boolean ok = true;

		try {
			contact = new JSONObjectWrapper(contactStr);
			id = contact.get(JSON_KEY_CONTACT_ID, EMPTY);

			if (null != id && !EMPTY.equals(id)) {
				ops.add(ContentProviderOperation
						.newDelete(RawContacts.CONTENT_URI)
						.withSelection(Data.CONTACT_ID + " = ? ",
								new String[] { id }).build());

				applyBatch(ops);
			}
		} catch (Exception e) {
			ok = false;
			e.printStackTrace();
		} finally {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			if (ok) {
				this.onSuccess();
			} else {
				this.onError();
			}
		}
	}

	private JSONArray getContacts(Cursor cur) throws Exception {
		JSONArray contactsArr = new JSONArray();
		String contactId = EMPTY;
		String prevContactId = EMPTY;
		String rawContactId = EMPTY;

		if (null != cur && cur.getCount() > 0) {
			String mimetype = EMPTY;
			JSONObject contact = null;
			JSONObject name = null;
			JSONArray phoneNumbers = null;
			JSONArray emails = null;
			JSONArray addresses = null;
			JSONArray ims = null;
			JSONArray organizations = null;
			JSONArray photos = null;
			JSONArray categories = null;
			JSONArray urls = null;

			while (cur.moveToNext()) {
				try {
					contactId = cur.getString(cur
							.getColumnIndex(Data.CONTACT_ID));
					rawContactId = cur.getString(cur
							.getColumnIndex(Data.RAW_CONTACT_ID));

					if (!prevContactId.equalsIgnoreCase(contactId)) {
						if (cur.getPosition() > 0) {
							contactsArr.put(getContact(contact, phoneNumbers,
									emails, addresses, ims, organizations,
									photos, urls, categories, prevContactId,
									rawContactId));
						}

						contact = new JSONObject();
						name = new JSONObject();
						phoneNumbers = new JSONArray();
						emails = new JSONArray();
						addresses = new JSONArray();
						ims = new JSONArray();
						organizations = new JSONArray();
						photos = new JSONArray();
						categories = new JSONArray();
						urls = new JSONArray();
						prevContactId = contactId;
					}

					mimetype = cur.getString(cur.getColumnIndex(Data.MIMETYPE));

					if (StructuredName.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						name = getName(cur);
						contact.put(JSON_KEY_CONTACT_NAME, name);
					} else if (Nickname.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						contact.put(JSON_KEY_CONTACT_NICKNAME, cur
								.getString(cur.getColumnIndex(Nickname.NAME)));
					} else if (Phone.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						phoneNumbers.put(getPhoneNumber(cur));
					} else if (Email.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						emails.put(getEmail(cur));
					} else if (StructuredPostal.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						addresses.put(getAddress(cur));
					} else if (Im.CONTENT_ITEM_TYPE.equalsIgnoreCase(mimetype)) {
						ims.put(getIm(cur));
					} else if (Organization.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						organizations.put(getOrganization(cur));
					} else if (Photo.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						photos.put(getPhoto(cur));
					} else if (Website.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						urls.put(getUrl(cur));
					} else if (GroupMembership.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						categories.put(getCategory(cur));
					} else if (Note.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						contact.put(JSON_KEY_CONTACT_NOTE,
								cur.getString(cur.getColumnIndex(Note.NOTE)));
					} else if (Event.CONTENT_ITEM_TYPE
							.equalsIgnoreCase(mimetype)) {
						if (Event.TYPE_BIRTHDAY == cur.getInt(cur
								.getColumnIndex(Event.TYPE))) {
							contact.put(JSON_KEY_CONTACT_BDAY, cur.getInt(cur
									.getColumnIndex(Event.START_DATE)));
						}
					}

					if (cur.getPosition() == cur.getCount() - 1) {
						contactsArr.put(getContact(contact, phoneNumbers,
								emails, addresses, ims, organizations, photos,
								urls, categories, prevContactId, rawContactId));
					}
				} catch (Exception e) {
					throw e;
				}
			}
		}

		return contactsArr;
	}

	private JSONObject getContact(JSONObject contact, JSONArray phoneNumbers,
			JSONArray emails, JSONArray addresses, JSONArray ims,
			JSONArray organizations, JSONArray photos, JSONArray urls,
			JSONArray categories, String contactId, String rawContactId)
			throws JSONException {

		contact.put(JSON_KEY_CONTACT_ID, contactId);
		contact.put(JSON_KEY_RAW_CONTACT_ID, rawContactId);
		contact.put(JSON_KEY_CONTACT_PHONE_NUMBER, phoneNumbers);
		contact.put(JSON_KEY_CONTACT_EMAIL, emails);
		contact.put(JSON_KEY_CONTACT_ADDRESS, addresses);
		contact.put(JSON_KEY_CONTACT_IM, ims);
		contact.put(JSON_KEY_CONTACT_ORG, organizations);
		contact.put(JSON_KEY_CONTACT_PHOTO, photos);
		contact.put(JSON_KEY_CONTACT_URL, urls);
		contact.put(JSON_KEY_CONTACT_CATEGORY, categories);

		return contact;
	}

	private JSONObject getName(Cursor cur) throws JSONException {
		JSONObject name = new JSONObject();
		StringBuffer formmattedName = new StringBuffer(EMPTY);
		String displayName = cur.getString(cur
				.getColumnIndex(StructuredName.DISPLAY_NAME));
		String prefix = cur
				.getString(cur.getColumnIndex(StructuredName.PREFIX));
		String firstName = cur.getString(cur
				.getColumnIndex(StructuredName.GIVEN_NAME));
		String middleName = cur.getString(cur
				.getColumnIndex(StructuredName.MIDDLE_NAME));
		String lastName = cur.getString(cur
				.getColumnIndex(StructuredName.FAMILY_NAME));
		String suffix = cur
				.getString(cur.getColumnIndex(StructuredName.SUFFIX));

		name.put(JSON_KEY_CONTACT_DISPLAY_NAME, displayName);
		name.put(JSON_KEY_CONTACT_FAMILY_NAME, lastName);
		name.put(JSON_KEY_CONTACT_GIVEN_NAME, firstName);
		name.put(JSON_KEY_CONTACT_MIDDLE_NAME, middleName);
		name.put(JSON_KEY_CONTACT_PREFIX, prefix);
		name.put(JSON_KEY_CONTACT_SUFFIX, suffix);

		if (null != prefix) {
			formmattedName.append(prefix + " ");
		}

		if (null != firstName) {
			formmattedName.append(firstName + " ");
		}

		if (null != middleName) {
			formmattedName.append(middleName + " ");
		}

		if (null != lastName) {
			formmattedName.append(lastName + " ");
		}

		if (null != suffix) {
			formmattedName.append(suffix);
		}
		name.put(JSON_KEY_CONTACT_FORMATTED_NAME, formmattedName.toString());

		displayName = null;
		lastName = null;
		firstName = null;
		middleName = null;
		prefix = null;
		suffix = null;

		return name;
	}

	private JSONObject getPhoneNumber(Cursor cur) throws JSONException {
		JSONObject phoneNumber = new JSONObject();
		String type = cur.getString(cur.getColumnIndex(Phone.TYPE));
		String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));

		phoneNumber.put(JSON_KEY_PHONE_NUMBER_TYPE, type);
		phoneNumber.put(JSON_KEY_PHONE_NUMBER, number);

		type = null;
		number = null;

		return phoneNumber;
	}

	private JSONObject getEmail(Cursor cur) throws JSONException {
		JSONObject email = new JSONObject();
		String type = cur.getString(cur.getColumnIndex(Email.TYPE));
		String address = cur.getString(cur.getColumnIndex(Email.ADDRESS));

		email.put(JSON_KEY_EMAIL_TYPE, type);
		email.put(JSON_KEY_EMAIL, address);

		type = null;
		address = null;

		return email;
	}

	private JSONObject getAddress(Cursor cur) throws JSONException {
		JSONObject address = new JSONObject();
		String type = cur.getString(cur.getColumnIndex(StructuredPostal.TYPE));
		String formatted = cur.getString(cur
				.getColumnIndex(StructuredPostal.FORMATTED_ADDRESS));
		String street = cur.getString(cur
				.getColumnIndex(StructuredPostal.STREET));
		String locality = cur.getString(cur
				.getColumnIndex(StructuredPostal.CITY));
		String region = cur.getString(cur
				.getColumnIndex(StructuredPostal.REGION));
		String postalCode = cur.getString(cur
				.getColumnIndex(StructuredPostal.POSTCODE));
		String country = cur.getString(cur
				.getColumnIndex(StructuredPostal.COUNTRY));

		address.put(JSON_KEY_CONTACT_FORMATTED_ADDRESS, formatted);
		address.put(JSON_KEY_ADDRESS_TYPE, type);
		address.put(JSON_KEY_ADDRESS_STREET, street);
		address.put(JSON_KEY_ADDRESS_LOCALITY, locality);
		address.put(JSON_KEY_ADDRESS_REGION, region);
		address.put(JSON_KEY_ADDRESS_POSTCODE, postalCode);
		address.put(JSON_KEY_ADDRESS_COUNTRY, country);

		formatted = null;
		type = null;
		street = null;
		locality = null;
		region = null;
		postalCode = null;
		country = null;

		return address;
	}

	private JSONObject getIm(Cursor cur) throws JSONException {
		JSONObject im = new JSONObject();
		String type = cur.getString(cur.getColumnIndex(Im.TYPE));
		String address = cur.getString(cur.getColumnIndex(Im.DATA));

		im.put(JSON_KEY_IM_TYPE, type);
		im.put(JSON_KEY_IM, address);

		type = null;
		address = null;

		return im;
	}

	private JSONObject getOrganization(Cursor cur) throws JSONException {
		JSONObject org = new JSONObject();
		String type = cur.getString(cur.getColumnIndex(Organization.TYPE));
		String department = cur.getString(cur
				.getColumnIndex(Organization.DEPARTMENT));
		String title = cur.getString(cur.getColumnIndex(Organization.TITLE));
		String name = cur.getString(cur.getColumnIndex(Organization.COMPANY));

		org.put(JSON_KEY_ORG_TYPE, type);
		org.put(JSON_KEY_ORG_DEPT, department);
		org.put(JSON_KEY_ORG_TITLE, title);
		org.put(JSON_KEY_ORG, name);

		type = null;
		department = null;
		title = null;
		name = null;

		return org;
	}

	private JSONObject getPhoto(Cursor cur) throws JSONException {
		JSONObject photo = new JSONObject();
		String id = cur.getString(cur.getColumnIndex(Photo._ID));
		Uri person = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, (Long.valueOf(id)));
		Uri photoUri = Uri.withAppendedPath(person,
				ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

		photo.put("type", "url");
		photo.put("value", photoUri.toString());

		id = null;
		person = null;
		photoUri = null;

		return photo;
	}

	private JSONObject getUrl(Cursor cur) throws JSONException {
		JSONObject url = new JSONObject();
		String site = cur.getString(cur.getColumnIndex(Website.URL));

		url.put(JSON_KEY_URL, site);

		site = null;

		return url;
	}

	private JSONObject getCategory(Cursor cur) throws JSONException {
		JSONObject category = new JSONObject();
		String cat = cur.getString(cur
				.getColumnIndex(GroupMembership.GROUP_ROW_ID));

		category.put(JSON_KEY_CATEGORY, cat);

		cat = null;

		return category;
	}

	private WhereClause populateWhereClause(JSONArray conditions)
			throws JSONException {
		List<String> whereList = new ArrayList<String>();
		List<String> whereParamsList = new ArrayList<String>();
		WhereClause whereClause = new WhereClause();
		StringBuffer where = new StringBuffer();
		String whereParams[] = null;

		if (null != conditions) {
			int index;
			int length = conditions.length();
			String conditionArr[] = null;
			String condition = EMPTY;
			String operator = EMPTY;

			for (index = 0; index < length; index++) {
				condition = conditions.getString(index);

				if (condition.contains("!=")) {
					conditionArr = condition.split("!=");
					operator = " <> ";
				} else {
					conditionArr = condition.split("=");
					operator = " = ";
				}

				if (null != condition) {
					if (condition.contains(JSON_KEY_CONTACT_DISPLAY_NAME)) {
						whereList.add(" ( " + StructuredName.DISPLAY_NAME
								+ operator + "? AND " + Data.MIMETYPE
								+ " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(StructuredName.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_GIVEN_NAME)) {
						whereList.add(" ( " + StructuredName.GIVEN_NAME
								+ operator + "? AND " + Data.MIMETYPE
								+ " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(StructuredName.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_FAMILY_NAME)) {
						whereList.add(" ( " + StructuredName.FAMILY_NAME
								+ operator + "? AND " + Data.MIMETYPE
								+ " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(StructuredName.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_MIDDLE_NAME)) {
						whereList.add(" ( " + StructuredName.MIDDLE_NAME
								+ operator + "? AND " + Data.MIMETYPE
								+ " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(StructuredName.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_SUFFIX)) {
						whereList.add(" ( " + StructuredName.SUFFIX + operator
								+ "? AND " + Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(StructuredName.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_PREFIX)) {
						whereList.add(" ( " + StructuredName.PREFIX + operator
								+ "? AND " + Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(StructuredName.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_NICKNAME)) {
						whereList.add(" ( " + Nickname.NAME + operator
								+ "? AND " + Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(Nickname.CONTENT_ITEM_TYPE);
					} else if (condition
							.contains(JSON_KEY_CONTACT_PHONE_NUMBER)) {
						whereList.add(" ( " + Phone.NUMBER + operator
								+ "? AND " + Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(Phone.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_EMAIL)) {
						whereList.add(" ( " + Email.DATA + operator + "? AND "
								+ Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(Email.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_ADDRESS)) {
						whereList.add(" ( "
								+ StructuredPostal.FORMATTED_ADDRESS + operator
								+ "? AND " + Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(StructuredPostal.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_IM)) {
						whereList.add(" ( " + Im.DATA + operator + "? AND "
								+ Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(Im.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_ORG)) {
						whereList.add(" ( " + Organization.COMPANY + operator
								+ "? AND " + Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(Organization.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_NOTE)) {
						whereList.add(" ( " + Note.NOTE + operator + "? AND "
								+ Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(Note.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_URL)) {
						whereList.add(" ( " + Website.URL + operator + "? AND "
								+ Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(Website.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_BDAY)) {
						whereList.add(" ( " + Event.START_DATE + operator
								+ "? AND " + Data.MIMETYPE + " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(Event.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_CATEGORY)) {
						whereList.add(" ( " + GroupMembership.GROUP_ROW_ID
								+ operator + "? AND " + Data.MIMETYPE
								+ " = ? ) ");
						whereParamsList.add(conditionArr[1].trim());
						whereParamsList.add(GroupMembership.CONTENT_ITEM_TYPE);
					} else if (condition.contains(JSON_KEY_CONTACT_ID)) {
						whereList.add(" ( " + Data.CONTACT_ID + operator
								+ "? ) ");
						whereParamsList.add(conditionArr[1].trim());
					}
				}
			}

			length = whereList.size();
			for (index = 0; index < length; index++) {
				where.append(whereList.get(index));
				if (index < (length - 1)) {
					where.append(" AND ");
				}
			}

			length = whereParamsList.size();
			whereParams = new String[length];
			for (index = 0; index < length; index++) {
				whereParams[index] = whereParamsList.get(index);
			}

			whereClause.setWhere(where.toString());
			whereClause.setWhereParams(whereParams);
		} else {
			whereClause.setWhere(null);
			whereClause.setWhereParams(null);
		}

		return whereClause;
	}

	private Set<String> populateContactIdSet(Cursor cur) {
		Set<String> idSet = new HashSet<String>();

		while (cur.moveToNext()) {
			idSet.add(cur.getString(cur
					.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
		}

		return idSet;
	}

	private WhereClause populateWhereIdClause(Set<String> idSet) {
		WhereClause whereClause = new WhereClause();
		Iterator<String> idSetIt = idSet.iterator();
		StringBuffer contactIds = new StringBuffer("(");

		while (idSetIt.hasNext()) {
			contactIds.append("'" + idSetIt.next() + "'");
			if (idSetIt.hasNext()) {
				contactIds.append(",");
			}
		}

		contactIds.append(")");

		whereClause.setWhere(ContactsContract.Data.CONTACT_ID + " IN "
				+ contactIds.toString());
		whereClause.setWhereParams(null);

		return whereClause;
	}

	private void insert(String contact) throws Exception {
		ops = new ArrayList<ContentProviderOperation>();
		boolean isUpdate = false;

		try {
			ops = this.setRawContact(ops, isUpdate);
			ops = this.setName(ops, contact);
			ops = this.setBirthday(ops, contact);
			ops = this.setNote(ops, contact);
			ops = this.setPhoneNumbers(ops, contact);
			ops = this.setEmails(ops, contact);
			ops = this.setAddresses(ops, contact);
			ops = this.setIms(ops, contact);
			ops = this.setOrganizations(ops, contact);
			ops = this.setPhotos(ops, contact);
			ops = this.setCategories(ops, contact);
			ops = this.setUrls(ops, contact);

			applyBatch(ops);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void update(String contactId, String contact) throws Exception {
		ops = new ArrayList<ContentProviderOperation>();
		boolean isUpdate = true;

		try {
			ops = this.setRawContact(ops, isUpdate);
			ops = this.modifyName(ops, contact, contactId);
			ops = this.modifyBirthday(ops, contact, contactId);
			ops = this.modifyNote(ops, contact, contactId);
			ops = this.modifyPhoneNumbers(ops, contact, contactId);
			ops = this.modifyEmails(ops, contact, contactId);
			ops = this.modifyAddresses(ops, contact, contactId);
			ops = this.modifyIms(ops, contact, contactId);
			ops = this.modifyOrganizations(ops, contact, contactId);
			ops = this.modifyPhotos(ops, contact, contactId);
			ops = this.modifyCategories(ops, contact, contactId);
			ops = this.modifyUrls(ops, contact, contactId);

			applyBatch(ops);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private ArrayList<ContentProviderOperation> setRawContact(
			ArrayList<ContentProviderOperation> ops, boolean isUpdate)
			throws JSONException {

		if (isUpdate) {
			ops.add(ContentProviderOperation.newUpdate(RawContacts.CONTENT_URI)
					.withValue(RawContacts.ACCOUNT_TYPE, EMPTY)
					.withValue(RawContacts.ACCOUNT_NAME, EMPTY).build());
		} else {
			ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
					.withValue(RawContacts.ACCOUNT_TYPE, EMPTY)
					.withValue(RawContacts.ACCOUNT_NAME, EMPTY).build());
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setName(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		String nameStr = contact.get(JSON_KEY_CONTACT_NAME, EMPTY);
		String displayNameStr = contact.get(JSON_KEY_CONTACT_DISPLAY_NAME,
				EMPTY);
		String nicknameStr = contact.get(JSON_KEY_CONTACT_NICKNAME, EMPTY);
		JSONObjectWrapper name = new JSONObjectWrapper(nameStr);

		ops.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.DISPLAY_NAME, displayNameStr)
				.withValue(StructuredName.FAMILY_NAME,
						name.get(JSON_KEY_CONTACT_FAMILY_NAME, EMPTY))
				.withValue(StructuredName.GIVEN_NAME,
						name.get(JSON_KEY_CONTACT_GIVEN_NAME, EMPTY))
				.withValue(StructuredName.MIDDLE_NAME,
						name.get(JSON_KEY_CONTACT_MIDDLE_NAME, EMPTY))
				.withValue(StructuredName.PREFIX,
						name.get(JSON_KEY_CONTACT_PREFIX, EMPTY))
				.withValue(StructuredName.SUFFIX,
						name.get(JSON_KEY_CONTACT_SUFFIX, EMPTY)).build());

		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, Nickname.CONTENT_ITEM_TYPE)
				.withValue(Nickname.NAME, nicknameStr).build());

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyName(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		String nameStr = contact.get(JSON_KEY_CONTACT_NAME, EMPTY);

		if (null != nameStr && !EMPTY.equals(nameStr)) {
			JSONObjectWrapper name = new JSONObjectWrapper(nameStr);
			Builder buffer = ContentProviderOperation.newUpdate(
					Data.CONTENT_URI)
					.withSelection(
							Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
									+ " = ? ",
							new String[] { contactId,
									StructuredName.CONTENT_ITEM_TYPE });

			String familyNameStr = name
					.get(JSON_KEY_CONTACT_FAMILY_NAME, EMPTY);
			if (null != familyNameStr && !EMPTY.equals(familyNameStr)) {
				buffer.withValue(StructuredName.FAMILY_NAME, familyNameStr);
			}

			String givenNameStr = name.get(JSON_KEY_CONTACT_GIVEN_NAME, EMPTY);
			if (null != givenNameStr && !EMPTY.equals(givenNameStr)) {
				buffer.withValue(StructuredName.GIVEN_NAME, givenNameStr);
			}

			String middleNameStr = name
					.get(JSON_KEY_CONTACT_MIDDLE_NAME, EMPTY);
			if (null != middleNameStr && !EMPTY.equals(middleNameStr)) {
				buffer.withValue(StructuredName.MIDDLE_NAME, middleNameStr);
			}

			String prefixStr = name.get(JSON_KEY_CONTACT_PREFIX, EMPTY);
			if (null != prefixStr && !EMPTY.equals(prefixStr)) {
				buffer.withValue(StructuredName.PREFIX, prefixStr);
			}

			String suffixStr = name.get(JSON_KEY_CONTACT_SUFFIX, EMPTY);
			if (null != suffixStr && !EMPTY.equals(suffixStr)) {
				buffer.withValue(StructuredName.SUFFIX, suffixStr);
			}

			ops.add(buffer.build());
		}

		String displayNameStr = contact.get(JSON_KEY_CONTACT_DISPLAY_NAME,
				EMPTY);
		if (null != displayNameStr && !EMPTY.equals(displayNameStr)) {
			Builder displayNameBuffer = ContentProviderOperation.newUpdate(
					Data.CONTENT_URI)
					.withSelection(
							Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
									+ " = ? ",
							new String[] { contactId,
									StructuredName.CONTENT_ITEM_TYPE });
			displayNameBuffer.withValue(StructuredName.DISPLAY_NAME,
					displayNameStr);
			ops.add(displayNameBuffer.build());
		}

		String nicknameStr = contact.get(JSON_KEY_CONTACT_NICKNAME, EMPTY);
		if (null != nicknameStr && !EMPTY.equals(nicknameStr)) {
			Builder nickNameBuffer = ContentProviderOperation.newUpdate(
					Data.CONTENT_URI).withSelection(
					Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE + " = ? ",
					new String[] { contactId, Nickname.CONTENT_ITEM_TYPE });
			nickNameBuffer.withValue(Nickname.NAME, nicknameStr);
			ops.add(nickNameBuffer.build());
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setNote(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		String noteStr = contact.get(JSON_KEY_CONTACT_NOTE, EMPTY);

		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)
				.withValue(Note.NOTE, noteStr).build());

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyNote(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		String noteStr = contact.get(JSON_KEY_CONTACT_NOTE, EMPTY);

		if (null != noteStr && !EMPTY.equals(noteStr)) {
			ops.add(ContentProviderOperation
					.newUpdate(Data.CONTENT_URI)
					.withSelection(
							Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
									+ " = ? ",
							new String[] { contactId, Note.CONTENT_ITEM_TYPE })
					.withValue(Note.NOTE, noteStr).build());
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setBirthday(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		String birthdayStr = contact.get(JSON_KEY_CONTACT_BDAY, EMPTY);

		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, Event.CONTENT_ITEM_TYPE)
				.withValue(Event.TYPE, Event.TYPE_BIRTHDAY)
				.withValue(Event.START_DATE, birthdayStr).build());

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyBirthday(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		String birthdayStr = contact.get(JSON_KEY_CONTACT_BDAY, EMPTY);

		if (null != birthdayStr && !EMPTY.equals(birthdayStr)) {
			ops.add(ContentProviderOperation
					.newUpdate(Data.CONTENT_URI)
					.withSelection(
							Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE
									+ " = ? ",
							new String[] { contactId, Event.CONTENT_ITEM_TYPE })
					.withValue(Event.TYPE, Event.TYPE_BIRTHDAY)
					.withValue(Event.START_DATE, birthdayStr).build());
		}
		return ops;
	}

	private ArrayList<ContentProviderOperation> setPhoneNumbers(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray phoneNumbers = contact.get(JSON_KEY_CONTACT_PHONE_NUMBER,
				new JSONArray());
		JSONObject phone = null;
		String phoneTypeStr = EMPTY;
		Integer phoneType = null;

		if (null != phoneNumbers) {
			int index;
			int count = phoneNumbers.length();
			for (index = 0; index < count; index++) {
				phone = phoneNumbers.getJSONObject(index);
				phoneType = getPhoneType(phone);
				phoneTypeStr = getPhoneTypeStr(phone);

				ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, 0)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.NUMBER, phone.get(phoneTypeStr))
						.withValue(Phone.TYPE, phoneType).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyPhoneNumbers(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray phoneNumbers = contact.get(JSON_KEY_CONTACT_PHONE_NUMBER,
				new JSONArray());
		JSONObject phone = null;
		String phoneTypeStr = EMPTY;
		Integer phoneType = null;

		if (null != phoneNumbers) {
			int index;
			int count = phoneNumbers.length();
			for (index = 0; index < count; index++) {
				phone = phoneNumbers.getJSONObject(index);
				phoneType = getPhoneType(phone);
				phoneTypeStr = getPhoneTypeStr(phone);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValue(Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.NUMBER, phone.get(phoneTypeStr))
						.withValue(Phone.TYPE, phoneType).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setEmails(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray emails = contact.get(JSON_KEY_CONTACT_EMAIL, new JSONArray());
		JSONObject email = null;
		String emailTypeStr = EMPTY;
		Integer emailType = null;

		if (null != emails) {
			int index;
			int count = emails.length();
			for (index = 0; index < count; index++) {
				email = emails.getJSONObject(index);
				emailType = getEmailType(email);
				emailTypeStr = getEmailTypeStr(email);

				ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, 0)
						.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
						.withValue(Email.ADDRESS, email.get(emailTypeStr))
						.withValue(Email.TYPE, emailType).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyEmails(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray emails = contact.get(JSON_KEY_CONTACT_EMAIL, new JSONArray());
		JSONObject email = null;
		String emailTypeStr = EMPTY;
		Integer emailType = null;

		if (null != emails) {
			int index;
			int count = emails.length();
			for (index = 0; index < count; index++) {
				email = emails.getJSONObject(index);
				emailType = getEmailType(email);
				emailTypeStr = getEmailTypeStr(email);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValue(Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
						.withValue(Email.ADDRESS, email.get(emailTypeStr))
						.withValue(Email.TYPE, emailType).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setAddresses(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray addresses = contact.get(JSON_KEY_CONTACT_ADDRESS,
				new JSONArray());
		JSONObjectWrapper addressDetailWrap = null;
		JSONObject address = null;
		Integer addressType = null;

		if (null != addresses) {
			int index;
			int count = addresses.length();
			for (index = 0; index < count; index++) {
				address = addresses.getJSONObject(index);
				addressType = getAddressType(address);
				addressDetailWrap = getAddressTypeObj(address);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, 0)
						.withValue(Data.MIMETYPE,
								StructuredPostal.CONTENT_ITEM_TYPE)
						.withValue(StructuredPostal.TYPE, addressType)
						.withValue(
								StructuredPostal.STREET,
								addressDetailWrap.get(JSON_KEY_ADDRESS_STREET,
										EMPTY))
						.withValue(
								StructuredPostal.REGION,
								addressDetailWrap.get(JSON_KEY_ADDRESS_REGION,
										EMPTY))
						.withValue(
								StructuredPostal.POSTCODE,
								addressDetailWrap.get(
										JSON_KEY_ADDRESS_POSTCODE, EMPTY))
						.withValue(
								StructuredPostal.COUNTRY,
								addressDetailWrap.get(JSON_KEY_ADDRESS_COUNTRY,
										EMPTY))
						.withValue(
								StructuredPostal.CITY,
								addressDetailWrap.get(
										JSON_KEY_ADDRESS_LOCALITY, EMPTY))
						.build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyAddresses(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray addresses = contact.get(JSON_KEY_CONTACT_ADDRESS,
				new JSONArray());
		JSONObjectWrapper addressDetailWrap = null;
		JSONObject address = null;
		Integer addressType = null;

		if (null != addresses) {
			int index;
			int count = addresses.length();
			for (index = 0; index < count; index++) {
				address = addresses.getJSONObject(index);
				addressType = getAddressType(address);
				addressDetailWrap = getAddressTypeObj(address);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValue(Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)
						.withValue(StructuredPostal.TYPE, addressType)
						.withValue(
								StructuredPostal.STREET,
								addressDetailWrap.get(JSON_KEY_ADDRESS_STREET,
										EMPTY))
						.withValue(
								StructuredPostal.REGION,
								addressDetailWrap.get(JSON_KEY_ADDRESS_REGION,
										EMPTY))
						.withValue(
								StructuredPostal.POSTCODE,
								addressDetailWrap.get(
										JSON_KEY_ADDRESS_POSTCODE, EMPTY))
						.withValue(
								StructuredPostal.COUNTRY,
								addressDetailWrap.get(JSON_KEY_ADDRESS_COUNTRY,
										EMPTY))
						.withValue(
								StructuredPostal.CITY,
								addressDetailWrap.get(
										JSON_KEY_ADDRESS_LOCALITY, EMPTY))
						.build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setIms(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray ims = contact.get(JSON_KEY_CONTACT_IM, new JSONArray());
		JSONObject im = null;
		String imProtocolStr = null;
		Integer imProtocol = null;

		if (null != ims) {
			int index;
			int count = ims.length();
			for (index = 0; index < count; index++) {
				im = ims.getJSONObject(index);
				imProtocol = getImProtocol(im);
				imProtocolStr = getImProtocolStr(im);

				ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, 0)
						.withValue(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE)
						.withValue(Im.DATA, im.get(imProtocolStr))
						.withValue(Im.PROTOCOL, imProtocol).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyIms(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray ims = contact.get(JSON_KEY_CONTACT_IM, new JSONArray());
		JSONObject im = null;
		String imProtocolStr = null;
		Integer imProtocol = null;

		if (null != ims) {
			int index;
			int count = ims.length();
			for (index = 0; index < count; index++) {
				im = ims.getJSONObject(index);
				imProtocol = getImProtocol(im);
				imProtocolStr = getImProtocolStr(im);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValue(Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, Im.CONTENT_ITEM_TYPE)
						.withValue(Im.DATA, im.get(imProtocolStr))
						.withValue(Im.PROTOCOL, imProtocol).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setOrganizations(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray organizations = contact.get(JSON_KEY_CONTACT_ORG,
				new JSONArray());
		JSONObject org = null;
		JSONObjectWrapper orgDetailWrap = null;
		Integer orgType = null;

		if (null != organizations) {
			int index;
			int count = organizations.length();
			for (index = 0; index < count; index++) {
				org = organizations.getJSONObject(index);
				orgType = getOrganizationType(org);
				orgDetailWrap = getOrganizationTypeObj(org);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, 0)
						.withValue(Data.MIMETYPE,
								Organization.CONTENT_ITEM_TYPE)
						.withValue(Organization.TYPE, orgType)
						.withValue(Organization.DEPARTMENT,
								orgDetailWrap.get(JSON_KEY_ORG_DEPT, EMPTY))
						.withValue(Organization.TITLE,
								orgDetailWrap.get(JSON_KEY_ORG_TITLE, EMPTY))
						.withValue(Organization.COMPANY,
								orgDetailWrap.get(JSON_KEY_ORG, EMPTY)).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyOrganizations(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray organizations = contact.get(JSON_KEY_CONTACT_ORG,
				new JSONArray());
		JSONObject org = null;
		JSONObjectWrapper orgDetailWrap = null;
		Integer orgType = null;

		if (null != organizations) {
			int index;
			int count = organizations.length();
			for (index = 0; index < count; index++) {
				org = organizations.getJSONObject(index);
				orgType = getOrganizationType(org);
				orgDetailWrap = getOrganizationTypeObj(org);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValue(Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE)
						.withValue(Organization.TYPE, orgType)
						.withValue(Organization.DEPARTMENT,
								orgDetailWrap.get(JSON_KEY_ORG_DEPT, EMPTY))
						.withValue(Organization.TITLE,
								orgDetailWrap.get(JSON_KEY_ORG_TITLE, EMPTY))
						.withValue(Organization.COMPANY,
								orgDetailWrap.get(JSON_KEY_ORG, EMPTY)).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setPhotos(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws Exception {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		String photo = contact.get(JSON_KEY_CONTACT_PHOTO, EMPTY);
		byte[] photoBytes = null;

		if (null != photo && !EMPTY.equals(photo)) {
			try {
				photoBytes = getBytes(photo);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, 0)
					.withValue(Data.IS_SUPER_PRIMARY, 1)
					.withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
					.withValue(Photo.PHOTO, photoBytes).build());
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyPhotos(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws Exception {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		String photo = contact.get(JSON_KEY_CONTACT_PHOTO, EMPTY);
		byte[] photoBytes = null;

		if (null != photo && !EMPTY.equals(photo)) {
			try {
				photoBytes = getBytes(photo);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			ops.add(ContentProviderOperation
					.newInsert(Data.CONTENT_URI)
					.withValue(Data.RAW_CONTACT_ID, contactId)
					.withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
					.withValue(Data.IS_SUPER_PRIMARY, 1)
					.withValue(Photo.PHOTO, photoBytes).build());
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setCategories(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray categories = contact.get(JSON_KEY_CONTACT_CATEGORY,
				new JSONArray());
		JSONObject category = null;

		if (null != categories) {
			int index;
			int count = categories.length();
			for (index = 0; index < count; index++) {
				category = categories.getJSONObject(index);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, 0)
						.withValue(Data.MIMETYPE,
								GroupMembership.CONTENT_ITEM_TYPE)
						.withValue(GroupMembership.GROUP_ROW_ID,
								category.get(JSON_KEY_CATEGORY)).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyCategories(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray categories = contact.get(JSON_KEY_CONTACT_CATEGORY,
				new JSONArray());
		JSONObject category = null;

		if (null != categories) {
			int index;
			int count = categories.length();
			for (index = 0; index < count; index++) {
				category = categories.getJSONObject(index);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValue(Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE)
						.withValue(GroupMembership.GROUP_ROW_ID,
								category.get(JSON_KEY_CATEGORY)).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> setUrls(
			ArrayList<ContentProviderOperation> ops, String contactStr)
			throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray urls = contact.get(JSON_KEY_CONTACT_URL, new JSONArray());
		JSONObject url = null;
		String urlTypeStr = EMPTY;
		Integer urlType = null;

		if (null != urls) {
			int index;
			int count = urls.length();
			for (index = 0; index < count; index++) {
				url = urls.getJSONObject(index);
				urlType = getUrlType(url);
				urlTypeStr = getUrlTypeStr(url);

				ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID, 0)
						.withValue(Data.MIMETYPE, Website.CONTENT_ITEM_TYPE)
						.withValue(Website.TYPE, urlType)
						.withValue(Website.URL, url.get(urlTypeStr)).build());
			}
		}

		return ops;
	}

	private ArrayList<ContentProviderOperation> modifyUrls(
			ArrayList<ContentProviderOperation> ops, String contactStr,
			String contactId) throws JSONException {

		JSONObjectWrapper contact = new JSONObjectWrapper(contactStr);
		JSONArray urls = contact.get(JSON_KEY_CONTACT_URL, new JSONArray());
		JSONObject url = null;
		String urlTypeStr = EMPTY;
		Integer urlType = null;

		if (null != urls) {
			int index;
			int count = urls.length();
			for (index = 0; index < count; index++) {
				url = urls.getJSONObject(index);
				urlType = getUrlType(url);
				urlTypeStr = getUrlTypeStr(url);

				ops.add(ContentProviderOperation
						.newInsert(Data.CONTENT_URI)
						.withValue(Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, Website.CONTENT_ITEM_TYPE)
						.withValue(Website.TYPE, urlType)
						.withValue(Website.URL, url.get(urlTypeStr)).build());
			}
		}

		return ops;
	}

	private byte[] getBytes(String photo) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int bytesRead = 0;
		long totalBytesRead = 0;
		byte[] data = new byte[8192];
		InputStream in = getPathFromUri(photo);

		while ((bytesRead = in.read(data, 0, data.length)) != -1
				&& totalBytesRead <= MAX_PHOTO_SIZE) {
			os.write(data, 0, bytesRead);
			totalBytesRead += bytesRead;
		}

		in.close();
		os.flush();

		return os.toByteArray();
	}

	private InputStream getPathFromUri(String path) throws IOException {
		if (path.startsWith("content:")) {
			Uri uri = Uri.parse(path);
			return dynamicApp.getContentResolver().openInputStream(uri);
		} else if (path.startsWith("http:") || path.startsWith("file:")) {
			URL url = new URL(path);
			return url.openStream();
		} else {
			return new FileInputStream(path);
		}
	}

	private Integer getPhoneType(JSONObject phone) {
		Integer phoneType = null;
		String phoneStr = EMPTY + phone;

		if (phoneStr.contains(JSON_KEY_HOME_FAX)) {
			phoneType = Phone.TYPE_FAX_HOME;
		} else if (phoneStr.contains(JSON_KEY_WORK_MOBILE)) {
			phoneType = Phone.TYPE_WORK_MOBILE;
		} else if (phoneStr.contains(JSON_KEY_WORK_PAGER)) {
			phoneType = Phone.TYPE_WORK_PAGER;
		} else if (phoneStr.contains(JSON_KEY_WORK_FAX)) {
			phoneType = Phone.TYPE_FAX_WORK;
		} else if (phoneStr.contains(JSON_KEY_HOME)) {
			phoneType = Phone.TYPE_HOME;
		} else if (phoneStr.contains(JSON_KEY_PAGER)) {
			phoneType = Phone.TYPE_PAGER;
		} else if (phoneStr.contains(JSON_KEY_CUSTOM)) {
			phoneType = Phone.TYPE_CUSTOM;
		} else if (phoneStr.contains(JSON_KEY_CALLBACK)) {
			phoneType = Phone.TYPE_CALLBACK;
		} else if (phoneStr.contains(JSON_KEY_ASSISTANT)) {
			phoneType = Phone.TYPE_ASSISTANT;
		} else if (phoneStr.contains(JSON_KEY_CAR)) {
			phoneType = Phone.TYPE_CAR;
		} else if (phoneStr.contains(JSON_KEY_COMPANY_MAIN)) {
			phoneType = Phone.TYPE_COMPANY_MAIN;
		} else if (phoneStr.contains(JSON_KEY_ISDN)) {
			phoneType = Phone.TYPE_ISDN;
		} else if (phoneStr.contains(JSON_KEY_MAIN)) {
			phoneType = Phone.TYPE_MAIN;
		} else if (phoneStr.contains(JSON_KEY_MMS)) {
			phoneType = Phone.TYPE_MMS;
		} else if (phoneStr.contains(JSON_KEY_OTHER_FAX)) {
			phoneType = Phone.TYPE_OTHER_FAX;
		} else if (phoneStr.contains(JSON_KEY_RADIO)) {
			phoneType = Phone.TYPE_RADIO;
		} else if (phoneStr.contains(JSON_KEY_TELEX)) {
			phoneType = Phone.TYPE_TELEX;
		} else if (phoneStr.contains(JSON_KEY_TTY_TDD)) {
			phoneType = Phone.TYPE_TTY_TDD;
		} else if (phoneStr.contains(JSON_KEY_MOBILE)) {
			phoneType = Phone.TYPE_MOBILE;
		} else if (phoneStr.contains(JSON_KEY_WORK)) {
			phoneType = Phone.TYPE_WORK;
		} else {
			phoneType = Phone.TYPE_OTHER;
		}

		return phoneType;
	}

	private String getPhoneTypeStr(JSONObject phone) {
		String phoneType = EMPTY;
		String phoneStr = EMPTY + phone;

		if (phoneStr.contains(JSON_KEY_HOME_FAX)) {
			phoneType = JSON_KEY_HOME_FAX;
		} else if (phoneStr.contains(JSON_KEY_WORK_MOBILE)) {
			phoneType = JSON_KEY_WORK_MOBILE;
		} else if (phoneStr.contains(JSON_KEY_WORK_PAGER)) {
			phoneType = JSON_KEY_WORK_PAGER;
		} else if (phoneStr.contains(JSON_KEY_WORK_FAX)) {
			phoneType = JSON_KEY_WORK_FAX;
		} else if (phoneStr.contains(JSON_KEY_HOME)) {
			phoneType = JSON_KEY_HOME;
		} else if (phoneStr.contains(JSON_KEY_PAGER)) {
			phoneType = JSON_KEY_PAGER;
		} else if (phoneStr.contains(JSON_KEY_CUSTOM)) {
			phoneType = JSON_KEY_CUSTOM;
		} else if (phoneStr.contains(JSON_KEY_CALLBACK)) {
			phoneType = JSON_KEY_CALLBACK;
		} else if (phoneStr.contains(JSON_KEY_ASSISTANT)) {
			phoneType = JSON_KEY_ASSISTANT;
		} else if (phoneStr.contains(JSON_KEY_CAR)) {
			phoneType = JSON_KEY_CAR;
		} else if (phoneStr.contains(JSON_KEY_COMPANY_MAIN)) {
			phoneType = JSON_KEY_COMPANY_MAIN;
		} else if (phoneStr.contains(JSON_KEY_ISDN)) {
			phoneType = JSON_KEY_ISDN;
		} else if (phoneStr.contains(JSON_KEY_MAIN)) {
			phoneType = JSON_KEY_MAIN;
		} else if (phoneStr.contains(JSON_KEY_MMS)) {
			phoneType = JSON_KEY_MMS;
		} else if (phoneStr.contains(JSON_KEY_OTHER_FAX)) {
			phoneType = JSON_KEY_OTHER_FAX;
		} else if (phoneStr.contains(JSON_KEY_RADIO)) {
			phoneType = JSON_KEY_RADIO;
		} else if (phoneStr.contains(JSON_KEY_TELEX)) {
			phoneType = JSON_KEY_TELEX;
		} else if (phoneStr.contains(JSON_KEY_TTY_TDD)) {
			phoneType = JSON_KEY_TTY_TDD;
		} else if (phoneStr.contains(JSON_KEY_MOBILE)) {
			phoneType = JSON_KEY_MOBILE;
		} else if (phoneStr.contains(JSON_KEY_WORK)) {
			phoneType = JSON_KEY_WORK;
		} else {
			phoneType = JSON_KEY_OTHER;
		}

		return phoneType;
	}

	private Integer getEmailType(JSONObject email) {
		Integer emailType = null;
		String emailStr = EMPTY + email;

		if (emailStr.contains(JSON_KEY_HOME)) {
			emailType = Email.TYPE_HOME;
		} else if (emailStr.contains(JSON_KEY_MOBILE)) {
			emailType = Email.TYPE_MOBILE;
		} else if (emailStr.contains(JSON_KEY_WORK)) {
			emailType = Email.TYPE_WORK;
		} else if (emailStr.contains(JSON_KEY_CUSTOM)) {
			emailType = Email.TYPE_CUSTOM;
		} else {
			emailType = Email.TYPE_OTHER;
		}

		return emailType;
	}

	private String getEmailTypeStr(JSONObject email) {
		String emailType = EMPTY;
		String emailStr = EMPTY + email;

		if (emailStr.contains(JSON_KEY_HOME)) {
			emailType = JSON_KEY_HOME;
		} else if (emailStr.contains(JSON_KEY_MOBILE)) {
			emailType = JSON_KEY_MOBILE;
		} else if (emailStr.contains(JSON_KEY_WORK)) {
			emailType = JSON_KEY_WORK;
		} else if (emailStr.contains(JSON_KEY_CUSTOM)) {
			emailType = JSON_KEY_CUSTOM;
		} else {
			emailType = JSON_KEY_OTHER;
		}

		return emailType;
	}

	private Integer getAddressType(JSONObject address) {
		Integer addressType = null;
		String addressStr = EMPTY + address;

		if (addressStr.contains(JSON_KEY_HOME)) {
			addressType = StructuredPostal.TYPE_HOME;
		} else if (addressStr.contains(JSON_KEY_WORK)) {
			addressType = StructuredPostal.TYPE_WORK;
		} else if (addressStr.contains(JSON_KEY_CUSTOM)) {
			addressType = StructuredPostal.TYPE_CUSTOM;
		} else {
			addressType = StructuredPostal.TYPE_OTHER;
		}

		return addressType;
	}

	private JSONObjectWrapper getAddressTypeObj(JSONObject address)
			throws JSONException {
		JSONObject addressType = null;
		JSONObjectWrapper addressTypeWrap = null;
		String addressStr = EMPTY + address;

		if (addressStr.contains(JSON_KEY_HOME)) {
			addressType = address.getJSONObject(JSON_KEY_HOME);
		} else if (addressStr.contains(JSON_KEY_WORK)) {
			addressType = address.getJSONObject(JSON_KEY_WORK);
		} else if (addressStr.contains(JSON_KEY_CUSTOM)) {
			addressType = address.getJSONObject(JSON_KEY_CUSTOM);
		} else {
			addressType = address.getJSONObject(JSON_KEY_OTHER);
		}

		addressTypeWrap = new JSONObjectWrapper(EMPTY + addressType);

		return addressTypeWrap;
	}

	private Integer getImProtocol(JSONObject im) {
		Integer imType = null;
		String imStr = EMPTY + im;

		if (imStr.contains(JSON_KEY_PROTOCOL_AIM)) {
			imType = Im.PROTOCOL_AIM;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_YAHOO)) {
			imType = Im.PROTOCOL_YAHOO;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_GTALK)) {
			imType = Im.PROTOCOL_GOOGLE_TALK;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_ICQ)) {
			imType = Im.PROTOCOL_ICQ;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_JABBER)) {
			imType = Im.PROTOCOL_JABBER;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_MSN)) {
			imType = Im.PROTOCOL_MSN;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_NET_MEETING)) {
			imType = Im.PROTOCOL_NETMEETING;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_QQ)) {
			imType = Im.PROTOCOL_QQ;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_SKYPE)) {
			imType = Im.PROTOCOL_SKYPE;
		} else {
			imType = Im.PROTOCOL_CUSTOM;
		}

		return imType;
	}

	private String getImProtocolStr(JSONObject im) throws JSONException {
		String imProtocolStr = null;
		String imStr = EMPTY + im;

		if (imStr.contains(JSON_KEY_PROTOCOL_AIM)) {
			imProtocolStr = JSON_KEY_PROTOCOL_AIM;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_YAHOO)) {
			imProtocolStr = JSON_KEY_PROTOCOL_YAHOO;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_GTALK)) {
			imProtocolStr = JSON_KEY_PROTOCOL_GTALK;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_ICQ)) {
			imProtocolStr = JSON_KEY_PROTOCOL_ICQ;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_JABBER)) {
			imProtocolStr = JSON_KEY_PROTOCOL_JABBER;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_MSN)) {
			imProtocolStr = JSON_KEY_PROTOCOL_MSN;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_NET_MEETING)) {
			imProtocolStr = JSON_KEY_PROTOCOL_NET_MEETING;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_QQ)) {
			imProtocolStr = JSON_KEY_PROTOCOL_QQ;
		} else if (imStr.contains(JSON_KEY_PROTOCOL_SKYPE)) {
			imProtocolStr = JSON_KEY_PROTOCOL_SKYPE;
		} else {
			imProtocolStr = JSON_KEY_PROTOCOL_CUSTOM;
		}

		return imProtocolStr;
	}

	private Integer getOrganizationType(JSONObject org) {
		Integer orgType = null;
		String orgStr = EMPTY + org;

		if (orgStr.contains(JSON_KEY_WORK)) {
			orgType = Organization.TYPE_WORK;
		} else if (orgStr.contains(JSON_KEY_CUSTOM)) {
			orgType = Organization.TYPE_CUSTOM;
		} else {
			orgType = Organization.TYPE_OTHER;
		}

		return orgType;
	}

	private JSONObjectWrapper getOrganizationTypeObj(JSONObject org)
			throws JSONException {
		JSONObject orgType = null;
		JSONObjectWrapper orgTypeWrap = null;
		String orgStr = EMPTY + org;

		if (orgStr.contains(JSON_KEY_WORK)) {
			orgType = org.getJSONObject(JSON_KEY_WORK);
		} else if (orgStr.contains(JSON_KEY_CUSTOM)) {
			orgType = org.getJSONObject(JSON_KEY_CUSTOM);
		} else {
			orgType = org.getJSONObject(JSON_KEY_OTHER);
		}

		orgTypeWrap = new JSONObjectWrapper(EMPTY + orgType);

		return orgTypeWrap;
	}

	private Integer getUrlType(JSONObject url) {
		Integer urlType = null;
		String urlStr = EMPTY + url;

		if (urlStr.contains(JSON_KEY_HOMEPAGE)) {
			urlType = Website.TYPE_HOMEPAGE;
		} else if (urlStr.contains(JSON_KEY_WORK)) {
			urlType = Website.TYPE_WORK;
		} else if (urlStr.contains(JSON_KEY_CUSTOM)) {
			urlType = Website.TYPE_CUSTOM;
		} else if (urlStr.contains(JSON_KEY_BLOG)) {
			urlType = Website.TYPE_BLOG;
		} else if (urlStr.contains(JSON_KEY_FTP)) {
			urlType = Website.TYPE_FTP;
		} else if (urlStr.contains(JSON_KEY_HOME)) {
			urlType = Website.TYPE_HOME;
		} else if (urlStr.contains(JSON_KEY_PROFILE)) {
			urlType = Website.TYPE_PROFILE;
		} else {
			urlType = Website.TYPE_OTHER;
		}

		return urlType;
	}

	private String getUrlTypeStr(JSONObject url) {
		String urlType = EMPTY;
		String urlStr = EMPTY + url;

		if (urlStr.contains(JSON_KEY_HOMEPAGE)) {
			urlType = JSON_KEY_HOMEPAGE;
		} else if (urlStr.contains(JSON_KEY_WORK)) {
			urlType = JSON_KEY_WORK;
		} else if (urlStr.contains(JSON_KEY_CUSTOM)) {
			urlType = JSON_KEY_CUSTOM;
		} else if (urlStr.contains(JSON_KEY_BLOG)) {
			urlType = JSON_KEY_BLOG;
		} else if (urlStr.contains(JSON_KEY_FTP)) {
			urlType = JSON_KEY_FTP;
		} else if (urlStr.contains(JSON_KEY_HOME)) {
			urlType = JSON_KEY_HOME;
		} else if (urlStr.contains(JSON_KEY_PROFILE)) {
			urlType = JSON_KEY_PROFILE;
		} else {
			urlType = JSON_KEY_OTHER;
		}

		return urlType;
	}

	@Override
	public void onBackKeyDown() {
		DynamicAppUtils.currentCommandRef = null;
		instance = null;
	}

	class WhereClause {
		private String where;
		private String whereParams[];

		public String getWhere() {
			return where;
		}

		public void setWhere(String where) {
			this.where = where;
		}

		public String[] getWhereParams() {
			return whereParams;
		}

		public void setWhereParams(String whereParams[]) {
			this.whereParams = whereParams;
		}
	}
}
