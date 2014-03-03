package jp.zyyx.dynamicapp;

import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.view.Window;
import android.view.WindowManager;

public class Preferences extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
    public void onCreate( Bundle savedInstanceState ) {
    	// It needs to call before calling super.onCreate
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        DynamicAppUtils.fixDisplayOrientation(this);

        super.onCreate( savedInstanceState );
        setPreferenceScreen(createPreferenceScreen());
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    @SuppressWarnings("deprecation")
	private PreferenceScreen createPreferenceScreen() {
    	 PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
    	 
    	 EditTextPreference editServer = new EditTextPreference(this);
    	 EditTextPreference editUserId = new EditTextPreference(this);
    	 EditTextPreference editPassword = new EditTextPreference(this);
    	 
    	 // For server address.
    	 editServer.setKey("ServerAddress");
    	 editServer.setDialogTitle("Server Address");
    	 editServer.setDialogMessage("Server Address");
    	 editServer.setTitle("サーバーアドレス");
    	 editServer.setSummary("サーバーのアドレスを設定します");
    	 screen.addPreference(editServer);
    	 
    	 // For user Id.
    	 editUserId.setKey("UserId");
    	 editUserId.setDialogTitle("User ID");
    	 editUserId.setDialogMessage("User ID");
    	 editUserId.setTitle("ユーザーID");
    	 editUserId.setSummary("ユーザーIDを設定します");
    	 screen.addPreference(editUserId);

    	 // For password.
    	 editPassword.setKey("Password");
    	 editPassword.setDialogTitle("Password");
    	 editPassword.setDialogMessage("Password");
    	 editPassword.setTitle("パスワード");
    	 editPassword.setSummary("パスワードを設定します");
    	 editPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    	 screen.addPreference(editPassword);
   	 
    	 return screen;
    }
}

