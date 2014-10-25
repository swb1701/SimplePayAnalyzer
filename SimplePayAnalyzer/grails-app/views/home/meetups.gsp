<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="main"/>
<title>Meetup Stats</title>
</head>
<body>
  <div class="body">
<table>
<tr><th>Meetup</th><th>Revenue</th><th>Attendees</th><th>Amounts Payed</th></tr>
<g:each in="${meetups.entrySet().sort{it.value.total}.reverse()}" var="meetup">
<tr><td>${meetup.key}</td><td>${meetup.value.total}</td><td>${meetup.value.attendees} -- ${meetup.value.names}</td><td>${meetup.value.amounts}</td></tr>
</g:each>
</table>  
  </div>
</body>
</html>