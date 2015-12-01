package bg.alexander.chat.model;

import bg.alexander.chat.controllers.validation.ValidUser;

@ValidUser
public class User {
	private String userName;
	private String userId;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public String toString(){
		return userId+" "+userName;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		User anotherUser = (User) o;
		if(anotherUser.getUserName().equals(this.getUserName())
				|| anotherUser.getUserId().equals(this.getUserId()))
			return true;
		return false;
	}
}
