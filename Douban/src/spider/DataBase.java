package spider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DataBase implements Runnable{
	
	private static int Movie_Info=0,Comment_Info=1,Review_Info=2,File_Info=3;
	
	private Connection dbConn;
	private String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private String dbURL="jdbc:sqlserver://127.0.0.1:1433; DatabaseName=DouBan";
	private String user="sa";
	private String pwd="j2eeserver";
	private LinkedTempList<MovieinfoStorage> il;
	private LinkedTempList<CommentStorage> cl;
	private LinkedTempList<ReviewStorage> rl;
	private LinkedTempList<FileStorage> fl;
	private IOBase IO;
	boolean sleep=false,stop=false;
	
	DataBase(LinkedTempList<MovieinfoStorage> il,LinkedTempList<CommentStorage>  cl,LinkedTempList<ReviewStorage> rl,LinkedTempList<FileStorage> fl,IOBase IO){
		this.il=il;
		this.cl=cl;
		this.rl=rl;
		this.fl=fl;
		this.IO=IO;
	}
	

	public void run(){
		while(true){
			if(stop){break;}
			if(sleep){
				try {Thread.sleep(5000);} catch (InterruptedException e) {IO.error("DataBase 线程挂起失败！");}
			}
			else{
				if(il.size()>0){
				excu(Movie_Info);
				//for(int i=0;i<il.size();i++)il.get(i).putout();
			}
			if(cl.size()>0){
				excu(Comment_Info);
				//for(int i=0;i<cl.size();i++)cl.get(i).putout();
			}
			if(rl.size()>0){
				excu(Review_Info);
				//for(int i=0;i<rl.size();i++)rl.get(i).putout();
			}
			if(fl.size()>0){
				excu(File_Info);
				//for(int i=0;i<fl.size();i++)fl.get(i).putout();
			}
			closeConn();
		}
			if(!sleep)try {Thread.sleep(10000);} catch (InterruptedException e) {IO.error("DataBase 线程挂起失败！");}
		}
	}
	
	
	public int[] excu(int type){
		int[]result = null;
		PreparedStatement pst = null;
		int listsize=0;
		
		if(dbConn!=null){
			 try {
				if(dbConn.isClosed()){link();}
			} catch (SQLException e) {IO.error("无法获数据库取连接状况！");}
		} else{link(); }
		
		
		if(type==Movie_Info){
			listsize=il.size();
			try {
				pst=dbConn.prepareStatement("INSERT INTO [DouBan].[dbo].[movieinfo] VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				for(int i=0;i<listsize;i++){
					pst.setString(1, il.get(i).getId());
					pst.setString(2 , il.get(i).getCover());
					pst.setString(3, il.get(i).getName());
					pst.setString(4, il.get(i).getDiector());
					pst.setString(5, il.get(i).getScreenwriter());
					pst.setString(6, il.get(i).getAct());
					pst.setString(7, il.get(i).getType());
					pst.setString(8 , il.get(i).getCountry());
					pst.setString(9 , il.get(i).getLanguage());
					if(il.get(i).getDate()==null || il.get(i).getDate()=="" ) pst.setDate(10 , null);
					else pst.setDate(10 ,  java.sql.Date.valueOf(il.get(i).getDate()));
					pst.setInt(11 , il.get(i).getLength());
					pst.setString(12 , il.get(i).getAka());
					pst.setInt(13 , il.get(i).getEpisode());
					pst.setString(14 , il.get(i).getIMDb());
					pst.setFloat(15 , il.get(i).getAverage());
					pst.setString(16 , il.get(i).getSummary());
					pst.setBoolean(17 , il.get(i).getIsmovie());
					pst.addBatch();
				}
				result=pst.executeBatch();
				dbConn.commit();
				
				for(int j=0;j<result.length;j++){
						il.remove(0);
				}
				
			} catch (SQLException e) {
				IO.error("movieinfo insert error "+ e);
			}
			
		}
		else if(type==Comment_Info){
			listsize=cl.size();
			try {
				pst=dbConn.prepareStatement("INSERT INTO [DouBan].[dbo].[comments] VALUES (?,?,?,?,?)");
				for(int i=0;i<listsize;i++){
					pst.setString(1, cl.get(i).getID());
					pst.setString(2, cl.get(i).getUserName());
					pst.setInt(3, cl.get(i).getScore());
					pst.setDate(4, java.sql.Date.valueOf(cl.get(i).getDate()));
					pst.setString(5, cl.get(i).getComment());
					pst.addBatch();
				}
				result=pst.executeBatch();
				dbConn.commit();
				for(int j=0;j<result.length;j++){
						cl.remove(0);
				}
				
			} catch (SQLException e) {
				IO.error("comment insert error "+ e);
			}
		}
		else if(type==Review_Info){
			listsize=rl.size();
			
			try {
				pst=dbConn.prepareStatement("INSERT INTO [DouBan].[dbo].[reviews] VALUES (?,?,?,?,?,?)");
				for(int i=0;i<listsize;i++){
					pst.setString(1, rl.get(i).getID());
					pst.setString(2, rl.get(i).getUserName());
					pst.setInt(3, rl.get(i).getScore());
					pst.setTimestamp(4, Timestamp.valueOf(rl.get(i).getDate()));
					pst.setString(5, rl.get(i).getReview());
					pst.setString(6, rl.get(i).getTitle());
					pst.addBatch();
				}
				result=pst.executeBatch();
				dbConn.commit();
				for(int j=0;j<result.length;j++){
						rl.remove(0);
				}
				
			} catch (SQLException e) {
				IO.error("reivew insert error "+ e);
			}
		}
		else if(type==File_Info){
			listsize=fl.size();
			try {
				pst = dbConn.prepareStatement("INSERT INTO [DouBan].[dbo].[file] VALUES (?,?,?)");
			if(pst!=null){
				for(int i=0;i<listsize;i++){
							pst.setString(1, fl.get(i).getID());
							pst.setString(2, fl.get(i).getName());
							pst.setString(3, fl.get(i).getPath());
							pst.addBatch();
					}
					result=pst.executeBatch();
					dbConn.commit();
					for(int j=0;j<result.length;j++){
							fl.remove(0);
					}
				} 
			}catch (SQLException e) {
				IO.error("file insert error "+e);}
		}
		return result;
	}
	
	public void link(){
			try {
				Class.forName(driverName);
				dbConn=DriverManager.getConnection(dbURL,user,pwd);
				dbConn.setAutoCommit(false);	
			} catch (ClassNotFoundException e) {
				IO.error("获取驱动类出错");
			} catch (SQLException e) {
				IO.error("连接错误，网络或用户或密码或数据库名错误");
			}	
	}
	public void closeConn(){
		try {
			if(dbConn!=null){
				if(!dbConn.isClosed())dbConn.close();
			}
		} catch (SQLException e) {
			IO.error("Connection Close Not Successful ");
		}
		
	}
	
}
