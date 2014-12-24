package jp.ksjApp.rakutencategory;

import jp.tksjApp.rakutencategory.data.ItemData;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;

import com.androidquery.AQuery;

public class Util {
	private static final String TAG = Util.class.getSimpleName();

	public static void showItemDialog(final Context context, final ItemData item) {
		final LayoutInflater factory = LayoutInflater
				.from(context);
		final View layoutForDialog = factory.inflate(R.layout.dialog_item,
				null);
		final AQuery aq = new AQuery(layoutForDialog);
		aq.id(R.id.txt_title).text(item.name);
		aq.id(R.id.txt_author).text(item.author);
		aq.id(R.id.txt_price).text(item.price + "円");
		final String imgUrl = item.thumbnailUrl;
		aq.id(R.id.img_thumbnail).image(imgUrl, true, true);

		final RatingBar bar = (RatingBar) layoutForDialog
				.findViewById(R.id.bar_review);
		final float reviewRat = item.reviewRate;
		if ( 0 < reviewRat && reviewRat <= 5) {
			bar.setVisibility(View.VISIBLE);
			bar.setRating(reviewRat);
		} else {
			bar.setVisibility(View.GONE);
		}
		
		layoutForDialog.findViewById(R.id.btn_web).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						final String urlString;
						if (TextUtils.isEmpty(item.affiliateUrl)) {
							urlString = item.url;
						} else {
							urlString = item.affiliateUrl;
						}
						if (!TextUtils.isEmpty(urlString)) {
							if(Const._DEBUG_){
								Log.d(TAG, "url : " + urlString);
							}
							final Uri uri = Uri.parse(urlString);
							final Intent intent = new Intent(
									Intent.ACTION_VIEW, uri);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent);
						}
					}
				});

		final AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setView(layoutForDialog);
		dlg.show();
	}

	/**
	 * ネットワーク接続確認 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context){
		final ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni == null){
			return false;
		}
		return ni.isConnected();
	}
	
}
