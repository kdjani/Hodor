package com.app.hodor;


public class Chat {
    static final String UN_BLOCKED_VALUE = "UnBlocked";
    static final String BLOCKED_VALUE = "Blocked";
	String receiverId;
    String blocked;
	long unreadState;
	long count;
    
    public Chat() {
    
    }

    public Chat(
    		String receiverId, 
    		long count, 
    		long unreadState ) {
    	this.receiverId = receiverId;
    	this.blocked = UN_BLOCKED_VALUE;
    	this.count = count;
    	this.unreadState = unreadState;
    }
    
    public Chat(
    		String receiverId, 
    		String blocked) {
    	this.receiverId = receiverId;
    	this.blocked = blocked;
    	this.count = 0;
    	this.unreadState = 0;
    }
    
    public Chat(
    		String receiverId) {
    	this.receiverId = receiverId;
    	this.blocked = UN_BLOCKED_VALUE;
    	this.count = 0;
    	this.unreadState = 0;
    }

}
