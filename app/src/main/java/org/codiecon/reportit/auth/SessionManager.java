package org.codiecon.reportit.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.codiecon.reportit.LoginActivity;
import org.codiecon.reportit.NearbyIssueActivity;
import org.codiecon.reportit.constant.SystemConstant;
import org.codiecon.reportit.models.UserPrincipal;

public class SessionManager {

    private static final String PREFERENCE_NAME = "ReportIT";

    private static int PRIVATE_MODE = 0;

    private Context context;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    public SessionManager(Context _context) {
        this.context = _context;
        this.preferences = _context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        this.editor = this.preferences.edit();
    }

    public void onSuccessfulAuthentication(UserPrincipal principal){
        this.editor.putBoolean(SystemConstant.IS_AUTHENTICATED, true);
        this.editor.putString(SystemConstant.USERNAME, principal.getName());
        this.editor.putString(SystemConstant.USER_ID, principal.getIdentifier());
    }

    private boolean isAuthenticated(){
        return this.preferences.getBoolean(SystemConstant.IS_AUTHENTICATED, false);
    }

    public void authenticate(){
        if(!this.isAuthenticated()){
            Intent intent = new Intent(this.context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(intent);
        }
        Intent intent = new Intent(this.context, NearbyIssueActivity.class);
        this.context.startActivity(intent);
    }

    public void signOut(){
        this.editor.clear();
        this.editor.commit();
        this.authenticate();
    }
}
