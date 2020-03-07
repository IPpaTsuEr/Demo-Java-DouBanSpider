package spider;


import java.util.LinkedList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class UrlAnalysis extends AnalysisBase implements  Runnable{
	private MovieinfoStorage mis;
	
	UrlAnalysis(URLList mul,URLList ful,URLList cul,URLList rul,LinkedTempList<MovieinfoStorage> il,LinkedList<Integer> map,IOBase IO){
		this.il=il;
		this.mul=mul;
		this.ful=ful;
		this.cul=cul;
		this.rul=rul;
		this.map=map;
		this.IO=IO;
	}
	public MovieinfoStorage getStorage(String url){
		mis=new MovieinfoStorage();
		setMovieId(url);
		mis.setId(movieId);
		setParser(url,UrlAna);

		if(parser!=null){
			getMovieName();
			getMovieInfo();
			getMoviePic();
			getMovieScore();
			getMovieSummary();
			getMovieMutuality();
			getBigimage("");
			return mis;
		}
		else {
			IO.error("UrlAnalysis Parser 为空-->"+url);
			mul.eadd(url);
			return null;
		}
	}

	public void run(){
		while(true){
			holdTime=random(6,46)*15;
			if(stop){break;}
			if(sleep){
				try {Thread.sleep(5000);} catch (InterruptedException e) {}
			}
			else if(mul.size()>0){
					MovieinfoStorage mst=getStorage(mul.get(0));
					mul.remove(0);
					if(mst!=null){
						il.add(mst);
						cul.add(getMovieCommentAddress());
						rul.add(getMovieReviewAddress());
					}
			}
			else if(mul.esize()>0){
				String tg=mul.eget(0);
				if(tg==null || tg=="")mul.eremove(0);
				else{
					mul.eremove(0);
					if(tg.endsWith("all_photos")){
						getBigimage(tg);
					}
					else{
						MovieinfoStorage mst=getStorage(tg);
						if(mst!=null){
							il.add(mst);
							cul.add(getMovieCommentAddress());
							rul.add(getMovieReviewAddress());
						}
					}
				}
			}
			if(!sleep)try {Thread.sleep(holdTime+random(18,24)*100);} catch (InterruptedException e) {}
		}
		
	}

	 boolean doseHas(String id){
		id=id.replace("http://movie.douban.com/subject/", "").replace("from=subject-pag","");
		if(id.lastIndexOf("/")>0)id=id.substring(0, id.lastIndexOf("/"));
		
		int tem = 0;
		try{tem=Integer.parseInt(id);}
		catch(Exception e){IO.equals("向列表中存入"+id+"时，转化失败，详细信息："+e);}
		
		if(map.size()>0){
			if(map.get(map.size()-1)<tem){map.add(tem);return true;}
			else if(map.get(0)>tem){map.add(0,tem);return true;}
			else{
				for(int i=1;i<map.size()-2;i++){
					if(map.get(i)==tem)return false;
					if(map.get(i)<tem && map.get(i+1)>tem){
						map.add(i+1,tem);
						return true;
					}
				}
			}
		}
		else{
			map.add(tem);
			return true;
		}
		return false;
	}
	
	
	public void getMovieName() {
		String name="";
		Node nameNode;
		try {
			parser.reset();
			NodeList lt=parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("span"),new HasAttributeFilter("property","v:itemreviewed")));
			nameNode = lt.elementAt(0);
			if(nameNode!=null){
				name=nameNode.toPlainTextString().trim().replaceAll("\n", "");}
		} catch (Exception e) {IO.error("获取"+movieId+"的片名时出错，详细信息："+e);}
		
		if(""!=name)
			{intoMIS("片名",name);}
		else
			{
				intoMIS("片名",movieId);
				IO.print(movieId+"的片名时被置为其ID");
			}
	}
	public void getMovieInfo() {
		NodeList infoList;
		Node node = null;
		
		try {
			parser.reset();
			infoList = parser.extractAllNodesThatMatch(new HasAttributeFilter("id","info"));
			node=(Node) infoList.elementAt(0);
		} catch (Exception e) {
			IO.error("获取"+movieId+"的信息时出错");}
		if(node!=null){
			String info=node.toPlainTextString().replaceAll("  ", "").replaceAll("/ ", "/").replaceAll(" /", "/").replaceAll(": ", ":");
			String infoArray[]=info.split("\n");
			for(int i=0;i<infoArray.length;i++){
				if(infoArray[i].indexOf(":")>0){
					String keyvalue[]=infoArray[i].split(":");
					if(keyvalue.length>1){
						if(keyvalue.length>2){
							for(int x=1;x<keyvalue.length;x++)
							keyvalue[1]=keyvalue[1]+keyvalue[x];
						}
						if(keyvalue[1]!=""){
							intoMIS(keyvalue[0],keyvalue[1].replaceAll(":","-"));
						}
						else{
							intoMIS(keyvalue[0],"");
						}
					}
				}
			}
		}
		else{IO.print(movieId+"的信息节点为空。");}
	}
	public void getMoviePic(){
		NodeFilter filemFilter=new HasAttributeFilter("rel","v:image");
		Node node=null;	
		try {
			parser.reset();
			node=(Node) parser.extractAllNodesThatMatch(filemFilter).elementAt(0);
		} catch (Exception e) {
			IO.error("在获取"+movieId+"的封面图片时出错！");}	
		
		if(node!=null){
				ImageTag img=(ImageTag)node;
				String adr=img.getAttribute("src");
				adr=adr.substring(img.getAttribute("src").lastIndexOf("/")+1, img.getAttribute("src").length());
				mis.setCover(adr);
				ful.add(img.getAttribute("src")+"@ID@"+movieId);
			}
		else{
				mis.setCover("");
				IO.print(movieId+"电影封面图片为空。");
			}
	}
	public void getMovieScore(){
		float av = 0;
		NodeFilter scoreFilter=new  HasAttributeFilter("property","v:average");
		Node node = null;
		try {
			parser.reset();
			node=(Node) parser.extractAllNodesThatMatch(scoreFilter).elementAt(0);
		} catch (Exception e) {
			IO.error("获取"+movieId+"的平均评分时出错，详细信息："+e);
		}
		if(node!=null){
			try{
				String avr=node.getParent().getChildren().elementAt(2).toPlainTextString();
				if(avr!=""){
					av=Float.parseFloat(avr);
				}
				else {
					av=0;
					IO.print(movieId+"的平均评分为空，已将其置为零");
				}
			}catch(Exception e1){
				av=0;
				IO.error("获取"+movieId+"的平均评分时出错，详细信息："+e1);
			}
		}
		else{
			IO.print(movieId+"的平均评分为空，已将其置为零");}
		mis.setAverage(av);
	}
	public void getMovieSummary(){
		NodeFilter summaryFilter=new HasAttributeFilter("property","v:summary");
		Node node = null;
		try {
			parser.reset();
			node=(Node) parser.extractAllNodesThatMatch(summaryFilter).elementAt(0);
		} catch (ParserException e) {
			IO.print("获取"+movieId+"的简介时出错，详细信息："+e);
		}
		if(node!=null){
			mis.setSummary(node.toPlainTextString().trim().replaceAll("\n","").replaceAll("  ", ""));
			}
		else{
			IO.print(movieId+"的简介为空。");
		}
	}
	public void getMovieMutuality(){
		LinkTag tag = null;
		NodeList tempList = null;
		try {
			parser.reset();
			tempList = parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("a"),new HasParentFilter(new TagNameFilter("dt"))));
		} catch (Exception e) {
			IO.equals("获取"+movieId+"的相关电影出错，详细信息："+e);
		}
		if(tempList.size()>0){
			for(int i=0;i<tempList.size();i++){
				Node node=(Node)tempList.elementAt(i);
				if(node!=null){
					tag=(LinkTag) node;
					if(doseHas(tag.getAttribute("href"))){
							mul.add(tag.getAttribute("href"));
						}
					else{
							//队列已包含此链接
						}
					
				}
			}
		}
		else {IO.print(movieId+"的相关电影为空。");}
	}
	
	public void getSmallImage(){ 
        NodeFilter filter = new AndFilter(new TagNameFilter("ul"),new HasAttributeFilter("class","related-pic-bd narrow"));  
        NodeFilter filter2 = new AndFilter(new TagNameFilter("ul"),new HasAttributeFilter("class","related-pic-bd"));  
        Node node=null;
        NodeList imglist=null;
       
		try {
			parser.reset();
				imglist=parser.extractAllNodesThatMatch(filter);
			if(imglist.size()>0){node=(Node)imglist.elementAt(0);}
			else{
				parser.reset();
				imglist=parser.extractAllNodesThatMatch(filter2);
				if(imglist.size()>0){node=(Node)imglist.elementAt(0);}
				}
			if (node!=null){
				String lihtml=node.getChildren().toHtml();
				Parser imgParser;
				NodeList imgList=null;
				imgParser = new Parser(lihtml);
				imgList=imgParser.extractAllNodesThatMatch(new TagNameFilter("img"));
				parser.reset();
		        ImageTag img=null;
		        
		        if(imgList.size()>0){
		        	for(int i=0;i<imgList.size();i++){
		        	Node imgnode=imgList.elementAt(i);
		        	if(imgnode!=null){
		        		img=(ImageTag) imgnode;
		        		ful.add(img.getAttribute("src")+"@ID@"+movieId);
		        		//System.out.println("剧照图片地址为：" + img.getAttribute("src")+"@ID@"+movieId);
		        	}
		         }
		        }
			}
		} catch (ParserException e) {
			
		}
	}

	public void getBigimage(String url){
		try {Thread.sleep(holdTime+random(13,26)*100);} catch (InterruptedException e) {IO.error("getBigImage Thread hold error1");}
		Parser allphoto=null;
		
		if(url==""){allphoto=getParser(getAllPhotoAddress(),UrlAna);}
		else {allphoto=getParser(url,UrlAna);}
		
		if(allphoto!=null){
			try {
				NodeList photoList=allphoto.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("ul"),new HasAttributeFilter("class","pic-col5")));
				if(photoList!=null){
					for(int p=0;p<photoList.size();p++){
						NodeList hcl=photoList.elementAt(p).getChildren();
						for(int y=0;y<hcl.size();y++){
							NodeList hccl=hcl.elementAt(y).getChildren();
							if(hccl!=null){
								for(int u=0;u<hccl.size();u++){
									NodeList nccl=hccl.elementAt(u).getChildren();
									 if(nccl!=null){
										 for(int t=0;t<nccl.size();t++){
											 Node fd=nccl.elementAt(t);
											 if(fd!=null){
												 try{
													 ImageTag  mm=(ImageTag)fd;
													 String lk=mm.getAttribute("src");
															 if(lk.indexOf("shire")<0){
																 ful.add(lk.replace("albumicon","photo")+"@ID@"+movieId);
															 }
												 }
												 catch(Exception e){
													// IO.error("转换"+movieId+"的相关图片地址时出错，详细信息："+e);
												 }
											 }
										 }
									 }
								}
							}
						}
					}
				}
			} catch (ParserException e) {
				IO.error("获取"+movieId+"的相关图片时出错，详细信息："+e);
			}
		}
		else{
			IO.error("获取"+movieId+"的图片图片页面为空等待重试");
			mul.eadd(getAllPhotoAddress());
		}
	}
	
	
	protected void intoMIS(String type,String item){
		switch (type){
		case"片名":mis.setName(item);
			break;
		case"导演" :
			mis.setDiector(item);
			break;
		case"编剧":mis.setScreenwriter(item);
			break;
		case"主演":mis.setAct(item);
			break;
		case"类型":mis.setType(item);
			break;
		case"制片国家/地区":mis.setCountry(item);
			break;
		case"语言":mis.setLanguage(item);
			break;
		case"首播":	
		case"上映日期":
		case"上映日期 ":
			int a=0;
			if(item.length()>10){item=item.substring(0, 10);}
			for(int i=0;i<item.length();i++){
				if(!Character.isDigit(item.charAt(i)) && item.charAt(i)!='-'){a++;}
				if(a>=1){
					item=item.substring(0,i);
					break;
				}
			}
			String[] tm=item.split("-");
			
			if(tm.length==1){item=item+"-02-27";}
			else if(tm.length==2){
				if(tm[1].length()==1)tm[1]="0"+tm[1];
				else{item=item+"-01";}
				}
			else if(tm.length==3){
				if(tm[2].length()==1)tm[2]="0"+tm[2];
			}
			mis.setDate(item);
			break;
		case"片长":
		case"单集片长":
			int j;
			item=item.replaceAll(" ","");
			if(!Character.isDigit(item.charAt(0))){
				for(j=0;j<item.length();j++){
					if(Character.isDigit(item.charAt(j)))break;
				}
				if(j!=0){
					item=item.substring(j,item.length());
				}
			}
			else{
				for(j=0;j<item.length();j++){
					if(!Character.isDigit(item.charAt(j)))break;
				}
				if(j!=0){
					item=item.substring(0,j);
				}
			}
			if(item.indexOf("分钟")>0)item=item.replaceAll(" ","").substring(0,item.indexOf("分钟")-1);
			mis.setLength(Integer.parseInt(item));
			break;
		case"集数":
			int c=0;
			if(item!=null && item!=""){
					try{
						c=Integer.parseInt(item);
					}
					catch(NumberFormatException e2){
						IO.error("转换"+movieId+"的集数时出错，详细信息："+e2);
					}
				}
			
			mis.setEpisode(c);
			mis.setIsmovie(false);
			break;
		case"又名":mis.setAka(item);
			break;
		case"IMDb链接":mis.setIMDb(item);
			break;
		case"季数":
			break;
		}
		
	}
}

