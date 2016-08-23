package co.flock.approval.database;

public class DbConstants
{
    public static class Table
    {
        public static final String USERS = "users";
        public static final String BILLS = "bills";
    }

    public static class Fields
    {
        public static final String USER_ID = "user_id";
        public static final String TOKEN = "token";
        public static final String BILL_ID = "bill_id";
        public static final String AMOUNT = "amount";
        public static final String CREATOR = "creator";
        public static final String APPROVER = "approver";
        public static final String STATUS = "status";
    }
}
