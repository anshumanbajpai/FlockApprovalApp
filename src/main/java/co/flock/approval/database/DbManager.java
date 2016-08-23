package co.flock.approval.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import co.flock.approval.database.Bill.Status;
import co.flock.approval.database.DbConstants.Fields;

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

    public Bill insertBill(int billAmt, String creatorId, String creatorName, String approverId, String approverName) throws SQLException
    {
        Bill bill = new Bill(billAmt, approverId, approverName, creatorId, creatorName);
        _billsDao.createOrUpdate(bill);
        return bill;
    }

    public Bill getBill(String billId) throws SQLException
    {
        return _billsDao.queryForId(billId);
    }

    public List<Bill> getBillsForUser(String id) throws SQLException
    {
        QueryBuilder<Bill, String> queryBuilder = _billsDao.queryBuilder();
        queryBuilder.where().eq(Fields.CREATOR, id);
        PreparedQuery<Bill> preparedQuery = queryBuilder.prepare();
        return _billsDao.query(preparedQuery);
    }

    public boolean approveBill(String billId) throws SQLException
    {
        return updateBillStatus(billId, Status.APPROVED);
    }

    public boolean rejectBill(String billId) throws SQLException
    {
        return updateBillStatus(billId, Status.REJECTED);
    }

    private boolean updateBillStatus(String billId, Status status) throws SQLException
    {
        Bill bill = _billsDao.queryForId(billId);
        if (bill != null && bill.getStatus().equals(Status.PENDING)) {
            bill.setStatus(status);
            _billsDao.update(bill);
            return true;
        }

        return false;
    }

    private void setupDatabase(DbConfig dbConfig) throws SQLException
    {
        JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource(
            dbConfig.getConnectionURL());
        connectionSource.setMaxConnectionAgeMillis(Long.MAX_VALUE);
        _userDao = DaoManager.createDao(connectionSource, User.class);
        _billsDao = DaoManager.createDao(connectionSource, Bill.class);

    }
}
