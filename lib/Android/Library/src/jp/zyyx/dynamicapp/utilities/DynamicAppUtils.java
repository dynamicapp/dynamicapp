package jp.zyyx.dynamicapp.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jp.zyyx.dynamicapp.core.Command;
import jp.zyyx.dynamicapp.plugins.view.DynamicAppVideoView;
import android.content.pm.ActivityInfo;

import org.apache.http.HttpException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.Surface;

public class DynamicAppUtils {
	public static String _basepath;
	public static Activity dynamicAppActivityRef;
	public static DynamicAppVideoView VideoViewRef;
	public static Command currentCommandRef = null;
	public static boolean download_flg = false; // Flag for download.
	public static final boolean DEBUG = true;

	public static void setBasePath(String _base) {
		_basepath = _base;
	}

	public static String makePath(String _path) {
		if (_path.length() > 0)
			return _basepath + "/" + _path;
		return _basepath;
	}

	public static String makeWWWPath(String _path) {
		if (_path.length() > 0)
			return _basepath + "/" + Constant.WWW_FOLDER + "/" + _path;
		return _basepath;
	}

	public static String getFilenameFromURL(URL url) {
		String[] p = url.getFile().split("/");
		String s = p[p.length - 1];
		if (s.indexOf("?") > -1) {
			return s.substring(0, s.indexOf("?"));
		}
		return s;
	}

	public static void delete(String _path) {
		File f = new File(_path);
		deletefile(f);
	}

	public static void deletefile(File f) {
		if (f.exists() == false) {
			return;
		}
		if (f.isFile()) {
			f.delete();
		}
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				deletefile(files[i]);
			}
			f.delete();
		}
	}

	public static void copyAsset(Context context, String srcdir) {
		copyAsset(context, srcdir, "");
	}

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public static boolean copyAsset(Context context, String sourceFolder, String destinationFolder) {
		InputStream input = null;
		FileOutputStream output = null;
		try {
			String[] fileList = context.getAssets().list(sourceFolder);
			if (fileList == null || fileList.length == 0) {
				return false;
			}

			if (destinationFolder != null && destinationFolder.compareTo("") != 0) {
				File dstfile = new File(makePath(destinationFolder));
				if (!dstfile.exists()) {
					dstfile.mkdir();
				}
			}

			AssetManager as = context.getAssets();
			for (String file : fileList) {
				File outfile = null;
				if (destinationFolder != null && destinationFolder.compareTo("") != 0) {
					outfile = new File(makePath(destinationFolder + "/" + file));
					if (context.getAssets().list(destinationFolder + "/" + file).length > 0) {
						if (!outfile.exists()) {
							outfile.mkdir();
						}
						copyAsset(context, sourceFolder + "/" + file, destinationFolder + "/" + file);
						continue;
					}
				} else {
					outfile = new File(makePath(file));
				}

				input = as.open(sourceFolder + "/" + file);
				if (outfile.exists()) {
					int inputsize = input.available();
					int outputsize = (int) outfile.length();
					if (outfile.exists() && inputsize == outputsize) {
						input.close();
						continue;
					}
				}

				if (destinationFolder != null && destinationFolder.compareTo("") != 0) {
					output = new FileOutputStream(outfile, false);
				} else {
					output = context.openFileOutput(file,
							Context.MODE_WORLD_READABLE);
				}
				int DEFAULT_BUFFER_SIZE = 1024 * 16;

				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int n = 0;
				while (-1 != (n = input.read(buffer))) {
					output.write(buffer, 0, n);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean unZip(String _zip, String _dest) {
		ZipInputStream in = null;
		BufferedOutputStream out = null;
		boolean ret = true;
		try {
			ZipEntry zipEntry;
			String path = makePath(_dest);
			DynamicAppUtils.delete(path + Constant.WWW_FOLDER);
			in = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(makePath(_zip))));
			while ((zipEntry = in.getNextEntry()) != null) {
				File file = new File(path, zipEntry.getName());
				if (zipEntry.isDirectory()) {
					file.mkdirs();
				} else {
					file.getParentFile().mkdirs();
					out = new BufferedOutputStream(
							new FileOutputStream(file));
					int count;
					byte[] buffer = new byte[1024];
					while ((count = in.read(buffer)) > 0) {
						out.write(buffer, 0, count);
					}
					out.close();
					out = null;
				}
				in.closeEntry();
			}
		} catch (Exception e) {
			ret = false;
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch(Exception e) {
					ret = false;
				}
			}
			
			if(in != null) {
				try {
					in.close();
				} catch(Exception e) {
					ret = false;
				}
			}
		}
		
		return ret;
	}

	public static boolean httpDownload(String _url, String _savepath,
			String _user, String _pass) {
		try {
			File lastModifiedFile = new File(makePath(Constant.LAST_MODIFIED_DATE_FILE));
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			long savedLastModified = 0;
			try {
				fis = new FileInputStream(lastModifiedFile);
				isr = new InputStreamReader(fis, Constant.CHARSET_UTF8);
				br = new BufferedReader(isr);
				String s = br.readLine();
				savedLastModified = Long.valueOf(s);
			} catch (FileNotFoundException e) {

			} finally {
				if (br != null) {
					br.close();
				}

				if (isr != null) {
					isr.close();
				}

				if (fis != null) {
					fis.close();
				}
			}

			// BASIC authentication
			if (_user != null && _user.length() > 0) {
				final String username = _user;
				final String password = _pass;
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password
								.toCharArray());
					}
				});
			}
			URL url = new URL(_url);
			String outfile = getFilenameFromURL(url);

			String outpath = "";
			if (_savepath != null && _savepath.length() > 0) {
				outpath = _savepath + "/";
			}
			outpath += outfile;

			URLConnection conn = url.openConnection();
			File file = new File(makePath(outpath));
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod(Constant.METHOD_GET);
			httpConn.setIfModifiedSince(savedLastModified);

			httpConn.connect();
			int response = httpConn.getResponseCode();

			if (response != HttpURLConnection.HTTP_OK) {
				if (response == HttpURLConnection.HTTP_NOT_MODIFIED) {
					return true;
				} else {
					throw new HttpException();
				}
			}
			int contentLength = httpConn.getContentLength();

			InputStream in = httpConn.getInputStream();

			FileOutputStream outStream = new FileOutputStream(file);

			DataInputStream dataInStream = new DataInputStream(in);
			DataOutputStream dataOutStream = new DataOutputStream(
					new BufferedOutputStream(outStream));

			// Read Data
			byte[] b = new byte[4096];
			int readByte = 0;

			while (-1 != (readByte = dataInStream.read(b))) {
				dataOutStream.write(b, 0, readByte);
			}

			dataInStream.close();
			dataOutStream.close();

			if (contentLength < 0) {
			}
			if (!unZip(Constant.WWW_ZIP_FOLDER, "")) {
				return false;
			}

			long lastModified = httpConn.getHeaderFieldDate(Constant.KEY_LAST_MODIFIED, 0);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(lastModifiedFile, false);
				String str = String.valueOf(lastModified);
				fos.write(str.getBytes());
			} catch (FileNotFoundException ex) {

			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		} catch (IOException e) {
		} catch (HttpException e) {
		}
		return true;
	}

	public static void fixDisplayOrientation(Activity activity) {
		Configuration config = activity.getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	public static void unfixDisplayOrientation(Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}

	@SuppressLint("NewApi")
	public static float setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		int result = 0;

		if (Build.VERSION.SDK_INT >= 9) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(cameraId, info);
			int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
			int degrees = 0;
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}

			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (info.orientation - degrees + 360) % 360;
			}
		} else {
			result = 0;
		}
		camera.setDisplayOrientation(result);
		return result;
	}

	public static int getDPI(int size) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, dynamicAppActivityRef.getResources().getDisplayMetrics());
	}
	
	public static Bitmap decodeBitmap(ContentResolver cr, Uri uri,
	        int reqWidth, int reqHeight) {
		InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(input, null, options);
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
	    input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
	    return BitmapFactory.decodeStream(input, null, options);
	}
	
	public static Bitmap decodeBitmap(String pathName,
	        int reqWidth, int reqHeight) {

	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(pathName, options);
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(pathName, options);
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}
	
	public static boolean isEmulator() {
		return Build.DEVICE.startsWith(Constant.KEY_EMULATOR);
	}
	
	public static int isNewerVersion(String v1, String v2) {
		String[] vals1 = v1.split("\\.");
		String[] vals2 = v2.split("\\.");
		int len1 = vals1.length;
		int len2 = vals2.length;
		int i=0;
		
		while(i<len1 && i<len2 && vals1[i].equals(vals2[i])) {
		  i++;
		}

		if (i<len1 && i<len2) {
		    int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
		    return diff<0? -1 : diff==0? 0:1;
		} 
		
		return len1<len2? -1 : len1==len2? 0 :1;
	}
}
