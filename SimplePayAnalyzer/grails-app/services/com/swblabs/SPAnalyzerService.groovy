package com.swblabs

import grails.transaction.Transactional

@Transactional
class SPAnalyzerService {
	
	def updateMap(nameMap,trans) {
	  def rec=nameMap[trans.name]
	  if (rec==null) {
		  rec=["total":0,"totalTrans":0,"name":trans.name]
		  nameMap[trans.name]=rec
	  }
	  rec.total+=trans.amount
	  rec.totalTrans++
	}
	
    def analyze() {
		def nameMap=[:]
		SPTransaction.all.each {
			updateMap(nameMap,it)
		}
		println(nameMap)
		nameMap.values().sort{ a,b -> a.total<=>b.total}.each {
			println(it.toString()+" avg="+(it.total/it.totalTrans))
		}
    }
}
