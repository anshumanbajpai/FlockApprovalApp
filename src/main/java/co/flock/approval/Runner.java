package co.flock.approval;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.IOException;
import java.util.HashMap;

import static spark.Spark.*;

public class Runner
{
    private static final Logger _logger = Logger.getLogger(Runner.class);

    public static void main(String[] args) throws IOException
    {
        _logger.debug("Starting..");
        port(9000);
        HashMap map = new HashMap();
        staticFileLocation("/public");
        map.put("resourcePrefix", "");
        get("/new", (req, res) -> new ModelAndView(map, "template.mustache"),
                new MustacheTemplateEngine());

        post("/create", (req, res) -> {
            String body = req.body();
            System.out.println("Received request with body: " + body);
            ObjectMapper mapper = new ObjectMapper();
            ApprovalRequest approvalRequest = mapper.readValue(body, ApprovalRequest.class);
            System.out.println("approvalRequest created: " + approvalRequest);
            return "Approval created";
        });

        post("/", (req, res) -> {
            _logger.debug("Req received : " + req);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONObject jsonObject = new JSONObject(req.body());
            String type = (String) jsonObject.get("name");
            if ("app.install".equals(type))
            {
                String userId = jsonObject.getString("userId");
                _logger.debug("Install event received " + userId);
                System.out.println("Userid : " + userId);

            } else
            {
                _logger.debug("Got event: " + type);
            }
            return "";
        });
    }
}
