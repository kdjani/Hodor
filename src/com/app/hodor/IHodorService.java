package com.app.hodor;

public interface IHodorService {

	public static final String PROPERTY_REG_ID = "registration_id";

	//method=sendhodor&sender=HEMAN&recipient=TARZAN&authtoken=mWeb6WR3jR%2FzRVfG5CxzBrv8rK8%2FRq3MwGBd3AURgrY%3D
	public abstract void SendHodor(String sender, String recipient);

	//method=createuser&username=BATMAN&authtoken=OY2jyj5XUnHN3R9uyN4RIqAdnwEPyv6mUZRU9w8x1ts%3D
	public abstract void CreateUser(String user);

	//method=blockuser&blocker=HEMAN&blockee=random&authtoken=7%2FISc0Bo56%2FL0uqcKXnU8nC%2F33kYxni%2FXME90h7Ezaw%3D
	public abstract void BlockUser(String blocker, String blockee);

}