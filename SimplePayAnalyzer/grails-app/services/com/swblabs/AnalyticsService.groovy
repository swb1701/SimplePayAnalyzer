package com.swblabs

import grails.transaction.Transactional

@Transactional
class AnalyticsService {

    def getMeetupRevenueStats() {
		def map=[:]
		SPTransaction.all.each { trans ->
			if (trans.spFor.startsWith("Meetup")) {
				def entry=map[trans.spFor]
				if (entry==null) {
					entry=[total:0.0,attendees:0,amounts:[],names:[]]
					map[trans.spFor]=entry
				}
				entry.total+=trans.amount
				entry.attendees+=1
				if (!entry.amounts.contains(trans.amount)) entry.amounts<<trans.amount
				if (!entry.names.contains(trans.name)) entry.names<<trans.name
			}
		}
		return(map)
    }
	
	def topics=["laser","novarrg","robot build","malware","kicad","safety orientation","prototype electronics","soldering","composites","cnc","arduino","takeapart","metal shaping","wearable",
		"raspberry pi","minecraft","woodworking","appinventor","lathe","python"]
}
