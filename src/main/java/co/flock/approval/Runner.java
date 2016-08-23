package co.flock.approval;

import co.flock.approval.database.DbConfig;
import co.flock.approval.database.DbManager;
import co.flock.approval.database.User;
import co.flock.www.FlockApiClient;
import co.flock.www.model.messages.FlockMessage;
import co.flock.www.model.messages.Message;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.json.JSONObject;

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
    private static final String USER_TOKEN = "dd21bf92-38d5-40b1-a62d-d13f1a8d5d95";

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
            _logger.debug("Received request with body: " + body);
            ObjectMapper mapper = new ObjectMapper();
            ApprovalRequest approvalRequest = mapper.readValue(body, ApprovalRequest.class);
            _logger.debug("approvalRequest created: " + approvalRequest);
            Message message = new Message(approvalRequest.getApproverId(),
                "Please approve this bill for Rs. " + approvalRequest.getAmount());
            sendMessage(message);
            return "Approval created";
        });

        post("/", (req, res) -> {
            _logger.debug("Req received : " + req.body());
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONObject jsonObject = new JSONObject(req.body());
            String type = (String) jsonObject.get("name");
            _logger.debug("Got event: " + type);
            if ("app.install".equals(type)) {
                String userId = jsonObject.getString("userId");
                String userToken = jsonObject.getString("userToken");
                _dbManager.insertOrUpdateUser(new User(userId, userToken));
                _logger.debug("User inserted : " + userId + "  " + userToken);
            } else if ("app.uninstall".equalsIgnoreCase(type)) {
                String userId = jsonObject.getString("userId");
                _dbManager.deleteUser(new User(userId, ""));
                _logger.debug("User deleted : " + userId);
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

    private static void sendMessage(Message message)
    {
        FlockMessage flockMessage = new FlockMessage(message);
        FlockApiClient flockApiClient = new FlockApiClient(USER_TOKEN, false);
        try {
            flockApiClient.chatSendMessage(flockMessage);
        } catch (Exception e) {
            _logger.error("Failed to send message: ", e);
        }
    }
}
