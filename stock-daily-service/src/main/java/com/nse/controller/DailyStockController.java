package com.nse.controller;


import com.dao.ProcessingDay;
import com.google.gson.Gson;
import com.kafka.producer.KafkaProducer;
import com.nse.connect.NSERetrieveAllEquityNames;
import com.nse.constant.PriceAction;
import com.nse.constant.StockFrequency;
import com.nse.service.DailyCalculationService;
import com.nse.service.StockService;
import com.nse.strategy.PriceActionStrategy;
import com.pojo.ErrorResponse;
import com.pojo.Stock;
import com.pojo.StockResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/")
public class DailyStockController {

    private static final Logger logger = LogManager.getLogger(DailyStockController.class);


    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    StockService stockService;

    @Autowired
    DailyCalculationService calcService;

    @Autowired
    KafkaProducer producer;

    public DailyStockController() {

    }

    @GetMapping(path = "/scratch-calculation/{groupId}")
    public @ResponseBody String doScratchCalculationDaily(@PathVariable String groupId) {
        List list = null;
        String jsonArray = null;
        try {
            NSERetrieveAllEquityNames names = new NSERetrieveAllEquityNames();
            ;
            System.out.println("groupId -->" + groupId + "<--");
            list = names.getStockNames(URLEncoder.encode(groupId));

            if (list != null) {
                list.remove(groupId);
            }

            ProcessingDay day = null;
            for (int i = 0; i < list.size(); i++) {

                producer.sendToAllDayCalculationTopic("stock-calculation-from-first-day", (String) list.get(i));
            }
            Gson gson = new Gson();
            jsonArray = gson.toJson(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jsonArray;

    }

    @GetMapping(path = "/stock-scratch-calculation/{stockSymbol}")
    public @ResponseBody String doStockScratchCalculationDaily(@PathVariable String stockSymbol) {
        List list = null;
        String jsonArray = null;
        NSERetrieveAllEquityNames names = new NSERetrieveAllEquityNames();

        System.out.println("stockSymbol -->" + stockSymbol + "<--");
        if (StringUtils.trim(stockSymbol) != null ) {
            list = new ArrayList();
            list.add(stockSymbol.trim());

            ProcessingDay day = null;
            for (int i = 0; i < list.size(); i++) {

                producer.sendToAllDayCalculationTopic("stock-calculation-from-first-day", (String) list.get(i));
            }
            Gson gson = new Gson();
            jsonArray = gson.toJson(list);
        }

        return jsonArray;

    }

    @GetMapping(path = "/{groupId}")
    public @ResponseBody String getDailyStockDetails(@PathVariable String groupId) {
        List list = null;
        String jsonArray = null;
        try {
            NSERetrieveAllEquityNames names = new NSERetrieveAllEquityNames();
            ;
            System.out.println("groupId -->" + groupId + "<--");
            list = names.getStockNames(URLEncoder.encode(groupId));

            if (list != null) {
                list.remove(groupId);
            }

            ProcessingDay day = null;
            for (int i = 0; i < list.size(); i++) {
                day = stockService.getProcessingDayBySymbol((String) list.get(i));
                if (day ==null) {
                    day = new ProcessingDay();
                    day.setName((String) list.get(i));
                }
                producer.send("stock-names", day);
            }
            Gson gson = new Gson();
            jsonArray = gson.toJson(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jsonArray;

    }


    @GetMapping(path = "/fetch-stock/{stockSymbol}")
    public @ResponseBody String getDailySpecificStockDetails(@PathVariable String stockSymbol) {
        List list = null;
        String jsonArray = null;
        NSERetrieveAllEquityNames names = new NSERetrieveAllEquityNames();
        list = new ArrayList();
        list.add(stockSymbol);

        ProcessingDay day = null;
        for (int i = 0; i < list.size(); i++) {
            day = stockService.getProcessingDayBySymbol((String) list.get(i));
            if (day ==null) {
                day = new ProcessingDay();
                day.setName((String) list.get(i));
            }
            producer.send("stock-names", day);
        }
        Gson gson = new Gson();
        jsonArray = gson.toJson(list);

        return jsonArray;

    }



   /* @GetMapping(path = "/near52WeekLow/{groupId}")
    @ResponseBody
    public ResponseEntity<Object> fetch52WeekLowNearerStocks(@PathVariable String groupId) {
        List list = null;
        String jsonArray = null;
        try {

            list = stockService.getStockNear52WeekLow(groupId);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.OK).body(list);

    }


    @GetMapping("/bullish/{symbol}/{frequency}/{rsi}/{from}/{to}")
    @ResponseBody
    public ResponseEntity<Object> checkBullishStock(@PathVariable String symbol, @PathVariable String frequency,
                                                    @PathVariable Integer rsi,
                                                    @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date from,
                                                    @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date to) {
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Stock stock = null;
        StockResponse response = null;
        logger.info("symbol -->" + symbol + "<--");
        logger.info("frequency -->" + frequency + "<--");
        logger.info("rsi -->" + rsi + "<--");
        logger.info("from -->" + from + "<--");
        logger.info("to -->" + to + "<--");
        response = stockService.bulishSignal(symbol, lFormatter.format(from), lFormatter.format(to),
                rsi.intValue(), StockFrequency.valueOf(frequency));


        if (response != null) {
            BeanUtils.copyProperties(stock, response);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } else {
            ErrorResponse errResponse = new ErrorResponse();
            errResponse.setErrorCode("404");
            errResponse.setErrorMessage(symbol + " is not a bullish");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errResponse);
        }

    }


    @GetMapping("/fetch/stock/{symbol}/{from}/{to}")
    @ResponseBody
    public ResponseEntity<Object> getDailyStockDetails(@PathVariable String symbol,
                                                       @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy", iso = DateTimeFormat.ISO.DATE) Date from,
                                                       @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy", iso = DateTimeFormat.ISO.DATE) Date to) {

        List<StockResponse> destinationList = null;
        destinationList = stockService.retrieveDailyStockDtls(symbol, from, to);

        if (destinationList != null || destinationList.size() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body(destinationList);
        } else {
            ErrorResponse errResponse = new ErrorResponse();
            errResponse.setErrorCode("404");
            errResponse.setErrorMessage(" Unable to retrieve stock(" + symbol + ") details from NSE website");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errResponse);
        }

    }

    @GetMapping("/fetch/allStocks/{groupId}/{from}/{to}")
    @ResponseBody
    public ResponseEntity<Object> getDailyStockDetailsByGroupId(@PathVariable String groupId,
                                                                @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy", iso = DateTimeFormat.ISO.DATE) Date from,
                                                                @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy", iso = DateTimeFormat.ISO.DATE) Date to) {

        List list = null;
        try {
            NSERetrieveAllEquityNames names = new NSERetrieveAllEquityNames();
            list = names.getStockNames(URLEncoder.encode(groupId));

            if (list != null) {
                list.remove(groupId);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<StockResponse> destinationList = null;
        destinationList = stockService.getDailyStockDetailsByGroupId(list, from, to);

        if (destinationList != null || destinationList.size() > 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(destinationList);
        } else {
            ErrorResponse errResponse = new ErrorResponse();
            errResponse.setErrorCode("200");
            errResponse.setErrorMessage("Fetch All Details");
            return ResponseEntity.status(HttpStatus.OK).body(errResponse);
        }

    }

    @GetMapping("/find/bullish/{frequency}/{rsi}/{from}/{to}")
    public List<StockResponse> findBullish(@RequestBody List<String> list, @PathVariable String frequency,
                                           @PathVariable Integer rsi,
                                           @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date from,
                                           @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date to)
            throws IOException, ParseException {

        String symbol;
        List<Stock> sourceList = new ArrayList<Stock>();
        Stock stock = null;
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");

        logger.info("list -->" + list + "<--");
        logger.info("frequency -->" + frequency + "<--");
        logger.info("rsi -->" + rsi + "<--");
        logger.info("from -->" + from + "<--");
        logger.info("to -->" + to + "<--");

        List<StockResponse> destinationList = stockService.findBullishStock(list, lFormatter.format(from), lFormatter.format(to),
                rsi.intValue(), StockFrequency.valueOf(frequency));

        return destinationList;
    }

    @GetMapping("/higherhigh/{symbol}/{frequency}/{rsi}/{from}/{to}")
    public List<StockResponse> findHigherHigh(@PathVariable String symbol, @PathVariable String frequency,
                                              @PathVariable Integer rsi,
                                              @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date from,
                                              @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date to)
            throws IOException, ParseException {
        PriceActionStrategy strategy = new PriceActionStrategy();
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");

        List<Stock> list = strategy.findPriceHigherHigh(symbol, lFormatter.format(from), lFormatter.format(to), rsi.intValue(), StockFrequency.valueOf(frequency), PriceAction.HIGHER_HIGH);
        logger.info("Higher High Price size -->" + list.size());

        List<StockResponse> destinationList = new ArrayList<StockResponse>();
        destinationList = copyList(list, destinationList);
        return destinationList;

    }*/

    @GetMapping("/lowerlow/{symbol}/{frequency}/{rsi}/{from}/{to}")
    public List<StockResponse> findLowerLow(@PathVariable String symbol, @PathVariable String frequency,
                                            @PathVariable Integer rsi,
                                            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date from,
                                            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date to)
            throws IOException, ParseException {
        PriceActionStrategy strategy = new PriceActionStrategy();
        DateFormat lFormatter = new SimpleDateFormat("dd-MM-yyyy");

        List<Stock> list = strategy.findPriceHigherHigh(symbol, lFormatter.format(from),
                lFormatter.format(to), rsi.intValue(), StockFrequency.valueOf(frequency),
                PriceAction.LOWER_LOW);
        logger.info("Higher High Price size -->" + list.size());
        List<StockResponse> destinationList = new ArrayList<StockResponse>();
        destinationList = copyList(list, destinationList);
        return destinationList;

    }


    private List copyList(List source, List destination) {
        if (source != null) {
            StockResponse toBean = null;
            for (Object fromBean : source) {
                if (fromBean != null) {
                    toBean = new StockResponse();
                    org.springframework.beans.BeanUtils.copyProperties(fromBean, toBean);
                    destination.add(toBean);
                }
            }
        }

        return destination;
    }


}