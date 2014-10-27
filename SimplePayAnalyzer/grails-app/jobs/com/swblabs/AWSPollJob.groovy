package com.swblabs

class AWSPollJob {
	static triggers = {
		//simple startDelay: 60000, repeatInterval: 60000
	}

	def AWSWebDriverService

	def execute() {
	  AWSWebDriverService.loadAWSData()
	}
}
