package model.dao.intf;

import model.entity.CustomerRank;

import java.util.List;

public interface CustomerRankDAO {

    boolean save(CustomerRank customerRank);

    boolean update(CustomerRank customerRank);

    boolean delete(String rankId);

    CustomerRank findById(String rankId);

    List<CustomerRank> findAll();
}