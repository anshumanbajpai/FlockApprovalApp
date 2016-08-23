package co.flock.approval;

import co.flock.approval.database.Bill;
import co.flock.approval.database.User;
import co.flock.www.FlockApiClient;
import co.flock.www.model.messages.Attachments.*;
import co.flock.www.model.messages.FlockMessage;
import co.flock.www.model.messages.Message;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class MessagingService
{
    private static final Logger _logger = Logger.getLogger(MessagingService.class);
    private static final String BOT_TOKEN = "d3f0bea6-eb89-4708-8408-092e6693c5d1";

    public void sendApprovalRequestMessage(User user, Bill bill)
    {
        _logger.debug("sendApprovalRequestMessage user: " + user + "bill: " + bill);
        Message message = new Message(bill.getApprover(), "Kindly approve");
        Attachment attachment = new Attachment();
        Button[] buttons = new Button[2];
        buttons[0] = new Button();
        attachment.setId(String.valueOf(bill.getId()));
        buttons[0].setId("a" + bill.getId());
        buttons[0].setName("Approve");
        Action action = new Action();
        action.addDispatchEvent();
        buttons[0].setAction(action);

        buttons[1] = new Button();
        buttons[1].setId("r" + bill.getId());
        buttons[1].setName("Reject");
        action = new Action();
        action.addDispatchEvent();
        buttons[1].setAction(action);

        HtmlView htmlView = new HtmlView();
        htmlView.setInline("<style type=\"text/css\">body { margin:0; padding:0; } ul { margin:0; padding: 5px 0 0 20px; } li { line-height:19px; } </style><div style=\"font-family:'Lucida Grande',Arial,sans-serif;font-size:14px;font-weight:400\"><ul><li>Amount Requested: Rs. " + bill.getAmount() + "</li><li>Status: " + bill.getStatus().toString() + "</li></ul></div>");
        View views = new View();
        views.setHtml(htmlView);
        attachment.setViews(views);
        attachment.setButtons(buttons);
        message.setAttachments(new Attachment[]{attachment});
        String messageJson = new Gson().toJson(message);
        _logger.debug("messageJson: " + messageJson);
        sendMessage(user.getUserToken(), message);
    }

    public static void sendBillApprovedOrRejectedMsgFromBot(Bill bill, boolean approval)
    {
        String text = String
            .format("Your bill of amount Rs %s created on %s has been %s.", bill.getAmount(),
                bill.getCreationDate(), approval ? "accepted" : "rejected");
        Message message = new Message(bill.getCreator(), text);
        sendMessage(BOT_TOKEN, message);
    }

    private static void sendMessage(String token, Message message)
    {
        _logger.debug("Sending message to  : " + message.getTo() + " text : " + message.getText());
        FlockMessage flockMessage = new FlockMessage(message);
        FlockApiClient flockApiClient = new FlockApiClient(token, false);
        try {
            String responseBody = flockApiClient.chatSendMessage(flockMessage);
            _logger.debug("responseBody: " + responseBody);
        } catch (Exception e) {
            _logger.error("Failed to send message: ", e);
        }
    }
}
