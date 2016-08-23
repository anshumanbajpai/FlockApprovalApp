package co.flock.approval;

import co.flock.approval.database.Bill;
import co.flock.approval.database.DbConfig;
import co.flock.approval.database.DbManager;
import co.flock.approval.database.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import spark.ModelAndView;
import spark.Request;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static spark.Spark.*;

public class Runner
{
    private static final Logger _logger = Logger.getLogger(Runner.class);
    private static DbManager _dbManager;
    private static MessagingService _messagingService;

    public static void main(String[] args) throws Exception
    {
        //secure("deploy/keystore.jks", "password", null, null);
        _logger.debug("Starting..");
        _dbManager = new DbManager(getDbConfig());
        _messagingService = new MessagingService();
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
            User user = _dbManager.getUserById(approvalRequest.getRequestorId());
            _logger.debug("requestor user: " + user);
            Bill bill = _dbManager
                .insertBill(approvalRequest.getAmount(), approvalRequest.getRequestorId(), approvalRequest.getRequestorName(),
                    approvalRequest.getApproverId(), approvalRequest.getApproverName());
            _logger.debug("Sending approval request for bill: " + bill);
            _messagingService.sendApprovalRequestMessage(user, bill);
            return "Approval created";
        });

        get("/viewall",
            (req, res) -> new ModelAndView(getLauncherMap(req.queryParams("flockEvent")),
                "template_launcher.mustache"), new MustacheTemplateEngine());


        post("/approve", (req, res) -> {
            _logger.debug("Received approval request with body: " + req.body());
            return approveOrRejectBill(req, true);
        });

        post("/reject", (req, res) -> {
            _logger.debug("Received rejection request with body: " + req.body());
            return approveOrRejectBill(req, false);
        });

        post("/", (req, res) -> {
            _logger.debug("Req received : " + req.body());
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
            } else if ("client.pressButton".equalsIgnoreCase(type)) {
                String buttonName = jsonObject.getString("button");
                _logger.debug("buttonName: " + buttonName);
                if ("attachmentButton".equalsIgnoreCase(buttonName)) {
                    _logger.debug("Processing attachment button press");
                    String buttonId = jsonObject.getString("buttonId");
                    String approvalId = buttonId.substring(1);
                    if (buttonId.startsWith("a")) {
                        approveOrRejectBill(approvalId, true);
                    } else {
                        approveOrRejectBill(approvalId, false);
                    }
                }
            }
            return "";
        });
    }

    private static String approveOrRejectBill(Request req, boolean isApproval) throws SQLException
    {
        JSONObject jsonObject = new JSONObject(req.body());
        String id = jsonObject.getString("id");
        approveOrRejectBill(id, isApproval);
        return "";
    }

    private static void approveOrRejectBill(String id, boolean isApproval) throws SQLException
    {
        boolean billUpdated;
        if (isApproval) {
            billUpdated = _dbManager.approveBill(id);
        } else {
            billUpdated = _dbManager.rejectBill(id);
        }
        if (billUpdated && isApproval) {
            MessagingService.sendBillApprovedOrRejectedMsgFromBot(_dbManager.getBill(id), true);
        } else if (billUpdated) {
            MessagingService.sendBillApprovedOrRejectedMsgFromBot(_dbManager.getBill(id), false);
        } else {
            _logger.debug("Bill not present or already approved or rejected ");
        }
    }

    private static DbConfig getDbConfig()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("config", Locale.getDefault());
        return new DbConfig(bundle.getString("db_host"),
            Integer.parseInt(bundle.getString("db_port")), bundle.getString("db_name"),
            bundle.getString("db_username"), bundle.getString("db_password"));
    }

    private static Map<String, List<Bill>> getLauncherMap(String queryString) throws SQLException
    {
        Map<String, List<Bill>> s = new HashMap<>();
        _logger.debug("String : " + queryString);
        JSONObject jsonObject = new JSONObject(queryString);
        String userId = jsonObject.getString("userId");
        _logger.debug("Userid : " + userId);

        List<Bill> bills = _dbManager.getBillsForUser(userId);
        _logger.debug("Bills fetched : " + bills);
        s.put("bills", bills);
        return s;
    }
}
