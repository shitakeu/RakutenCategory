package jp.ksjApp.rakutencategory.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import jp.ksjApp.rakutencategory.Const;
import jp.ksjApp.rakutencategory.parser.GenreParser;
import jp.tksjApp.rakutencategory.data.GenreData;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class GenreApiTask extends AsyncTask<String, Void, ArrayList<GenreData>> {

	@SuppressWarnings("unused")
	private static final String TAG = GenreApiTask.class.getSimpleName();

	public GenreApiTask() {
	}

	@Override
	protected ArrayList<GenreData> doInBackground(String... params) {
		return requestApi(params[0]);
	}

	private ArrayList<GenreData> requestApi(String genreId) {

		ArrayList<GenreData> data = null;
		final HttpClient httpClient = new DefaultHttpClient();
		try {
			final HttpGet request = new HttpGet(createUrl(genreId));
			final HttpResponse httpResponse = httpClient.execute(request);
			final InputStream in = httpResponse.getEntity().getContent();
			final GenreParser rakutenParser = new GenreParser();
			data = rakutenParser.xmlParser(in);
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	private String createUrl(String genreId) {
		final StringBuffer strbuf = new StringBuffer();
		strbuf.append(Const.RAKUTEN_GENRE_API_URL);
		strbuf.append("&");
		strbuf.append("genreId=" + genreId);
		if(Const._DEBUG_){
			Log.d(TAG, "url : " + strbuf.toString());
		}
		return strbuf.toString();
	}
}
