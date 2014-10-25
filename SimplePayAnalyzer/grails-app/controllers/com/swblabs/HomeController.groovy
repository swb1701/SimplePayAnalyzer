package com.swblabs

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
		def active=SPSubscription.findAllByStatus('Active')
		["subs":active]
	}
	
	def meetups() {
		def map=AnalyticsService.getMeetupRevenueStats()
		["meetups":map]
	}
}
