package com.***REMOVED***.site.controller;

import com.***REMOVED***.site.model.DBCard;
import com.***REMOVED***.site.model.DisplayedCard;
import com.***REMOVED***.site.services.CardAccessor;
import com.***REMOVED***.site.services.CardConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CardController {

    final private CardAccessor cardAccessor;
    final private CardConverter cardConverter;

    @Autowired
    CardController(CardAccessor cardAccessor, CardConverter cardConverter) {
        this.cardAccessor = cardAccessor;
        this.cardConverter = cardConverter;
    }


    @RequestMapping(value = "/getAllExpireCards", method = RequestMethod.GET)
    public List<DisplayedCard> getAllExpireCards() {
        List<DBCard> DBCardList = cardAccessor.getAllExpiredCards();
        List<DisplayedCard> displayCards = new ArrayList<>();
        for(DBCard DBCard : DBCardList){
            displayCards.add(cardConverter.toDisplayedCard(DBCard));
        }
        return displayCards;
    }

    @RequestMapping(value = "/getSpecificCard/{key}", method = RequestMethod.GET)
    public DisplayedCard getSpecificCard(@PathVariable String key) {
        DBCard DBCard = cardAccessor.getSpecificCard(key);
        return cardConverter.toDisplayedCard(DBCard);
    }

   @RequestMapping(value = "/getTodayCards/{expiredLimit}/{newLimit}", method = RequestMethod.GET)
    public List<DisplayedCard> getTodayCards(@PathVariable int expiredLimit, @PathVariable int newLimit){
       List<DBCard> DBCardList = cardAccessor.getTodayCards(expiredLimit, newLimit);
       List<DisplayedCard> displayCards = new ArrayList<>();
       for(DBCard DBCard : DBCardList){
           displayCards.add(cardConverter.toDisplayedCard(DBCard));
       }
       return displayCards;
    }

    @RequestMapping(value = "/updateCardStatus/{key}/{status}", method = RequestMethod.GET)
    public DisplayedCard updateCardStatus(@PathVariable String key, @PathVariable String status) {
        return cardAccessor.updateCardStatus(key, status);
    }

    @RequestMapping(value = "/updateCardDetail/{key}", method = RequestMethod.PUT)
    public void updateCardDetail(@PathVariable String key, @RequestBody DisplayedCard displayedCard){
        System.out.println("update detail: " + key);
        cardAccessor.updateCardFrontAndBack(key, displayedCard.front, displayedCard.back);
    }

    @RequestMapping(value = "/deleteCard/{key}", method = RequestMethod.DELETE)
    public void deleteCard(@PathVariable String key) {
        System.out.println("delete " + key);
        cardAccessor.deleteCard(key);
    }

//    @RequestMapping(value = "/updateCardExpirationDate/{key}/{date}", method = RequestMethod.GET)
//    public void updateCardExpirationDate(@PathVariable String key, @PathVariable String date) throws ParseException {
//        Date ExpirationDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
//        //TODO:单纯更新单词的过期时间
//    }

}