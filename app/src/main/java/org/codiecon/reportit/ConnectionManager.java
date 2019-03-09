package org.codiecon.reportit;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConnectionManager extends Application {

    private static final String BASE_URI = "http://8d67081e.ngrok.io/backend";

    private static ConnectionManager manager;

    private Retrofit retrofit;

    private ConnectionManager() {
        this.retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URI)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    public static synchronized Retrofit instance() {
        if (manager == null) {
            manager = new ConnectionManager();
        }
        return manager.retrofit;
    }
}
