package com.fico.cbs.common.database.storage;

public class ListResult {
    private final int DEFAULT_COLUMN_SIZE = 10;
    private final int DEFAULT_ROW_SIZE = 50;
    private String[] _column = new String[10];
    private Row[] _data = new Row[50];
    private int _rowCount = 0;
    private int _colCount = 0;
    private int _cusor = -1;

    public ListResult() {
    }

    public int addRow() {
        return this.addRow("");
    }

    public int addRow(String... data) {
        if (this._rowCount >= 50) {
            this._data = (Row[])Row.arrayGrow(this._data, 1);
        }

        this._data[this._rowCount] = new Row();

        for(int i = 0; i < this._colCount; ++i) {
            this._data[this._rowCount].add(i < data.length ? data[i] : "");
        }

        ++this._rowCount;
        return this._rowCount;
    }

    public void addColumns(String... names) {
        for(int i = 0; i < names.length; ++i) {
            this.addColumn(names[i]);
        }

    }

    public void addColumn(String name) {
        this.addColumn(name, "");
    }

    public void addColumn(String name, String... data) {
        int columnIndex = this.containColumn(name);
        if (columnIndex <= 0) {
            if (this._colCount >= 10) {
                this._column = (String[])Row.arrayGrow(this._column, 1);
            }

            this._column[this._colCount] = name;
            ++this._colCount;

            for(int i = 0; i < this._rowCount; ++i) {
                this._data[i].add(i < data.length ? data[i] : "");
            }

        }
    }

    public int deleteRow(int index) {
        if (index > this._rowCount) {
            return 0;
        } else {
            for(int i = index - 1; i < this._rowCount - 1; ++i) {
                this._data[i] = this._data[i + 1];
            }

            this._data[this._rowCount--] = null;
            return index;
        }
    }

    public int deleteColumn(int index) {
        if (index > this._colCount) {
            return 0;
        } else {
            int i;
            for(i = index - 1; i < this._colCount - 1; ++i) {
                this._column[i] = this._column[i + 1];
            }

            this._column[this._colCount--] = null;

            for(i = 0; i < this._rowCount; ++i) {
                this._data[i].delete(index);
            }

            return index;
        }
    }

    public int deleteColumn(String name) {
        return this.deleteColumn(this.containColumn(name));
    }

    public boolean next() {
        if (this._rowCount > this._cusor + 1) {
            ++this._cusor;
            return true;
        } else {
            return false;
        }
    }

    public String getField(String column) {
        return this.getField(this.containColumn(column));
    }

    public String getField(int index) {
        return index >= 1 && index <= this._colCount ? this._data[this._cusor].get(index) : null;
    }

    public String getField(String column, int row) {
        return row >= 1 && row <= this._rowCount ? this._data[row - 1].get(this.containColumn(column)) : null;
    }

    public String getField(int col, int row) {
        return row >= 1 && row <= this._rowCount ? this._data[row - 1].get(col) : null;
    }

    public SingleResult getRow(int index) {
        if (index >= 1 && index <= this._rowCount) {
            SingleResult sr = new SingleResult();

            for(int i = 0; i < this._colCount; ++i) {
                sr.put(this._column[i], this._data[index - 1].get(i + 1));
            }

            return sr;
        } else {
            return null;
        }
    }

    public void set(String column, int row, String val) {
        this.set(this.containColumn(column), row, val);
    }

    public void set(int col, int row, String val) {
        if (col >= 1 && col <= this._colCount) {
            this._data[row - 1].modify(col, val);
        }
    }

    public String[] getColumnNames() {
        return this._column;
    }

    public String getColumnNameAtIndex(int index) {
        return this._column[index - 1];
    }

    public int containColumn(String name) {
        for(int i = 0; i < this._colCount; ++i) {
            if (name.equalsIgnoreCase(this._column[i])) {
                return i + 1;
            }
        }

        return 0;
    }

    public int rowCount() {
        return this._rowCount;
    }

    public int columnCount() {
        return this._colCount;
    }

    public void print() {
        StringBuffer cols = new StringBuffer();

        for(int i = 0; i < this._colCount; ++i) {
            cols = cols.append(this._column[i]).append(" | ");
        }

        cols = cols.delete(cols.length() - 2, cols.length());
        StringBuffer rows = new StringBuffer();

        for(int i = 0; i < this._rowCount; ++i) {
            Row row = this._data[i];

            for(int j = 0; j < this._colCount; ++j) {
                rows = rows.append(row.get(j + 1)).append(" | ");
            }

            rows = rows.delete(rows.length() - 2, rows.length()).append("\r\n");
        }

    }
}
