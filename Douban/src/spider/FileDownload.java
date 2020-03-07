package spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FileDownload implements Runnable{
	private String[] userAgant={
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0)",
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36",
			"Mozilla/5.0 (Windows NT 6.1) Gecko/20100101 Firefox/41.0",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)"
	};
	
	private String fileName,savepath;
	private byte[] buffer=new byte[2048];
	private int length=0;
	private LinkedTempList<FileStorage> fl;
	private IOBase IO;
	private URLList ful;
	private String ID="";
	boolean sleep=false,stop=false;
	
	FileDownload(URLList ful,LinkedTempList<FileStorage> fl,IOBase IO){
		this.fl=fl;
		this.ful=ful;
		this.IO=IO;
		String tmp=System.getProperty("java.class.path").split(";")[0];
		tmp=tmp.substring(0, tmp.lastIndexOf("\\"));
		savepath=tmp+"\\img";
	}

	public void run(){
		while(true){
			if(stop){break;}
			if(sleep){
				try {Thread.sleep(5000);} catch (InterruptedException e) {IO.error("FileDoneload 线程挂起失败！");}
			}
			else if(ful.size()>0){
				FileStorage t=download(ful.get(0));
				ful.remove(0);
				if(t!=null){fl.add(t);}
			}
			else if(ful.esize()>0){
				FileStorage t=download(ful.eget(0));
				ful.eremove(0);
				if(t!=null){
					fl.add(t);
					}
			}
			if(!sleep)try {Thread.sleep(random(9,12)*100);} catch (InterruptedException e) {IO.error("FileDoneload 线程挂起失败！");}
		}
	}
	public void setSavepath(String path){
		savepath=path;
	}
	public void getFileName(String link){
		if(link.endsWith(".jpg")){
			fileName=link.substring(link.lastIndexOf("/")+1,link.length());
		}
		else if(link.endsWith(".jpg?")){
			fileName=link.substring(link.lastIndexOf("/")+1,link.length()-1);
		}
		else{
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
			fileName=format.format(date)+".jpg";
		}
		
	}
	public FileStorage download(String urlstr){
		if(urlstr==null || urlstr=="")return null;
		String[] sq=urlstr.split("@ID@");
		ID=sq[1];
		try {
			URL downLink=new URL(sq[0]);
			HttpURLConnection conn=(HttpURLConnection )downLink.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-agent",userAgant[random(0,5)]);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.connect();
			
			File file=new File(savepath);
			if (!file.exists()){
				file.mkdir();
			}
			getFileName(sq[0]);
			File check=new File(file.getPath()+"\\"+fileName);
			if(check.exists()){
				//IO.print("文件"+fileName+"已经存在，跳过下载");
				return null;
			}
			InputStream inp=conn.getInputStream();
			OutputStream opt=new FileOutputStream(file.getPath()+"\\"+fileName);
			while((length=inp.read(buffer))!=-1){
				opt.write(buffer,0,length);
			}
			opt.close();
			inp.close();
			//IO.print(ID+"的文件"+fileName+"下载完成");
			FileStorage df=new FileStorage();
			df.setID(ID);
			df.setPath(savepath);
			df.setName(fileName);
			return df;
		}catch (Exception e) {
			ful.eadd(urlstr);
			IO.error("下载文件："+urlstr+"出错，详细信息："+e);
			return null;
		}
		
	}
	
	public int random(int low,int high){
		Random random = new Random();
		return random.nextInt(high)+low;
	}
}
