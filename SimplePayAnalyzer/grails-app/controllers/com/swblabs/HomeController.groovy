package com.swblabs

class HomeController {
	
	def DataLoaderService
	def SPAnalyzerService

    def index() { }
	
	def analyze() {
		SPAnalyzerService.analyze()
	}
	
	/*
	def load() {
	  def csvFile=grailsApplication.parentContext.getResource("data/all.csv").file
	  DataLoaderService.loadData(csvFile,false)
	}
	*/
}
