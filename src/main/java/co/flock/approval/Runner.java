package co.flock.approval;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.IOException;
import java.util.HashMap;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

public class Runner
{
    public static void main(String[] args) throws IOException
    {
        port(8080);
        HashMap map = new HashMap();
        map.put("resourcePrefix", "");
        get("/requestApproval", (req, res) -> new ModelAndView(map, "template.mustache"),
                new MustacheTemplateEngine());

        post("/generateApprovalRequest", (req, res) -> {
            String body = req.body();
            System.out.println("Received request with body: " + body);
            ObjectMapper mapper = new ObjectMapper();
            ApprovalRequest approvalRequest = mapper.readValue(body, ApprovalRequest.class);
            System.out.println("approvalRequest created: " + approvalRequest);
            return "Approval created";
        });
    }
}
