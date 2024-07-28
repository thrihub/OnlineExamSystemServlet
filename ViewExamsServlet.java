import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ViewExamsServlet")
public class ViewExamsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/online_exam_system";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "admin"; // Replace with your MySQL password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("username");

            // Retrieve user_id based on username
            String userIdSql = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement userIdStmt = conn.prepareStatement(userIdSql);
            userIdStmt.setString(1, username);
            ResultSet userIdRs = userIdStmt.executeQuery();
            int userId = -1;
            if (userIdRs.next()) {
                userId = userIdRs.getInt("user_id");
            }
            userIdRs.close();
            userIdStmt.close();

            String sql = "SELECT * FROM exams";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }");
            out.println(".container { width: 80%; margin: 0 auto; padding: 20px; background-color: #fff; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
            out.println("h2 { color: #333; }");
            out.println("p { color: #555; }");
            out.println("a { display: inline-block; padding: 10px 20px; margin-top: 10px; color: #fff; background-color: #007bff; text-decoration: none; border-radius: 5px; }");
            out.println("a:hover { background-color: #0056b3; }");
            out.println("</style></head><body>");
            out.println("<div class='container'>");
            out.println("<h2>Select an Exam</h2>");

            while (rs.next()) {
                int examId = rs.getInt("exam_id");
                String examName = rs.getString("exam_name");
                out.println("<p>" + examName + "</p>");

                String checkSql = "SELECT * FROM results WHERE user_id = ? AND exam_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, examId);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    out.println("<a href='ViewResultsServlet?exam_id=" + examId + "'>View Results</a>");
                } else {
                    out.println("<a href='TakeExamServlet?exam_id=" + examId + "'>Take Exam</a>");
                }

                checkRs.close();
                checkStmt.close();
            }

            out.println("</div>");
            out.println("</body></html>");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Error occurred: " + e.getMessage() + "</h2>");
            out.println("<a href='loginSuccess.html'>Go back</a>");
            out.println("</body></html>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
