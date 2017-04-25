package lg;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Vector;


@WebServlet(name = "Server")
public class Server extends HttpServlet {

    static RecordManager recman;
    private static final String DATABASE_NAME = "index";
    private static final String STOPWORDS_FILE_NAME = "/stopwords.txt";
    private static final int TOP_K_RESULTS = 50;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (recman == null) {
            System.out.println("Database is Null");
            return;
        }

        try {
            Querier querier = new Querier(readStopWordsFile(STOPWORDS_FILE_NAME), recman);
            request.setAttribute("queryResult", querier.NaiveSearch(request.getParameter("query"), TOP_K_RESULTS));
        } catch (Exception e) {
            System.err.println("Error");
            System.err.println(e.toString());
        }

        getServletContext().getRequestDispatcher("/home.jsp").forward(request, response);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (recman == null) {
            recman = RecordManagerFactory.createRecordManager(DATABASE_NAME);
            doCrawling(readStopWordsFile(STOPWORDS_FILE_NAME));
        }
        getServletContext().getRequestDispatcher("/home.jsp").forward(request, response);
    }


    public void doCrawling(BufferedReader br) {
        try {
            Crawler crawler = new Crawler(br, recman);
            // Initialization
            System.out.println("Initializing..");
            System.out.print("Base URL: ");
            for (int i = 1; i <= 1000; ) {
                int state = crawler.crawl();
                if (state > 0)
                    System.out.print("Website " + (i++) + ": ");
                else if (state < 0)
                    break;
            }
            System.out.println("Max Reached");
            recman.commit();

        } catch (Exception e) {
            System.err.println("Error");
            System.err.println(e.toString());
        }

    }


    public BufferedReader readStopWordsFile(String name) {
        return new BufferedReader(new InputStreamReader(getServletContext().getResourceAsStream(name)));
    }
}
