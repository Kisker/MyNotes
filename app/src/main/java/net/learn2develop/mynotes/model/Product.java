package net.learn2develop.mynotes.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Product.TABLE_NAME_USERS)
public class Product {

    public static final String TABLE_NAME_USERS = "products";

    public static final String FIELD_NAME_ID  = "id";
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_DESCRIPTION = "description";

    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String name;

    @DatabaseField(columnName = FIELD_NAME_DESCRIPTION)
    private String description;

    public Product() {
    }

    /** Getters & Setters **/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}

