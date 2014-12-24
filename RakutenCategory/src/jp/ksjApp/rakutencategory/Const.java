package jp.ksjApp.rakutencategory;

public class Const {

	public static final boolean _DEBUG_ = true;
	
	public static final int API_TIMEOUT = 30000;

	// Google Suggest API
	public static final String SUGGEST_API = "http://www.google.com/complete/search?hl=jp&output=toolbar&q=";

	/**
	 * 楽天API関連
	 */
	public static final String RAKUTEN_APP_ID = "1064840937862996285";
	public static final String RAKUTEN_AFFILIATEI_ID = "117fe8b8.858f41ba.117fe8b9.90504b56";

	// 楽天ジャンル検索API 
	// https://webservice.rakuten.co.jp/api/ichibagenresearch/
	public static final String RAKUTEN_GENRE_API_URL = "https://app.rakuten.co.jp/services/api/IchibaGenre/Search/20120723?format=xml&applicationId="
			+ RAKUTEN_APP_ID + "&affiliateId=" + RAKUTEN_AFFILIATEI_ID;

	// 楽天ランキングAPI
	public static final String RAKUTEN_RANLING_API_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Ranking/20120927?format=xml&applicationId="
			+ RAKUTEN_APP_ID + "&affiliateId=" + RAKUTEN_AFFILIATEI_ID;
	
	//楽天商品検索API
	public static final String RAKUTEN_SEARCH_API_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20130805?format=xml&applicationId="
			+ RAKUTEN_APP_ID + "&affiliateId=" + RAKUTEN_AFFILIATEI_ID;

	// Intent Key
	public static final String INTENT_KEY_SEARCH_WORD = "search_word";
	public static final String INTENT_KEY_SEARCH_GENRE_ID = "search_genre_id";
	public static final String INTENT_KEY_SEARCH_GENRE_TITLE = "search_genre_title";

}
