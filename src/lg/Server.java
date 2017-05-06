package lg;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


@WebServlet(name = "Server")
public class Server extends HttpServlet {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    History history;
    Querier querier;

    public Server() throws Exception {
        try {
            history = new History();
            querier = new Querier();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String userId = randomAlphaNumeric(12);
        boolean cookieExists = false;
        if(cookies != null) {
            for (Cookie cookie: cookies) {
                if(cookie.getName().equals("userId")) {
                    cookieExists = true;
                    userId = cookie.getValue();
                }
            }
        }

        if(!cookieExists) {
            Cookie cookie = new Cookie("userId", userId);
            response.addCookie(cookie);
            System.out.println("New Cookie Saved");
        }


        if(request.getParameter("query") != null){
            long start_time = 0;
            long end_time = 0;
            try {
                history.addEntry(userId, request.getParameter("query"));
                request.setAttribute("suggest", null);
                start_time = System.nanoTime();
                SearchResult searchResult = querier.NaiveSearch(request.getParameter("query"), Integer.parseInt(request.getParameter("topk")), Double.parseDouble(request.getParameter("simValue")));
                end_time = System.nanoTime();
                Vector<PageInfo> queryResult = searchResult.PageInfoVector;
                request.setAttribute("queryResult", queryResult);
                Set<String> queryHistory = new HashSet<String>(Arrays.asList(history.getHistory(userId)));
                for(String s: history.getHistory(userId)) {
                    System.out.println(s);
                }
                if(searchResult.SuggestedQuery.indexOf((String)request.getParameter("query")) != 0){
                    request.setAttribute("suggest", searchResult.SuggestedQuery);
                }
                request.setAttribute("queryHistory", queryHistory);

            } catch (Exception e) {
                System.err.println("Error");
                e.printStackTrace();
            }
            ;
            double diff = (end_time - start_time)/1e9;
            DecimalFormat df = new DecimalFormat("#.###");
            request.setAttribute("time", df.format(diff));
            getServletContext().getRequestDispatcher("/home.jsp").forward(request, response);
            return;
        }
        else {
            //Crawler.main(new String[] {});
            getServletContext().getRequestDispatcher("/home.jsp").forward(request, response);
            return;
        }

    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }



}
