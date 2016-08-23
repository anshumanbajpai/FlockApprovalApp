package co.flock.approval;

import co.flock.approval.database.Bill;
import co.flock.approval.database.User;
import co.flock.www.FlockApiClient;
import co.flock.www.model.messages.Attachments.Action;
import co.flock.www.model.messages.Attachments.Attachment;
import co.flock.www.model.messages.Attachments.Button;
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
        buttons[0].setId("id1-approve");
        buttons[0].setName("Approve");
        Action action = new Action();
        action.addDispatchEvent();
        buttons[0].setAction(action);

        buttons[1] = new Button();
        buttons[1].setId("id1-reject");
        buttons[1].setName("Reject");
        action = new Action();
        action.addDispatchEvent();
        buttons[1].setAction(action);

        attachment.setId(String.valueOf(bill.getId()));
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
