package co.flock.approval;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApprovalRequest
{
    private int amount;
    private String requestorId;
    private String requestorName;
    private String approverId;
    private String approverName;

    public ApprovalRequest(int amount, String requestorId, String requestorName, String approverId,
                           String approverName)
    {
        this.amount = amount;
        this.requestorId = requestorId;
        this.requestorName = requestorName;
        this.approverId = approverId;
        this.approverName = approverName;
    }

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

    @JsonProperty("requestorName")
    public String getRequestorName()
    {
        return requestorId;
    }

    @JsonProperty("approverName")
    public String getApproverName()
    {
        return approverId;
    }

    @Override
    public String toString()
    {
        return "ApprovalRequest{" +
                "amount=" + amount +
                ", requestorId='" + requestorId + '\'' +
                ", requestorName='" + requestorName + '\'' +
                ", approverId='" + approverId + '\'' +
                ", approverName='" + approverName + '\'' +
                '}';
    }
}
