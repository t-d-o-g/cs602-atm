import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.*;
import java.math.BigDecimal;


@WebServlet("/Main")
public class Main extends HttpServlet {
	static final long serialVersionUID = 1L;

	public Main() {
		super();
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			String transMsg = "";
			String withdrawEnabled = "<input type=\"radio\" name=\"rd\" value=\"withdraw\" checked=\"checked\">";
			String withdrawRd = "<input type=\"radio\" name=\"rd\" value=\"withdraw\">";
			String depositEnabled = "<input type=\"radio\" name=\"rd\" value=\"deposit\" checked=\"checked\">";
			String depositRd = "<input type=\"radio\" name=\"rd\" value=\"deposit\">";
			BigDecimal amount = new BigDecimal("0.00");
			BigDecimal balance = new BigDecimal("0.00");


			String transaction = request.getParameter("rd");
			System.out.println("Transaction: " + transaction);
			
			if (transaction.equals("withdraw")) {
				withdrawRd = withdrawEnabled;
				try {
					amount = new BigDecimal(request.getParameter("withdraw"));
				} catch (Exception e) {
					transMsg = "Please enter a withdraw amount";
				}
				balance = withdraw(amount);

			} else {
				depositRd = depositEnabled;
				try {
					amount = new BigDecimal(request.getParameter("deposit"));
				} catch (Exception e) {
					transMsg = "Please enter a deposit amount";
				}
				balance = deposit(amount);
			}

			String msg = transMsg.isEmpty() ? "Balance: $" + balance.toString() : transMsg;
			out.println("<html>");
			out.println("<head><title>ATM</title></head>");
			out.println("<body bgcolor=\"fef666\">");
			out.println("<form action=\"http://localhost:8080/cs602-atm-0.0.1/Client.jsp\" method=\"GET\">");
			out.println("<input type=\"submit\" value=\"Logout\">");
			out.println("</form>");
			out.println("<center>");
			out.println("<h2>" + msg + "</h2>");
			out.println("<form action=\"http://localhost:8080/cs602-atm-0.0.1/Main\" method=\"GET\">");
			out.println(withdrawRd);
			out.println("Withdraw: <input type=\"text\" name=\"withdraw\"><br>");
			out.println(depositRd);
			out.println("Deposit: <input type=\"text\" name=\"deposit\"><br>");
			out.println("<input type=\"submit\" value=\"Submit\">");
			out.println("</form>");
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
			response.setContentType("text/html");
			response.setHeader("WWW-Authenticate", "BASICATM realm=\"atm\"");
			String userID = request.getParameter("userID");
			String pin = request.getParameter("pin");
			PrintWriter out = response.getWriter();
			String errMsg = "Your authentication has failed, please try again";
			String succMsg = "Your authentication is successful";

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

				ResultSet accountInfo = null; 
				if (!userID.isEmpty())
					accountInfo = stmt.executeQuery("Select * from accountInfo where userID = " + userID);
				else
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errMsg);

				if (accountInfo.next() == false)
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errMsg);
				else
					if (!accountInfo.getString(3).equals(pin))
					  response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errMsg);
			} catch (Exception e) {
				System.out.println(e);
			}
			out.println("<html>");
			out.println("<head><title>ATM</title></head>");
			out.println("<body bgcolor=\"fef666\">");
			out.println("<form action=\"http://localhost:8080/cs602-atm-0.0.1/Client.jsp\" method=\"GET\">");
			out.println("<input type=\"submit\" value=\"Logout\">");
			out.println("</form>");
			out.println("<center>");
			out.println("<h2 style=\"color:green\">" + succMsg + "</h2>");
			out.println("<form action=\"http://localhost:8080/cs602-atm-0.0.1/Main\" method=\"GET\">");
			out.println("<input type=\"radio\" name=\"rd\" value=\"withdraw\" checked=\"checked\">");
			out.println("Withdraw: <input type=\"text\" name=\"withdraw\"><br>");
			out.println("<input type=\"radio\" name=\"rd\" value=\"deposit\">");
			out.println("Deposit: <input type=\"text\" name=\"deposit\"><br>");
			out.println("<input type=\"submit\" value=\"Submit\">");
			out.println("</form>");
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
		} 
	
		private BigDecimal withdraw(BigDecimal amount) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/ATM?user=root&password=root"
				);
				Statement stmt = conn.createStatement();
			} catch (Exception e) {
				System.out.println(e);
			}
			
			return amount;
		}
		
		private BigDecimal deposit(BigDecimal amount) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/ATM?user=root&password=root"
				);
				Statement stmt = conn.createStatement();
			} catch (Exception e) {
				System.out.println(e);
			}

			return amount;
		}
}