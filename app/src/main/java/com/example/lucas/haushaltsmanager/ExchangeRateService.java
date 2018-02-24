package com.example.lucas.haushaltsmanager;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


//code from: http://www.codexpedia.com/android/asynctask-and-httpurlconnection-sample-in-android/
public class ExchangeRateService extends IntentService {

    //TODO der service soll auch für weiter umrechnungs apis ausgelegt sein
    //außerdem soll er auch nur in bestimmte währungen umrechnen können und auch historische daten abfragen können
    //die daten kommen dann mit dem intent
    private static String URLPATH = "https://api.fixer.io/latest";
    private ExpensesDataSource database;
    private static String TAG = "ExchangeRateService";


    /**
     * Service um Umrechnungskurse aus dem Internet abzurufen und diese in die Datenbank zu schreiben
     */
    public ExchangeRateService() {

        super("ExchangeRateService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        database = new ExpensesDataSource(ExchangeRateService.this);
        database.open();

        try {

            URL url = new URL(URLPATH);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {

                //if no result
                //TODO den service nach einer halben stunde noch einmal starten
                stopSelf();
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                //hier wird das json object zu einem string convertiert
                buffer.append(line + "\n");
            }

            if (buffer.length() != 0) {

                try {

                    JSONObject json = new JSONObject(buffer.toString());

                    long baseCurId = database.getCurrencyId(json.getString("base"));
                    String downloadDate = json.getString("date");

                    JSONObject exchangeRates = json.getJSONObject("rates");

                    Iterator<?> currencies = exchangeRates.keys();
                    while (currencies.hasNext()) {

                        String currency = (String) currencies.next();
                        long toCurId = database.getCurrencyId(currency);
                        database.createExchangeRate(baseCurId, toCurId, exchangeRates.getDouble(currency), downloadDate);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            } else {

                stopSelf();
            }

        } catch (IOException e) {

            Log.e(TAG, "Error ", e);
            //TODO nach einer halben stunde noch einmal probieren
            stopSelf();
        } finally {

            if (urlConnection != null) {

                urlConnection.disconnect();
            }

            if (reader != null) {

                try {
                    reader.close();
                } catch (final Exception e) {

                    Log.e(TAG, "Error while closing stream ", e);
                }
            }

            database.close();
            stopSelf();
        }
    }
}
