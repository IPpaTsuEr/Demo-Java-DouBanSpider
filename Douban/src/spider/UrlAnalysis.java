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
			IO.error("UrlAnalysis Parser Ϊ��-->"+url);
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
		catch(Exception e){IO.equals("���б��д���"+id+"ʱ��ת��ʧ�ܣ���ϸ��Ϣ��"+e);}
		
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
		} catch (Exception e) {IO.error("��ȡ"+movieId+"��Ƭ��ʱ������ϸ��Ϣ��"+e);}
		
		if(""!=name)
			{intoMIS("Ƭ��",name);}
		else
			{
				intoMIS("Ƭ��",movieId);
				IO.print(movieId+"��Ƭ��ʱ����Ϊ��ID");
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
			IO.error("��ȡ"+movieId+"����Ϣʱ����");}
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
		else{IO.print(movieId+"����Ϣ�ڵ�Ϊ�ա�");}
	}
	public void getMoviePic(){
		NodeFilter filemFilter=new HasAttributeFilter("rel","v:image");
		Node node=null;	
		try {
			parser.reset();
			node=(Node) parser.extractAllNodesThatMatch(filemFilter).elementAt(0);
		} catch (Exception e) {
			IO.error("�ڻ�ȡ"+movieId+"�ķ���ͼƬʱ����");}	
		
		if(node!=null){
				ImageTag img=(ImageTag)node;
				String adr=img.getAttribute("src");
				adr=adr.substring(img.getAttribute("src").lastIndexOf("/")+1, img.getAttribute("src").length());
				mis.setCover(adr);
				ful.add(img.getAttribute("src")+"@ID@"+movieId);
			}
		else{
				mis.setCover("");
				IO.print(movieId+"��Ӱ����ͼƬΪ�ա�");
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
			IO.error("��ȡ"+movieId+"��ƽ������ʱ������ϸ��Ϣ��"+e);
		}
		if(node!=null){
			try{
				String avr=node.getParent().getChildren().elementAt(2).toPlainTextString();
				if(avr!=""){
					av=Float.parseFloat(avr);
				}
				else {
					av=0;
					IO.print(movieId+"��ƽ������Ϊ�գ��ѽ�����Ϊ��");
				}
			}catch(Exception e1){
				av=0;
				IO.error("��ȡ"+movieId+"��ƽ������ʱ������ϸ��Ϣ��"+e1);
			}
		}
		else{
			IO.print(movieId+"��ƽ������Ϊ�գ��ѽ�����Ϊ��");}
		mis.setAverage(av);
	}
	public void getMovieSummary(){
		NodeFilter summaryFilter=new HasAttributeFilter("property","v:summary");
		Node node = null;
		try {
			parser.reset();
			node=(Node) parser.extractAllNodesThatMatch(summaryFilter).elementAt(0);
		} catch (ParserException e) {
			IO.print("��ȡ"+movieId+"�ļ��ʱ������ϸ��Ϣ��"+e);
		}
		if(node!=null){
			mis.setSummary(node.toPlainTextString().trim().replaceAll("\n","").replaceAll("  ", ""));
			}
		else{
			IO.print(movieId+"�ļ��Ϊ�ա�");
		}
	}
	public void getMovieMutuality(){
		LinkTag tag = null;
		NodeList tempList = null;
		try {
			parser.reset();
			tempList = parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("a"),new HasParentFilter(new TagNameFilter("dt"))));
		} catch (Exception e) {
			IO.equals("��ȡ"+movieId+"����ص�Ӱ������ϸ��Ϣ��"+e);
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
							//�����Ѱ���������
						}
					
				}
			}
		}
		else {IO.print(movieId+"����ص�ӰΪ�ա�");}
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
		        		//System.out.println("����ͼƬ��ַΪ��" + img.getAttribute("src")+"@ID@"+movieId);
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
													// IO.error("ת��"+movieId+"�����ͼƬ��ַʱ������ϸ��Ϣ��"+e);
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
				IO.error("��ȡ"+movieId+"�����ͼƬʱ������ϸ��Ϣ��"+e);
			}
		}
		else{
			IO.error("��ȡ"+movieId+"��ͼƬͼƬҳ��Ϊ�յȴ�����");
			mul.eadd(getAllPhotoAddress());
		}
	}
	
	
	protected void intoMIS(String type,String item){
		switch (type){
		case"Ƭ��":mis.setName(item);
			break;
		case"����" :
			mis.setDiector(item);
			break;
		case"���":mis.setScreenwriter(item);
			break;
		case"����":mis.setAct(item);
			break;
		case"����":mis.setType(item);
			break;
		case"��Ƭ����/����":mis.setCountry(item);
			break;
		case"����":mis.setLanguage(item);
			break;
		case"�ײ�":	
		case"��ӳ����":
		case"��ӳ���� ":
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
		case"Ƭ��":
		case"����Ƭ��":
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
			if(item.indexOf("����")>0)item=item.replaceAll(" ","").substring(0,item.indexOf("����")-1);
			mis.setLength(Integer.parseInt(item));
			break;
		case"����":
			int c=0;
			if(item!=null && item!=""){
					try{
						c=Integer.parseInt(item);
					}
					catch(NumberFormatException e2){
						IO.error("ת��"+movieId+"�ļ���ʱ������ϸ��Ϣ��"+e2);
					}
				}
			
			mis.setEpisode(c);
			mis.setIsmovie(false);
			break;
		case"����":mis.setAka(item);
			break;
		case"IMDb����":mis.setIMDb(item);
			break;
		case"����":
			break;
		}
		
	}
}

