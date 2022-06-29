import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.sql.*;


@WebServlet("/Main")
public class Main extends HttpServlet implements Servlet {
	static final long serialVersionUID = 1L;

	public Main() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
			response.setContentType("text/html");
			String userID = request.getParameter("userID");
			PrintWriter out = response.getWriter();
			String msg = "";

			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/ATM?user=root&password=root"
				);
				Statement stmt = conn.createStatement();
				try {
					stmt.execute("drop table if exists accountInfo");
					stmt.execute(
						"create table if not exists accountInfo(userID integer, Name char(30), Balance decimal(15, 2))");
				} catch (Exception e) {
					System.out.println(e);
				}
				stmt.execute("Insert into accountInfo (userID, Name, Balance) values(1, \"alice\", 99.99)");
				stmt.execute("Insert into accountInfo (userID, Name, Balance) values(2, \"bob\", 100.00)");
				stmt.execute("Insert into accountInfo (userID, Name, Balance) values(3, \"frank\", 100.01)");

				ResultSet accountInfo = null; 
				if (!userID.isEmpty()) {
					accountInfo = stmt.executeQuery("Select * from accountInfo where userID = " + userID);
				} else {
					response.sendRedirect("http://localhost:8080/cs602-atm-0.0.1/Client.jsp");
				}

				if (accountInfo.next() == false) {
					msg = "Your authentication has failed, please try again";
				} else {
					System.out.println("User ID entered: " + userID);
					System.out.println("User ID in DB: " + accountInfo.getString(1));
					if (accountInfo.getString(1).equals(userID)) {
						msg = "Your authentication is successful";
					} else {
						msg = "Your authentication has failed, please try again";
					}
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			out.println("<html>");
			out.println("<head><title>ATM</title></head>");
			out.println("<body bgcolor=\"abc123\">");
			out.println("<center>");
			out.println("<h2>" + msg + "</h2>");
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
		} 
}