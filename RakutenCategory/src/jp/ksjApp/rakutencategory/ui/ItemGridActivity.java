package jp.ksjApp.rakutencategory.ui;

import java.util.ArrayList;

import jp.ksjApp.rakutencategory.Const;
import jp.ksjApp.rakutencategory.R;
import jp.ksjApp.rakutencategory.Util;
import jp.ksjApp.rakutencategory.api.RankingItemApiTask;
import jp.ksjApp.rakutencategory.api.SearchItemApiTask;
import jp.tksjApp.rakutencategory.data.ItemData;

import com.androidquery.AQuery;

import android.R.bool;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class ItemGridActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = ItemGridActivity.class.getSimpleName();

	private GridView mGridView;
	private String mSearchWord = "";
	private String mSearchGenre = "";

	// これ以上Itemがない場合はfalse
	private boolean mRequestFlag = true;

	// ランキングAPI
	private boolean mRankingApi = true;

	// スクロール中の判定
	private Boolean mScrolling = false;

	private ImageAdapter mImgAdapter;

	private ArrayList<ItemData> mBookDataList = new ArrayList<ItemData>();

	private static int mPageCount = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String title = null;
		final Bundle ext = getIntent().getExtras();
		if (ext != null) {
			mSearchWord = ext.getString(Const.INTENT_KEY_SEARCH_WORD);
			mSearchGenre = ext.getString(Const.INTENT_KEY_SEARCH_GENRE_ID);
			title = ext.getString(Const.INTENT_KEY_SEARCH_GENRE_TITLE);
		}

		if (TextUtils.isEmpty(title)) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		} else {
			setTitle(title);
		}

		setContentView(R.layout.activity_grid);

		mGridView = (GridView) findViewById(R.id.gridView);
		mGridView.setOnScrollListener(new GridViewOnScrollListener());
		startApiTask(1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPageCount = 1;
		mRequestFlag = true;
		mRankingApi = true;
	}

	private void startApiTask(int page) {
		if (!mRequestFlag) {
			return;
		}

		if (TextUtils.isEmpty(mSearchWord)) {
			if (mRankingApi) {
				requestRankingApi(page);
			} else {
				requestSearchItemApi(page);
			}
		} else {
			requestSearchItemApi(page);
		}
	}

	/**
	 * 商品検索APIからデータを取得
	 * 
	 * @param page
	 */
	private void requestSearchItemApi(int page) {
		final SearchItemApiTask task = new SearchItemApiTask(mSearchWord,
				mSearchGenre, page) {
			private ProgressDialog mDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mDialog = new ProgressDialog(ItemGridActivity.this);
				mDialog.setTitle("通信中");
				mDialog.setMessage("商品情報取得中...");
				mDialog.show();
			}

			public void onPostExecute(final ArrayList<ItemData> result) {
				if (result == null) {
					if (Const._DEBUG_) {
						Log.d(TAG, "通信失敗");
					}
				} else if (result.size() <= 0) {
					if (Const._DEBUG_) {
						Log.d(TAG, "該当商品なし");
						Log.d(TAG, "mPageCount : " + mPageCount);
					}
					if (mPageCount == 1) {
						showNotItemDialog();
					} else {
						showNotMoreItemDialog();
					}
				} else {
					// API取得成功
					for (ItemData data : result) {
						mBookDataList.add(data);
					}
					if (mImgAdapter == null) {
						mImgAdapter = new ImageAdapter(getApplicationContext(),
								mBookDataList);
						mGridView.setAdapter(mImgAdapter);
					} else {
						// アダプターにリストデータ変更の通知
						mImgAdapter.notifyDataSetChanged();
						// GridViewの再描画
						mGridView.invalidateViews();
					}
				}

				if (mDialog != null) {
					mDialog.dismiss();
				}
			}

		};
		task.execute();
	}

	/**
	 * ランキングAPIからデータを取得
	 * 
	 * @param page
	 */
	private void requestRankingApi(int page) {
		final RankingItemApiTask task = new RankingItemApiTask(mSearchGenre,
				page) {
			private ProgressDialog mDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mDialog = new ProgressDialog(ItemGridActivity.this);
				mDialog.setTitle("通信中");
				mDialog.setMessage("商品情報取得中...");
				mDialog.show();
			}

			public void onPostExecute(final ArrayList<ItemData> result) {
				if (result == null) {
					if (Const._DEBUG_) {
						Log.d(TAG, "通信失敗");
					}
				} else if (result.size() <= 0) {
					if (Const._DEBUG_) {
						Log.d(TAG, "該当商品なし");
						Log.d(TAG, "mPageCount : " + mPageCount);
					}
					if (mPageCount == 1) {
						if (mRankingApi) {
							//TODO
							mRankingApi = false;
							startApiTask(mPageCount);
						} else {
							showNotItemDialog();
						}

					} else {
						showNotMoreItemDialog();
					}
					mRequestFlag = false;
				} else {
					// API取得成功
					for (ItemData data : result) {
						mBookDataList.add(data);
					}
					if (mImgAdapter == null) {
						mImgAdapter = new ImageAdapter(getApplicationContext(),
								mBookDataList);
						mGridView.setAdapter(mImgAdapter);
					} else {
						// アダプターにリストデータ変更の通知
						mImgAdapter.notifyDataSetChanged();
						// GridViewの再描画
						mGridView.invalidateViews();
					}
				}

				if (mDialog != null) {
					mDialog.dismiss();
				}
			}

		};
		task.execute();
	}

	/**
	 * アイテムがない場合にダイアログを表示する
	 */
	private void showNotItemDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("エラー");
		// アラートダイアログのメッセージを設定します
		alertDialogBuilder.setMessage("該当商品はありませんでした。\n"
				+ "【ヒント】一つ上のジャンルでランキングをご覧ください。");
		// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		final AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}

	/**
	 * アイテムがない場合にダイアログを表示する
	 */
	private void showNotMoreItemDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("エラー");
		// アラートダイアログのメッセージを設定します
		alertDialogBuilder.setMessage("該当商品はありませんでした。");
		// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		final AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}

	/**
	 * 　再度リクエストしてこれ以上アイテムがない場合に表示するダイアログを生成する。
	 * 
	 * @author mtb_cc_sin5
	 * 
	 */
	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<ItemData> mItemData;
		private LayoutInflater mInflater;

		public ImageAdapter(Context c, ArrayList<ItemData> itemData) {
			mContext = c;
			mItemData = itemData;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mItemData.size();
		}

		public Object getItem(int position) {
			return mItemData.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final AQuery aq;
			if (convertView == null || convertView.getId() != position) {
				convertView = mInflater.inflate(R.layout.grid_item_image,
						parent, false);
				aq = new AQuery(convertView);
				convertView.setTag(aq);
			} else {
				aq = (AQuery) convertView.getTag();
			}

			final ItemData item = (ItemData) mItemData.get(position);
			if (item == null) {
				return convertView;
			}
			final String imgUrl = item.thumbnailUrl;
			aq.id(R.id.img_thumbnail).image(imgUrl, true, true);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Util.showItemDialog(ItemGridActivity.this, item);
				}
			});

			return convertView;
		}
	}

	/*
	 * スクローラーの状態検知
	 */
	public class GridViewOnScrollListener implements OnScrollListener {

		/*
		 * ステータスが変わった時
		 * 
		 * @see
		 * android.widget.AbsListView.OnScrollListener#onScrollStateChanged(
		 * android.widget.AbsListView, int)
		 */
		@Override
		public void onScrollStateChanged(AbsListView paramAbsListView,
				int scrollState) {
			switch (scrollState) {

			// スクロール停止
			case OnScrollListener.SCROLL_STATE_IDLE:

				// decode処理をqueueに登録して開始する記述
				mScrolling = false;

				// アダプターにデータ変更の通知
				mImgAdapter.notifyDataSetChanged();
				// GridViewの再描画
				mGridView.invalidateViews();

				break;

			// スクロール
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				// decodeのqueueをキャンセルする処理を記述
				mScrolling = true;
				break;

			// フリック
			case OnScrollListener.SCROLL_STATE_FLING:
				// decodeのqueueをキャンセルする処理を記述
				mScrolling = true;
				break;

			default:
				break;
			}
		}

		/*
		 * スクロール中
		 * 
		 * @see
		 * android.widget.AbsListView.OnScrollListener#onScroll(android.widget
		 * .AbsListView, int, int, int)
		 */
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			// 現在表示されているリストの末尾番号
			int displayCount = firstVisibleItem + visibleItemCount;

			/*
			 * 
			 * 初期でdisplayCountに数値が入ってるのに、totalItemCountが0の場合があるためスクロール中のみ判定するようにする
			 * 。
			 */
			if (displayCount < totalItemCount || !mScrolling) {
				return;
			}

			mPageCount++;

			// リストにデータ追加
			startApiTask(mPageCount);

		}

	}
}
