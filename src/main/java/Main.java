import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.*;
import java.math.BigDecimal;
import java.math.RoundingMode;


class InsufficientFundsException extends Exception {
    InsufficientFundsException(String message) {
        super(message);
    }
}

class DecimalPlaceException extends Exception {
    DecimalPlaceException(String message) {
        super(message);
    }
}

@WebServlet("/Main")
public class Main extends HttpServlet {
	static final long serialVersionUID = 1L;
	private static final BigDecimal minAmount = new BigDecimal("0.00");
	private String userID;
	private String name;
	private String balanceStr;

	public Main() {
		super();
		seedData();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
			if (request.getParameter("login") != null) {
				response.setContentType("text/html");
				response.setHeader("WWW-Authenticate", "BASICATM realm=\"atm\"");
				userID = request.getParameter("userID");
				String pin = request.getParameter("pin");
				String errMsg = "Your authentication has failed, please try again";
				String succMsg = "Your authentication is successful";
				String withdrawRd = "<input type=\"radio\" name=\"rd\" value=\"withdraw\" checked=\"checked\">";
				String depositRd = "<input type=\"radio\" name=\"rd\" value=\"deposit\">";

				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/ATM?user=root&password=root"
					);
					Statement stmt = conn.createStatement();
					ResultSet accountInfo = null; 
					if (!userID.isEmpty() && userID.matches("[0-9]+"))
						accountInfo = stmt.executeQuery("Select * from accountInfo where userID = " + userID);
					else
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errMsg);

					if (accountInfo.next() == false)
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errMsg);
					else
						if (!accountInfo.getString(3).equals(pin)) {
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errMsg);
						} else {
							name = accountInfo.getString(2); 
							balanceStr = accountInfo.getString(4); 
						}
				} catch (Exception e) {
					System.out.println(e);
				}
				
				String msg = "<h3 class=\"succ-msg\">" + succMsg + "</h3>";
				display(response, msg, withdrawRd, depositRd);
			} else if (request.getParameter("withdraw-deposit") != null) {
				response.setContentType("text/html");
				String paramVal = "";
				String errMsg = "";
				String withdrawEnabled = "<input type=\"radio\" name=\"rd\" value=\"withdraw\" checked=\"checked\">";
				String withdrawRd = "<input type=\"radio\" name=\"rd\" value=\"withdraw\">";
				String depositEnabled = "<input type=\"radio\" name=\"rd\" value=\"deposit\" checked=\"checked\">";
				String depositRd = "<input type=\"radio\" name=\"rd\" value=\"deposit\">";
				BigDecimal amount = new BigDecimal("0.00");
				BigDecimal balance = new BigDecimal((String) balanceStr);

				String transaction = request.getParameter("rd");
				
				if (transaction.equals("withdraw")) {
					withdrawRd = withdrawEnabled;
					paramVal = request.getParameter("withdraw");
					try {
						amount = new BigDecimal(paramVal);
					} catch (Exception e) {
						errMsg = "Please enter a monetary value greater than 0";
					}
					if (amount.compareTo(minAmount) <= 0) {
						errMsg = "Please enter a monetary value greater than 0";
					} else {
						try {
							withdraw(balance, amount);
						} catch (InsufficientFundsException e) {
							errMsg = e.getMessage();
						} catch (DecimalPlaceException e) {
							errMsg = e.getMessage();
						}
					}
				} else {
					paramVal = request.getParameter("deposit");
					depositRd = depositEnabled;
					try {
						amount = new BigDecimal(paramVal);
					} catch (Exception e) {
						errMsg = "Please enter a monetary value";
					}
					if (amount.compareTo(minAmount) <= 0) {
						errMsg = "Please enter a monetary value greater than 0";
					} else {
						try {
							deposit(balance, amount);
						} catch (DecimalPlaceException e) {
							errMsg = e.getMessage();
						}
				}
			}

			String msg = errMsg.isEmpty() 
				? "<h3> Your account balance is $" + balanceStr + "</h3>"
				: "<h3 class=\"err-msg\">" + errMsg + "</h3>";
			display(response, msg, withdrawRd, depositRd);

			}
	} 

	private void display(HttpServletResponse response, String msg, String withdrawRd, String depositRd)
		throws IOException {
			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head>");
			out.println("<title>NJIT Credit Union</title>");
			out.println("<style>");
			out.println("html, body {background-color:#ffffcc; text-align:center;}"); 
			out.println("#form {display:inline-block; margin:0 auto; font-family:Arial; font-size:18px; text-align:left}");
			out.println("#fields {display:inline-block; width:50%; margin:100px auto; padding:0 65px}");
			out.println(".header {background-color:#071D49; border:3px solid #C1C6C8; color:white; width:50%; margin:20px auto; padding: 10px;}"); 
			out.println(".err-msg {color:#D22630}"); 
			out.println(".succ-msg {color:green;}"); 
			out.println(".submit-btn {width:80; height:25; font-size:15; margin:50px 35%}"); 
			out.println(".logout-btn {width:80px; height:25px; font-size:15px; margin:0 auto}"); 
			out.println("input {margin:10px; font-size:18px; outline:none; background-color:#eee; border:3px solid #ccc}"); 
			out.println("input[type=text]:focus {border:3px solid #071D49}"); 
			out.println("input[type=radio] {accent-color:#071D49}"); 
			out.println("</style>");
			out.println("<head>");

			out.println("<body>");
			out.println("<div class=\"header\">");
			out.println("<h1>Hello " + name + "!</h1>");
			out.println("<form action=\"http://localhost:8080/cs602-atm-0.0.1/Client.jsp\" method=\"POST\">");
			out.println("<button class=\"logout-btn\" type=\"submit\">Logout</button>");
			out.println("</form>");
			out.println("</div>");
			out.println(msg);
			out.println("<div id=\"form\">");
			out.println("<form action=\"http://localhost:8080/cs602-atm-0.0.1/Main\" method=\"POST\">");
			out.println("<div id=\"fields\">");
			out.println(withdrawRd);
			out.println("Withdraw: <input type=\"text\" name=\"withdraw\"><br>");
			out.println(depositRd);
			out.println("Deposit: <input type=\"text\" name=\"deposit\"><br>");
			out.println("<button class=\"submit-btn\" type=\"submit\" name=\"withdraw-deposit\">Submit</button>");
			out.println("</div>");
			out.println("</form>");
			out.println("</div>");
			out.println("</body>");
			out.println("</html>");
	}

	private void seedData() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/ATM?user=root&password=root"
			);
			Statement stmt = conn.createStatement();
			try {
				stmt.execute("drop table if exists accountInfo");
				stmt.execute(
					"create table if not exists accountInfo(userID integer, Name char(30), Pin char(4), Balance decimal(15, 2))");
			} catch (Exception e) {
				System.out.println(e);
			}
			stmt.execute("Insert into accountInfo (userID, Name, Pin, Balance) values(1, \"alice\", \"1234\", 99.99)");
			stmt.execute("Insert into accountInfo (userID, Name, Pin, Balance) values(2, \"bob\", \"2345\", 100.00)");
			stmt.execute("Insert into accountInfo (userID, Name, Pin, Balance) values(3, \"frank\", \"3456\", 100.01)");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void withdraw(BigDecimal balance, BigDecimal amount) throws InsufficientFundsException, DecimalPlaceException {
		int decimalIndex = amount.toString().indexOf("."); 
		int decimalPlaces = decimalIndex == -1
			? 0
			: amount.toString().length() - decimalIndex - 1;

		if (decimalPlaces > 2) {
			throw new DecimalPlaceException("Please enter amount rounded to the nearest hundredth");
		}
		amount = amount.setScale(2, RoundingMode.FLOOR);
		BigDecimal updatedBalance = balance.subtract(amount);

		if (updatedBalance.compareTo(minAmount) < 0)
			throw new InsufficientFundsException("There is insufficient funds, please try a smaller amount");

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/ATM?user=root&password=root"
			);
			Statement stmt = conn.createStatement();
			stmt.execute("update accountInfo set balance = " + updatedBalance + "where userID = " + userID);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		balanceStr = updatedBalance.toString();
	}
	
	private void deposit(BigDecimal balance, BigDecimal amount) throws DecimalPlaceException {
		int decimalIndex = amount.toString().indexOf("."); 
		int decimalPlaces = decimalIndex == -1
			? 0
			: amount.toString().length() - decimalIndex - 1;

		if (decimalPlaces > 2) {
			throw new DecimalPlaceException("Please enter amount rounded to the nearest hundredth");
		}
		amount = amount.setScale(2, RoundingMode.FLOOR);
		BigDecimal updatedBalance = balance.add(amount);

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/ATM?user=root&password=root"
			);
			Statement stmt = conn.createStatement();
			stmt.execute("update accountInfo set balance = " + updatedBalance + "where userID = " + userID);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		balanceStr = updatedBalance.toString();
	}
}