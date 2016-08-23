package co.flock.approval.database;

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

    public Bill(int amount, String approver, String creator)
    {
        _amount = amount;
        _approver = approver;
        _creator = creator;
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

    @Override
    public String toString()
    {
        return "Bill{" +
               "_id='" + _id + '\'' +
               ", _amount='" + _amount + '\'' +
               ", _approver='" + _approver + '\'' +
               ", _creator='" + _creator + '\'' +
               '}';
    }
}
