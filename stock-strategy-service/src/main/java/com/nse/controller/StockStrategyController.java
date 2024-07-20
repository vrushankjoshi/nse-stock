package com.nse.controller;


import com.nse.constant.PriceAction;
import com.nse.constant.StockFrequency;
import com.nse.service.StrategyService;
import com.nse.strategy.PriceActionStrategy;
import com.pojo.Stock;
import com.pojo.StockResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/")
public class StockStrategyController {

    private static final Logger logger = LogManager.getLogger(StockStrategyController.class);


    @Autowired
    StrategyService strategyService;



    public StockStrategyController() {

    }

    @GetMapping(path = "/near52WeekLow/{groupId}")
    @ResponseBody
    public ResponseEntity<Object> fetch52WeekLowNearerStocks(@PathVariable String groupId) {
        List list = null;
        String jsonArray = null;
        try {

            list = strategyService.getStockNear52WeekLow(groupId);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.OK).body(list);

    }




    @GetMapping("/find/bullish/{frequency}/{rsi}")
    public List<StockResponse> findBullish(@PathVariable String frequency,
                                           @PathVariable Integer rsi)
            throws IOException, ParseException {


        List<String> list = strategyService.findAllStockNames();

        logger.info("list -->" + list + "<--");
        logger.info("frequency -->" + frequency + "<--");
        logger.info("rsi -->" + rsi + "<--");

        List<StockResponse> destinationList = strategyService.findBullishStock(list, rsi.intValue(), StockFrequency.valueOf(frequency));

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

    }

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