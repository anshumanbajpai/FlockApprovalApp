package co.flock.approval;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Runner {

    public static void main(String[] args) {

        port(9000);

        get("/", (req, res) -> {
            System.out.println("Req : " + req.body());
            return "hello get";
        });

        post("/", (req, res) -> {
            System.out.println("Req : " + req.body());
            return "hello post";
        });
    }
}
