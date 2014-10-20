<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="main"/>
<title>Active Subscriptions</title>
</head>
<body>
  <div class="body">
<table>
<tr><th>Name</th><th>Amount</th><th>Since Date</th></tr>
<g:each in="${subs.sort{it.amount}.reverse()}" var="sub">
<tr><td>${sub.name}</td><td>${sub.amount}</td><td>${sub.fromDate}</td></tr>
</g:each>
</table>  
  </div>
</body>
</html>