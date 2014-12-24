package jp.ksjApp.rakutencategory.ui;

import java.util.ArrayList;

import com.androidquery.AQuery;

import jp.ksjApp.rakutencategory.Const;
import jp.ksjApp.rakutencategory.R;
import jp.ksjApp.rakutencategory.Util;
import jp.ksjApp.rakutencategory.R.id;
import jp.ksjApp.rakutencategory.R.layout;
import jp.ksjApp.rakutencategory.api.GenreApiTask;
import jp.tksjApp.rakutencategory.data.ItemData;
import jp.tksjApp.rakutencategory.data.GenreData;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getSimpleName();

	// 検索するジャンル
	private String mSearchGenreId = "0";

	private String mSearchGenreTitle = "すべて";

	private GridView mGridView;

	private static final String INTENT_GENRE_KEY_ID = "intent_genre_id";
	private static final String INTENT_GENRE_KEY_TITLE = "intent_genre_title";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final String genre = getIntent().getStringExtra(INTENT_GENRE_KEY_ID);
		if (genre != null) {
			mSearchGenreId = genre;
		}

		final String genreTitle = getIntent().getStringExtra(
				INTENT_GENRE_KEY_TITLE);
		if (genreTitle != null) {
			mSearchGenreTitle = genreTitle;
		}
		setTitle(mSearchGenreTitle);

		if (!Util.isNetworkAvailable(getApplicationContext())) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle("エラー");
			// アラートダイアログのメッセージを設定します
			alertDialogBuilder
					.setMessage("ネットワークに接続できません。¥n接続してから再度アプリを起動してください。");
			// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
			alertDialogBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			alertDialogBuilder.create().show();
		}
		setupView();
		requstGenre();
	}

	private void setupView() {
		final EditText editText = (EditText) findViewById(R.id.edit_word);
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (event != null
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					if (event.getAction() == KeyEvent.ACTION_UP) {
						final String searchWord = editText.getText().toString();
						final Intent intent = new Intent(MainActivity.this,
								ItemGridActivity.class);
						intent.putExtra(Const.INTENT_KEY_SEARCH_WORD,
								searchWord);
						intent.putExtra(Const.INTENT_KEY_SEARCH_GENRE_ID,
								mSearchGenreId);
						intent.putExtra(Const.INTENT_KEY_SEARCH_GENRE_TITLE,
								mSearchGenreTitle);
						startActivity(intent);
					}
					return true;
				}
				return false;
			}
		});

		findViewById(R.id.btn_ranking).setOnClickListener(this);
		findViewById(R.id.btn_search).setOnClickListener(this);
	}

	/**
	 * ジャンル一覧を取得する
	 */
	private void requstGenre() {
		if (mGridView == null) {
			mGridView = (GridView) findViewById(R.id.grd_category);
		}

		final GenreApiTask task = new GenreApiTask() {
			private ProgressDialog mDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mDialog = new ProgressDialog(MainActivity.this);
				mDialog.setTitle("通信中");
				mDialog.setMessage("商品情報取得中...");
				mDialog.show();
			}

			public void onPostExecute(final ArrayList<GenreData> result) {
				if (result == null) {
					final String[] list = { "通信失敗" };
					final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							MainActivity.this,
							android.R.layout.simple_list_item_1, list);
					mGridView.setAdapter(adapter);
				} else if (result.size() <= 0) {
					// 現在のジャンル以下の子ジャンルはない
					final Intent intent = new Intent(MainActivity.this,
							ItemGridActivity.class);
					intent.putExtra(Const.INTENT_KEY_SEARCH_GENRE_ID,
							mSearchGenreId);
					intent.putExtra(Const.INTENT_KEY_SEARCH_GENRE_TITLE,
							mSearchGenreTitle);
					startActivity(intent);
					finish();
				} else {

					final GenreAdapter adapter = new GenreAdapter(
							MainActivity.this, result);
					mGridView.setAdapter(adapter);
					mGridView.setEnabled(true);
					mGridView
							.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
								@Override
								public void onItemSelected(
										AdapterView<?> parent, View view,
										int position, long id) {
									// 選択されたアイテムを取得します
									if (position != 0) {
										mSearchGenreId = result.get(position).id;
									}
								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
								}
							});
				}
				if (mDialog != null) {
					mDialog.dismiss();
				}
			}
		};
		task.execute(mSearchGenreId);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.btn_search) {
			if (!Util.isNetworkAvailable(getApplicationContext())) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);
				alertDialogBuilder.setTitle("エラー");
				// アラートダイアログのメッセージを設定します
				alertDialogBuilder
						.setMessage("ネットワークに接続できません。¥n接続してから再度検索してください。");
				// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
				alertDialogBuilder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialogBuilder.create().show();
				return;
			}

			final EditText editText = (EditText) findViewById(R.id.edit_word);
			final String searchWord = editText.getText().toString();

			final Intent intent = new Intent(MainActivity.this,
					ItemGridActivity.class);
			intent.putExtra(Const.INTENT_KEY_SEARCH_WORD, searchWord);
			intent.putExtra(Const.INTENT_KEY_SEARCH_GENRE_ID, mSearchGenreId);
			intent.putExtra(Const.INTENT_KEY_SEARCH_GENRE_TITLE, mSearchGenreTitle);
			startActivity(intent);
		} else if (id == R.id.btn_ranking) {
			final Intent intent = new Intent(MainActivity.this,
					ItemGridActivity.class);
			intent.putExtra(Const.INTENT_KEY_SEARCH_GENRE_ID, mSearchGenreId);
			intent.putExtra(Const.INTENT_KEY_SEARCH_GENRE_TITLE, mSearchGenreTitle);
			startActivity(intent);
		}
	}

	/**
	 * 
	 * @author mtb_cc_sin5
	 * 
	 */
	public class GenreAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<GenreData> mGenreData;
		private LayoutInflater mInflater;

		public GenreAdapter(Context c, ArrayList<GenreData> genreData) {
			mContext = c;
			mGenreData = genreData;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mGenreData.size();
		}

		public Object getItem(int position) {
			return mGenreData.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final TextView tv;
			if (convertView == null || convertView.getId() != position) {
				convertView = mInflater.inflate(R.layout.grid_item_text,
						parent, false);
				tv = (TextView) convertView.findViewById(R.id.txt_genre);
				convertView.setTag(tv);
			} else {
				tv = (TextView) convertView.getTag();
			}

			final GenreData item = (GenreData) mGenreData.get(position);
			if (item == null) {
				return convertView;
			}
			tv.setText(item.name);

			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final Intent intent = new Intent(MainActivity.this,
							MainActivity.class);
					intent.putExtra(INTENT_GENRE_KEY_ID, item.id);
					intent.putExtra(INTENT_GENRE_KEY_TITLE, mSearchGenreTitle
							+ " > " + item.name);
					startActivity(intent);
				}
			});

			return convertView;
		}
	}
}
