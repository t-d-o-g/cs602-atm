<!DOCTYPE html>
<html>
	<head>
		<title>NJIT Credit Union</title>
	</head>
	<body bgcolor="#abc123">
		<center>	
			<h1>NJIT Credit Union</h1>
			<h3>Please enter your User ID:</h3>
			<%
				out.println("<form action=\"http://localhost:8080/cs602-atm-0.0.1/Main\">");
					out.println("UserID: <input type=\"text\" name=\"userID\"><br>");
					out.println("<input type=\"submit\" value=\"Submit\">");
			%>
			</form>
		</center>
	</body>
</html>