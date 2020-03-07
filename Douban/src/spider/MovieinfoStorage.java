package spider;


public class MovieinfoStorage {
	private String Id;
	private String Cover;
	private String Name;
	private String Diector;
	private String Screenwriter;
	private String Act;
	private String Type;
	private String Country;
	private String Language;
	private String Date;
	private int Length;
	private String Aka;
	private int Episode;
	private String IMDb;
	private float Average;
	private String Summary;
	private boolean Ismovie=true;
	
	public void setIsmovie(boolean ismovie){
		Ismovie=ismovie;
	}
	public void putout(){
		System.out.println(Id+" "+Cover+" "+Name+" "+Diector+" "+Screenwriter+" "+Act+" "+Type+" "+Country+" "+Language+" "+Date+" "+Length+" "+Aka+" "+Episode+" "+IMDb+" "+Average+" "+Summary+" "+Ismovie);
	}
	public boolean getIsmovie(){
		return Ismovie;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getCover() {
		return Cover;
	}
	public void setCover(String cover) {
		Cover = cover;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getDiector() {
		return Diector;
	}
	public void setDiector(String diector) {
		Diector = diector;
	}
	public String getAct() {
		return Act;
	}
	public void setAct(String act) {
		Act = act;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public String getLanguage() {
		return Language;
	}
	public void setLanguage(String language) {
		Language = language;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
			Date =date;
	}
	public int getLength() {
		return Length;
	}
	public void setLength(int length) {
		Length = length;
	}
	public String getAka() {
		return Aka;
	}
	public void setAka(String aka) {
		Aka = aka;
	}
	public int getEpisode() {
		return Episode;
	}
	public void setEpisode(int episode) {
		Episode = episode;
	}
	public String getIMDb() {
		return IMDb;
	}
	public void setIMDb(String iMDb) {
		IMDb = iMDb;
	}
	public String getScreenwriter() {
		return Screenwriter;
	}
	public void setScreenwriter(String screenwriter) {
		Screenwriter = screenwriter;
	}
	public float getAverage() {
		return Average;
	}
	public void setAverage(float average) {
		Average = average;
	}
	public String getSummary() {
		return Summary;
	}
	public void setSummary(String summary) {
		Summary = summary;
	}

}
