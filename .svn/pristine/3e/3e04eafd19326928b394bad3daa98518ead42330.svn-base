package com.fico.cbs.common.database.storage;

import java.lang.reflect.Array;

public class Row {
    private final int DEFAULT_COLUMN_SIZE = 10;
    private String[] _data = new String[10];
    private int _size = 0;

    public Row() {
    }

    public int size() {
        return this._size;
    }

    public void add(String data) {
        if (this._size >= 10) {
            this._data = (String[])arrayGrow(this._data, 1);
        }

        this._data[this._size++] = data;
    }

    public void modify(int index, String nval) {
        this._data[index - 1] = nval;
    }

    public void delete(int index) {
        for(int i = index - 1; i < this._size - 1; ++i) {
            this._data[i] = this._data[i + 1];
        }

        this._data[--this._size] = null;
    }

    public String get(int index) {
        return index >= 1 && index <= this._size ? this._data[index - 1] : null;
    }

    public static Object arrayGrow(Object src, int addLen) {
        Class cl = src.getClass();
        if (!cl.isArray()) {
            return null;
        } else {
            Class componentType = cl.getComponentType();
            int length = Array.getLength(src);
            int newLength = length + addLen;
            Object newArray = Array.newInstance(componentType, newLength);
            System.arraycopy(src, 0, newArray, 0, addLen < 0 ? newLength : length);
            return newArray;
        }
    }
}
