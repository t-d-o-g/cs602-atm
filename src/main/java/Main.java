import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.*;


@WebServlet("/Main")
public class Main extends HttpServlet {
	static final long serialVersionUID = 1L;

	public Main() {
		super();
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
			out.println("<body bgcolor=\"abc123\">");
			out.println("<center>");
			out.println("<h2>" + succMsg + "</h2>");
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
		} 
}