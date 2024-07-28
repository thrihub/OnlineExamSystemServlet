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

@WebServlet("/ShowResultsServlet")
public class ShowResultsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/online_exam_system";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "admin"; // Replace with your MySQL password

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String examId = request.getParameter("exam_id");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            String sql = "SELECT exam_name FROM exams WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(examId));
            rs = stmt.executeQuery();

            String examName = null;
            if (rs.next()) {
                examName = rs.getString("exam_name");
            }

            if (examName != null) {
                // Fetch the score for the selected exam (assuming you have a user_id in the session)
                String userId = request.getSession().getAttribute("user_id").toString();
                String scoreSql = "SELECT score FROM results WHERE user_id = ? AND exam_id = ?";
                PreparedStatement scoreStmt = conn.prepareStatement(scoreSql);
                scoreStmt.setInt(1, Integer.parseInt(userId));
                scoreStmt.setInt(2, Integer.parseInt(examId));
                ResultSet scoreRs = scoreStmt.executeQuery();

                int score = 0;
                if (scoreRs.next()) {
                    score = scoreRs.getInt("score");
                }

                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><head><link rel='stylesheet' type='text/css' href='styles.css'></head><body>");
                out.println("<div class='container'>");
                out.println("<h2>Results for " + examName + "</h2>");
                out.println("<p>Your score: " + score + "</p>");
                out.println("<a href='loginSuccess.html'>Go back</a>");
                out.println("</div>");
                out.println("</body></html>");
            } else {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h2>Invalid exam selected.</h2>");
                out.println("<a href='viewAvailableExams.html'>Go back</a>");
                out.println("</body></html>");
            }
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
