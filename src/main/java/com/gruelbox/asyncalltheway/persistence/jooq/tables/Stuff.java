/*
 * This file is generated by jOOQ.
 */
package com.gruelbox.asyncalltheway.persistence.jooq.tables;


import com.gruelbox.asyncalltheway.persistence.jooq.DefaultSchema;
import com.gruelbox.asyncalltheway.persistence.jooq.Keys;
import com.gruelbox.asyncalltheway.persistence.jooq.tables.records.StuffRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Stuff extends TableImpl<StuffRecord> {

    private static final long serialVersionUID = -1925208223;

    /**
     * The reference instance of <code>STUFF</code>
     */
    public static final Stuff STUFF = new Stuff();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StuffRecord> getRecordType() {
        return StuffRecord.class;
    }

    /**
     * The column <code>STUFF.ID</code>.
     */
    public final TableField<StuffRecord, String> ID = createField(DSL.name("ID"), org.jooq.impl.SQLDataType.VARCHAR(34).nullable(false), this, "");

    /**
     * The column <code>STUFF.DATA</code>.
     */
    public final TableField<StuffRecord, String> DATA = createField(DSL.name("DATA"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "");

    /**
     * Create a <code>STUFF</code> table reference
     */
    public Stuff() {
        this(DSL.name("STUFF"), null);
    }

    /**
     * Create an aliased <code>STUFF</code> table reference
     */
    public Stuff(String alias) {
        this(DSL.name(alias), STUFF);
    }

    /**
     * Create an aliased <code>STUFF</code> table reference
     */
    public Stuff(Name alias) {
        this(alias, STUFF);
    }

    private Stuff(Name alias, Table<StuffRecord> aliased) {
        this(alias, aliased, null);
    }

    private Stuff(Name alias, Table<StuffRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Stuff(Table<O> child, ForeignKey<O, StuffRecord> key) {
        super(child, key, STUFF);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<StuffRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_4;
    }

    @Override
    public List<UniqueKey<StuffRecord>> getKeys() {
        return Arrays.<UniqueKey<StuffRecord>>asList(Keys.CONSTRAINT_4);
    }

    @Override
    public Stuff as(String alias) {
        return new Stuff(DSL.name(alias), this);
    }

    @Override
    public Stuff as(Name alias) {
        return new Stuff(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Stuff rename(String name) {
        return new Stuff(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Stuff rename(Name name) {
        return new Stuff(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
