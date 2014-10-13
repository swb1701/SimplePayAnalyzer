package com.swblabs

import grails.transaction.Transactional

@Transactional
class SPAnalyzerService {
	
	def updateMap(nameMap,trans,oldindex,newindex) {
	  def rec=nameMap[trans.name]
	  if (rec==null) {
		  rec=["total":0,"totalTrans":0,"name":trans.name,"months":new Object[newindex-oldindex+1]]
		  nameMap[trans.name]=rec
	  }
	  rec.total+=trans.amount
	  rec.totalTrans++
	  def monthIndex=getMonthIndex(trans.date)-oldindex
	  def oldmonth=rec.months[monthIndex]
	  if (oldmonth==null) {
		  oldmonth=["max":0]
		  rec.months[monthIndex]=oldmonth
	  }
	  if (trans.amount>oldmonth.max) {
		  oldmonth.max=trans.amount
	  }
	}
	
    def analyze() {
		def range=getDateRange()
		def oldest=range[0]
		def newest=range[1]
		def oldindex=getMonthIndex(oldest)
		def newindex=getMonthIndex(newest)
		println("oldest date="+oldest+" newest="+newest+" range="+(newindex-oldindex))
		def nameMap=[:]
		SPTransaction.all.each {
			updateMap(nameMap,it,oldindex,newindex)
		}
		println(nameMap)
		nameMap.values().sort{ a,b -> a.total<=>b.total}.each {
			println(it.toString()+" avg="+(it.total/it.totalTrans))
		}
    }
	
	def getMonthIndex(Date date) {
		Calendar cal=Calendar.getInstance()
		cal.setTime(date)
		int year=cal.get(Calendar.YEAR)
		int mon=cal.get(Calendar.MONTH)
		return(year*12+mon)
	}
	
	def getDateRange() {
		def all=SPTransaction.all
		def result=[all.first().date,all.first().date]
		all.each {
			if (it.date.compareTo(result[0])<0) {
				result[0]=it.date
			}
			if (it.date.compareTo(result[1])>0) {
				result[1]=it.date
			}
		}
		return(result)
	}
}
