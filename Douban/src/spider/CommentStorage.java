package spider;


public class CommentStorage {
	private String ID;
	private String UserName;
	private int Score;
	private String Date;
	private String Comment;
	
	
	public String getUserName() {
		return UserName;
	}
	public void putout(){
		System.out.println(ID+" "+UserName);//+" "+Score+" "+Date+" "+Comment
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public int getScore() {
		return Score;
	}
	public void setScore(int score) {
		Score = score;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
}

