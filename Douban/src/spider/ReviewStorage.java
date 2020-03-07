package spider;



public class ReviewStorage {
	private String ID;
	private String UserName;
	private int Score;
	private String Date;
	private String Review;
	private String Title;
	
	public void putout(){
		System.out.println(ID+" "+UserName+" "+Score+" "+Date+" "+" "+Title);
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
			Date= date;
	}
	public String getReview() {
		return Review;
	}
	public void setReview(String review) {
		Review = review;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public int getScore() {
		return Score;
	}
	public void setScore(int score) {
		Score = score;
	}
}
