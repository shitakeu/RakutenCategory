package jp.tksjApp.rakutencategory.data;

/**
 * 商品格納クラス
 * 
 * @author shitakeu
 * 
 */
public class ItemData {
	// 商品紹介URL
	public String url;
	// アフリエイト用商品紹介URL
	public String affiliateUrl;
	// 商品名
	public String name;
	// ISBN
	public String isbn;
	//出版社
	public String publisherName;
	//著者名
	public String author;
	// 説明（あれば）
	public String description;
	// サムネイル画像
	public String thumbnailUrl;
	// レビュー
	public float reviewRate = -1;
	// レビュー数
	public String reviewCount = "";
	// 値段
	public String price = "";
}
