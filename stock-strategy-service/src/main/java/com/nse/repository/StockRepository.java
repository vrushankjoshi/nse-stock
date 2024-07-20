package com.nse.repository;

import com.dao.StockEntity;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface StockRepository {

//    List<StockEntity> findBySymbolAndDate(StockPrimaryKey key);


    @Query(value = "'_id' : { 'chSymbol' : symbol, 'chTimestamp' : { $gte: from, $lte: to }}", delete = true)
    //@Query("DELETE FROM"
    public List<StockEntity> deleteStockBySymbolAndDate(String symbol, Date from, Date to);


    /*
    // { 'location' : { '$near' : [point.x, point.y], '$maxDistance' : distance}}
    List<StockEntity> findByLocationNear(LocalDate location, Distance distance);

     */
}
