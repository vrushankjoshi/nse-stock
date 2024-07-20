package com.nse.service;

import com.dao.ProcessingDay;
import com.dao.StockEntity;
import com.dao.StockPrimaryKey;
import com.nse.connect.NSERetrieveAllEquityNames;
import com.nse.connect.NSERetrieveEquityDetails;
import com.nse.constant.StockFrequency;
import com.nse.repository.StockRepositoryImpl;
import com.nse.strategy.BulishStockStrategy;
import com.nse.utils.NSEUtils;
import com.pojo.ErrorResponse;
import com.pojo.Stock;
import com.pojo.StockResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LogManager.getLogger(StockServiceImpl.class);

   /* @Autowired
    private StockRepository stockRepository;*/

    @Autowired
    StockRepositoryImpl stockRepositoryImpl;

    public java.util.Date getProcessingDayForSymbol(String symbol) {
        ProcessingDay lastProcessingDay = stockRepositoryImpl.findProcessingDayBySymbol(symbol);

        return lastProcessingDay.getProcessingDay();
    }

    public ProcessingDay getProcessingDayBySymbol(String symbol) {
        ProcessingDay lastProcessingDay = stockRepositoryImpl.findProcessingDayBySymbol(symbol);

        return lastProcessingDay;
    }

    public List findBullishStock(List stockList, String from, String to, int rsiLength, StockFrequency frequency) {
        BulishStockStrategy bullish = new BulishStockStrategy();
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Stock stock = null;
        List<Stock> sourceList = new ArrayList<Stock>();
        String symbol = null;
        for (int i = 0; i < stockList.size(); i++) {
            symbol = (String) stockList.get(i);
            logger.info("i -->" + i + ", symbol -->" + symbol);

            try {
                stock = null;
                stock = bullish.bulishSignal(symbol, from, to,
                        rsiLength, StockFrequency.valueOf(String.valueOf(frequency)));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }

            if (stock != null) {
                sourceList.add(stock);
                logger.info("i -->" + i + ", Bullish -->" + symbol + " , Date -->" +
                        stock.getChTimestamp() + ", current Price -->" + stock.getChClosingPrice() +
                        ", rsi -->" + stock.getRsi() + ", 52Week -->" + stock.getCh52WeekHighPrice());
            }

        }
        List<StockResponse> destinationList = new ArrayList<StockResponse>();
        destinationList = NSEUtils.copyList(sourceList, destinationList);
        return destinationList;

    }

    public List<StockResponse> getStockNear52WeekLow(String groupId) throws IOException, ParseException {
        List<StockResponse> responseList = new ArrayList<StockResponse>();
        NSERetrieveAllEquityNames names = new NSERetrieveAllEquityNames();


        List<String> stockList = names.getStockNames(URLEncoder.encode(groupId));

        if (stockList != null) {
            stockList.remove(groupId);
        }

        String symbol = null;
        StockEntity stock = null;
        ProcessingDay day = null;
        for (int i = 0; i < stockList.size(); i++) {
            symbol = (String) stockList.get(i);
            logger.info(i + " symbol -->" + symbol);

            day = stockRepositoryImpl.findProcessingDayBySymbol(symbol);

            if (day != null ) {

                stock = stockRepositoryImpl.findStockByKey(symbol,day.getProcessingDay());

                if (stock != null ) {

                    float varianceFrom52WeekLow = stock.getCh52WeekLowPrice() - stock.getChClosingPrice();
                    varianceFrom52WeekLow = (varianceFrom52WeekLow / stock.getCh52WeekLowPrice()) * 100;
                    logger.info(i + " symbol -->" + symbol + "<-- varianceFrom52WeekLow -->" + varianceFrom52WeekLow + "<--");
                    if (varianceFrom52WeekLow > -20 && varianceFrom52WeekLow < 20) {

                        logger.info(i + " symbol -->" + symbol + " near to low 52week Low price -->" + stock.getCh52WeekLowPrice() +
                                "<-- closing price -->" + stock.getChClosingPrice());

                        responseList.add(copyFromDAOResponse(stock));

                    }
                }
            }

        }

        return responseList;
    }

    public StockResponse bulishSignal(String symbol, String from, String to, int length, StockFrequency frequency) {

        BulishStockStrategy strategy = new BulishStockStrategy();
        Stock stock = null;
        StockResponse response = null;
        try {
            stock = strategy.bulishSignal(symbol, from, to, length, frequency);
            if (stock != null) {
                response = new StockResponse();
                response.setChSymbol(stock.getChSymbol());
                response.setChTimestamp(stock.getChTimestamp());
                response.setWeekStartDate(stock.getWeekStartDate());
                response.setWeekEndDate(stock.getWeekEndDate());
                response.setEma(stock.getEma());
                response.setSma(stock.getSma());
                response.setRsi(stock.getRsi());
                response.setChClosingPrice(stock.getChClosingPrice());
                response.setCh52WeekHighPrice(stock.getCh52WeekHighPrice());
                response.setCh52WeekLowPrice(stock.getCh52WeekLowPrice());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public List getDailyStockDetailsByGroupId(List stockList, Date from, Date to) {
        String symbol = null;
        List<StockResponse> destinationList = null;
        List<ErrorResponse> errorList = new ArrayList<>();

        for (int i = 0; i < stockList.size(); i++) {
            if (stockList.get(i) instanceof String) {
                symbol = (String) stockList.get(i);
                //symbol = URLEncoder.encode(symbol);
            } else
                continue;
            destinationList = retrieveDailyStockDtls(symbol, from, to);

            if (destinationList == null || destinationList.size() <= 0) {
                ErrorResponse errResponse = new ErrorResponse();
                errResponse.setErrorCode("404");
                errResponse.setErrorMessage(" Unable to retrieve stock(" + symbol + ") details from NSE website");
                errorList.add(errResponse);
            }
        }
        return errorList;
    }

    public List retrieveDailyStocks(ProcessingDay day ,Date from, Date to) {
        String symbol = day.getName();
        NSERetrieveEquityDetails retrieveStockDtls = new NSERetrieveEquityDetails();
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        List stockList = null;
        List<StockEntity> destinationList = null;
        try {
            logger.info("[StockServiceImpl] - retrieveDailyStocks() : symbol -->"+ symbol + "<-- , from -->" + from + "<-- to -->" + to + "<--");
            Stock[] response = retrieveStockDtls.retrieveStockDtls(symbol, lFormatter.format(from), lFormatter.format(to));
            logger.info("[StockServiceImpl] - retrieveDailyStocks() : symbol -->"+ symbol + "<-- , response -->" + response + "<--");

            if (response != null ) {
                logger.info("[StockServiceImpl] - retrieveDailyStocks() : symbol -->"+ symbol + "<-- , response.length -->" + response.length + "<--");
                stockList = Arrays.asList(response);

                if ( response.length > 0) {
                    List<Stock> tempList = Arrays.asList(response);
                    destinationList = new ArrayList();
                    destinationList = copyToDAOList(tempList, destinationList);

                    stockRepositoryImpl.deleteStockBySymbolAndDate(symbol, from, to);
                    stockRepositoryImpl.saveAllEntities(destinationList);

                    String procDay = lFormatter.format(getLastProcessingDateFromResponse(destinationList));
                    to = lFormatter.parse(procDay);
                    stockRepositoryImpl.insertUpdateProcessingDayForSymbol(symbol, to);

                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stockList;

    }

    private Date getLastProcessingDateFromResponse(List destinationList) {
        Date returnDate = null;

        StockEntity entity = null;

        for (int i = 0 ; i < destinationList.size();i++) {
            entity = (StockEntity)destinationList.get(i);
            if (returnDate == null || entity.getChTimestamp().after(returnDate))
                returnDate = entity.getChTimestamp();
        }

        return returnDate;
    }

    public List retrieveDailyStockDtls(String symbol, Date from, Date to) {
        NSERetrieveEquityDetails retrieveStockDtls = new NSERetrieveEquityDetails();

        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        List<StockEntity> destinationList = null;
        try {
            HashMap map = retrieveStockDtls.retriveStockAllDetails(symbol, lFormatter.format(from), lFormatter.format(to), 3, 3);
            Stock[] response = Arrays.copyOf(map.values().toArray(), map.values().toArray().length, Stock[].class);

            if (response.length >= 0) {
                List<Stock> tempList = Arrays.asList(response);
                destinationList = new ArrayList<StockEntity>();
                destinationList = copyToDAOList(tempList, destinationList);

                stockRepositoryImpl.deleteStockBySymbolAndDate(symbol, from, to);
                destinationList = stockRepositoryImpl.saveAllEntities(destinationList);

                stockRepositoryImpl.insertUpdateProcessingDayForSymbol(symbol, to);

            }

        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }


        return copyFromDAOList(destinationList);

    }

    private List copyToDAOList(List<Stock> source, List destination) throws ParseException {
        if (source != null) {
            StockEntity entity = null;
            StockPrimaryKey key = null;
            DateFormat lFormatter = new SimpleDateFormat("yyyy-MM-dd");

            String procDay = null;
            Date entityDate = null;

            for (Stock fromBean : source) {
                if (fromBean != null) {
                    entity = new StockEntity();
                    key = new StockPrimaryKey();
                    logger.info("copyToDAOList() - fromBean.getChTimestamp() -->" + fromBean.getChTimestamp() + "<--");
                    procDay = lFormatter.format(fromBean.getChTimestamp() );
                    entityDate = lFormatter.parse(procDay);

                    key.setChSymbol(fromBean.getChSymbol());
                    key.setChTimestamp( entityDate );
                    //entity.setStockPrimaryKey(key);
                    entity.setChSymbol(fromBean.getChSymbol());
                    entity.setChTimestamp( entityDate );

                    entity.setWeekStartDate(fromBean.getWeekStartDate());
                    entity.setWeekEndDate(fromBean.getWeekEndDate());
                    entity.setEma(fromBean.getEma());
                    entity.setSma(fromBean.getSma());
                    entity.setRsi(fromBean.getRsi());
                    entity.setChClosingPrice(fromBean.getChClosingPrice());
                    entity.setCh52WeekHighPrice(fromBean.getCh52WeekHighPrice());
                    entity.setCh52WeekLowPrice(fromBean.getCh52WeekLowPrice());
                    destination.add(entity);
                }
            }
        }

        return destination;
    }

    private StockResponse copyFromDAOResponse(StockEntity source) {
        StockResponse response = null;


        response = new StockResponse();
        response.setChSymbol(source.getChSymbol());
        response.setChTimestamp(source.getChTimestamp());
        response.setWeekStartDate(source.getWeekStartDate());
        response.setWeekEndDate(source.getWeekEndDate());
        response.setEma(source.getEma());
        response.setSma(source.getSma());
        response.setRsi(source.getRsi());
        response.setChClosingPrice(source.getChClosingPrice());
        response.setCh52WeekHighPrice(source.getCh52WeekHighPrice());
        response.setCh52WeekLowPrice(source.getCh52WeekLowPrice());


        return response;
    }

    private List copyFromDAOList(List<StockEntity> source) {
        List<StockResponse> destination = null;
        if (source != null) {
            destination = new ArrayList<StockResponse>();
            StockEntity entity = null;
            StockPrimaryKey key = null;
            StockResponse response = null;
            for (StockEntity fromBean : source) {
                if (fromBean != null) {
                    response = new StockResponse();
                    response.setChSymbol(fromBean.getChSymbol());
                    response.setChTimestamp(fromBean.getChTimestamp());
                    response.setWeekStartDate(fromBean.getWeekStartDate());
                    response.setWeekEndDate(fromBean.getWeekEndDate());
                    response.setEma(fromBean.getEma());
                    response.setSma(fromBean.getSma());
                    response.setRsi(fromBean.getRsi());
                    response.setChClosingPrice(fromBean.getChClosingPrice());
                    response.setCh52WeekHighPrice(fromBean.getCh52WeekHighPrice());
                    response.setCh52WeekLowPrice(fromBean.getCh52WeekLowPrice());
                    destination.add(response);
                }
            }
        }

        return destination;
    }

}
