package spider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class Config {
	private URLList ful;
	private URLList mul;
	private URLList cul;
	private URLList rul;
	private IOBase IO;
	private LinkedList<Integer> map;
	private String path="/congif.cf";
	Config(URLList mul,URLList cul,URLList rul,URLList ful,LinkedList<Integer> map,IOBase IO){
		this.mul=mul;
		this.ful=ful;
		this.cul=cul;
		this.rul=rul;
		this.map=map;
		this.IO=IO;
		String tmp=System.getProperty("java.class.path").split(";")[0];
		tmp=tmp.substring(0, tmp.lastIndexOf("\\"));
		path=tmp+"\\congif.cf";
	}
	public void setPath(String path){
		if(path.endsWith("\\") || path.endsWith("/"))this.path=path+"congif.cf";
		else this.path=path;
	}
	public String getPath(){
		return path;
	}
	public void readList(String t){
		if(t=="")t=path;
		
		try {
			File tf=new File(t);
			if(!tf.exists()){
				IO.error("Load File Not Exists");
				return;
			}
			FileReader fd=new FileReader(tf);
			BufferedReader br=new BufferedReader(fd);
			String line=null;
			line=br.readLine();
			while(line!=null){
				if(line.indexOf("[MUL]")>=0){
					while((line=br.readLine())!=null){
						if(line.startsWith("["))break;
						mul.add(line);
					}
				}
				else if(line.indexOf("[CUL]")>=0){
					while((line=br.readLine())!=null){
						if(line.startsWith("["))break;
						cul.add(line);
					}
				}
				else if(line.indexOf("[RUL]")>=0){
					while((line=br.readLine())!=null){
						if(line.startsWith("["))break;
						rul.add(line);
					}
				}
				else if(line.indexOf("[FUL]")>=0){
					while((line=br.readLine())!=null){
						if(line.startsWith("["))break;
						ful.add(line);
					}
				}
				else if(line.indexOf("[EMUL]")>=0){
					while((line=br.readLine())!=null){
						if(line.startsWith("["))break;
						mul.eadd(line);
					}
				}
				else if(line.indexOf("[ECUL]")>=0){
					while((line=br.readLine())!=null){
						if(line.startsWith("["))break;
						cul.eadd(line);
					}
				}
				else if(line.indexOf("[ERUL]")>=0){
					while((line=br.readLine())!=null){
						if(line.startsWith("["))break;
						rul.eadd(line);
					}
				}
				else if(line.indexOf("[EFUL]")>=0){
					while((line=br.readLine())!=null){
						if(line.startsWith("["))break;
						ful.eadd(line);
					}
				}
				else if(line.indexOf("[DONE]")>=0){
					while((line=br.readLine())!=null){
						if(line.startsWith("["))break;
						map.add(Integer.parseInt(line));
					}
				}
				else{
					IO.print("Loading Without Target,Load Error!");
					line=br.readLine();
				}
			}
			br.close();
			fd.close();
		} catch (FileNotFoundException e) {IO.error("Loade File Error "+e);} 
		catch (IOException e) {IO.error("Read File Error "+e);}
	}
	public void saveList(String t){
		FileWriter fw = null;
		BufferedWriter bw = null;
		int i;
		if(t=="")t=path;
			try {
				File tf=new File(t);
				if(!tf.exists())tf.createNewFile();
				fw = new FileWriter(tf);
				bw=new BufferedWriter(fw);
			} catch (IOException e1) {IO.error("Write To File "+e1);}
			
			try {
				if(mul.size()>0){
					bw.write("[MUL]\n");
					for(i=0;i<mul.size();i++)bw.write(mul.get(i)+"\n");
				}
			} catch (IOException e) {IO.error("Write To File "+e);}
			try {
				if(cul.size()>0){
					bw.write("[CUL]\n");
					for(i=0;i<cul.size();i++)bw.write(cul.get(i)+"\n");
				}
			} catch (IOException e) {IO.error("Write To File "+e);}
			try {
				if(rul.size()>0){
					bw.write("[RUL]\n");
					for(i=0;i<rul.size();i++)bw.write(rul.get(i)+"\n");
				}
			} catch (IOException e) {IO.error("Write To File "+e);}
			try {
				if(ful.size()>0){
					bw.write("[FUL]\n");
					for(i=0;i<ful.size();i++)bw.write(ful.get(i)+"\n");
				}
			} catch (IOException e) {IO.error("Write To File "+e);}
			try {
				if(mul.esize()>0){
					bw.write("[EMUL]\n");
					for(i=0;i<mul.esize();i++)bw.write(mul.eget(i)+"\n");
				}
			} catch (IOException e) {IO.error("Write To File "+e);}
			try {
				if(cul.esize()>0){
					bw.write("[ECUL]\n");
					for(i=0;i<cul.esize();i++)bw.write(cul.eget(i)+"\n");
				}
			} catch (IOException e) {IO.error("Write To File "+e);}
			try {
				if(rul.esize()>0){
					bw.write("[ERUL]\n");
					for(i=0;i<rul.esize();i++)bw.write(rul.eget(i)+"\n");
				}
			} catch (IOException e) {IO.error("Write To File "+e);}
			try {
				if(ful.esize()>0){
					bw.write("[EFUL]\n");
					for(i=0;i<ful.esize();i++)bw.write(ful.eget(i)+"\n");
				}
			} catch (IOException e) {IO.error("Write To File "+e);}
			try {
				if(map.size()>0){
					bw.write("[DONE]\n");
					for(i=0;i<map.size();i++)bw.write(map.get(i)+"\n");
			}
			} catch (IOException e) {IO.error("Write To File "+e);}
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {IO.error("Write To File "+e);}
		
	}
}
