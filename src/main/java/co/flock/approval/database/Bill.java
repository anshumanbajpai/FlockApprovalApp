package co.flock.approval.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

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
    @DatabaseField(columnName = Fields.APPROVER_NAME, canBeNull = true)
    private String _approverName;
    @DatabaseField(columnName = Fields.CREATOR, canBeNull = false)
    private String _creator;
    @DatabaseField(columnName = Fields.CREATOR_NAME, canBeNull = true)
    private String _creatorName;
    @DatabaseField(columnName = Fields.STATUS, dataType = DataType.ENUM_INTEGER, canBeNull = false, defaultValue = "0")
    private Status _status;
    @DatabaseField(columnName = Fields.CREATION_DATE, dataType = DataType.DATE)
    private Date _creationDate;

    private String _path;


    public Bill(int amount, String approver, String approverName, String creator,
                String creatorName, Status status, Date date)
    {
        _amount = amount;
        _approver = approver;
        _approverName = approverName;
        _creator = creator;
        _creatorName = creatorName;
        _status = status;
        _creationDate = date;
    }

    public Bill(int amount, String approver, String approverName, String creator,
                String creatorName)
    {
        this(amount, approver, approverName, creator, creatorName, Status.PENDING, new Date());
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

    public String getApproverName()
    {
        return _approverName;
    }

    public String getCreatorName()
    {
        return _creatorName;
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

    public Date getCreationDate()
    {
        return _creationDate;
    }

    public void setPath(String p)
    {
        _path = p;
    }

    @Override
    public String toString()
    {
        return "Bill{" +
               "_id=" + _id +
               ", _amount=" + _amount +
               ", _approver='" + _approver + '\'' +
               ", _approverName='" + _approverName + '\'' +
               ", _creator='" + _creator + '\'' +
               ", _creatorName='" + _creatorName + '\'' +
               ", _status=" + _status +
               ", _creationDate=" + _creationDate +
               '}';
    }

    public String getPath()
    {
        return _path;
    }

    public enum Status
    {
        PENDING, APPROVED, REJECTED;
    }
}
