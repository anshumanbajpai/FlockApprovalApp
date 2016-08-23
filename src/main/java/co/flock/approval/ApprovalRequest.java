package co.flock.approval;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApprovalRequest
{
    private int amount;
    private String requestorId;
    private String approverId;

    @JsonProperty("amount")
    public int getAmount()
    {
        return amount;
    }

    @JsonProperty("requestorId")
    public String getRequestorId()
    {
        return requestorId;
    }

    @JsonProperty("approverId")
    public String getApproverId()
    {
        return approverId;
    }

    @Override
    public String toString()
    {
        return "ApprovalRequest{" +
                "amount=" + amount +
                ", requestorId='" + requestorId + '\'' +
                ", approverId='" + approverId + '\'' +
                '}';
    }
}
