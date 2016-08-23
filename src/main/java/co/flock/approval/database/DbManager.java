package co.flock.approval.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class DbManager
{
    private Dao<User, String> _userDao;
    private Dao<Bill, String> _billsDao;

    public DbManager(DbConfig dbConfig) throws SQLException
    {
        setupDatabase(dbConfig);
    }

    public CreateOrUpdateStatus insertOrUpdateUser(User user) throws SQLException
    {
        return _userDao.createOrUpdate(user);
    }

    public User getUserById(String userID) throws SQLException
    {
        return _userDao.queryForId(userID);
    }

    public List<User> getAllUsers() throws SQLException
    {
        return _userDao.queryForAll();
    }

    public void deleteUser(User user) throws SQLException
    {
        _userDao.delete(user);
    }

    public Bill insertBill(int billAmt, String creatorId, String approverId) throws SQLException
    {
        Bill bill = new Bill(billAmt, approverId, creatorId);
        _billsDao.createOrUpdate(bill);
        return bill;
    }

    private void setupDatabase(DbConfig dbConfig) throws SQLException
    {
        JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource(
            dbConfig.getConnectionURL());
        connectionSource.setMaxConnectionAgeMillis(Long.MAX_VALUE);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Bill.class);
        _userDao = DaoManager.createDao(connectionSource, User.class);
        _billsDao = DaoManager.createDao(connectionSource, Bill.class);

    }
}
