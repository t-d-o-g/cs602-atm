<!DOCTYPE html>
<html>
	<head>
		<title>NJIT Credit Union</title>
	</head>
	<body bgcolor="#fef666">
		<center>	
			<h1>NJIT Credit Union</h1>
			<h3>Please enter your User ID:</h3>
			<%
				out.println("<form action=\"http://localhost:8080/cs602-atm-0.0.1/Main\" method=\"POST\">");
				out.println("UserID: <input type=\"text\" name=\"userID\"><br>");
				out.println("Pin: <input type=\"text\" name=\"pin\"><br>");
				out.println("<input type=\"submit\" value=\"Submit\">");
				out.println("</form>");
			%>
		</center>
	</body>
</html>