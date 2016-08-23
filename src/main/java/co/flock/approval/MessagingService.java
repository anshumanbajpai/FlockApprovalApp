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
        sendMessage(user, message);
    }

    private static void sendMessage(User user, Message message)
    {
        _logger.debug("Sending message");
        String userToken = user.getUserToken();
        FlockMessage flockMessage = new FlockMessage(message);
        FlockApiClient flockApiClient = new FlockApiClient(userToken, false);
        try {
            String responseBody = flockApiClient.chatSendMessage(flockMessage);
            _logger.debug("responseBody: " + responseBody);
        } catch (Exception e) {
            _logger.error("Failed to send message: ", e);
        }
    }
}
