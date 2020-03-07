package spider;



import org.htmlparser.Node;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

public class CommentAnalysis extends AnalysisBase implements Runnable {

	private String nextString;

	CommentAnalysis(URLList cul,LinkedTempList<CommentStorage> cl,IOBase IO){
		this.cl=cl;
		this.cul=cul;
		this.IO=IO;
	}
	
	public void run(){
		while(true){
			holdTime=random(10,66)*15;
			if(stop){break;}
			if(sleep){try {Thread.sleep(5000);} catch (InterruptedException e) {}}
			else if(cul.size()>0){
				IO.print("comment url---"+cul.get(0));
				getComments(cul.get(0));
				cul.remove(0);
			}
			else if(cul.esize()>0){
				IO.print("comment retry url---"+cul.eget(0));
				if(cul.eget(0)!=null || cul.eget(0) !=""){
					getComments(cul.eget(0));
				}
					cul.eremove(0);
			}
			if(!sleep)try {Thread.sleep(holdTime+random(18,24)*100);} catch (InterruptedException e) {IO.error("Comment 线程挂起失败！");}
		}
	}
	public void sleep(){
		try {Thread.sleep(holdTime*2+random(13,28*100));} catch (InterruptedException e) {IO.error("Comment 线程挂起失败！");}
	}
	
	public void getComments(String commentsPage){
		
		 setMovieId(commentsPage);
		 setParser(commentsPage,ComAna);
		
		 if(parser==null){ cul.eadd(commentsPage);IO.print(movieId+"的短评页为空。");return;}
		
		int totalitem=0;
		totalitem=getTotalSize(parser);
		
		if (totalitem<=0){IO.print(movieId+"的短评数为0.");return;}
		
		if (totalitem>20){
			int nextsize=0,readsize=0;
			
			showComments(totalitem,0);readsize++;
			
				while(true){
						
						commentsPage=getNextAddress(ComAna);
						if(commentsPage==null){
							cul.eadd(nextString);
							IO.print("没有获取到"+movieId+"的短评页"+nextString+"为空,服务器上或许有误，等待重试");
							return;
						}
						else{
							nextString=commentsPage;
						}
						nextsize=getNextSize(nextString,getMovieCommentAddress());
						
						if(nextsize>totalitem || readsize>=totalitem || nextsize<0){break;}
			
						sleep();
						setParser(nextString,ComAna);
						
						if(parser==null){
							 cul.eadd(nextString);
							 IO.print("没有获取到"+movieId+"的短评页"+nextString+"为空,服务器上或许有误，等待重试");
							 return;
						 }
						
						showComments(totalitem,nextsize);
						readsize++;
						
				}
			}
		else if(totalitem>0){
				showComments(totalitem,0);
				
		}
	}
	
	
	
	public boolean showComments(int totalitem,int nextitem){
		
		NodeList pinglunList=null;
		NodeList pinglunDirList=null;
		try{
			parser.reset();
			pinglunList = parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("span"),new HasAttributeFilter("class","comment-info")));
			parser.reset();
			pinglunDirList=parser.extractAllNodesThatMatch(new AndFilter(new TagNameFilter("p"),new HasParentFilter(new HasAttributeFilter("class","comment"))));
		}catch(Exception es){
			IO.error(movieId+"的评论页解析出错。等待机会重试");
			cul.eadd(parser.getURL());
			return false;
		}
		if(null!=pinglunList){
			
			for(int i=0;i<pinglunList.size();i++){
				try{
					Node node=(Node) pinglunList.elementAt(i);
					CommentStorage cm=new CommentStorage();
					
					cm.setID(movieId);
					cm.setUserName(node.getChildren().elementAt(1).toPlainTextString());
					
					String vote=node.getChildren().elementAt(3).getText();
					int score=0;
					if(vote.indexOf("10")>0){score=1;}
					else if(vote.indexOf("20")>0){score=2;}
					else if(vote.indexOf("30")>0){score=3;}
					else if(vote.indexOf("40")>0){score=4;}
					else if(vote.indexOf("50")>0){score=5;}
					else{score=-1;}
					cm.setScore(score);
					
					if(node.getChildren().elementAt(5)!=null){
						cm.setDate(node.getChildren().elementAt(5).toPlainTextString().replaceAll("\n", "").replaceAll(" ",""));}
					else{
						cm.setDate(node.getChildren().elementAt(3).toPlainTextString().replaceAll("\n", "").replaceAll(" ",""));}
					
					Node plnode=(Node)pinglunDirList.elementAt(i);
					cm.setComment(plnode.toPlainTextString().replaceAll("\n", ""));
					
					if(cm!=null){
						cl.add(cm);
						if(i==pinglunList.size()-1)IO.print(movieId+"Comment 解析"+(i+nextitem)+"/"+totalitem);
					}
					
				}catch(Exception e){
					IO.error(movieId+"的评论页解析节点"+i+"时出现错误。等待机会重试");
					cul.eadd(parser.getURL());
					continue;
				}
			}
		}else {
				IO.error(movieId+"的评论页解析节点为空。等待机会重试");
				cul.eadd(parser.getURL());
				return false;
			}
		return true;
	}
}
