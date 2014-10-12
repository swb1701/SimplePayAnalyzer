package com.swblabs

class HomeController {
	
	def DataLoaderService

    def index() { }
	
	def load() {
	  def csvFile=grailsApplication.parentContext.getResource("data/all.csv").file
	  DataLoaderService.loadData(csvFile,false)
	}
}
