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
import spark.Response;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
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
        //add dummy bill with valid guids
        _dbManager.insertBill(230, "u:hjo55jy33x5onyjx", "u:007effelfl6l6ajf");
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
                .insertBill(approvalRequest.getAmount(), approvalRequest.getRequestorId(),
                    approvalRequest.getApproverId());
            _logger.debug("Sending approval request for bill: " + bill);
            _messagingService.sendApprovalRequestMessage(user, bill);
            return "Approval created";
        });

        post("/approve", (req, res) -> {
            _logger.debug("Received approval request with body: " + req.body());
            return approveOrRejectBill(req, res, true);
        });

        post("/reject", (req, res) -> {
            _logger.debug("Received rejection request with body: " + req.body());
            return approveOrRejectBill(req, res, false);
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
            }
            return "";
        });
    }

    private static String approveOrRejectBill(Request req, Response res, boolean isApproval)
        throws SQLException
    {
        JSONObject jsonObject = new JSONObject(req.body());
        String id = jsonObject.getString("id");
        boolean billUpdated;
        if (isApproval) {
            billUpdated = _dbManager.approveBill(id);
        } else {
            billUpdated = _dbManager.rejectBill(id);
        }
        if (billUpdated && isApproval) {
            MessagingService.sendBillApprovedOrRejectedMsgFromBot(_dbManager.getBill(id), true);
        } else if (billUpdated && !isApproval) {
            MessagingService.sendBillApprovedOrRejectedMsgFromBot(_dbManager.getBill(id), false);
        } else {
            _logger.debug("Bill not present or already approved or rejected ");
            res.status(401);
        }

        return "";
    }

    private static DbConfig getDbConfig()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("config", Locale.getDefault());
        return new DbConfig(bundle.getString("db_host"),
            Integer.parseInt(bundle.getString("db_port")), bundle.getString("db_name"),
            bundle.getString("db_username"), bundle.getString("db_password"));
    }
}
