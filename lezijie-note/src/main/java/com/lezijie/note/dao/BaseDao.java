package com.lezijie.note.dao;

import com.lezijie.note.util.DBUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class BaseDao {

    public static int executeUpdate(String sql, List<Object> params) {
        int row = 0; // 受影响的行数
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {

            connection = DBUtil.getConnetion();

            preparedStatement = connection.prepareStatement(sql);

            if (params != null && params.size() > 0) {

                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }

            row = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            DBUtil.close(null, preparedStatement, connection);
        }

        return row;
    }


    public static Object findSingleValue(String sql, List<Object> params) {
        Object object = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnetion();

            preparedStatement = connection.prepareStatement(sql);

            if (params != null && params.size() > 0) {

                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                object = resultSet.getObject(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            DBUtil.close(resultSet, preparedStatement, connection);
        }

        return object;
    }


    public static List queryRows(String sql, List<Object> params, Class cls) {
        List list = new ArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            connection = DBUtil.getConnetion();

            preparedStatement = connection.prepareStatement(sql);
            if (params != null && params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int fieldNum = resultSetMetaData.getColumnCount();

            while (resultSet.next()) {
                Object object = cls.newInstance();
                for (int i = 1; i <= fieldNum; i++) {
                    String columnName = resultSetMetaData.getColumnLabel(i); // 如果是tb_user,userId字段
                    Field field = cls.getDeclaredField(columnName);
                    String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method method = cls.getDeclaredMethod(setMethod, field.getType());
                    Object value = resultSet.getObject(columnName);
                    method.invoke(object, value);
                }
                // 将Javabean设置到集合中
                list.add(object);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /**
     * 查询对象
     *
     * @param sql
     * @param params
     * @param cls
     * @return
     */
    public static Object queryRow(String sql, List<Object> params, Class cls) {
        List list = queryRows(sql, params, cls);
        Object object = null;
        // 如果集合不为空，则获取查询的第一条数据
        if (list != null && list.size() > 0) {
            object = list.get(0);
        }

        return object;
    }

}
