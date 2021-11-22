package com.zqf.service.impl;


import com.zqf.annotation.Autowired;
import com.zqf.annotation.Service;
import com.zqf.annotation.Transactional;
import com.zqf.dao.AccountDao;
import com.zqf.dao.impl.JdbcAccountDaoImpl;
import com.zqf.factory.TransactionManager;
import com.zqf.pojo.Account;
import com.zqf.service.TransferService;

/**
 * @author
 */
@Service("transferService")
@Transactional
public class TransferServiceImpl implements TransferService {

//    private AccountDao accountDao = new JdbcAccountDaoImpl();

    @Autowired
    private AccountDao accountDao;

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

           Account from = accountDao.queryAccountByCardNo(fromCardNo);
           Account to = accountDao.queryAccountByCardNo(toCardNo);

           from.setMoney(from.getMoney() - money);
           to.setMoney(to.getMoney() + money);

           accountDao.updateAccountByCardNo(to);
           int i =  1/0;
           accountDao.updateAccountByCardNo(from);
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
}
