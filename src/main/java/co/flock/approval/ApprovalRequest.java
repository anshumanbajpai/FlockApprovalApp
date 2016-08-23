package co.flock.approval;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApprovalRequest
{
    private int amount;

    @JsonProperty("amount")
    public int getAmount()
    {
        return amount;
    }

    @Override
    public String toString()
    {
        return "ApprovalRequest{" +
                "amount=" + amount +
                '}';
    }
}
