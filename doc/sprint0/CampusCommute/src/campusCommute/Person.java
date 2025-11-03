package campusCommute;

public class Person {
	private String email,phone,fName,lName,DOB,password,adress,userType;
	
	public Person(String email,String phone,String fName,String lName,String DOB,String password,String adress,String userType) {
		
		this.email=email;
		this.phone= phone;
		this.fName=fName;
		this.lName= lName;
		this.DOB=DOB;
		this.password=password;
		this.adress=adress;
		this.userType = userType;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getDOB() {
		return DOB;
	}

	public void setDOB(String dOB) {
		DOB = dOB;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}


	public String getUserType() {
		return userType;
	}


	public void setUserType(String userType) {
		this.userType = userType;
	}



}
