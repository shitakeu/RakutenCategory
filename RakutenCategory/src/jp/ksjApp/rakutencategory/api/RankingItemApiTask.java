package jp.ksjApp.rakutencategory.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

import jp.ksjApp.rakutencategory.Const;
import jp.ksjApp.rakutencategory.parser.RankingParser;
import jp.ksjApp.rakutencategory.parser.RankingParser;
import jp.tksjApp.rakutencategory.data.ItemData;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

/**
 * 楽天ランキングAPI
 * 
 * @author mtb_cc_sin5
 * 
 */
public class RankingItemApiTask extends
		AsyncTask<String, Void, ArrayList<ItemData>> {

	private static final String TAG = RankingItemApiTask.class.getSimpleName();

	private String mSearchGenre = "0";
	private int mPage = 1;

	public RankingItemApiTask(String searchGenre, int page) {
		mSearchGenre = searchGenre;
		mPage = page;
	}

	@Override
	protected ArrayList<ItemData> doInBackground(String... params) {
		return requestApi();
	}

	private ArrayList<ItemData> requestApi() {

		ArrayList<ItemData> data = null;
		final HttpClient httpClient = new DefaultHttpClient();
		try {
			final HttpGet request = new HttpGet(createApiUrl());
			final HttpResponse httpResponse = httpClient.execute(request);
			final InputStream in = httpResponse.getEntity().getContent();
			final RankingParser rakutenParser = new RankingParser();
			data = rakutenParser.xmlParser(in);
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	private String createApiUrl() {
		final StringBuffer strbuf = new StringBuffer();

		strbuf.append(Const.RAKUTEN_RANLING_API_URL);
		strbuf.append("&");
		strbuf.append("genreId=" + URLEncoder.encode(mSearchGenre));
		strbuf.append("&");
		strbuf.append("page=" + mPage);

		if(Const._DEBUG_){
			Log.d(TAG, strbuf.toString());
		}

		return strbuf.toString();
	}

}
