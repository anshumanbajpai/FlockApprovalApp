package co.flock.approval.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = DbConstants.Table.USERS)
public class User
{
    @DatabaseField(id = true, columnName = DbConstants.Fields.USER_ID, canBeNull = false)

    private String _id;

    public User()
    {
    }

    public User(String id)
    {
        _id = id;
    }

    public String getId()
    {
        return _id;
    }

    @Override
    public String toString()
    {
        return "User{" +
               "_id='" + _id + '\'' +
               '}';
    }
}
