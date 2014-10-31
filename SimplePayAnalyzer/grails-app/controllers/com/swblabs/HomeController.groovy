package com.swblabs

import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_ADMIN","ROLE_USER"])
class HomeController {
	
	def DataLoaderService
	def SPAnalyzerService
	def AWSWebDriverService
	def AnalyticsService

    def index() { }
	
	def analyze() {
		SPAnalyzerService.analyze()
	}
	
	def aws() {
		AWSWebDriverService.loadAWSData()	
	}
	
	def subs() {
		def active=SPSubscription.findAllByStatus('Active').collect { sub->
			[name:sub.name,amount:sub.amount,fromDate:sub.fromDate.format('yyyy-MM-dd')]
		}
		["subs":active]
	}
	
	def subgraph() {
		def active=SPSubscription.findAllByStatus('Active').collect { sub->
			[name:sub.name,amount:sub.amount,fromDate:sub.fromDate.format('yyyy-MM-dd')]
		}
		[data:active]
	}

	def meetups() {
		def map=AnalyticsService.getMeetupRevenueStats()
		["meetups":map]
	}
	
	def dates() {
		def dates=AWSWebDriverService.getRecentDates()
		println(dates)
	}
}
