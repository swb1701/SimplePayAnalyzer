package com.swblabs

class SPTransaction {

	Date date
	String type
	String toFrom
	String name
	String status
	float amount
	float fees
	String transactionId
	String reference
	
    static constraints = {
		reference nullable: true
    }
}
