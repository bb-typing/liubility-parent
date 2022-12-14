package org.liubility.commons.http.response.table;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.liubility.commons.dto.annotation.TableHeader;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jdragon
 * @email: 1061917196@qq.com
 * @Date: 2020.06.23 22:41
 * @Description:
 */
public class TableFactory {

    public static <T> PageTable<T> buildPageTable(TableRef<T> table) {
        return buildPageTable(table.getList().size(), 1L, 1L, 1L, table);
    }

    public static <T> PageTable<T> buildPageTable(IPage<T> iPage, TableRef<T> table) {
        return buildPageTable(iPage.getTotal(), iPage.getPages(), iPage.getSize(), iPage.getCurrent(), table);
    }

    public static <T> PageTable<T> buildPageTable(long total, long pages, long size, long current, TableRef<T> table) {
        PageTable<T> pageTable = new PageTable<>();
        pageTable.setTotal(total);
        pageTable.setPages(pages);
        pageTable.setSize(size);
        pageTable.setCurrent(current);
        pageTable.setTable(buildTable(table));
        return pageTable;
    }

    public static <T> Table<T> buildTable(TableRef<T> tableRef) {
        Table<T> table = new Table<>();
        List<T> list = tableRef.getList();
        if (list == null) {
            list = new ArrayList<>();
        }
        Type type = tableRef.getType();
        Field[] fields = ((Class<?>) type).getDeclaredFields();
        for (Field field : fields) {
            TableHeader tableHeader = field.getAnnotation(TableHeader.class);
            if (null != tableHeader) {
                Table.Header header = new Table.Header();
                header.setKey(field.getName());
                header.setLabel(tableHeader.value());
                header.setComponent(tableHeader.component());
                header.setWidth(tableHeader.width());
                header.setMerge(tableHeader.merge());
                table.addHeader(header);
            }
        }
        table.setBodies(list);
        return table;
    }
}
