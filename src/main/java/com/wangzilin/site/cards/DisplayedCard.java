package com.***REMOVED***.site.cards;

import com.mongodb.DB;
import com.mongodb.client.model.DBCollectionFindAndModifyOptions;

import java.util.Date;
import java.util.List;

public class DisplayedCard extends Card {
    //之所以要显式的写出来而不是继承是因为restfulController必须这样才可以返回
    public String key;
    public String front;
    public String back;
    public Date expireDate;
    public List<String> options;

    public DisplayedCard(DBCard DBCard, List<String> options) {
        super(DBCard.key, DBCard.front, DBCard.back, DBCard.expireDate);
        this.key = DBCard.key;
        this.front = DBCard.front;
        this.back = DBCard.back;
        this.expireDate = DBCard.expireDate;
        this.options = options;
    }
}
