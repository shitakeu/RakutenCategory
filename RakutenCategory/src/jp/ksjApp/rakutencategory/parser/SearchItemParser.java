package jp.ksjApp.rakutencategory.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jp.tksjApp.rakutencategory.data.ItemData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.TextUtils;
import android.util.Log;

/**
 * 楽天商品ランキングAPI 用Parser
 * 
 * @author shitakeu
 * 
 */
public class SearchItemParser {

	@SuppressWarnings("unused")
	private static final String TAG = SearchItemParser.class.getSimpleName();

	public ArrayList<ItemData> xmlParser(InputStream is) {
		XmlPullParser parser = null;
		final ArrayList<ItemData> result = new ArrayList<ItemData>();
		ItemData currentMsg = null;

		try {
			parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(is, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tag = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("Items")) {
					} else if (tag.equals("Item")) {
						currentMsg = new ItemData();
					} else if (currentMsg != null) {
						if (tag.equals("itemUrl")) {
							currentMsg.url = parser.nextText();
						} else if (tag.equals("affiliateUrl")) {
							currentMsg.url = parser.nextText();
						} else if (tag.equals("itemName")) {
							currentMsg.name = parser.nextText();
						} else if (tag.equals("itemPrice")) {
							currentMsg.price = parser.nextText();
						} else if (tag.equals("imageUrl")
								&& TextUtils.isEmpty(currentMsg.thumbnailUrl)) {
							currentMsg.thumbnailUrl = parser.nextText();
						} else if (tag.equals("reviewAverage")) {
							currentMsg.reviewRate = Float.parseFloat(parser
									.nextText());
						} else if (tag.equals("reviewCount")) {
							currentMsg.reviewCount = parser.nextText();
						}
					}
					break;

				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if (tag.equals("Item")) {
						result.add(currentMsg);
						currentMsg = null;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "XmlPullParserException : " + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e(TAG, "IOException : " + e.getMessage());
			return null;
		}
		return result;
	}
}
