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
        buttons[0] = new Button()
        {
            {
                setId("id1-approve");
                setName("Approve");
                Action action = new Action()
                {
                    {
                        addDispatchEvent();
                    }
                };
                setAction(action);
            }
        };

        buttons[1] = new Button()
        {
            {
                setId("id1-reject");
                setName("Reject");
                Action action = new Action()
                {
                    {
                        addDispatchEvent();
                    }
                };
                setAction(action);
            }
        };
        attachment.setId(String.valueOf(bill.getId()));
        attachment.setButtons(buttons);
        message.setAttachments(new Attachment[]{attachment});
        sendMessage(user.getUserToken(), message);
    }

    public static void sendBillApprovedMessageFromBot(Bill bill)
    {
        Message message = new Message(bill.getCreator(),
            "Your bill of amount Rs " + bill.getAmount() + " has been approved.");
        sendMessage(BOT_TOKEN, message);
    }

    public static void sendBillRejectedMessageFromBot(Bill bill)
    {
        Message message = new Message(bill.getCreator(),
            "Your bill of amount Rs " + bill.getAmount() + " has been rejected.");

        sendMessage(BOT_TOKEN, message);
    }

    private static void sendMessage(String token, Message message)
    {
        _logger.debug("Sending message : " + message.getTo());
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
