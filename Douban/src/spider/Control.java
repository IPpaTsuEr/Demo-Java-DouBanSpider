package spider;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class Control {
	
	public static void main(String[] arg){
		
		LinkedList<CommentStorage>commentList=new LinkedList<CommentStorage> ();
		LinkedList<ReviewStorage>reviewList=new LinkedList<ReviewStorage> ();
		LinkedList<MovieinfoStorage>movieinfoList=new LinkedList<MovieinfoStorage> ();
		LinkedList<FileStorage>movieimageList=new LinkedList<FileStorage> ();
		
		LinkedList<String>MovieUrlList=new LinkedList<String>();
		LinkedList<String>PicUrlList=new LinkedList<String>();
		LinkedList<String>ComUrlList=new LinkedList<String>();
		LinkedList<String>RevUrlList=new LinkedList<String>();
		
		LinkedTempList<MovieinfoStorage> il=new LinkedTempList<MovieinfoStorage>(movieinfoList);
		LinkedTempList<CommentStorage> cl=new LinkedTempList<CommentStorage>(commentList);
		LinkedTempList<ReviewStorage> rl=new LinkedTempList<ReviewStorage>(reviewList);
		LinkedTempList<FileStorage> fl=new LinkedTempList<FileStorage>(movieimageList);
		
		URLList ful=new URLList(PicUrlList);
		URLList mul=new URLList(MovieUrlList);
		URLList cul=new URLList(ComUrlList);
		URLList rul=new URLList(RevUrlList);
		
		LinkedList<Integer> map=new LinkedList<Integer>();
		
		UI ui=new UI();
		IOBase IO =new IOBase(ui);
		UrlAnalysis ua=new UrlAnalysis(mul,ful,cul,rul,il,map,IO);
		CommentAnalysis ca=new CommentAnalysis(cul,cl,IO);
		ReviewAnalysis ra=new ReviewAnalysis(rul,rl,IO);
	    FileDownload fd=new FileDownload(ful,fl,IO);
		DataBase db =new DataBase(il,cl,rl,fl,IO);
		Config cf=new Config(mul,cul,rul,ful,map,IO);
		
		
		
		Thread UA=new Thread(ua);
		Thread CA=new Thread(ca);
		Thread RA=new Thread(ra);
		Thread FD=new Thread(fd);
		Thread DB=new Thread(db);
		

		ui.initial();
		ui.setSelectedPath("");
		ui.setFiled(cf.getPath());
	
		
		UA.start();
		CA.start();
		RA.start();
		FD.start();
		DB.start();		
		
		boolean isstart=true;
		int loop=0;
		boolean sleep=false;
		
		while(true){
			
			try {Thread.sleep(500);} catch (InterruptedException e) {}
			if(ua.getBanStatu() || ca.getBanStatu() || ra.getBanStatu()){
				while(true){
					try {
						Thread.sleep(5000);
						IO.print("正在测试链接。。。");
						URL tl=new URL("");
						HttpURLConnection rcl=(HttpURLConnection)tl.openConnection();
						int code=rcl.getResponseCode();
						IO.print("状态码为"+code);
						if(code==200){
							IO.print("连接恢复，继续执行。");
							ua.setBanStatu(true);
							ca.setBanStatu(true);
							ra.setBanStatu(true);
							break;
						}
					} catch (MalformedURLException e) {
						
					} 
					catch (IOException e) {
						
					} 
					catch (InterruptedException e) {
						
					}
					
				}
			}
			
			loop++;
			if(!isstart & loop>500){
				IO.print("Auto Save");
				cf.saveList("");
				loop=0;
			}

			if(sleep!=ui.isSleep()){
				sleep=ui.isSleep();
				ua.sleep=sleep;ca.sleep=sleep;ra.sleep=sleep;fd.sleep=sleep;db.sleep=sleep;
				if(sleep)IO.print("SLEEP");
				else IO.print("UP");
				}
			if(ui.isStop()){
				ua.stop=true;
				IO.print("Start STOP Process");
						ca.stop=true;
						ra.stop=true;
						fd.stop=true;
						IO.print("Wait Thread End");
						while(true){if(!UA.isAlive() && !CA.isAlive() && !RA.isAlive() && !FD.isAlive())break;}
								db.stop=true;
								IO.print("Thread End");
								IO.print("Save The Cureent");
								cf.saveList("");
								IO.print("Save Info Done");
								
						while(DB.isAlive()){}
								IO.print("Save To DataBase Done");
								IO.print("Exit");
								break;
			}
			
			if(ui.getSelectedPath()!=""){
				cf.setPath(ui.getSelectedPath());
				ui.setSelectedPath("");
			}
			
			
			if(ui.isAdd()){
				isstart=false;
				IO.statu("正在读取文件...");
				cf.readList(ui.getSelectedFile());
				if(mul.size()>0){
					IO.print("Load the Config");
					for(int z=0;z<mul.size();z++)
						ua.doseHas(mul.get(z));
					IO.print("Load the Config Done");
					ui.setAdd(false);
				}
			}
			
				if(isstart){IO.statu("--解析目标为空，请添加--");}
				else{
					IO.statu("UA:"+UA.isAlive()+" CA:"+CA.isAlive()+" RA:"+RA.isAlive()+" DB:"+DB.isAlive()+" FD:"+FD.isAlive()+
							"\n 页面："+ mul.size()+" 影评："+rul.size()+" 短评："+cul.size()+" 图片："+ful.size()+
							"\n re页面："+ mul.esize()+" re影评："+rul.esize()+" re短评："+cul.esize()+" re图片："+ful.esize());
				}
				
			
		}
		
	}

}

