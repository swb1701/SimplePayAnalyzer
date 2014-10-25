package com.swblabs

class SPSubscription {
	
	String payee
	String payfor
	String terms
	String status
	String validFrom
	String validUntil
	String subscriptionId
	
	Date fromDate
	String name
	float amount
	String period
	
	static mapping={
		subscriptionId index: "subscriptionId_idx"
	}
	
    static constraints = {
		subscriptionId unique: true
    }
}
