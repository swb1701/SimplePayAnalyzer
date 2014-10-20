package com.swblabs

class HomeController {
	
	def DataLoaderService
	def SPAnalyzerService
	def AWSWebDriverService

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
}
