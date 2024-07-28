import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterUserServlet")
public class RegisterUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/online_exam_system";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "admin"; // Replace with your MySQL password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        request.getRequestDispatcher("/registeruser.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("user_id");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (userId == null || userId.isEmpty() || username == null || username.isEmpty() || password == null || password.isEmpty() || role == null || role.isEmpty()) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body bgcolor=\"cyan\" align=\"center\">");
            out.println("<h2>All fields are required!</h2>");
            out.println("<a href='RegisterUserServlet'>Go back</a>");
            out.println("</body></html>");
            return;
        }

        try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver loaded Successfully");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (user_id, username, password, role) VALUES (?, ?, ?, ?)")) {

            

            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body bgcolor=\"cyan\" align=\"center\">");
            out.println("<h2>User registered successfully</h2>");
            out.println("<a href=\"viewresults.html\"> Click here to see Results</a> ");
            out.println("<br><br>");
            out.println("<a href='RegisterUserServlet'>Go back</a>");
            out.println("</body></html>");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body bgcolor=\"cyan\" align=\"center\">");
            out.println("<h2>Error occurred: " + e.getMessage() + "</h2>");
            out.println("<a href='RegisterUserServlet'>Go back</a>");
            out.println("</body></html>");
        }
    }
}
