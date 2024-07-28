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

@WebServlet("/SubmitResponsesServlet")
public class SubmitResponsesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/online_exam_system";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "admin"; // Replace with your MySQL password

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            redirectToErrorPage(response, "Session has expired. Please start the exam again.");
            return;
        }

        Integer exam_id = (Integer) session.getAttribute("exam_id");
        Integer user_id = (Integer) session.getAttribute("user_id");

        // Log the attributes
        System.out.println("Exam ID retrieved from session: " + exam_id);
        System.out.println("User ID retrieved from session: " + user_id);

        if (exam_id == null || user_id == null) {
            redirectToErrorPage(response, "No Exam ID or User ID found in session. Please start the exam again.");
            return;
        }

        // Retrieve user responses
        int score = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            // Calculate score based on responses
            String fetchQuestionsSql = "SELECT question_id, correct_option FROM questions WHERE exam_id = ?";
            stmt = conn.prepareStatement(fetchQuestionsSql);
            stmt.setInt(1, exam_id);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                String correctOption = rs.getString("correct_option");

                String userAnswer = request.getParameter("question_" + questionId);
                if (correctOption.equals(userAnswer)) {
                    score++;
                }
            }

            rs.close();
            stmt.close();

            // Insert score into the results table
            String insertSql = "INSERT INTO results (user_id, exam_id, score) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(insertSql);
            stmt.setInt(1, user_id);
            stmt.setInt(2, exam_id);
            stmt.setInt(3, score);
            stmt.executeUpdate();

            // Redirect to the results page with score
            response.sendRedirect("viewresults.html?score=" + score);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            redirectToErrorPage(response, "Error occurred: " + e.getMessage());
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

    private void redirectToErrorPage(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>" + message + "</h2>");
        out.println("<a href='takeExam.html'>Go back</a>");
        out.println("</body></html>");
    }
}
