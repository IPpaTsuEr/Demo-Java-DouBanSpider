package spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class IOBase {
	private UI pc;
	private File file;
	private String path;
	IOBase(UI pc){
		this.pc=pc;
		String tmp=System.getProperty("java.class.path").split(";")[0];
		tmp=tmp.substring(0, tmp.lastIndexOf("\\"));
		path=tmp+"\\ErrorLog.txt";
	}
	
	public void error(String str){
		pc.putTextArea("Error:"+str);
	}
	public void print(String str){
		pc.putTextArea("---->"+str);
	}
	public void statu(String str){
		pc.putStatu(str);
	}
	public void log(String str){
		file=new File(path);
		if(!file.exists())try {file.createNewFile();} catch (IOException e) {error("在 "+path+" 日志文件创建失败！");}
		if(file!=null){
			FileWriter fw = null;
			try {fw = new FileWriter(file,true);} catch (IOException e) {error("从 日志文件获取输出流失败！");}
			if(fw!=null)
			{
				BufferedWriter bw=new BufferedWriter(fw);
				try {
					bw.write(str);
					bw.newLine();
				} catch (IOException e) {error("写入日志失败！");}
			}
			
		}
		
	}
}


class LinkedTempList<T>{
	LinkedList<T> list;
	LinkedTempList(LinkedList<T> list){
		this.list=list;
	}
	public  int size(){
		return list.size();
	}
	public synchronized void add(T item){
		list.add(item);
	}
	public synchronized T remove(int index){
		return list.remove(index);
	}
	public synchronized T get(int index){
		return list.get(index);
	}
}

class URLList extends ListBase{
	private LinkedList<String> UrlList;
	URLList(LinkedList<String> UrlList){
		this.UrlList=UrlList;
	}
	public  int size(){
		return UrlList.size();
	}
	public synchronized boolean add(String item){
		return UrlList.add(item);
	}
	public synchronized String remove(int index){
		return UrlList.remove(index);
	}
	public synchronized String get(int index){
		return UrlList.get(index);
	}
}


class ListBase{
	 
	static LinkedList<String>ErrorList=new LinkedList<String>();
	
	public LinkedList<String> getErrorList(){
		return ErrorList;
	}
	public String eget(int index){
		return ErrorList.get(index);
	}
	public String eremove(int index){
		return ErrorList.remove(index);
	}
	public boolean eremove(String item){
		return ErrorList.remove(item);
	}
	public boolean eadd(String item){
		return ErrorList.add(item);
	}
	public int esize(){
		return ErrorList.size();
	}
}
