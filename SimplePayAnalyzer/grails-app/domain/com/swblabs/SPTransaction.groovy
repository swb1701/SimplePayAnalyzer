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
	//additional fields from detail from https://payments.amazon.com/txndetail?transactionId=
	//table class txnDetails tr td
	//To,From,Amount,Date Completed*,Fees,Payment Method*,For*,Type,Status,Reference ID*,Transaction ID,Reference
	String dateCompleted //includes time
	String paymentMethod //Credit Card
	String spFor //has details of what it was for
	String referenceId
	
	static mapping={
		transactionId index: "transactionId_idx"
	}
	
    static constraints = {
		reference nullable: true
		dateCompleted nullable: true
		paymentMethod nullable: true
		spFor nullable: true
		referenceId nullable: true
		transactionId unique: true
    }
}
