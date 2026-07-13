package br.com.project.springboot.starter.template.api.configs;

import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.spi.SchemaFilter;

import java.util.Objects;

public class TableSchemaFilterConfig implements SchemaFilter {
    public static final TableSchemaFilterConfig INSTANCE = new TableSchemaFilterConfig();

    @Override
    public boolean includeNamespace(Namespace namespace) {
        return true;
    }

    @Override
    public boolean includeTable(Table table) {
        return Objects.isNull(table.getSchema()) || table.getSchema().equals("public");
    }

    @Override
    public boolean includeSequence(Sequence sequence) {
        return true;
    }
}
