package spider;

public class FileStorage {
	private String Name;
	private String ID;
	private String Path;

	public void putout(){
		System.out.println(Name+" "+ID+" "+Path);
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getPath() {
		return Path;
	}
	public void setPath(String path) {
		Path = path;
	}

	
}
