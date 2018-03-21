package com.example.lucas.haushaltsmanager.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.EntityNotExistingException;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

//code from: http://www.codexpedia.com/android/asynctask-and-httpurlconnection-sample-in-android/
public class GetExchangeRatesService extends IntentService {

    static String TAG = GetExchangeRatesService.class.getSimpleName();

    static String URLPATH = "https://api.fixer.io/latest";
    ExpensesDataSource mDatabase;

    //wenn der service von einer API eine neue währung bekommen sollte dann soll diese auch in die Datenbank geschrieben werden
    //  --> wo bekomme ich das Währungssymbol her
    //  --> wo bekomme ich den vollen Namen der Währung her
    //  --> wo bekomme ich den Kurznamen der Währung her

    //Der Service soll manuell ausgelöst werden können
    //  --> bei manueller aktivierung soll der service in der lage sein historische daten abzufragen (Umrechnungskurse zu einem bestimmten datum)
    //  --> bei manueller aktivierung soll der service in der lage sein einen bestimmten umrechnungskurs abzufragen

    //Der Service soll mit mehreren API's arbeiten können
    //  --> wann soll welche API benutzt werden
    //  --> was passiert wenn der User keine internetverbindung erlaubt
    //  --> verschiedene HTTP statuscodes müssen erkannt und behandelt werden (pay, not reachable, ...)

    /**
     * Service um Umrechnungskurse aus dem Internet abzurufen und diese in die Datenbank zu schreiben
     */
    public GetExchangeRatesService() {

        super("ExchangeRateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDatabase = new ExpensesDataSource(GetExchangeRatesService.this);
        mDatabase.open();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {
            JSONObject jsonResponse = getDataFromApi(new URL(URLPATH));

            Currency baseCurrency = extractBaseCurrency(jsonResponse);
            Calendar downloadDate = extractUpdateTime(jsonResponse);
            Map<Currency, Double> exchangeRates = transformJsonExchangeRates(extractRates(jsonResponse));

            createExchangeRates(exchangeRates, baseCurrency, downloadDate);

        } catch (MalformedURLException e) {

            Log.e(TAG, "onHandleIntent: Error while querying the API Data.", e);
        } catch (JSONException e) {

            Log.e(TAG, "onHandleIntent: Error while parsing the response json.", e);
        } catch (ParseException e) {

            Log.e(TAG, "onHandleIntent: Error while parsing the date", e);
        }

        stopServiceExecution(1800000L);//30 minutes
    }

    /**
     * Methode um die abgefragten Daten von einer API in einem JSON format zu erhalten
     *
     * @param url Server url
     * @return Response als JsonObject
     */
    private JSONObject getDataFromApi(URL url) {

        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            assertHttpStatusOk(urlConnection.getResponseCode());

            InputStream inputStream = urlConnection.getInputStream();
            closeUrlConnection(urlConnection);

            return transformInputToJson(inputStream);
        } catch (IOException e) {

            Log.e(TAG, "getDataFromApi: Could not retrieve data from server " + url.getQuery(), e);
            closeUrlConnection(urlConnection);
            stopServiceExecution(null);
            return null;
        }
    }

    /**
     * Methode die prüft ob durch die API eine error status code zurückgegeben wurde.
     * Falls den so ist wird der Service gestopt und eine neue Abfrage in 30 min. geplant.
     *
     * @param httpCode Zurückgegebener Statuscode
     */
    private void assertHttpStatusOk(int httpCode) {

        if (httpCode != 200) {

            Log.d(TAG, "resolveHttpStatusCode: Die API hat einen Fehlerstatuscode zurückgegeben. Service stoppen und neu schedulen!");
            stopServiceExecution(1800000L);//30 minutes
        }
    }

    /**
     * Methode um eine HttpUrlConnection zu schließen
     *
     * @param connection Url verbindung
     */
    private void closeUrlConnection(HttpsURLConnection connection) {

        if (connection != null)
            connection.disconnect();

    }

    /**
     * Methode um eine Response von einer API query in ein JsonObject umzuwandeln
     *
     * @param inputStream Response der API
     * @return Response als JSONObject
     */
    private JSONObject transformInputToJson(InputStream inputStream) {

        BufferedReader bufferedReader = null;
        try {

            if (inputStream == null)
                stopServiceExecution(1800000L);//30 minutes

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {

                stringBuilder.append(line).append("\n");
            }

            return new JSONObject(stringBuilder.toString());
        } catch (IOException e) {

            Log.e(TAG, "transformInputToJson: Error while ", e);
        } catch (JSONException e) {

            Log.e(TAG, "transformInputToJson: Error while parsing the api response", e);
        }

        closeBufferedReader(bufferedReader);
        stopServiceExecution(1800000L);//30 minutes
        return null;
    }

    /**
     * Methode um einen BuffredReader zu schließen
     *
     * @param reader BufferedReader
     */
    private void closeBufferedReader(BufferedReader reader) {

        if (reader != null) {

            try {
                reader.close();
            } catch (Exception e) {

                Log.e(TAG, "closeBufferedReader: Error while closing stream", e);
            }
        }
    }

    /**
     * Methode um die Base currency aus einem JSONObject zu extrahieren
     *
     * @param response JSON response
     * @return Base Währung
     */
    private Currency extractBaseCurrency(JSONObject response) throws JSONException {

        String baseCurrencyShortName = response.getString("base");
        return mDatabase.getCurrency(baseCurrencyShortName);
    }

    /**
     * Methode um ein Datum aus einem JSONObject zu extrahieren
     *
     * @param response JSON response
     * @return Datum
     */
    private Calendar extractUpdateTime(JSONObject response) throws JSONException, ParseException {

        Calendar downloadDate = Calendar.getInstance();

        String dateString = response.getString("date");
        downloadDate.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString));

        return downloadDate;
    }

    /**
     * Methode um ein JSONObject mit Währungen und Umtauschkursen in eine Map(Währung, Umtauschkurs) umzuwandeln
     *
     * @param exchangeRatesJson JSON response
     * @return Map
     */
    private Map<Currency, Double> transformJsonExchangeRates(JSONObject exchangeRatesJson) throws JSONException {

        Map<Currency, Double> ratesToBase = new HashMap<>();
        while (exchangeRatesJson.keys().hasNext()) {

            String currencyShortName = exchangeRatesJson.keys().next();
            Currency toCurrency = mDatabase.getCurrency(currencyShortName);
            double rateToBase = exchangeRatesJson.getDouble(currencyShortName);
            ratesToBase.put(toCurrency, rateToBase);
        }

        return ratesToBase;
    }

    /**
     * Methode unm das Json object mit den Wechselkursen zu extrahieren
     *
     * @param response JSON response
     * @return Wechselkusre im JSON format
     */
    private JSONObject extractRates(JSONObject response) throws JSONException {

        return response.getJSONObject("rates");
    }

    /**
     * Methode um Umtauschkurse zu einer bestimmten Währung in die Datenbank zu schreiben
     *
     * @param ratesToBase Umtauschkurse (Zielwährung, Umtauschkurs)
     */
    private void createExchangeRates(Map<Currency, Double> ratesToBase, Currency fromCurrency, Calendar downloadTime) {

        for (Map.Entry<Currency, Double> rateToBase : ratesToBase.entrySet()) {

            Currency toCurrency = rateToBase.getKey();
            double exchangeRate = rateToBase.getValue();

            createNewExchangeRate(fromCurrency.getIndex(), toCurrency.getIndex(), exchangeRate, downloadTime.getTimeInMillis());
        }
    }

    /**
     * Methode um einen Wechselkurs in die Datenbank zu schreiben
     *
     * @param fromCurId           Id der Ausgangswährung
     * @param toCurId             Id der Zielwährung
     * @param exchangeRate        Wechselkurs
     * @param downloadTimeInMills Update Zeit der Währungskurses
     */
    private void createNewExchangeRate(long fromCurId, long toCurId, double exchangeRate, long downloadTimeInMills) {

        try {

            mDatabase.createExchangeRate(fromCurId, toCurId, exchangeRate, downloadTimeInMills);
        } catch (EntityNotExistingException e) {

            Log.d(TAG, "createNewExchangeRate: " + e.getMessage());
        }
    }

    /**
     * Methode um die Ausführung des Services zu stoppen und bei bedarf nach einer bestimmten Zeit wieder neu zu starten.
     *
     * @param restartInMillis Starte den Service in ... Millisekunden neu
     */
    private void stopServiceExecution(Long restartInMillis) {

        if (restartInMillis != null)
            //todo starte den service in "restartInMillis" millisekunden neu
            ;

        mDatabase.close();
        stopSelf();
    }
}
