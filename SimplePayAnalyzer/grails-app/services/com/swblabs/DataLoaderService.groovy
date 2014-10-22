
package com.swblabs

import grails.transaction.Transactional

@Transactional
class DataLoaderService {

	def loadData(File csvFile,boolean merge) {
		int row=0;
		csvFile.eachCsvLine { items ->
			if (row>0) processRow(items,merge)
			row++
		}
	}
	
	def loadData(String csvString,boolean merge) {
		int row=0;
		csvString.eachCsvLine { items ->
			if (row>0) processRow(items,merge)
			row++
		}
	}
	
	def convertAmount(String amt) {
		amt=amt.replace(",","")
		if (amt.startsWith("(")) {
			return(-1*Float.parseFloat(amt.substring(2,amt.length()-3)))
		} else {
			return(Float.parseFloat(amt.substring(1)));
		}
	}
	
	def processRow(items,merge) {
		//"Date","Type","To/From","Name","Status","Amount","Fees","Transaction ID","Reference"
		Date date=Date.parse('MM/dd/yyyy',items[0])
		String type=items[1]
		String toFrom=items[2]
		String name=items[3]
		String status=items[4]
		float amount=convertAmount(items[5])
		float fees=convertAmount(items[6])
		String transactionId=items[7]
		String reference=items[8]
		boolean skip=false
		if (merge) {
		  //SPTransaction trans=SPTransaction.findWhere(date:date,name:name,amount:amount)
		  SPTransaction trans=SPTransaction.findByTransactionId(transactionId)
		  if (trans!=null) skip=true
		}
		if (!skip) {
		  println("Adding transaction "+date+" "+name+" "+amount)
		  SPTransaction trans=new SPTransaction(date:date,type:type,toFrom:toFrom,name:name,status:status,amount:amount,fees:fees,transactionId:transactionId,reference:reference)
		  trans.save()
		}
	}
}
