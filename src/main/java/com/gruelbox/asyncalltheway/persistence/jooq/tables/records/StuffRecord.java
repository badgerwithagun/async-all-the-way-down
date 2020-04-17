/*
 * This file is generated by jOOQ.
 */
package com.gruelbox.asyncalltheway.persistence.jooq.tables.records;


import com.gruelbox.asyncalltheway.persistence.jooq.tables.Stuff;
import com.gruelbox.asyncalltheway.persistence.jooq.tables.interfaces.IStuff;

import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StuffRecord extends UpdatableRecordImpl<StuffRecord> implements VertxPojo, Record2<String, String>, IStuff {

    private static final long serialVersionUID = -2099760462;

    /**
     * Setter for <code>STUFF.ID</code>.
     */
    @Override
    public StuffRecord setId(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>STUFF.ID</code>.
     */
    @Override
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>STUFF.DATA</code>.
     */
    @Override
    public StuffRecord setData(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>STUFF.DATA</code>.
     */
    @Override
    public String getData() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Stuff.STUFF.ID;
    }

    @Override
    public Field<String> field2() {
        return Stuff.STUFF.DATA;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getData();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getData();
    }

    @Override
    public StuffRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public StuffRecord value2(String value) {
        setData(value);
        return this;
    }

    @Override
    public StuffRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IStuff from) {
        setId(from.getId());
        setData(from.getData());
    }

    @Override
    public <E extends IStuff> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached StuffRecord
     */
    public StuffRecord() {
        super(Stuff.STUFF);
    }

    /**
     * Create a detached, initialised StuffRecord
     */
    public StuffRecord(String id, String data) {
        super(Stuff.STUFF);

        set(0, id);
        set(1, data);
    }

    public StuffRecord(io.vertx.core.json.JsonObject json) {
        this();
        fromJson(json);
    }
}
