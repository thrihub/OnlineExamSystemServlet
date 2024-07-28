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

@WebServlet("/TakeExamServlet")
public class TakeExamServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/online_exam_system";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "admin"; // Replace with your MySQL password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String exam_idStr = request.getParameter("exam_id");

        if (exam_idStr == null || exam_idStr.isEmpty()) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Exam ID is missing. Please provide a valid Exam ID.</h2>");
            out.println("<p>URL: " + request.getRequestURL() + "</p>");
            out.println("<p>Query String: " + request.getQueryString() + "</p>");
            out.println("<a href='takeExam.html'>Go back</a>");
            out.println("</body></html>");
            return;
        }

        int exam_id;
        try {
            exam_id = Integer.parseInt(exam_idStr);
        } catch (NumberFormatException e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Invalid Exam ID format. Please provide a valid Exam ID.</h2>");
            out.println("<p>Provided Exam ID: " + exam_idStr + "</p>");
            out.println("<a href='takeExam.html'>Go back</a>");
            out.println("</body></html>");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
            String sql = "SELECT * FROM questions WHERE exam_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, exam_id);
            rs = stmt.executeQuery();

            // Store exam_id in session
            HttpSession session = request.getSession();
            session.setAttribute("exam_id", exam_id);

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Take Exam</title>");
            out.println("<style>");
            out.println("body {font-family: Arial, sans-serif; background-color: #f2f2f2; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0;}");
            out.println(".container {background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); width: 80%; max-width: 600px; height: 80vh; overflow-y: auto;}");
            out.println("h2 {margin-top: 0;}");
            out.println("label {display: block; margin: 10px 0 5px;}");
            out.println(".question {margin-bottom: 15px; border-bottom: 1px solid #ddd; padding-bottom: 10px;}");
            out.println("input[type='radio'] {margin-right: 10px;}");
            out.println("input[type='submit'] {background-color: #4CAF50; color: white; border: none; padding: 10px 15px; font-size: 16px; border-radius: 4px; cursor: pointer;}");
            out.println("input[type='submit']:hover {background-color: #45a049;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h2>Take Exam</h2>");
            out.println("<form method='post' action='SubmitResponsesServlet'>");
            out.println("<input type='hidden' name='exam_id' value='" + exam_id + "'>");

            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                String questionText = rs.getString("question_text");
                String optionA = rs.getString("option_a");
                String optionB = rs.getString("option_b");
                String optionC = rs.getString("option_c");
                String optionD = rs.getString("option_d");

                out.println("<div class='question'>");
                out.println("<label>Question " + questionId + ":</label>");
                out.println("<p>" + questionText + "</p>");
                out.println("<input type='radio' id='q" + questionId + "_optionA' name='question_" + questionId + "' value='A'>");
                out.println("<label for='q" + questionId + "_optionA'>" + optionA + "</label><br>");
                out.println("<input type='radio' id='q" + questionId + "_optionB' name='question_" + questionId + "' value='B'>");
                out.println("<label for='q" + questionId + "_optionB'>" + optionB + "</label><br>");
                out.println("<input type='radio' id='q" + questionId + "_optionC' name='question_" + questionId + "' value='C'>");
                out.println("<label for='q" + questionId + "_optionC'>" + optionC + "</label><br>");
                out.println("<input type='radio' id='q" + questionId + "_optionD' name='question_" + questionId + "' value='D'>");
                out.println("<label for='q" + questionId + "_optionD'>" + optionD + "</label>");
                out.println("</div>");
            }

            out.println("<input type='submit' value='Submit Responses'>");
            out.println("</form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Error occurred: " + e.getMessage() + "</h2>");
            out.println("<a href='takeExam.html'>Go back</a>");
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
