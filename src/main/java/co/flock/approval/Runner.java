package co.flock.approval;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import co.flock.approval.database.DbConfig;
import co.flock.approval.database.DbManager;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import static spark.Spark.*;

public class Runner
{
    private static final Logger _logger = Logger.getLogger(Runner.class);
    private static DbManager _dbManager;

    public static void main(String[] args) throws Exception
    {
        _logger.debug("Starting..");
        _dbManager = new DbManager(getDbConfig());
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
            _logger.debug("Req received : " + req.body());
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONObject jsonObject = new JSONObject(req.body());
            String type = (String) jsonObject.get("name");
            if ("app.install".equals(type)) {
                String userId = jsonObject.getString("userId");
                _logger.debug("Install event received " + userId);
                System.out.println("Userid : " + userId);

            } else {
                _logger.debug("Got event: " + type);
            }
            return "";
        });
    }

    private static DbConfig getDbConfig()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("config", Locale.getDefault());
        return new DbConfig(bundle.getString("db_host"),
            Integer.parseInt(bundle.getString("db_port")), bundle.getString("db_name"),
            bundle.getString("db_username"), bundle.getString("db_password"));
    }
}
