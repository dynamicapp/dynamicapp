package jp.zyyx.dynamicapp;

import jp.zyyx.dynamicapp.utilities.Utilities;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.view.Window;
import android.view.WindowManager;

/*
 * Copyright (C) 2014 ZYYX, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Preferences extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
    public void onCreate( Bundle savedInstanceState ) {
    	// It needs to call before calling super.onCreate
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utilities.fixDisplayOrientation(this);

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

