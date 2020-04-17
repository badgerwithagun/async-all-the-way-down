package com.gruelbox.asyncalltheway.persistence.jooq.tables.mappers;

import io.vertx.sqlclient.Row;
import java.util.function.Function;

public class RowMappers {

    private RowMappers(){}

    public static Function<Row,com.gruelbox.asyncalltheway.persistence.jooq.tables.pojos.Stuff> getStuffMapper() {
        return row -> {
            com.gruelbox.asyncalltheway.persistence.jooq.tables.pojos.Stuff pojo = new com.gruelbox.asyncalltheway.persistence.jooq.tables.pojos.Stuff();
            pojo.setId(row.getString("ID"));
            pojo.setData(row.getString("DATA"));
            return pojo;
        };
    }

}
