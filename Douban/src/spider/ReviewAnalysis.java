package spider;




import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

public class ReviewAnalysis extends AnalysisBase implements Runnable {
	private String nextString;
	
	ReviewAnalysis(URLList rul,LinkedTempList<ReviewStorage> rl,IOBase IO){
		this.rl=rl;
		this.rul=rul;
		this.IO=IO;
	}
	
	public void run(){
		while(true){
			holdTime=random(10,66)*15;
			if(stop){break;}
			if(sleep){
				try {Thread.sleep(5000);} catch (InterruptedException e) {}
			}
			else if(rul.size()>0){
					IO.print("review url---"+rul.get(0));
					getReview(rul.get(0));
					rul.remove(0);
			}else if(rul.esize()>0){
				String url=rul.eget(0);
				IO.print("review retry url---"+ url);
				rul.eremove(0);
				if(url!=null || url!=""){
					if(url.indexOf("com/review/")>0){
						try{
							setParser(url,RevAna);
							if(parser==null){rul.eadd(url);}
							else{
								ReviewStorage trss=showReview();
								if(trss!=null){rl.add(trss);}
							}
							
						}catch(Exception eee){
							rul.eadd(url);
						}
					}
					else{
						getReview(url);
					}
				}
				
			}
			if(!sleep)try {Thread.sleep(holdTime*2+random(18,24)*100);} catch (InterruptedException e) {IO.error("Review �̹߳���ʧ�ܣ�");}
		}
		
	}
	public NodeList getNodeList(){
		NodeList yingpingList = null ;
		try {
			parser.reset();
			yingpingList = parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("class","review-hd")));
			parser=new Parser( yingpingList.toHtml());
			yingpingList.removeAll();
			yingpingList=parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("a"),new HasAttributeFilter("onclick","moreurl(this, {from: ''})")));
		}catch (Exception e) 
		{
			IO.error("�ڻ�ȡ"+movieId+"��reviews��ҳʱ������ϸ��Ϣ��"+e);
			return null;
		}
		return yingpingList ;
	}
	public String getLink(NodeList yingpingList,int index){
		Node yingpingNode;
		LinkTag yingpingLink;
		try{
			yingpingNode=(Node)yingpingList.elementAt(index);
			yingpingLink=(LinkTag)yingpingNode;	
			return yingpingLink.getAttribute("href");
		}
		catch(Exception e){IO.error("�ڻ�ȡ"+movieId+"��reviews��ҳ����ʱ������ϸ��Ϣ��");return null;}
	}
	
	public void sleep(){
		try {Thread.sleep(holdTime*2+random(11,26)*100);}catch (InterruptedException e1) {IO.error("Review ��ҳ����ʱ���� �̹߳���ʧ�ܣ�");}
	}
	
	public void getReview(String reviewPage){
		setMovieId(reviewPage);
		setParser(reviewPage,RevAna);

		if(parser==null){rul.eadd(reviewPage);IO.print(movieId+"��Ӱ��ҳ�治����.�ȴ�����");return;}
		
		int totalitem=0;
		totalitem=getTotalSize(parser);
		
		if(totalitem<=0){IO.print(movieId+"��Ӱ����Ϊ0.");return;}
		
		if(totalitem>20){
			int nextsize=0,readsize=0;
			NodeList yingpingList;
			
			while(true){
						reviewPage=getNextAddress(RevAna);	
						if(reviewPage==null){
							rul.eadd(nextString);
							IO.print("û�л�ȡ��"+movieId+"��Ӱ��ҳ"+nextString+"Ϊ��,�������ϻ������󣬵ȴ�����");
							return;
							}
						else{nextString=reviewPage;}
						
						nextsize=getNextSize(nextString,getMovieReviewAddress());
						
						yingpingList=getNodeList();
						
						if(yingpingList!=null){
							for(int i=0;i<yingpingList.size();i++){
								sleep();
							
								String link=getLink(yingpingList,i);
								if(link!=null){
									
									setParser(link,RevAna);
									if(parser==null){rul.eadd(link);IO.print(movieId+"��Ӱ��ҳ��"+link+"������.�ȴ����ԡ�");continue;}
									
									ReviewStorage trss=showReview();
									IO.print(movieId+"Review ����"+readsize+"/"+totalitem);
									readsize++;
									if(trss!=null){rl.add(trss);}
									else {IO.print("��ȡ"+movieId+"��Ӱ��ҳ"+link+"���Ϊ��,�������̻�ҳ������");}
								}
								else continue;
							}
						}
						
						
						
						if(nextsize>totalitem || readsize>=totalitem || nextsize<0){ break;}
						
						sleep();
						
						setParser(nextString,RevAna);
						if(parser==null){rul.eadd(nextString);IO.print(movieId+"��Ӱ��ҳ��"+nextString+"������.");return;}
						
					}
		}
		else{
				NodeList yingpingList=getNodeList();
				if(yingpingList!=null){
					for(int i=0;i<yingpingList.size();i++){
						sleep();
						String link=getLink(yingpingList,i);
						if(link!=null){
							
							setParser(link,RevAna);
							if(parser==null){rul.eadd(link);IO.print(movieId+"��Ӱ��ҳ��"+link+"������.");continue;}
							
							ReviewStorage trss=showReview();
							IO.print(movieId+"Review ����"+i+"/"+totalitem);
							
							if(trss!=null){rl.add(trss);}
							else {IO.print("��ȡ"+movieId+"��Ӱ��ҳ"+link+"���Ϊ��,�������̻�ҳ������");}
						}
						else continue;
					}
				}
				
		}
	}
	public ReviewStorage showReview(){
		ReviewStorage trs=new ReviewStorage();
		try{
		trs.setID(movieId);
		
		parser.reset();
		Node titleNode=(Node)parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("span"),new HasAttributeFilter("property","v:summary"))).elementAt(0);
		trs.setTitle(titleNode.toPlainTextString());
		
		parser.reset();
		Node usernameNode=(Node) parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("span"),new HasAttributeFilter("property","v:reviewer"))).elementAt(0);
		trs.setUserName(usernameNode.toPlainTextString());
		
		parser.reset();
		Node ratingNode=(Node) parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("span"),new HasAttributeFilter("property","v:rating"))).elementAt(0);
		try{trs.setScore(Integer.parseInt(ratingNode.toPlainTextString()));}
		catch(Exception e){trs.setScore(0);}
		
		parser.reset();
		Node datetimeNode=(Node) parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("span"),new HasAttributeFilter("property","v:dtreviewed"))).elementAt(0);
		trs.setDate(datetimeNode.toPlainTextString());
		
		parser.reset();
		Node direcNode=(Node) parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("property","v:description"))).elementAt(0);
		trs.setReview(direcNode.toPlainTextString().replaceAll("  ", "").replaceAll("\n", "").replaceAll("&nbsp;", ""));
		}
		catch(Exception e){
			IO.error("��ȡ"+movieId+"��Ӱ����ҳ�����ڵ���ִ���"+e);
			return null;
			}
		return trs;
	}
	
	
}
