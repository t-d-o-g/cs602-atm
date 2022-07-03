<!DOCTYPE html>
<html>
	<head>
		<title>NJIT Credit Union</title>
		<style>
			html, body {
				background-color:#ffffcc;
				text-align: center;
			}
			#form {
				display: inline-block;
				margin: 0 auto;
				font-family: Arial; 
				font-size: 18px;
				text-align: left;
			}
			#fields {
				display: inline-block;
				width: 50%;
				margin: 100px auto;
				padding: 0 65px;
			}
			.header {
				background-color: #071D49;
				border: 3px solid #C1C6C8;
				color: white;
				width: 50%;
				margin: 20px auto;
				padding: 25px;
			}
			.submit-btn {
				width: 100px;
				height: 30px;
				font-size: 20px;
				margin: 50px 35%;
			}
			.err-msg {
				color: #D22630;
			}
			input {
				margin: 10px;
				font-size: 18px;
				outline: none;
				background-color: #eee;
				border: 3px solid #ccc;
			}
			input[type=text]:focus {
				border: 3px solid #071D49;
			}
		</style>
	</head>
	<body>
		<div class="header">
			<h1>NJIT Credit Union</h1>
		</div>
		<%
			out.println("<h3 class=\"err-msg\">Your authentication has failed, please try again</h3>");
			out.println("<div id=\"form\">");
			out.println("<form action=\"http://localhost:8080/cs602-atm-0.0.1/Main\" method=\"POST\">");
			out.println("<div id=\"fields\">");
			out.println("<span>User ID:</span> <input type=\"text\" name=\"userID\"><br>");
			out.println("<span>Pin:</span> <input type=\"text\" name=\"pin\"><br>");
			out.println("<button class=\"submit-btn\" type=\"submit\">Submit</button>");
			out.println("</div>");
			out.println("</form>");
			out.println("</div>");
		%>
	</body>
</html>