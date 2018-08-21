package com.acuitybotting.statistics.services;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;

/**
 * Created by Zachary Herridge on 8/21/2018.
 */
@Service
@Slf4j
public class RsBuddyService {

    private ItemPrices itemPrices;

    @Scheduled(initialDelay = 0, fixedRate = 2100000)
    private void load() throws IOException {
        URL url = new URL("https://rsbuddy.com/exchange/summary.json");
        InputStreamReader reader = new InputStreamReader(url.openStream());
        Type typeOfHashMap = new TypeToken<Map<String, ItemPrice>>() { }.getType();
        Map<String, ItemPrice> priceMap = new Gson().fromJson(reader, typeOfHashMap);

        ItemPrices itemPrices = new ItemPrices();
        itemPrices.setPrices(priceMap);

        this.itemPrices = itemPrices;
    }

    public ItemPrices getItemPrices() {
        if (itemPrices == null) {
            try {
                load();
            } catch (IOException e) {
                log.error("Error loading item prices.", e);
            }
        }
        return itemPrices;
    }

    @Getter
    @Setter
    public static class ItemPrices {

        private long loadedAt = System.currentTimeMillis();
        private Map<String, ItemPrice> prices;
    }


    @Getter
    public static class ItemPrice {

        private int id;
        private String name;
        private boolean members;
        private int sp;
        private int buy_average;
        private int buy_quantity;
        private int sell_average;
        private int sell_quantity;
        private int overall_average;
        private int overall_quantity;
    }
}
