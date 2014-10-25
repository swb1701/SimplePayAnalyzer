package com.swblabs

import grails.transaction.Transactional

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxBinary
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile

@Transactional
class AWSWebDriverService {

	def DataLoaderService
	def getcsv=true //whether to get csv of all transactions
	def getsub=true //whether to crawl subscription data
	def grailsApplication
	def cookies=[:]

	//url for date ranges when fetching all transactions
	def csvUrl="https://payments.amazon.com/exportTransactions?searchfilter=all_activity&searchPeriod=LAST_SEVEN_DAYS&criteriaSelect=free&startMonth=10&startDay=11&startYear=2010&endMonth=10&endDay=18&endYear=2020&format=csv&x=44&y=9"

	def fillInDetails(cookies) {
		def errcnt=0 //consecutive errors
		SPTransaction.all.each { trans ->
			if (errcnt<10) { //if too many consecutive errors, probably something serious wrong
				if (trans.spFor==null) {
					try {
						def href="https://payments.amazon.com/txndetail?transactionId="+trans.transactionId
						Document doc=Jsoup.connect(href).cookies(cookies).timeout(15000).get()
						Elements fields=doc.select("table.txnDetails tr td")
						trans.dateCompleted=fields[3].text()
						trans.paymentMethod=fields[5].text()
						trans.spFor=fields[6].text()
						trans.referenceId=fields[9].text()
						println("Added detail:"+trans.spFor)
						trans.save()
						errcnt=0
					} catch (Exception e) {
						println("Error parsing details for transaction="+trans.transactionId)
						errcnt++
					}
				}
			}
		}
	}


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
		Thread.sleep(1000) //needed to allow page to come up
		driver.findElement(By.id("ap_email")).clear()
		driver.findElement(By.id("ap_email")).sendKeys(grailsApplication.config.aws.simplepay.email)
		driver.findElement(By.id("ap_password")).clear()
		driver.findElement(By.id("ap_password")).sendKeys(grailsApplication.config.aws.simplepay.password)
		driver.findElement(By.id("signInSubmit")).click()
		driver.manage().getCookies().each { cookie ->
			cookies[cookie.name]=cookie.value //capture all the cookies
		}

		if (getsub) {
			//table has: name, status (active/cancelled), created on (e.g. September 26, 2014), expires on (never expires), details
			//details has: (txn_detail_table) -- just gets you amount, subscription id
			/*
			 Pay: 	Nova Labs, Inc
			 For: 	Nova Labs Subscription for first last
			 Subscription Terms: 	Pay $100.00 every Month from 09/26/2014
			 Subscription Status: 	Active
			 Valid From: 	September 26, 2014
			 Valid Until: 	never expires
			 Subscription ID: 	xxx
			 */
			println("***Fetching detail links")
			def details=[]
			driver.findElement(By.linkText("Your Subscribers List")).click() //click on subscriber list
			while(true) {
				driver.findElements(By.cssSelector("a[href^=subscriptiondetail]")).each { detail ->
					println(detail.getAttribute("href"))
					details<<detail.getAttribute("href")
				}
				//break; //for debugging after one page
				try {
					def older=driver.findElement(By.linkText("Older subscriptions"))
					if (older==null) break;
					older.click()
				} catch (Exception e) {
					break;
				}
			}
			println("***Fetching details")
			/*
			 def href=details[0]
			 println(href)
			 Document doc=Jsoup.connect(href).cookies(cookies).get()
			 println(doc.toString())
			 doc.select("table.txn_detail_table tr td").each { element ->
			 println(element.text())
			 }
			 */
			details.each { href ->
				try {
					println(href)
					Document doc=Jsoup.connect(href).cookies(cookies).timeout(15000).get()
					Elements fields=doc.select("table.txn_detail_table tr td")
					def subid=fields[6].text()
					SPSubscription sub=SPSubscription.findBySubscriptionId(subid)
					if (sub==null) {
						println("Detected new subscription, adding to DB...")
						fields.each { println(it.text()) }
						//leave detail feeds as strings and then provide additional fields with parsed interpretation
						def vfdate=Date.parse('MMMM dd, yyyy',fields[4].text()) //parse valid from date
						//println("date="+vfdate)
						int pos=fields[1].text().indexOf(" for ")
						def pf=fields[1].text().substring(pos+5) //extract name from "For:" field
						//println("pf="+pf)
						//println("fields[2]="+fields[2].text())
						//extract amount, period, and date from terms field
						def matcher=fields[2].text()=~/Pay \$([0-9.]+) every ([^ ]+) from ([0-9][0-9]\/[0-9][0-9]\/[0-9][0-9][0-9][0-9])/
						//println(matcher[0][1]+","+matcher[0][2]+","+matcher[0][3])
						def spsub=new SPSubscription(
								payee:fields[0].text(),
								payfor:fields[1].text(),
								terms:fields[2].text(),
								status:fields[3].text(),
								validFrom:fields[4].text(),
								validUntil:fields[5].text(),
								subscriptionId:fields[6].text(),
								fromDate:vfdate,
								name:pf,
								amount:Float.parseFloat(matcher[0][1]),
								period:matcher[0][2])
						if (!spsub.save()) {
							spsub.errors.each { println it }
						}
					}
				} catch (Exception e) {
					e.printStackTrace()
				}
				/*
				 doc.select("table.txn_detail_table tr td").each { element ->
				 println(element.text())
				 }*/
			}
			/*
			 driver.findElements(By.cssSelector("table[id='ieflushtable'] tr td")).each { element -> //show that we can parse the table
			 println(element.text)
			 }
			 */
		}

		if (getcsv) { //if we need to fetch the csv we need to do it ourselves using the session cookies
			Thread.sleep(5000)
			Connection conn=Jsoup.connect(csvUrl)
			conn.timeout(60000) //it takes a long time to do a full report -- allow 60 seconds
			conn.ignoreContentType(true) //it will be a content type jsoup can't handle
			conn.cookies(cookies)
			/*
			 driver.manage().getCookies().each { cookie ->
			 //println("Cookie "+cookie.name+"="+cookie.value)
			 conn.cookie(cookie.name,cookie.value) //give all the cookies to jsoup
			 }
			 */
			fillInDetails(cookies) //do some filling in of details while the cookies are still fresh

			 println("Downloading transactions report...")
			 Connection.Response resp=conn.execute() //execute the url
			 File file=File.createTempFile("temp",".csv")
			 file<<resp.bodyAsBytes()
			 println("Parsing transactions report...")
			 DataLoaderService.loadData(file,true)
			 file.delete()

			//new File("o:/my.csv")<<resp.bodyAsBytes() //for testing put it to a file, but would normally just merge it into our current DB

			//if details are missing, fill them in with a detail query
			fillInDetails(cookies)
			
		}

		driver.quit()
	}
}
