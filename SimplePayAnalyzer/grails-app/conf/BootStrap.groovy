import com.swblabs.Role
import com.swblabs.User
import com.swblabs.UserRole

class BootStrap {

	def DataLoaderService
	def GrailsApplication

	def createUser(name, password, role) {
		def me = new User(username: name, password: password, enabled: true).save()
		UserRole.create(me, role, true)
	}

	def init = { servletContext ->
		println 'Bootstrapping'
		if (Role.count==0) {
			println('Setting up accounts')
			def adminRole = new Role(authority: "ROLE_ADMIN").save()
			def userRole = new Role(authority: "ROLE_USER").save()
			createUser('admin', 'nladmin', adminRole)
			createUser('user', 'nluser', userRole)
		}
		//println("Loading Data...")
		//def csvFile=grailsApplication.parentContext.getResource("data/all.csv").file
		//DataLoaderService.loadData(csvFile,false)
	}
	def destroy = {
	}
}
