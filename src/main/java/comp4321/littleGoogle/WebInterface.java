package comp4321.littleGoogle;

import static spark.Spark.*;

public class WebInterface {
    public static void main(String[] args) {
        port(8080); //Setting the localhost port to 8080
        staticFiles.location("/public");
        get("/", (request, response) -> "Hello World");
        System.out.println("LittleGoogle is running on http://localhost:8080/home.html");
    }
}
