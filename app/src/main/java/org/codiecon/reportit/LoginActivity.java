package org.codiecon.reportit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.codiecon.reportit.models.request.ReportIssueRequest;
import org.codiecon.reportit.outbound.IssueService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/passwordView.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText passwordView;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.usernameView = findViewById(R.id.username);
        this.passwordView = findViewById(R.id.password);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    _loginWithCredentials();
                    return true;
                }
                return false;
            }
        });

        Button loginButton = (Button) findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                _loginWithCredentials();
            }
        });

        Button loginWithFacebook = (Button) findViewById(R.id.sign_in_with_facebook);
        loginWithFacebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                _loginWithFacebook();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void _loginWithCredentials() {
        // Reset errors.
        usernameView.setError(null);
        passwordView.setError(null);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        Call<Void> response = ConnectionManager.instance()
            .create(IssueService.class)
            .save(new ReportIssueRequest());

        response.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                response.body();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                call.request();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void _loginWithFacebook() {

    }

}

