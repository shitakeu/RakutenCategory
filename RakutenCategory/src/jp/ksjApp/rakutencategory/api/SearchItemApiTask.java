package jp.ksjApp.rakutencategory.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

import jp.ksjApp.rakutencategory.Const;
import jp.ksjApp.rakutencategory.parser.RankingParser;
import jp.ksjApp.rakutencategory.parser.RankingParser;
import jp.ksjApp.rakutencategory.parser.SearchItemParser;
import jp.tksjApp.rakutencategory.data.ItemData;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

/**
 * 楽天BookAPI
 * 
 * @author mtb_cc_sin5
 * 
 */
public class SearchItemApiTask extends
		AsyncTask<String, Void, ArrayList<ItemData>> {

	private static final String TAG = SearchItemApiTask.class.getSimpleName();

	private String mSearchWord = "";
	private String mSearchGenre = "0";
	private int mPage = 1;

	public SearchItemApiTask(String searchWord, String searchGenre, int page) {
		mSearchWord = searchWord;
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
			final SearchItemParser rakutenParser = new SearchItemParser();
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

		strbuf.append(Const.RAKUTEN_SEARCH_API_URL);
		if(!TextUtils.isEmpty(mSearchWord)){
			strbuf.append("&");
			strbuf.append("keyword=" + URLEncoder.encode(mSearchWord));
		}
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
