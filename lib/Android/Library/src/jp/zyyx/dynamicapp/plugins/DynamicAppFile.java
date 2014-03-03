package jp.zyyx.dynamicapp.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLConnection;

import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class DynamicAppFile extends DynamicAppPlugin {
	private static final int ERROR_NOT_FOUND = 1;
	private static final int ERROR_SECURITY = 2;
	private static final int ERROR_ABORT = 3;
	private static final int ERROR_NOT_READABLE = 4;
	private static final int ERROR_ENCODING = 5;
	private static final int ERROR_NO_MODIFICATION_ALLOWED = 6;
	private static final int ERROR_INVALID_STATE = 7;
	private static final int ERROR_SYNTAX = 8;
	private static final int ERROR_INVALID_MODIFICATION = 9;
	private static final int ERROR_QUOTA_EXCEEDED = 10;
	private static final int ERROR_TYPE_MISMATCH = 11;
	private static final int ERROR_PATH_EXISTS = 12;
	
	private static DynamicAppFile instance = null;
	
	private DynamicAppFile() {}
	
	public static synchronized DynamicAppFile getInstance() {
        if (instance == null) {
	            instance = new DynamicAppFile();
	    }
	    return instance;
	}

	private File getFileInstance(String path, String filename) {
		return new File(DynamicAppUtils.makePath(path), filename);
	}
	
	@Override
	public void execute() {
		if (methodName.equalsIgnoreCase("getMetadata")) {
			this.getMetaData();
			
		} else if (methodName.equalsIgnoreCase("create")) {
			this.create();
			
		} else if (methodName.equalsIgnoreCase("removeRecursively")) {
			this.removeRecursively();
			
		} else if (methodName.equalsIgnoreCase("copy")) {
			this.copy();
			
		} else if (methodName.equalsIgnoreCase("move")) {
			this.move();
			
		} else if (methodName.equalsIgnoreCase("write")) {
			this.write();
			
		} else if (methodName.equalsIgnoreCase("read")) {
			this.read();
			
		} else {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
		}
	}

	private void getMetaData() {
		String path = param.get("parentPath", "");
		String filename = param.get("filename", "");

		JSONObject jsonObject = new JSONObject();
		
		File file = getFileInstance(path, filename);
		if (file.exists()) {
			try {
				jsonObject.put("fullPath", file.getAbsolutePath());
				jsonObject.put("kind", file.isFile() ? 1 : 0);
				jsonObject.put("type", URLConnection.getFileNameMap().getContentTypeFor("file://" + file.getAbsolutePath()));
				jsonObject.put("size", file.length());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			DynamicAppFile.onSuccess(jsonObject, callbackId, false);
		} else {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			DynamicAppFile.onError(DynamicAppFile.ERROR_NOT_FOUND + "", callbackId);
		}
	}

	private void create() {
		String path = param.get("parentPath", "");
		String filename = param.get("filename", "");
		
		File file = getFileInstance(path, filename);
		boolean createOk = false;
		
		try {
			if(!file.exists()) {
				if(file.isDirectory()) {
					createOk = file.mkdirs();
				} else {
					if(file.getParentFile().exists() || file.getParentFile().mkdirs()) {
						createOk = file.createNewFile();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(createOk) {
			getMetaData();
		} else {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			DynamicAppFile.onError(DynamicAppFile.ERROR_ABORT + "", callbackId);
		}
	}
	
	private void removeRecursively() {
		String fullPath = param.get("fullPath", "");
		if(fullPath != DynamicAppUtils.makePath("") && _delete(fullPath)) {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			DynamicAppFile.onSuccess(null, callbackId, false);
		} else {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			DynamicAppFile.onError(DynamicAppFile.ERROR_ABORT + "", callbackId);
		}
	}

	private boolean _delete(String fullPath) {
		File file = new File(fullPath);
		
		if(file.exists()) {
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File c : files) {
					if(!_delete(c.getAbsolutePath())) {
						return false;
					}
				}
				
				if(file.listFiles().length == 0) {
					file.delete();
				}
				
				return true;
			} else {
				return file.delete();
			}
		}
		
		return false;
	}
	
	private void copy() {
		String fullPath = param.get("fullPath", "");
		String targetDirectory = param.get("targetDirectory", "");
		String newFilename = param.get("newName", "");
		
		try {
			if(_copy(fullPath, targetDirectory, newFilename, false)) {
				dynamicApp.callJsEvent(PROCESSING_FALSE);
				DynamicAppFile.onSuccess(null, callbackId, false);
			} else {
				dynamicApp.callJsEvent(PROCESSING_FALSE);
				DynamicAppFile.onError(DynamicAppFile.ERROR_ABORT + "", callbackId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean _copy(String fullPath, String targetDirectory, String newFilename, boolean recurse) throws IOException {
		File src = new File(fullPath);
		File dest;
		if(recurse) {
			dest = new File(targetDirectory, newFilename);
		} else {
			dest = getFileInstance(targetDirectory, newFilename.equals("") ? src.getName() : newFilename);
		}
		
		try {
			if(dest.getCanonicalPath().startsWith(src.getCanonicalPath())) {
				return false;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		boolean result = true;
		IOException exception = null;
		try {
			if (src.exists()) {
				if (src.isDirectory()) {
					File files[] = src.listFiles();
					for (File srcFile : files) {
						File destFile = new File(dest.getAbsolutePath());
						if(!_copy(srcFile.getAbsolutePath(),
								destFile.getPath(), srcFile.getName(), true)) {
							result = false;
							break;
						}
					}
				} else {
					fis = new FileInputStream(src);
					if(dest.createNewFile()) {
						fos = new FileOutputStream(dest);
						
						byte[] buf = new byte[1024];
						int len;
						while ((len = fis.read(buf)) > 0) {
							fos.write(buf, 0, len);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			exception = e;
			result = false;
		} finally {
			if(fis != null) {
				fis.close();
			}
			if(fos != null) {
				fos.close();
			}
		}
		
		if(exception != null) {
			throw(exception);
		}
		
		return result;
	}

	private void move() {
		String fullPath = param.get("fullPath", "");
		String targetDirectory = param.get("targetDirectory", "");
		String newFilename = param.get("newName", "");
		
		File src = new File(fullPath);
		File dest = getFileInstance(targetDirectory, newFilename.equals("") ? src.getName() : newFilename);
		JSONObject jsonObject = null;
		
		try {
			if(!dest.getCanonicalPath().startsWith(src.getCanonicalPath()) && src.exists() && !dest.exists()) {
				if(!dest.getParentFile().exists()) {
					dest.getParentFile().mkdirs();
				}
				if (src.renameTo(dest)) {
					jsonObject = new JSONObject();
					jsonObject.put("newFullPath", dest.getAbsolutePath());
					jsonObject.put("newParentPath", dest.getParent());
					jsonObject.put("newFilename", dest.getName());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		dynamicApp.callJsEvent(PROCESSING_FALSE);
		if(jsonObject != null) {
			DynamicAppFile.onSuccess(jsonObject, callbackId, false);
		} else {
			DynamicAppFile.onError(DynamicAppFile.ERROR_ABORT + "", callbackId);
		}
	}

	private void write() {
		String fullPath = param.get("fullPath", "");
		String text = param.get("data", "");
		long offset = param.get("position", 0);
		
		File file = new File(fullPath);
		JSONObject jsonObject = null;
		RandomAccessFile raf = null;
		
		try {
			raf = new RandomAccessFile(file, "rw");
			if(offset > raf.length()) {
				offset = raf.length();
			} else if(offset < 0) {
				offset = 0;
			}

			raf.seek(offset);
			raf.write(text.getBytes());
			
			long filePointer = raf.getFilePointer(); 
			raf.setLength(filePointer);
			
			jsonObject = new JSONObject();
			jsonObject.put("size", filePointer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if(raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		dynamicApp.callJsEvent(PROCESSING_FALSE);
		if(jsonObject != null) {
			DynamicAppFile.onSuccess(jsonObject, callbackId, false);
		} else {
			DynamicAppFile.onError(DynamicAppFile.ERROR_ABORT + "", callbackId);
		}
	}

	private void read() {
		String fullPath = param.get("fullPath", "");
		
	    StringBuilder contents = new StringBuilder();
	    boolean success = false;

	    BufferedReader input = null;
	    try {
	    	input =  new BufferedReader(new FileReader(new File(fullPath)));
			String line = null;
			while((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
			success = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch(Exception e) {
					
				}
			}
		}

		dynamicApp.callJsEvent(PROCESSING_FALSE);
	    if(success) {
			DynamicAppFile.onSuccess(contents.toString(), callbackId, false);
		} else {
			DynamicAppFile.onError(DynamicAppFile.ERROR_ABORT + "", callbackId);
		}
	}
}
