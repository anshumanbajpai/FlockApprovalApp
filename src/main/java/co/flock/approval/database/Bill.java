package co.flock.approval.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import co.flock.approval.database.DbConstants.Fields;

@DatabaseTable(tableName = DbConstants.Table.BILLS)
public class Bill
{
    @DatabaseField(columnName = Fields.BILL_ID, canBeNull = false, generatedId = true)
    private int _id;
    @DatabaseField(columnName = Fields.AMOUNT, canBeNull = false)
    private int _amount;
    @DatabaseField(columnName = Fields.APPROVER, canBeNull = false)
    private String _approver;
    @DatabaseField(columnName = Fields.CREATOR, canBeNull = false)
    private String _creator;
    @DatabaseField(columnName = Fields.STATUS, dataType = DataType.ENUM_INTEGER, canBeNull = false, defaultValue = "0")
    private Status _status;

    public Bill(int amount, String approver, String creator, Status status)
    {
        _amount = amount;
        _approver = approver;
        _creator = creator;
        _status = status;
    }

    public Bill(int amount, String approver, String creator)
    {
        this(amount, approver, creator, Status.PENDING);
    }

    public Bill()
    {
    }

    public int getId()
    {
        return _id;
    }

    public int getAmount()
    {
        return _amount;
    }

    public String getApprover()
    {
        return _approver;
    }

    public String getCreator()
    {
        return _creator;
    }

    public Status getStatus()
    {
        return _status;
    }

    public void setStatus(Status status)
    {
        _status = status;
    }

    @Override
    public String toString()
    {
        return "Bill{" +
               "_id=" + _id +
               ", _amount=" + _amount +
               ", _approver='" + _approver + '\'' +
               ", _creator='" + _creator + '\'' +
               ", _status=" + _status +
               '}';
    }

    public enum Status
    {
        PENDING, APPROVED, REJECTED;
    }
}
