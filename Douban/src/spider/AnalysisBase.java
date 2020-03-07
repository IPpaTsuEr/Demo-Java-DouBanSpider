package spider;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class AnalysisBase {
	String movieId="";
	int waittime=10000,holdTime=1000,UrlAna=1,ComAna=2,RevAna=3;
	boolean Bande=false,stop=false,sleep=false;
	
	Parser parser;
	LinkedTempList<MovieinfoStorage> il;
	LinkedTempList<CommentStorage> cl;
	LinkedTempList<ReviewStorage> rl;
	LinkedTempList<FileStorage> fl;
	
	LinkedList<Integer> map;
	
	URLList ful;
	URLList mul;
	URLList cul;
	URLList rul;
	
	IOBase IO;
	
	private String[] userAgant={
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0)",
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1) Gecko/20100101 Firefox/41.0",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)"
	};
	
	public int random(int low,int high){
		Random random = new Random();
		return random.nextInt(high)+low;
	}
	public boolean getBanStatu(){
		return Bande;
	}
	public void setBanStatu(boolean var){
		Bande=var;
	}
	public String  getNextAddress(int type) {
		try {
			if(parser==null)return null;
			parser.reset();
			Node nextPageNode = (Node) parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("a"),new HasAttributeFilter("class","next"))).elementAt(0);
			LinkTag nextLink=(LinkTag)nextPageNode;
			if(type==ComAna){
				if(nextLink!=null){
					return getMovieCommentAddress()+nextLink.getAttribute("href").replaceAll("amp;", "");
				}
				else{
					return getMovieCommentAddress()+"?start=2147483646&limit=-20&sort=new_score";
				}
			}
			else if(type==RevAna){
				if(nextLink!=null){
					return getMovieReviewAddress()+nextLink.getAttribute("href").replaceAll("amp;", "");
				}
				else{
					return getMovieReviewAddress()+"?start=2147483646&limit=-20&sort=new_score";
				}
			}
			else return null;
			
		} catch (Exception e) {
			if(type==ComAna){
				IO.error("获取"+movieId+"的下一页Comment（短评）地址时出错，详细信息："+e);
			}
			else  if(type==RevAna){
				IO.error("获取"+movieId+"的下一页Review（影评）地址时出错，详细信息："+e);
			}
				return null;
		}
		
	}
	public int getNextSize(String nextString,String replaceStr){
		if (nextString==null)return -1;
		try{
			String temp=nextString.replaceAll(replaceStr,"").replaceAll("start=","");
			int i;
			for(i=0;i<temp.length();i++){
				if('&'==temp.charAt(i)){break;}
			}
			i=Integer.parseInt(temp.substring(1, i));
			return i;
		}catch(Exception e)
		{return -1;}
	}
	public int getTotalSize(Parser parser) {
		NodeList totalList;
		try {
			parser.reset();
			totalList = parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("span"),new HasAttributeFilter("class","total")));
			Node totalNode=(Node)totalList.elementAt(0);
			if(totalNode!=null){
				String totalString=totalNode.toPlainTextString().replaceAll(" ", "").replace("(共","").replace("条)", "");
				return Integer.parseInt(totalString);
			}
			else {return -2;}
		} catch (ParserException e) {return -1;}
		
	}

	public void setParser(String url,int type){
		parser=null;
		try {
			HttpURLConnection conn=(HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-agent",userAgant[random(0,5)]);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setConnectTimeout(waittime);
			conn.setReadTimeout(waittime);
			conn.connect();
			if(conn.getResponseCode()==200)parser=new Parser(conn);
			else if(conn.getResponseCode()==403)pro403(url);
			else parser= null;
		} catch (Exception e) {
			if(e.toString().indexOf("SocketTimeoutException")>0){
				IO.error("读取"+url+"超时！");
				parser=null;
			}		
		}
		
	}
	public void pro403(String url){
		Bande=true;
		IO.error("Bande!!!Bande!!!Bande!!!");
		while(Bande){
			try {Thread.sleep(5000);holdTime+=10;} catch (InterruptedException ed) {}
		}
		try{
			HttpURLConnection conn=(HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-agent",userAgant[random(0,5)]);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();
			parser= new Parser(conn);
		}catch(Exception ef){
			parser=null;
		}
	}
	public Parser getParser(String url,int type){
		try {
			HttpURLConnection conn=(HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-agent",userAgant[random(0,5)]);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setConnectTimeout(waittime);
			conn.setReadTimeout(waittime);
			conn.connect();
			if(conn.getResponseCode()==200)return new Parser(conn);
			else return null;
		} catch (Exception e) {
			
			if(e.toString().indexOf("code: 403")>0){
				Bande=true;
				while(Bande){
					try {Thread.sleep(5000);holdTime+=10;} 
					catch (InterruptedException ed) {}
				}
				try{
					HttpURLConnection conn=(HttpURLConnection) new URL(url).openConnection();
					conn.setRequestMethod("GET");
					conn.setRequestProperty("User-agent",userAgant[random(0,5)]);
					conn.setRequestProperty("accept", "*/*");
					conn.setRequestProperty("connection", "Keep-Alive");
					conn.setConnectTimeout(waittime);
					conn.setReadTimeout(waittime);
					conn.connect();
					return new Parser(conn);
					}catch(Exception ef){
						return null;
					}
			}
			return null;
		}
	}
	
	public void setMovieId(String urlstr){
		if(urlstr!=""){
			urlstr=urlstr.replaceAll("http://movie.douban.com/subject/", "");
			if(urlstr.indexOf("/")<=0){
				movieId=urlstr;
			}
			else movieId=urlstr.substring(0,urlstr.indexOf("/"));
		}
	}
	
	public String getMovieIdFromPage(){
		Node lnode;
		NodeList idList = null;
		try {
				NodeFilter regx=new AndFilter(new TagNameFilter("a"),new HasAttributeFilter("class","bn-sharing "));
				idList=parser.extractAllNodesThatMatch(regx);
			} catch (ParserException e) {System.out.println("get id error");}	
		lnode = (Node)idList.elementAt(0);
		if(lnode!=null){
			LinkTag moveid=(LinkTag) lnode;
			String temp=moveid.getAttribute("data-url");
			temp=temp.replace("http://movie.douban.com/subject/", "");
			movieId=temp.substring(0,temp.indexOf("/"));
		}
		else{System.out.println("电影id为：N/A");}
		parser.reset();
		return movieId;
	}
	
	public String getMovieCommentAddress(){
		return "http://movie.douban.com/subject/"+movieId+"/comments";
	}
	public String getMovieReviewAddress(){
		return "http://movie.douban.com/subject/"+movieId+"/reviews";
	}
	public String getAllPhotoAddress(){
		return "http://movie.douban.com/subject/"+movieId+"/all_photos";
	}
}
