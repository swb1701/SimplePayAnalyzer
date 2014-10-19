package com.swblabs

import grails.transaction.Transactional

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxBinary
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile

@Transactional
class AWSWebDriverService {

	def getcsv=false //whether to get csv of all transactions
	def getsub=true //whether to crawl subscription data
	def grailsApplication

	//url for date ranges when fetching all transactions
	def csvUrl="https://payments.amazon.com/exportTransactions?searchfilter=all_activity&searchPeriod=LAST_SEVEN_DAYS&criteriaSelect=free&startMonth=10&startDay=11&startYear=2010&endMonth=10&endDay=18&endYear=2020&format=csv&x=44&y=9"

	def loadAWSData() {
		//invoke Firefox through web driver (usually won't require specific path -- we'll move this out to a config file if it stays)
		WebDriver driver=new FirefoxDriver(new FirefoxBinary(new File("e:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe")),new FirefoxProfile())
		/*//for reference: taking a screenshot example:
		 File snap=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE)
		 FileUtils.copyFile(snap,new File("o:/snap.png"))
		 */
		//use webdriver to get logged in
		def baseUrl="https://payments.amazon.com/"
		driver.get(baseUrl + "/merchant#signin")
		driver.findElement(By.id("login-dropdown-menu")).click() //added manually to trigger dropdown
		driver.findElement(By.id("merchant-sign-in")).click()
		driver.findElement(By.xpath("(//input[@name='product'])[3]")).click()
		driver.findElement(By.cssSelector("input.amzn-button")).click()
		Thread.sleep(2000) //needed to allow page to come up
		driver.findElement(By.id("ap_email")).clear()
		driver.findElement(By.id("ap_email")).sendKeys(grailsApplication.config.aws.simplepay.email)
		driver.findElement(By.id("ap_password")).clear()
		driver.findElement(By.id("ap_password")).sendKeys(grailsApplication.config.aws.simplepay.password)
		driver.findElement(By.id("signInSubmit")).click()

		if (getcsv) { //if we need to fetch the csv we need to do it ourselves using the session cookies
			Connection conn=Jsoup.connect(csvUrl)
			conn.timeout(15000) //it takes a long time to do a full report -- allow 15 seconds
			conn.ignoreContentType(true) //it will be a content type jsoup can't handle
			driver.manage().getCookies().each { cookie ->
				//println("Cookie "+cookie.name+"="+cookie.value)
				conn.cookie(cookie.name,cookie.value) //give all the cookies to jsoup
			}
			Connection.Response resp=conn.execute() //execute the url
			new File("o:/my.csv")<<resp.bodyAsBytes() //for testing put it to a file, but would normally just merge it into our current DB
		}
		
		if (getsub) {
			driver.findElement(By.linkText("Your Subscribers List")).click() //click on subscriber list
			driver.findElements(By.cssSelector("table[id='ieflushtable'] tr td")).each { element -> //show that we can parse the table
				println(element.text)
			}
		}
		
		driver.quit()
	}
}
